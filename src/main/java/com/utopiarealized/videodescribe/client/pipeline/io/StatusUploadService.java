package com.utopiarealized.videodescribe.client.pipeline.io;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import java.io.IOException;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import com.utopiarealized.videodescribe.client.service.io.StatusIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class StatusUploadService {

    @Autowired
    private StatusIOService statusIOService;

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private static final Logger logger = LoggerFactory.getLogger(StatusUploadService.class);

    @Consume(threads = 1)
    public void uploadStatus(VideoStatus videoStatus) {
        try {
            logger.info("[" + Thread.currentThread().getName() + "] Uploading status for video " + videoStatus.getId());
            VideoStatusDTO videoStatusDTO = new VideoStatusDTO(videoStatus.getId(), videoStatus.getStatus(),
                    videoStatus.getSubstatus());
            statusIOService.updateVideoStatus(videoStatusDTO);
        } catch (IOException e) {
            pipelineOrchestrator.submitData(new ProcessLog(videoStatus.getId(), "Failed to upload status"));
            pipelineOrchestrator.resumbmitData(videoStatus);
        }
    }
    
    @Consume(threads = 1)
    public void uploadProcessLog(ProcessLog processLog) {
        try {
            statusIOService.sendLog(processLog.getVideoId(), processLog.getMessage());
        } catch (IOException e) {
            logger.error("[" + Thread.currentThread().getName() + "] Error uploading process log: " + processLog.getMessage());
        }
    }
}
