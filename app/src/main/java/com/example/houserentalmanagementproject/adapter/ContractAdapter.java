package com.example.houserentalmanagementproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.houserentalmanagementproject.Contract;
import com.example.houserentalmanagementproject.R;

import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {
    Context context;
    private final List<Contract> contracts;
    private final boolean isTerminated;

    public ContractAdapter(Context context, List<Contract> contracts, boolean isTerminated) {
        this.context = context;
        this.contracts = contracts;
        this.isTerminated = isTerminated;
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seecontract, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        Contract contract = contracts.get(position);
        holder.renterName.setText(context.getString(R.string.memberName,contract.getMemberName()));
        holder.ownerName.setText(context.getString(R.string.ownerName,contract.getOwnerName()));
        holder.houseName.setText(context.getString(R.string.houseName,contract.getHouseName()));
        holder.roomNumber.setText(context.getString(R.string.roomNumber,contract.getRoomNumber()));
        holder.joiningDate.setText(context.getString(R.string.joiningDate,contract.getStartDate()));
        holder.rentAmount.setText(context.getString(R.string.rentAmount, String.valueOf((int) contract.getRentAmount())));
        holder.status.setText(context.getString(R.string.Status,contract.getStatus()));

        if (isTerminated) {
            holder.endingDate.setVisibility(View.VISIBLE);
            holder.endingDate.setText(contract.getEndDate());
        } else {
            holder.endingDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contracts.size();
    }

    public void updateData(List<Contract> newContracts) {
        this.contracts.clear();
        this.contracts.addAll(newContracts);
        notifyDataSetChanged();
    }

    static class ContractViewHolder extends RecyclerView.ViewHolder {

        TextView renterName, ownerName, houseName, roomNumber, joiningDate, endingDate, rentAmount, status;

        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            renterName = itemView.findViewById(R.id.renter_name);
            ownerName = itemView.findViewById(R.id.owner_name);
            houseName = itemView.findViewById(R.id.house_name);
            roomNumber = itemView.findViewById(R.id.room_number);
            joiningDate = itemView.findViewById(R.id.joining_date);
            endingDate = itemView.findViewById(R.id.ending_date);
            rentAmount = itemView.findViewById(R.id.rent_amount);
            status = itemView.findViewById(R.id.status);
        }
    }
}


