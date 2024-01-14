package com.example.houserentalmanagementproject.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.ChangePassword;
import com.example.houserentalmanagementproject.EmptyActivity;
import com.example.houserentalmanagementproject.MainActivity;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.user.UpdateProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        TextView tv_userId = findViewById(R.id.userID);
        TextView tv_username = findViewById(R.id.user_name);
        TextView tv_userEmail = findViewById(R.id.user_email);
        Button btn_logout = findViewById(R.id.logout);

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
        Button btn_viewContract = findViewById(R.id.view_contract);
        btn_viewContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference contractRef = FirebaseDatabase.getInstance().getReference().child("memberContract");
                contractRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean userExists = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(userId)) {
                                userExists = true;
                                break;
                            }
                        }
                        if (userExists) {
                            // If the userId exists in the list, navigate to HouseRental
                            startActivity(new Intent(UserProfile.this, MemberContract.class));
                        } else {
                            // If the userId does not exist in the list, navigate to EmptyActivity
                            startActivity(new Intent(UserProfile.this, EmptyActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
            }
        });

        Button btn_viewRental = findViewById(R.id.view_rental);
        btn_viewRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rentalRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("houseId");
                rentalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // If the houseId exists, navigate to MemberContract
                            startActivity(new Intent(UserProfile.this, HouseRental.class));
                        } else {
                            // If the houseId does not exist, navigate to EmptyActivity
                            startActivity(new Intent(UserProfile.this, EmptyActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
            }
        });

        Button btn_changePassword = findViewById(R.id.change_password);
        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this, ChangePassword.class));
            }
        });
        Button btn_changeInformation = findViewById(R.id.change_info);
        btn_changeInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this, UpdateProfile.class));
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);
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
                Intent intent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            startActivity(new Intent(UserProfile.this, DashboardUser.class));
                            overridePendingTransition(0,0);
                            break;
                        case R.id.person:
                            // You're already in UserProfileActivity
                            break;
                        case R.id.search:
                            startActivity(new Intent(UserProfile.this, UserSearch.class));
                            overridePendingTransition(0,0);
                            break;
                    }
                    return true;
                }
            };
}