package com.utopiarealized.videodescribe.model.dto; 

public class WavMetadataDTO {
    private int videoId;
    private String codec;
    private int sampleRate;
    private int channels;
    private int bitRate;
    private double duration;

    private int size;

    public WavMetadataDTO(int videoId, String codec, int sampleRate, int channels, int bitRate, double duration) {
        this.videoId = videoId;
        this.codec = codec;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitRate = bitRate;
        this.duration = duration;
    }
    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
