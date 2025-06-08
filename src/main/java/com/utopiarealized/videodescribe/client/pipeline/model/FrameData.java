package com.utopiarealized.videodescribe.client.pipeline.model;

import java.util.Base64;
public class FrameData extends PipelineData {
    private int videoId;
    private byte[] bytes;
    private double timestamp;
    private int sequence;
    private boolean lastFrame;
    private int numFrames;
    private String location;

    public FrameData(int videoId, String location,byte[] bytes, double timestamp, int sequence, boolean lastFrame, int numFrames) {
        this.videoId = videoId;
        this.bytes = bytes;
        this.timestamp = timestamp;
        this.sequence = sequence;
        this.lastFrame = lastFrame;
        this.location = location;
        this.numFrames = numFrames;
    }   
    
    public int getNumFrames() {
        return numFrames;
    }   

    public int getVideoId() {
        return videoId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getBase64() {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public double getTimestamp() {
        return timestamp;   
    }

    public int getSequence() {
        return sequence;
    }

    public boolean isLastFrame() {
        return lastFrame;
    }

    public String getLocation() {
        return location;
    }


    @Override
    public String toString() {
        return "FrameData{" +
                "videoId=" + videoId +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                ", sequence=" + sequence +
                ", lastFrame=" + lastFrame +
                '}';
    }
}