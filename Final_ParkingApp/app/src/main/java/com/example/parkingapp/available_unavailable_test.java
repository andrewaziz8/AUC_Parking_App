package com.example.parkingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class available_unavailable_test extends AppCompatActivity {
    private LinearLayout SpotsLayout;
    private List<ParkingSpot> parkingSpotList = new ArrayList<>();
    private FirebaseFirestore db;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_two_test);

        SpotsLayout = findViewById(R.id.SpotsLayout);

        // Create a notification channel (for Android Oreo and above)
        createNotificationChannel();

        // Initialize Firestore Database
        db = FirebaseFirestore.getInstance();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                new HttpRequestTask().execute();

                // Schedule the task to run again after 5 seconds
                handler.postDelayed(this, 5000);
            }
        };
        // Start the repeating task
        handler.post(runnable);
        // Trigger the AsyncTask
//        new HttpRequestTask().execute();

        // Fetch and display data
        fetchAndDisplayParkingSpots();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending posts of the runnable to prevent memory leaks
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void fetchAndDisplayParkingSpots() {
        db.collection("spots")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            parkingSpotList.clear();

                            // Clear only the views that are dynamically added
                            SpotsLayout.removeViewsInLayout(1, SpotsLayout.getChildCount() - 1);
//                            unavailableSpotsLayout.removeViewsInLayout(1, unavailableSpotsLayout.getChildCount() - 1);

                            boolean hasAvailableSpots = false;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ParkingSpot spot = new ParkingSpot();
                                spot.setId(document.getString("id"));
                                spot.setAvailable(document.getBoolean("isAvailable"));
                                spot.setExpectedTimeToLeave(document.getString("expectedTimeToLeave"));
                                parkingSpotList.add(spot);

                                if (spot.isAvailable()) {
                                    hasAvailableSpots = true;
                                }
                            }

