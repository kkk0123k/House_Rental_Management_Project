package com.example.houserentalmanagementproject.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ManageAccounts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);

        Button btnManageUser = findViewById(R.id.btn_manageUser);
        Button btnManageHouseOwner = findViewById(R.id.btn_manageHouseOwner);

        btnManageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for btn_manageUser
                // For example, start a new activity
                Intent intent = new Intent(ManageAccounts.this, ManageRenter.class);
                startActivity(intent);
            }
        });

        btnManageHouseOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for btn_manageHouseOwner
                // For example, start a new activity
                Intent intent = new Intent(ManageAccounts.this, ManageHouseOwner.class);
                startActivity(intent);
            }
        });
    }
}


