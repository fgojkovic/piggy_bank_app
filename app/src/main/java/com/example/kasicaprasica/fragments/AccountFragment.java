package com.example.kasicaprasica.fragments;

import android.app.usage.UsageEvents;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.adapters.BanksAdapter;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.dialogFragments.SuccessEntryDialogFragment;
import com.example.kasicaprasica.interfaces.BanksRecyclerViewInterface;
import com.example.kasicaprasica.interfaces.SuccessDialogInterface;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.models.BankCurrencyConnect;
import com.example.kasicaprasica.models.BankValueLog;
import com.example.kasicaprasica.models.ConversionRatesModel;
import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.Pictures;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements BanksRecyclerViewInterface, SuccessDialogInterface {

    private View addForma;
    private View listaBanka;
    DatabaseHelper helper;

    //PRVI RED PLUS
    private boolean pritisnutPlusic = false;
    private ImageButton prviRedPlusic;

    //DRUGI RED PLUS
    private boolean pritisnutPlusicList = false;
    private ImageButton drugiRedPlusic;

    private ImageButton takePictureButton;
    private ImageView prostorZaSliku;
    private String currentPhotoPath;

    private EditText inputFieldBankName;

    ViewPager2 viewPager2ListBanks;
    ArrayList<Bank> banksArrayList;
    BanksAdapter banksAdapter;
    /*int redniBrojevi[] = {1,2,3};
    String nazivi[] = {"Prva prasica", "Druga prasica", "Treća prasica"};
    String datumi[] = {"1.1.1970.", "1.1.2000.", "16.3.1991"};
    String iznosi[] = {"10kn", "15€", "1000kn"};
*/
    /*int redniBrojevi[];
    String nazivi[];
    String datumi[];
    String iznosi[];*/

    ArrayList<Integer> redniBrojevi;
    ArrayList<String> nazivi;
    ArrayList<String> datumi;
    ArrayList<String> iznosi;

    ArrayList<BankCurrencyConnect> bccList;

    private MyCurrency mC;

    int brojac = 0;

    MediaPlayer player;

    private JSONObject jsonObject;
    ArrayList<String> exchangeRateStringList;
    ArrayList<Double> exchangeRateDoubleList;
    ArrayList<String> exchangeRateStringListFirst;
    ArrayList<ConversionRatesModel> conversionRatesModelArrayList;

    private ProgressBar progressBar;

    private MenuItem button;

    private Runnable r;
    ExecutorService executorService;
    Handler mHandler;
    private boolean finished = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_button, menu);
        button = menu.getItem(0);
        button.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_bank_form_button) {
            player.start();
            prostorZaSliku.setVisibility(View.GONE);
            currentPhotoPath = null;
            if(pritisnutPlusic) {
                toggle(addForma);
                //prviRedPlusic.setBackgroundResource(R.mipmap.add_button);
                pritisnutPlusic = false;
                inputFieldBankName.setSelected(false);
            } else {
                inputFieldBankName.setText("");
                //.setBackgroundResource(R.mipmap.remove_button);
                toggle(addForma);
                pritisnutPlusic = true;
            }
            super.onOptionsItemSelected(item);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setUpActionBar();

        progressBar = view.findViewById(R.id.account_progres_bar_circle);
        progressBar.setVisibility(View.VISIBLE);

        addForma = view.findViewById(R.id.add_bank_form);
        //listaBanka = view.findViewById(R.id.list_all_banks);
        inputFieldBankName = view.findViewById(R.id.add_bank_input);
        //prviRedPlusic = view.findViewById(R.id.plusic);
        //drugiRedPlusic = view.findViewById(R.id.plusic_list);
        takePictureButton = view.findViewById(R.id.take_picture_button);
        prostorZaSliku = view.findViewById(R.id.prostor_za_sliku);

        executorService = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());
        r = () -> loadScreen(view, savedInstanceState);
        mHandler.postDelayed(r, 400);


    }

    private void loadScreen(View view, Bundle savedInstanceState) {
        helper = new DatabaseHelper(getContext());

        redniBrojevi = new ArrayList<Integer>();
        nazivi = new ArrayList<String>();
        datumi = new ArrayList<String>();
        iznosi = new ArrayList<String>();

        mC = helper.getDefaultCurrency();

        exchangeRateStringList = new ArrayList<String>();
        exchangeRateDoubleList = new ArrayList<Double>();
        exchangeRateStringListFirst = new ArrayList<String>();
        exchangeRateStringListFirst = helper.getAllCurrenciesCodesButNotDefault(mC.getCode());
        conversionRatesModelArrayList = new ArrayList<ConversionRatesModel>();

        bccList = new ArrayList<BankCurrencyConnect>();

        viewPager2ListBanks = view.findViewById(R.id.lista_banka_reciklaza);

        banksArrayList = new ArrayList<Bank>();
        banksAdapter = new BanksAdapter(getContext(), this);

        player = MediaPlayer.create(getContext(), R.raw.button_sound);

        //getApiResult();

        executorService = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());
        r = new Runnable() {
            @Override
            public void run() {
                getConversionRates();
                if (finished) {
                    executorService.shutdown();
                }
            }
        };
        mHandler.post(r);


        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*

                Bundle bundle = new Bundle();
                bundle.putInt("br", 99);

                NavHostFragment.findNavController(AccountFragment.this).navigate(R.id.action_accountFragment2_to_pictureFragment, bundle);*/

                closeKeyboard();
                player.start();
                dispatchTakePictureIntent();
            }
        });

        /*view.findViewById(R.id.plusic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                if(pritisnutPlusic) {
                    toggle(addForma);
                    pritisnutPlusic = false;
                    inputFieldBankName.setSelected(false);
                } else {
                    inputFieldBankName.setText("");
                    toggle(addForma);
                    pritisnutPlusic = true;
                }
            }
        });*/


        /*view.findViewById(R.id.plusic_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generirajListu();
                if(pritisnutPlusicList) {
                    drugiRedPlusic.setBackgroundResource(R.mipmap.add_button);
                    toggle(listaBanka);
                    pritisnutPlusicList = false;
                } else {
                    drugiRedPlusic.setBackgroundResource(R.mipmap.remove_button);
                    toggle(listaBanka);
                    pritisnutPlusicList = true;
                }

            }
        });*/

        view.findViewById(R.id.prihvati_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                String tekst = inputFieldBankName.getText().toString();
                if(!tekst.trim().isEmpty()) {
                    long yourmilliseconds = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy.", Locale.UK);
                    Date date = new Date(yourmilliseconds);
                    String time = sdf.format(date);


                    Cursor cur = helper.getUsersDefCurr();
                    cur.moveToFirst();
                    String imagePath = currentPhotoPath;
                    cur.close();

                    //String value = "Defult value na prihvati acc gumb";
                    if(currentPhotoPath != null) {
                        if(currentPhotoPath.equals("")) {
                            Toast.makeText(getContext(), "No Photo", Toast.LENGTH_SHORT).show();
                        }
                    }

                    Bank b = new Bank(inputFieldBankName.getText().toString(), time, imagePath);
                    int id = -1;

                    boolean isInserted = helper.insertBankData(b);
                    if (isInserted) {
                        closeKeyboard();
                        inputFieldBankName.setText("");
                        toggle(addForma);
                        //prviRedPlusic.setBackgroundResource(R.mipmap.add_button);
                        pritisnutPlusic = false;
                        Toast.makeText(getContext(), "Data is inserted!", Toast.LENGTH_SHORT).show();
                        Cursor c = helper.getLastBank();
                        if (c.getCount() != 0 && c.getCount() < 2) {
                            c.moveToFirst();
                            id = c.getInt(0);
                            b.setId(id);
                            //zašto zvat funkciju ako nema početne vrijednosti??
                            b.setValue(izracunajValueUDefaultCurr(b)+ " " + mC.getSymbol());
                        }
                        c.close();
                        setBankValueLog(b, yourmilliseconds);
                    } else {
                        Toast.makeText(getContext(), "Data is NOT inserted!", Toast.LENGTH_SHORT).show();
                    }
                    banksArrayList.add(b);
                    banksAdapter.notifyDataSetChanged();
                }else {
                    closeKeyboard();
                    Toast.makeText(getContext(), "Name of bank is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeKeyboard();
                return false;
            }
        });
    }

    private void setBankValueLog(Bank b, long miliseconds) {
        ArrayList<BankValueLog> bankValueLogs = new ArrayList<>();
        ArrayList<MyCurrency> currencies = new ArrayList<>();
        double startingValue = 0;

        Cursor cursor = helper.getAllCurrencies();
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                MyCurrency currency = new MyCurrency();
                currency.setId(cursor.getInt(0));
                currency.setName(cursor.getString(1));
                currency.setCode(cursor.getString(2));
                currency.setSymbol(cursor.getString(3));
                currencies.add(currency);
            }
        }
        cursor.close();

        for(MyCurrency currencyLocal : currencies) {
            BankValueLog bvl = new BankValueLog();
            bvl.setId_bank(b.getId());
            bvl.setId_curr(currencyLocal.getId());
            bvl.setTotal_value(startingValue);
            bvl.setDate(String.valueOf(miliseconds));
            bankValueLogs.add(bvl);
        }

        for(BankValueLog bvlLocal : bankValueLogs) {
            boolean isInserted = helper.insertBankValueLog(bvlLocal);
            if(!isInserted) {
                Toast.makeText(getContext(), "Bank value log NOT inserted", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void obrisiKasicu() {
        banksAdapter.notifyDataSetChanged();
    }

    private void generirajListu() {
        //dohvati sve banke iz baze();
        Cursor c = helper.getAllBanks();
        if(mC == null) {
            mC = helper.getDefaultCurrency();
        }
        //Cursor c = helper.getOneBank(1);
        banksArrayList.clear();

        DecimalFormat formater = new DecimalFormat("0.00");

        if(c.getCount()!=0) {
            //StringBuffer buffer = new StringBuffer();
            while(c.moveToNext()) {
                Bank b = new Bank();
                if(c.isNull(0)) {
                    redniBrojevi.add(c.getInt(0));
                }else {
                    brojac++;
                    redniBrojevi.add(brojac);
                }
                b.setId(c.getInt(0));
                b.setBankName(c.getString(1));
                b.setDateOfCreation(c.getString(2));
                //b.setValue("Defaultni iznos u funkcijij");
                b.setImagePath(c.getString(3));

                String string = formater.format(izracunajValueUDefaultCurr(b));
                b.setValue(string + " " + mC.getSymbol());
                banksArrayList.add(b);
            }
            /*banksAdapter.notifyDataSetChanged();*/
        }

        c.close();

        banksAdapter.setItems(banksArrayList);

        viewPager2ListBanks.setClipToPadding(false);
        viewPager2ListBanks.setClipChildren(false);
        viewPager2ListBanks.setOffscreenPageLimit(3);
        viewPager2ListBanks.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        viewPager2ListBanks.setAdapter(banksAdapter);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(8));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1- Math.abs(position);
                page.setScaleY(0.8f + v * 0.2f);
            }
        });

        viewPager2ListBanks.setPageTransformer(transformer);
        //ostaci prijašnjeg loadinga
        if(progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            button.setVisible(true);
        }
        finished = true;
    }

    private double izracunajValueUDefaultCurr(Bank b) {
        Cursor c2 = helper.getAllMoneyByBank(b);
        bccList.clear();
        double result = 0;
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
            }

            c2.close();



            for(BankCurrencyConnect bccLocal : bccList) {
                Pictures p = new Pictures();
                p =  helper.getPictureById(bccLocal.getId_money_pic());
                if(mC == null) {
                    mC = helper.getDefaultCurrency();
                }

                if(bccLocal.getId_curr() == mC.getId()) {
                    result += p.getValue() * bccLocal.getAmount();
                } else {

                    /*//pretvori valutu
                    myConvertCurrrency = helper.getCurrencyById(bccLocal.getId_curr());
                    MyCurrency newCurrency = helper.getCurrencyById(p.getId_curr());
                    rez += convert(bccLocal.getAmount() * pictures.getValue(), myConvertCurrrency, newCurrency);*/

                    double poslati = p.getValue() * bccLocal.getAmount();
                    MyCurrency tmp_curr = helper.getCurrencyById(bccLocal.getId_curr());
                    //result += convert(poslati, bccLocal.getId_curr(), mC.getId());
                    result += convert(poslati, tmp_curr, mC);
                }
            }
        } else {
            c2.close();
        }

        return result;
    }

   /* private double convert(double value, int curr_id, int deff_curr_id) {
        double res = 0;
        if(curr_id == 2) {
            res = value * 7.5;
        } else if (curr_id == 3) {
            res = value * 6.2;
        } else if( curr_id == 4) {
            res = value * 8.4;
        }
        return res;
    }*/

    private double convert(Double rez, MyCurrency myCurrency, MyCurrency myBaseCurrency) { // myBaseCurrency is currencie in which everything has to be converted to
        int brojacc = 0;
        String tmpCurrency;
        String convertToCurrency;
        convertToCurrency = myCurrency.getCode();
        tmpCurrency = myBaseCurrency.getCode();

        Double multiplier = 1.0;
        /*if(!tmpCurrency.equals(baseCurrency)) {
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
        }else {*/
            if(!convertToCurrency.equals(mC.getCode())) {
                int position = exchangeRateStringList.indexOf(convertToCurrency);
                multiplier = exchangeRateDoubleList.get(position);
                rez = rez / multiplier;
            }
        //}

        return rez;
    }


    private void toggle(View view) {

        //Moja tranzicija
        /*int provjera = view.getVisibility();
        Transition transition;
        if(provjera == View.GONE) {
            transition = new Fade(Fade.IN);
        } else  {
            transition = new Fade(Fade.OUT);
        }

        transition.setDuration(500);
        transition.addTarget(view);

        TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transition);*/

        //Samo enable/disable view pa s android:animateLayoutChanges="true" to radi donekle ljepo
        int show = View.GONE;
        if(view != null) {
            if(view.getTag().toString().equals("AddBankForm")) {
                if(!pritisnutPlusic) {

                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            } else if(view.getTag().toString().equals("ListBank")) {
                if(!pritisnutPlusicList) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }

        /*view.setVisibility(show == View.VISIBLE ? View.GONE : View.VISIBLE);*/
    }

    @Override
    public void onSmeceClick(int id) {
        player.start();
        SuccessEntryDialogFragment dialogFragment = new SuccessEntryDialogFragment();
        dialogFragment.setTargetFragment(this, 0);
        Bundle args = new Bundle();
        args.putString("title", "Deleting!");
        args.putString("message", "Are you sure you want to delete this bank");
        args.putString("positiveButton", "Delete");
        args.putString("negativeButton", "Cancel");
        args.putInt("id", id);
        dialogFragment.setArguments(args);
        dialogFragment.show(getParentFragmentManager(), "SuccessEntryDialogFragment");
    }

    @Override
    public void onPenClick(int id) {
        player.start();
        Bundle bundle = new Bundle();
        bundle.putInt("Bank_id", id);

        NavHostFragment.findNavController(AccountFragment.this).navigate(R.id.action_accountFragment2_to_editBankFragment, bundle);
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        player.start();
        Bundle bundle = dialog.getArguments();
        int id = bundle.getInt("id");
        /*Log.w("ON SMECE CLICK ID U ACCOUNT FRAGMENTU: ", String.valueOf(id));*/
        //banksArrayList.
        Cursor curr = helper.getOneBank(id);
        Bank b = new Bank();
        if(curr.getCount()>0) {
            curr.moveToFirst();
            b.setId(curr.getInt(0));
            b.setBankName(curr.getString(1));
            b.setDateOfCreation(curr.getString(2));
            b.setImagePath(curr.getString(3));
        }

        /*Log.w("ID iz baze", String.valueOf(b.getId()));*/


        int brojObrisanih = helper.deleteOneBank(id);
        if(brojObrisanih > 0) {
            //Toast.makeText(getContext(), "Bank is deleted!", Toast.LENGTH_SHORT).show();
            int vel = banksArrayList.size();
            for(int i = 0; i < vel; i++) {
                if(banksArrayList.get(i).getId() == b.getId()) {
                    banksArrayList.remove(i);
                    break;
                }
            }
            /*if(banksArrayList.isEmpty()) {
                toggle(listaBanka);
                drugiRedPlusic.setBackgroundResource(R.mipmap.add_button);
                pritisnutPlusicList = false;
            }*/
            deleteBankValueLog(b);
            banksAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(getContext(), "Bank is NOT deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBankValueLog(Bank b) {
        int deleted = helper.deleteBankValueLog(b.getId());
        if(deleted <= 0) {
            Toast.makeText(getContext(), "Bank value logs weren't deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        player.start();
        dialog.dismiss();
    }



    //CAMERA HANDLING

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e("IOException Account Fragment:", e.toString());
            }
            if(photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Log.e("Activity Not found Account Fragment:", e.toString());
                }
            }

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(this.currentPhotoPath);
            prostorZaSliku.setImageBitmap(imageBitmap);
            prostorZaSliku.setVisibility(View.VISIBLE);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HH:mm dd.MM.yyyy.").format(new Date(System.currentTimeMillis()));
        String imageFileName = "JPEG_" + timeStamp + "_";
        //MediaStore.Images

        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        this.currentPhotoPath = image.getAbsolutePath();
        return image;
    }

   /* private void getApiResult() {

        //{"base":"HRK","rates":{"GBP":0.1160671336,"USD":0.1599577111,"EUR":0.1321527686},"date":"2021-02-09"}
        String symbols = "";
        int sizeOfArray = exchangeRateStringListFirst.size();
        for(int i = 0; i < sizeOfArray; i++) {
            symbols += exchangeRateStringListFirst.get(i) + ",";
            if(i + 1 == sizeOfArray) {
                symbols = symbols.substring(0, symbols.length() - 1);
            }
        }
        String apiCall = "https://api.ratesapi.io/api/latest?base=" + mC.getCode() + "&symbols=" + symbols;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        //naslov.setText(error.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiCall, null,
                response -> {
                    jsonObject = response;
                    //pokreni sljedeću metodu
                    povuciPodatkeIzJsona();
                }, Throwable::printStackTrace);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void povuciPodatkeIzJsona() {
        String bazaString = "";
        String date = "";

        try {
            bazaString = (String)jsonObject.get("base");
            date = (String)jsonObject.get("date");

            JSONObject object = jsonObject.getJSONObject("rates");
            int velicina = object.length();
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
        }catch (JSONException e) {
            e.printStackTrace();
        } finally {
            generirajListu();
        }
    }*/


    private void getConversionRates() {
        conversionRatesModelArrayList = helper.getAllConversionRates();

        int velicina = conversionRatesModelArrayList.size();
        for(int i = 0; i < velicina; i++) {
            if(i == 0) {
                mC = helper.getCurrencyByCode(conversionRatesModelArrayList.get(i).getCode());
            } else {
                exchangeRateStringList.add(conversionRatesModelArrayList.get(i).getCode());
                exchangeRateDoubleList.add(conversionRatesModelArrayList.get(i).getRate());
            }
        }

        generirajListu();
    }

    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String barTitle = "Piggy Bank -> My Banks";
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
