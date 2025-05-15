package com.utopiarealized.videodescribe.client.pipeline.model;

public class FrameDescription extends PipelineData {
    private String description;
    private double timestamp;
    private int sequence;
    private long videoId;
    private boolean isLastFrame;
    

    public FrameDescription(long videoId, String description, double timestamp, int sequence, boolean isLastFrame) {
        this.description = description;
        this.timestamp = timestamp;
        this.sequence = sequence;
        this.isLastFrame = isLastFrame;
        this.videoId = videoId;
    }

    public boolean isLastFrame() {
        return isLastFrame;
    }

    public long getVideoId() {
        return videoId;
    }

    public String getDescription() {
        return description;
    }

    public double getTimestamp() {
        return timestamp;       
    }

    public int getSequence() {
        return sequence;
    }   

    @Override
    public String toString() {
        return "FrameDescription{" +
                "description=" + description +
                ", timestamp=" + timestamp +    
                ", sequence=" + sequence +
                ", videoId=" + videoId +
                ", isLastFrame=" + isLastFrame +
                '}';
    }
} 