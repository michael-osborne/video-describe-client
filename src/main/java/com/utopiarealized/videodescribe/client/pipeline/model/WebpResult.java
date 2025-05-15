package com.utopiarealized.videodescribe.client.pipeline.model;

public class WebpResult extends PipelineData {
    private String location;
    private long videoId;
    private int width;
    private int height;
    private long sizeInBytes;

    public WebpResult(String location, long videoId, int width, int height, long sizeInBytes) {
        this.location = location;
        this.videoId = videoId;
        this.width = width;
        this.height = height;
        this.sizeInBytes = sizeInBytes;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public String toString() {
        return "WebpResult{" +
                "location='" + location + '\'' +
                ", videoId=" + videoId +
                ", width=" + width +
                ", height=" + height +
                ", sizeInBytes=" + sizeInBytes +
                '}';
    }
    
    public String getLocation() {
        return location;
    }

    public long getVideoId() {
        return videoId;
    }
}
