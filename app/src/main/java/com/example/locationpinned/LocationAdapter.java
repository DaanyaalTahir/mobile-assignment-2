package com.example.locationpinned;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    Context context;
    List<LocationData> locations;
    Fragment fragment;

    /**
     * Constructor
     * @param context
     * @param locations
     */
    public LocationAdapter(Context context, List<LocationData> locations, Fragment fragment) {
        this.context = context;
        this.locations = locations;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationViewHolder(LayoutInflater.from(context).inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.idView.setText("ID: "+locations.get(position).getId());
        holder.latitudeView.setText("Latitude: "+locations.get(position).getLatitude());
        holder.longitudeView.setText("Longitude: "+locations.get(position).getLongitude());
        holder.addressView.setText("Address: "+locations.get(position).getAddress());

        String color = "#2196F3";

        holder.cardView.setCardBackgroundColor(Color.parseColor(color));

        // Location card click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", locations.get(position).getId());
                bundle.putString("lat", locations.get(position).getLatitude());
                bundle.putString("lon", locations.get(position).getLongitude());
                bundle.putString("address", locations.get(position).getAddress());
                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_FirstFragment_to_editLocation, bundle);
            }
        });

        // Delete button listener
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = locations.get(position).getId();
                SQLiteManager db = new SQLiteManager(context);
                db.deleteLocation(id);

                setlocations(db.searchByAddress(""));
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void setlocations(List<LocationData> updatedlocations) {
        locations.clear();
        locations.addAll(updatedlocations);
        notifyDataSetChanged();
    }
}
