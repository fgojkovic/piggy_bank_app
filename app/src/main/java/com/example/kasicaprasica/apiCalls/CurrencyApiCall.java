package com.example.kasicaprasica.apiCalls;

import android.content.Context;

import androidx.camera.camera2.internal.compat.quirk.StillCaptureFlashStopRepeatingQuirk;

import com.example.kasicaprasica.models.MyCurrency;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrencyApiCall {

    private String apiCall;
    private String url;
    private String baseCurrency;

    private MyCurrency defaultCurrency;
    private MyCurrency currentCurrency;
    private MyCurrency ultimateCurrency;

    private Context context;

    private JSONObject jsonObject;

    public CurrencyApiCall(Context context, String apiCall){
        this.context = context;
        this.apiCall = apiCall;
    }




    public void getApiResult() {

        //{"base":"HRK","rates":{"GBP":0.1160671336,"USD":0.1599577111,"EUR":0.1321527686},"date":"2021-02-09"}
        //String apiCall = "https://api.ratesapi.io/api/latest?base=" + baseCurrency + "&symbols=GBP,EUR,USD";



        // Instantiate the RequestQueue.
        //RequestQueue queue = Volley.newRequestQueue(context);
        //final String url ="https://www.google.com";

        // Request a string response from the provided URL.
       /* StringRequest stringRequest = new StringRequest(Request.Method.GET, apiCall,
                response -> {
                    try {
                        jsonObject = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    jsonObject = null;
                }
        );

        // Add the request to the RequestQueue.
        queue.add(stringRequest);*/
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
