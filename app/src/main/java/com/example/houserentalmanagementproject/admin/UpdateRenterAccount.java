package com.example.houserentalmanagementproject.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.UserModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateRenterAccount extends AppCompatActivity {

    private EditText etUsername;
    private EditText etUserEmail;
    private EditText etPhone;
    private UserModel user;
    private DatabaseReference mDatabase;

    public UpdateRenterAccount() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        etUsername = findViewById(R.id.et_username);
        etUserEmail = findViewById(R.id.et_user_email);
        etPhone = findViewById(R.id.et_phone);
        Button btnUpdate = findViewById(R.id.btn_update);

        // Get the UserModel instance
        user = (UserModel) getIntent().getSerializableExtra("USER_DATA");

        // Set the initial values
        etUsername.setText(user.getUsername());
        etUserEmail.setText(user.getUserEmail());
        etPhone.setText(String.valueOf(user.getPhone()));


        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        btnUpdate.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String userEmail = etUserEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!username.isEmpty() && !userEmail.isEmpty() && !phone.isEmpty()) {
                // Update the UserModel instance
                user.setUsername(username);
                user.setUserEmail(userEmail);
                user.setPhone(phone);

                // Update the user data in the database
                mDatabase.child(user.getUserId()).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateRenterAccount.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the UpdateActivity
                    } else {
                        Toast.makeText(UpdateRenterAccount.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UpdateRenterAccount.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

