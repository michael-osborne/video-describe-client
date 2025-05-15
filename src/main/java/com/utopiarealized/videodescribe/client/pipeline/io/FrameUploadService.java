package com.utopiarealized.videodescribe.client.pipeline.io;

import com.utopiarealized.videodescribe.client.service.io.FileUploadService;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;
import com.utopiarealized.videodescribe.model.dto.FrameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameData;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
@Service
public class FrameUploadService {

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private static final Logger logger = LoggerFactory.getLogger(FrameUploadService.class);

    @Consume(threads=1)
    public void uploadFrame(FrameData frame) {
        logger.info("[" + Thread.currentThread().getName() + "] Uploading frame for video " + frame.getVideoId() + " " + frame.getSequence() + "/" + frame.getNumFrames());
        FrameDTO frameDTO = new FrameDTO(frame.getTimestamp(), frame.getSequence(), null, frame.getVideoId());
        try {
            fileUploadService.uploadFrame(frame.getLocation(), frameDTO);
            pipelineOrchestrator.submitData(new ProcessLog(frame.getVideoId(), "Uploaded frame " + frame.getSequence() + "/" + frame.getNumFrames()  ));
        } catch (IOException e) {
            pipelineOrchestrator.submitData(new ProcessLog(frame.getVideoId(), "Failed to upload frame " + frame.getSequence() + "/" + frame.getNumFrames()));
            pipelineOrchestrator.resumbmitData(frame);
        }
    }
}
