package com.example.kasicaprasica.fragments;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.adapters.MyAdapter;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.dialogFragments.AlertDialogFragment;
import com.example.kasicaprasica.dialogFragments.SuccessEntryDialogFragment;
import com.example.kasicaprasica.interfaces.AlertDialogInterface;
import com.example.kasicaprasica.interfaces.MyInterface;
import com.example.kasicaprasica.interfaces.SuccessDialogInterface;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.models.BankCurrencyConnect;
import com.example.kasicaprasica.models.BankValueLog;
import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.Pictures;
import com.example.kasicaprasica.models.Types;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryFragment extends Fragment implements MyInterface, SuccessDialogInterface, AlertDialogInterface {

    private String valuta = " KN";
    private TextView zbrojTekst;

    private View myView;

    private int brojac = 0;
    private int brojacDrugi = 0;
    private int brojacTreci = 0;

    RecyclerView mRecycler;
    MyAdapter adaptercic;
    DatabaseHelper helper;

    //lista Banaka
    ArrayList<Bank> listaBanaka;
    ArrayList<String> listaBanakaString;

    ArrayList<MyCurrency> listaValuta;
    ArrayList<String> listaValutaString;

    ArrayList<Types> listaTipova;
    ArrayList<String> listaTipovaString;

    ArrayList<Pictures> listaSlika;
    ArrayList<Integer> listaSlikaInt;

    ArrayList<String> listaStringova;

    private MyCurrency mc;
    private Types types;
    private Bank selectedBank;

    private MediaPlayer player;
    private MediaPlayer player_coin;
    private boolean playCoinSound = false;

    private boolean firstClick = false;

    private Runnable r;
    ExecutorService executorService;
    Handler mHandler;

    private ProgressBar progressBar;
    private Button entryButton;

    //A bit more modern spinners using material.design library
    private TextInputLayout bankSelection;
    private AutoCompleteTextView autoCompleteBankSelection;

    private TextInputLayout typeSelection;
    private AutoCompleteTextView autoCompleteTypeSelection;

    private TextInputLayout currencySelection;
    private AutoCompleteTextView autoCompleteCurrencySelection;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        /*outState.putInt("spinner2", autoCompleteCurrencySelection.g());
        outState.putInt("spinner", autoCompleteTypeSelection.getSelectedItemPosition());*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setUpActionBar();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = view.findViewById(R.id.entry_progres_bar_circle);

        bankSelection = view.findViewById(R.id.entry_bank_selection);
        autoCompleteBankSelection = view.findViewById(R.id.auto_complete_bank_selection);

        typeSelection = view.findViewById(R.id.entry_type_selection);
        autoCompleteTypeSelection = view.findViewById(R.id.auto_complete_type_selection);

        currencySelection = view.findViewById(R.id.entry_currency_selection);
        autoCompleteCurrencySelection = view.findViewById(R.id.auto_complete_currency_selection);

        entryButton = view.findViewById(R.id.entry_confirm_button);

        myView = view;
        mRecycler = view.findViewById(R.id.reciklaza);
        //mRecycler.setNestedScrollingEnabled(false);
        //mRecycler.setAdapter(adapter);
        zbrojTekst = view.findViewById(R.id.zbrojTekst);

        loading("begin");

        executorService = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());
        r = () -> loadScreen(view, savedInstanceState);
        mHandler.postDelayed(r, 400);
    }

    private void loading(String begin) {
        if(begin.equals("begin")) {
            progressBar.setVisibility(View.VISIBLE);
            bankSelection.setVisibility(View.GONE);
            typeSelection.setVisibility(View.GONE);
            currencySelection.setVisibility(View.GONE);;
            //odabirBanke.setVisibility(View.GONE);
            mRecycler.setVisibility(View.GONE);
            zbrojTekst.setVisibility(View.GONE);
            entryButton.setVisibility(View.GONE);
        }else {
            //Set all visible
            progressBar.setVisibility(View.GONE);
            bankSelection.setVisibility(View.VISIBLE);
            typeSelection.setVisibility(View.VISIBLE);
            currencySelection.setVisibility(View.VISIBLE);
            entryButton.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.VISIBLE);
            zbrojTekst.setVisibility(View.VISIBLE);
            executorService.shutdown();
        }
    }

    private void loadScreen(View view, Bundle savedInstanceState) {
        helper = new DatabaseHelper(getContext());

        mc = helper.getDefaultCurrency();

        mc = helper.getDefaultCurrency();
        types = helper.getTypeByName("Coin");

        listaStringova = new ArrayList<String>();
        listaSlikaInt = new ArrayList<Integer>();
        listaSlika = helper.getStartingPictures();
        for(Pictures p : listaSlika) {
            listaSlikaInt.add(p.getLocation());
            String s = "x 0";
            listaStringova.add(s);
        }


        adaptercic = new MyAdapter(getContext(), listaStringova, listaSlika, this);
        mRecycler.setAdapter(adaptercic);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        namjestiSpinnere();

        /*if(savedInstanceState != null) {
            spinn2.setSelection(savedInstanceState.getInt("spinner2"));
        }*/

        String nesto = autoCompleteCurrencySelection.getText().toString();



        namjestiZbrojTekst();

        player = MediaPlayer.create(getContext(), R.raw.button_sound);
        player_coin = MediaPlayer.create(getContext(), R.raw.coin_sound);
        player_coin.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                playCoinSound = true;
            }
        });


        entryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstClick = true;
                player.start();
                displayDialog();
            }
        });


        autoCompleteBankSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBank = listaBanaka.get(position);
            }
        });


        autoCompleteTypeSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tmp = (TextView) view;
                int odakle = 1;
                String text = "";
                if(tmp == null) {
                    //text = spinn.getItemAtPosition(savedInstanceState.getInt("spinner")).toString();
                } else {
                    text = tmp.getText().toString();
                }
                selectajSlike(text, odakle);
            }
        });

        autoCompleteCurrencySelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tmp = (TextView) view;
                int odakle = 2;
                String text = "";
                if(tmp == null) {
                    //text = spinn2.getItemAtPosition(savedInstanceState.getInt("spinner2")).toString();
                } else {
                    text = tmp.getText().toString();
                }
                selectajSlike(text, odakle);
                namjestiZbrojTekst();
            }
        });

        if(!executorService.isTerminated()) {
            loading("done");
        }

    }


    private void selectajSlike(String currency, int odakle) {
        //Log.e("Curency", currency);
        if(firstClick) {
            adaptercic.clearListCounters();
        }


        if(odakle == 2) {
            mc = helper.getCurrencyByName(currency);
            types = helper.getTypeByName(autoCompleteTypeSelection.getText().toString());
        }else if(odakle == 1) {
            mc = helper.getCurrencyByName(autoCompleteCurrencySelection.getText().toString());
            types = helper.getTypeByName(currency);
        }

        String tekst = "x 0";
        Cursor c = helper.getPicturesByCurrAndType(mc,types);
        if(c.getCount() != 0) {
            listaSlika.clear();
            listaSlikaInt.clear();
            listaStringova.clear();
            while(c.moveToNext()) {
                Pictures p = new Pictures();
                p.setId(c.getInt(0));
                p.setId_curr(c.getInt(1));
                p.setLocation(c.getInt(2));
                p.setId_type(c.getInt(3));
                p.setValue(c.getDouble(4));
                listaSlika.add(p);
                listaSlikaInt.add(p.getLocation());
                listaStringova.add(tekst);
            }
        } else {
            Toast.makeText(getContext(), mc.getSymbol() + " : " + types.getName(), Toast.LENGTH_LONG).show();
        }
        c.close();
        adaptercic.popuniListuBrojacaIVrijednosti();
        adaptercic.notifyDataSetChanged();

    }

    private void unesiNovacUBanku() {
        String spin1Item = autoCompleteTypeSelection.getText().toString();
        String drugiSpinTekst = autoCompleteCurrencySelection.getText().toString();

        Bank b = selectedBank;
        MyCurrency myCurrency = helper.getCurrencyByName(drugiSpinTekst);
        Types types = helper.getTypeByName(spin1Item);
        Double value = Double.parseDouble(zbrojTekst.getText().toString().split("\\s+")[0]);

        ArrayList<Integer> listaBrojaca = new ArrayList<Integer>();
        listaBrojaca = adaptercic.getListaBrojaca();

        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy.", Locale.UK);
        Date date = new Date(yourmilliseconds);
        String time = sdf.format(date);

        boolean isInserted = helper.insertMoneyInBank(b, myCurrency, types, listaSlika, listaBrojaca, time);
        if (isInserted) {
            Toast.makeText(getContext(), "Data is inserted!", Toast.LENGTH_SHORT).show();
            // Počisti cijeli layout
            adaptercic.clearListCounters();
            adaptercic.popuniListuBrojacaIVrijednosti();
            adaptercic.notifyDataSetChanged();
            namjestiZbrojTekst();
            updateBankLog(b, myCurrency, yourmilliseconds);
        } else {
            Toast.makeText(getContext(), "Data is NOT inserted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDialog() {
        long ne = listaBanaka.indexOf(selectedBank);
        boolean isEmpty = true;

        ArrayList<Integer> lb = adaptercic.getListaBrojaca();
        for (Integer i : lb) {
            if (i != 0) {
                isEmpty = false;
                break;
            }
        }
        if (ne < 0) {
            //moj alert dialog
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString("title", "Warning!");
            arguments.putString("message", "You need to create a bank first.");
            alertDialogFragment.setArguments(arguments);
            alertDialogFragment.setTargetFragment(this, 0);
            alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");
        } else if (isEmpty) {
            //moj alert dialog
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString("title", "Warning!");
            arguments.putString("message", "You haven't selected any coins.");
            alertDialogFragment.setArguments(arguments);
            alertDialogFragment.setTargetFragment(this, 0);
            alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");
        } else {
            //MOj success dialog
            SuccessEntryDialogFragment dialogFragment = new SuccessEntryDialogFragment();
            dialogFragment.setTargetFragment(this, 0);
            Bundle args = new Bundle();
            args.putString("title", "Inserting money!");
            args.putString("message", "You will insert " + zbrojTekst.getText());
            args.putString("positiveButton", "Insert");
            args.putString("negativeButton", "Cancel");
            dialogFragment.setArguments(args);
            dialogFragment.show(getParentFragmentManager(), "SuccessEntryDialogFragment");
        }


        //moj defaultni
        /*AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");*/
    }



    private void namjestiSpinnere() {
        listaTipova = new ArrayList<Types>();
        listaTipovaString = new ArrayList<String>();
        Cursor cursic = helper.getAllTypes();
        if(cursic.getCount() > 0) {
            while (cursic.moveToNext()) {
                Types tip = new Types();
                tip.setId(cursic.getInt(0));
                tip.setName(cursic.getString(1));
                listaTipova.add(tip);
                listaTipovaString.add(tip.getName());
            }
        }
        cursic.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listaTipovaString);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTypeSelection.setAdapter(adapter);
        autoCompleteTypeSelection.setText(types.getName(), false);

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
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listaValutaString);
        //adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteCurrencySelection.setAdapter(adapter2);
        autoCompleteCurrencySelection.setText(mc.getName(), false);


        /*int position = listaValutaString.indexOf(mc.getName());
        autoCompleteCurrencySelection.setText("Hrvatska Kuna");*/

        listaBanaka = new ArrayList<Bank>();
        listaBanakaString = new ArrayList<String>();
        Cursor cursor = helper.getAllBanks();
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                Bank b = new Bank();
                b.setId(cursor.getInt(0));
                b.setBankName(cursor.getString(1));
                b.setDateOfCreation(cursor.getString(2));
                //b.setValue("Defaultni iznos u funkcijij");
                b.setImagePath(cursor.getString(3));
                listaBanaka.add(b);
                listaBanakaString.add(b.getBankName());
            }
        }
        cursor.close();
        /*ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listaBanakaString);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        odabirBanke.setAdapter(adapter3);*/

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listaBanakaString);
        autoCompleteBankSelection.setAdapter(adapter3);
        //adapter3.setDropDownViewResource();
        if(listaBanaka.size() > 0) {
            autoCompleteBankSelection.setText(listaBanaka.get(0).getBankName(), false);
            selectedBank = listaBanaka.get(0);
        }

        selectajSlike(types.getName(), 1);
    }

    private void namjestiZbrojTekst() {
        String pocetni = "0.0 " + mc.getSymbol();
        zbrojTekst.setText(pocetni);
    }

    //Function for updating bank log for chart data
    private void updateBankLog(Bank b, MyCurrency currency, long timeInMilis) {
        //Calculate total value of the bank
        double value = 0;
        ArrayList<BankCurrencyConnect> bccList = new ArrayList<>();
        Cursor c = helper.getAllMoneyByBank(b);
        if(c.getCount() > 0) {
            while (c.moveToNext()) {
                BankCurrencyConnect bcc = new BankCurrencyConnect();
                if(currency.getId() == c.getInt(2)) {
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
        }
        c.close();

        for(BankCurrencyConnect bccLocal : bccList) {
            Pictures p = new Pictures();
            p = helper.getPictureById(bccLocal.getId_money_pic());
            value += bccLocal.getAmount() * p.getValue();
        }

        BankValueLog bankValueLog = new BankValueLog(b.getId(), currency.getId(), value, String.valueOf(timeInMilis));
        boolean isInserted = helper.insertBankValueLog(bankValueLog);
        if(!isInserted) {
            Toast.makeText(getContext(), "Bank value log NOT inserted", Toast.LENGTH_SHORT).show();
        }
    }


    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String barTitle = "Entry";
        assert actionBar != null;
        actionBar.setTitle(barTitle);
        int bisque = ContextCompat.getColor(getContext(), R.color.Bisque);
        setActionbarTextColor(actionBar, bisque);
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    private void setActionbarTextColor(ActionBar actBar, int color) {
        String title = String.valueOf(actBar.getTitle());
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);
    }



    @Override
    public void interfaceMethod(int pos, ArrayList<Integer> lista, ArrayList<Double> values) {
        firstClick = true;
        if(player_coin.isPlaying()) {
            player_coin.seekTo(0);
        }else if(playCoinSound){
            player_coin.start();
        }

        double br = 0;
        int lmt = lista.size();
        for(int i = 0; i < lmt; i++) {
            br += lista.get(i) * values.get(i);
        }

        if(zbrojTekst == null) {
            zbrojTekst = myView.findViewById(R.id.zbrojTekst);
        }
        String txt = br + " " + mc.getSymbol();
        zbrojTekst.setText(txt);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        player.start();
        unesiNovacUBanku();
        //dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        player.start();
        dialog.dismiss();
    }

    @Override
    public void onOkClick(DialogFragment dialogFragment) {
        player.start();
        dialogFragment.dismiss();
    }
}


