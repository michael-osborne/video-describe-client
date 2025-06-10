package com.utopiarealized.videodescribe.client.pipeline.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.model.FrameDescription;
import com.utopiarealized.videodescribe.client.pipeline.model.TranscriptionResult;
import java.util.Map;
import com.utopiarealized.videodescribe.client.pipeline.model.FramesDescriptionAndTranscript;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;

@Service
public class VideoDescriptionAggregationService {
    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private Map<Integer, Holder> holders = new ConcurrentHashMap<>();

    @Consume
    public void consumeTranscription(TranscriptionResult transcription) {
        Holder holder = holders.computeIfAbsent(transcription.getVideoId(),
                (key) -> new Holder(transcription.getVideoId(), this));
        holder.setTranscription(transcription.getTranscription());
    }

    @Consume
    public void consumeFrameDescription(FrameDescription frameDescription) {
        Holder holder = holders.computeIfAbsent((int) frameDescription.getVideoId(),
                (key) -> new Holder((int) frameDescription.getVideoId(), this));
        holder.addFrameDescription(frameDescription);
    }

    @Consume
    public void consumeFrameNumber(VideoStatus videoStatus) {
        Holder holder = holders.computeIfAbsent((int) videoStatus.getId(),
                (key) -> new Holder((int) videoStatus.getId(), this));
        if (videoStatus.getTotalFrames() != null) {
            holder.setNumFrames((int) videoStatus.getTotalFrames());
        }
    }

    public void submitDataAndRemoveHolder(Holder holder) {
        pipelineOrchestrator
                .submitData(new FramesDescriptionAndTranscript(holder.videoId, holder.transcription, holder.frameDescriptions));
        holders.remove(holder.videoId);
    }

    class Holder {
        private int numFrames = -1;
        private int videoId;
        private List<String> frameDescriptions = new ArrayList<>();
        private String transcription;
        private VideoDescriptionAggregationService callback;

        public Holder(int videoId, VideoDescriptionAggregationService callback) {
            this.videoId = videoId;
            this.callback = callback;
        }

        public synchronized void addFrameDescription(FrameDescription frameDescription) {
            frameDescriptions.add(frameDescription.getDescription());
            if (frameDescriptions.size() >= numFrames && numFrames != -1) {
                callback.submitDataAndRemoveHolder(this);
            }
        }

        public synchronized void setTranscription(String transcription) {
            this.transcription = transcription;
            if (frameDescriptions.size() >= numFrames && numFrames != -1) {
                callback.submitDataAndRemoveHolder(this);
            }
        }

        public synchronized void setNumFrames(int numFrames) {
            this.numFrames = numFrames;
            if (frameDescriptions.size() >= numFrames) {
                callback.submitDataAndRemoveHolder(this);
            }
        }
    }

}
