package com.example.parkingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ReserveSpotActivity extends AppCompatActivity {
    private TextView thankYouText;
    private Button freeSpotButton;
    private FirebaseFirestore db;
    private String spotId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_spot);

        thankYouText = findViewById(R.id.thankYouText);
        freeSpotButton = findViewById(R.id.freeSpotButton);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        // Get the spot ID from the Intent
        Intent intent = getIntent();
        if (intent.hasExtra("spotId")) { // Check if the Intent has the "spotId" extra
            spotId = intent.getStringExtra("spotId");
            if (spotId != null) {
                // Directly set the thankYouText if spotId is provided via Intent
                thankYouText.setText("Thanks for reserving " + spotId + "!\n" + "Don't forget to cancel it when you leave.");
            }
        }
        else{
            // Google sign-in successful, now check Firestore for the user email
            FirebaseUser user = mAuth.getCurrentUser();
            String userEmail = user.getEmail();

            // Query Firestore to find the user by email
            db.collection("spots")
                    .whereEqualTo("userid", userEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // If the email exists in Firestore, get the first document's ID
                                DocumentSnapshot document = task.getResult().getDocuments().get(0); // Get the first document
                                spotId = document.getId(); // Get the spotId (the document's ID)
                                thankYouText.setText("Thanks for reserving " + spotId + "!\n" + "Don't forget to cancel it when you leave.");
                            } else {
                                // Handle case when no matching document is found
                                Toast.makeText(ReserveSpotActivity.this, "No spot found for this user.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        // Free the spot when the button is clicked
        freeSpotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeSpot(spotId);
                navigateToAvailableUnavailable();
            }
        });
    }

    private void freeSpot(String spotId) {
        if (spotId != null) {
            db.collection("spots").document(spotId)
                    .update("isAvailable", true, "userid", "unknown")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ReserveSpotActivity.this, "Spot freed successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ReserveSpotActivity.this, "Failed to free spot.", Toast.LENGTH_SHORT).show());
        }
    }

    private void navigateToAvailableUnavailable() {
        Intent intent = new Intent(ReserveSpotActivity.this, SongSuggestionActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}