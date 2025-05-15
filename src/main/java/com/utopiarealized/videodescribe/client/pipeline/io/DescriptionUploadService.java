package com.utopiarealized.videodescribe.client.pipeline.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;

import org.springframework.stereotype.Service;
import com.utopiarealized.videodescribe.model.dto.FrameDTO;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameDescription;
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
    public void uploadDescription(FrameDescription frameDescription) {
        try {
            logger.info("[" + Thread.currentThread().getName() + "] Uploading description for video " + frameDescription.getVideoId() + " " + frameDescription.getSequence() + "/" + frameDescription.getDescription());
            videoIOService.postFrame(new FrameDTO(frameDescription.getTimestamp(), frameDescription.getSequence(), frameDescription.getDescription(),
                    frameDescription.getVideoId()));
        } catch (IOException e) {
            pipelineOrchestrator.resumbmitData(frameDescription);
        }
    }
}
