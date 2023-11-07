package com.example.locationpinned;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.locationpinned.databinding.FragmentEditNoteBinding;

public class EditLocation extends Fragment {

    private FragmentEditNoteBinding binding;
    private LocationData location;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the location data
        int id = getArguments().getInt("id");
        String lat = getArguments().getString("lat");
        String lon = getArguments().getString("lon");
        String address = getArguments().getString("address");

        // Create a location object using it
        location = new LocationData(address, lat,lon);
        location.setId(id);


        // Fill the input fields with the location data
        binding.latInputField.setText(lat);
        binding.lonInputField.setText(lon);

        // Cancel button listener
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the item click and navigate to the new fragment here
                NavHostFragment.findNavController(EditLocation.this)
                        .navigate(R.id.action_editLocation_to_FirstFragment);
            }
        });

        // Save location button listener
        binding.saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteManager db = new SQLiteManager(getContext());
                location.setLatitude(binding.latInputField.getText().toString());
                location.setLongitude(binding.lonInputField.getText().toString());
                db.editLocation(location);
                NavHostFragment.findNavController(EditLocation.this)
                        .navigate(R.id.action_editLocation_to_FirstFragment);
            }
        });

    }


}