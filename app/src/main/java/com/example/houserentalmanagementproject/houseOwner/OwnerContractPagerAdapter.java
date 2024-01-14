package com.example.houserentalmanagementproject.houseOwner;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OwnerContractPagerAdapter extends FragmentStateAdapter {

    private String selectedHouseName = "Select a house";;

    public OwnerContractPagerAdapter(@NonNull OwnerContract activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create a bundle to hold the selected house name
        Bundle bundle = new Bundle();
        bundle.putString("selectedHouseName", selectedHouseName);

        // Create the appropriate fragment
        Fragment fragment;
        if (position == 0) {
            fragment = new ActiveContractFragment();
            Log.d("OwnerContractPagerAdapter", "Creating ActiveContractFragment");
        } else {
            fragment = new TerminatedContractFragment();
            Log.d("OwnerContractPagerAdapter", "Creating TerminatedContractFragment");
        }

        // Set the arguments bundle to the fragment
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void updateSelectedHouseName(String newSelectedHouseName) {
        this.selectedHouseName = newSelectedHouseName;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        // Return a unique ID for each item
        return selectedHouseName.hashCode() + position;
    }

    @Override
    public boolean containsItem(long itemId) {
        // Check if an item with the given ID exists
        int position = (int) (itemId - selectedHouseName.hashCode());
        return position >= 0 && position < getItemCount();
    }
}






