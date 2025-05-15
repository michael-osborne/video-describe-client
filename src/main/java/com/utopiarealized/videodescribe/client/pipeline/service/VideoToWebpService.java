package com.utopiarealized.videodescribe.client.pipeline.service;

import java.io.IOException;
import java.io.File;

import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.model.ProcessModel;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoAndMetadata;    
import com.utopiarealized.videodescribe.client.pipeline.model.WebpResult;   
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.client.service.ProcessCreationService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VideoToWebpService {

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    @Autowired
    private ProcessCreationService processCreationService;

    private static final Logger logger = LoggerFactory.getLogger(VideoToWebpService.class);
  
    
    /**
     * Converts a video to an animated WebP thumbnail with half the framerate
     * @param inputPath Path to the input video file
     * @param outputPath Path for the output WebP file
     * @param thumbnailWidth Desired width of the thumbnail (height auto-calculated)
     * @throws IOException If the process fails or FFmpeg isn't found
     * @throws InterruptedException If the process is interrupted
     */
    @Consume(threads=2)
    public void convertToWebpThumbnail(VideoAndMetadata videoAndMetadata)
            throws IOException, InterruptedException {
        pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(), "Converting video to WebP thumbnail"));
        WidthAndHeight widthAndHeight = getScale(videoAndMetadata);
        String[] args = {
                "ffmpeg", // Command
                "-i", videoAndMetadata.getDownloadResult().getFilePath(), // Input file
                "-vf", getSelect(videoAndMetadata) + ",scale=" + widthAndHeight.width + ":" + widthAndHeight.height,
                "-c:v", "libwebp", // Video codec: WebP
                "-lossless", "0", // Lossy compression
                "-q:v", "40", // Quality
                "-loop", "0", // Infinite loop
                "-an", // Disable audio
                "-y", // Overwrite output
                "-preset", "picture", // or "picture" for smaller size
                videoAndMetadata.getDownloadResult().getFilePath() + ".webp"
        };
        ProcessModel processModel = new ProcessModel(args, null);
        processCreationService.createProcess(processModel);

        int exitCode = processModel.getExitCode();
        if (exitCode != 0) {
            String error = "unknown error";
            if (processModel.getStdErrCapture().size() > 0) {
                error = processModel.getStdErrCapture().get(processModel.getStdErrCapture().size() - 1);
            }
            pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(),
                    "FFmpeg process failed with exit code " + exitCode +". Last error: " + error));
            throw new IOException("FFmpeg process failed with exit code " + exitCode);
        }
        pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(),
                "Video to Webp conversion completed"));
            

        logger.info("Conversion completed: " + videoAndMetadata.getDownloadResult().getFilePath() + ".webp");
        WebpResult webpResult = new WebpResult(videoAndMetadata.getDownloadResult().getFilePath() + ".webp",
                videoAndMetadata.getDownloadResult().getVideoId(), widthAndHeight.width, widthAndHeight.height, new File(videoAndMetadata.getDownloadResult().getFilePath() + ".webp").length());
        pipelineOrchestrator.submitData(webpResult);
    }

    private static String getSelect(VideoAndMetadata videoAndMetadata) {
        int mod = ((int) videoAndMetadata.getFps()) / 4;
        float pts = ((float)1 /  (float)mod);
        logger.info("mod: " + mod + " pts: " + pts);
        return "select='mod(n," + 5 + ")',setpts=" + .2 + "*PTS";
    }
    
    private static WidthAndHeight getScale(VideoAndMetadata videoAndMetadata) {
        if (videoAndMetadata.getHeight() > videoAndMetadata.getWidth()) {
            double divisor = (double) 320 / (double) videoAndMetadata.getHeight();
            int width = (int) Math.round( (double) videoAndMetadata.getWidth() * divisor);
            return new WidthAndHeight(width, 320);
        } else {
            double divisor = (double) 320 / (double) videoAndMetadata.getWidth();
            int height = (int) Math.round( (double) videoAndMetadata.getHeight() * divisor);
            return new WidthAndHeight(320, height);
        }
    }

    private static class WidthAndHeight {
        private int width;
        private int height;

        public WidthAndHeight(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}