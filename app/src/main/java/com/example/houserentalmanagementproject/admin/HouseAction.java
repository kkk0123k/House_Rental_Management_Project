package com.example.houserentalmanagementproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.OwnerModel;
import com.example.houserentalmanagementproject.R;

public class HouseAction extends AppCompatActivity {

    private OwnerModel owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_action);

        // Get the OwnerModel instance
        owner = (OwnerModel) getIntent().getSerializableExtra("OWNER_DATA");

        Button btnViewHouse = findViewById(R.id.btn_view_house);
        Button btnAddHouse = findViewById(R.id.btn_add_house);

        btnViewHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseAction.this, ViewHouse.class);
                intent.putExtra("OWNER_DATA", owner); // Pass the OwnerModel instance
                startActivity(intent);
            }
        });

        btnAddHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseAction.this, AddHouse.class);
                intent.putExtra("OWNER_DATA", owner); // Pass the OwnerModel instance
                startActivity(intent);
            }
        });
    }
}

