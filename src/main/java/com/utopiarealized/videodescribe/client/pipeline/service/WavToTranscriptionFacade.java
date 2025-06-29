    package com.utopiarealized.videodescribe.client.pipeline.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.client.pipeline.model.WavFileResult;
import com.utopiarealized.videodescribe.model.dto.AudioTranscriptRequestDTO;
import com.utopiarealized.videodescribe.model.dto.AudioTranscriptResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;
import com.utopiarealized.videodescribe.client.pipeline.model.TranscriptionResult;
import java.io.IOException;
import java.util.Random;

@Service
@Profile("dev")
public class WavToTranscriptionFacade {

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;


    private String[] randomWords = {
            "apple", "bridge", "cloud", "dolphin", "eagle",
            "forest", "guitar", "horizon", "island", "jacket",
            "kettle", "lantern", "mountain", "nest", "ocean",
            "puzzle", "quartz", "river", "shadow", "tiger",
            "umbrella", "violet", "whisper", "xylophone", "yellow",
            "zebra", "breeze", "cactus", "dawn", "ember",
            "flame", "glacier", "hollow", "ivory", "jungle",
            "knight", "lighthouse", "meadow", "night", "orbit",
            "prism", "quest", "ridge", "spark", "thunder",
            "twilight", "valley", "wave", "xenon", "yield" };

    @Consume(threads = 1)
    public void wavToTranscription(WavFileResult wavFileResult) throws IOException {
       
        pipelineOrchestrator.submitData(
                new TranscriptionResult(wavFileResult.getVideoId(), getDescription(),"facade transcriber"));
    }

    private String getDescription() {
        Random r = new Random();

        StringBuilder sb = new StringBuilder();

        int numWords = r.nextInt(20) + 10;
        for (int i = 0; i < numWords; i++) {
            sb.append(randomWords[r.nextInt(randomWords.length)]).append(" ");
        }
        return sb.toString();

    }
}
