package com.example.houserentalmanagementproject.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.houseOwner.RegisterOwner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HouseRental extends AppCompatActivity {
    TextView rentRoom;
    // Declare DatabaseReference
    private DatabaseReference mDatabase;

    // Declare variables to store memberId, houseId, ownerId and ownerPhone
    private String memberId, houseId, ownerId, ownerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_house_rental);

        // Initialize Firebase Auth
        // Declare Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Initialize DatabaseReference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get memberId from Firebase Auth
        memberId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Log.d("HouseRental", "memberId: " + memberId);

        // Get houseId from Firebase Database
        mDatabase.child("Users").child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                houseId = dataSnapshot.child("houseId").getValue(String.class);
                Log.d("HouseRental", "houseId: " + houseId);

                // Get ownerId from Firebase Database
                mDatabase.child("members").child(houseId).child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ownerId = dataSnapshot.child("ownerId").getValue(String.class);
                        Log.d("HouseRental", "ownerId: " + ownerId);

                        // Get ownerPhone from Firebase Database
                        mDatabase.child("Owner").child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ownerPhone = dataSnapshot.child("phone").getValue(String.class);
                                Log.d("HouseRental", "ownerPhone: " + ownerPhone);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle possible errors.
                                Log.e("HouseRental", "Database error", databaseError.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                        Log.e("HouseRental", "Database error", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.e("HouseRental", "Database error", databaseError.toException());
            }
        });

        Button btn_call = findViewById(R.id.btn_call);
        Button btn_message = findViewById(R.id.btn_message);
        Button btn_viewBill = findViewById(R.id.btn_viewBill);
        Button btn_requestMaintenance = findViewById(R.id.btn_requestMaintenance);
        Button btn_leaveHouse = findViewById(R.id.btn_leaveHouse);

        // Add functionality to your buttons here
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + ownerPhone));
                startActivity(intent);
            }
        });

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:" + ownerPhone));
                startActivity(smsIntent);
                }
        });


        btn_viewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ViewBill activity
                Intent intent = new Intent(HouseRental.this, ViewBill.class);
                startActivity(intent);
            }
        });

        btn_requestMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the request maintenance action
            }
        });

        btn_leaveHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HouseRental.this);
                builder.setTitle("Leave the house?");
                builder.setMessage("Do you want to leave this house? This will also terminate the contract with this member. Do you still wish to continue?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Get the contractId
                                DatabaseReference contractRef = FirebaseDatabase.getInstance().getReference().child("memberContract").child(memberId);
                                contractRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String contractId = snapshot.getKey();
                                            // Set the status to terminated in ownerContracts
                                            DatabaseReference ownerContractRef = FirebaseDatabase.getInstance().getReference().child("ownerContracts").child(houseId).child(contractId).child("status");
                                            ownerContractRef.setValue("terminated");
                                            // Set the status to terminated in memberContract
                                            DatabaseReference memberContractRef = FirebaseDatabase.getInstance().getReference().child("memberContract").child(memberId).child(contractId).child("status");
                                            memberContractRef.setValue("terminated");

                                            // After setting the status to terminated, delete the member
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(RegisterOwner.MEMBERS).child(houseId).child(memberId);
                                            ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // After successfully deleting the member, proceed with deleting the houseId
                                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(memberId).child("houseId");
                                                        userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(HouseRental.this, "Member removed success", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle possible errors.
                                        Log.e("HouseRental", "Database error", databaseError.toException());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            }
        });

    }
}
