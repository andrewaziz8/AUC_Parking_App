package com.example.parkingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private TextView signup;
    private Button start;
    private Button google;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean isPasswordVisible = false; // Track password visibility


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear_sign_in);

        FirebaseApp.initializeApp(this);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("546793747606-eqb2urf6h5u2dq18c9e4rilhprnu85oq")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // *** IMPORTANT: Assign to the global instance!
        AppUtils.mGoogleSignInClient = mGoogleSignInClient;

//        // Check if user is already signed in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // User is already signed in, proceed to the map activity
//            start_available_unavailable_Activity();
//        }

        signup = (TextView)findViewById(R.id.SIGNUP);
        start = (Button)findViewById(R.id.sign);
        google = (Button)findViewById(R.id.google);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, sign_up.class);
                startActivity(i);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em = email.getText().toString().trim();
                String pas = password.getText().toString().trim();

                if (em.isEmpty()) {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }

                if (pas.isEmpty()) {   //More constraints on password
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }

                signIn(em, pas);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    signOut();
                }
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        // Call the API in the background when the activity starts
//        new TestGeminiAPITask().execute("Hi");

//        // AsyncTask to process parking data and call API
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... voids) {
//                try {
//                    // Call the loadParkingData method (pass the appropriate file path)
//                    List<String[]> data = ParkingDataProcessor.loadParkingData("path_to_your_csv_file");
//
//                    // Call the prepareMessageFromData method to generate the message
//                    String message = ParkingDataProcessor.prepareMessageFromData(data);
//
//                    // Call the getNextAvailableTime method to get the next available time
//                    return ParkingDataProcessor.getNextAvailableTime(message);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String nextAvailableTime) {
//                // Run on UI thread to update UI with the result
//                if (nextAvailableTime != null && !nextAvailableTime.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Next available time: " + nextAvailableTime, Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Could not determine the next available time.", Toast.LENGTH_LONG).show();
//                }
//            }
//        }.execute();  // Execute the AsyncTask


        // Set the onTouchListener for password visibility toggle
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility();
                        password.performClick(); // Call performClick() to handle click event
                        return true;
                    }
                }
                return false;
            }
        });
    }

//    // AsyncTask for calling the API in the background
//    private class TestGeminiAPITask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String message = params[0]; // "Hi"
//            try {
//                // Create the request payload
//                JsonObject payload = new JsonObject();
//                payload.addProperty("prompt", message);
//                payload.addProperty("temperature", 1);
//                payload.addProperty("top_p", 0.95);
//                payload.addProperty("top_k", 40);
//                payload.addProperty("max_output_tokens", 100); // Limit response length
//
//                // Create OkHttpClient and request body
//                OkHttpClient client = new OkHttpClient();
//                RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
//
//                // Build the request
//                Request request = new Request.Builder()
//                        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=AIzaSyCL6_2CegP8QXrjdLipxUiiqINMzySFhAc")
//                        .post(body)
//                        .build();
//
//                // Execute the request
//                try (Response response = client.newCall(request).execute()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        String responseString = response.body().string();
//                        Log.d("TestGeminiAPI", "API Response: " + responseString);
//
//                        // Parse the response using Gson
//                        JsonElement responseJsonElement = JsonParser.parseString(responseString);
//                        JsonObject responseJson = responseJsonElement.getAsJsonObject();
//                        return responseJson.has("text") ? responseJson.get("text").getAsString() : "No response text found";
//                    } else {
//                        Log.d("TestGeminiAPI", "Request failed: " + response.message());
//                        return "Request failed";
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Error occurred during API call";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // This will run on the main thread after the background task completes
//            Log.d("TestGeminiAPI", "Generated Response: " + result);
//        }
//    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide Password
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
        } else {
            // Show Password
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        // Move the cursor to the end of the text
        password.setSelection(password.getText().length());
    }

    private void signIn(String em, String pas) {
        mAuth.signInWithEmailAndPassword(em, pas)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User is signed in successfully, now check Firestore for the user email
                            checkUserInFirestore(em);

                            // Sign in success, update UI with the signed-in user's information
//                            Intent i = new Intent(MainActivity.this, available_unavailable.class);
//                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Invalid Email or Password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //String email = account.getEmail(); // Get the email address!
                //firebaseAuthWithGoogle(account.getIdToken(), email); // Pass email
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("MainActivity", "Google sign in failed", e);
                String errorMessage = e.getMessage(); // Get the specific error message from ApiException
                Toast.makeText(this, "Google sign up failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Google sign-in successful, now check Firestore for the user email
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userEmail = user.getEmail();
                                checkUserInFirestore(userEmail);
                            }
//                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(MainActivity.this, "Google sign in successful", Toast.LENGTH_SHORT).show();
//                            start_available_unavailable_Activity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    // User is now signed out from Google account
                });
        mAuth.signOut();
    }

    private void start_available_unavailable_Activity() {
        Intent intent = new Intent(MainActivity.this, available_unavailable_test.class);
        startActivity(intent);
        finish(); // Optional: Finish MainActivity to prevent going back
        // to the login screen with the back button
    }

    // Method to check if the user exists in Firestore
    private void checkUserInFirestore(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore to find the user by email
        db.collection("spots")
                .whereEqualTo("userid", email) // "userId" is the field where the user's email is stored
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // If the email exists in Firestore, redirect to ReserveSpotActivity
                            Intent intent = new Intent(MainActivity.this, ReserveSpotActivity.class);
                            startActivity(intent);
                            finish(); //Finish MainActivity to prevent going back
                        } else {
                            // If email doesn't exist, proceed with the regular flow
                            Intent intent = new Intent(MainActivity.this, available_unavailable_test.class);
                            startActivity(intent);
                            finish(); //Finish MainActivity to prevent going back
                        }
                    }
                });
    }
}