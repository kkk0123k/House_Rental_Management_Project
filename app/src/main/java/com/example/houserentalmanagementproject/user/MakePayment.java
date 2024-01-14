package com.example.houserentalmanagementproject.user;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class MakePayment extends AppCompatActivity {
    private EditText et_paymentAmount;
    private Button btn_makePayment;
    private ProgressDialog progressDialog;
    private String billSum, month, year, ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        Intent intent = getIntent();

        String billId = intent.getStringExtra("billId");
        String houseId = intent.getStringExtra("houseId");
        String memberId = intent.getStringExtra("userId");
        billSum = intent.getStringExtra("totalAmount");

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        month = Integer.toString(currentMonth);
        year = Integer.toString(currentYear);

        et_paymentAmount = findViewById(R.id.et_paymentAmount);
        btn_makePayment = findViewById(R.id.btn_makePayment);
        TextView tv_paymentDate = findViewById(R.id.tv_paymentDate);
        TextView tv_billSum = findViewById(R.id.tv_billSum);

        tv_billSum.setText(getString(R.string.billSum, billSum));
        tv_paymentDate.setText(getString(R.string.payment_date, month, year));

        progressDialog = new ProgressDialog(MakePayment.this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("members").child(houseId).child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownerId = dataSnapshot.child("ownerId").getValue(String.class);

                btn_makePayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setMessage("Making Payment...");
                        progressDialog.setTitle("Processing...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        double amount = Double.parseDouble(et_paymentAmount.getText().toString());

                        if (amount == Double.parseDouble(billSum)) {
                            makePayment(amount, memberId, houseId, billId, ownerId);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MakePayment.this, "Wrong amount of money, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void makePayment(double amount, String memberId, String houseId, String billId, String ownerId) {
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference().child("payment").child(ownerId).child(houseId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("amount", amount);
        hashMap.put("month", month);
        hashMap.put("year", year);
        hashMap.put("payCheck", true);
        hashMap.put("memberId", memberId);
        hashMap.put("houseId", houseId);
        String paymentId = UUID.randomUUID().toString();
        hashMap.put("paymentId", paymentId);
        hashMap.put("billId", billId);

        paymentRef.child(paymentId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    DatabaseReference billRef = FirebaseDatabase.getInstance().getReference().child("bill").child(memberId).child(houseId).child(billId);
                    billRef.child("status").setValue("Complete");

                    Toast.makeText(MakePayment.this, "Payment made successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MakePayment.this, "Failed to make payment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

