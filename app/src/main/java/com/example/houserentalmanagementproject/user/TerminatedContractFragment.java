package com.example.houserentalmanagementproject.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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
import com.example.houserentalmanagementproject.adapter.ContractMemberAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TerminatedContractFragment extends Fragment {
    Context context;
    private ContractMemberAdapter adapter;
    private List<Contract> contracts;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminated_contract, container, false);
        context = getContext();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        contracts = new ArrayList<>();
        adapter = new ContractMemberAdapter(context, contracts, true);
        recyclerView.setAdapter(adapter);

        // Initialize and show the ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String memberId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference memberContractsRef = FirebaseDatabase.getInstance().getReference().child("memberContract").child(memberId);
        memberContractsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contracts.clear();
                for (DataSnapshot contractSnapshot : dataSnapshot.getChildren()) {
                    String contractId = contractSnapshot.getKey();
                    DatabaseReference contractRef = memberContractsRef.child(contractId);
                    contractRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Contract contract = dataSnapshot.getValue(Contract.class);
                            if ("terminated".equals(contract.getStatus())) {
                                contracts.add(contract);
                            }
                            adapter.notifyDataSetChanged();

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


