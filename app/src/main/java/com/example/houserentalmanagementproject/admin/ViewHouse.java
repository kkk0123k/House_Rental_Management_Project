package com.example.houserentalmanagementproject.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.OwnerModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.SeeHouseAdapterAdmin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewHouse extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SeeHouseAdapterAdmin adapter;
    private ArrayList<HouseModel> houses;
    private OwnerModel owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_house);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        houses = new ArrayList<>();
        adapter = new SeeHouseAdapterAdmin(this, houses);
        recyclerView.setAdapter(adapter);

        // Get the OwnerModel instance
        owner = (OwnerModel) getIntent().getSerializableExtra("OWNER_DATA");

        loadHouses();
    }

    private void loadHouses() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("houses").child(owner.getOwnerId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                houses.clear();
                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
                    HouseModel house = houseSnapshot.getValue(HouseModel.class);
                    if (house != null) {
                        houses.add(house);
                        Log.d("ViewHouse", "Added house to list: " + house.getHouseId());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewHouse", "Error loading houses", databaseError.toException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHouses(); // Refresh the data when the activity resumes
    }
}

