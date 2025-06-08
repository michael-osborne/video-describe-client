package com.utopiarealized.videodescribe.client.service.io;

import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.utopiarealized.videodescribe.model.dto.FrameDTO;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utopiarealized.videodescribe.model.dto.WebpDTO;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.model.dto.WavMetadataDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class FileUploadService {

    @Value("${host.url}")
    private String HOST_URL = "http://localhost:6660";
    private String VIDEO_ENDPOINT = "/video-srvr/api/webp";
    private String FRAME_ENDPOINT = "/video-srvr/api/frame";
    private String WAV_ENDPOINT = "/video-srvr/api/wav";
    private final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final long PROGRESS_UPDATE_INTERVAL = 2000;

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    public void uploadVideoThumbnail(String fileLocation, WebpDTO metadata)
            throws IOException {
        uploadFileWithMetadata(HOST_URL + VIDEO_ENDPOINT, fileLocation, metadata,
                new StatusUploadProgressListener(pipelineOrchestrator, metadata.getVideoId()));
    }

    public void uploadFrame(String fileLocation, FrameDTO frameDTO)
            throws IOException {
        uploadFileWithMetadata(HOST_URL + FRAME_ENDPOINT, fileLocation, frameDTO,
                null);
    }

    public void uploadWav(String fileLocation, WavMetadataDTO wavMetadataDTO)
            throws IOException {
        uploadFileWithMetadata(HOST_URL + WAV_ENDPOINT, fileLocation, wavMetadataDTO,
                null);
    }

    private void uploadFileWithMetadata(String url, String fileLocation, Object metadata,
            ProgressListener progressListener) throws IOException {

        RequestBody fileBody = RequestBody.create(
                new File(fileLocation),
                MediaType.parse("application/octet-stream"));

        // Build multipart request with file and metadata
        RequestBody requestBody;
        if (progressListener != null) {
            requestBody = new ProgressRequestBody(new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileLocation, fileBody)
                    .addFormDataPart("metadata", mapper.writeValueAsString(metadata))
                    .build(), progressListener);
        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileLocation, fileBody)
                    .addFormDataPart("metadata", mapper.writeValueAsString(metadata))
                    .build();
        }

        // Create and execute request
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.code());
            }
        }
    }

    class StatusUploadProgressListener implements ProgressListener {
        int previousPercentage = 0;
        PipelineOrchestrator pipelineOrchestrator;
        long lastWriteTime = 0;
        long videoId;

        StatusUploadProgressListener(PipelineOrchestrator pipelineOrchestrator, long videoId) {
            this.pipelineOrchestrator = pipelineOrchestrator;
            this.videoId = videoId;
        }

        @Override
        public void onProgress(long bytesWritten, long totalBytes) {
            int percentage = (int) ((bytesWritten * 100) / totalBytes);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastWriteTime > PROGRESS_UPDATE_INTERVAL || percentage == 100) {
                lastWriteTime = currentTime;
                if (percentage != previousPercentage) {
                    previousPercentage = percentage;
                    pipelineOrchestrator.submitData(new ProcessLog(videoId,
                            "Uploaded  " + percentage + "% (" + bytesWritten + "/" + totalBytes + ")"));
                }
            }
        }
    }

    class ProgressRequestBody extends RequestBody {
        private final RequestBody requestBody;
        private final ProgressListener progressListener;

        public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
            this.requestBody = requestBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        @Override
        public void writeTo(okio.BufferedSink sink) throws IOException {
            okio.ForwardingSink forwardingSink = new okio.ForwardingSink(sink) {
                long bytesWritten = 0L;

                @Override
                public void write(okio.Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    bytesWritten += byteCount;
                    progressListener.onProgress(bytesWritten, contentLength());
                }
            };
            okio.BufferedSink bufferedSink = okio.Okio.buffer(forwardingSink);
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        }
    }
}

