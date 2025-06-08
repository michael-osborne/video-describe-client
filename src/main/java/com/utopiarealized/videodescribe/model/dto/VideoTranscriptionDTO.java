package com.utopiarealized.videodescribe.model.dto;

public class VideoTranscriptionDTO {
    private int videoId;
    private String transcription;
        private String transcriber;

    public VideoTranscriptionDTO(int videoId, String transcription, String transcriber) {
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
