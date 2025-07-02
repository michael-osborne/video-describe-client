package com.utopiarealized.videodescribe.client.pipeline.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.utopiarealized.videodescribe.client.pipeline.model.FramesDescriptionAndTranscript;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import org.springframework.beans.factory.annotation.Value;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoDescriptionResult;
import org.springframework.context.annotation.Profile;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import com.utopiarealized.videodescribe.client.service.io.OllamaClient;

import java.io.IOException;
@Service
public class VideoSummaryService {
    @Autowired
    private OllamaClient ollamaClient;
    

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    @Value("${ollama.model}")
    private String ollamaModel;

/*
    private String prompt = "You are an editor writing brief video summaries for a website." + 
    "Summarize a video in 2-3 paragraphs based on the transcript and the descriptions of the ordered frames. Be concise. " +
    "Focus on actions,relational, and emotional content instead of technical details such as style or technology. " +
    "If something is not clear, do your best to correct it. The transcript may be blank or garbage. If so, ignore it " +
    "THIS IS A ONE-SHOT PROMPT. DO NOT ASK QUESTIONS. DO NOT DISCUSS THE INPUT." +
    "The frames are numbered from 1 to N, where N is the total number of frames in the video. " +
    "The transcript is the audio dialog of the video\n";

    private String framesStartPrompt = "Frames :";

    private String framesEndPrompt = "\n\nTranscript :";

    private String endPrompt = "\n\nTo be clear, the ask is: You are an editor writing brief video summaries for a website." + 
    "Summarize a video in 2-3 paragraphs based on the transcript and the descriptions of the ordered frames. Be concise. " +
    "Focus on actions,relational, and emotional content instead of technical details such as style or technology. " +
    "If something is not clear, do your best to correct it. The transcript may be blank or garbage. If so, ignore it " +
    "THIS IS A ONE-SHOT PROMPT. DO NOT ASK QUESTIONS. DO NOT DISCUSS THE INPUT.";
*/

private String prompt = "You are an editor writing brief video summaries for a website." + 
"Summarize a video in 2-3 paragraphs based on the transcript and the descriptions of the ordered frames.The frames are 6 seconds apart unless there are 250 or more of them. " +    
"The words in the frame go from more important to less important. Be concise in your response, and make the response approachable to someone with an associates degree " +
"Focus on actions being taken firstly, emotional context second (and only if there is emotive content in the frames) instead of technical details such as style or technology. " +
"If the frames aren't changing much, it may be a podcast. If so, focus on the transcription if it looks valid.If something is not clear, do your best to correct it. The transcript may be blank or garbage. If so, ignore it " +
"THIS IS A ONE-SHOT PROMPT. DO NOT ASK QUESTIONS. DO NOT DISCUSS THE INPUT." +
"The frames are numbered from 1 to N, where N is the total number of frames in the video. " +
"The transcript is the audio dialog of the video\n";

private String framesStartPrompt = "Frames :";

private String framesEndPrompt = "\n\nTranscript :";

private String endPrompt = "\n\nTo be clear, the ask is: You are an editor writing brief video summaries for a website." + 
"Summarize a video in 2-3 paragraphs based on the transcript and the descriptions of the ordered frames.The frames are 6 seconds apart unless there are 250 or more of them. " +    
"The words in the frame go from more important to less important. Be concise in your response, and make the response approachable to someone with an associates degree " +
"Focus on actions being taken firstly, emotional context second (and only if there is emotive content in the frames) instead of technical details such as style or technology. " +
"If the frames aren't changing much, it may be a podcast. If so, focus on the transcription if it looks valid.If something is not clear, do your best to correct it. The transcript may be blank or garbage. If so, ignore it " +
"THIS IS A ONE-SHOT PROMPT. DO NOT ASK QUESTIONS. DO NOT DISCUSS THE INPUT.";

    @Consume
    public void consumeVideoDescription(FramesDescriptionAndTranscript videoDescription) {
        pipelineOrchestrator.submitData(new ProcessLog(videoDescription.getVideoId(), "Generating video summary"));
        final StringBuilder sb = new StringBuilder(prompt);
        sb.append(framesStartPrompt);
        int frame = 1;
        for (String frameDescription : videoDescription.getFrameDescriptions()) {
            sb.append(frame +":" +frameDescription +"\n");
            frame++;
        }       
        sb.append(framesEndPrompt);
        sb.append(videoDescription.getTranscription());
        sb.append(endPrompt);
        try {
            String summary = ollamaClient.generateText(ollamaModel, sb.toString());
            System.out.println(summary);

            pipelineOrchestrator.submitData(new VideoDescriptionResult(videoDescription.getVideoId(),
                summary, ollamaModel));
            pipelineOrchestrator.submitData(new ProcessLog(videoDescription.getVideoId(), "Video summary completed"));
            pipelineOrchestrator.submitData(new VideoStatus(videoDescription.getVideoId(), VideoStatusDTO.STATUS_COMPLETED, VideoStatusDTO.SUBSTATUS_COMPLETE));

        } catch (IOException e) {
            pipelineOrchestrator.submitData(videoDescription);
        }
    }

}
