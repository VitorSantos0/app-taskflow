package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RoutinePagerAdapter extends FragmentStateAdapter {

    public RoutinePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new DiaFragment();
            case 1: return new SemanaFragment();
            default: return new MesFragment();
        }
    }

    @Override
    public int getItemCount() { return 3; }
}