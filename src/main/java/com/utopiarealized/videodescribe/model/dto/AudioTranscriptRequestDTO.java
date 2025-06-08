package com.utopiarealized.videodescribe.model.dto;

import java.util.Base64;

public class AudioTranscriptRequestDTO {
    private String url;
    private long videoId;
    private String base64EncodedAudio;

    public AudioTranscriptRequestDTO(String url, long videoId,  byte[] audio) {
        this.url = url;
        this.videoId = videoId;
        this.base64EncodedAudio = Base64.getEncoder().encodeToString(audio);
    }

    public String getUrl() {
        return url;
    }

    public long getVideoId() {
        return videoId;
    }
    
    public String getBase64EncodedAudio() {
        return base64EncodedAudio;
    }
}
