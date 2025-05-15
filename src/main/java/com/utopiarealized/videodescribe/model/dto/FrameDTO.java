package com.utopiarealized.videodescribe.model.dto;

public class FrameDTO {
    private double timestamp;
    private int sequence;
    private String description;
    private long videoId;

    public FrameDTO() {
    }

    public FrameDTO(double timestamp, int sequence, String description, long videoId) {
        this.timestamp = timestamp;
        this.sequence = sequence;
        this.description = description;
        this.videoId = videoId;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public int getSequence() {
        return sequence;
    }

    public String getDescription() {
        return description;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    @Override
    public String toString() {
        return "FrameDTO{" +
                "timestamp=" + timestamp +
                ", sequence=" + sequence +
                ", description=" + description +
                ", videoId=" + videoId +
                '}';
    }
}