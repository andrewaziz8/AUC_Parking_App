package com.example.parkingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SongSuggestionActivity extends AppCompatActivity {

    private TextView songTitle, songArtist;
    private Button listenButton, closeButton;
    private String songUrl;
    private static final List<Song> songList = Arrays.asList(
            new Song("Don't Stop Believin'", "Journey", "https://open.spotify.com/track/1k8mUSDKILvs0Zg2VITxXJ"),
            new Song("Bohemian Rhapsody", "Queen", "https://open.spotify.com/track/7tFiyTwD0nx5a1eklYtX2J"),
            new Song("Blinding Lights", "The Weeknd", "https://open.spotify.com/track/0VjIjW4GlUZAMYd2vXMi3b")
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_suggestion_activity);

        // Initialize UI components
        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        listenButton = findViewById(R.id.listenButton);
        closeButton = findViewById(R.id.closeButton);

        // Pick a random song from the list
        Random random = new Random();
        Song randomSong = songList.get(random.nextInt(songList.size()));

        songArtist.setText("How about listening to " + randomSong.getTitle() + " by artist name " + randomSong.getArtist());
        songUrl = randomSong.getUrl();

        // Handle "Listen" button click
        listenButton.setOnClickListener(v -> {
            // Open the song's URL in Spotify or a browser
            Intent listenIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(songUrl));
            startActivity(listenIntent);
        });

        // Handle "Close" button click
        closeButton.setOnClickListener(v -> {
            // Navigate back to the available_unavailable screen
            Intent backIntent = new Intent(SongSuggestionActivity.this, available_unavailable_test.class);
            startActivity(backIntent);
            finish(); // Close this activity
        });
    }
}