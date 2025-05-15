package com.utopiarealized.videodescribe.client.pipeline.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameData;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameDescription;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class FrameToDescriptionFacade  {
    
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
    "twilight", "valley", "wave", "xenon", "yield"};


    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;


    @Consume(threads=1)
    public void processFrame(FrameData frame) {
        pipelineOrchestrator.submitData(new FrameDescription(frame.getVideoId(), getDescription(), frame.getTimestamp(), frame.getSequence(),
                frame.isLastFrame()));
        if (frame.isLastFrame()) {
            pipelineOrchestrator.submitData(new VideoStatus(frame.getVideoId(), VideoStatusDTO.STATUS_COMPLETED, VideoStatusDTO.SUBSTATUS_COMPLETE));
        }
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