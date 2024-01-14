package com.example.houserentalmanagementproject.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.OwnerModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.SeeHouseOwnerAdapterAdmin;
import com.example.houserentalmanagementproject.adapter.SeeMemberAdapterAdmin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewHouseOwner extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SeeHouseOwnerAdapterAdmin adapter;
    private List<OwnerModel> owners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_owner_account);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        owners = new ArrayList<>();
        adapter = new SeeHouseOwnerAdapterAdmin(this, owners); // Pass the context to the adapter
        recyclerView.setAdapter(adapter);

        loadOwners();
    }

    private void loadOwners() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Owner");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                owners.clear();
                for (DataSnapshot ownerSnapshot : dataSnapshot.getChildren()) {
                    OwnerModel owner = ownerSnapshot.getValue(OwnerModel.class);
                    if (owner != null) {
                        owners.add(owner);
                        Log.d("ViewHouseOwner", "Added owner to list: " + owner.getOwnerId());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewHouseOwner", "Error loading owners", databaseError.toException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOwners(); // Refresh the data when the activity resumes
    }
}
