package com.utopiarealized.videodescribe.client.service.io;

import java.io.IOException;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;

public interface StatusIOService { 

    public void sendLog(long videoId, String log) throws IOException;

    public void updateVideoStatus(VideoStatusDTO videoDTO) throws IOException;  
}
