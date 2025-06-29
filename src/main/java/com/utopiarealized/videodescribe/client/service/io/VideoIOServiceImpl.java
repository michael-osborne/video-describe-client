package com.utopiarealized.videodescribe.client.service.io;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.OkHttpClient;

import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import com.utopiarealized.videodescribe.model.dto.FrameDTO;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionResponseDTO;
import com.utopiarealized.videodescribe.model.dto.MetadataDTO;
import com.utopiarealized.videodescribe.model.dto.FrameDescriptionRequestDTO;

import com.utopiarealized.videodescribe.model.dto.VideoTranscriptionDTO;

import org.bytedeco.librealsense2.global.realsense2;
import org.slf4j.Logger;
import com.utopiarealized.videodescribe.model.dto.VideoDescriptionDTO;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.utopiarealized.videodescribe.model.dto.AudioTranscriptResponseDTO;
import com.utopiarealized.videodescribe.model.dto.AudioTranscriptRequestDTO;
import com.utopiarealized.videodescribe.client.pipeline.model.TranscriptionResult;

@Service
public class VideoIOServiceImpl implements VideoIOService {

    private static final Logger logger = LoggerFactory.getLogger(VideoIOServiceImpl.class);

    @Value("${host.url}")
    private String HOST_URL = "http://localhost:6660";

    @Value("${audio.transcript.url}")
    private String AUDIO_TRANSCRIPT_URL = "http://localhost:6660/api/audio-transcript";

    @Value("${frame.description.url}")
    private String FRAME_DESCRIPTION_URL = "http://localhost:5680/";

    @Value("${video.url}")
    private String GET_VIDEO_ENDPOINT = "/video-srvr/api/next-video";

    private String FRAME_ENDPOINT = "/video-srvr/api/frame";

    private String TRANSCRIPT_ENDPOINT = "/video-srvr/api/transcription";

    private String POST_METADATA_ENDPOINT = "/video-srvr/api/metadata";

    private String PUT_VIDEO_DESCRIPTION_ENDPOINT = "/video-srvr/api/video-description";

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final OkHttpClient frameDescriptionClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // Time to establish connection
            .readTimeout(90, TimeUnit.SECONDS) // Time to read response data
            .writeTimeout(15, TimeUnit.SECONDS) // Time to send request body
            .build();

    private static final OkHttpClient audioTranscriptClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // Time to establish connection
            .readTimeout(300, TimeUnit.SECONDS) // Time to read response data
            .writeTimeout(300, TimeUnit.SECONDS) // Time to send request body
            .build();

    private String descriptionUrl = "";

    public VideoStatusDTO getNextVideo() throws IOException {
        Request request = new Request.Builder().url(HOST_URL + GET_VIDEO_ENDPOINT).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get next video: " + response.code());
            }

            String jsonString = response.body().string();
            VideoStatusDTO videoDTO = mapper.readValue(jsonString, VideoStatusDTO.class);
            return videoDTO;
        }
    }

    public void sendMetadata(MetadataDTO metadataDTO) {
        Response response = null;
        try {
            Request request = new Request.Builder()
                    .post(RequestBody.create(mapper.writeValueAsString(metadataDTO),
                            MediaType.parse("application/json")))
                    .url(HOST_URL + POST_METADATA_ENDPOINT).build();
            response = client.newCall(request).execute();
        } catch (IOException e) {
            logger.error("Error sending metadata: " + e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    public void postFrameDescription(FrameDTO frameDTO) throws IOException {
        Request request = new Request.Builder().url(HOST_URL + FRAME_ENDPOINT)
                .method("POST",
                        RequestBody.create(mapper.writeValueAsString(frameDTO), MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200) {
                throw new IOException("Failed to send frame: " + response.code());
            }
        }
    }

    @Override
    public AudioTranscriptResponseDTO getAudioTranscript(AudioTranscriptRequestDTO audioTranscriptRequestDTO)
            throws IOException {
        ObjectNode payload = mapper.createObjectNode().put("audio", audioTranscriptRequestDTO.getBase64EncodedAudio());

        String payloadString = mapper.writeValueAsString(payload);
        Request request = new Request.Builder()
                .url(AUDIO_TRANSCRIPT_URL)
                .post(RequestBody.create(payloadString, MediaType.parse("application/json")))
                .build();

        try (Response response = audioTranscriptClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get audio transcript: " + response.code());
            }
            final String body = response.body().string();
            logger.info("Audio transcript response: " + body);
            JsonNode json = mapper.readTree(body);
            String curTranscription = "";
            if (json.get("transcription") != null) {
                curTranscription = getTranscriptionTextFromString(json.get("transcription").asText());
            }
            return new AudioTranscriptResponseDTO(curTranscription, "parakeet-tdt-0.6b-v2");
        }
    }

    private String getTranscriptionTextFromString(String transcription){
        if (transcription == null ) {
            return "";
        }
        int startIndex = transcription.indexOf("text=");
        if (startIndex == -1) {
            return "";
        }
        String end= transcription.substring(startIndex+5,startIndex+6);
        startIndex +=6;
        int endIndex = transcription.indexOf(end, startIndex+1);
        if ( endIndex == -1) {
            return "";
        }
        return transcription.substring(startIndex, endIndex);
    }

    @Override
    public void uploadTranscription(TranscriptionResult transcriptionResult) throws IOException {
        Request request = new Request.Builder().url(HOST_URL + TRANSCRIPT_ENDPOINT)
                .method("POST",
                        RequestBody.create(mapper.writeValueAsString(
                                new VideoTranscriptionDTO(transcriptionResult.getVideoId(),
                                transcriptionResult.getTranscription(),
                                transcriptionResult.getTranscriber())),
                                MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200) {
                throw new IOException("Failed to send transcription: " + response.code());
            }
        }
    }

    public FrameDescriptionResponseDTO getFrameDescription(FrameDescriptionRequestDTO frameDescriptionRequestDTO)
            throws IOException {

        ObjectNode payload = mapper.createObjectNode().put("image", frameDescriptionRequestDTO.getBase64EncodedFrame());
        payload.put("clip_model_name", frameDescriptionRequestDTO.getClipModelName());
        payload.put("mode", frameDescriptionRequestDTO.getMode());

        String payloadString = mapper.writeValueAsString(payload);
        Request request = new Request.Builder()
                .url(frameDescriptionRequestDTO.getUrl())
                .post(RequestBody.create(payloadString, MediaType.parse("application/json")))
                .build();

        try (Response response = frameDescriptionClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get frame description: " + response.code());
            }

            JsonNode json = mapper.readTree(response.body().string());
            String text = json.get("prompt").asText();
            return new FrameDescriptionResponseDTO(text);
        }
    }

    @Override
    public void postFullDescription(VideoDescriptionDTO videoDescription) throws IOException {
        Request request = new Request.Builder().url(HOST_URL + PUT_VIDEO_DESCRIPTION_ENDPOINT)
                .method("PUT",
                        RequestBody.create(mapper.writeValueAsString(
                                videoDescription),
                                MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to send video description: " + response.code());
            }
        }
    }

}
