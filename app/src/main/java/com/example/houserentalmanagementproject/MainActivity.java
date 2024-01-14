package com.example.houserentalmanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.houseOwner.RegisterOwner;
import com.example.houserentalmanagementproject.user.RegisterUser;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btn_houseOwner, btn_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_houseOwner = findViewById(R.id.btn_houseOwner);
        btn_user = findViewById(R.id.btn_user);

        btn_houseOwner.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterOwner.class)));

        btn_user.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterUser.class)));

    }
}