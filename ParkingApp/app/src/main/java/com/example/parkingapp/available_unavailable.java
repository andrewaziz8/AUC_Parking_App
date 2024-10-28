package com.example.parkingapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class available_unavailable extends AppCompatActivity {
    private RecyclerView parkingSpotsRecyclerView;
    private ParkingSpotAdapter parkingSpotAdapter;
    private List<ParkingSpot> parkingSpotList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // Initialize RecyclerView and Adapter
        parkingSpotsRecyclerView = findViewById(R.id.parkingSpotsRecyclerView);
        parkingSpotAdapter = new ParkingSpotAdapter(parkingSpotList);
        parkingSpotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        parkingSpotsRecyclerView.setAdapter(parkingSpotAdapter);

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

                            // Notify the adapter that the data has changed
                            parkingSpotAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(available_unavailable.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}