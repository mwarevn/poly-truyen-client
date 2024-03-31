package com.example.poly_truyen_client.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.poly_truyen_client.ui.popular_pager.PopularFragment;

public class AdapterViewPagerTopPopular extends FragmentStateAdapter {

    public AdapterViewPagerTopPopular(@NonNull FragmentManager fragmentActivity, Lifecycle lifecycle) {
        super(fragmentActivity, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PopularFragment("day");
            case 1:
                return new PopularFragment("week");
            case 2:
                return new PopularFragment("month");
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
