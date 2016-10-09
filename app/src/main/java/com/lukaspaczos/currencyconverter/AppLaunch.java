package com.lukaspaczos.currencyconverter;

import android.app.Application;
import android.os.Environment;

import com.lukaspaczos.currencyconverter.currency.Currency;

import java.io.File;

public class AppLaunch extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Currency.loadFlags();
        File xmlFile = new File(Environment.getExternalStorageDirectory().toString() + "/Download/" + RatesUpdate.FILE_NAME);
        if (xmlFile.exists()) {
            RatesUpdate.parse(this);
        } else {
            RatesUpdate.update(this);
        }
    }
}
