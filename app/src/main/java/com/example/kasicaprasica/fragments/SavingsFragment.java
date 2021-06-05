package com.example.kasicaprasica.fragments;

import android.database.Cursor;
import android.graphics.Path;
import android.net.IpSecManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.comparators.MoneyDateComparator;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.formatters.DateValueFormatter;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.models.BankCurrencyConnect;
import com.example.kasicaprasica.models.BankValueLog;
import com.example.kasicaprasica.models.MyCurrency;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavingsFragment extends Fragment {

    private Runnable r;
    ExecutorService executorService;
    Handler mHandler;
    private boolean finished = false;
    private DatabaseHelper helper;

    private Bank myBank;
    private MyCurrency mySelectedCurrency;
    private MyCurrency myDefaultCurrency;

    private ArrayList<MyCurrency> currenciesArrayList;
    private ArrayList<String> currenciesStringArrayList;

    private ArrayList<Bank> bankArrayList;
    private ArrayList<String> banksStringArrayList;

    private TextInputLayout bankSelection;
    private TextInputLayout currencySelection;
    private AutoCompleteTextView autoCompleteBankSelection;
    private AutoCompleteTextView autoCompleteCurrencySelection;

    private ProgressBar progressBar;
    private RelativeLayout contentLayout;

    //Line chart variables
    private LineChart lineChart;
    private ArrayList<ILineDataSet> dataSets;
    //private ArrayList<Entry> entries;

    private LineData data;

    private ArrayList<BankCurrencyConnect> bccList;
    private ArrayList<BankValueLog> bvlList;

    private ArrayList<MyCurrency> allCurrencies;

    private RadioGroup radioGroup;
    private SwitchCompat switchCompat;

    //X axsis LABELS
    //HOURLY 0:00, 1:00... 24:00
    //DAILY MON,TUE... SUN
    //MONTHLY JAN,FEB...DEC
    //YEARLY 2020, 2021...
    private static final ArrayList<String> xAxisLabelsDaily = new ArrayList<>();
    private static final ArrayList<String> xAxishoursInDayLabel = new ArrayList<>();
    ArrayList<String> xAxisLabelsMonthlyLong = new ArrayList<>(
            Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    );

    ArrayList<String> xAxisLabelsMonthlyShort = new ArrayList<>(
            Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    );

    ArrayList<String> xAxisLabelsYearlyShort = new ArrayList<>(
            Arrays.asList("2020", "2021", "2022", "2023")
    );
    //insert into table_bank_currency_connect values(null, 1,4,1,2,1, '15:00 02.03.2021.')

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_savings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setUpActionBar();

        progressBar = view.findViewById(R.id.savings_progres_bar_circle);
        contentLayout = view.findViewById(R.id.savings_content_layout);
        contentLayout.setVisibility(View.GONE);
        lineChart = view.findViewById(R.id.savings_chart_test);
        radioGroup = view.findViewById(R.id.savings_radio_group);
        switchCompat = view.findViewById(R.id.savings_switch);

        bankSelection = view.findViewById(R.id.savings_bank_selection);
        currencySelection = view.findViewById(R.id.savings_currency_selection);
        autoCompleteBankSelection = view.findViewById(R.id.savings_auto_complete_bank_selection);
        autoCompleteCurrencySelection = view.findViewById(R.id.savings_auto_complete_currency_selection);

        executorService = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());
        r = new Runnable() {
            @Override
            public void run() {
                loadScreen();
            }
        };
        mHandler.postDelayed(r, 400);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.savings_radio_button1) {
                    setChart(1);
                } else if(checkedId == R.id.savings_radio_button2) {
                    setChart(2);
                } else if(checkedId == R.id.savings_radio_button3) {
                    setChart(3);
                } else if(checkedId == R.id.savings_radio_button4) {
                    setChart(4);
                }
            }
        });


        autoCompleteBankSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myBank = bankArrayList.get(position);
            }
        });

        autoCompleteCurrencySelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mySelectedCurrency = currenciesArrayList.get(position);
                if(switchCompat.isChecked()) {
                    ArrayList<MyCurrency> tmpCurrencyList;
                    tmpCurrencyList = new ArrayList<>();
                    tmpCurrencyList.add(mySelectedCurrency);
                    LineData data = new LineData(getLineDataSetForCurrency(tmpCurrencyList, radioGroup.getCheckedRadioButtonId()));
                    //LineDataSet set = getLineDataSetForCurrency(tmpCurrencyList);
                    lineChart.setData(data);
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                }
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ArrayList<MyCurrency> tmpCurrencyList;
                    tmpCurrencyList = new ArrayList<>();
                    tmpCurrencyList.add(mySelectedCurrency);
                    LineData data = new LineData(getLineDataSetForCurrency(tmpCurrencyList, radioGroup.getCheckedRadioButtonId()));
                    //LineDataSet set = getLineDataSetForCurrency(tmpCurrencyList);
                    lineChart.setData(data);

                } else {
                    LineData data = new LineData(getLineDataSetForCurrency(allCurrencies, radioGroup.getCheckedRadioButtonId()));
                    lineChart.setData(data);
                }
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        });

    }

    private void loadScreen() {
        helper = new DatabaseHelper(getContext());

        myDefaultCurrency = helper.getDefaultCurrency();

        setUpSpinners();

        bccList = new ArrayList<>();
        bvlList = new ArrayList<>();
        allCurrencies = new ArrayList<>();
        Cursor cur = helper.getAllCurrencies();
        if(cur.getCount() > 0) {
            while (cur.moveToNext()) {
                MyCurrency currencyLocal = new MyCurrency();
                currencyLocal.setId(cur.getInt(0));
                currencyLocal.setName(cur.getString(1));
                currencyLocal.setCode(cur.getString(2));
                currencyLocal.setSymbol(cur.getString(3));
                allCurrencies.add(currencyLocal);
            }
        }
        cur.close();

        //Adding
        xAxisLabelsDaily.add("Mon");
        xAxisLabelsDaily.add("Tue");
        xAxisLabelsDaily.add("Wed");
        xAxisLabelsDaily.add("Thu");
        xAxisLabelsDaily.add("Fri");
        xAxisLabelsDaily.add("Sat");
        xAxisLabelsDaily.add("Sun");


        //Adding
        int numberOfHoursInDay = 24;
        for(int i = 0; i <= numberOfHoursInDay; i++) {
            xAxishoursInDayLabel.add(i + ":00");
        }



        setUpMoneyList();

        //entries = new ArrayList<>();
        dataSets = new ArrayList<>();

        setUpChart(myBank, myDefaultCurrency, 1);

        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    private void setChart(int br) {
        setUpChart(myBank, mySelectedCurrency, br);
    }


    private void setUpChart(Bank bank, MyCurrency currency, int type) {
        //Description
        Description desc = new Description();
        desc.setText("Your savings chart");
        desc.setTextSize(28);
        lineChart.getDescription().setEnabled(false);

        lineChart.setDragEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        //lineChart.setHorizontalScrollBarEnabled(true);
        //lineChart.canScrollHorizontally(1);
        /*lineChart.getViewPortHandler().setMaximumScaleX(5f);
        lineChart.getViewPortHandler().setMaximumScaleY(5f);*/




        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //this gave me extra value in formatter
        //xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0);
        /*xAxis.setSpaceMax(5);
        xAxis.setSpaceMin(2);*/

        xAxis.resetAxisMaximum();


        //namjestiti label i formater za svaki tip
        ArrayList<String> tmp = new ArrayList();
        switch (type) {
            case 1:
                xAxis.setLabelCount(xAxishoursInDayLabel.size() - 1, false);
                xAxis.setAxisMaximum(xAxishoursInDayLabel.size() -1);
                tmp = xAxishoursInDayLabel;
                break;
            case 2:
                xAxis.setLabelCount(xAxisLabelsDaily.size() - 1, false);
                xAxis.setAxisMaximum(xAxisLabelsDaily.size() - 1);
                tmp = xAxisLabelsDaily;
                break;
            case 3:
                xAxis.setLabelCount(xAxisLabelsMonthlyShort.size() - 1, false);
                xAxis.setAxisMaximum(xAxisLabelsMonthlyShort.size() -1);
                tmp = xAxisLabelsMonthlyShort;
                break;
            case 4:
                xAxis.setLabelCount(xAxisLabelsYearlyShort.size() - 1, false);
                xAxis.setAxisMaximum(xAxisLabelsYearlyShort.size() -1);
                tmp = xAxisLabelsYearlyShort;
                break;
        }


        int labelCount = xAxis.getLabelCount();
        float axisMax = xAxis.getAxisMaximum();

        ArrayList<String> finalTmp = tmp;
        xAxis.setValueFormatter(new IndexAxisValueFormatter(finalTmp) {
            @Override
            public String getFormattedValue(float value) {
                if(value < 0) {
                    return "negative";
                } else if(value >= finalTmp.size()) {
                    return "Vece je od liste!";
                } else {
                    return finalTmp.get((int)value);
                }


            }
        });


        xAxis.setAxisLineWidth(1);
        int colorXY = getResources().getColor(R.color.Black, null);
        xAxis.setAxisLineColor(colorXY);


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setDrawGridLines(false);
        //leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisLineWidth(1);
        leftAxis.setAxisLineColor(colorXY);

        YAxis rightAxis = lineChart.getAxisRight();
        /*rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0);*/
        rightAxis.setEnabled(false);
        rightAxis.setAxisMinimum(0);


        lineChart.setDrawGridBackground(false);

        dataSets = getLineDataSetForCurrency(allCurrencies, type);

        data = new LineData(dataSets);

        lineChart.setData(data);

        lineChart.notifyDataSetChanged();

        lineChart.invalidate();

    }

    private void setUpMoneyList() {
        bvlList = helper.getBankLogsByBankId(myBank.getId());

        //basically useless
        bvlList.sort(new MoneyDateComparator());
    }

    private ArrayList<Entry> getEntriesForCurrency(MyCurrency currency, int type) {
        //type 1 hourly, 2 daily, 3 monthly, 4 yearly
        //Prikupljanje podataka za chart
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf;
        switch (type) {
            case 1:
                sdf = new SimpleDateFormat("HH:mm", Locale.UK);
                break;
            case 2:
                sdf = new SimpleDateFormat("HH:mm E.", Locale.UK);
                break;
            case 3:
                sdf = new SimpleDateFormat("HH:mm d.M.yyyy", Locale.UK);
                break;
            case 4:
                sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy.", Locale.UK);
                break;
            default:
                sdf = new SimpleDateFormat("HH:mm", Locale.UK);
                break;
        }



        for(BankValueLog bvlLocal : bvlList) {
            if(bvlLocal.getId_curr() == currency.getId()) {
                Date date = new Date(Long.parseLong(bvlLocal.getDate()));
                Date presentDate = new Date(System.currentTimeMillis());
                /*Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                int sat = calendar.HOUR_OF_DAY;*/
                String time = sdf.format(date);
                String hours = time.split(":|\\s")[0].trim();
                String minutes = time.split(":|\\s")[1].trim();
                String day = "";
                String month = "";
                String year = "";
                float hoursFlo = Float.parseFloat(hours);
                float minutesFlo = Float.parseFloat(minutes);
                minutesFlo = minutesFlo / 60;
                float xValue = hoursFlo + minutesFlo;
                if(type == 1) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy", Locale.UK);
                    if(!simpleDateFormat.format(date).equals(simpleDateFormat.format(presentDate))) {
                        continue;
                    }
                }else if(type == 2) {
                   //float
                    day = time.split(":|\\s|\\.")[2].trim();
                    float dayFlo = xAxisLabelsDaily.indexOf(day) + 1;
                    xValue = xValue/24;
                    xValue = dayFlo + xValue;
                } else if (type == 3) {
                    day = time.split(":|\\s|\\.")[2].trim();
                    month = time.split(":|\\s|\\.")[3].trim();
                    year = time.split(":|\\s|\\.")[4].trim();
                    float monthFlo = Float.parseFloat(month) - 1;
                    int daysInMonth = 0;
                    float dayFlo = 0f;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
                        daysInMonth = yearMonth.lengthOfMonth();
                    }
                    if(daysInMonth > 0) {
                        xValue = xValue/24/daysInMonth;
                        dayFlo = Float.parseFloat(day) / daysInMonth;
                        xValue += dayFlo;
                    }

                    xValue += monthFlo;
                } else if (type == 4) {
                    //puno se ponavlja
                    day = time.split(":|\\s|\\.")[2].trim();
                    month = time.split(":|\\s|\\.")[3].trim();
                    year = time.split(":|\\s|\\.")[4].trim();
                    float yearFlo = xAxisLabelsYearlyShort.indexOf(year);
                    int daysInMonth = 0;
                    int daysInYear = 0;
                    float dayFlo = 0f;
                    float monthFlo = 0f;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
                        daysInMonth = yearMonth.lengthOfMonth();
                        daysInYear = yearMonth.lengthOfYear();
                    }
                    if(daysInMonth > 0) {
                        xValue = xValue/24/daysInMonth / daysInYear;
                        dayFlo = Float.parseFloat(day) / daysInMonth /daysInYear;
                        monthFlo = Float.parseFloat(month) / daysInYear;
                        xValue += dayFlo;
                    }
                    xValue += yearFlo;
                }

                entries.add(new Entry(xValue, (float)bvlLocal.getTotal_value()));
            }
        }

        return entries;
    }

    private ArrayList<ILineDataSet> getLineDataSetForCurrency(ArrayList<MyCurrency> currencies, int type) {

        List<Entry> entries = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        for(MyCurrency mc : currencies) {
            entries = getEntriesForCurrency(mc, type);
            LineDataSet lineDataSet = new LineDataSet(entries, mc.getSymbol());

            int color = 0;
            //Izgled data seta
            lineDataSet.setLineWidth(2);
            int circleColor = getResources().getColor(R.color.DarkCyan, null);
            lineDataSet.setCircleColor(circleColor);
            switch (mc.getCode()) {
                case "EUR":
                    color = getResources().getColor(R.color.MidnightBlue, null);
                    break;
                case "USD":
                    color = getResources().getColor(R.color.SpringGreen, null);
                    break;
                case "GBP":
                    color = getResources().getColor(R.color.Aquamarine, null);
                    break;
                case "HRK":
                    color = getResources().getColor(R.color.IndianRed, null);
                    break;
            }

            lineDataSet.setColor(color);
            lineDataSet.setValueTextSize(16f);


            dataSets.add(lineDataSet);
        }

        return dataSets;
    }


    //SETING UP SPINNERS
    private void setUpSpinners() {
        bankArrayList = new ArrayList<>();
        banksStringArrayList = new ArrayList<>();
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
        if(bankArrayList.size() > 0) {
            autoCompleteBankSelection.setText(banksStringArrayList.get(0), false);
            myBank = bankArrayList.get(0);
        }


        currenciesArrayList = new ArrayList<>();
        currenciesStringArrayList = new ArrayList<>();
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
        if(currenciesArrayList.size() > 0) {
            autoCompleteCurrencySelection.setText(myDefaultCurrency.getName(), false);
            mySelectedCurrency = myDefaultCurrency;
        }
    }


    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String barTitle = "Piggy Bank -> Savings";
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
