package com.utopiarealized.videodescribe.client.pipeline.io;

import com.utopiarealized.videodescribe.client.service.io.FileUploadService;
import com.utopiarealized.videodescribe.client.pipeline.model.WebpResult;  

import com.utopiarealized.videodescribe.model.dto.WebpDTO;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WebpUploadService {
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;
    private static final Logger logger = LoggerFactory.getLogger(WebpUploadService.class);


    @Consume(threads = 1)
    public void uploadWebp(WebpResult result) {
        logger.info("[" + Thread.currentThread().getName() + "] Uploading webp for video " + result.getVideoId());
        WebpDTO metadata = new WebpDTO(result.getVideoId(), result.getWidth(), result.getHeight(),
                result.getSizeInBytes());
        try {
            fileUploadService.uploadVideoThumbnail(result.getLocation(), metadata);
        } catch (IOException e) {
            pipelineOrchestrator.submitData(new ProcessLog(result.getVideoId(), "Failed to upload webp"));
            pipelineOrchestrator.resumbmitData(result);
        }
    }

}
