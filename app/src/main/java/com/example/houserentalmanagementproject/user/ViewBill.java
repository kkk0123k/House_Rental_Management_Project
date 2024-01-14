package com.example.houserentalmanagementproject.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.Bill;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.BillAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewBill extends AppCompatActivity {
    private Spinner spinner_month, spinner_year, spinner_status;
    private ArrayList<Bill> billArrayList;
    private BillAdapter billAdapter;
    private DatabaseReference billRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        spinner_month = findViewById(R.id.spinner_month);
        spinner_year = findViewById(R.id.spinner_year);
        spinner_status = findViewById(R.id.spinner_status); // New status spinner

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        billArrayList = new ArrayList<>();
        billAdapter = new BillAdapter(this, billArrayList, new BillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bill bill) {
                Intent intent = new Intent(ViewBill.this, MakePayment.class);
                intent.putExtra("billId", bill.getBillId());
                intent.putExtra("billSum", bill.getBillSum());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(billAdapter);

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(yearAdapter);

        // New status adapter
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(statusAdapter);

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        spinner_month.setSelection(currentMonth - 1);
        spinner_year.setSelection(yearAdapter.getPosition(String.valueOf(currentYear)));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String houseId = dataSnapshot.child("houseId").getValue(String.class);

                DatabaseReference billRef = FirebaseDatabase.getInstance().getReference().child("bill").child(userId).child(houseId);
                billRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        billArrayList.clear();
                        for (DataSnapshot billSnapshot : dataSnapshot.getChildren()) {
                            Bill bill = billSnapshot.getValue(Bill.class);
                            billArrayList.add(bill);
                        }
                        filterBills();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterBills();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinner_month.setOnItemSelectedListener(onItemSelectedListener);
        spinner_year.setOnItemSelectedListener(onItemSelectedListener);
        spinner_status.setOnItemSelectedListener(onItemSelectedListener); // New status spinner listener

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(billAdapter);
    }

    private void filterBills() {
        int selectedMonth = spinner_month.getSelectedItemPosition() + 1;
        int selectedYear = Integer.parseInt(spinner_year.getSelectedItem().toString());
        String selectedStatus = spinner_status.getSelectedItem().toString(); // New status filter
        ArrayList<Bill> filteredBillArrayList = new ArrayList<>();
        for (Bill bill : billArrayList) {
            if (bill.getMonth() == selectedMonth && bill.getYear() == selectedYear && bill.getStatus().equals(selectedStatus)) {
                filteredBillArrayList.add(bill);
            }
        }
        billAdapter.updateData(filteredBillArrayList);
    }
}



