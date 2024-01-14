package com.example.houserentalmanagementproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SeeHouseAdapterAdmin extends RecyclerView.Adapter<SeeHouseAdapterAdmin.viewholder>{
    Context context;
    ArrayList<HouseModel> houseModelArrayList = new ArrayList<>();
    DatabaseReference mDatabase;

    public SeeHouseAdapterAdmin(Context context, ArrayList<HouseModel> houseModelArrayList) {
        this.context = context;
        this.houseModelArrayList = houseModelArrayList;
        mDatabase = FirebaseDatabase.getInstance().getReference("houses");
    }

    @NonNull
    @Override
    public SeeHouseAdapterAdmin.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seehouse, null, false);
        return new SeeHouseAdapterAdmin.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeeHouseAdapterAdmin.viewholder holder, int position) {
        HouseModel model = houseModelArrayList.get(position);

        holder.tv_location.setText(context.getString(R.string.houseLocation,model.getHouseLocation()));
        holder.tv_rentPerRoom.setText(context.getString(R.string.rentPerRomm, model.getRentPerRoom()));
        holder.tv_noOfRoom.setText(context.getString(R.string.numberOfRoom,model.getNoOfRoom()));
        Glide.with(context).load(model.getHouseImage()).into(holder.iv_houseImage);

        holder.itemView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.popup_menu_house); // your menu file
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.update_house:
                        // navigate to another activity to update the House data
                        Intent intent = new Intent(context, com.example.houserentalmanagementproject.admin.UpdateHouse.class);
                        intent.putExtra("HOUSE_DATA", model); // Pass the HouseModel instance
                        context.startActivity(intent);
                        return true;
                    case R.id.delete_house:
                        // display a dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete House");
                        builder.setMessage("Do you want to delete this House?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // delete the house
                            mDatabase.child(model.getUserId()).child(model.getHouseId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // remove the house from the list and notify the adapter
                                    houseModelArrayList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "House deleted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to delete house", Toast.LENGTH_SHORT).show();
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
        return houseModelArrayList.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        ImageView iv_houseImage;
        TextView tv_noOfRoom, tv_rentPerRoom, tv_location;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            iv_houseImage = itemView.findViewById(R.id.imageview);
            tv_noOfRoom = itemView.findViewById(R.id.tv_noOfRooms);
            tv_rentPerRoom = itemView.findViewById(R.id.tv_rentPerRoom);
            tv_location = itemView.findViewById(R.id.tv_location);
        }
    }
}

