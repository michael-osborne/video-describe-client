package com.utopiarealized.videodescribe.client.pipeline.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoDescriptionResult;
import java.util.Random;
import com.utopiarealized.videodescribe.client.pipeline.model.FramesDescriptionAndTranscript;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
@Service
@Profile("dev")
public class VideoDescriptionServiceFacade {
    
    private static final String thinking = "<think> thinking htingking thing </think>\n";

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

    @Consume
    public void consumeFullDescription(FramesDescriptionAndTranscript fullDescription) {
        pipelineOrchestrator.submitData(new VideoDescriptionResult(fullDescription.getVideoId(),
                getDescription(), "describer facade"));
        pipelineOrchestrator.submitData(new ProcessLog(fullDescription.getVideoId(), "Video description completed"));
        pipelineOrchestrator.submitData(new VideoStatus(fullDescription.getVideoId(), VideoStatusDTO.STATUS_COMPLETED, VideoStatusDTO.SUBSTATUS_COMPLETE));
    }

    private String getDescription() {
        Random r = new Random();

        StringBuilder sb = new StringBuilder(thinking);

        int numWords = r.nextInt(30) + 20;
        for (int i = 0; i < numWords; i++) {
            sb.append(randomWords[r.nextInt(randomWords.length)]).append(" ");
            if (i % 10 == 0) {
                sb.append("\n\n");
            }
        }
        return sb.toString();

    }
}
