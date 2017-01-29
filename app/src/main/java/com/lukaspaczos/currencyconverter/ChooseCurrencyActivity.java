package com.lukaspaczos.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lukaspaczos.currencyconverter.currency.Currency;
import com.lukaspaczos.currencyconverter.currency.CurrencyAdapter;

public class ChooseCurrencyActivity extends AppCompatActivity {

    private static final int CHANGE_FROM = 0;
    public int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_currency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        if (Currency.list.isEmpty())
            Toast.makeText(this, R.string.error_try_update, Toast.LENGTH_SHORT).show();

    }

    public void changeCurrency(String newCurrency, int type) {
        if (type == CHANGE_FROM) {
            PrefsManager.setString(PrefsManager.PREFERENCE_FROM, newCurrency);
        } else {
            PrefsManager.setString(PrefsManager.PREFERENCE_TO, newCurrency);
        }
    }
}
