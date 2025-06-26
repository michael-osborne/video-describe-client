package com.utopiarealized.videodescribe.model.dto;


public class OllamaDTO {
    private String model;
    private String prompt;
    private boolean stream;

    public OllamaDTO(String model, String prompt, boolean stream) {
        this.model = model;
        this.prompt = prompt;
        this.stream = stream;
    }

    public String getModel() {
        return model;
    }

    public String getPrompt(){
        return prompt;
    }

    public boolean getStream(){
        return stream;
    }
}
