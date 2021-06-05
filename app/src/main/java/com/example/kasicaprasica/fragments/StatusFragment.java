package com.example.kasicaprasica.fragments;

import android.app.VoiceInteractor;
import android.content.ClipData;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.adapters.BankStatusAdapter;
import com.example.kasicaprasica.adapters.MyAdapter;
import com.example.kasicaprasica.adapters.MyHorizontalAdapter;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.interfaces.BankStatusInterface;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.models.BankCurrencyConnect;
import com.example.kasicaprasica.models.ConversionRatesModel;
import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.Pictures;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatusFragment extends Fragment implements BankStatusInterface {

    private Spinner spinner;
    private Bank bank;
    private boolean izSelecta = false;
    private MediaPlayer player;

    //lista Banaka
    ArrayList<Bank> listaBanaka;
    ArrayList<String> listaBanakaString;

    //Lista slika
    ArrayList<Pictures> listaSlika;
    ArrayList<Pictures> listaSlikaZaPoslati;

    //Lista svih valuta i vrijednosti
    ArrayList<BankCurrencyConnect> bccList;
    ArrayList<Integer> bccAmounts;
    ArrayList<Double> bccValues;
    ArrayList<Double> bccTotalValues;

    RecyclerView mRecycler;
    ViewPager2 viewPager2;

    BankStatusAdapter bankStatusAdapter;
    MyHorizontalAdapter myHorizontalAdapter;
    DatabaseHelper helper;

    private String baseCurrency;
    private MyCurrency myBaseCurrency;
    private MyCurrency myConvertCurrrency;

    ArrayList<Double> exchangeRateDoubleList;
    ArrayList<String> exchangeRateStringList;
    ArrayList<String> exchangeRateStringListFirst;
    ArrayList<ConversionRatesModel> conversionRatesModelArrayList;

    private JSONArray jsonArray;
    private JSONObject jsonObject;

    private TextView naslov;
    private TextView naslovAllCurrencies;
    private TextView label1;
    private LinearLayout bankSpinnerRow;

    private TextInputLayout bankSelection;
    private AutoCompleteTextView autoCompleteBankSelection;

    private ProgressBar progressBar;

    private Runnable r;
    ExecutorService executorService;
    Handler mHandler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setUpActionBar();

        //naslov = view.findViewById(R.id.naslov_status);
        naslovAllCurrencies = view.findViewById(R.id.status_naslov_all_currency);
        naslovAllCurrencies.setVisibility(View.GONE);

        bankSpinnerRow = view.findViewById(R.id.status_filter_red);
        bankSpinnerRow.setVisibility(View.GONE);

        bankSelection = view.findViewById(R.id.status_bank_selection);
        autoCompleteBankSelection = view.findViewById(R.id.status_auto_complete_bank_selection);

        progressBar = view.findViewById(R.id.status_progres_bar_circle);
        progressBar.setVisibility(View.VISIBLE);

        executorService = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());

        r = new Runnable() {
            @Override
            public void run() {
                loadScreen(view, savedInstanceState);
                /*if (finished) {
                    executorService.shutdown();
                    progressBar.setVisibility(View.GONE);
                    mRecycler.setVisibility(View.VISIBLE);
                    viewPager2.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                    label1.setVisibility(View.VISIBLE);
                    naslovAllCurrencies.setVisibility(View.VISIBLE);
                }*/
            }
        };
        mHandler.postDelayed(r, 400);
        //mHandler.post(r);
        //mHandler.postAtTime(r, 2000);

    }

    private void loadScreen(View view, Bundle savedInstanceState) {



        //contentLoadingProgressBar = view.findViewById(R.id.status_content_progres_bar_circle);
        //contentLoadingProgressBar.onAttachedToWindow();
        //contentLoadingProgressBar.show();

        mRecycler = view.findViewById(R.id.status_reciklaza);
        mRecycler.setNestedScrollingEnabled(false);
        viewPager2 = view.findViewById(R.id.status_pager);
        viewPager2.setNestedScrollingEnabled(false);

        helper = new DatabaseHelper(getContext());

        player = MediaPlayer.create(getContext(), R.raw.button_sound);

        //Banke
        listaBanaka = new ArrayList<Bank>();
        listaBanakaString = new ArrayList<String>();

        //Slike
        listaSlika = new ArrayList<Pictures>();
        listaSlika = helper.getStartingSymbols();
        listaSlikaZaPoslati = new ArrayList<Pictures>();


        //Vrijednosti
        bccValues = new ArrayList<Double>();
        bccTotalValues = new ArrayList<Double>();
        bccAmounts = new ArrayList<Integer>();
        bccList = new ArrayList<BankCurrencyConnect>();

        exchangeRateDoubleList = new ArrayList<Double>();
        exchangeRateStringList = new ArrayList<String>();
        exchangeRateStringListFirst = new ArrayList<String>();
        conversionRatesModelArrayList = new ArrayList<ConversionRatesModel>();

        myBaseCurrency = helper.getDefaultCurrency();
        baseCurrency = myBaseCurrency.getCode();
        for(Pictures p : listaSlika) {
            if(p.getId_curr() != myBaseCurrency.getId()) {
                MyCurrency tmp = helper.getCurrencyById(p.getId_curr());
                exchangeRateStringListFirst.add(tmp.getCode());
            }
        }
        //euroCurrency = helper.getCurrencyByName("Euro");


        namjestiSpinner();

        //setValuesForBancCurrencyConnect();

        getConversionRates();

        autoCompleteBankSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tmp = (TextView) view;
                Bank b = listaBanaka.get(position);
                bank = b;
                if(izSelecta) {
                    izSelecta = false;
                } else {

                    ispisiStatusBanke(b);
                    bankStatusAdapter.notifyDataSetChanged();

                    //getApiResult();

                    if(exchangeRateStringListFirst.size() > 0) {
                        exchangeRateStringListFirst.clear();
                    }
                    for(Pictures p : listaSlikaZaPoslati) {
                        if(p.getId_curr() != myBaseCurrency.getId()) {
                            MyCurrency tmp_tmp = helper.getCurrencyById(p.getId_curr());
                            exchangeRateStringListFirst.add(tmp_tmp.getCode());
                        }
                    }

                    initializeWithdrawal(b);
                    myHorizontalAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void namjestiSpinner() {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listaBanakaString);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteBankSelection.setAdapter(adapter);
        if(listaBanakaString.size() > 0) {
            autoCompleteBankSelection.setText(listaBanakaString.get(0), false);
            bank = listaBanaka.get(0);
        }

    }

    private void ispisiStatusBanke(Bank bank) {
        //c.close();
        listaSlikaZaPoslati.clear();
        Cursor c2 = helper.getAllMoneyByBank(bank);
        bccList.clear();
        bccValues.clear();
        bccAmounts.clear();
        double result = 0;
        ArrayList<Pictures> nepotrebni = new ArrayList<Pictures>();
        if(c2.getCount() > 0) {
            while (c2.moveToNext()) {
                BankCurrencyConnect bcc = new BankCurrencyConnect();
                bcc.setId(c2.getInt(0));
                bcc.setId_bank(c2.getInt(1));
                bcc.setId_curr(c2.getInt(2));
                bcc.setId_type(c2.getInt(3));
                bcc.setId_money_pic(c2.getInt(4));
                bcc.setAmount(c2.getInt(5));
                bcc.setDate(c2.getString(6));
                bccList.add(bcc);
                bccAmounts.add(bcc.getAmount());
            }


            for(Pictures p : listaSlika) {
                boolean found = false;
                for(BankCurrencyConnect bc : bccList) {
                    if(p.getId_curr() == bc.getId_curr()) {
                        found = true;
                        break;
                    }
                }
                if(found)
                {
                    listaSlikaZaPoslati.add(p);
                }
            }

            BankCurrencyConnect previousItem = new BankCurrencyConnect();

            Double vrijednost = 0.0;
            int velicina = bccList.size();
            for(Pictures p : listaSlika) {
                vrijednost = 0.0;
                for(BankCurrencyConnect bcc: bccList) {
                    Pictures pic = new Pictures();
                    pic = helper.getPictureById(bcc.getId_money_pic());
                    if(p.getId_curr() == bcc.getId_curr() ) {
                        vrijednost += pic.getValue() * bcc.getAmount();
                    }
                }
                if(vrijednost != 0.0) {
                    bccValues.add(vrijednost);
                }

            }
        }

        c2.close();
    }

    private void initializeAdapter()  {
        if(listaBanaka.size()>0) {
            this.bank = listaBanaka.get(0);
        }

        ispisiStatusBanke(this.bank);
        initializeWithdrawal(this.bank);

        bankStatusAdapter = new BankStatusAdapter(getContext(), listaSlikaZaPoslati, this, bccValues);

        myHorizontalAdapter = new MyHorizontalAdapter(getContext(), listaSlikaZaPoslati, this, bccTotalValues);

        mRecycler.setAdapter(bankStatusAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        /*mRecyclerDva.setAdapter(bankStatusAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        //mRecyclerDva.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerDva.setLayoutManager(llm);*/



       /* viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);*/
        viewPager2.setAdapter(myHorizontalAdapter);

        /*CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(8));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1- Math.abs(position);
                page.setScaleY(0.8f + v * 0.2f);
            }
        });

        viewPager2.setPageTransformer(transformer);*/
        /*progressBar.setVisibility(View.GONE);
        label1.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        naslovAllCurrencies.setVisibility(View.VISIBLE);*/


        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        viewPager2.setVisibility(View.VISIBLE);
        /*spinner.setVisibility(View.VISIBLE);
        label1.setVisibility(View.VISIBLE);*/
        bankSpinnerRow.setVisibility(View.VISIBLE);
        naslovAllCurrencies.setVisibility(View.VISIBLE);
        executorService.shutdown();
    }

    private void initializeWithdrawal(Bank bank) {
        double rez;
        bccTotalValues.clear();
        for(Pictures p : listaSlikaZaPoslati) {
            rez = 0;
            for(BankCurrencyConnect bccLocal : bccList) {
                Pictures pictures = helper.getPictureById(bccLocal.getId_money_pic());
                if(p.getId_curr() == bccLocal.getId_curr()) {
                    rez += bccLocal.getAmount() * pictures.getValue();
                } else {
                    //pretvori valutu
                    myConvertCurrrency = helper.getCurrencyById(bccLocal.getId_curr());
                    MyCurrency newCurrency = helper.getCurrencyById(p.getId_curr());
                    rez += convert(bccLocal.getAmount() * pictures.getValue(), myConvertCurrrency, newCurrency);
                }
            }
            bccTotalValues.add(rez);
        }


        //calculateEachCurrencyTotal();
    }

    @Override
    public void onDetailsClick(int id) {
        player.start();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("bank_id", bank.getId());
        NavHostFragment.findNavController(StatusFragment.this).navigate(R.id.action_statusFragment_to_statusDetailsFragment, bundle);
    }

    private double convert(Double rez, MyCurrency myCurrency, MyCurrency myBaseCurrency) { // myBaseCurrency is currencie in which everything has to be converted to
        int brojacc = 0;
        String tmpCurrency;
        String convertToCurrency;
        convertToCurrency = myCurrency.getCode();
        tmpCurrency = myBaseCurrency.getCode();

        Double multiplier = 1.0;
        if(!tmpCurrency.equals(baseCurrency)) {
            if(!convertToCurrency.equals(tmpCurrency)) {
                if(convertToCurrency.equals(baseCurrency)) {
                    int position = exchangeRateStringList.indexOf(tmpCurrency);
                    multiplier = exchangeRateDoubleList.get(position);
                    rez = rez * multiplier;
                }else {
                    int position_one = exchangeRateStringList.indexOf(convertToCurrency);
                    multiplier = exchangeRateDoubleList.get(position_one);
                    double tmp_value = rez / multiplier;
                    position_one = exchangeRateStringList.indexOf(tmpCurrency);
                    multiplier = exchangeRateDoubleList.get(position_one);
                    rez = tmp_value * multiplier;
                }
            }
        }else {
            if(!convertToCurrency.equals(baseCurrency)) {
                int position = exchangeRateStringList.indexOf(convertToCurrency);
                multiplier = exchangeRateDoubleList.get(position);
                rez = rez / multiplier;
            }
        }

        return rez;
    }

    private void getConversionRates() {
        conversionRatesModelArrayList = helper.getAllConversionRates();

        int velicina = conversionRatesModelArrayList.size();
        for(int i = 0; i < velicina; i++) {
            if(i == 0) {
                baseCurrency = conversionRatesModelArrayList.get(i).getCode();
                myBaseCurrency = helper.getCurrencyByCode(baseCurrency);
            } else {
                exchangeRateStringList.add(conversionRatesModelArrayList.get(i).getCode());
                exchangeRateDoubleList.add(conversionRatesModelArrayList.get(i).getRate());
            }
        }

        initializeAdapter();
    }

    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String barTitle = "Piggy Bank -> Status";
        assert actionBar != null;
        actionBar.setTitle(barTitle);
        int bisque = ContextCompat.getColor(getContext(), R.color.Bisque);
        setActionbarTextColor(actionBar, bisque);
    }

    private void setActionbarTextColor(ActionBar actBar, int color) {
        String title = String.valueOf(actBar.getTitle());
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);
    }
}

///