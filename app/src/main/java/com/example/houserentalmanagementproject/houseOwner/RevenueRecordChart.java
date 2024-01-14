package com.example.houserentalmanagementproject.houseOwner;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalmanagementproject.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RevenueRecordChart extends AppCompatActivity {
    private BarChart chart;
    private ProgressDialog progressDialog;
    private Spinner spinnerYear,spinnerHouse,spinnerFilterByDate;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String ownerId;
    Map<String, String> houseMap;
    {
        assert user != null;
        ownerId = user.getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_record_chart);

        // Initialize the spinners
        spinnerFilterByDate = findViewById(R.id.spinnerFilterByDate);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerHouse = findViewById(R.id.spinnerHouse);

        houseMap = new HashMap<>();
        // Populate spinnerYear
        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Set the default value of spinnerYear to the current year
        spinnerYear.setSelection(adapterYear.getPosition(String.valueOf(currentYear)));

        // Populate spinnerHouse
        DatabaseReference housesRef = FirebaseDatabase.getInstance().getReference().child("houses").child(ownerId);
        housesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> houseList = new ArrayList<>();

                houseList.add("All house"); // Add "All house" to the list
                houseMap.put("All house", "All house"); // Add "All house" to the map

                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
                    String houseName = houseSnapshot.child("search").getValue(String.class);
                    String houseId = houseSnapshot.getKey(); // Get the house ID

                    houseList.add(houseName);
                    houseMap.put(houseName, houseId); // Store the house name and ID in the map
                }

                ArrayAdapter<String> adapterHouse = new ArrayAdapter<>(RevenueRecordChart.this,
                        android.R.layout.simple_spinner_item, houseList);
                adapterHouse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerHouse.setAdapter(adapterHouse);
                spinnerHouse.setSelection(0); // Set "All house" as the default selected item
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.d("DatabaseError", databaseError.getMessage());
            }
        });

        // Populate spinnerFilterByDate
        ArrayAdapter<CharSequence> adapterFilterByDate = ArrayAdapter.createFromResource(this,
                R.array.filter_by_date_array, android.R.layout.simple_spinner_item);
        adapterFilterByDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterByDate.setAdapter(adapterFilterByDate);

        spinnerHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no house is selected if necessary
            }
        });

        spinnerFilterByDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Log.d("FilterByDate", "Selected: " + selected);

                // Make spinnerYear visible when "Month" or "Quarter Year" is selected
                if (selected.equals("Month") || selected.equals("Quarter Year")) {
                    spinnerYear.setVisibility(View.VISIBLE);
                } else if (selected.equals("Year")) {
                    spinnerYear.setVisibility(View.GONE);
                }

                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected if necessary
                Log.d("FilterByDate", "Nothing selected");
            }
        });


        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no year is selected if necessary
            }
        });

        chart = findViewById(R.id.chart);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);

        progressDialog = new ProgressDialog(RevenueRecordChart.this);
        progressDialog.setMessage("Loading data...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void updateData() {
        // Get the selected year, filter, and house
        String selectedYear = spinnerYear.getSelectedItem().toString();
        String selectedFilter = spinnerFilterByDate.getSelectedItem().toString();
        String selectedHouse = spinnerHouse.getSelectedItem().toString();

        Log.d("SelectedYear", "Year: " + selectedYear);
        Log.d("FilterByDate", "Selected: " + selectedFilter);
        Log.d("SelectedHouse", "House: " + selectedHouse);

        // Get the house ID corresponding to the selected house name
        String houseId = houseMap.get(selectedHouse);

        // Filter the data based on the selected year, filter, and house
        DatabaseReference paymentsRef;
        if (selectedHouse.equals("All house")) {
            paymentsRef = FirebaseDatabase.getInstance().getReference().child("payment").child(ownerId);
        } else {
            paymentsRef = FirebaseDatabase.getInstance().getReference().child("payment").child(ownerId).child(houseId);
        }

        paymentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize an array to hold the sums for each month
                float[] monthlySums = new float[13];
                float[] quarterlySums = new float[5];
                float[] yearlySums = new float[6];

                Iterable<DataSnapshot> paymentSnapshots;
                if (selectedHouse.equals("All house")) {
                    List<DataSnapshot> allPayments = new ArrayList<>();
                    for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
                        houseSnapshot.getChildren().forEach(allPayments::add);
                    }
                    paymentSnapshots = allPayments;
                } else {
                    paymentSnapshots = dataSnapshot.getChildren();
                }

                for (DataSnapshot paymentSnapshot : paymentSnapshots) {
                    String paymentId = paymentSnapshot.getKey();
                    Log.d("PaymentId", "PaymentId: " + paymentId);
                    Float amount = paymentSnapshot.child("amount").getValue(Float.class);
                    String monthString = paymentSnapshot.child("month").getValue(String.class);
                    String yearString = paymentSnapshot.child("year").getValue(String.class);

                    Log.d("PaymentDetail", "Amount: " + amount + ", Month: " + monthString + ", Year: " + yearString);

                    if (amount != null && monthString != null && yearString != null && yearString.equals(selectedYear)) {
                        int month = Integer.parseInt(monthString);
                        int quarter = (month - 1) / 3 + 1; // Calculate the quarter of the year
                        int year = Integer.parseInt(yearString);
                        int yearIndex = year - (Integer.parseInt(selectedYear) - 5); // Calculate the index for the yearlySums array

                        if (yearIndex >= 0 && yearIndex < yearlySums.length) {
                            monthlySums[month] += amount;
                            quarterlySums[quarter] += amount;
                            yearlySums[yearIndex] += amount;
                        }
                    }
                }

                // Create the entries for the chart
                ArrayList<BarEntry> monthlyEntries = new ArrayList<>();
                ArrayList<BarEntry> quarterlyEntries = new ArrayList<>();
                ArrayList<BarEntry> yearlyEntries = new ArrayList<>();

                float maxMonth = 0;
                float maxQuarter = 0;
                float maxYear = 0;

                for (int i = 1; i < 13; i++) {
                    monthlyEntries.add(new BarEntry(i + 1, monthlySums[i])); // Add 1 to match month number
                    if (monthlySums[i] > maxMonth) {
                        maxMonth = monthlySums[i];
                    }
                }

                for (int i = 1; i < 5; i++) {
                    quarterlyEntries.add(new BarEntry(i + 1, quarterlySums[i]));
                    if (quarterlySums[i] > maxQuarter) {
                        maxQuarter = quarterlySums[i];
                    }
                }

                for (int i = 0; i < yearlySums.length; i++) {
                    yearlyEntries.add(new BarEntry(i + 1, yearlySums[i]));
                    if (yearlySums[i] > maxYear) {
                        maxYear = yearlySums[i];
                    }
                }

                float roundedMaxMonth = (float) Math.pow(10, Math.floor(Math.log10(maxMonth))) * ((int)(maxMonth / Math.pow(10, Math.floor(Math.log10(maxMonth)))) + 1);
                float roundedMaxQuarter = (float) Math.pow(10, Math.floor(Math.log10(maxQuarter))) * ((int)(maxQuarter / Math.pow(10, Math.floor(Math.log10(maxQuarter)))) + 1);
                float roundedMaxYear = (float) Math.pow(10, Math.floor(Math.log10(maxYear))) * ((int)(maxYear / Math.pow(10, Math.floor(Math.log10(maxYear)))) + 1);

                switch (selectedFilter) {
                    case "Month":
                        Log.d("Chart", "Displaying Month chart");
                        MonthChart(monthlyEntries, roundedMaxMonth);
                        break;
                    case "Quarter Year":
                        Log.d("Chart", "Displaying Quarter Year chart");
                        QuarterYearChart(quarterlyEntries, roundedMaxQuarter);
                        break;
                    case "Year":
                        Log.d("Chart", "Displaying Year chart");
                        YearChart(yearlyEntries, roundedMaxYear);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.d("DatabaseError", databaseError.getMessage());
            }
        });
    }



    private void MonthChart(ArrayList<BarEntry> monthlyEntries, float max) {
        createBarChart(monthlyEntries, max, new String[]{"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", ""}, 14f, 14);
    }

    private void QuarterYearChart(ArrayList<BarEntry> quarterlyEntries, float max) {
        createBarChart(quarterlyEntries, max, new String[]{"", "Q1", "Q2", "Q3", "Q4", ""}, 6f, 6);
    }

    private void YearChart(ArrayList<BarEntry> yearlyEntries, float max) {
        createBarChart(yearlyEntries, max, new String[]{"", "2020", "2021", "2022", "2023", "2024", ""}, 7f, 7);
    }

    private void createBarChart(ArrayList<BarEntry> entries, float max, String[] labels, float xAxisMax, int labelCount) {
        BarDataSet dataSet = new BarDataSet(entries, "Rent Amount");
        BarData data = new BarData(dataSet);
        float barWidth = 0.8f;
        data.setBarWidth(barWidth);
        chart.setData(data);
        chart.setExtraOffsets(0f, 10f, 0f, 20f);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(max);
        leftAxis.setLabelCount(4, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.getDescription().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(xAxisMax);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(labelCount, true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setYOffset(10f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value - 1;
                if (index >= 0 && index < labels.length) {
                    return labels[index];
                } else {
                    return "";
                }
            }
        });

        Legend legend = chart.getLegend();
        legend.setYOffset(0f);
        legend.setEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(10f);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);

        chart.invalidate();
        progressDialog.dismiss();
    }
}
























