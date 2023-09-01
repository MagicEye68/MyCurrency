package com.example.mycurrency;

import java.lang.reflect.Type;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Salvataggio {
    private static final String PREF_NAME = "Preferiti";
    private static final String KEY_ARRAY = "Array";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public Salvataggio(Context context){
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    public void salva(CurrencyList array) {
        String json = gson.toJson(array);
        sharedPreferences.edit().putString(KEY_ARRAY, json).apply();
    }
    public CurrencyList carica() {
        String json = sharedPreferences.getString(KEY_ARRAY, null);
        if (json != null) {
            Type type = new TypeToken<CurrencyList>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new CurrencyList();
    }
}
