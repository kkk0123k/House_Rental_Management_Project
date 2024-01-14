package com.example.houserentalmanagementproject.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.SeeHouseAdapterUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;

public class UserSearch extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private SearchView searchView;
    private SeekBar priceFilter;
    private TextView priceRange;
    private RecyclerView recyclerView;
    private ArrayList<HouseModel> houseArrayList;
    private SeeHouseAdapterUser adapter;

    private String lastQuery = "";
    private int lastPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        // Initialize the database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize the SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lastQuery = newText;
                searchAndFilterHouses(lastQuery, lastPrice);
                return false;
            }
        });

        // Initialize the SeekBar and TextView
        priceFilter = findViewById(R.id.price_filter);
        priceRange = findViewById(R.id.price_range);
        priceFilter.setMax(40); // Set the maximum to 40 positions
        priceFilter.setProgress(0); // Set the initial position to 0
        priceFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int price = progress * 500000; // Each position represents 500000
                priceRange.setText(getString(R.string.price_from_0_to,String.valueOf(price)));
                lastPrice = price;
                searchAndFilterHouses(lastQuery, lastPrice);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something when the user starts moving the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do something when the user stops moving the SeekBar
            }
        });

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the houseArrayList and adapter
        houseArrayList = new ArrayList<>();
        adapter = new SeeHouseAdapterUser(this, houseArrayList);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            startActivity(new Intent(UserSearch.this, DashboardUser.class));
                            overridePendingTransition(0,0);
                            break;
                        case R.id.person:
                            startActivity(new Intent(UserSearch.this, UserProfile.class));
                            overridePendingTransition(0,0);
                            break;
                        case R.id.search:
                            // You're already in UserSearchActivity
                            break;
                    }
                    return true;
                }
            };

    private void searchAndFilterHouses(String query, int price) {
        // Normalize the query string
        String normalizedQuery = normalizeString(query);

        mDatabase.child("houses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the previous search results
                houseArrayList.clear();

                for (DataSnapshot ownerSnapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot houseSnapshot: ownerSnapshot.getChildren()) {
                        HouseModel house = houseSnapshot.getValue(HouseModel.class);
                        if (house != null) {
                            // Normalize the houseLocation string
                            String normalizedHouseLocation = normalizeString(house.getHouseLocation());
                            int rentPerRoom = Integer.parseInt(house.getRentPerRoom());

                            // Only apply the price filter if the SeekBar is not at its default position
                            if (normalizedHouseLocation.contains(normalizedQuery) && (price == 0 || rentPerRoom < price)) {
                                houseArrayList.add(house);
                            }
                        }
                    }
                }
                // Update the RecyclerView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static String normalizeString(String text) {
        String normalized = text.toLowerCase();
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}\\p{M}]", "");
        return normalized;
    }

}




