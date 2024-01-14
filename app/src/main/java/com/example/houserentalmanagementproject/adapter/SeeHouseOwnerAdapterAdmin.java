package com.example.houserentalmanagementproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.OwnerModel;
import com.example.houserentalmanagementproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SeeHouseOwnerAdapterAdmin extends RecyclerView.Adapter<SeeHouseOwnerAdapterAdmin.OwnerViewHolder> {

    private List<OwnerModel> owners;
    private Context context;
    private DatabaseReference mDatabase;

    public SeeHouseOwnerAdapterAdmin(Context context, List<OwnerModel> owners) {
        this.owners = owners;
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference("Owner");
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
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.popup_menu); // your menu file
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.update_member:
                        // navigate to another activity to update the Owner data
                        Intent intent = new Intent(context, com.example.houserentalmanagementproject.admin.UpdateHouseOwner.class);
                        intent.putExtra("OWNER_DATA", owner); // Pass the OwnerModel instance
                        context.startActivity(intent);
                        return true;
                    case R.id.delete_member:
                        // display a dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Owner Account");
                        builder.setMessage("Do you want to delete this Owner Account?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // delete the owner account
                            mDatabase.child(owner.getOwnerId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // remove the owner from the list and notify the adapter
                                    owners.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Owner deleted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to delete owner", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                        builder.create().show();
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
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
