package com.utopiarealized.videodescribe.client.pipeline.service;

import java.io.IOException;

import com.utopiarealized.videodescribe.client.service.io.VideoIOService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameData;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameDescription;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionResponseDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionRequestDTO;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
@Service
@Profile("prod")
public class FrameToDescriptionService  {

    @Value("${frame.description.url}")
    private String descriptionUrl;

    
    @Autowired  
    private PipelineOrchestrator pipelineOrchestrator;

    @Autowired
    private VideoIOService videoIOService;


   // A more robust implementation would use a strategy to determine the number of GPU resources available and create a thread for each
    @Consume(threads=1)
    public void processFrame(FrameData frame) {
        try {
            FrameDescriptionResponseDTO frameDescriptionResponseDTO = videoIOService.getFrameDescription(new FrameDescriptionRequestDTO(
                    descriptionUrl, frame.getBytes(), "ViT-L-14/openai", "fast"));
                
            pipelineOrchestrator.submitData(new FrameDescription(frame.getVideoId(), 
                    frameDescriptionResponseDTO.getDescription(), frame.getTimestamp(), frame.getSequence(),
                    frame.isLastFrame()));
            pipelineOrchestrator.submitData(new ProcessLog(frame.getVideoId(), "Received frame description for frame " + frame.getSequence() + ""));
            if (frame.isLastFrame()) {
                pipelineOrchestrator.submitData(new VideoStatus(frame.getVideoId(), VideoStatusDTO.STATUS_COMPLETED,
                        VideoStatusDTO.SUBSTATUS_COMPLETE));
            }
        } catch (IOException e) {
            pipelineOrchestrator.submitData(new ProcessLog(frame.getVideoId(), "Error getting frame description for frame " + frame.getSequence() + ": " + e.getMessage()));
        }
    }
}