package com.lukaspaczos.currencyconverter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.lukaspaczos.currencyconverter.currency.Currency;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import me.grantland.widget.AutofitTextView;

//TODO comments
public class MainActivity extends AppCompatActivity implements OnLoadingStateChangeListener {

    private double input = 0;
    public String currencyFrom;
    public String currencyTo;
    private AdView adView;
    private final RatesUpdater ratesUpdater = RatesUpdater.getInstance();
    private boolean isLoading = false;
    private ImageView refreshImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshImage = (ImageView) getLayoutInflater().inflate(R.layout.refresh_rotation, null);

        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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
                            char[] value = inputView.getText().toString().toCharArray();
                            boolean wrongCharFlag = false;
                            int dotsCount = 0;
                            for (char c : value) {
                                if (!Character.isDigit(c)) {
                                    if (c != '.') {
                                        wrongCharFlag = true;
                                        break;
                                    }
                                    else if (++dotsCount > 1) {
                                        wrongCharFlag = true;
                                        break;
                                    }
                                }
                            }
                            if (wrongCharFlag) {
                                Toast.makeText(MainActivity.this, getString(R.string.input_error), Toast.LENGTH_LONG).show();
                                dialogInterface.cancel();
                            } else {
                                input = Double.valueOf(inputView.getText().toString());
                                calculateOutcome(input);
                            }
                        }
                    }
                });
                AlertDialog dialog = alertDialog.create();
                Window window = dialog.getWindow();
                if (window != null)
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();

            }
        });

        ImageView swapView = (ImageView) findViewById(R.id.swap_currencies);
        swapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefsManager.setString(PrefsManager.PREFERENCE_FROM, currencyTo);
                PrefsManager.setString(PrefsManager.PREFERENCE_TO, currencyFrom);
                setViews();
                calculateOutcome(input);
            }
        });
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
    protected void onStart() {
        super.onStart();
        if (PrefsManager.getBoolean(PrefsManager.UPDATE_SCHEDULED, false)) {
            ratesUpdater.update(this);
            PrefsManager.setBoolean(PrefsManager.UPDATE_SCHEDULED, false);
        } else if (PrefsManager.getBoolean(PrefsManager.PARSE_SCHEDULED, false)) {
            ratesUpdater.parse(this);
            PrefsManager.setBoolean(PrefsManager.PARSE_SCHEDULED, false);
        }
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

        if (PrefsManager.getString(PrefsManager.DATA, "").isEmpty())
            Toast.makeText(this, R.string.error_try_update, Toast.LENGTH_SHORT).show();
    }

    private void setViews() {
        currencyFrom = PrefsManager.getString(PrefsManager.PREFERENCE_FROM, PrefsManager.DEFAULT_FROM);
        currencyTo = PrefsManager.getString(PrefsManager.PREFERENCE_TO, PrefsManager.DEFAULT_TO);
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

        TextView date = (TextView) findViewById(R.id.date);
        date.setText(PrefsManager.getString(PrefsManager.DATE, ""));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                setViews();
                calculateOutcome(input);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem refreshItem = menu.getItem(0);
        if (isLoading) {
            refreshImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
            refreshItem.setActionView(refreshImage);
        } else {
            if (refreshImage != null) {
                refreshImage.clearAnimation();
                refreshImage.setVisibility(View.GONE);
                refreshItem.setActionView(null);
            }
            refreshItem.collapseActionView();
            refreshItem.setActionView(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
            if (!isLoading)
                ratesUpdater.update(this);
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
            Log.e("error", e.getMessage());
        }
        return activity;
    }

    @Override
    public void onLoadingStarted() {
        isLoading = true;
        invalidateOptionsMenu();
    }

    @Override
    public void onLoadingFinished() {
        isLoading = false;
        invalidateOptionsMenu();
        calculateOutcome(input);
    }
}
