package com.example.kasicaprasica.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ConcatAdapter;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.adapters.MyAdapter;
import com.example.kasicaprasica.adapters.MyAdapterHeader;
import com.example.kasicaprasica.adapters.MyAdapterWithTotalValue;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.interfaces.BankStatusInterface;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.models.BankCurrencyConnect;
import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.Pictures;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatusDetailsFragment extends Fragment {

    private int id;
    private MyCurrency myCurrency;

    private int bank_id;
    private Bank bank;

    private Pictures pictures;
    private TextView text;

    ArrayList<Pictures> listaSlika;
    ArrayList<Pictures> listaSlikaForAdapter;
    ArrayList<String> listaStringova;
    ArrayList<Integer> listaAmountova;
    ArrayList<Double> listOfTotalValues;
    ArrayList<BankCurrencyConnect> bccList;

    DatabaseHelper helper;
    MyAdapterHeader adapterHeader;
    MyAdapterWithTotalValue adapterWithTotalValue;
    RecyclerView recyclerView;

    private Runnable r;
    ExecutorService executorService;
    Handler mHandler;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        id = bundle.getInt("id");
        bank_id = bundle.getInt("bank_id");
        return inflater.inflate(R.layout.fragment_status_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.status_details_progres_bar_circle);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.status_detalji_reciklaza);
        recyclerView.setVisibility(View.GONE);

        text = view.findViewById(R.id.naslov_status_detalji);

        executorService = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());
        r = () -> loadScreen(view, savedInstanceState);
        mHandler.postDelayed(r, 400);
    }

    private void loadScreen(View view, Bundle savedInstanceState) {
        helper = new DatabaseHelper(getContext());

        pictures = helper.getPictureById(id);

        myCurrency = helper.getCurrencyById(pictures.getId_curr());

        listaStringova = new ArrayList<String>();
        listaAmountova = new ArrayList<Integer>();
        bccList = new ArrayList<BankCurrencyConnect>();
        listaSlika = new ArrayList<Pictures>();
        listaSlika = helper.getPicturesByCurrency(myCurrency);
        listaSlikaForAdapter = new ArrayList<Pictures>();
        listOfTotalValues = new ArrayList<Double>();

        /*for(Pictures p : listaSlika) {
            listaStringova.add("x 0");
        }*/

        bank = new Bank();
        Cursor bankCursor = helper.getOneBank(bank_id);
        if(bankCursor.getCount() > 0) {
            bankCursor.moveToFirst();
            bank.setId(bankCursor.getInt(0));
            bank.setBankName(bankCursor.getString(1));
            bank.setDateOfCreation(bankCursor.getString(2));
            bank.setImagePath(bankCursor.getString(3));
        }
        bankCursor.close();


        text.setText(myCurrency.getName());
        //text.setText(String.valueOf(bank_id));

        Cursor c = helper.getAllMoneyByBankAndCurrency(bank, myCurrency);
        if(c.getCount() > 0) {
            while (c.moveToNext()) {
                BankCurrencyConnect bcc = new BankCurrencyConnect();
                bcc.setId(c.getInt(0));
                bcc.setId_bank(c.getInt(1));
                bcc.setId_curr(c.getInt(2));
                bcc.setId_type(c.getInt(3));
                bcc.setId_money_pic(c.getInt(4));
                bcc.setAmount(c.getInt(5));
                bcc.setDate(c.getString(6));
                bccList.add(bcc);
            }
        }

        c.close();

        /*BankCurrencyConnect previousItem = new BankCurrencyConnect();*/
        int locAmount = 0;
        /*int velicina = bccList.size();
        ArrayList<Integer> odigrani = new ArrayList<Integer>();*/

        for(Pictures p : listaSlika) {
            locAmount = 0;
            for(BankCurrencyConnect bccLocal : bccList) {
                if(p.getId() == bccLocal.getId_money_pic()) {
                    locAmount += bccLocal.getAmount();
                }
            }
            if(locAmount > 0) {
                String stavi = String.valueOf(locAmount);
                listaStringova.add(stavi);
                listaSlikaForAdapter.add(p);
                listOfTotalValues.add(p.getValue() * locAmount);
            }
        }



        adapterHeader = new MyAdapterHeader(getContext());
        adapterWithTotalValue = new MyAdapterWithTotalValue(getContext(), listaStringova, listaSlikaForAdapter, listOfTotalValues, null);

        ConcatAdapter concatAdapter = new ConcatAdapter(adapterHeader, adapterWithTotalValue);

        recyclerView.setAdapter(concatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        executorService.shutdown();
    }
}
