package com.lukaspaczos.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lukaspaczos.currencyconverter.currency.Currency;
import com.lukaspaczos.currencyconverter.currency.CurrencyAdapter;

public class ChooseCurrencyActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private static final int CHANGE_FROM = 0;
    public int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_currency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = this.getSharedPreferences("default_currencies", Context.MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("TYPE");
        }

        CurrencyAdapter adapter = new CurrencyAdapter(this, Currency.list);

        ListView listView = (ListView) findViewById(R.id.list_currencies);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String newCurrency = ((Currency)adapterView.getItemAtPosition(i)).getShortName();
                changeCurrency(newCurrency, type);
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    public void changeCurrency(String newCurrency, int type) {
        SharedPreferences.Editor editor = sharedPref.edit();
        if (type == CHANGE_FROM) {
            editor.putString(getString(R.string.preference_from), newCurrency);
        } else {
            editor.putString(getString(R.string.preference_to), newCurrency);
        }
        editor.apply();
    }
}
