package com.utopiarealized.videodescribe.model.dto;

public class AudioTranscriptResponseDTO {
    private String transcript;

    public AudioTranscriptResponseDTO(String transcript) {
        this.transcript = transcript;
    }
    
    public String getTranscript() {
        return transcript;
    }
}
