package com.utopiarealized.videodescribe.client.pipeline.model;

import com.utopiarealized.videodescribe.client.pipeline.model.child.AudioMetadata;

public class WavFileResult extends PipelineData {
    
    private String location;
    private int videoId;
    private long sizeInBytes;
    private byte[] wavFile;

    private AudioMetadata audioMetadata;

    public WavFileResult(String location, int videoId, long sizeInBytes, byte[] wavFile, AudioMetadata audioMetadata) {
        this.location = location;
        this.videoId = videoId;
        this.sizeInBytes = sizeInBytes;
        this.wavFile = wavFile;
        this.audioMetadata = audioMetadata;
    }
    
    public String getLocation() {
        return location;
    }

    public int getVideoId() {
        return videoId;
    }   

    public long getSizeInBytes() {
        return sizeInBytes;
    }   

    public byte[] getWavFile() {
        return wavFile;
    }

    public AudioMetadata getAudioMetadata() {
        return audioMetadata;
    }
     
}
