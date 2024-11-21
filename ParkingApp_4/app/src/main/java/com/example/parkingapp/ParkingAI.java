package com.example.parkingapp;

import android.util.Log;

import okhttp3.*;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ParkingAI {
    private String apiKey;
    private String modelName;
    private String filePath;

    // Constructor
    public ParkingAI(String apiKey, String modelName, String filePath) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.filePath = filePath;
    }

    // Method to load parking data from a CSV file
    private String loadParkingData() {
        StringBuilder dataBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        System.out.println("Loaded Data:\n" + dataBuilder);
        Log.d("ParkingAI", "Loaded Data:\n" + dataBuilder);
        return dataBuilder.toString();
    }

    // Method to prepare the message
    private String prepareMessageFromData(String data) {
//        String message = "Here is the parking data:\n" + data +
//                "\nPlease provide the expected next available time for the parking spot. " +
//                "Gimme just the time. Also, do not print anything with the time, just the time please.";
//        System.out.println("Message to be sent:\n" + message);
        String message = "Hii";
        System.out.println("Message to be sent:\n" + message);
        Log.d("ParkingAI", "Message to be sent:\n" + message);
        return message;
    }

    // Method to send the message to the AI model and get the response
    public String getNextAvailableTime() {
        try {

            Log.d("ParkingAI", "HI");
            String parkingData = loadParkingData();
            String message = prepareMessageFromData(parkingData);

            // HTTP Client
            OkHttpClient client = new OkHttpClient();

            // JSON Body for the request
            JSONObject requestBody = new JSONObject();
            requestBody.put("model_name", modelName);
            requestBody.put("temperature", 1);
            requestBody.put("top_p", 0.95);
            requestBody.put("top_k", 40);
            requestBody.put("max_output_tokens", 8192);
            requestBody.put("message", message);

            Request request = new Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent")  // Replace with the correct endpoint
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("Full response: " + responseBody);

                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.has("text")) {
                    return jsonResponse.getString("text").trim();
                }
            } else {
                System.err.println("Request failed: " + response.code());
            }
        } catch (Exception e) {
            Log.d("ParkingAI", "in catch");
            e.printStackTrace();
        }
        return null;
    }
}