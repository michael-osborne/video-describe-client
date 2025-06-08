package com.utopiarealized.videodescribe.client.pipeline.io;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoAndAudioMetadata;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.model.dto.MetadataDTO;
import java.io.IOException;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MetadataUploadService {

    @Autowired
    private VideoIOService videoIOService;

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private static final Logger logger = LoggerFactory.getLogger(MetadataUploadService.class);

    @Consume(threads = 1)
    public void uploadMetadata(VideoAndAudioMetadata videoAndMetadata) {
        logger.info(" Uploading metadata for video " + videoAndMetadata.getDownloadResult().getVideoId());
        MetadataDTO metadataDTO = new MetadataDTO(
                videoAndMetadata.getDownloadResult().getVideoId(),
                (int) videoAndMetadata.getVideoMetadata().getTotalFrames(),
                videoAndMetadata.getVideoMetadata().getWidth(),
                videoAndMetadata.getVideoMetadata().getHeight(),
                videoAndMetadata.getVideoMetadata().getDuration(),
                videoAndMetadata.getVideoMetadata().getFps(),
                (int) videoAndMetadata.getDownloadResult().getSize(),
                videoAndMetadata.getAudioMetadata().getAudioCodec(),
                videoAndMetadata.getAudioMetadata().getSampleRate(),
                videoAndMetadata.getAudioMetadata().getChannels(),
                videoAndMetadata.getAudioMetadata().getAudioBitRate(),
                (int) videoAndMetadata.getAudioMetadata().getAudioDuration());
        try {
            videoIOService.sendMetadata(metadataDTO);
        } catch (IOException e) {
            pipelineOrchestrator.submitData(
                    new ProcessLog(videoAndMetadata.getDownloadResult().getVideoId(), "Failed to upload metadata"));
            pipelineOrchestrator.resumbmitData(videoAndMetadata);
        }
    }

}
