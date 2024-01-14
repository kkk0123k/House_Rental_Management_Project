package com.example.houserentalmanagementproject.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.houseOwner.DashboardOwner;
import com.example.houserentalmanagementproject.houseOwner.LoginOwner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AddHouseOwner extends AppCompatActivity {

    public static final String OWNER = "Owner";
    public static final String HOUSES = "houses";
    public static final String MEMBERS = "members";

    EditText et_email, et_password, et_confirmPassword, et_username, et_phoneNumber;
    Button btn_addHouseOwner;
    TextView tv_loginBtn;

    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex);

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_houseowner);

        et_phoneNumber = findViewById(R.id.et_phoneNumber);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        btn_addHouseOwner = findViewById(R.id.btn_addHouseOwner);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btn_addHouseOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAuth();
            }
        });

    }

    private void PerformAuth() {
        String phoneNumber = et_phoneNumber.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String confirmPassword = et_confirmPassword.getText().toString();
        String username = et_username.getText().toString();

        if (email.isEmpty()) {
            et_email.setError("Please Enter Email");
        } else if (!pat.matcher(email).matches()) {
            et_email.setError("Please Enter a valid Email");
        } else if (password.isEmpty()) {
            et_password.setError("Please input Password");
        } else if (password.length() < 6) {
            et_password.setError("Password too short");
        } else if (!confirmPassword.equals(password)) {
            et_confirmPassword.setError("Password doesn't matches");
        }
        else if(phoneNumber.isEmpty()){
            et_phoneNumber.setError("Please input Phone number");
        } else {
            progressDialog.setMessage("Creating your Account....");
            progressDialog.setTitle("Creating");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference().child(com.example.houserentalmanagementproject.houseOwner.RegisterOwner.OWNER).child(userId);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("phone", phoneNumber);
                        hashMap.put("ownerId", userId);
                        hashMap.put("userEmail", email);
                        hashMap.put("imageUrl", "default");
                        hashMap.put("username",username);
                        hashMap.put("search", username.toLowerCase());

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(com.example.houserentalmanagementproject.admin.AddHouseOwner.this, "Add House Owner Success", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(com.example.houserentalmanagementproject.admin.AddHouseOwner.this, "Add House Owner Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}