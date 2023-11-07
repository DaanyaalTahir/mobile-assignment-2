package com.example.locationpinned;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.locationpinned.databinding.FragmentSecondBinding;

import java.util.Objects;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Done button listener
        binding.saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check to see if user at least provides a latitude and longitude, if not display a toast warning
                if (TextUtils.isEmpty(binding.addLatInputField.getText()) || TextUtils.isEmpty(binding.addLonInputField.getText())) {
                    Toast.makeText(requireActivity().getApplicationContext(), "You must provide a latitude and longitude", Toast.LENGTH_SHORT).show();
                } else {
                    // Extract the location information
                    String lat = Objects.requireNonNull(binding.addLatInputField.getText()).toString();
                    String lon = Objects.requireNonNull(binding.addLonInputField.getText()).toString();

                    // Add location to the database
                    SQLiteManager db = new SQLiteManager(getContext());
                    db.addLocation(lat, lon);

                    // Return back to the home page
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);

                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}