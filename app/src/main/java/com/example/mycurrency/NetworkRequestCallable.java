package com.example.mycurrency;


import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.Callable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NetworkRequestCallable implements Callable<Double> {
    private final LocalDate data;
    private static final String API_URL = "https://openexchangerates.org/api//historical/%s.json?app_id=d7f89e645c4545e8915c17c1d3fe95fe";
    private final String fromCurrency,toCurrency;
    NetworkRequestCallable(LocalDate data, String fromCurrency, String toCurrency){
      this.data=data;
      this.fromCurrency=fromCurrency;
      this.toCurrency=toCurrency;
    }
    @Override
    public Double call() {

        OkHttpClient client = new OkHttpClient();
        String requestUrl = String.format(API_URL, data);

        //richiesta di rete
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String responseBody = response.body().string();

            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonObject rates = jsonObject.getAsJsonObject("rates");

            //le API di openexchangerates.org convertono solo da USD ad altre valute,
            // tramite queste operazioni riesco a trovare il rate di ogni valuta.
            double fromRate = rates.get(fromCurrency).getAsDouble();
            double toRate = rates.get(toCurrency).getAsDouble();

            return toRate / fromRate;
        } catch (IOException e) {
            e.printStackTrace();
            return -1.0;
        }
    }
}

