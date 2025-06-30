package com.utopiarealized.videodescribe.client.pipeline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.WavFileResult;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.model.dto.AudioTranscriptResponseDTO;
import com.utopiarealized.videodescribe.model.dto.AudioTranscriptRequestDTO;
import com.utopiarealized.videodescribe.client.pipeline.model.TranscriptionResult;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;

import java.io.IOException;

@Service
@Profile("prod")
public class WavToTranscriptionService {
    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private static final Logger logger = LoggerFactory.getLogger(GetVideoMetadataService.class);

    @Autowired
    private VideoIOService videoIOService;

    @Consume(threads = 1)
    public void wavToTranscription(WavFileResult wavFileResult) {
        pipelineOrchestrator.submitData(new ProcessLog(wavFileResult.getVideoId(), "Getting audio transcription"));

        try {
            AudioTranscriptResponseDTO audioTranscriptResponseDTO = videoIOService.getAudioTranscript(
                    new AudioTranscriptRequestDTO(wavFileResult.getLocation(), wavFileResult.getVideoId(), wavFileResult.getWavFile()));
            pipelineOrchestrator.submitData(new TranscriptionResult(wavFileResult.getVideoId(), audioTranscriptResponseDTO.getTranscript(), "basic transcriber"));
        } catch (IOException e) {
            pipelineOrchestrator.resumbmitData(new ProcessLog(wavFileResult.getVideoId(), "Error getting audio transcription: " + e.getMessage()));
        }
    }
}
