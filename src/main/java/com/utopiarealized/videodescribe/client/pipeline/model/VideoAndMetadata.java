package com.utopiarealized.videodescribe.client.pipeline.model;

public class VideoAndMetadata extends PipelineData {
    private DownloadResult downloadResult;
    private int width;
    private int height;
    private double duration;
    private double fps;
    private long totalFrames;

    public VideoAndMetadata(DownloadResult downloadResult, int width, int height, double duration, double fps, long totalFrames) {
        this.downloadResult = downloadResult;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.fps = fps;
        this.totalFrames = totalFrames;
    }

    public DownloadResult getDownloadResult() {
        return downloadResult;
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

    public long getTotalFrames() {
        return totalFrames;
    }   

    @Override
    public String toString() {
        return "VideoAndMetadata{" +
                "downloadResult=" + downloadResult +
                ", width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                ", fps=" + fps +
                ", totalFrames=" + totalFrames +
                '}';
    }
}
