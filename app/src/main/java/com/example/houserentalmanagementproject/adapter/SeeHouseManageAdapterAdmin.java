package com.example.houserentalmanagementproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.OwnerModel;
import com.example.houserentalmanagementproject.R;

import java.util.List;

public class SeeHouseManageAdapterAdmin extends RecyclerView.Adapter<SeeHouseManageAdapterAdmin.OwnerViewHolder> {

    private List<OwnerModel> owners;
    private Context context;

    public SeeHouseManageAdapterAdmin(Context context, List<OwnerModel> owners) {
        this.owners = owners;
        this.context = context;
    }

    @NonNull
    @Override
    public OwnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_owner_item, parent, false);
        return new OwnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerViewHolder holder, int position) {
        OwnerModel owner = owners.get(position);
        holder.bind(owner);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.houserentalmanagementproject.admin.HouseAction.class);
            intent.putExtra("OWNER_DATA", owner);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return owners.size();
    }

    class OwnerViewHolder extends RecyclerView.ViewHolder {

        TextView tvOwnerId, tvUsername, tvUserEmail;

        OwnerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOwnerId = itemView.findViewById(R.id.tv_owner_id);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
        }

        void bind(OwnerModel owner) {
            tvOwnerId.setText(owner.getOwnerId());
            tvUsername.setText(context.getString(R.string.user_name,owner.getUsername()));
            tvUserEmail.setText(context.getString(R.string.user_email,owner.getUserEmail()));
        }
    }
}

