package com.example.mycurrency;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.Spinner;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.IntentFilter;
import android.annotation.SuppressLint;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Double[] rates;
    private EditText valueEditText;
    private CurrencyList preferiti;
    private Salvataggio salvataggio;
    private static String lastMessage;
    private LineChart exchangeRateChart;
    private List<Future<Double>>futureList;
    private ExecutorService executorService;
    private Spinner currencyStartSpinner,currencyEndSpinner;
    private static final List<Entry> entries= new ArrayList<>();

    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        salvataggio = new Salvataggio(this);
        preferiti = salvataggio.carica();//carico i preferiti salvati nelle precedenti esecuzioni

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currencyStartSpinner = findViewById(R.id.currencyStartSpinner);
        currencyEndSpinner = findViewById(R.id.currencyEndSpinner);
        currencyStartSpinner.setOnItemSelectedListener(this);
        currencyEndSpinner.setOnItemSelectedListener(this);
        valueEditText = findViewById(R.id.valueEditText);

        String[] currencies = {"USD","EUR","BTC","AUD","BRL","CAD","CNY","CZK","DKK","HKD","HUF","ILS","JPY","MYR","MXN","TWD","NZD","NOK","PHP","PLN","GBP","SGD","SEK","CHF","THB"};

        //assegnazione valori spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencyStartSpinner.setAdapter(adapter);
        currencyEndSpinner.setAdapter(adapter);

        //ascolto notifica di PreferitiActivity
        Ricevitore ricevitore = new Ricevitore(currencyStartSpinner,currencyEndSpinner,adapter);
        registerReceiver(ricevitore, new IntentFilter("passovalori"));

        Button calculateButton = findViewById(R.id.calculateButton);
        TextView resultTextView = findViewById(R.id.resultTextView);
        exchangeRateChart = findViewById(R.id.exchangeRateChart);

        calculateButton.setOnClickListener(v -> { // OnCLick
            final LocalDate[] data = {LocalDate.now()};
            //resetto l'array dei rates
            entries.clear();
            String inputValueStr = valueEditText.getText().toString();
            //se l'utente non inserisce un valore da calcolare, di default sara' 1
            double inputValue = 1;
            try {
                inputValue= Double.parseDouble(inputValueStr);
            }catch(Exception ignored){}

            String selectedCurrencyStart = currencyStartSpinner.getSelectedItem().toString();
            String selectedCurrencyEnd = currencyEndSpinner.getSelectedItem().toString();

            //eseguo 7 chiamate di rete ( per ottenere i 7 tassi precedenti )
            //salvo i tassi in un array di Future
            executorService = Executors.newFixedThreadPool(7);
            futureList = new ArrayList<>();
            rates = new Double[7];
            for (int i = 0; i < 7; i++) {
                Callable<Double> callable = new NetworkRequestCallable(data[0],selectedCurrencyStart,selectedCurrencyEnd);
                Future<Double> future = executorService.submit(callable);
                futureList.add(future);
                data[0] = data[0].minusDays(1);
            }
            processResults();

            //messaggio di output con i valori del giorno corrente
            double valoreConvertito = inputValue*rates[0];
            lastMessage = String.format("Tasso di cambio: %.8f\nValore convertito: %.8f %s", rates[0], valoreConvertito, selectedCurrencyEnd);
            resultTextView.setText(lastMessage);

            for (int i=7; i>0; i--){
                entries.add(new Entry(8-i,Float.parseFloat(rates[i-1].toString())));
            }
            updateChart();
        });
        //aggiorno il grafico con i nuovi valori
        if(entries!=null) updateChart();
        if (lastMessage!=null) resultTextView.setText(lastMessage);
    }
    private void updateChart() {
        LineDataSet dataSet = new LineDataSet(MainActivity.entries, "Tasso di cambio negli ultimi 7 giorni");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);
        exchangeRateChart.setData(lineData);
        exchangeRateChart.getDescription().setEnabled(false);
        exchangeRateChart.getAxisRight().setEnabled(false);

        //assegno alle ascisse i giorni
        XAxis xAxis = exchangeRateChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                LocalDate giornomenosette = LocalDate.now().minusDays(7);
                LocalDate giorno=giornomenosette.plusDays((long) value);
                DateTimeFormatter dtf =DateTimeFormatter.ofPattern("dd/MM");
                return dtf.format(giorno);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        exchangeRateChart.invalidate();

    }
    private void processResults() {
            //processo i Future ottenuti dalla richiesta di rete
            for (int i = 0; i < futureList.size(); i++) {
                Future<Double> future = futureList.get(i);
                try {
                    Double result = future.get();
                    rates[i]=result;
                }catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        executorService.shutdown();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem stella = menu.findItem(R.id.action_add_favorites);
        boolean contenuto=preferiti.contains(new Coppia(currencyStartSpinner.getSelectedItem().toString(),currencyEndSpinner.getSelectedItem().toString()));
        //se la coppia corrente e' gia nei preferiti, la stella sara' piena
        if(contenuto) {
            stella.setIcon(R.drawable.ic_stella_piena);
        }else{//altrimenti sara' vuota
            stella.setIcon(R.drawable.ic_stella_vuota);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_add_favorites) {
            String partenza =currencyStartSpinner.getSelectedItem().toString();
            String arrivo =currencyEndSpinner.getSelectedItem().toString();
            Coppia p = new Coppia(partenza,arrivo);
            if(preferiti.contains(p)){
                //se la coppia e' gia presente nei preferiti, cliccando sulla stella
                // la coppia verra' rimossa e la stella si svuotera'
                int index = preferiti.getIndexFromPair(p);
                preferiti.rimuovi(index);
            }else{
                //se la coppia non era effettivamente presente nei preferiti,
                // cliccando sulla stella faro' l'aggiunta e la stella si riempira'
                preferiti.aggiungi(partenza,arrivo);
            }
            //salvo in memoria l'array di preferiti
            salvataggio.salva(preferiti);
            invalidateOptionsMenu();
            return true;
        }else if (itemId == R.id.action_view_favorites) {
            //creo un intent verso la PreferitiActivity
            Intent intent = new Intent(this,PreferitiActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("array",preferiti);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        invalidateOptionsMenu();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}