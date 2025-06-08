package com.utopiarealized.videodescribe.model.dto;

public class MetadataDTO {
    private long videoId;
    private int totalFrames;
    private int width;
    private int height;
    private double duration;
    private double fps;
    private int size;
    private String audioCodec;
    private int audioSampleRate;
    private int channels;
    private int audioBitRate;
    private double audioDuration;
    private boolean hasAudio;

    public MetadataDTO() {
    }

    public MetadataDTO(long videoId, int totalFrames, int width,
            int height, double duration, double fps, int size, String audioCodec, int audioSampleRate, int channels,
            int audioBitRate, double audioDuration) {
        this.videoId = videoId;
        this.totalFrames = totalFrames;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.fps = fps;
        this.size = size;
        this.audioCodec = audioCodec;
        this.audioSampleRate = audioSampleRate;
        this.channels = channels;
        this.audioBitRate = audioBitRate;
        this.audioDuration = audioDuration;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public long getVideoId() {
        return videoId;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public int getAudioBitRate() {
        return audioBitRate;
    }

    public double getAudioDuration() {
        return audioDuration;
    }

    public int getSize() {
        return size;
    }

    public int getTotalFrames() {
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
                "totalFrames=" + totalFrames +
                ", width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                ", fps=" + fps +
                ", size=" + size +
                ", audioCodec=" + audioCodec +
                ", audioSampleRate=" + audioSampleRate +
                ", channels=" + channels +
                ", audioBitRate=" + audioBitRate +
                ", audioDuration=" + audioDuration +
                ", hasAudio=" + hasAudio +
                '}';
    }

}
