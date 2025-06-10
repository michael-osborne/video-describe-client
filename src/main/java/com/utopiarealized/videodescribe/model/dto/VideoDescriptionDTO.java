package com.utopiarealized.videodescribe.model.dto;

public class VideoDescriptionDTO {
    private int videoId;
    private String description;
    private String describer;

    public VideoDescriptionDTO(int videoId, String description, String describer) {
        this.videoId = videoId;
        this.description = description;
        this.describer = describer;
    }
    
    public int getVideoId() {
        return videoId;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriber() {
        return describer;
    }
}
