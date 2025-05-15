package com.utopiarealized.videodescribe.client.pipeline.model;

public class ProcessLog extends PipelineData {
    private String message;
    private long videoId;

    public ProcessLog(long videoId, String message) {
        this.videoId = videoId;
        this.message = message;
    }
    
    public long getVideoId() {
        return videoId;
    }

    public String getMessage() {
        return message;
    }
}
