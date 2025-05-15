package com.utopiarealized.videodescribe.client.service.io;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utopiarealized.videodescribe.model.dto.ProcessLogDTO;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import java.io.IOException;
@Service
public class StatusIOServiceImpl implements StatusIOService {

    @Value("${host.url}")
    private String HOST_URL = "http://localhost:6660";
    private String UPDATE_VIDEO_ENDPOINT = "/video-srvr/api/update-video";
    private String LOG_ENDPOINT = "/video-srvr/api/process-log";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public void sendLog(long videoId, String log) throws IOException {
        ProcessLogDTO processLogDTO = new ProcessLogDTO(videoId, null, log);
        Request request = new Request.Builder()
                .post(RequestBody.create(mapper.writeValueAsString(processLogDTO),
                        MediaType.parse("application/json")))
                .url(HOST_URL + LOG_ENDPOINT).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to send log: " + response.code());
            }
        } 
    }

    public void updateVideoStatus(VideoStatusDTO videoDTO) throws IOException {
        Request request = new Request.Builder()
                .put(RequestBody.create(mapper.writeValueAsString(videoDTO), MediaType.parse("application/json")))
                .url(HOST_URL + UPDATE_VIDEO_ENDPOINT).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update video: " + response.code());
            }
        }
    }
}