package com.example.kasicaprasica.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.hardware.input.InputManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.User;

import java.util.ArrayList;

public class StartFragment extends Fragment {

    private Spinner spinn;
    private User u;
    private EditText username;
    private EditText password;
    DatabaseHelper helper;

    private MediaPlayer player;

    private ArrayList<MyCurrency> listaValuta;
    private ArrayList<String> listaValutaString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //provjerimo ako postoji account ako da idemo na main fragment ako ne ostajemo na start fragmentu da napravimo račun
        helper = new DatabaseHelper(getContext());
        Cursor c = helper.getUserById(1);
        if(c.getCount() != 0) {
            c.close();
            NavHostFragment.findNavController(StartFragment.this).navigate(R.id.action_startFragment_to_mainFragment);
        }

        player = MediaPlayer.create(getContext(), R.raw.button_sound);

        username = view.findViewById(R.id.username_input);
        password = view.findViewById(R.id.password_input);

        spinn = getView().findViewById(R.id.default_value_input);
        // Create an ArrayAdapter using the string array and a default spinner layout
        listaValuta = new ArrayList<MyCurrency>();
        listaValutaString = new ArrayList<String>();
        Cursor ci = helper.getAllCurrencies();
        if(ci.getCount() > 0) {
            while (ci.moveToNext()) {
                MyCurrency myCurrency = new MyCurrency();
                myCurrency.setId(ci.getInt(0));
                myCurrency.setName(ci.getString(1));
                myCurrency.setCode(ci.getString(2));
                myCurrency.setSymbol(ci.getString(3));
                listaValuta.add(myCurrency);
                listaValutaString.add(myCurrency.getName());
            }
        }
        ci.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listaValutaString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinn.setAdapter(adapter);

        spinn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                username.setSelected(false);
                password.setSelected(false);
                spinn.performClick();
                closeKeyboard();
                return false;
            }
        });


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeKeyboard();
                return false;
            }
        });


        view.findViewById(R.id.create_acc_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                String ime;
                String pass;
                String defValue;
                ime = username.getText().toString();
                pass = password.getText().toString();
                defValue = spinn.getSelectedItem().toString();
                MyCurrency mc = helper.getCurrencyByName(defValue);

                if(ime.trim().isEmpty() && pass.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Username and password can not be empty", Toast.LENGTH_SHORT).show();
                } else if(ime.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Username can not be empty", Toast.LENGTH_SHORT).show();
                } else if(pass.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Password can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    u = new User(ime,pass,mc.getId());
                    boolean isInserted = helper.insertUserData(u);
                    if(isInserted) {
                        Toast.makeText(getContext(), "Data is inserted!", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(StartFragment.this).navigate(R.id.action_startFragment_to_mainFragment);
                    }else {
                        Toast.makeText(getContext(), "Data is NOT inserted!", Toast.LENGTH_SHORT).show();
                    }
                }

                closeKeyboard();
            }
        });


    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
