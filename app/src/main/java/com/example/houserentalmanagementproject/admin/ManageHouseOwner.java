package com.example.houserentalmanagementproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.houserentalmanagementproject.R;

public class ManageHouseOwner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_houseowner);

        Button btnViewHouseOwner = findViewById(R.id.btn_viewHouseOwner);
        Button btnAddHouseOwner = findViewById(R.id.btn_addHouseOwner);

        btnViewHouseOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for btn_viewHouseOwner
                // For example, start a new activity
                Intent intent = new Intent(ManageHouseOwner.this, ViewHouseOwner.class);
                startActivity(intent);
            }
        });

        btnAddHouseOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for btn_addHouseOwner
                // For example, start a new activity
                Intent intent = new Intent(ManageHouseOwner.this, AddHouseOwner.class);
                startActivity(intent);
            }
        });
    }
}

