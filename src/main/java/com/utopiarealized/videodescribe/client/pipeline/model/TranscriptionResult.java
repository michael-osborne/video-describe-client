package com.utopiarealized.videodescribe.client.pipeline.model;

public class TranscriptionResult extends PipelineData{
    private int videoId;
    private String transcription;
    private String transcriber;

    public TranscriptionResult(int videoId, String transcription, String transcriber) {
        this.videoId = videoId;
        this.transcription = transcription;
        this.transcriber = transcriber;
    }

    public int getVideoId() {
        return videoId;
    }

    public String getTranscription() {
        return transcription;
    }   

    public String getTranscriber() {
        return transcriber;
    }
}
