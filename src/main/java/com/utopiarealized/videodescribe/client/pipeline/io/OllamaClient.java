package com.utopiarealized.videodescribe.client.pipeline.io;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.*;
import java.io.IOException;

@Service
public class OllamaClient {
    @Value("${ollama.api.url}")
    private String OLLAMA_API_URL;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    public OllamaClient() {
        this.client = new OkHttpClient();
    }

    public String generateText(String model, String prompt) throws IOException {
        // Create JSON payload
        String jsonPayload = String.format(
            "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
            model, prompt
        );

        // Build request
        RequestBody body = RequestBody.create(jsonPayload, JSON);
        Request request = new Request.Builder()
            .url(OLLAMA_API_URL)
            .post(body)
            .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + ": " + response.body().string());
            }
            return response.body().string();
        }
    }

    public static void main(String[] args) {
        OllamaClient ollamaClient = new OllamaClient();
        try {
            String model = "llama3";
            String prompt = "What is the capital of France?";
            String response = ollamaClient.generateText(model, prompt);
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
