package com.utopiarealized.videodescribe.model.dto;

public class FrameDescriptionResponseDTO {
    private String description;

    public FrameDescriptionResponseDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }   
    
    @Override
    public String toString() {
        return "FrameDescriptionResponseDTO{" +
                "description='" + description + '\'' +
                '}';
    }
}

