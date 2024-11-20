package com.example.parkingapp;

public class ParkingSpot {
    private String id;
    private boolean isAvailable;
    private String expectedTimeToLeave;

    public ParkingSpot() {
        // Default constructor required for calls to DataSnapshot.getValue(ParkingSpot.class)
    }

    public ParkingSpot(String id, boolean isAvailable) {
        this.id = id;
        this.isAvailable = isAvailable;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getExpectedTimeToLeave() { return expectedTimeToLeave; }
    public void setExpectedTimeToLeave(String expectedTimeToLeave) { this.expectedTimeToLeave = expectedTimeToLeave; }
}
