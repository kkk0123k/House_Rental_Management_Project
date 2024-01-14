package com.example.houserentalmanagementproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.Bill;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.user.MakePayment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    Context context;
    ArrayList<Bill> billArrayList;
    OnItemClickListener listener;

    public BillAdapter(Context context, ArrayList<Bill> billArrayList, OnItemClickListener listener) {
        this.context = context;
        this.billArrayList = billArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bill_card_view, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billArrayList.get(position);
        holder.bind(bill, listener);
    }

    @Override
    public int getItemCount() {
        return billArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Bill bill);
    }

    public class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tv_roomNumber, tv_billSum, tv_status, tv_paymentDate;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_roomNumber = itemView.findViewById(R.id.tv_roomNumber);
            tv_billSum = itemView.findViewById(R.id.tv_billSum);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_paymentDate = itemView.findViewById(R.id.tv_paymentDate);
        }

        public void bind(final Bill bill, final OnItemClickListener listener) {
            // Set the TextViews with data from the Bill object
            tv_roomNumber.setText(context.getString(R.string.roomNumber,bill.getRoomNumber()));
            tv_billSum.setText(context.getString(R.string.billSum,String.valueOf(bill.getBillSum())));
            tv_status.setText(context.getString(R.string.Status,bill.getStatus()));
            String monthYear = bill.getMonth() + " / " + bill.getYear();
            tv_paymentDate.setText(context.getString(R.string.monthYear,monthYear));
            Log.d("ViewBill", "Displaying bill: " + bill);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Bill Details");

                    // Format the bill details
                    String message = "Room Number: " + bill.getRoomNumber() +
                            "\nBill Sum: " + bill.getBillSum();

                    builder.setMessage(message);

                    builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the Pay button
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String houseId = dataSnapshot.child("houseId").getValue(String.class);
                                        // Now you have the houseId and you can pass it to the MakePayment activity
                                        Intent intent = new Intent(context, MakePayment.class);
                                        intent.putExtra("billId", bill.getBillId());
                                        intent.putExtra("totalAmount", bill.getBillSum());
                                        intent.putExtra("month", String.valueOf(bill.getMonth()));
                                        intent.putExtra("year", String.valueOf(bill.getYear()));
                                        intent.putExtra("houseId", houseId);
                                        intent.putExtra("userId", userId);
                                        Log.d("BillAdapter", userId);
                                        Log.d("BillAdapter", houseId);

                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle possible errors.
                                    }
                                });
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

    }

    // Move updateData method here
    public void updateData(ArrayList<Bill> newBillArrayList) {
        this.billArrayList = newBillArrayList;
        notifyDataSetChanged();
    }
}

