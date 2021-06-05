package com.example.kasicaprasica.interfaces;

import com.example.kasicaprasica.models.ConversionRatesJsonModel;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    //https://api.ratesapi.io/api/latest?base=HRK&symbols=GBP,EUR,USD
    @GET("/api/latest?")
    Call<ConversionRatesJsonModel> getConversionRateFor(@Query("base") String base, @Query("symbols") String symbols);

    @GET("/api/latest")
    Call<ConversionRatesJsonModel> getAllConversionRates();

    /*@GET("/api/unknown")
    Call<MultipleResources> doGetListResources();



    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);*/

    //ostalo od prije neka ostane da vidim i post
    /*@FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);

    @POST("/api/users")
    Call<UserForApi> createUser(@Body UserForApi user);*/

}
