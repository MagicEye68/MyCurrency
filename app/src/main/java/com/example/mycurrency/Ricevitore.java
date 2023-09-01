package com.example.mycurrency;

import java.util.Objects;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Spinner;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.content.BroadcastReceiver;

public class Ricevitore extends BroadcastReceiver {
    private final Spinner spinner1, spinner2;
    private final ArrayAdapter<String> adapter;
    public Ricevitore(Spinner spinner1, Spinner spinner2, ArrayAdapter<String> adapter) {
        this.spinner1 = spinner1;
        this.spinner2 = spinner2;
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //ricevo gli intent,spacchetto il bundle e setto gli spinner in MainActivity
        if (Objects.equals(intent.getAction(), "passovalori")) {
            Bundle bundle = intent.getExtras();
            assert bundle != null;

            String string1 = bundle.getString("partenza");
            String string2 = bundle.getString("arrivo");
            int index1 = adapter.getPosition(string1);
            int index2 = adapter.getPosition(string2);

            spinner1.setSelection(index1);
            spinner2.setSelection(index2);
        }
    }
}
