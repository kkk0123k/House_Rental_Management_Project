package com.example.houserentalmanagementproject.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class AdminDashboard extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        TextView tv_userId = findViewById(R.id.userID);
        TextView tv_username = findViewById(R.id.user_name);
        TextView tv_userEmail = findViewById(R.id.user_email);
        Button btn_logout = findViewById(R.id.btn_logout);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        String userEmail = firebaseUser.getEmail();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);

                tv_username.setText(getString(R.string.user_name, username));
                tv_userEmail.setText(getString(R.string.user_email, userEmail));
                tv_userId.setText(getString(R.string.user_id, userId));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(AdminDashboard.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btn_updateInfo = findViewById(R.id.update_Info);
        btn_updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, UpdateProfile.class));
            }
        });

        Button btn_manageAccounts = findViewById(R.id.btn_manageAccounts);
        btn_manageAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, ManageAccounts.class));
            }
        });

        Button btn_manageHouses = findViewById(R.id.btn_manageHouses);
        btn_manageHouses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, ManageHouses.class));
            }
        });

        Button btn_viewStatistics = findViewById(R.id.btn_viewStatistics);
        btn_viewStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, ViewStatistics.class));
            }
        });
    }
}
