package com.lukaspaczos.currencyconverter;

import android.app.Application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppLaunch extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrefsManager.initialize(this);
        String data = PrefsManager.getString(PrefsManager.DATA, "");
        String dateString = PrefsManager.getString(PrefsManager.DATE, "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date todayDate = null;
        try {
            date = sdf.parse(dateString);
            todayDate = sdf.parse(new Date().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (data.isEmpty() || dateString.isEmpty()) {
            PrefsManager.setBoolean(PrefsManager.UPDATE_SCHEDULED, true);
        } else if (date != null && todayDate != null && todayDate.after(date)) {
            PrefsManager.setBoolean(PrefsManager.UPDATE_SCHEDULED, true);
        } else {
            PrefsManager.setBoolean(PrefsManager.PARSE_SCHEDULED, true);
        }
    }
}