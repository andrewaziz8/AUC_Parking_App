package com.example.parkingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParkingSpotAdapter extends RecyclerView.Adapter<ParkingSpotAdapter.ViewHolder> {
    private List<ParkingSpot> parkingSpotList;

    public ParkingSpotAdapter(List<ParkingSpot> parkingSpotList) {
        this.parkingSpotList = parkingSpotList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parking_spot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParkingSpot parkingSpot = parkingSpotList.get(position);
        holder.spotIdTextView.setText(parkingSpot.getId());
        holder.spotStatusTextView.setText(parkingSpot.isAvailable() ? "Available" : "Unavailable");

        if (parkingSpot.isAvailable()) {
            // Show reserve button for available spots
            holder.reserveButton.setVisibility(View.VISIBLE);
            holder.expectedTimeTextView.setVisibility(View.GONE);

            // Handle reserve button click
            holder.reserveButton.setOnClickListener(v -> {
                // Reserve the spot logic (e.g., call Firebase to update status)
                // You can add your reservation logic here
                Toast.makeText(v.getContext(), "Spot reserved!", Toast.LENGTH_SHORT).show(); //change logic
            });
        } else {
            // Show expected time for unavailable spots
            holder.reserveButton.setVisibility(View.GONE);
            holder.expectedTimeTextView.setVisibility(View.VISIBLE);
            holder.expectedTimeTextView.setText("Expected time to leave: " + parkingSpot.getExpectedTimeToLeave());
        }
    }

    @Override
    public int getItemCount() {
        return parkingSpotList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView spotIdTextView;
        TextView spotStatusTextView;
        Button reserveButton;  // New button for reservation
        TextView expectedTimeTextView;  // New text view for expected time

        public ViewHolder(View itemView) {
            super(itemView);
            spotIdTextView = itemView.findViewById(R.id.spotIdTextView);
            spotStatusTextView = itemView.findViewById(R.id.spotStatusTextView);
            reserveButton = itemView.findViewById(R.id.reserveButton);
            expectedTimeTextView = itemView.findViewById(R.id.expectedTimeTextView);
        }
    }
}