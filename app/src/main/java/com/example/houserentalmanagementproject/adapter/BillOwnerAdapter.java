package com.example.houserentalmanagementproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.houserentalmanagementproject.houseOwner.ViewBill;
import com.example.houserentalmanagementproject.Bill;
import com.example.houserentalmanagementproject.MemberModel;
import com.example.houserentalmanagementproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BillOwnerAdapter extends RecyclerView.Adapter<BillOwnerAdapter.BillViewHolder> {
    private final Context context;
    private final ArrayList<Bill> billArrayList;
    private final OnItemClickListener listener;

    public BillOwnerAdapter(Context context, ArrayList<Bill> billArrayList, OnItemClickListener listener) {
        this.context = context;
        this.billArrayList = billArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bill_owner_card_view, parent, false);
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

            itemView.setOnClickListener(v -> {
                DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference().child("members").child(bill.getHouseId()).child(bill.getMemberId());
                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MemberModel member = dataSnapshot.getValue(MemberModel.class);
                        if (member != null) {
                            String userName = member.getName(); // Get the user name from the MemberModel object
                            Toast.makeText(context, "User Name: " + userName + "\nRoom Number: " + member.getRoomNumber() + "\nBill Sum: " + bill.getBillSum(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
                listener.onItemClick(bill);
            });
        }
    }
    public void updateData(ArrayList<Bill> newBillArrayList) {
        billArrayList.clear();
        billArrayList.addAll(newBillArrayList);
        notifyDataSetChanged();
    }
}

