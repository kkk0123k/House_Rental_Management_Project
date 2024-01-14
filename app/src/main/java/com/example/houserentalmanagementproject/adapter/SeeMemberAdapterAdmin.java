package com.example.houserentalmanagementproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.UserModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SeeMemberAdapterAdmin extends RecyclerView.Adapter<SeeMemberAdapterAdmin.RenterViewHolder> {

    private List<UserModel> renters;
    private Context context;
    private DatabaseReference mDatabase;

    public SeeMemberAdapterAdmin(Context context, List<UserModel> renters) {
        this.renters = renters;
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        Log.d("SeeMemberAdapterAdmin", "Number of renters: " + renters.size());
    }

    @NonNull
    @Override
    public RenterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("SeeMemberAdapterAdmin", "Creating view holder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.renter_item, parent, false);
        return new RenterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RenterViewHolder holder, int position) {
        Log.d("SeeMemberAdapterAdmin", "Binding data to view holder at position: " + position);
        UserModel renter = renters.get(position);
        holder.bind(renter);

        holder.itemView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.popup_menu); // your menu file
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.update_member:
                        // navigate to another activity to update the User data
                        Intent intent = new Intent(context, com.example.houserentalmanagementproject.admin.UpdateRenterAccount.class);
                        intent.putExtra("USER_DATA", renter); // Pass the UserModel instance
                        context.startActivity(intent);
                        return true;
                    case R.id.delete_member:
                        // display a dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Renter Account");
                        builder.setMessage("Do you want to delete this Renter Account?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // delete the renter account
                            mDatabase.child(renter.getUserId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // remove the user from the list and notify the adapter
                                    renters.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
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
        return renters.size();
    }

    class RenterViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserId, tvUsername, tvUserEmail;

        RenterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
        }

        void bind(UserModel renter) {
            Log.d("RenterViewHolder", "User id: " + renter.getUserId());
            Log.d("RenterViewHolder", "Username: " + renter.getUsername());
            Log.d("RenterViewHolder", "User email: " + renter.getUserEmail());
            tvUserId.setText(renter.getUserId());
            tvUsername.setText(context.getString(R.string.user_name,renter.getUsername()));
            tvUserEmail.setText(context.getString(R.string.user_email,renter.getUserEmail()));
        }
    }
}



