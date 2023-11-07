package com.example.locationpinned;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationpinned.databinding.FragmentFirstBinding;

import java.util.List;

public class FirstFragment extends Fragment {
    private List<LocationData> locations;
    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false); // Add button

        // Add button event listener
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        // Get database instance
        SQLiteManager db = new SQLiteManager(getContext());

        // Create recycler view and set the layout manager
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve the locations and setup the adapter
        locations = db.searchByAddress("");
        LocationAdapter adapter = new LocationAdapter(getContext(), locations, FirstFragment.this);
        recyclerView.setAdapter(adapter);

        // Create an event listener for when the user searches for locations
        binding.searchView.getEditText().setOnEditorActionListener(
                (v, actionId, event) -> {
                    // Extract the search query and update the location list
                    binding.searchBar.setText(binding.searchView.getText());
                    adapter.setlocations(db.searchByAddress(binding.searchView.getText().toString()));
                    binding.searchView.hide();
                    return false;
                });


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}