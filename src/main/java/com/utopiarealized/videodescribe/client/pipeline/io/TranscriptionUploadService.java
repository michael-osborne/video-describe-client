package com.utopiarealized.videodescribe.client.pipeline.io;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;
import com.utopiarealized.videodescribe.client.pipeline.model.TranscriptionResult;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import java.io.IOException;
@Service
public class TranscriptionUploadService {

    @Autowired
    private VideoIOService videoIOService;

    @Consume(threads = 1)
    public void uploadTranscription(TranscriptionResult transcriptionResult) throws IOException {
        videoIOService.uploadTranscription(transcriptionResult);
    }

}
