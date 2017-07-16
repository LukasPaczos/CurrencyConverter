package com.lukaspaczos.currencyconverter;

import android.app.Application;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        PrefsManager.initialize(this);
    }

    public static Context getContext() {
        return context;
    }
}