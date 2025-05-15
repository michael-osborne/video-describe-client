package com.utopiarealized.videodescribe.model.dto;

public class MetadataDTO {
    private long videoId;
    private long totalFrames;
    private int width;
    private int height;
    private double duration;
    private double fps;
    public int size;
    
    public MetadataDTO(long videoId, long totalFrames, int width, int height, double duration, double fps, int size) {
        this.videoId = videoId;
        this.totalFrames = totalFrames;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.fps = fps;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public long getVideoId() {
        return videoId;
    }   

    public long getTotalFrames() {
        return totalFrames;
    }   

    public int getWidth() {
        return width;
    }   

    public int getHeight() {
        return height;
    }                  

    public double getDuration() {
        return duration;
    }   

    public double getFps() {
        return fps;
    }
    
    
    @Override
    public String toString() {
        return "MetadataDTO{" +
                "videoId=" + videoId +
                ", totalFrames=" + totalFrames +
                ", width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                ", fps=" + fps +
                ", size=" + size +
                '}';
    }

}
