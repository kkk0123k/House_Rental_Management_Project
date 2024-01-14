package com.example.houserentalmanagementproject.houseOwner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.houserentalmanagementproject.FullScreenImage;
import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HouseDetails extends AppCompatActivity {
    private String noOfRoom;
    private String rentPerRoom;
    private String houseDescription;
    private String houseLocation;
    private String search;
    private String houseImage;
    TextView tv_houseDesc,tv_search;
    PhotoView photoView;
    Button btn_viewMember,btn_addMember, btn_updateHouse, btn_viewPayment;
    private HouseModel house; // This is your HouseModel instance
    private ImageView iv_houseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details);
        Context context = this;

        Intent intent = getIntent();
        String houseId = intent.getStringExtra("houseId");
        noOfRoom = intent.getStringExtra("noOfRoom");
        rentPerRoom = intent.getStringExtra("rentPerRoom");
        houseDescription = intent.getStringExtra("houseDescription");
        houseLocation = intent.getStringExtra("houseLocation");
        houseImage = intent.getStringExtra("houseImage");
        search = intent.getStringExtra("search");
        String userId = intent.getStringExtra("userId");

        house = new HouseModel(houseId, noOfRoom, rentPerRoom, houseDescription, houseLocation, houseImage, userId, search);

        tv_search = findViewById(R.id.tv_search);
        photoView = findViewById(R.id.iv_houseImage);
        btn_viewMember = findViewById(R.id.btn_viewMember);
        btn_addMember = findViewById(R.id.btn_addMember);
        btn_updateHouse = findViewById(R.id.btn_updateHouse);
        btn_viewPayment = findViewById(R.id.btn_viewPayment);

        tv_search.setText(search);
        // Get reference to your ImageView

        // Load the image into the ImageView using Glide
        Glide.with(this).load(house.getHouseImage()).into(photoView);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(RegisterOwner.HOUSES).child(house.getUserId()).child(house.getHouseId());

        // Add ValueEventListener
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the updated HouseModel object
                HouseModel updatedHouse = dataSnapshot.getValue(HouseModel.class);

                // Now update the variables
                if (updatedHouse != null) {
                    house = updatedHouse; // Update the house instance
                    Glide.with(context).load(house.getHouseImage()).into(photoView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        btn_viewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HouseDetails.this, ViewMembers.class);
                intent1.putExtra("houseId", houseId);
                intent1.putExtra("noOfRoom", noOfRoom);
                intent1.putExtra("rentPerRoom", rentPerRoom);
                intent1.putExtra("houseDescription", houseDescription);
                intent1.putExtra("houseLocation", houseLocation);
                intent1.putExtra("houseImage", houseImage);
                intent1.putExtra("userId", userId);
                intent1.putExtra("search",search);
                startActivity(intent1);
            }
        });
        btn_addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HouseDetails.this, AddMember.class);
                intent1.putExtra("houseId", houseId);
                intent1.putExtra("noOfRoom", noOfRoom);
                intent1.putExtra("rentPerRoom", rentPerRoom);
                intent1.putExtra("houseDescription", houseDescription);
                intent1.putExtra("houseLocation", houseLocation);
                intent1.putExtra("houseImage", houseImage);
                intent1.putExtra("userId", userId);
                startActivity(intent1);
            }
        });
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.houserentalmanagementproject.houseOwner.HouseDetails.this, FullScreenImage.class);
                intent.putExtra("image_url", house.getHouseImage()); // Get the updated image URL from the house object
                startActivity(intent);
            }
        });
        btn_updateHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to UpdateHouseActivity
                Intent intent = new Intent(HouseDetails.this, UpdateHouse.class);
                intent.putExtra("house", house); // house is your HouseModel instance
                startActivity(intent);
            }
        });
        btn_viewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseDetails.this, ViewBill.class);
                intent.putExtra("userId", userId);
                intent.putExtra("houseId", houseId);
                startActivity(intent);
            }
        });

    }
}
