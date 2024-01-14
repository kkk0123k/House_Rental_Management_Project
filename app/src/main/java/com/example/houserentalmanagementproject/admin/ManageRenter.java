package com.example.houserentalmanagementproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.houserentalmanagementproject.R;

public class ManageRenter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        Button btnViewUser = findViewById(R.id.btn_viewUser);
        Button btnAddUser = findViewById(R.id.btn_addUser);

        btnViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for btn_viewUser
                // For example, start a new activity
                Intent intent = new Intent(ManageRenter.this, ViewUser.class);
                startActivity(intent);
            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for btn_addUser
                // For example, start a new activity
                Intent intent = new Intent(ManageRenter.this, AddUser.class);
                startActivity(intent);
            }
        });
    }
}

