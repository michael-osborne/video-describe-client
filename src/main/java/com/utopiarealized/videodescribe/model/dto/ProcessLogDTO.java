package com.utopiarealized.videodescribe.model.dto;


import java.time.LocalDateTime;

public class ProcessLogDTO {
    private Long videoId;
    private LocalDateTime createdAt;
    private String message;
    
    public ProcessLogDTO(Long videoId, LocalDateTime createdAt, String message) {
        this.videoId = videoId;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ProcessLogDTO{" +
                "videoId=" + videoId +
                ", createdAt=" + createdAt +
                ", message='" + message + '\'' +
                '}';
    }
    
}
