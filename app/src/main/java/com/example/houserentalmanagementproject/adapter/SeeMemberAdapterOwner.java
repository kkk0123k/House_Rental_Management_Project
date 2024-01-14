package com.example.houserentalmanagementproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.MemberModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.houseOwner.CreateBill;
import com.example.houserentalmanagementproject.houseOwner.RegisterOwner;
import com.example.houserentalmanagementproject.houseOwner.UpdateMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SeeMemberAdapterOwner extends RecyclerView.Adapter<SeeMemberAdapterOwner.viewholder> {
    String name, roomNumber, rentBill;
    private final String userId;
    private final String houseId;
    Context context;
    ArrayList<MemberModel> arrayList;

    public SeeMemberAdapterOwner(Context context, ArrayList<MemberModel> MemberModelArrayList, String userId, String houseId) {
        this.context = context;
        this.arrayList = MemberModelArrayList;
        this.userId = userId;
        this.houseId = houseId;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seemember, null, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        MemberModel model = arrayList.get(position);
        name = model.getName();
        roomNumber = model.getRoomNumber();
        rentBill = model.getRent();
        holder.tv_name.setText(context.getString(R.string.memberName, name));
        holder.tv_joiningDate.setText(context.getString(R.string.joiningDate, model.getJoiningDate()));
        holder.tv_phoneNumber.setText(context.getString(R.string.phoneNumber, model.getPhoneNumber()));
        holder.tv_age.setText(context.getString(R.string.Age, model.getAge()));
        holder.tv_roomNumber.setText(context.getString(R.string.roomNumber, roomNumber));
        holder.tv_memberRent.setText(context.getString(R.string.memberRent, rentBill));

        // Set OnClickListener for the ViewHolder
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu
                PopupMenu popup = new PopupMenu(context, v);
                // Inflate the menu from xml
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                // Set a listener for menu item click
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // Get the current position
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                            MemberModel model = arrayList.get(position);
                            switch (item.getItemId()) {
                                case R.id.update_member:
                                    // Handle "Update Member" action
                                    Intent intent = new Intent(context, UpdateMember.class);
                                    intent.putExtra("memberModel", model); // Pass the MemberModel object to the new activity
                                    context.startActivity(intent);
                                    break;
                                case R.id.delete_member:
                                    // Handle "Delete Member" action
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete Member?");
                                    builder.setMessage("Do you want to delete this member? This will also terminate the contract with this member. Do you still wish to continue?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // Get the contractId
                                                    DatabaseReference contractRef = FirebaseDatabase.getInstance().getReference().child("memberContract").child(model.getMemberId());
                                                    contractRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                String contractId = snapshot.getKey();
                                                                // Set the status to terminated in ownerContracts
                                                                DatabaseReference ownerContractRef = FirebaseDatabase.getInstance().getReference().child("ownerContracts").child(model.getHouseId()).child(contractId).child("status");
                                                                ownerContractRef.setValue("terminated");
                                                                // Set the status to terminated in memberContract
                                                                DatabaseReference memberContractRef = FirebaseDatabase.getInstance().getReference().child("memberContract").child(model.getMemberId()).child(contractId).child("status");
                                                                memberContractRef.setValue("terminated");

                                                                // After setting the status to terminated, delete the member
                                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(RegisterOwner.MEMBERS).child(model.getHouseId()).child(model.getMemberId());
                                                                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // After successfully deleting the member, proceed with deleting the houseId
                                                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getMemberId()).child("houseId");
                                                                            userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(context, "Member removed success", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Handle possible errors.
                                                        }
                                                    });
                                                }


                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // Handle the "No" button click
                                                    // Nothing to do here, the dialog will be dismissed automatically
                                                }
                                            });
                                    builder.create().show();
                                    break;
                            }
                        }
                        return true;
                    }
                });
                // Show the menu
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_joiningDate, tv_phoneNumber, tv_age, tv_roomNumber, tv_memberRent;
        Button btn_createBill;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_memberName);
            tv_joiningDate = itemView.findViewById(R.id.tv_memberJoiningDate);
            tv_phoneNumber = itemView.findViewById(R.id.tv_memberPhoneNumber);
            tv_roomNumber = itemView.findViewById(R.id.tv_roomNumber);
            tv_age = itemView.findViewById(R.id.tv_memberAge);
            tv_memberRent = itemView.findViewById(R.id.tv_memberRent);

            btn_createBill = itemView.findViewById(R.id.btn_createBill);
            btn_createBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the current position
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                        MemberModel model = arrayList.get(position);

                        Intent intent = new Intent(context, CreateBill.class);
                        intent.putExtra("name", model.getName());
                        intent.putExtra("roomNumber", model.getRoomNumber());
                        intent.putExtra("rentHouse", model.getRent());
                        intent.putExtra("userId", model.getOwnerId());
                        intent.putExtra("houseId", model.getHouseId());
                        intent.putExtra("memberId", model.getMemberId()); // Pass the memberId to the new activity

                        context.startActivity(intent);
                    }
                }
            });

        }
    }
}
