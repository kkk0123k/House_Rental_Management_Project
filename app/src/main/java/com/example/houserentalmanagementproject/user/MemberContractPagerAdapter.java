package com.example.houserentalmanagementproject.user;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MemberContractPagerAdapter extends FragmentStateAdapter {

    public MemberContractPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ActiveContractFragment();
        } else {
            return new TerminatedContractFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}