package com.utopiarealized.videodescribe.client.service.io;

import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import com.utopiarealized.videodescribe.model.dto.MetadataDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionResponseDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionRequestDTO;


public class VideoIOServiceMock implements VideoIOService {
    @Override
    public VideoStatusDTO getNextVideo() {
        return null;
    }

    @Override
    public void sendMetadata(MetadataDTO metadataDTO) {
    }

    @Override
    public void postFrame(FrameDTO frameDTO) {
    }

    @Override
    public FrameDescriptionResponseDTO getFrameDescription(FrameDescriptionRequestDTO frameDescriptionRequestDTO) {
        return null;
    }

}
