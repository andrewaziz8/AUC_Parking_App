package com.example.parkingapp;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class available_unavailable extends AppCompatActivity {
    private LinearLayout availableSpotsLayout, unavailableSpotsLayout;
    private List<ParkingSpot> parkingSpotList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_two);

        availableSpotsLayout = findViewById(R.id.availableSpotsLayout);
        unavailableSpotsLayout = findViewById(R.id.unavailableSpotsLayout);

        // Initialize Firestore Database
        db = FirebaseFirestore.getInstance();

        // Fetch and display data
        fetchAndDisplayParkingSpots();
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
                            availableSpotsLayout.removeViewsInLayout(1, availableSpotsLayout.getChildCount() - 1);
                            unavailableSpotsLayout.removeViewsInLayout(1, unavailableSpotsLayout.getChildCount() - 1);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ParkingSpot spot = new ParkingSpot();
                                spot.setId(document.getString("id"));
                                spot.setAvailable(document.getBoolean("isAvailable"));
                                spot.setExpectedTimeToLeave(document.getString("expectedTimeToLeave"));
                                parkingSpotList.add(spot);
                            }

                            // Sort parkingSpotList based on spot ID
                            parkingSpotList.sort((spot1, spot2) -> {
                                String id1 = spot1.getId();
                                String id2 = spot2.getId();

                                // Extract letter and number from each ID
                                String letterPart1 = id1.replaceAll("\\d", ""); // Extract letters
                                String letterPart2 = id2.replaceAll("\\d", "");
                                int numberPart1 = Integer.parseInt(id1.replaceAll("\\D", "")); // Extract numbers
                                int numberPart2 = Integer.parseInt(id2.replaceAll("\\D", ""));

                                // Compare letters first, then numbers
                                int letterComparison = letterPart1.compareTo(letterPart2);
                                if (letterComparison != 0) {
                                    return letterComparison;
                                }
                                return Integer.compare(numberPart1, numberPart2);
                            });

                            for (ParkingSpot spot : parkingSpotList) {
                                if (spot.isAvailable()) {
                                    // Create a rounded button for available spots
                                    Button spotButton = new Button(available_unavailable.this);
                                    spotButton.setText(spot.getId());
                                    spotButton.setBackgroundResource(R.drawable.rounded_button);
                                    spotButton.setGravity(Gravity.CENTER);
                                    spotButton.setPadding(16, 8, 16, 8);

                                    // Set layout parameters to define the width
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            200, // Set the desired width in pixels
                                            LinearLayout.LayoutParams.WRAP_CONTENT); // Set height as wrap_content
                                    params.gravity = Gravity.CENTER_HORIZONTAL; // Center the button horizontally
                                    params.setMargins(0, 16, 0, 0); // Set margins if needed
                                    spotButton.setLayoutParams(params);

                                    // Set click listener for reservation action
                                    spotButton.setOnClickListener(v -> {
                                        new AlertDialog.Builder(available_unavailable.this)
                                                .setTitle("Reservation Confirmation")
                                                .setMessage("Do you want to reserve this spot?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Mark the spot as reserved in Firestore
                                                        db.collection("spots").document(spot.getId())
                                                                .update("isAvailable", false)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Navigate to the new activity
                                                                    Intent intent = new Intent(available_unavailable.this, ReserveSpotActivity.class);
                                                                    intent.putExtra("spotId", spot.getId());
                                                                    startActivity(intent);
                                                                })
                                                                .addOnFailureListener(e -> Toast.makeText(available_unavailable.this, "Failed to reserve spot.", Toast.LENGTH_SHORT).show());
                                                    }
                                                })
                                                .setNegativeButton("No", null) // Do nothing if "No" is clicked
                                                .show();
                                    });
                                    availableSpotsLayout.addView(spotButton);
                                } else {
                                    // Create a TextView for unavailable spots with expected time
                                    TextView spotTextView = new TextView(available_unavailable.this);
                                    spotTextView.setText(spot.getId() + " - Expected time to be available: " + spot.getExpectedTimeToLeave());
                                    spotTextView.setPadding(8, 8, 8, 8);
                                    spotTextView.setGravity(Gravity.START);
                                    unavailableSpotsLayout.addView(spotTextView);
                                }
                            }
                        } else {
                            Toast.makeText(available_unavailable.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