//                            // Sort parkingSpotList based on spot ID
//                            parkingSpotList.sort((spot1, spot2) -> {
//                                String id1 = spot1.getId();
//                                String id2 = spot2.getId();
//
//                                // Extract letter and number from each ID
//                                String letterPart1 = id1.replaceAll("\\d", ""); // Extract letters
//                                String letterPart2 = id2.replaceAll("\\d", "");
//                                int numberPart1 = Integer.parseInt(id1.replaceAll("\\D", "")); // Extract numbers
//                                int numberPart2 = Integer.parseInt(id2.replaceAll("\\D", ""));
//
//                                // Compare letters first, then numbers
//                                int letterComparison = letterPart1.compareTo(letterPart2);
//                                if (letterComparison != 0) {
//                                    return letterComparison;
//                                }
//                                return Integer.compare(numberPart1, numberPart2);
//                            });

                            // If no available spots, display Notify Me button
                            if (!hasAvailableSpots) {
                                Button notifyMeButton = new Button(available_unavailable_test.this);
                                notifyMeButton.setText("Notify Me");
                                notifyMeButton.setBackgroundResource(R.drawable.rounded_button_notify);
                                notifyMeButton.setGravity(Gravity.CENTER);
                                notifyMeButton.setPadding(16, 8, 16, 8);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.gravity = Gravity.CENTER_HORIZONTAL;
                                params.setMargins(0, 16, 0, 0);
                                notifyMeButton.setLayoutParams(params);

                                notifyMeButton.setOnClickListener(v -> {
                                    Toast.makeText(available_unavailable_test.this, "You'll be notified when a spot is available.", Toast.LENGTH_SHORT).show();

                                    // Start the foreground service
                                    Intent serviceIntent = new Intent(available_unavailable_test.this, ParkingMonitorService.class);
                                    startService(serviceIntent);
                                });
                                SpotsLayout.addView(notifyMeButton);
                            }

                            for (ParkingSpot spot : parkingSpotList) {
                                // Create a rounded button for available spots
                                Button spotButton = new Button(available_unavailable_test.this);
//                                    spotButton.setBackgroundColor(Color.GREEN);
                                spotButton.setText(spot.getId());
                                spotButton.setGravity(Gravity.CENTER);
                                spotButton.setPadding(16, 8, 16, 8);

                                // Set layout parameters to define the width
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        200, // Set the desired width in pixels
                                        LinearLayout.LayoutParams.WRAP_CONTENT); // Set height as wrap_content
                                params.gravity = Gravity.CENTER_HORIZONTAL; // Center the button horizontally
                                params.setMargins(0, 16, 0, 0); // Set margins if needed
                                spotButton.setLayoutParams(params);
                                if (spot.isAvailable()) {
                                    spotButton.setBackgroundResource(R.drawable.rounded_button_green);
                                    // Set click listener for reservation action
                                    spotButton.setOnClickListener(v -> {
                                        new AlertDialog.Builder(available_unavailable_test.this)
                                                .setTitle("Reservation Confirmation")
                                                .setMessage("Do you want to reserve this spot?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Get the current user's email
                                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                        String userEmail = user != null ? user.getEmail() : null;

                                                        // Mark the spot as reserved in Firestore
                                                        db.collection("spots").document(spot.getId())
                                                                .update("isAvailable", false, "userid", userEmail)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Navigate to the new activity
                                                                    Intent intent = new Intent(available_unavailable_test.this, ReserveSpotActivity.class);
                                                                    intent.putExtra("spotId", spot.getId());
                                                                    startActivity(intent);
                                                                    finish();
                                                                })
                                                                .addOnFailureListener(e -> Toast.makeText(available_unavailable_test.this, "Failed to reserve spot.", Toast.LENGTH_SHORT).show());
                                                    }
                                                })
                                                .setNegativeButton("No", null) // Do nothing if "No" is clicked
                                                .show();
                                    });
                                } else {
                                    spotButton.setBackgroundResource(R.drawable.rounded_button_red);
                                    spotButton.setTextColor(Color.WHITE); // Set the text color to white
                                    spotButton.setOnClickListener(v -> {
                                        // Show a popup dialog instead of a Toast
                                        new AlertDialog.Builder(available_unavailable_test.this)
                                                .setTitle("Spot Unavailable")
                                                .setMessage("This spot is currently unavailable.\nExpected availability: " + spot.getExpectedTimeToLeave())
                                                .setPositiveButton("OK", null) // Close dialog on "OK"
                                                .show();
                                    });
                                }
                                SpotsLayout.addView(spotButton);
                            }
                        } else {
                            Toast.makeText(available_unavailable_test.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "parking_notifications";
            String channelName = "Parking Spot Notifications";
            String channelDescription = "Notifies when a parking spot becomes available.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                // URL to the Python Flask API running locally
                URL url = new URL("http://192.168.129.94:8080");
                // Open a connection to the URL
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                // Set the HTTP method (GET in this case)
                httpURLConnection.setRequestMethod("GET");

                httpURLConnection.connect();

                // Set the request headers (optional)
//                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

                // Get the HTTP response code
                int responseCode = httpURLConnection.getResponseCode();
                Log.d("Response from API", String.valueOf(responseCode));

                // Read the response from the server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                // Close the streams
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                // Store the response
                response = stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Response from API", "Error: " + e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            // Process the response
            if (!response.isEmpty()) {
                Log.d("Response from API", response);
                handling_response(response);
                // Here you can update the UI or perform other actions with the response
            } else {
                Log.d("Response from API", "No response received");
            }
        }
    }

    private void handling_response(String response){
        try {
            // Replace single quotes with double quotes to make it valid JSON
            String validJson = response.replace("'", "\"");

            // Parse the JSON string into a JSONObject
            JSONObject jsonObject = new JSONObject(validJson);

            // Convert the JSONObject into a Java Map (dictionary)
            Map<String, String> javaDictionary = new HashMap<>();
            Iterator<String> keys = jsonObject.keys();

            // Initialize Firestore Database
            db = FirebaseFirestore.getInstance();

            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);

                Log.d("Response from API", value);

                // Mark the spot as reserved in Firestore
                db.collection("spots").document(key)
                        .update("expectedTimeToLeave", value);
                javaDictionary.put(key, value);
            }

            // Print the Java Map
//            System.out.println("Java Dictionary: " + javaDictionary);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
    }

}
