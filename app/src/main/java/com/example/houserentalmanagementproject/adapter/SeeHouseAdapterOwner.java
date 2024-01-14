package com.example.houserentalmanagementproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.MemberModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.houseOwner.HouseDetails;
import com.example.houserentalmanagementproject.houseOwner.RegisterOwner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SeeHouseAdapterOwner extends RecyclerView.Adapter<SeeHouseAdapterOwner.viewholder> {
    Context context;
    ArrayList<HouseModel> houseModelArrayList;

    public SeeHouseAdapterOwner(Context context, ArrayList<HouseModel> houseModelArrayList) {
        this.context = context;
        this.houseModelArrayList = houseModelArrayList;
    }

    @NonNull
    @Override
    public SeeHouseAdapterOwner.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seehouse, null, false);
        return new SeeHouseAdapterOwner.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeeHouseAdapterOwner.viewholder holder, int position) {
        HouseModel model = houseModelArrayList.get(position);

        // Get reference to Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(RegisterOwner.HOUSES).child(model.getUserId()).child(model.getHouseId());

        // Add ValueEventListener
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the updated HouseModel object
                HouseModel updatedHouse = dataSnapshot.getValue(HouseModel.class);

                // Check if updatedHouse is not null before updating the UI
                if (updatedHouse != null) {
                    holder.tv_location.setText(context.getString(R.string.houseLocation,model.getHouseLocation()));
                    holder.tv_rentPerRoom.setText(context.getString(R.string.rentPerRomm, model.getRentPerRoom()));
                    holder.tv_noOfRoom.setText(context.getString(R.string.numberOfRoom,model.getNoOfRoom()));
                    Glide.with(context).load(updatedHouse.getHouseImage()).into(holder.iv_houseImage);
                } else {
                    // Handle the case where updatedHouse is null
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        holder.tv_location.setText(context.getString(R.string.houseLocation,model.getHouseLocation()));
        holder.tv_rentPerRoom.setText(context.getString(R.string.rentPerRomm, model.getRentPerRoom()));
        holder.tv_noOfRoom.setText(context.getString(R.string.numberOfRoom,model.getNoOfRoom()));
        Glide.with(context).load(model.getHouseImage()).into(holder.iv_houseImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    HouseModel model = houseModelArrayList.get(position);
                    Intent intent = new Intent(context, HouseDetails.class);

                    intent.putExtra("houseId", model.getHouseId());
                    intent.putExtra("noOfRoom", model.getNoOfRoom());
                    intent.putExtra("rentPerRoom", model.getRentPerRoom());
                    intent.putExtra("houseDescription", model.getHouseDescription());
                    intent.putExtra("houseLocation", model.getHouseLocation());
                    intent.putExtra("houseImage", model.getHouseImage());
                    intent.putExtra("userId", model.getUserId());
                    intent.putExtra("search", model.getSearch());

                    context.startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    HouseModel model = houseModelArrayList.get(position);
                    new AlertDialog.Builder(context)
                            .setTitle("Do you want to delete this house?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Delete the house from Firebase
                                    DatabaseReference housesRef = FirebaseDatabase.getInstance().getReference()
                                            .child(RegisterOwner.HOUSES).child(model.getUserId()).child(model.getHouseId());
                                    housesRef.removeValue();

                                    // Get reference to members node
                                    DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference()
                                            .child("members").child(model.getHouseId());

                                    // Get the list of memberIds
                                    membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ArrayList<String> memberIds = new ArrayList<>();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                memberIds.add(snapshot.getKey());
                                            }

                                            // Delete the houseId from the members node
                                            membersRef.removeValue();

                                            // Check each user and remove the houseId if it matches
                                            for (String memberId : memberIds) {
                                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                                        .child("Users").child(memberId);
                                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        MemberModel member = dataSnapshot.getValue(MemberModel.class);
                                                        if (member != null && model.getHouseId().equals(member.getHouseId())) {
                                                            userRef.child("houseId").removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Handle possible errors.
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle possible errors.
                                        }
                                    });

                                    // Remove the house from the adapter's data set
                                    houseModelArrayList.remove(position);
                                    notifyItemRemoved(position);

                                    // Show a success message
                                    Toast.makeText(context, "Delete rental house success", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            }
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

