package com.utopiarealized.videodescribe.client.service.io;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.utopiarealized.videodescribe.model.dto.OllamaDTO;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import okhttp3.*;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
@Service
public class OllamaClient {
    @Value("${ollama.api.url}")
    private String OLLAMA_API_URL="http://localhost:11434/api/generate";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // Time to establish connection
            .readTimeout(60, TimeUnit.SECONDS) // Time to read response data
            .writeTimeout(15, TimeUnit.SECONDS) // Time to send request body
            .build();



    public String generateText(String model, String prompt) throws IOException {
        // Create JSON payload
        
        final OllamaDTO ollamaDTO = new OllamaDTO(model, prompt, false);

        // Build request
        RequestBody body = RequestBody.create(mapper.writeValueAsString(ollamaDTO), JSON);
        Request request = new Request.Builder()
            .url(OLLAMA_API_URL)
            .post(body).build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + ": " + response.body().string());
            }
            JsonNode json = mapper.readTree(response.body().string());
            return json.get("response").asText();
        }
    }

    public static void main(String[] args) {
        OllamaClient ollamaClient = new OllamaClient();
        try {
            String model = "qwen3:14b";
            String prompt = "What is the capital of France?";
            String response = ollamaClient.generateText(model, prompt);
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
