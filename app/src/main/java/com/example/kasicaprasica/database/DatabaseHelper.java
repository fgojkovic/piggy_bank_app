package com.example.kasicaprasica.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.camera.camera2.internal.compat.quirk.StillCaptureFlashStopRepeatingQuirk;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.database.FeedReaderContract.*;
import com.example.kasicaprasica.models.BankCurrencyConnect;
import com.example.kasicaprasica.models.BankValueLog;
import com.example.kasicaprasica.models.ConversionRatesJsonModel;
import com.example.kasicaprasica.models.ConversionRatesModel;
import com.example.kasicaprasica.models.MyCurrency;
import com.example.kasicaprasica.models.Pictures;
import com.example.kasicaprasica.models.Types;
import com.example.kasicaprasica.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {
    //database name and version
    public static final int DATABASE_VERSION = 20;
    public static final String DATABASE_NAME = "piggy_bank_database.db";

    private static final int images[] = {
            //HRATSKE KUNE
            R.mipmap.hrvatska_kuna_symbol, R.mipmap.jedna_kuna, R.mipmap.dvije_kuna, R.mipmap.pet_kuna, R.mipmap.deset_kuna, R.mipmap.dvadeset_kuna, R.mipmap.petdeset_kuna, R.mipmap.sto_kuna, R.mipmap.dvijesto_kuna, R.mipmap.petsto_kuna, R.mipmap.tisucu_kuna,
            //EURI
            R.mipmap.euro_symbol, R.mipmap.jedan_euro, R.mipmap.dva_euro, R.mipmap.pet_eura, R.mipmap.deset_eura, R.mipmap.dvadeset_eura, R.mipmap.pedeset_eura, R.mipmap.sto_eura, R.mipmap.dvijesto_eura, R.mipmap.petsto_eura,
            //US DOLLARS
            R.mipmap.dollar_symbol, R.mipmap.one_us_dollar_coin, R.mipmap.jedan_dolar, R.mipmap.dva_dolara, R.mipmap.pet_dolara, R.mipmap.deset_dolara,R.mipmap.dvadeset_dolara, R.mipmap.pedeset_dolara, R.mipmap.sto_dolara,
            //POUND STERLING
            R.mipmap.pound_sterling_symbol, R.mipmap.one_pound_sterling_coin, R.mipmap.two_pound_sterling_coin, R.mipmap.pet_funti, R.mipmap.deset_funti, R.mipmap.dvadeset_funti, R.mipmap.pedeset_funti
            //Ostale
    };



    /*//table names
    public static final String table_bank = "table_bank";
    public static final String table_currency = "table_currency";
    public static final String table_start_value = "table_start_value";
    public static final String table_pictures = "table_pictures";
    public static final String table_type = "table_type";

    //connection tables names
    public static final String table_bank_connect = "table_bank_currency_connect";
    public static final String table_start_vale_connect = "table_start_value_connect";*/



    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_BANK_TABLE);
        db.execSQL(SQL_CREATE_CURRENCY_TABLE);
        db.execSQL(SQL_CREATE_TYPE_TABLE);
        db.execSQL(SQL_CREATE_PICTURES_TABLE);
        db.execSQL(SQL_CREATE_BANK_CURRENCY_CONNECT_TABLE);
        db.execSQL(SQL_CREATE_START_VALUE_BANK_CONNECT_TABLE);
        db.execSQL(SQL_CREATE_CONVERSION_RATES_TABLE);
        db.execSQL(SQL_CREATE_BANK_VALUE_LOG_TABLE);
        db.execSQL(SQL_CREATE_CONVERSION_RATES_BASE_RETROFIT_TABLE);
        db.execSQL(SQL_CREATE_CONVERSION_RATES_RETROFIT_TABLE);

        //insert default type and default currencies
        /*defaultTypesInsert();
        defaultCurrienciesInsert();*/
        db.execSQL(SQL_INSERT_DEFAULT_TYPES);
        db.execSQL(SQL_INSERT_DEFAULT_CURRENCIES);
        db.execSQL(SQL_INSERT_DEFAULT_PICTURES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_USER_TABLE);
        db.execSQL(SQL_DELETE_USER_TABLE_OLD);
        db.execSQL(SQL_DELETE_BANK_TABLE);
        db.execSQL(SQL_DELETE_CURRENCY_TABLE);
        db.execSQL(SQL_DELETE_TYPE_TABLE);
        db.execSQL(SQL_DELETE_PICTURES_TABLE);
        db.execSQL(SQL_DELETE_BANK_CURRENCY_CONNECT_TABLE);
        db.execSQL(SQL_DELETE_START_VALUE_BANK_CONNECT_TABLE);
        db.execSQL(SQL_DELETE_CONVERSION_RATES_TABLE);
        db.execSQL(SQL_DELETE_BANK_VALUE_LOG_TABLE);
        db.execSQL(SQL_DELETE_CONVERSION_RATES_BASE_RETROFIT_TABLE);
        db.execSQL(SQL_DELETE_CONVERSION_RATES_RETROFIT_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }


    //INSERTS
    public boolean insertBankData(Bank b) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COL_2_BANK, b.getBankName());
        contentValues.put(FeedEntry.COL_3_BANK, b.getDateOfCreation());
        contentValues.put(FeedEntry.COL_4_BANK, b.getImagePath());
        long result = db.insert(FeedEntry.TABLE_BANK, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertUserData(User u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COL_2_USER, u.getUsername());
        contentValues.put(FeedEntry.COL_3_USER, u.getPassword());
        contentValues.put(FeedEntry.COL_4_USER, u.getDefCurr());
        /*contentValues.put(FeedEntry.COL_5_USER, u.getFirstName());
        contentValues.put(FeedEntry.COL_6_USER, u.getLastName());*/
        long result = db.insert(FeedEntry.TABLE_USER, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertMoneyInBank(Bank b, MyCurrency c,Types t,ArrayList<Pictures> p, ArrayList<Integer> listaBrojaca, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int velicina = listaBrojaca.size();
        for(int i = 0; i< velicina; i++) {
            if(listaBrojaca.get(i) > 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(FeedEntry.COL_2_BCC, b.getId());
                contentValues.put(FeedEntry.COL_3_BCC, c.getId());
                contentValues.put(FeedEntry.COL_4_BCC, t.getId());
                contentValues.put(FeedEntry.COL_5_BCC, p.get(i).getId());
                contentValues.put(FeedEntry.COL_6_BCC, listaBrojaca.get(i));
                contentValues.put(FeedEntry.COL_7_BCC, time);
                long result = db.insert(FeedEntry.TABLE_BANK_CURRENCY_CONNECT, null, contentValues);
                if(result == -1) {
                    Log.e("Insert money in bank failed! ", b.toString());
                    return false;
                }
            }
        }
        /*if(result == -1) {
            return false;
        } else {
            return true;
        }*/
        return true;
    }




    //UPDATES
    public boolean updateUserData(User u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COL_2_USER, u.getUsername());
        contentValues.put(FeedEntry.COL_3_USER, u.getPassword());
        contentValues.put(FeedEntry.COL_4_USER, u.getDefCurr());
        /*contentValues.put(FeedEntry.COL_5_USER, u.getFirstName());
        contentValues.put(FeedEntry.COL_6_USER, u.getLastName());*/
        long result = db.update(FeedEntry.TABLE_USER, contentValues, "_id = ?", new String[] {String.valueOf(1)});
        if(result <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateBankData(Bank b) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COL_2_BANK, b.getBankName());
        contentValues.put(FeedEntry.COL_3_BANK, b.getDateOfCreation());
        contentValues.put(FeedEntry.COL_4_BANK, b.getImagePath());
        /*contentValues.put(FeedEntry.COL_5_USER, u.getFirstName());
        contentValues.put(FeedEntry.COL_6_USER, u.getLastName());*/
        long result = db.update(FeedEntry.TABLE_BANK, contentValues, "_id = ?", new String[] {String.valueOf(b.getId())});
        if(result <= 0) {
            return false;
        } else {
            return true;
        }
    }


    //USERS
    public Cursor getUserById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_USER_BY_ID, new String[] {String.valueOf(id)});
        return cursor;
    }

    public Cursor getUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_USER_BY_USER_PASS, new String[] {username, password});
        return cursor;
    }


    public Cursor getUsersDefCurr() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_USERS_DEFAULT_CURRENCY,new String[] {String.valueOf(1)});
        return cursor;
    }


    //BANKS
    public Cursor getAllBanks() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_BANKS, null);
        return cursor;
    }

    public Cursor getOneBank(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ONE_BANK, new String[] {String.valueOf(id)});
        return cursor;
    }

    public Cursor getLastBank() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_LAST_BANK, null);
        return cursor;
    }

    public int deleteAllBanks() {
        SQLiteDatabase db = this.getWritableDatabase();
        int obrisano = db.delete(FeedEntry.TABLE_BANK, null, null);
        return obrisano;
    }

    public int deleteOneBank(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int obrisano = db.delete(FeedEntry.TABLE_BANK, WHERE_DELETE_ONE_BANK, new String[] {String.valueOf(id)});
        int obrisanoDrugo = db.delete(FeedEntry.TABLE_BANK_CURRENCY_CONNECT, WHERE_DELETE_BANK_WITH_MONEY, new String[] {String.valueOf(id)});
        return obrisano;
    }


    //CURRENCY
    public MyCurrency getCurrencyByName(String ime) {
        SQLiteDatabase db = this.getWritableDatabase();
        MyCurrency currency = new MyCurrency();
        Cursor c = db.rawQuery(SQL_SELECT_ID_BY_NAME_CURRENCY, new String[] {ime});
        if(c.getCount() > 0) {
            c.moveToFirst();
            currency.setId(c.getInt(0));
            currency.setName(c.getString(1));
            currency.setCode(c.getString(2));
            currency.setSymbol(c.getString(3));
            c.close();
            return currency;
        } else {
            c.close();
            return null;
        }
    }

    public MyCurrency getCurrencyByCode(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        MyCurrency currency = new MyCurrency();
        Cursor c = db.rawQuery(SQL_SELECT_CURRENCY_BY_CODE, new String[] {code});
        if(c.getCount() > 0) {
            c.moveToFirst();
            currency.setId(c.getInt(0));
            currency.setName(c.getString(1));
            currency.setCode(c.getString(2));
            currency.setSymbol(c.getString(3));
            c.close();
            return currency;
        } else {
            c.close();
            return null;
        }
    }

    public MyCurrency getDefaultCurrency() {
        SQLiteDatabase db = this.getWritableDatabase();
        MyCurrency mc = new MyCurrency();
        Cursor c = getUsersDefCurr();
        if(c.getCount() > 0) {
            c.moveToFirst();
            mc = getCurrencyById(c.getInt(0));
            c.close();
            return mc;
        } else {
            c.close();
            return null;
        }
    }

    public MyCurrency getCurrencyById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        MyCurrency mc = new MyCurrency();
        Cursor c = db.rawQuery(SQL_SELECT_CURRENCY_BY_ID, new String[] {String.valueOf(id)});
        if(c.getCount() > 0) {
            c.moveToFirst();
            mc.setId(c.getInt(0));
            mc.setName(c.getString(1));
            mc.setCode(c.getString(2));
            mc.setSymbol(c.getString(3));
            c.close();
            return mc;
        }else {
            c.close();
            return null;
        }
    }

    public Cursor getAllCurrencies() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SQL_SELECT_ALL_CURRENCIES, null);
        return  c;
    }

    public ArrayList<String> getAllCurrenciesCodesButNotDefault(String deffaultCurrencyCode) {
        ArrayList<String> toReturn = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SQL_SELECT_ALL_CURRENCIES_CODES_BUT_NOT_DEFAULT, new String[] {deffaultCurrencyCode});
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                toReturn.add(c.getString(0));
            }
        }
        c.close();
        return toReturn;
    }


    //TYPES
    public Cursor getAllTypes() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SQL_SELECT_ALL_TYPES, null);
        return  c;
    }

    public Types getTypeByName(String ime) {
        SQLiteDatabase db = this.getWritableDatabase();
        Types types = new Types();
        Cursor c = db.rawQuery(SQL_SELECT_TYPE_BY_NAME, new String[] {ime});
        if(c.getCount() > 0) {
            c.moveToFirst();
            types.setId(c.getInt(0));
            types.setName(c.getString(1));
            c.close();
            return types;
        } else {
            c.close();
            return null;
        }
    }

    //BANK & MONEY
    public Cursor getAllMoneyByBank(Bank b) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SQL_SELECT_BCC_BY_BANK_ID, new String[] {String.valueOf(b.getId())});
        return c;
    }

    public Cursor getAllMoneyByBankAndCurrency(Bank b, MyCurrency m) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SQL_SELECT_BCC_BY_BANK_ID_AND_CURR_ID, new String[] {String.valueOf(b.getId()), String.valueOf(m.getId())});
        return c;
    }

    //PICTURES
    public Cursor getPicturesByCurrAndType(MyCurrency mc, Types t) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SQL_SELECT_PICTURES_BY_CURR_ID_AND_TYPE_ID, new String[] {String.valueOf(mc.getId()), String.valueOf(t.getId())});
        return c;
    }

    public ArrayList<Pictures> getStartingPictures() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pictures> pic = new ArrayList<Pictures>();
        Cursor c = db.rawQuery(SQL_SELECT_PICTURES_BY_CURR_ID_AND_TYPE_ID, new String[] {String.valueOf(1), String.valueOf(1)});
        if(c.getCount() != 0) {
            while (c.moveToNext()) {
                Pictures p = new Pictures();
                p.setId(c.getInt(0));
                p.setId_curr(c.getInt(1));
                p.setLocation(c.getInt(2));
                p.setId_type(c.getInt(3));
                p.setValue(c.getDouble(4));
                pic.add(p);
            }
            c.close();
            return pic;
        } else
        {
            c.close();
            return null;
        }
    }

    public ArrayList<Pictures> getStartingSymbols() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pictures> pic = new ArrayList<Pictures>();
        Cursor c = db.rawQuery(SQL_SELECT_PICTURES_BY_TYPE_ID, new String[] {String.valueOf(0)});
        if(c.getCount()>0) {
            while(c.moveToNext()) {
                Pictures p = new Pictures();
                p.setId(c.getInt(0));
                p.setId_curr(c.getInt(1));
                p.setLocation(c.getInt(2));
                p.setId_type(c.getInt(3));
                p.setValue(c.getDouble(4));
                pic.add(p);
            }
            c.close();
            return pic;
        }else {
            c.close();
            return null;
        }
    }

    public Pictures getPictureById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Pictures pictures = new Pictures();
        Cursor c = db.rawQuery(SQL_SELECT_PICTURE_BY_ID, new String[] {String.valueOf(id)});
        if(c.getCount() > 0) {
            c.moveToFirst();
            pictures.setId(c.getInt(0));
            pictures.setId_curr(c.getInt(1));
            pictures.setLocation(c.getInt(2));
            pictures.setId_type(c.getInt(3));
            pictures.setValue(c.getDouble(4));

            c.close();
            return pictures;
        }else {
            c.close();
            return null;
        }
    }

    public ArrayList<Pictures> getPicturesByCurrency(MyCurrency mc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pictures> pList = new ArrayList<Pictures>();
        Cursor c = db.rawQuery(SQL_SELECT_PICTURES_BY_CURR_ID, new String[] {String.valueOf(mc.getId())});
        if(c.getCount() > 0) {
            while (c.moveToNext()) {
                Pictures p = new Pictures();
                p.setId(c.getInt(0));
                p.setId_curr(c.getInt(1));
                p.setLocation(c.getInt(2));
                p.setId_type(c.getInt(3));
                p.setValue(c.getDouble(4));
                pList.add(p);
            }

            c.close();
            return pList;
        }else {
            c.close();
            return null;
        }
    }

    public ArrayList<Pictures> getAllPictures() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pictures> pList = new ArrayList<Pictures>();
        Cursor c = db.rawQuery(SQL_SELECT_ALL_PICTURES, null);
        if(c.getCount() > 0) {
            while(c.moveToNext()) {
                Pictures p = new Pictures();
                p.setId(c.getInt(0));
                p.setId_curr(c.getInt(1));
                p.setLocation(c.getInt(2));
                p.setId_type(c.getInt(3));
                p.setValue(c.getDouble(4));
                pList.add(p);
            }
        }
        c.close();
        return pList;
    }

    //MIX BETWEEN BANKCURRENCY CONNECT AND PICTURES
    public ArrayList<Pictures> getAllPicturesByBank(Bank bank) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pictures> pList = new ArrayList<Pictures>();
        ArrayList<BankCurrencyConnect> bccList = new ArrayList<BankCurrencyConnect>();
        Cursor c = db.rawQuery(SQL_SELECT_BCC_BY_BANK_ID, new String[] {String.valueOf(bank.getId())});
        if(c.getCount() > 0) {
            while(c.moveToNext()) {
                BankCurrencyConnect bcc = new BankCurrencyConnect();
                bcc.setId(c.getInt(0));
                bcc.setId_bank(c.getInt(1));
                bcc.setId_curr(c.getInt(2));
                bcc.setId_type(c.getInt(3));
                bcc.setId_money_pic(c.getInt(4));
                bcc.setAmount(c.getInt(5));
                bccList.add(bcc);
                /*p.setId(c.getInt(0));
                p.setId_curr(c.getInt(1));
                p.setLocation(c.getInt(2));
                p.setId_type(c.getInt(3));
                p.setValue(c.getDouble(4));
                pList.add(p);*/
            }


            for(BankCurrencyConnect bccLocal : bccList) {
                Pictures p = new Pictures();
                p = getPictureById(bccLocal.getId_money_pic());
                if(!pList.contains(p)) {
                    pList.add(p);
                }
            }
        }
        c.close();
        return pList;
    }

    public int deleteBankCurrencyConnectData(ArrayList<BankCurrencyConnect> listForDelete) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedItems = 0;
        for(BankCurrencyConnect bccLocal : listForDelete) {
            deletedItems += db.delete(FeedEntry.TABLE_BANK_CURRENCY_CONNECT, "_id = ?" , new String[] {String.valueOf(bccLocal.getId())});
        }

        return deletedItems;
    }

    public boolean updateBankCurrencyConnectData(ArrayList<BankCurrencyConnect> listForUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(BankCurrencyConnect bccLocal : listForUpdate) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FeedEntry._ID, bccLocal.getId());
            contentValues.put(FeedEntry.COL_2_BCC, bccLocal.getId_bank());
            contentValues.put(FeedEntry.COL_3_BCC, bccLocal.getId_curr());
            contentValues.put(FeedEntry.COL_4_BCC, bccLocal.getId_type());
            contentValues.put(FeedEntry.COL_5_BCC, bccLocal.getId_money_pic());
            contentValues.put(FeedEntry.COL_6_BCC, bccLocal.getAmount());
            long isUpdatedCount = db.update(FeedEntry.TABLE_BANK_CURRENCY_CONNECT, contentValues, "_id = ?", new String[] {String.valueOf(bccLocal.getId())});
            if(isUpdatedCount == 0) {
                return false;
            }
        }

        return true;
    }


    //CONVERSION RATES SELECT, INSERT, UPDATE
    public ArrayList<ConversionRatesModel> getAllConversionRates() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ConversionRatesModel> toSend = new ArrayList<ConversionRatesModel>();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_CONVERSION_RATES, null);
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ConversionRatesModel crm = new ConversionRatesModel();
                crm.setId(cursor.getInt(0));
                crm.setCode(cursor.getString(1));
                crm.setRate(cursor.getDouble(2));
                crm.setBase(cursor.getInt(3));
                toSend.add(crm);
            }

            cursor.close();
            return toSend;
        }else {
            cursor.close();
            return toSend;
        }
    }

    public boolean insertConversionRates(ArrayList<ConversionRatesModel> listToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(ConversionRatesModel model : listToAdd) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FeedEntry.COL_2_CVR, model.getCode());
            contentValues.put(FeedEntry.COL_3_CVR, model.getRate());
            contentValues.put(FeedEntry.COL_4_CVR, model.getBase());
            contentValues.put(FeedEntry.COL_5_CVR, model.getDate());
            long result = db.insert(FeedEntry.TABLE_CONVERSION_RATES, null, contentValues);
            if(result == -1) {
                return false;
            }
        }
        return true;
    }

    public String getConversionRatesDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        String toSend = "";
        Cursor cursor = db.rawQuery(SQL_SELECT_CONVERSION_RATE_DATE, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            toSend = cursor.getString(0);
            cursor.close();
            return toSend;
        } else {
            cursor.close();
            return toSend;
        }
    }

    public int deleteConversionRates() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(FeedEntry.TABLE_CONVERSION_RATES, null, null);
        return deleted;
    }

    //RETROFIT CONVERSION RATES AND BASE INSERT
    public boolean insertConversionRatesRetrofit(HashMap<String, Double> map) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(Map.Entry<String, Double> entry : map.entrySet()) {
            String name = entry.getKey();
            Double rate = entry.getValue();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FeedEntry.COL_2_CRR, 1);
            contentValues.put(FeedEntry.COL_3_CRR, name);
            contentValues.put(FeedEntry.COL_4_CRR, rate);
            long result = db.insert(FeedEntry.TABLE_CONVERSION_RATES_RETROFIT, null, contentValues);
            if(result == -1) {
                return false;
            }
        }
        return true;
    }

    public boolean insertConversionRatesBaseRetrofit(String base, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COL_2_CRBR, base);
        contentValues.put(FeedEntry.COL_3_CRBR, date);
        long result = db.insert(FeedEntry.TABLE_CONVERSION_RATES_BASE_RETROFIT, null, contentValues);
        return result != -1;

    }

    public int deleteConversionRatesRetrofit() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(FeedEntry.TABLE_CONVERSION_RATES_RETROFIT, null, null);
        return deleted;
    }

    public int deleteConversionRatesBaseRetrofit() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(FeedEntry.TABLE_CONVERSION_RATES_BASE_RETROFIT, null, null);
        return deleted;
    }

    //BANK VALUE LOG INSERT, SELECT...
    public ArrayList<BankValueLog> getAllBankLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<BankValueLog> bankValueLogs = new ArrayList<>();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_BANK_VALUE_LOG, null);
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                BankValueLog bvl = new BankValueLog();
                bvl.setId(cursor.getInt(0));
                bvl.setId_bank(cursor.getInt(1));
                bvl.setId_curr(cursor.getInt(2));
                bvl.setTotal_value(cursor.getDouble(3));
                bvl.setDate(cursor.getString(4));
                bankValueLogs.add(bvl);
            }
        }
        cursor.close();
        return bankValueLogs;
    }

    public ArrayList<BankValueLog> getBankLogsByBankId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<BankValueLog> bankValueLogs = new ArrayList<>();
        Cursor cursor = db.rawQuery(SQL_SELECT_BANK_VALUE_LOG_BY_BANK_ID, new String[] {String.valueOf(id)});
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                BankValueLog bvl = new BankValueLog();
                bvl.setId(cursor.getInt(0));
                bvl.setId_bank(cursor.getInt(1));
                bvl.setId_curr(cursor.getInt(2));
                bvl.setTotal_value(cursor.getDouble(3));
                bvl.setDate(cursor.getString(4));
                bankValueLogs.add(bvl);
            }
        }
        cursor.close();
        return bankValueLogs;
    }

    public boolean insertBankValueLog(BankValueLog bvl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COL_2_BVL, bvl.getId_bank());
        contentValues.put(FeedEntry.COL_3_BVL, bvl.getId_curr());
        contentValues.put(FeedEntry.COL_4_BVL, bvl.getTotal_value());
        contentValues.put(FeedEntry.COL_5_BVL, bvl.getDate());
        long result = db.insert(FeedEntry.TABLE_BANK_VALUE_LOG, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int deleteBankValueLog(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FeedEntry.TABLE_BANK_VALUE_LOG, FeedEntry.COL_2_BVL + " = ?", new String[] {String.valueOf(id)});
    }





