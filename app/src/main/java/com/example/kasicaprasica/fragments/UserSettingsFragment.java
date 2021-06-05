package com.example.kasicaprasica.fragments;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Annotation;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/*import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;*/
import com.example.kasicaprasica.R;
import com.example.kasicaprasica.apiCalls.CurrencyApiCall;
import com.example.kasicaprasica.apiCalls.CurrencyApiCallRetrofit;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.dialogFragments.AlertDialogFragment;
import com.example.kasicaprasica.dialogFragments.SuccessEntryDialogFragment;
import com.example.kasicaprasica.interfaces.APIInterface;
import com.example.kasicaprasica.interfaces.AlertDialogInterface;
import com.example.kasicaprasica.interfaces.SuccessDialogInterface;
import com.example.kasicaprasica.models.ConversionRatesJsonModel;
import com.example.kasicaprasica.models.ConversionRatesModel;

import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import android.widget.TextView;


import com.example.kasicaprasica.models.UserForApi;
import com.example.kasicaprasica.models.UserList;
import com.example.kasicaprasica.models.CreateUserResponse;
import com.example.kasicaprasica.models.MultipleResources;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserSettingsFragment extends Fragment implements SuccessDialogInterface, AlertDialogInterface {

    TextView responseText;
    APIInterface apiInterface;

    private EditText username;
    private EditText password;

    private MyCurrency mcDeff;

    private TextInputLayout currencySelection;
    private AutoCompleteTextView autoCompleteCurrencySelection;
    private ArrayList<MyCurrency> listaValuta;
    private ArrayList<String> listaValutaString;

    private DatabaseHelper helper;

    private User u;

    private MediaPlayer player;

    private ArrayList<String> currenciesToConvertTo;
    private ArrayList<Double> exchangeRateDoubleList;
    private ArrayList<String> exchangeRateStringList;
    private ArrayList<ConversionRatesModel> databaseConversionRates;

    private JSONObject jsonObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setUpActionBar();

        helper = new DatabaseHelper(getContext());

        mcDeff = helper.getDefaultCurrency();
        newApi(view);

        player = MediaPlayer.create(getContext(), R.raw.button_sound);

        u = new User();
        Cursor c = helper.getUserById(1);
        if(c.getCount() != 0) {
            c.moveToFirst();
            u.setId(c.getInt(0));
            u.setUsername(c.getString(1));
            u.setPassword(c.getString(2));
            u.setDefCurr(c.getInt(3));
        }
        c.close();

        username = view.findViewById(R.id.user_settings_user_edit_text);
        password = view.findViewById(R.id.user_settings_pass_edit_text);

        username.setText(u.getUsername());
        password.setText(u.getPassword());



        currencySelection = view.findViewById(R.id.user_settings_currency_selection);
        autoCompleteCurrencySelection = view.findViewById(R.id.user_settings_auto_complete_currency_selection);

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listaValutaString);
        autoCompleteCurrencySelection.setAdapter(adapter);

        autoCompleteCurrencySelection.setText(mcDeff.getName(), false);



        exchangeRateDoubleList = new ArrayList<Double>();
        exchangeRateStringList = new ArrayList<String>();
        currenciesToConvertTo = new ArrayList<String>();
        databaseConversionRates = new ArrayList<ConversionRatesModel>();

        currenciesToConvertTo = helper.getAllCurrenciesCodesButNotDefault(mcDeff.getCode());

        databaseConversionRates = helper.getAllConversionRates();



        view.findViewById(R.id.save_button_user_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                prikaziDialog();
                //spremiPromjene();
            }
        });


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeKeyboard();
                return false;
            }
        });

        autoCompleteCurrencySelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                username.setSelected(false);
                password.setSelected(false);
                autoCompleteCurrencySelection.performClick();
                closeKeyboard();
            }
        });
    }


    //////////////////////////////////////RETROFIT API CALLL PROBAAA////////////////////////////////////////////////
    private void newApi(View view) {
        responseText = view.findViewById(R.id.responseText);
        apiInterface = CurrencyApiCallRetrofit.getCurrencyRates().create(APIInterface.class);
        MyCurrency newBaseCurrency = new MyCurrency();
        newBaseCurrency = mcDeff;

        //{"base":"HRK","rates":{"GBP":0.1160671336,"USD":0.1599577111,"EUR":0.1321527686},"date":"2021-02-09"}
        StringBuilder symbols = new StringBuilder();
        currenciesToConvertTo = helper.getAllCurrenciesCodesButNotDefault(newBaseCurrency.getCode());
        int sizeOfArray = currenciesToConvertTo.size();
        for(int i = 0; i < sizeOfArray; i++) {
            symbols.append(currenciesToConvertTo.get(i)).append(",");
            if(i + 1 == sizeOfArray) {
                symbols = new StringBuilder(symbols.substring(0, symbols.length() - 1));
            }
        }

        //Call<ConversionRatesJsonModel> call = apiInterface.getConversionRateFor(newBaseCurrency.getCode(), symbols.toString());
        Call<ConversionRatesJsonModel> call = apiInterface.getAllConversionRates();
        call.enqueue(new Callback<ConversionRatesJsonModel>() {
            @Override
            public void onResponse(Call<ConversionRatesJsonModel> call, Response<ConversionRatesJsonModel> response) {
                helper.deleteConversionRatesRetrofit();
                helper.deleteConversionRatesBaseRetrofit();

                ConversionRatesJsonModel conversionRatesJsonModel = response.body();
                String keke = "koajf";

                Gson gson = new Gson();
                String json = gson.toJson(conversionRatesJsonModel.rates);

                HashMap<String, Double> map = new HashMap<>();
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Iterator<?> keys = jObject.keys();

                while( keys.hasNext() ){
                    String key = (String)keys.next();
                    Double value = null;
                    try {
                        value = jObject.getDouble(key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    map.put(key, value);
                }

                boolean isBaseInserted = helper.insertConversionRatesRetrofit(map);
                boolean areRatesInserted = helper.insertConversionRatesBaseRetrofit(conversionRatesJsonModel.base, conversionRatesJsonModel.date);

                if(isBaseInserted && areRatesInserted) {
                    Toast.makeText(getContext(),"Call succesfull", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConversionRatesJsonModel> call, Throwable t) {
                Toast.makeText(getContext(), "Call FAILED!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                call.cancel();
            }
        });

    }

    private void spremiPromjene() {
        int id = 1;
        String ime = username.getText().toString();
        String pass = password.getText().toString();
        String valuta = autoCompleteCurrencySelection.getText().toString();

        MyCurrency n = helper.getCurrencyByName(valuta);
        if(n.getId() != mcDeff.getId()) {
            //getApiResult(n);
        }

        User locUser = new User(id, ime, pass, n.getId());

        boolean isInserted = helper.updateUserData(locUser);
        if(isInserted) {
            u = locUser;
            Toast.makeText(getContext(), "Data is updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "DATA IS NOT updated", Toast.LENGTH_LONG).show();
        }


    }

    private void prikaziDialog() {
        int id = 1;
        String ime = username.getText().toString();
        String pass = password.getText().toString();
        String valuta = autoCompleteCurrencySelection.getText().toString();

        MyCurrency n = helper.getCurrencyByName(valuta);

        User locUser = new User(id, ime, pass, n.getId());

        if(u.equals(locUser)) {
            //Toast.makeText(getContext(), "YOU DIDN'T MAKE ANY CHANGES!", Toast.LENGTH_LONG).show();
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString("title", "Warning!");
            arguments.putString("message", "You didn't make any changes.");
            alertDialogFragment.setArguments(arguments);
            alertDialogFragment.setTargetFragment(this, 0);
            alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");
        }else {
            SuccessEntryDialogFragment dialogFragment = new SuccessEntryDialogFragment();
            dialogFragment.setTargetFragment(this, 0);
            Bundle args = new Bundle();
            args.putString("title", "Editing!");
            args.putString("message", "You will make changes to your account information.");
            args.putString("positiveButton", "Proceed");
            args.putString("negativeButton", "Cancel");
            dialogFragment.setArguments(args);
            dialogFragment.show(getParentFragmentManager(), "SuccessEntryDialogFragment");
        }


    }


    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onOkClick(DialogFragment dialogFragment) {
        player.start();
        dialogFragment.dismiss();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        player.start();
        spremiPromjene();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        player.start();
        dialog.dismiss();
    }


    private void upisiNovePodatke(String date) {
        //delete old data
        helper.deleteConversionRates();
        databaseConversionRates.clear();

        //Base currency
        int counter = 1;
        ConversionRatesModel baseConversionRateModel = new ConversionRatesModel();
        baseConversionRateModel.setId(counter);
        baseConversionRateModel.setCode(mcDeff.getCode());
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


    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String barTitle = "Piggy Bank -> User Settings";
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