package com.example.houserentalmanagementproject;

// Import the required Firebase modules
import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

public class ChangePassword extends AppCompatActivity {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Get references to the EditText fields
        oldPasswordEditText = findViewById(R.id.et_oldPassword);
        newPasswordEditText = findViewById(R.id.et_newPassword);

        // Get reference to the button
        changePasswordButton = findViewById(R.id.btn_changePassword);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Get the old and new passwords
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                // Create credentials with the email and old password
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                // Try to re-authenticate the user
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // The old password is correct, update the password
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Password updated");
                                                        Toast.makeText(ChangePassword.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Log.d(TAG, "Error password not updated");
                                                        Toast.makeText(ChangePassword.this, "Error! Password not updated.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // The old password is incorrect
                                    Toast.makeText(ChangePassword.this, "Error! Old password is incorrect.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
