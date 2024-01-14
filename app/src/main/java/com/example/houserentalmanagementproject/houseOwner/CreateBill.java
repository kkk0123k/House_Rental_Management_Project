package com.example.houserentalmanagementproject.houseOwner;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.Bill;
import com.example.houserentalmanagementproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.stream.IntStream;

public class CreateBill extends AppCompatActivity {

    EditText et_name, et_roomNumber, et_rentBill, et_waterBill, et_electricBill;
    TextView tv_billSum;
    Button btn_createBill;
    Spinner spinner_month, spinner_year;
    String userId, houseId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);

        et_name = findViewById(R.id.et_name);
        et_roomNumber = findViewById(R.id.et_roomNumber);
        et_rentBill = findViewById(R.id.et_rentBill);
        et_waterBill = findViewById(R.id.et_waterBill);
        et_electricBill = findViewById(R.id.et_electricBill);
        tv_billSum = findViewById(R.id.tv_billsum);
        btn_createBill = findViewById(R.id.btn_createBill);
        spinner_month = findViewById(R.id.spinner_month);
        spinner_year = findViewById(R.id.spinner_year);

        // Get the data from the Intent
        userId = getIntent().getStringExtra("userId");
        houseId = getIntent().getStringExtra("houseId");
        String name = getIntent().getStringExtra("name");
        String roomNumber = getIntent().getStringExtra("roomNumber");
        String rentHouse = getIntent().getStringExtra("rentHouse");

        // Populate the EditTexts with data from the Intent
        et_name.setText(name);
        et_roomNumber.setText(roomNumber);
        et_rentBill.setText(rentHouse);
        // Get the current month and year
        ArrayAdapter<CharSequence> monthAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, IntStream.rangeClosed(1, 12).mapToObj(Integer::toString).toArray(CharSequence[]::new));
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> yearAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, IntStream.rangeClosed(2000, 2050).mapToObj(Integer::toString).toArray(CharSequence[]::new));
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(yearAdapter);
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int currentYear = calendar.get(Calendar.YEAR);

        // Set the Spinners to the current month and year
        spinner_month.setSelection(currentMonth - 1); // The Spinner is zero-based
        spinner_year.setSelection(yearAdapter.getPosition(String.valueOf(currentYear)));
        // Add TextChangedListener to update billSum in real-time
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                long rentBill = et_rentBill.getText().toString().isEmpty() ? 0 : Long.parseLong(et_rentBill.getText().toString());
                long waterBill = et_waterBill.getText().toString().isEmpty() ? 0 : Long.parseLong(et_waterBill.getText().toString());
                long electricBill = et_electricBill.getText().toString().isEmpty() ? 0 : Long.parseLong(et_electricBill.getText().toString());
                long billSum = rentBill + waterBill + electricBill;
                tv_billSum.setText(String.valueOf(billSum));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        et_rentBill.addTextChangedListener(textWatcher);
        et_waterBill.addTextChangedListener(textWatcher);
        et_electricBill.addTextChangedListener(textWatcher);

        btn_createBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the data from the EditTexts and Spinners
                String status = "Pending";
                String name = et_name.getText().toString();
                String roomNumber = et_roomNumber.getText().toString();
                String rentBill = et_rentBill.getText().toString();
                String waterBill = et_waterBill.getText().toString();
                String electricBill = et_electricBill.getText().toString();
                String billSum = tv_billSum.getText().toString();
                int month = spinner_month.getSelectedItemPosition() + 1;
                int year = 2000 + spinner_year.getSelectedItemPosition(); // Assuming the Spinner starts from 2000

                // Get the memberId from the Intent
                String memberId = getIntent().getStringExtra("memberId");
                // Generate a unique ID for the bill
                DatabaseReference billRef = FirebaseDatabase.getInstance().getReference().child("bill").child(memberId).child(houseId);
                String billId = billRef.push().getKey();
                // Create a new Bill object with the billId and other data
                Bill bill = new Bill(status, billId, name, roomNumber, rentBill, waterBill, electricBill, billSum, month, year, memberId, houseId);

                // Save the Bill object to Firebase Realtime Database
                billRef.child(billId).setValue(bill).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateBill.this, "Bill create success", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(CreateBill.this, "Failed to create bill", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}


