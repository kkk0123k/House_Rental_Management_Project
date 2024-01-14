package com.example.houserentalmanagementproject.houseOwner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.MainActivity;
import com.example.houserentalmanagementproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardOwner extends AppCompatActivity {

    TextView tv_name, tv_email;
    Button btn_addHouse, btn_seeHouse,btn_logOut, btn_viewContract, btn_viewChart, btn_updateInfo;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_owner);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn_addHouse = findViewById(R.id.btn_addHouse);
        btn_seeHouse = findViewById(R.id.btn_seeHouse);
        btn_viewContract = findViewById(R.id.view_contract);
        btn_viewChart = findViewById(R.id.btn_viewChart);
        btn_logOut = findViewById(R.id.logout);
        btn_updateInfo = findViewById(R.id.change_info);
        tv_name = findViewById(R.id.user_name);
        tv_email = findViewById(R.id.user_email);

        // Fetch and display the user's email
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tv_email.setText(getString(R.string.user_email, user.getEmail()));
        }

        // Fetch and display the user's name
        mDatabase.child("Owner").child(user.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                tv_name.setText(getString(R.string.user_name, username));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        btn_seeHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardOwner.this, SeeHouse.class));
            }
        });
        btn_addHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardOwner.this, AddHouse.class));
            }
        });
        btn_updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardOwner.this, UpdateProfile.class));
            }
        });
        btn_viewContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardOwner.this, OwnerContract.class));
            }
        });
        btn_viewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardOwner.this, RevenueRecordChart.class));
            }
        });
        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session data
                // This depends on how you're storing the session data
                // For example, if you're using SharedPreferences, you can do:
                SharedPreferences preferences = getSharedPreferences("YourAppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // Navigate back to MainActivity
                Intent intent = new Intent(DashboardOwner.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}



