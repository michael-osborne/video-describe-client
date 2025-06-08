package com.utopiarealized.videodescribe.client.pipeline.io;

import com.utopiarealized.videodescribe.client.service.io.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.model.dto.WavMetadataDTO;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.client.pipeline.model.WavFileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class WavUploadService {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private static final Logger logger = LoggerFactory.getLogger(FrameUploadService.class);

    @Consume(threads = 1)
    public void uploadWav(WavFileResult wavFileResult) {
        logger.info("[" + Thread.currentThread().getName() + "] Uploading wav for video " + wavFileResult.getVideoId());
        WavMetadataDTO wavMetadataDTO = new WavMetadataDTO(wavFileResult.getVideoId(),
                wavFileResult.getAudioMetadata().getAudioCodec(),
                wavFileResult.getAudioMetadata().getSampleRate(), wavFileResult.getAudioMetadata().getChannels(), wavFileResult.getAudioMetadata().getAudioBitRate(), wavFileResult.getAudioMetadata().getAudioDuration());
        try {
            fileUploadService.uploadWav(wavFileResult.getLocation(), wavMetadataDTO);
            pipelineOrchestrator.submitData(new ProcessLog(wavFileResult.getVideoId(),
                    "Uploaded wav file of duration " + wavMetadataDTO.getDuration()));
        } catch (IOException e) {
            pipelineOrchestrator.submitData(new ProcessLog(wavFileResult.getVideoId(),
                    "Failed to upload wav file"));
            pipelineOrchestrator.resumbmitData(wavFileResult);
        }
    }
}