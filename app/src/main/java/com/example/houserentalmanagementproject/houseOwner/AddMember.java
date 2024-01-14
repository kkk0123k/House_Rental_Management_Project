package com.example.houserentalmanagementproject.houseOwner;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.Contract;
import com.example.houserentalmanagementproject.MemberModel;
import com.example.houserentalmanagementproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddMember extends AppCompatActivity {

    EditText et_memberID, et_memberName, et_roomNumber, et_memberAge, et_memberRent, et_memberPhoneNumber;
    Button btn_addMember, btn_memberJoiningDate;
    private ProgressDialog progressDialog;
    private String ownerName; // Declare username at class level
    private String search; // Declare search at class level

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Intent intent = getIntent();
        String houseId = intent.getStringExtra("houseId");
        String ownerId = intent.getStringExtra("userId");
        Log.d("AddMember",ownerId);

        et_memberID = findViewById(R.id.et_memberID);
        et_memberAge = findViewById(R.id.et_memberAge);
        et_memberName = findViewById(R.id.et_memberName);
        et_roomNumber = findViewById(R.id.et_roomNumber);
        et_memberRent = findViewById(R.id.et_memberRent);
        btn_memberJoiningDate = findViewById(R.id.btn_memberJoiningDate);
        et_memberPhoneNumber = findViewById(R.id.et_memberPhoneNumber);
        btn_addMember = findViewById(R.id.btn_addMember);

        progressDialog = new ProgressDialog(AddMember.this);
        btn_memberJoiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMember.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btn_memberJoiningDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btn_addMember.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Adding New Member");
                progressDialog.setTitle("Adding...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String memberID = et_memberID.getText().toString();
                String age = et_memberAge.getText().toString();
                String name = et_memberName.getText().toString();
                String roomNumber = et_roomNumber.getText().toString();
                String rent = et_memberRent.getText().toString();
                String joiningDate = btn_memberJoiningDate.getText().toString();
                String phoneNumber = et_memberPhoneNumber.getText().toString();

                createMember(memberID, age, name, roomNumber, rent, joiningDate, phoneNumber, ownerId, houseId);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMember(String memberID, String age, String name, String roomNumber, String rent, String joiningDate, String phoneNumber, String ownerId, String houseId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference().child("Owner").child(ownerId);
        DatabaseReference houseRef = FirebaseDatabase.getInstance().getReference().child("houses").child(ownerId).child(houseId);

        // Fetch the username
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownerName = dataSnapshot.child("username").getValue(String.class);

                // Fetch the search value
                houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        search = dataSnapshot.child("search").getValue(String.class);

                        // Continue with your existing code here, now with access to username and search
                        usersRef.child(memberID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // The user ID exists in the database
                                    if (dataSnapshot.hasChild("houseId")) {
                                        // The houseId exists for the user
                                        String existingHouseId = dataSnapshot.child("houseId").getValue(String.class);
                                        if (existingHouseId.equals(houseId)) {
                                            // The houseId exists and matches the current houseId
                                            progressDialog.dismiss();
                                            Toast.makeText(AddMember.this, "The user have been added to a house already", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // The houseId exists but does not match the current houseId, continue processing
                                            // Add your processing code here
                                            progressDialog.dismiss();
                                            Toast.makeText(AddMember.this, "The user is already added to another house", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // The houseId does not exist for the user, continue processing
                                        // Add houseId to the user
                                        usersRef.child(memberID).child("houseId").setValue(houseId);
                                        // Add member to the new path
                                        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("members");
                                        DatabaseReference ownerContractsRef = FirebaseDatabase.getInstance().getReference().child("ownerContracts");
                                        DatabaseReference memberContractsRef = FirebaseDatabase.getInstance().getReference().child("memberContract");
                                        String contractId = ownerContractsRef.push().getKey(); // Generate unique contractId

                                        //Create a new member
                                        MemberModel member = new MemberModel(memberID, age, name, roomNumber, rent, joiningDate, phoneNumber, ownerId, houseId);
                                        // Create a new contract
                                        Contract ownerContract = new Contract(name, ownerName, search, memberID, ownerId, houseId, roomNumber, joiningDate, "", Double.parseDouble(rent), "active");
                                        Contract memberContract = new Contract(name, ownerName, search, memberID, ownerId, houseId, roomNumber, joiningDate, "", Double.parseDouble(rent), "active");

                                        ownerContractsRef.child(houseId).child(contractId).setValue(ownerContract); // Use contractId in the path
                                        memberContractsRef.child(memberID).child(contractId).setValue(memberContract); // Use contractId in the path
                                        membersRef.child(houseId).child(memberID).setValue(member);
                                        progressDialog.dismiss();
                                        Toast.makeText(AddMember.this, "Add Member successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    // The user ID does not exist in the database
                                    progressDialog.dismiss();
                                    Toast.makeText(AddMember.this, "UserID did not exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle possible errors.
                                progressDialog.dismiss();
                                Toast.makeText(AddMember.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}



