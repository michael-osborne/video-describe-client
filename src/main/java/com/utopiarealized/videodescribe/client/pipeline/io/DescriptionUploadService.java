package com.utopiarealized.videodescribe.client.pipeline.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;

import org.springframework.stereotype.Service;
import com.utopiarealized.videodescribe.model.dto.FrameDTO;
import com.utopiarealized.videodescribe.model.dto.VideoDescriptionDTO;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameDescription;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoDescriptionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DescriptionUploadService {

    @Autowired
    private VideoIOService videoIOService;

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private static final Logger logger = LoggerFactory.getLogger(DescriptionUploadService.class);

    @Consume(threads = 1)
    public void uploadFrameDescription(FrameDescription frameDescription) {
        try {
            logger.info("[" + Thread.currentThread().getName() + "] Uploading description for video "
                    + frameDescription.getVideoId() + " " + frameDescription.getSequence() + "/"
                    + frameDescription.getDescription());
            videoIOService
                    .postFrameDescription(new FrameDTO(frameDescription.getTimestamp(), frameDescription.getSequence(),
                            frameDescription.getDescription(),
                            frameDescription.getVideoId()));
        } catch (IOException e) {
            pipelineOrchestrator.resumbmitData(frameDescription);
        }
    }

    @Consume(threads = 1)
    public void uploadVideoDescription(VideoDescriptionResult videoDescription) {
        try {
            logger.info("    Uploading description for video "
                    + videoDescription.getVideoId());
            videoIOService
                    .postFullDescription(new VideoDescriptionDTO(videoDescription.getVideoId(),
                            videoDescription.getDescription(), videoDescription.getDescriber()));
        } catch (IOException e) {
            pipelineOrchestrator.resumbmitData(videoDescription);
        }
    }
}
