package com.example.houserentalmanagementproject.houseOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalmanagementproject.MemberModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.adapter.SeeMemberAdapterOwner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewMembers extends AppCompatActivity {
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    String houseId;
    private RecyclerView rv_showAllFood;
    private SeeMemberAdapterOwner adapter;
    private final ArrayList<MemberModel> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);

        Intent intent = getIntent();
        houseId = intent.getStringExtra("houseId");

        rv_showAllFood = findViewById(R.id.recyclerView);
        rv_showAllFood.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewMembers.this);
        rv_showAllFood.setLayoutManager(linearLayoutManager);
        getAllArticle();
    }

    private void getAllArticle() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        if (firebaseUser.getUid() != null) {
            reference = FirebaseDatabase.getInstance().getReference().child(RegisterOwner.MEMBERS).child(houseId);
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MemberModel article = dataSnapshot.getValue(MemberModel.class);
                        mList.add(article);
                    }
                    if (!mList.isEmpty()) {
                        Log.d("TAG1", "onDataChange: " + mList.get(0).getName());
                        adapter = new SeeMemberAdapterOwner(ViewMembers.this, mList, userId, houseId);
                        rv_showAllFood.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "There is no member in this house", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(0,0);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.addValueEventListener(valueEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reference != null && valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
    }
}
