package com.example.parkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ReserveSpotActivity extends AppCompatActivity {
    private TextView thankYouText;
    private Button freeSpotButton;
    private FirebaseFirestore db;
    private String spotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_spot);

        thankYouText = findViewById(R.id.thankYouText);
        freeSpotButton = findViewById(R.id.freeSpotButton);

        db = FirebaseFirestore.getInstance();

        // Get the spot ID from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            spotId = intent.getStringExtra("spotId");
        }

        if (spotId != null) {
            thankYouText.setText("Thanks for reserving " + spotId + "!\n" + "Don't forget to cancel it when you leave.");
        }

        // Free the spot when the button is clicked
        freeSpotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeSpot(spotId);
            }
        });
    }

    private void freeSpot(String spotId) {
        if (spotId != null) {
            db.collection("spots").document(spotId)
                    .update("isAvailable", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ReserveSpotActivity.this, "Spot freed successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close this activity and go back
                    })
                    .addOnFailureListener(e -> Toast.makeText(ReserveSpotActivity.this, "Failed to free spot.", Toast.LENGTH_SHORT).show());
        }
    }
}