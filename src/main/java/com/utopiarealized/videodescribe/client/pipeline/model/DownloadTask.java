package com.utopiarealized.videodescribe.client.pipeline.model;

public class DownloadTask extends PipelineData {

    private String url;
    private long videoId;

    public DownloadTask(String url, long videoId) {
        this.url = url;
        this.videoId = videoId;
    }

    
    public String getUrl() {
        return url;
    }

    public long getVideoId() {
        return videoId;
    }
    
}