/////////////////////////////////////////////////////////////////////////////////////////
////////////// SQL QUERIES  /////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

    //SELECT FROM USER TABLE
    private static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM " + FeedEntry.TABLE_USER + " WHERE _id = ?";
    private static final String SQL_SELECT_USER_BY_USER_PASS = "SELECT * FROM " + FeedEntry.TABLE_USER + " WHERE " + FeedEntry.COL_2_USER +" = ? AND " + FeedEntry.COL_3_USER + " = ?";
    private static final String SQL_SELECT_USERS_DEFAULT_CURRENCY = "SELECT " + FeedEntry.COL_4_USER + " FROM " + FeedEntry.TABLE_USER + " WHERE _id = ?";


    //Select AND Delete all banks and single bank
    private static final String SQL_SELECT_ALL_BANKS = "SELECT * FROM " + FeedEntry.TABLE_BANK;
    private static final String SQL_SELECT_ONE_BANK = "SELECT * FROM " + FeedEntry.TABLE_BANK + " WHERE _id = ?";
    private static final String WHERE_DELETE_ALL_BANKS = "DELETE FROM " + FeedEntry.TABLE_BANK;
    private static final String WHERE_DELETE_ONE_BANK = FeedEntry._ID + " = ?";
    private static final String WHERE_DELETE_BANK_WITH_MONEY = FeedEntry.COL_2_BCC + " = ?";
    private static final String SQL_SELECT_LAST_BANK = "SELECT * FROM " + FeedEntry.TABLE_BANK + " ORDER BY _id DESC LIMIT 1";

    //SELECT FROM BANK MONEY CONNECT
    private static final String SQL_SELECT_BCC_BY_BANK_ID = "SELECT * FROM " + FeedEntry.TABLE_BANK_CURRENCY_CONNECT + " WHERE " + FeedEntry.COL_2_BCC + " = ?";
    private static final String SQL_SELECT_BCC_BY_BANK_ID_AND_CURR_ID = "SELECT * FROM " + FeedEntry.TABLE_BANK_CURRENCY_CONNECT + " WHERE " + FeedEntry.COL_2_BCC + " = ? AND " + FeedEntry.COL_3_BCC + " = ?";

    //SELECT FROM CURRENCY TABLE
    private static final String SQL_SELECT_ID_BY_NAME_CURRENCY = "SELECT * FROM " + FeedEntry.TABLE_CURRENCY + " WHERE curr_name = ?";
    private static final String SQL_SELECT_CURRENCY_BY_CODE = "SELECT * FROM " + FeedEntry.TABLE_CURRENCY + " WHERE " + FeedEntry.COL_3_CURR + " = ?";
    private  static final String SQL_SELECT_ALL_CURRENCIES = "SELECT * FROM " + FeedEntry.TABLE_CURRENCY;
    private static final String SQL_SELECT_CURRENCY_BY_ID = "SELECT * FROM " + FeedEntry.TABLE_CURRENCY + " WHERE " + FeedEntry._ID + " = ?";
    private static final String SQL_SELECT_ALL_CURRENCIES_CODES = "SELECT " + FeedEntry.COL_3_CURR + " FROM " + FeedEntry.TABLE_CURRENCY;
    private static final String SQL_SELECT_ALL_CURRENCIES_CODES_BUT_NOT_DEFAULT = "SELECT " + FeedEntry.COL_3_CURR + " FROM " + FeedEntry.TABLE_CURRENCY + " WHERE " + FeedEntry.COL_3_CURR + " != ?";

    //SELECT FROM TYPES TABLE
    private  static final String SQL_SELECT_ALL_TYPES = "SELECT * FROM " + FeedEntry.TABLE_TYPE;
    private static final String SQL_SELECT_TYPE_BY_NAME = "SELECT * FROM " + FeedEntry.TABLE_TYPE + " WHERE " + FeedEntry.COL_2_TYPE + " = ?";

    //SELECT FROM PICTURES TABLES
    private static final String SQL_SELECT_ALL_PICTURES = "SELECT * FROM " + FeedEntry.TABLE_PICTURES;
    private static final String SQL_SELECT_PICTURE_BY_ID = "SELECT * FROM " + FeedEntry.TABLE_PICTURES + " WHERE " + FeedEntry._ID + " = ?";
    private static final String SQL_SELECT_PICTURES_BY_CURR_ID_AND_TYPE_ID = "SELECT * FROM " +FeedEntry.TABLE_PICTURES + " WHERE " + FeedEntry.COL_2_PIC + " = ? AND " + FeedEntry.COL_4_PIC + " = ?";
    private static final String SQL_SELECT_PICTURES_BY_CURR_ID = "SELECT * FROM " + FeedEntry.TABLE_PICTURES + " WHERE " + FeedEntry.COL_2_PIC + " = ? AND " + FeedEntry.COL_4_PIC + " != 0";
    private static final String SQL_SELECT_PICTURES_BY_TYPE_ID = "SELECT * FROM " + FeedEntry.TABLE_PICTURES + " WHERE " + FeedEntry.COL_4_PIC + " = ?";
    //private static final String SQL_SELECT_ALL_PICTURE_BY_BANK_EXCEPT_SYMBOL = "SELECT * FROM " + FeedEntry.TABLE_PICTURES + " WHERE " + FeedEntry.Pic + " = ?";

    //SELECT FROM CONVERSION RATES TABLE
    private static final String SQL_SELECT_ALL_CONVERSION_RATES = "SELECT * FROM " + FeedEntry.TABLE_CONVERSION_RATES;
    private static final String SQL_SELECT_CONVERSION_RATE_DATE = "SELECT " + FeedEntry.COL_5_CVR + " FROM " + FeedEntry.TABLE_CONVERSION_RATES + " WHERE _id = 1";

    //SELECT FROM BANK VALUE LOG TABLE
    private static final String SQL_SELECT_ALL_BANK_VALUE_LOG = "SELECT * FROM " + FeedEntry.TABLE_BANK_VALUE_LOG;
    private static final String SQL_SELECT_BANK_VALUE_LOG_BY_ID = "SELECT * FROM " + FeedEntry.TABLE_BANK_VALUE_LOG + " WHERE " + FeedEntry._ID + " = ?";
    private static final String SQL_SELECT_BANK_VALUE_LOG_BY_BANK_ID = "SELECT * FROM " + FeedEntry.TABLE_BANK_VALUE_LOG + " WHERE " + FeedEntry.COL_2_BVL + " = ?";

    //CREATE DELETE USER TABLE
    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_USER + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_USER + " TEXT," +
                    FeedEntry.COL_3_USER + " TEXT," +
                    FeedEntry.COL_4_USER + " INTEGER)";
                   /* FeedEntry.COL_5_USER + " TEXT," +
                    FeedEntry.COL_6_USER + " TEXT)";*/

    private static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_USER;

    private static final String SQL_DELETE_USER_TABLE_OLD =
            "DROP TABLE IF EXISTS user_table";

    //CREATE DELETE BANK TABLE
    private static final String SQL_CREATE_BANK_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_BANK + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_BANK + " TEXT," +
                    FeedEntry.COL_3_BANK + " TEXT," +
                    FeedEntry.COL_4_BANK + " TEXT)";

    private static final String SQL_DELETE_BANK_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_BANK;


    //CREATE DELETE CURRENCY TABLE
    private static final String SQL_CREATE_CURRENCY_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_CURRENCY + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_CURR + " TEXT," +
                    FeedEntry.COL_3_CURR + " TEXT," +
                    FeedEntry.COL_4_CURR + " TEXT)";

    private static final String SQL_DELETE_CURRENCY_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_CURRENCY;

    //CREATE DELETE START_VALUE TABLE
    private static final String SQL_CREATE_START_VALUE_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_START_VALUE + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_SV + " INTEGER)";

    private static final String SQL_DELETE_START_VALUE_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_START_VALUE;

    //CREATE DELETE PICTURES TABLE
    private static final String SQL_CREATE_PICTURES_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_PICTURES + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_PIC + " INTEGER," +
                    FeedEntry.COL_3_PIC + " INTEGER, " +
                    FeedEntry.COL_4_PIC + " INTEGER, " +
                    FeedEntry.COL_5_PIC + " REAL)";

    private static final String SQL_DELETE_PICTURES_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_PICTURES;

    //CREATE DELETE TYPE TABLE
    private static final String SQL_CREATE_TYPE_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_TYPE + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_TYPE + " TEXT)";

    private static final String SQL_DELETE_TYPE_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_TYPE;

    //CREATE DELETE BANK CURRENCY CONNECT TABLE TABLE_BANK_CURRENCY_CONNECT
    private static final String SQL_CREATE_BANK_CURRENCY_CONNECT_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_BANK_CURRENCY_CONNECT + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_BCC + " INTEGER," +
                    FeedEntry.COL_3_BCC + " INTEGER, " +
                    FeedEntry.COL_4_BCC + " INTEGER, " +
                    FeedEntry.COL_5_BCC + " INTEGER, " +
                    FeedEntry.COL_6_BCC + " INTEGER, " +
                    FeedEntry.COL_7_BCC + " TEXT)";

    private static final String SQL_DELETE_BANK_CURRENCY_CONNECT_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_BANK_CURRENCY_CONNECT;


    //CREATE DELETE START_VALUE_BANK_CONNECT TABLE
    private static final String SQL_CREATE_START_VALUE_BANK_CONNECT_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_START_VALUE_BANK_CONNECT + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_SVBC + " INTEGER," +
                    FeedEntry.COL_3_SVBC + " INTEGER, " +
                    FeedEntry.COL_4_SVBC + " INTEGER)";

    private static final String SQL_DELETE_START_VALUE_BANK_CONNECT_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_START_VALUE_BANK_CONNECT;


    //CREATE DELETE CONVERSION RATES TABLE
    private static final String SQL_CREATE_CONVERSION_RATES_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_CONVERSION_RATES + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_CVR + " TEXT," +
                    FeedEntry.COL_3_CVR + " REAL," +
                    FeedEntry.COL_4_CVR + " INTEGER," +
                    FeedEntry.COL_5_CVR + " TEXT)";

    private static final String SQL_DELETE_CONVERSION_RATES_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_CONVERSION_RATES;

    //CREATE DELETE BANK_VALUE_LOG TABLE
    private static final String SQL_CREATE_BANK_VALUE_LOG_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_BANK_VALUE_LOG + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_BVL + " INTEGER," +
                    FeedEntry.COL_3_BVL + " INTEGER," +
                    FeedEntry.COL_4_BVL + " REAL," +
                    FeedEntry.COL_5_BVL + " TEXT)";

    private static final String SQL_DELETE_BANK_VALUE_LOG_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_BANK_VALUE_LOG;

    //CREATE DELETE CONVERSION_RATES_BASE_RETROFIT TABLE
    private static final String SQL_CREATE_CONVERSION_RATES_BASE_RETROFIT_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_CONVERSION_RATES_BASE_RETROFIT + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_CRBR + " TEXT," +
                    FeedEntry.COL_3_CRBR + " TEXT)";

    private static final String SQL_DELETE_CONVERSION_RATES_BASE_RETROFIT_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_CONVERSION_RATES_BASE_RETROFIT;

    //CRATE DELETE CONVERSION_RATES_RETROFIT
    private static final String SQL_CREATE_CONVERSION_RATES_RETROFIT_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_CONVERSION_RATES_RETROFIT + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_2_CRR + " INTEGER," +
                    FeedEntry.COL_3_CRR + " TEXT," +
                    FeedEntry.COL_4_CRR + " REAL)";

    private static final String SQL_DELETE_CONVERSION_RATES_RETROFIT_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_CONVERSION_RATES_RETROFIT;


    //DEFAULT INSERTS

    //TYPES
    private static final String SQL_INSERT_DEFAULT_TYPES =
            "INSERT INTO " + FeedEntry.TABLE_TYPE +
                " SELECT null AS " + FeedEntry._ID + ", 'Coin' AS " + FeedEntry.COL_2_TYPE +
            " UNION ALL SELECT null, 'Banknote'";

    //CURRENCIES
    private static final String SQL_INSERT_DEFAULT_CURRENCIES =
            "INSERT INTO " + FeedEntry.TABLE_CURRENCY +
                    " SELECT null AS " + FeedEntry._ID + ", 'Euro' AS " + FeedEntry.COL_2_CURR + ", 'EUR' AS " + FeedEntry.COL_3_CURR + ", '€' AS " + FeedEntry.COL_4_CURR +
                    " UNION ALL SELECT null, 'US dollar', 'USD', '$'" +
                    " UNION ALL SELECT null, 'Pound sterling', 'GBP', '£'" +
                    " UNION ALL SELECT null, 'Hrvatska Kuna', 'HRK', 'kn'";


    //PICTURES
    private static final String SQL_INSERT_DEFAULT_PICTURES =
            "INSERT INTO " + FeedEntry.TABLE_PICTURES +
                    //KUNE
                    " SELECT null AS " + FeedEntry._ID + ", 4 AS " + FeedEntry.COL_2_PIC + ", " + images[0] + " AS " + FeedEntry.COL_3_PIC + ", 0 AS " + FeedEntry.COL_4_PIC + ", 0 AS " + FeedEntry.COL_5_PIC +
                    " UNION ALL SELECT null, 4, " + images[1] + ", 1, 1" +
                    " UNION ALL SELECT null, 4, " + images[2] + ", 1, 2" +
                    " UNION ALL SELECT null, 4, " + images[3] + ", 1, 5" +
                    " UNION ALL SELECT null, 4, " + images[4] + ", 2, 10" +
                    " UNION ALL SELECT null, 4, " + images[5] + ", 2, 20" +
                    " UNION ALL SELECT null, 4, " + images[6] + ", 2, 50" +
                    " UNION ALL SELECT null, 4, " + images[7] + ", 2, 100" +
                    " UNION ALL SELECT null, 4, " + images[8] + ", 2, 200" +
                    " UNION ALL SELECT null, 4, " + images[9] + ", 2, 500" +
                    " UNION ALL SELECT null, 4, " + images[10] + ", 2, 1000" +
                    //EURI
                    " UNION ALL SELECT null, 1, " + images[11] + ", 0, 0" +
                    " UNION ALL SELECT null, 1, " + images[12] + ", 1, 1" +
                    " UNION ALL SELECT null, 1, " + images[13] + ", 1, 2" +
                    " UNION ALL SELECT null, 1, " + images[14] + ", 2, 5" +
                    " UNION ALL SELECT null, 1, " + images[15] + ", 2, 10" +
                    " UNION ALL SELECT null, 1, " + images[16] + ", 2, 20" +
                    " UNION ALL SELECT null, 1, " + images[17] + ", 2, 50" +
                    " UNION ALL SELECT null, 1, " + images[18] + ", 2, 100" +
                    " UNION ALL SELECT null, 1, " + images[19] + ", 2, 200" +
                    " UNION ALL SELECT null, 1, " + images[20] + ", 2, 500" +
                    //US DOLLARS
                    " UNION ALL SELECT null, 2, " + images[21] + ", 0, 0" +
                    " UNION ALL SELECT null, 2, " + images[22] + ", 1, 1" +
                    " UNION ALL SELECT null, 2, " + images[23] + ", 2, 1" +
                    " UNION ALL SELECT null, 2, " + images[24] + ", 2, 2" +
                    " UNION ALL SELECT null, 2, " + images[25] + ", 2, 5" +
                    " UNION ALL SELECT null, 2, " + images[26] + ", 2, 10" +
                    " UNION ALL SELECT null, 2, " + images[27] + ", 2, 20" +
                    " UNION ALL SELECT null, 2, " + images[28] + ", 2, 50" +
                    " UNION ALL SELECT null, 2, " + images[29] + ", 2, 100" +
                    //POUND STERLING
                    " UNION ALL SELECT null, 3, " + images[30] + ", 0, 0" +
                    " UNION ALL SELECT null, 3, " + images[31] + ", 1, 1" +
                    " UNION ALL SELECT null, 3, " + images[32] + ", 1, 2" +
                    " UNION ALL SELECT null, 3, " + images[33] + ", 2, 5" +
                    " UNION ALL SELECT null, 3, " + images[34] + ", 2, 10" +
                    " UNION ALL SELECT null, 3, " + images[35] + ", 2, 20" +
                    " UNION ALL SELECT null, 3, " + images[36] + ", 2, 50";



//    ššššššđđđ


}
