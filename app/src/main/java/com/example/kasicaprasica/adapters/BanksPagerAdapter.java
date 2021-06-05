package com.example.kasicaprasica.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.interfaces.BanksRecyclerViewInterface;
import com.example.kasicaprasica.models.Bank;

import java.util.ArrayList;

public class BanksPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Bank> banks;
    private ArrayList<Bitmap> banksBitmapImage;
    private Context context;
    private DatabaseHelper helper;
    private BanksRecyclerViewInterface banksRecyclerViewInterface;

    public BanksPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return null;
    }

    

    @Override
    public int getItemCount() {
        return banks.size();
    }
}
