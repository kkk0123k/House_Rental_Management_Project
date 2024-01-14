package com.example.houserentalmanagementproject.houseOwner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.MemberModel;
import com.example.houserentalmanagementproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateMember extends AppCompatActivity {

    private EditText etMemberName, etRoomNumber, etMemberAge, etMemberRent, etMemberPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_member);

        // Get the MemberModel object from the intent
        final MemberModel model = (MemberModel) getIntent().getSerializableExtra("memberModel");

        // Get references to your EditTexts
        etMemberName = findViewById(R.id.et_memberName);
        etRoomNumber = findViewById(R.id.et_roomNumber);
        etMemberAge = findViewById(R.id.et_memberAge);
        etMemberRent = findViewById(R.id.et_memberRent);
        etMemberPhoneNumber = findViewById(R.id.et_memberPhoneNumber);

        // Populate the EditTexts with the member's details
        etMemberName.setText(model.getName());
        etRoomNumber.setText(model.getRoomNumber());
        etMemberAge.setText(model.getAge());
        etMemberRent.setText(model.getRent());
        etMemberPhoneNumber.setText(model.getPhoneNumber());

        // Get reference to your button
        Button btnUpdateMember = findViewById(R.id.btn_updateMember);

        // Set OnClickListener for the button
        btnUpdateMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the member's details in the model
                model.setName(etMemberName.getText().toString());
                model.setJob(etRoomNumber.getText().toString());
                model.setAge(etMemberAge.getText().toString());
                model.setRent(etMemberRent.getText().toString());
                model.setPhoneNumber(etMemberPhoneNumber.getText().toString());

                // Save the updated member's details in Firebase
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(RegisterOwner.MEMBERS).child(model.getHouseId()).child(model.getMemberId());
                ref.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateMember.this, "Member updated", Toast.LENGTH_SHORT).show();
                            // Navigate back to the ViewMember activity
                            finish();
                        } else {
                            Toast.makeText(UpdateMember.this, "Failed to update member", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}