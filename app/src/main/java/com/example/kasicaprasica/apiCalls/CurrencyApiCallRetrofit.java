package com.example.kasicaprasica.apiCalls;

//import com.journaldev.retrofitintro.pojo.MultipleResource;
//import com.example.kasicaprasica.apiCalls.MultipleResource;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyApiCallRetrofit {

    private static Retrofit retrofit = null;

    public static Retrofit getCurrencyRates() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        //https://api.ratesapi.io/api/latest?base=HRK&symbols=GBP,EUR,USD
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.ratesapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }


}
