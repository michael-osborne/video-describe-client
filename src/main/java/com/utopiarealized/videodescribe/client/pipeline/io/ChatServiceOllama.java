package com.utopiarealized.videodescribe.client.pipeline.io;
 

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;

public class ChatServiceOllama {
    private static final String API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "deepseek-r1:32b"; // or "deepseek-r1:14b"
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
/*
    public String generate(String prompt) throws IOException {
        // Build JSON payload
        String jsonPayload = mapper.writeValueAsString(new RequestBody(
            MODEL,
            prompt,
            false // stream: false for simplicity
        ));

        // Create request
        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
            .url(API_URL)
            .post(body)
            .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + ": " + response.body().string());
            }

            // Parse response
            StringBuilder result = new StringBuilder();
            String responseBody = response.body().string();
            // Ollama returns one JSON object per line
            for (String line : responseBody.split("\n")) {
                if (!line.trim().isEmpty()) {
                    JsonNode json = mapper.readTree(line);
                    if (json.has("response")) {
                        result.append(json.get("response").asText());
                    }
                }
            }
            return result.toString();
        }
    }

    // Request payload class
    private static class RequestBody {
        String model;
        String prompt;
        boolean stream;

        RequestBody(String model, String prompt, boolean stream) {
            this.model = model;
            this.prompt = prompt;
            this.stream = stream;
        }
    }

    public static void main(String[] args) {
        DeepSeekOllamaClient client = new DeepSeekOllamaClient();
        try {
            String response = client.generate("Write a Java function to reverse a string.");
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        */
}
