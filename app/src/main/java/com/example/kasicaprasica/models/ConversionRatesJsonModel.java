package com.example.kasicaprasica.models;

import com.example.kasicaprasica.apiCalls.CurrencyApiCallRetrofit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConversionRatesJsonModel {

    @SerializedName("base")
    public String base;

    @SerializedName("rates")
    public CurrencyRatesRetrofitApiModel rates;

    public static class CurrencyRatesRetrofitApiModel {
        @SerializedName("EUR")
        public double eurRate;
        @SerializedName("GBP")
        public double gbpRate;
        @SerializedName("HKD")
        public double hkdRate;
        @SerializedName("IDR")
        public double idrRate;
        @SerializedName("ILS")
        public double ilsRate;
        @SerializedName("DKK")
        public double dkkRate;
        @SerializedName("INR")
        public double inrRate;
        @SerializedName("CHF")
        public double chfRate;
        @SerializedName("MXN")
        public double mxnRate;
        @SerializedName("CZK")
        public double czkRate;
        @SerializedName("SGD")
        public double sgdRate;
        @SerializedName("THB")
        public double thbRate;
        @SerializedName("HRK")
        public double hrkRate;
        @SerializedName("MYR")
        public double myrRate;
        @SerializedName("NOK")
        public double nokRate;
        @SerializedName("CNY")
        public double cnyRate;
        @SerializedName("BGN")
        public double bgnRate;
        @SerializedName("PHP")
        public double phpRate;
        @SerializedName("SEK")
        public double sekRate;
        @SerializedName("PLN")
        public double plnRate;
        @SerializedName("ZAR")
        public double zarRate;
        @SerializedName("CAD")
        public double cadRate;
        @SerializedName("ISK")
        public double iskRate;
        @SerializedName("BRL")
        public double brlRate;
        @SerializedName("RON")
        public double ronRate;
        @SerializedName("NZD")
        public double nzdRate;
        @SerializedName("TRY")
        public double tryRate;
        @SerializedName("JPY")
        public double jpyRate;
        @SerializedName("RUB")
        public double rubRate;
        @SerializedName("KRW")
        public double kewRate;
        @SerializedName("USD")
        public double usdRate;
        @SerializedName("HUF")
        public double hufRate;
        @SerializedName("AUD")
        public double audRate;
    }

    @SerializedName("date")
    public String date;

}
