package com.lukaspaczos.currencyconverter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.lukaspaczos.currencyconverter.currency.Currency;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import me.grantland.widget.AutofitTextView;

//TODO translation to Polish
public class MainActivity extends AppCompatActivity {

    private double input = 0;
    private SharedPreferences sharedPref;
    public String currencyFrom;
    public String currencyTo;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //TODO remove before store upload
        //adView.loadAd(adRequest);

        sharedPref = this.getSharedPreferences("default_currencies", Context.MODE_PRIVATE);
        getSharedPreferences();

        Log.i("Preferences From", sharedPref.getString(getString(R.string.preference_from), getResources().getString(R.string.default_from)));
        Log.i("Preferences To", sharedPref.getString(getString(R.string.preference_to), getResources().getString(R.string.default_to)));

        setViews();

        AutofitTextView startingAmount = (AutofitTextView) findViewById(R.id.currency_starting);
        startingAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle(R.string.converter_choose_amount);
                final EditText inputView = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputView.setLayoutParams(lp);
                inputView.setSingleLine(true);
                inputView.setMaxLines(1);
                inputView.setLines(1);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(15);
                inputView.setFilters(filterArray);
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
                AlertDialog dialog = alertDialog.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();

            }
        });

        ImageView swapView = (ImageView) findViewById(R.id.swap_currencies);
        swapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.preference_from), currencyTo);
                Log.i("Change from", "done");
                editor.putString(getString(R.string.preference_to), currencyFrom);
                Log.i("Change to", "done");
                editor.apply();
                getSharedPreferences();
                setViews();
                calculateOutcome(input);
            }
        });
        calculateOutcome(input);
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putDouble("input", input);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        input = savedInstanceState.getDouble("input");
        calculateOutcome(input);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            this.moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void calculateOutcome(double input) {
        getSharedPreferences();
        double rateFrom = 1;
        double rateTo = 1;
        for (Currency c : Currency.list) {
            if (c.getShortName().equals(currencyFrom)) {
                rateFrom = c.getRate();
            }
            if (c.getShortName().equals(currencyTo)) {
                rateTo = c.getRate();
            }
        }

        double inputInEuro = input / rateFrom;
        double outcome = inputInEuro * rateTo;

        AutofitTextView amountFrom = (AutofitTextView) findViewById(R.id.currency_starting);
        amountFrom.setText(String.format(Locale.US, "%.2f", input));
        AutofitTextView amountOutcome = (AutofitTextView) findViewById(R.id.currency_outcome);
        amountOutcome.setText(String.format(Locale.US, "%.2f", outcome));
    }

    private void getSharedPreferences() {
        currencyFrom = sharedPref.getString(getString(R.string.preference_from), getResources().getString(R.string.default_from));
        currencyTo = sharedPref.getString(getString(R.string.preference_to), getResources().getString(R.string.default_to));
    }

    private void setViews() {
        TextView currencyFromTv = (TextView) findViewById(R.id.currency_from);
        currencyFromTv.setText(currencyFrom);
        LinearLayout currencyFromLayout = (LinearLayout) findViewById(R.id.layout_from);
        currencyFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseCurrencyActivity.class);
                intent.putExtra("TYPE", 0);
                startActivityForResult(intent, 1);
            }
        });

        TextView currencyToTv = (TextView) findViewById(R.id.currency_to);
        currencyToTv.setText(currencyTo);
        LinearLayout currencyToLayout = (LinearLayout) findViewById(R.id.layout_to);
        currencyToLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseCurrencyActivity.class);
                intent.putExtra("TYPE", 1);
                startActivityForResult(intent, 1);
            }
        });

        ImageView currencyFromFlag = (ImageView) findViewById(R.id.currency_from_flag);
        currencyFromFlag.setBackgroundResource(Currency.flagsMap.get(currencyFrom));

        ImageView currencyToFlag = (ImageView) findViewById(R.id.currency_to_flag);
        currencyToFlag.setBackgroundResource(Currency.flagsMap.get(currencyTo));

        /*TextView currencyToHintTv = (TextView) findViewById(R.id.currency_to_hint);
        currencyToHintTv.setText(currencyTo);*/

        /*TextView currencyFromHintTv = (TextView) findViewById(R.id.currency_from_hint);
        currencyFromHintTv.setText(currencyFrom);*/

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
        if (id == R.id.action_about) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.action_about));
            final TextView textView = (TextView) getLayoutInflater().inflate(R.layout.about_view, null, false);
            textView.setText(getText(R.string.about_text));
            textView.setMovementMethod(new ScrollingMovementMethod());
            alertDialog.setView(textView);
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
            return true;
        }
        if (id == R.id.action_update) {
            CheckDownloadComplete.isDownloadComplete = false;
            RatesUpdate.update(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Activity getActivity() {
        Activity activity = null;
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    activity = (Activity) activityField.get(activityRecord);
                }
            }

        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }
        return activity;
    }
}
