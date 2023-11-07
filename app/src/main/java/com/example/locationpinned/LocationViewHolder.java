package com.example.locationpinned;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    TextView idView;
     TextView latitudeView;
     TextView longitudeView;
     TextView addressView;
     CardView cardView;
     ImageView deleteBtn;

    public LocationViewHolder(@NonNull View itemView){
        super(itemView);
        idView = itemView.findViewById(R.id.id);
        latitudeView = itemView.findViewById(R.id.latitude);
        longitudeView = itemView.findViewById(R.id.longitude);
        addressView = itemView.findViewById(R.id.address);
        cardView = itemView.findViewById(R.id.locationCard);
        deleteBtn = itemView.findViewById(R.id.deleteButton);
    }


}
