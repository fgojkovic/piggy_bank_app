package com.example.kasicaprasica.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kasicaprasica.R;
//import androidx.navigation.fragment.NavHostFragment;

public class MainFragment extends Fragment {

    private MediaPlayer player;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String barTitle = "Piggy Bank";
        assert actionBar != null;
        actionBar.setTitle(barTitle);
        actionBar.setDisplayHomeAsUpEnabled(false);
        int bisque = ContextCompat.getColor(getContext(), R.color.Bisque);
        setActionbarTextColor(actionBar, bisque);*/

        player = MediaPlayer.create(getContext(), R.raw.button_sound);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_mainFragment_to_entryFragment);
                //stopPlayer();
            }
        });

        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_mainFragment_to_paymentFragment);
            }
        });

        view.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_mainFragment_to_statusFragment);
            }
        });

        view.findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_mainFragment_to_savingsFragment);
            }
        });

        view.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_mainFragment_to_accountFragment2);
            }
        });

        view.findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_mainFragment_to_userSettingsFragment);
            }
        });



    }



    private void stopPlayer() {
        if(player != null) {
            player.release();
            player = null;
            Toast.makeText(getContext(), "Media player released!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setActionbarTextColor(ActionBar actBar, int color) {

        String title = String.valueOf(actBar.getTitle());
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);

    }

}
