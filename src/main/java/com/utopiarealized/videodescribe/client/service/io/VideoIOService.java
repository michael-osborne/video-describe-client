package com.utopiarealized.videodescribe.client.service.io;

import java.io.IOException;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;

import com.utopiarealized.videodescribe.model.dto.MetadataDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionResponseDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionRequestDTO;
public interface VideoIOService {

    public VideoStatusDTO getNextVideo() throws IOException;

    public void sendMetadata(MetadataDTO metadataDTO) throws IOException;

    public void postFrame(FrameDTO frameDTO) throws IOException;

    public FrameDescriptionResponseDTO getFrameDescription(FrameDescriptionRequestDTO frameDescriptionRequestDTO) throws IOException;

}