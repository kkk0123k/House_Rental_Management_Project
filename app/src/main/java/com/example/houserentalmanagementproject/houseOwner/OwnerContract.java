package com.example.houserentalmanagementproject.houseOwner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.houserentalmanagementproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OwnerContract extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<String> houseNames;
    private ArrayAdapter<String> adapter;
    private OwnerContractPagerAdapter ownerContractPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_contract);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        houseNames = new ArrayList<>();
        houseNames.add("Select a house"); // Add the default value
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, houseNames);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        Spinner spinner = findViewById(R.id.my_spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // Set the default selection

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        ownerContractPagerAdapter = new OwnerContractPagerAdapter(this);

        viewPager.setAdapter(ownerContractPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Active Contracts");
                    } else {
                        tab.setText("Terminated Contracts");
                    }
                }
        ).attach();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected house name
                String selectedHouseName = (String) parent.getItemAtPosition(position);
                Log.d("OwnerContract", "Selected House Name: " + selectedHouseName);

                // Update the selected house name in the OwnerContractPagerAdapter
                ownerContractPagerAdapter.updateSelectedHouseName(selectedHouseName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        populateSpinner();
    }

    private void populateSpinner() {
        String ownerId = mAuth.getCurrentUser().getUid();
        Log.d("OwnerContract", "Owner ID: " + ownerId); // Log the owner ID

        mDatabase.child("houses").child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String houseId = snapshot.getKey();
                    Log.d("OwnerContract", "House ID: " + houseId); // Log the house ID

                    String houseName = snapshot.child("search").getValue(String.class);
                    Log.d("OwnerContract", "House Name: " + houseName); // Log the house name

                    if (houseName != null) {
                        houseNames.add(houseName);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("OwnerContract", "Error fetching house IDs", databaseError.toException()); // Log the error
            }
        });
    }

    public String getSelectedHouseName() {
        Spinner spinner = findViewById(R.id.my_spinner);
        return spinner.getSelectedItem().toString();
    }
}






