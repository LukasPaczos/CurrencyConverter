package com.lukaspaczos.currencyconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

//TODO find bugs
public class MainActivity extends AppCompatActivity {

    public static Context contextMain;
    private double input = 0;
    private SharedPreferences sharedPref;
    public String currencyFrom;
    public String currencyTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextMain = this;

        sharedPref = contextMain.getSharedPreferences("default_currencies", Context.MODE_PRIVATE);
        getSharedPreferences();

        Log.i("Preferences From", sharedPref.getString(getString(R.string.preference_from), getResources().getString(R.string.default_from)));
        Log.i("Preferences To", sharedPref.getString(getString(R.string.preference_to), getResources().getString(R.string.default_to)));

        File xmlFile = new File(this.getExternalFilesDir(null).toString() + "/Download/" + RatesUpdate.FILE_NAME);
        if (xmlFile.exists()) {
            RatesUpdate.parse(this);
        } else {
            RatesUpdate.update(this);
        }


        setViews();

        TextView startingAmount = (TextView) findViewById(R.id.currency_starting);
        startingAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(contextMain);
                alertDialog.setTitle(R.string.converter_choose_amount);
                final EditText inputView = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputView.setLayoutParams(lp);
                inputView.setSingleLine(true);
                inputView.setMaxLines(1);
                inputView.setLines(1);
                inputView.setHint(String.format(Locale.US, "%.2f", input));
                inputView.setRawInputType(Configuration.KEYBOARD_12KEY);
                alertDialog.setView(inputView);
                alertDialog.setCancelable(true);
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!inputView.getText().toString().equals("")) {
                            input = Double.valueOf(inputView.getText().toString());
                            calculateOutcome(input);
                        }
                    }
                });
                //reset button doesnt work yet
                /*alertDialog.setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        inputView.setText("");
                    }
                });*/
                AlertDialog dialog = alertDialog.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();

            }
        });
        calculateOutcome(input);
    }

    private void calculateOutcome(double input) {
        getSharedPreferences();
        double rateFrom = 1;
        double rateTo = 1;
        for (Currency c: Currency.list) {
            if (c.getShortName().equals(currencyFrom)) {
                rateFrom = c.getRate();
            }
            if (c.getShortName().equals(currencyTo)) {
                rateTo = c.getRate();
            }
        }

        double inputInEuro = input / rateFrom;
        double outcome = inputInEuro * rateTo;

        TextView amountFrom = (TextView) findViewById(R.id.currency_starting);
        amountFrom.setText(String.format(Locale.US, "%.2f", input));
        TextView amountOutcome = (TextView) findViewById(R.id.currency_outcome);
        amountOutcome.setText(String.format(Locale.US, "%.2f", outcome));
    }

    private void getSharedPreferences() {
        currencyFrom = sharedPref.getString(getString(R.string.preference_from), getResources().getString(R.string.default_from));
        currencyTo = sharedPref.getString(getString(R.string.preference_to), getResources().getString(R.string.default_to));
    }

    private void setViews() {
        TextView currencyFromTv = (TextView) findViewById(R.id.currency_from);
        currencyFromTv.setText(currencyFrom);
        currencyFromTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contextMain, ChooseCurrencyActivity.class);
                intent.putExtra("TYPE", 0);
                startActivityForResult(intent, 1);
            }
        });

        TextView currencyToTv = (TextView) findViewById(R.id.currency_to);
        currencyToTv.setText(currencyTo);
        currencyToTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contextMain, ChooseCurrencyActivity.class);
                intent.putExtra("TYPE", 1);
                startActivityForResult(intent, 1);
            }
        });

        TextView currencyToHintTv = (TextView) findViewById(R.id.currency_to_hint);
        currencyToHintTv.setText(currencyTo);

        TextView currencyFromHintTv = (TextView) findViewById(R.id.currency_from_hint);
        currencyFromHintTv.setText(currencyFrom);

        TextView date = (TextView) findViewById(R.id.date);
        date.setText(sharedPref.getString(getString(R.string.date), getResources().getString(R.string.default_date)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("requestCode", Integer.toString(requestCode));
        Log.i("resultCode", Integer.toString(resultCode));
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                getSharedPreferences();
                setViews();
                calculateOutcome(input);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_update) {
            CheckDownloadComplete.isDownloadComplete = false;
            RatesUpdate.update(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
