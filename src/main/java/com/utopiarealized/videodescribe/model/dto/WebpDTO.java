package com.utopiarealized.videodescribe.model.dto;

public class WebpDTO {
    private long videoId;
    private int width;
    private int height;
    private long sizeInBytes;       

    public WebpDTO(long videoId, int width, int height, long sizeInBytes) {
        this.videoId = videoId;
        this.width = width;
        this.height = height;
        this.sizeInBytes = sizeInBytes;
    }
    
    public long getVideoId() {
        return videoId;
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
        return "WebpDTO{" +
                "videoId=" + videoId +
                ", width=" + width +
                ", height=" + height +
                ", sizeInBytes=" + sizeInBytes +
                '}';
    }
}
