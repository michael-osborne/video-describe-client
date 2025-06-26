package com.utopiarealized.videodescribe.model.dto;

public class AudioTranscriptResponseDTO {
    private String transcript;
    private String transcriber;

    public AudioTranscriptResponseDTO(String transcript, String transcriber) {
        this.transcript = transcript;
        this.transcriber = transcriber;
    }
    
    public String getTranscriber() {
        return transcriber;
    }
    public String getTranscript() {
        return transcript;
    }
}
