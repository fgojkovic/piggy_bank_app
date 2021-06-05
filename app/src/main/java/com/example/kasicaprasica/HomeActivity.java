package com.example.kasicaprasica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Transition;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.models.ConversionRatesModel;
import com.example.kasicaprasica.models.MyCurrency;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import gr.net.maroulis.library.EasySplashScreen;

public class HomeActivity extends AppCompatActivity {

    private String time;
    private String timeFromDatabase;

    private MyCurrency myCurrency;
    private String baseCurrency;
    private ConversionRatesModel conversionRatesModel;

    private ArrayList<String> currenciesToConvertTo;
    private ArrayList<Double> exchangeRateDoubleList;
    private ArrayList<String> exchangeRateStringList;
    private ArrayList<ConversionRatesModel> databaseConversionRates;

    private JSONObject jsonObject;

    private boolean insert = true;

    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        helper = new DatabaseHelper(this);

        conversionRatesModel = new ConversionRatesModel();

        myCurrency = helper.getDefaultCurrency();

        if(myCurrency == null) {
            baseCurrency = "EUR";
        } else {
            baseCurrency = myCurrency.getCode();
        }


        exchangeRateDoubleList = new ArrayList<Double>();
        exchangeRateStringList = new ArrayList<String>();
        currenciesToConvertTo = new ArrayList<String>();
        databaseConversionRates = new ArrayList<ConversionRatesModel>();

        currenciesToConvertTo = helper.getAllCurrenciesCodesButNotDefault(baseCurrency);

        databaseConversionRates = helper.getAllConversionRates();

        if(databaseConversionRates.size() > 0) {
            insert = false;
        }


        //2021-02-12
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        timeFromDatabase = helper.getConversionRatesDate();
        try {
            date = sdf.parse(timeFromDatabase);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(System.currentTimeMillis() > date.getTime()) {
            getApiResult();
        }




        ///OD OVDJE NA DOLJE JE SPLASH SCREEN
        //setContentView(R.layout.activity_home);
        /*Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
                finish();*/
        /*Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();*/

        //getSupportActionBar().hide();

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);*/
        //super.onCreate(savedInstanceState);


        //EasySPlashScreen
        EasySplashScreen config = new EasySplashScreen(HomeActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2500)
                .withBackgroundColor(Color.parseColor("#FFE4C4"))
                /*.withLogo(R.mipmap.kasica_prasica_logo)*/
                .withLogo(R.drawable.ic_lac_ikona_piggy)
                //.withLogo(R.drawable.ic__test)
                /*.withHeaderText("Tekst gore")*/
                .withFooterText(getResources().getString(R.string.copyright))
                /*.withBeforeLogoText("Text before logo!")*/
                .withAfterLogoText(getResources().getString(R.string.app_name_welcome));

        //Set text logo
        /*config.getHeaderTextView().setTextColor(Color.BLACK);*/
        /*config.getBeforeLogoTextView().setTextColor(Color.RED);*/
        config.getAfterLogoTextView().setTextColor(Color.BLACK);
        config.getFooterTextView().setTextColor(Color.BLACK);

        config.getAfterLogoTextView().setAllCaps(true);
        config.getAfterLogoTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        config.getFooterTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        //Set to view
        View view = config.create();

        setContentView(view);
    }

    private void getApiResult() {

        //{"base":"HRK","rates":{"GBP":0.1160671336,"USD":0.1599577111,"EUR":0.1321527686},"date":"2021-02-09"}
        StringBuilder symbols = new StringBuilder();
        int sizeOfArray = currenciesToConvertTo.size();
        for(int i = 0; i < sizeOfArray; i++) {
            symbols.append(currenciesToConvertTo.get(i)).append(",");
            if(i + 1 == sizeOfArray) {
                symbols = new StringBuilder(symbols.substring(0, symbols.length() - 1));
            }
        }
        String apiCall = "https://api.ratesapi.io/api/latest?base=" + baseCurrency + "&symbols=" + symbols;


        // Instantiate the RequestQueue.
        //RequestQueue queue = Volley.newRequestQueue(this);

        /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiCall,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;
                povuciPodatkeIzJsona();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //naslov.setText(error.toString());
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
        queue.add(jsonObjectRequest);*/
    }

    private void povuciPodatkeIzJsona() {
        String date = "";

        try {
            date = (String)jsonObject.get("date");

            JSONObject object = jsonObject.getJSONObject("rates");
            Iterator<String> iterator = object.keys();
            Double value = 0.0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    value = (Double) object.get(key);
                    exchangeRateDoubleList.add(value);
                    exchangeRateStringList.add(key);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            upisiNovePodatke(date);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void upisiNovePodatke(String date) {
        //delete old data
        helper.deleteConversionRates();
        databaseConversionRates.clear();

        //Base currency
        int counter = 1;
        ConversionRatesModel baseConversionRateModel = new ConversionRatesModel();
        baseConversionRateModel.setId(counter);
        baseConversionRateModel.setCode(baseCurrency);
        baseConversionRateModel.setRate(0.0);
        baseConversionRateModel.setBase(1);
        baseConversionRateModel.setDate(date);

        databaseConversionRates.add(baseConversionRateModel);

        int velicina = exchangeRateDoubleList.size();
        for(int i = 0; i < velicina; i++) {
            counter++;
            ConversionRatesModel crm = new ConversionRatesModel();
            crm.setId(counter);
            crm.setCode(exchangeRateStringList.get(i));
            crm.setRate(exchangeRateDoubleList.get(i));
            crm.setBase(0);
            crm.setDate(date);
            databaseConversionRates.add(crm);
        }


        boolean isInserted = helper.insertConversionRates(databaseConversionRates);
        if(!isInserted) {
            Log.e("Home Activity: ", "Not inserted CONVERSION RATES DATA!");
        } else {
            Log.w("Home Activity: ", "Conversion data updated!");
        }

    }
}