package com.utopiarealized.videodescribe.client.pipeline.service;

import java.io.IOException;
import java.io.File;

import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.model.ProcessModel;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoAndAudioMetadata;    
import com.utopiarealized.videodescribe.client.pipeline.model.WebpResult;   
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.client.service.ProcessCreationService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.pipeline.model.WavFileResult;
import org.apache.commons.io.FileUtils;
import com.utopiarealized.videodescribe.client.pipeline.model.child.AudioMetadata;
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
    public void convertToWebpThumbnail(VideoAndAudioMetadata videoAndMetadata)
            throws IOException, InterruptedException {
        pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(), "Converting video to WebP thumbnail"));
        WidthAndHeight widthAndHeight = getScale(videoAndMetadata);
        String[] args = {
                "ffmpeg", // Command
                "-i", videoAndMetadata.getDownloadResult().getFilePath(), // Input file
                // Video output: WebP
                "-vf", getSelect(videoAndMetadata) + ",scale=" + widthAndHeight.width + ":" + widthAndHeight.height,
                "-c:v", "libwebp", // Video codec: WebP
                "-lossless", "0", // Lossy compression
                "-q:v", "40", // Quality
                "-loop", "0", // Infinite loop
                "-an", // Disable audio for WebP output
                "-y", // Overwrite output
                "-preset", "picture", // Preset for smaller size
                "-map", "v:0", // Map video stream to first output
                videoAndMetadata.getDownloadResult().getFilePath() + ".webp", // WebP output
                // Audio output: WAV
                "-vn", // Disable video for WAV output
                "-acodec", "pcm_s16le", // WAV PCM audio
                "-ar", "16000", // Sample rate 16kHz (for NeMo compatibility)
                "-ac", "1", // Mono audio
                "-map", "a:0?", // Map audio stream to second output
                videoAndMetadata.getDownloadResult().getFilePath() + ".wav" // WAV output
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
             

        logger.info("Conversion & wav file creation completed: " + videoAndMetadata.getDownloadResult().getFilePath() );
        WebpResult webpResult = new WebpResult(videoAndMetadata.getDownloadResult().getFilePath() + ".webp",
                videoAndMetadata.getDownloadResult().getVideoId(), widthAndHeight.width, widthAndHeight.height, new File(videoAndMetadata.getDownloadResult().getFilePath() + ".webp").length());
        pipelineOrchestrator.submitData(webpResult);
        WavFileResult wavFileResult = new WavFileResult(videoAndMetadata.getDownloadResult().getFilePath() + ".wav",
                        videoAndMetadata.getDownloadResult().getVideoId(), new File(videoAndMetadata.getDownloadResult().getFilePath() + ".wav").length(),
                        FileUtils.readFileToByteArray(new File(videoAndMetadata.getDownloadResult().getFilePath() + ".wav")),
                        new AudioMetadata("pcm_s16le", 
                        16000, 1, videoAndMetadata.getAudioMetadata().getAudioBitRate(),
                                videoAndMetadata.getAudioMetadata().getAudioDuration()));
        pipelineOrchestrator.submitData(wavFileResult);
    }

    private static String getSelect(VideoAndAudioMetadata videoAndMetadata) {
        int mod = ((int) videoAndMetadata.getVideoMetadata().getFps()) / 4;
        float pts = ((float)1 /  (float)mod);
        logger.info("mod: " + mod + " pts: " + pts);
        return "select='mod(n," + 5 + ")',setpts=" + .2 + "*PTS";
    }
    
    private static WidthAndHeight getScale(VideoAndAudioMetadata videoAndMetadata) {
        if (videoAndMetadata.getVideoMetadata().getHeight() > videoAndMetadata.getVideoMetadata().getWidth()) {
            double divisor = (double) 320 / (double) videoAndMetadata.getVideoMetadata().getHeight();
            int width = (int) Math.round( (double) videoAndMetadata.getVideoMetadata().getWidth() * divisor);
            return new WidthAndHeight(width, 320);
        } else {
            double divisor = (double) 320 / (double) videoAndMetadata.getVideoMetadata().getHeight();
            int height = (int) Math.round( (double) videoAndMetadata.getVideoMetadata().getWidth() * divisor);
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