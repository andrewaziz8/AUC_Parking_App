package com.example.parkingapp;

import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import okhttp3.*;
import org.json.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ParkingDataProcessor {

    private static final String API_KEY = "AIzaSyCL6_2CegP8QXrjdLipxUiiqINMzySFhAc";
    private static final String MODEL_NAME = "gemini-1.5-flash";
    private static final String API_URL = "https://generativeai.googleapis.com/v1beta3/models/" + MODEL_NAME + ":generateText";

    public static void main(String[] args) {
        String filePath = "D://Semester 10 (Fall 2024)//Software Engineering//Project//ParkingApp//parking_data//A1.csv";

        try {
            List<String[]> data = loadParkingData(filePath);
            String message = prepareMessageFromData(data);
            String nextAvailableTime = getNextAvailableTime(message);

            if (nextAvailableTime != null && !nextAvailableTime.isEmpty()) {
                System.out.println("The next available time for the spot is: " + nextAvailableTime.trim());
            } else {
                System.out.println("Could not determine the next available time.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> loadParkingData(String filePath) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();
            System.out.println("Loaded Data:");
            rows.forEach(row -> System.out.println(String.join(", ", row))); // Print each row
            return rows;
        }
    }

    public static String prepareMessageFromData(List<String[]> data) {
        StringBuilder dataString = new StringBuilder();
        data.forEach(row -> dataString.append(String.join(", ", row)).append("\n"));

        String message = "Here is the parking data:\n" + dataString +
                "Please provide the expected next available time for the parking spot. " +
                "Gimme just the time. Also, do not print anything with the time, just the time please.";
        System.out.println("Message to be sent:");
        System.out.println(message);
        Log.d("ParkingDataProcessor", "Full response: " + message); //change
        return message;
    }

    public static String getNextAvailableTime(String message) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Create the JsonObject and add properties
        JsonObject payload = new JsonObject();
        payload.addProperty("prompt", message);
        payload.addProperty("temperature", 1);
        payload.addProperty("top_p", 0.95);
        payload.addProperty("top_k", 40);
        payload.addProperty("max_output_tokens", 8192);

//        JsonObject payload = new JsonObject()
//                .put("prompt", message)
//                .put("temperature", 1)
//                .put("top_p", 0.95)
//                .put("top_k", 40)
//                .put("max_output_tokens", 8192);

        RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .post(body)
                .build();

//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseString = response.body().string();
//                System.out.println("Full response: " + responseString);
//
//                JSONObject responseJson = new JSONObject(responseString);
//                return responseJson.optString("text").trim();
//            } else {
//                System.out.println("Request failed: " + response.message());
//                return null;
//            }
//        }
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseString = response.body().string();
//                System.out.println("Full response: " + responseString);
                Log.d("ParkingDataProcessor", "Full response: " + responseString);

                // Parse the response using Gson
                JsonElement responseJsonElement = JsonParser.parseString(responseString);
                JsonObject responseJson = responseJsonElement.getAsJsonObject();
                return responseJson.has("text") ? responseJson.get("text").getAsString().trim() : null;
            } else {
                System.out.println("Request failed: " + response.message());
                return null;
            }
        }
    }
}
