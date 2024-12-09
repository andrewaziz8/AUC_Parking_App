package com.example.parkingapp;

import java.util.Arrays;
import java.util.List;

public class Song {
    private String title;
    private String artist;
    private String url;

    public Song(String title, String artist, String url) {
        this.title = title;
        this.artist = artist;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }
}