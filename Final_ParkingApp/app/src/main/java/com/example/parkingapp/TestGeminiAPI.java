package com.example.parkingapp;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;

public class TestGeminiAPI {

    private static final String API_KEY = "AIzaSyCL6_2CegP8QXrjdLipxUiiqINMzySFhAc";
    private static final String MODEL_NAME = "gemini-1.5-flash";
    private static final String API_URL = "https://generativeai.googleapis.com/v1beta3/models/" + MODEL_NAME + ":generateText";

    public static void main(String[] args) {
        // Message we want to send to the API
        String message = "Hi";

        try {
            // Create the request payload
            JsonObject payload = new JsonObject();
            payload.addProperty("prompt", message);
            payload.addProperty("temperature", 1);
            payload.addProperty("top_p", 0.95);
            payload.addProperty("top_k", 40);
            payload.addProperty("max_output_tokens", 100); // Limit response length

            // Create OkHttpClient and request body
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));

            // Build the request
            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + API_KEY)
                    .post(body)
                    .build();

            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    System.out.println("API Response: " + responseString);

                    // Parse the response using Gson
                    JsonElement responseJsonElement = JsonParser.parseString(responseString);
                    JsonObject responseJson = responseJsonElement.getAsJsonObject();
                    String text = responseJson.has("text") ? responseJson.get("text").getAsString() : "No response text found";

                    // Output the generated text
                    System.out.println("Generated Response: " + text);
                } else {
                    System.out.println("Request failed: " + response.message());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
