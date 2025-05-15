package com.utopiarealized.videodescribe.model.dto;

import java.util.Base64;

public class FrameDescriptionRequestDTO {
    private String url;
    private String base64EncodedFrame;
    private String clipModelName;
    private String mode;

    public FrameDescriptionRequestDTO(String url, byte[] frame, String clipModelName, String mode) {
        this.url = url;
        this.base64EncodedFrame = Base64.getEncoder().encodeToString(frame);
        this.clipModelName = clipModelName;
        this.mode = mode;
    }

    public String getBase64EncodedFrame() {
        return base64EncodedFrame;
    }

    public String getUrl() {
        return url;
    }

    public String getClipModelName() {
        return clipModelName;
    }

    public String getMode() {
        return mode;
    }
}
