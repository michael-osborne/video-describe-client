package com.utopiarealized.videodescribe.client.pipeline.service;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

import com.utopiarealized.videodescribe.client.pipeline.model.FrameData;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoAndMetadata;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import org.springframework.stereotype.Service;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VideoToFramesService {

    private static final int MAX_FRAMES = 250;
    private static final int SECONDS_TO_SKIP = 6;

    private static final Logger logger = LoggerFactory.getLogger(VideoToFramesService.class);


    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    @Consume(threads = 2)
    public void extractFrames(VideoAndMetadata videoAndMetadata) throws IOException {
        FrameCaptureResult frameCaptureResult = calculateFrameCaptureResult(videoAndMetadata);
        pipelineOrchestrator.submitData(new VideoStatus(videoAndMetadata.getDownloadResult().getVideoId(),
                frameCaptureResult.numFrames, VideoStatusDTO.STATUS_PROCESSING, VideoStatusDTO.SUBSTATUS_FRAMES, null));
        final String filePath = videoAndMetadata.getDownloadResult().getFilePath();
        Java2DFrameConverter converter = null;
        pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(), "Extracting frames from video"));
        boolean lastFrameSent = false;

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
            grabber.start();
            converter = new Java2DFrameConverter();
            int firstFrame = frameCaptureResult.startFrame;
            int frameSkip = frameCaptureResult.frameSkip;
            int totalFramesToGrab = frameCaptureResult.numFrames;
            if (totalFramesToGrab == 0) {
                logger.error("No frames to grab");
                totalFramesToGrab = 1;
            }

            int seq = 0;
            for (int frameNum = firstFrame; frameNum < videoAndMetadata.getTotalFrames(); frameNum += frameSkip) {
                seq++;
                grabber.setVideoFrameNumber(frameNum);
                Frame frame = grabber.grabImage();
                if (frame != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(converter.convert(frame), "jpg", baos);
                    File file = new File(filePath + "_" + seq + "_" + frameNum + ".jpg");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(baos.toByteArray());
                    fos.close();
                    double timestamp = grabber.getTimestamp() / 1_000_000.0;
                    lastFrameSent = seq >= totalFramesToGrab;
                    FrameData frameData = new FrameData(videoAndMetadata.getDownloadResult().getVideoId(),
                            filePath + "_" + seq + "_" + frameNum
                                    + ".jpg",
                            baos.toByteArray(),
                            timestamp, seq, lastFrameSent, totalFramesToGrab);
                    pipelineOrchestrator.submitData(frameData);
                    if (totalFramesToGrab >= 5) {
                        if (seq % (totalFramesToGrab / 5) == 0) {
                            pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(),
                                    "Extracted " + seq + "/" + totalFramesToGrab + " frames"));
                        }
                    }
                }

            }
            grabber.stop();
            if (!lastFrameSent) {
                pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(),
                        "Last frame not sent!"));
            }
        } catch (Exception e) {
            logger.error("Error extracting frames: " + e.getMessage());
            pipelineOrchestrator.submitData(new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(),
                    "Error extracting frames: " + e.getMessage()));
            pipelineOrchestrator.submitData(new VideoStatus(videoAndMetadata.getDownloadResult().getVideoId(),
                    0, VideoStatusDTO.STATUS_FAILED, VideoStatusDTO.SUBSTATUS_FRAMES, null));
        } finally {
            if (converter != null) {
                converter.close();
            }
        }

    }

    class FrameCaptureResult {
        private int numFrames;
        private int startFrame;
        private int frameSkip;

        public FrameCaptureResult(int numFrames, int startFrame, int frameSkip) {
            this.numFrames = numFrames;
            this.startFrame = startFrame;
            this.frameSkip = frameSkip;
        }

    }

    private FrameCaptureResult calculateFrameCaptureResult(VideoAndMetadata videoAndMetadata) {
        long totalFrames = videoAndMetadata.getTotalFrames();
        int fps = (int) videoAndMetadata.getFps();
        int maxFrames = MAX_FRAMES;
        int secondsToSkip = SECONDS_TO_SKIP;
        int firstFrame = (int) (2 * fps);

        int numFrames = (int) (totalFrames / (fps * secondsToSkip));

        if (firstFrame > totalFrames) {
            firstFrame = (int) (totalFrames / 2);
            numFrames = 1;
        }

        int frameSkip = (secondsToSkip * fps);

        if (secondsToSkip * fps * maxFrames < totalFrames) {
            frameSkip = (int) ((totalFrames - firstFrame) / maxFrames);
            numFrames = maxFrames;
        }

        return new FrameCaptureResult(numFrames, firstFrame, frameSkip);
    }

}