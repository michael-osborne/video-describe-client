package com.utopiarealized.videodescribe.client.pipeline.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;

public class DeepSeekVLLMClient {
    private static final String API_URL = "http://localhost:8000/v1/completions";
    private static final String MODEL = "deepseek-r1-distill-qwen-32b-awq"; // or other model name
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
/*
    public String generate(String prompt) throws IOException {
        // Build JSON payload
        String jsonPayload = mapper.writeValueAsString(new RequestBody(
                MODEL,
                prompt,
                1.0, // temperatureal
                1024 // max_tokens
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
            JsonNode json = mapper.readTree(response.body().string());
            JsonNode choices = json.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                return choices.get(0).get("text").asText();
            } else {
                throw new IOException("No valid choices in response");
            }
        }
    }

    // Request payload class
    private static class RequestBody {
        String model;
        String prompt;
        double temperature;
        int max_tokens;

        RequestBody(String model, String prompt, double temperature, int max_tokens) {
            this.model = model;
            this.prompt = prompt;
            this.temperature = temperature;
            this.max_tokens = max_tokens;
        }
    }

    public static void main(String[] args) {
        DeepSeekVLLMClient client = new DeepSeekVLLMClient();
        try {
            String response = client.generate("Write a Java function to reverse a string.");
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        */
}
