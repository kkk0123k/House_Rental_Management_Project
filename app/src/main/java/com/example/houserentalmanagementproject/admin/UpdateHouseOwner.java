package com.example.houserentalmanagementproject.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.OwnerModel;
import com.example.houserentalmanagementproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateHouseOwner extends AppCompatActivity {

    private EditText etUsername, etUserEmail, etPhone;
    private OwnerModel owner;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_owner);

        etUsername = findViewById(R.id.et_username);
        etUserEmail = findViewById(R.id.et_user_email);
        etPhone = findViewById(R.id.et_phone);
        Button btnUpdate = findViewById(R.id.btn_update);

        // Get the OwnerModel instance
        owner = (OwnerModel) getIntent().getSerializableExtra("OWNER_DATA");

        // Set the initial values
        etUsername.setText(owner.getUsername());
        etUserEmail.setText(owner.getUserEmail());
        etPhone.setText(owner.getPhoneNumber());

        mDatabase = FirebaseDatabase.getInstance().getReference("Owner");

        btnUpdate.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String userEmail = etUserEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!username.isEmpty() && !userEmail.isEmpty() && !phone.isEmpty()) {
                // Update the OwnerModel instance
                owner.setUsername(username);
                owner.setUserEmail(userEmail);
                owner.setPhoneNumber(phone);

                // Update the owner data in the database
                mDatabase.child(owner.getOwnerId()).setValue(owner).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateHouseOwner.this, "Owner updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the UpdateHouseOwner
                    } else {
                        Toast.makeText(UpdateHouseOwner.this, "Failed to update owner", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UpdateHouseOwner.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

