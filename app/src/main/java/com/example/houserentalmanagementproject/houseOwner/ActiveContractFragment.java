package com.example.houserentalmanagementproject.houseOwner;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.Contract;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.ContractAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveContractFragment extends Fragment {
    private Context context;
    private ContractAdapter adapter;
    private List<Contract> contracts;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_contract, container, false);

        context = getContext(); // Initialize the context

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        contracts = new ArrayList<>();
        adapter = new ContractAdapter(context, contracts, false);
        recyclerView.setAdapter(adapter);

        // Initialize and show the ProgressDialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference housesRef = FirebaseDatabase.getInstance().getReference().child("houses").child(ownerId);
        housesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the selected house name from the spinner
                OwnerContract activity = (OwnerContract) getActivity();
                String selectedHouseName = activity.getSelectedHouseName();
                Log.d("ActiveContractFragment", "Selected House Name: " + selectedHouseName);

                // Find the house ID that corresponds to the selected house name
                String selectedHouseId = null;
                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
                    if (selectedHouseName.equals(houseSnapshot.child("search").getValue(String.class))) {
                        selectedHouseId = houseSnapshot.getKey();
                        break;
                    }
                }

                // If a matching house ID was found, fetch its contracts
                if (selectedHouseId != null) {
                    DatabaseReference contractsRef = FirebaseDatabase.getInstance().getReference().child("ownerContracts").child(selectedHouseId);
                    contractsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Contract> newContracts = new ArrayList<>();
                            for (DataSnapshot contractSnapshot : dataSnapshot.getChildren()) {
                                Contract contract = contractSnapshot.getValue(Contract.class);
                                if ("active".equals(contract.getStatus())) {
                                    newContracts.add(contract);
                                    Log.d("ActiveContractFragment", "Added Contract: " + contract.toString());
                                }
                            }
                            adapter.updateData(newContracts);

                            // Dismiss the ProgressDialog when the data is fully loaded
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Dismiss the ProgressDialog in case of an error
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    // If no matching house ID was found, dismiss the ProgressDialog
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Dismiss the ProgressDialog in case of an error
                progressDialog.dismiss();
            }
        });

        return view;
    }
}












