package com.example.mycurrency;

import java.util.Objects;

import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PreferitiActivity extends AppCompatActivity {// View preferiti

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferiti);
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView lista = findViewById(R.id.lista);

        //richiedo i bundle mandati da MainActivity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        CurrencyList array;
        assert bundle != null;
        array = bundle.getSerializable("array", CurrencyList.class);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lista.setLayoutManager(layoutManager);
        AdattatoreLista adapter = new AdattatoreLista(array);
        lista.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // pop della view dallo stack
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}