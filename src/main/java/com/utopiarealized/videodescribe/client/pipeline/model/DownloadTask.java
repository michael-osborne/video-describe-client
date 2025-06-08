package com.utopiarealized.videodescribe.client.pipeline.model;

public class DownloadTask extends PipelineData {

    private String url;
    private int videoId;

    public DownloadTask(String url, int videoId) {
        this.url = url;
        this.videoId = videoId;
    }

    
    public String getUrl() {
        return url;
    }

    public int getVideoId() {
        return videoId;
    }
    
}
