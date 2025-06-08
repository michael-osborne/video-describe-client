package com.utopiarealized.videodescribe.client.pipeline.model.child;

public class AudioMetadata {
    private String audioCodec;
    private int sampleRate;
    private int channels;
    private int audioBitRate;
    private double audioDuration;

    public AudioMetadata(String audioCodec, int sampleRate, int channels, int audioBitRate, double audioDuration) {
        this.audioCodec = audioCodec;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.audioBitRate = audioBitRate;   
        this.audioDuration = audioDuration;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public int getSampleRate() {
        return sampleRate;
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
}
