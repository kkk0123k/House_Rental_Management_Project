package com.example.houserentalmanagementproject.houseOwner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.Bill;
import com.example.houserentalmanagementproject.MemberModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.BillOwnerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewBill extends AppCompatActivity {
    String userId, houseId;
    private RecyclerView recyclerView;
    private BillOwnerAdapter adapter;
    private final ArrayList<Bill> billArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Spinner spinner_month, spinner_year, spinner_status; // Declare the new spinner
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill_owner);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        houseId = intent.getStringExtra("houseId");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillOwnerAdapter(this, billArrayList, bill -> {
            // Handle bill click
        });
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(ViewBill.this);
        progressDialog.setMessage("Loading Bills...");
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        spinner_month = findViewById(R.id.spinner_month);
        spinner_year = findViewById(R.id.spinner_year);
        spinner_status = findViewById(R.id.spinner_status); // Initialize the new spinner

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.statuses, android.R.layout.simple_spinner_item); // Create an adapter for the spinner
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(statusAdapter); // Set the adapter to the spinner

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        spinner_month.setSelection(currentMonth - 1);
        spinner_year.setSelection(yearAdapter.getPosition(String.valueOf(currentYear)));

        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isLoading) {
                    loadBills();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isLoading) {
                    loadBills();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // Set an item selected listener for the spinner
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isLoading) {
                    loadBills();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadBills() {
        isLoading = true;
        progressDialog.show();
        billArrayList.clear();

        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("members").child(houseId);
        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> memberIdList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MemberModel member = snapshot.getValue(MemberModel.class);
                    memberIdList.add(member.getMemberId());
                }

                for (String memberId : memberIdList) {
                    DatabaseReference billsRef = FirebaseDatabase.getInstance().getReference().child("bill").child(memberId).child(houseId);
                    billsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot billSnapshot : dataSnapshot.getChildren()) {
                                Bill bill = billSnapshot.getValue(Bill.class);
                                billArrayList.add(bill);
                            }
                            filterBills();
                            progressDialog.dismiss();
                            isLoading = false;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle possible errors.
                            isLoading = false;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                isLoading = false;
            }
        });
    }

    private void filterBills() {
        int selectedMonth = spinner_month.getSelectedItemPosition() + 1;
        int selectedYear = Integer.parseInt(spinner_year.getSelectedItem().toString());
        String selectedStatus = spinner_status.getSelectedItem().toString(); // Get the selected status
        ArrayList<Bill> filteredBillArrayList = new ArrayList<>();
        for (Bill bill : billArrayList) {
            if (bill.getMonth() == selectedMonth && bill.getYear() == selectedYear && bill.getStatus().equals(selectedStatus)) {
                filteredBillArrayList.add(bill);
            }
        }
        adapter.updateData(filteredBillArrayList);
    }
}




