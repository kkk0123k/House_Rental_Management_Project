package com.example.houserentalmanagementproject.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.SeeHouseAdapterUser;
import com.example.houserentalmanagementproject.houseOwner.RegisterOwner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUser extends AppCompatActivity {

    private RecyclerView rv_showAllFood;
    private SeeHouseAdapterUser adapter;
    private final ArrayList<HouseModel> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);

        rv_showAllFood = findViewById(R.id.recyclerView);
        rv_showAllFood.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DashboardUser.this);
        rv_showAllFood.setLayoutManager(linearLayoutManager);
        getAllArticle();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);
    }

    private void getAllArticle() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        if (firebaseUser.getUid() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(RegisterOwner.HOUSES);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            HouseModel article = dataSnapshot1.getValue(HouseModel.class);
                            mList.add(article);
                        }
                    }
                    Log.d("TAG1", "onDataChange: " + mList.get(0));
                    adapter = new SeeHouseAdapterUser(DashboardUser.this, mList);
                    rv_showAllFood.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    switch (item.getItemId()) {
                        case R.id.home:
                            // You're already in DashboardUser activity
                            break;
                        case R.id.person:
                            intent = new Intent(DashboardUser.this, UserProfile.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            break;
                        case R.id.search:
                            intent = new Intent(DashboardUser.this, UserSearch.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            break;
                    }

                    return true;
                }
            };

}