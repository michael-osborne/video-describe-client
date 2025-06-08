package com.utopiarealized.videodescribe.client.pipeline.model.child;

import com.utopiarealized.videodescribe.client.pipeline.model.PipelineData;

public class VideoMetadata extends PipelineData {
    private int width;
    private int height;
    private double duration;
    private double fps;
    private long totalFrames;

    public VideoMetadata(int width, int height, double duration, double fps, long totalFrames) {
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.fps = fps;
        this.totalFrames = totalFrames;
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
}
