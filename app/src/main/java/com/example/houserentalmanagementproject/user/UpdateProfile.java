package com.example.houserentalmanagementproject.user;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateProfile extends AppCompatActivity {
    Button btn_updateProfile;
    TextView tv_userID, tv_userEmail;
    EditText et_username, et_email, et_phoneNumber;
    ImageView copy_userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_profile);

        btn_updateProfile = findViewById(R.id.btn_changePassword);
        tv_userID = findViewById(R.id.tv_userID);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();

            tv_userID.setText(userId);
            et_email.setText(userEmail);

            mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        String username = task.getResult().child("username").getValue(String.class);
                        String phone = task.getResult().child("phone").getValue(String.class);
                        et_username.setText(username);
                        if (phone != null) {
                            et_phoneNumber.setText(phone);
                        }
                    } else {
                        Toast.makeText(com.example.houserentalmanagementproject.user.UpdateProfile.this, "Error getting data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            copy_userID = findViewById(R.id.copy_userID);
            copy_userID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("userID", tv_userID.getText().toString());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(com.example.houserentalmanagementproject.user.UpdateProfile.this, "User ID copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });
            btn_updateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newUsername = et_username.getText().toString();
                    String newEmail = et_email.getText().toString();
                    String newPhone = et_phoneNumber.getText().toString();

                    if (!newUsername.isEmpty() && !newEmail.isEmpty() && !newPhone.isEmpty()) {
                        mDatabase.child("Users").child(userId).child("username").setValue(newUsername);
                        mDatabase.child("Users").child(userId).child("userEmail").setValue(newEmail);
                        mDatabase.child("Users").child(userId).child("search").setValue(newUsername.toLowerCase());
                        mDatabase.child("Users").child(userId).child("phone").setValue(newPhone);

                        Toast.makeText(com.example.houserentalmanagementproject.user.UpdateProfile.this, "Change user information successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(com.example.houserentalmanagementproject.user.UpdateProfile.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
