package com.example.kasicaprasica.fragments;

import android.accessibilityservice.GestureDescription;
import android.animation.Animator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Picture;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.adapters.MyAdapter;
import com.example.kasicaprasica.adapters.MyAdapterWithTotalValue;
import com.example.kasicaprasica.comparators.PictureValueComparator;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.dialogFragments.AlertDialogFragment;
import com.example.kasicaprasica.dialogFragments.SuccessEntryDialogFragment;
import com.example.kasicaprasica.interfaces.AlertDialogInterface;
import com.example.kasicaprasica.interfaces.MyInterface;
import com.example.kasicaprasica.interfaces.SuccessDialogInterface;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.models.BankCurrencyConnect;
import com.example.kasicaprasica.models.BankValueLog;
import com.example.kasicaprasica.models.ConversionRatesModel;
import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.Pictures;
import com.example.kasicaprasica.models.Types;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class PaymentFragment extends Fragment implements MyInterface, AlertDialogInterface, SuccessDialogInterface {

    private Bank myBank;
    private MyCurrency myCurrency;
    private MyCurrency myBaseCurrency;
    private String baseCurrency;

    private ArrayList<Types> listaTipova;
    private ArrayList<String> listaTipovaString;

    private ArrayList<Bank> bankArrayList;
    private ArrayList<String> banksStringArrayList;

    private ArrayList<MyCurrency> currenciesArrayList;
    private ArrayList<String> currenciesStringArrayList;

    DatabaseHelper helper;

    private EditText entryText;
    private Button prepareButton;
    private Button prepareAnotherAmountButton;
    //možda treba maknuti
    private Boolean prepared = false;
    private Button payOutButton;


    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private MyAdapterWithTotalValue myAdapterWithTotalValue;

    private ArrayList<Pictures> listOfPicturesWithOneAmountEach;
    private ArrayList<Pictures> picturesArrayList;
    private ArrayList<Pictures> picturesArrayListForEdit;
    private ArrayList<Double> picturesDoubleArrayList;
    private ArrayList<Double> picturesDoubleArrayListForEdit;
    private ArrayList<String> listOfDefaultAmountStringsForAdapter;
    private ArrayList<String> listOfDefaultAmountStringsForAdapterForEdit;
    private ArrayList<BankCurrencyConnect> bccList;
    private ArrayList<ConversionRatesModel> crmList;
    private ArrayList<Double> exchangeRateDoubleList;
    private ArrayList<String> exchangeRateStringList;

    private RelativeLayout firstRow;

    private TextView requestedAmountValue;
    private LinearLayout requestedAmountRow;
    private TextView noMoneyInBankText;
    private LinearLayout noMoneyInBankRow;
    private TextView overTheLimitValue;
    private TextView overTheLimitLabel;
    private LinearLayout overTheLimitRow;
    private LinearLayout bottomRowButtons;


    private boolean firstClick = false;

    private SwitchCompat switchCompat;

    private TextInputLayout bankSelection;
    private AutoCompleteTextView autoCompleteBankSelection;
    private TextInputLayout currencySelection;
    private AutoCompleteTextView autoCompleteCurrencySelection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setUpActionBar();

        helper = new DatabaseHelper(getContext());

        entryText = view.findViewById(R.id.payment_entry_edit_text);
        prepareButton = view.findViewById(R.id.payment_prepare_button);
        prepareAnotherAmountButton = view.findViewById(R.id.payment_prepare_another_amount_button);
        payOutButton = view.findViewById(R.id.payment_pay_out_button);

        requestedAmountRow = view.findViewById(R.id.payment_searched_amount_row);
        requestedAmountValue = view.findViewById(R.id.payment_searched_amount_text_field);
        noMoneyInBankText = view.findViewById(R.id.payment_no_money_in_bank_text);
        noMoneyInBankRow = view.findViewById(R.id.payment_no_money_in_bank_row);
        overTheLimitValue = view.findViewById(R.id.payment_over_the_limit_text_field);
        overTheLimitLabel = view.findViewById(R.id.payment_over_the_limit_label);
        overTheLimitRow = view.findViewById(R.id.payment_over_the_limit_row);
        bottomRowButtons = view.findViewById(R.id.payment_bottom_row_buttons);

        switchCompat = view.findViewById(R.id.payment_switch);

        recyclerView = view.findViewById(R.id.payment_money_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        firstRow = view.findViewById(R.id.payment_top_row);

        myBank = new Bank();
        myCurrency = helper.getDefaultCurrency();

        namjestiSpinnere(view);

        listOfDefaultAmountStringsForAdapter = new ArrayList<String>();
        listOfDefaultAmountStringsForAdapterForEdit = new ArrayList<String>();
        picturesDoubleArrayList = new ArrayList<Double>();
        picturesDoubleArrayListForEdit = new ArrayList<Double>();
        picturesArrayList = helper.getAllPicturesByBank(myBank);
        picturesArrayListForEdit = new ArrayList<Pictures>();
        picturesArrayListForEdit.addAll(picturesArrayList);
        listOfPicturesWithOneAmountEach = new ArrayList<Pictures>();

        //picturesArrayListForEdit = picturesArrayList;
        bccList = new ArrayList<BankCurrencyConnect>();
        crmList = new ArrayList<ConversionRatesModel>();
        exchangeRateDoubleList = new ArrayList<Double>();
        exchangeRateStringList = new ArrayList<String>();

        //Setting up arraylists from above
        countAmountOfMoney();

        listOfDefaultAmountStringsForAdapterForEdit.addAll(listOfDefaultAmountStringsForAdapter);
        picturesDoubleArrayListForEdit.addAll(picturesDoubleArrayList);

        //conversion rates

        getConversionRates();

        myAdapter = new MyAdapter(getContext(), listOfDefaultAmountStringsForAdapterForEdit, picturesArrayListForEdit, this);
        myAdapterWithTotalValue = new MyAdapterWithTotalValue(getContext(), listOfDefaultAmountStringsForAdapterForEdit, picturesArrayListForEdit, picturesDoubleArrayListForEdit, this);
        recyclerView.setAdapter(myAdapterWithTotalValue);
        //recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }
            }
        });*/

        autoCompleteBankSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(firstClick) {
                    myBank = bankArrayList.get(position);
                    picturesArrayList = helper.getAllPicturesByBank(myBank);
                    countAmountOfMoney();
                } else {
                    firstClick = true;
                }
            }
        });

        autoCompleteCurrencySelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCurrency = currenciesArrayList.get(position);
            }
        });

        prepareAnotherAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout();
                if(noMoneyInBankRow.getVisibility() == View.VISIBLE) {
                    noMoneyInBankRow.setVisibility(View.GONE);
                }
            }
        });


        prepareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                //Preparing Amount
                picturesArrayListForEdit.clear();
                listOfDefaultAmountStringsForAdapterForEdit.clear();
                picturesDoubleArrayListForEdit.clear();

                if(listOfPicturesWithOneAmountEach.size() == 0) {
                    for(BankCurrencyConnect bccLocal : bccList) {
                        Pictures pic = helper.getPictureById(bccLocal.getId_money_pic());
                        for(int br = 0; br < bccLocal.getAmount(); br++) {
                            listOfPicturesWithOneAmountEach.add(pic);
                        }
                    }
                }

                //myBank = bankArrayList.get(banksSpinner.getSelectedItemPosition());
                myBank = bankArrayList.get(banksStringArrayList.indexOf(autoCompleteBankSelection.getText().toString()));
                myCurrency = helper.getCurrencyByName(autoCompleteCurrencySelection.getText().toString());
                String enTxt = entryText.getText().toString();

                //if nothing is written in edit box alertdialog shows up
                if(enTxt.trim().isEmpty()) {
                    displayDialog("alertDialog");
                } else if (Integer.parseInt(enTxt.trim()) <= 0) {
                    displayDialog("alertDialogNull");
                } else {
                    //Taking amount of payment
                    Double targetValue = Double.parseDouble(entryText.getText().toString());
                    String requestedText = entryText.getText() + " " + myCurrency.getCode();
                    requestedAmountValue.setText(requestedText);

                    //sorting pictures from higher value to lower and by id
                    listOfPicturesWithOneAmountEach.sort(new PictureValueComparator());

                    //Seting all array list items first that are of selected currencyspinner
                    setSelectedCurrencyFirst(listOfPicturesWithOneAmountEach);

                    //if there is new pictureArrayList list of strings and amounts must be updated
                    setNewPicturesAndArrayString(listOfPicturesWithOneAmountEach);

                    double counter = 0.0;
                    double razlika = 0.0;
                    double coinValueInSelectedCurrency = 0.0;
                    boolean isThereHigherCoinOfSameCurrency = false;

                    int sizeOfList = listOfPicturesWithOneAmountEach.size();

                    if(sizeOfList < 1) {
                        noMoneyInBank();
                    } else {
                        moneyInBank();

                        for(int num = 0; num < sizeOfList; num++) {
                            razlika = targetValue - counter;

                            //selecting next picture if exists in array
                       /* Pictures nextP = new Pictures();
                        int maxPosition = listOfPicturesWithOneAmountEach.size() - 1;
                        if((listOfPicturesWithOneAmountEach.indexOf() + 1) <= maxPosition) {
                            nextP = listOfPicturesWithOneAmountEach.get(listOfPicturesWithOneAmountEach.indexOf(p) + 1);
                        }*/

                            MyCurrency tmpCurrency = helper.getCurrencyById(listOfPicturesWithOneAmountEach.get(num).getId_curr());

                            if(listOfPicturesWithOneAmountEach.get(num).getId_curr() != myCurrency.getId()) {
                                //Toast.makeText(getContext(), "You entered more than you have in your current currency!", Toast.LENGTH_SHORT).show();
                                if(switchCompat.isChecked()) {
                                    underTheLimit(targetValue, counter);
                                    break;
                                }
                           /* if(myCurrency.getId() == myBaseCurrency.getId()) {
                                //Toast.makeText(getContext(), "MyCurrency and BaseCurrency are same", Toast.LENGTH_SHORT).show();
                            }*/
                                coinValueInSelectedCurrency = convert(listOfPicturesWithOneAmountEach.get(num).getValue(), tmpCurrency, myCurrency);
                            } else {
                                coinValueInSelectedCurrency = listOfPicturesWithOneAmountEach.get(num).getValue();
                            }

                            if(razlika <= 0) {
                                if(razlika < 0) {
                                    overTheLimit(targetValue, counter);
                                }
                                break;
                            } else if (razlika < coinValueInSelectedCurrency) {
                                if(checkIfThereIsEnoughMoneyLeft(razlika, num, tmpCurrency)) {

                                } else {
                                    picturesArrayListForEdit.add(listOfPicturesWithOneAmountEach.get(num));
                                    counter += coinValueInSelectedCurrency;
                                }
                            } else {
                                if(!checkIfThereIsEnoughMoneyLeft(razlika, num, tmpCurrency)) {
                                    picturesArrayListForEdit.add(listOfPicturesWithOneAmountEach.get(num));
                                    counter += coinValueInSelectedCurrency;
                                }
                            }


                            if(num + 1 == sizeOfList) {
                                razlika = targetValue - counter;
                                if(razlika < 0) {
                                    overTheLimit(targetValue, counter);
                                } else if (razlika > 0) {
                                    underTheLimit(targetValue, counter);
                                }
                            }
                        }

                        setUpPlayerList();
                    }



                    myAdapterWithTotalValue.notifyDataSetChanged();

                    changeLayout();
                }
            }
        });


        payOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog("successDialog");
            }
        });

    }

    private void setUpPlayerList() {
        int size = picturesArrayListForEdit.size();
        int counter = 0;
        ArrayList<Pictures> tmpPictureList = new ArrayList<Pictures>();
        Pictures previousPicture = new Pictures();
        for(int i = 0; i < size; i++) {
            if(i == 0) {

                tmpPictureList.add(picturesArrayListForEdit.get(i));
                counter++;
                if((size - 1 == i)) {
                    String text = "x " + counter;
                    listOfDefaultAmountStringsForAdapterForEdit.add(text);
                    picturesDoubleArrayListForEdit.add(counter * picturesArrayListForEdit.get(i).getValue());
                }

            } else if(previousPicture.getId() != picturesArrayListForEdit.get(i).getId()) { //&& (size - 1) != i
                String text = "x " + counter;
                listOfDefaultAmountStringsForAdapterForEdit.add(text);
                picturesDoubleArrayListForEdit.add(counter * previousPicture.getValue());
                counter = 1;
                tmpPictureList.add(picturesArrayListForEdit.get(i));
                if(size - 1 == i) {
                    text = "x " + counter;
                    listOfDefaultAmountStringsForAdapterForEdit.add(text);
                    picturesDoubleArrayListForEdit.add(counter * picturesArrayListForEdit.get(i).getValue());
                }
            } else if((size - 1 == i)) {
                counter++;
                String text = "x " + counter;
                listOfDefaultAmountStringsForAdapterForEdit.add(text);
                picturesDoubleArrayListForEdit.add(counter * picturesArrayListForEdit.get(i).getValue());
            } else {
                counter++;
            }
            previousPicture = picturesArrayListForEdit.get(i);
        }

        picturesArrayListForEdit.clear();
        picturesArrayListForEdit.addAll(tmpPictureList);
    }

    private boolean checkIfThereIsEnoughMoneyLeft(double targetValue, int position, MyCurrency currentCurrency) {
        boolean toSend = false;
        double counter = 0;
        double startvalue = 0;
        double tmpValue = 0;
        int size = listOfPicturesWithOneAmountEach.size();
        //startvalue = picturesArrayList.get(position).getValue();
        
        for(int i = position + 1; i < size; i++) {
            if(listOfPicturesWithOneAmountEach.get(i).getId_curr() != currentCurrency.getId()) {
                break;
            } else if(listOfPicturesWithOneAmountEach.get(i).getId_curr() != myCurrency.getId()) {
                tmpValue = convert(listOfPicturesWithOneAmountEach.get(i).getValue(), currentCurrency, myCurrency);
                counter += tmpValue;
            } else {
                counter += listOfPicturesWithOneAmountEach.get(i).getValue();
            }
        }

        if(counter >= targetValue) {
            toSend = true;
        }

        return toSend;
    }

    private void overTheLimit(Double vrijednost, double v) {
        //DecimalFormat formater = new DecimalFormat("#.##");
        DecimalFormat formater = new DecimalFormat("0.00");
        String string = formater.format(v - vrijednost);
        String textToDisplay = string + " " + myCurrency.getSymbol();
        overTheLimitValue.setText(textToDisplay);
        overTheLimitRow.setVisibility(View.VISIBLE);
    }

    private void underTheLimit(Double vrijednost, double v) {
        DecimalFormat formater = new DecimalFormat("0.00");
        String string = formater.format(vrijednost - v);
        String textToDisplay = string + " " + myCurrency.getSymbol();
        String textForUnderTheLimit = "Under the limit:";
        overTheLimitLabel.setText(textForUnderTheLimit);
        overTheLimitValue.setText(textToDisplay);
        overTheLimitRow.setVisibility(View.VISIBLE);
    }

    private void noMoneyInBank() {
        noMoneyInBankRow.setVisibility(View.VISIBLE);
        payOutButton.setEnabled(false);
    }

    private void moneyInBank() {
        noMoneyInBankRow.setVisibility(View.GONE);
        payOutButton.setEnabled(true);
    }

    private void prepareLargerAmountInSameCurrency(Double razlika, Double counter, Double vrijednost) {
        int velicina = picturesArrayList.size();
        int amount = 0;
        for(int i = velicina - 1; i >= 0; i--) {
            for(BankCurrencyConnect bccLocal : bccList) {
                if(bccLocal.getId_money_pic() == picturesArrayList.get(i).getId()) {
                    if(picturesArrayList.get(i).getValue() > vrijednost && i != 0 && picturesDoubleArrayList.get(i - 1) < vrijednost) {
                        picturesArrayListForEdit.add(picturesArrayList.get(i));
                        listOfDefaultAmountStringsForAdapterForEdit.add("x " + 1);
                        picturesDoubleArrayListForEdit.add(1 * picturesArrayList.get(i).getValue());
                        break;
                    }
                }
            }
        }

        //myAdapterWithTotalValue.notifyDataSetChanged();

        //changeLayout();
    }

    private void prepareAditionalAmountInOtherCurrency(Double razlika, Double counter, Double vrijednost) {
        //pictures array list i za edit u edit dodaj ove iz array liste al prvo ih convertaj

    }


    private void namjestiSpinnere(View view) {
        bankSelection = view.findViewById(R.id.payment_bank_selection);
        currencySelection = view.findViewById(R.id.payment_currency_selection);
        autoCompleteBankSelection = view.findViewById(R.id.payment_auto_complete_bank_selection);
        autoCompleteCurrencySelection = view.findViewById(R.id.payment_auto_complete_currency_selection);


        bankArrayList = new ArrayList<Bank>();
        banksStringArrayList = new ArrayList<String>();
        Cursor cursor = helper.getAllBanks();
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                Bank b = new Bank();
                b.setId(cursor.getInt(0));
                b.setBankName(cursor.getString(1));
                b.setDateOfCreation(cursor.getString(2));
                //b.setValue("Defaultni iznos u funkcijij");
                b.setImagePath(cursor.getString(3));
                bankArrayList.add(b);
                banksStringArrayList.add(b.getBankName());
            }
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, banksStringArrayList);
        autoCompleteBankSelection.setAdapter(adapter);
        if(banksStringArrayList.size() > 0) {
            autoCompleteBankSelection.setText(banksStringArrayList.get(0), false);
            myBank = bankArrayList.get(0);
        }



        /*listaTipova = new ArrayList<Types>();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listaTipovaString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinn.setAdapter(adapter);*/

        currenciesArrayList = new ArrayList<MyCurrency>();
        currenciesStringArrayList = new ArrayList<String>();
        Cursor ci = helper.getAllCurrencies();
        if(ci.getCount() > 0) {
            while (ci.moveToNext()) {
                MyCurrency myCurrency = new MyCurrency();
                myCurrency.setId(ci.getInt(0));
                myCurrency.setName(ci.getString(1));
                myCurrency.setCode(ci.getString(2));
                myCurrency.setSymbol(ci.getString(3));
                currenciesArrayList.add(myCurrency);
                currenciesStringArrayList.add(myCurrency.getName());
            }
        }
        ci.close();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, currenciesStringArrayList);
        autoCompleteCurrencySelection.setAdapter(adapter2);
        autoCompleteCurrencySelection.setText(myCurrency.getName(),false);
    }

    @Override
    public void interfaceMethod(int pos, ArrayList<Integer> lista, ArrayList<Double> values) {
        Toast.makeText(getContext(), "Interface method", Toast.LENGTH_SHORT).show();
    }

    private void countAmountOfMoney() {
        if(bccList.size() > 0) {
            bccList.clear();
        }
        Cursor cursor = helper.getAllMoneyByBank(myBank);
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                BankCurrencyConnect bankCurrencyConnect = new BankCurrencyConnect();
                bankCurrencyConnect.setId(cursor.getInt(0));
                bankCurrencyConnect.setId_bank(cursor.getInt(1));
                bankCurrencyConnect.setId_curr(cursor.getInt(2));
                bankCurrencyConnect.setId_type(cursor.getInt(3));
                bankCurrencyConnect.setId_money_pic(cursor.getInt(4));
                bankCurrencyConnect.setAmount(cursor.getInt(5));
                bankCurrencyConnect.setDate(cursor.getString(6));
                bccList.add(bankCurrencyConnect);
            }
        }

        cursor.close();

        setNewPicturesAndArrayString(picturesArrayList);
    }

    //da li ovo uopće trebam??
    private void setNewPicturesAndArrayString(ArrayList<Pictures> pictureListToEdit) {
        int locAmount = 0;

        if(listOfDefaultAmountStringsForAdapter.size() > 0) {
            listOfDefaultAmountStringsForAdapter.clear();
        }

        if(picturesDoubleArrayList.size() > 0) {
            picturesDoubleArrayList.clear();
        }

        for(Pictures p : pictureListToEdit) {
            locAmount = 0;
            for(BankCurrencyConnect bccLocal : bccList) {
                if(p.getId() == bccLocal.getId_money_pic()) {
                    locAmount += bccLocal.getAmount();
                }
            }
            String stavi = "x " + locAmount;
            listOfDefaultAmountStringsForAdapter.add(stavi);
            picturesDoubleArrayList.add(locAmount * p.getValue());
        }
    }

    private void setSelectedCurrencyFirst(ArrayList<Pictures> list) {
        ArrayList<Pictures> tmp = new ArrayList<Pictures>();
        //if(myCurrency.getId() != 1)
        for(Pictures p : list) {
            if(p.getId_curr() == myCurrency.getId()) {
                tmp.add(p);
            }
        }

        list.removeAll(tmp);

        int sizeTmp = tmp.size();
        for(int i = 0; i < sizeTmp; i++) {
            list.add(i, tmp.get(i));
        }

    }

    private void changePrepareButtonText() {
        String txt;
        if(prepared) {
            txt = "PREPARE";
            prepareButton.setText(txt);
            prepareButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            prepared = false;
        } else {
            txt = "Prepare another amount";
            prepareButton.setText(txt);
            prepareButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            prepared = true;
        }
    }

    private void changeLayout() {
        //changePrepareButtonText();

        if(recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
            payOutButton.setVisibility(View.GONE);
            overTheLimitRow.setVisibility(View.GONE);
            requestedAmountRow.setVisibility(View.GONE);
            bottomRowButtons.setVisibility(View.GONE);
            firstRow.setVisibility(View.VISIBLE);
            prepareButton.setVisibility(View.VISIBLE);
            //focusable i selectAll
            entryText.selectAll();
        } else {
            firstRow.setVisibility(View.GONE);
            prepareButton.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            payOutButton.setVisibility(View.VISIBLE);
            requestedAmountRow.setVisibility(View.VISIBLE);
            bottomRowButtons.setVisibility(View.VISIBLE);
        }
    }

    private void withdrawMoney() {
        Log.w("Tu smo", "jeej");
        ArrayList<BankCurrencyConnect> listForDelete = new ArrayList<BankCurrencyConnect>();
        ArrayList<BankCurrencyConnect> listForUpdate = new ArrayList<BankCurrencyConnect>();
        int velicina = listOfDefaultAmountStringsForAdapterForEdit.size();
        for(int i = 0; i < velicina; i++) {
            Pictures p = picturesArrayListForEdit.get(i);
            int amountToWithdraw = Integer.parseInt(listOfDefaultAmountStringsForAdapterForEdit.get(i).substring(2));
            int currentAmount = 0;
            for(BankCurrencyConnect bccLocal : bccList) {
                if(p.getId() == bccLocal.getId_money_pic()) {
                    if(bccLocal.getAmount() > amountToWithdraw) {
                        currentAmount = bccLocal.getAmount() - amountToWithdraw;
                        bccLocal.setAmount(currentAmount);
                        listForUpdate.add(bccLocal);
                    } else if(bccLocal.getAmount() <= amountToWithdraw && amountToWithdraw != 0){
                        listForDelete.add(bccLocal);
                        amountToWithdraw -= bccLocal.getAmount();
                    }
                }
            }
        }

        int obrisane = 0;
        boolean updated = false;

        if(listForUpdate.size() > 0) {
            updated = helper.updateBankCurrencyConnectData(listForUpdate);
        }
        if(listForDelete.size() > 0) {
            obrisane = helper.deleteBankCurrencyConnectData(listForDelete);
        }

        if(obrisane > 0 || updated) {
            picturesArrayList = helper.getAllPicturesByBank(myBank);
            listOfPicturesWithOneAmountEach.clear();
            countAmountOfMoney();
            updateBankLog(myBank, myCurrency);
            Toast.makeText(getContext(), "Updates: " + updated + " and deleated: " + obrisane, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Error in withdrawal!", Toast.LENGTH_LONG).show();
        }

        //myAdapter.notifyDataSetChanged();
        myAdapterWithTotalValue.notifyDataSetChanged();

        changeLayout();
    }

    //Function for updating bank log for chart data
    private void updateBankLog(Bank b, MyCurrency currency) {
        //Calculate total value of the bank
        long timeInMilis = System.currentTimeMillis();
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
        crmList = helper.getAllConversionRates();

        int velicina = crmList.size();
        for(int i = 0; i < velicina; i++) {
            if(i == 0) {
                baseCurrency = crmList.get(i).getCode();
                myBaseCurrency = helper.getCurrencyByCode(baseCurrency);
            } else {
                exchangeRateStringList.add(crmList.get(i).getCode());
                exchangeRateDoubleList.add(crmList.get(i).getRate());
            }
        }
    }



    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }




    private void displayDialog(String which) {
       /* odabirBanke.getSelectedItem();
        long ne = odabirBanke.getSelectedItemId();
        boolean isEmpty = true;

        ArrayList<Integer> lb = adaptercic.getListaBrojaca();
        for (Integer i : lb) {
            if (i != 0) {
                isEmpty = false;
                break;
            }
        }
        if (ne < 0) {*/
            //moj alert dialog


        if(which.equals("alertDialog")) {
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString("title", "Warning!");
            arguments.putString("message", "You haven't entered any value!");
            alertDialogFragment.setArguments(arguments);
            alertDialogFragment.setTargetFragment(this, 0);
            alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");
        } else if(which.equals("alertDialogNull")) {
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString("title", "Warning!");
            arguments.putString("message", "Value can not be 0 or less!");
            alertDialogFragment.setArguments(arguments);
            alertDialogFragment.setTargetFragment(this, 0);
            alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");
        } else {
            SuccessEntryDialogFragment dialogFragment = new SuccessEntryDialogFragment();
            dialogFragment.setTargetFragment(this, 0);
            Bundle args = new Bundle();
            args.putString("title", "Money withdrawal!");
            args.putString("message", "Take " + entryText.getText() + " " + myCurrency.getSymbol() + " from your bank!" );
            args.putString("positiveButton", "Done");
            args.putString("negativeButton", "Cancel");
            dialogFragment.setArguments(args);
            dialogFragment.show(getParentFragmentManager(), "SuccessEntryDialogFragment");
        }

        /*} else if (isEmpty) {
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
*/

        //moj defaultni
        /*AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");*/


    }

    //Succes dialog
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        withdrawMoney();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    //Alert dialog
    @Override
    public void onOkClick(DialogFragment dialogFragment) {

    }


    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String barTitle = "Piggy Bank -> Withdrawal";
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