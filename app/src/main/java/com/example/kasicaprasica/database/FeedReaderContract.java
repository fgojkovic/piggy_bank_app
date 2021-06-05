package com.example.kasicaprasica.database;

import android.provider.BaseColumns;

import java.util.Locale;

public final class FeedReaderContract {

    private FeedReaderContract() {}

    //inner class that defines class contents
    public static class FeedEntry implements BaseColumns {

        //TABLE USER
        public static final String TABLE_USER = "table_user";
        public static final String COL_1_USER = "id";
        public static final String COL_2_USER = "username";
        public static final String COL_3_USER = "password";
        public static final String COL_4_USER = "def_curr_id";
        /*public static final String COL_5_USER = "first_name";
        public static final String COL_6_USER = "last_name";*/


        //TABLE BANK
        public static final String TABLE_BANK = "table_bank";
        public static final String COL_1_BANK = "id";
        public static final String COL_2_BANK = "bank_name";
        public static final String COL_3_BANK = "date_of_creation";
        public static final String COL_4_BANK = "image_path";

        //TABLE CURRENCY
        public static final String TABLE_CURRENCY = "table_currency";
        public static final String COL_1_CURR = "id";
        public static final String COL_2_CURR = "curr_name";
        public static final String COL_3_CURR = "curr_code";
        public static final String COL_4_CURR = "curr_symbol";

        //TABLE START_VALUE
        public static final String TABLE_START_VALUE = "table_start_value";
        public static final String COL_1_SV = "id";
        public static final String COL_2_SV = "id_banks";

        //TABLE PICTURES
        public static final String TABLE_PICTURES = "table_pictures";
        public static final String COL_1_PIC = "id";
        public static final String COL_2_PIC = "id_curr";
        public static final String COL_3_PIC = "location";
        public static final String COL_4_PIC = "id_type";
        public static final String COL_5_PIC = "value";

        //TABLE TYPE
        public static final String TABLE_TYPE = "table_type";
        public static final String COL_1_TYPE = "id";
        public static final String COL_2_TYPE = "name";

        //CONNECTION TABLE BANK CURRENCY
        public static final String TABLE_BANK_CURRENCY_CONNECT = "table_bank_currency_connect";
        public static final String COL_1_BCC = "id";
        public static final String COL_2_BCC = "id_bank";
        public static final String COL_3_BCC = "id_curr";
        public static final String COL_4_BCC = "id_type";
        public static final String COL_5_BCC = "id_money_pic";
        public static final String COL_6_BCC = "amount";
        public static final String COL_7_BCC = "date";

        //CONNECTION TABLE START_VALUE BANK
        public static final String TABLE_START_VALUE_BANK_CONNECT = "table_start_value_bank_connect";
        public static final String COL_1_SVBB = "id";
        public static final String COL_2_SVBC = "id_start";
        public static final String COL_3_SVBC = "id_curr";
        public static final String COL_4_SVBC = "value";

        //TABLE CONVERSION RATES
        public static final String TABLE_CONVERSION_RATES = "table_conversion_rates";
        public static final String COL_1_CVR = "id";
        public static final String COL_2_CVR = "code";
        public static final String COL_3_CVR = "rate";
        public static final String COL_4_CVR = "base";
        public static final String COL_5_CVR = "date";

        //TABLE BANK_VALUE_LOG
        public static final String TABLE_BANK_VALUE_LOG = "table_bank_value_log";
        public static final String COL_1_BVL = "id";
        public static final String COL_2_BVL = "id_bank";
        public static final String COL_3_BVL = "id_curr";
        public static final String COL_4_BVL = "total_value";
        public static final String COL_5_BVL = "date";

        //TABLE CONVERSION_RATES_BASE_RETROFIT
        public static final String TABLE_CONVERSION_RATES_BASE_RETROFIT = "table_conversion_rates_base_retrofit";
        public static final String COL_2_CRBR = "base";
        public static final String COL_3_CRBR = "date";

        //TABLE CONVERSION_RATES_RETROFIT
        public static final String TABLE_CONVERSION_RATES_RETROFIT = "table_conversion_rates_retrofit";
        public static final String COL_2_CRR = "id_base";
        public static final String COL_3_CRR = "name";
        public static final String COL_4_CRR = "rate";
    }


}
