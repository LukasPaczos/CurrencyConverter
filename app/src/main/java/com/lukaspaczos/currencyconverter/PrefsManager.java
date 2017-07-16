package com.lukaspaczos.currencyconverter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREFS_NAME = "currency_converter";
    public static final String  DATA = "data";
    public static final String  PREFERENCE_FROM = "preference_from";
    public static final String  PREFERENCE_TO = "preference_to";
    public static final String  DATE = "date";
    public static final String  DEFAULT_FROM = "EUR";
    public static final String  DEFAULT_TO = "USD";

    private static SharedPreferences sharedPreferences;

    public static void initialize(Application app) {
        sharedPreferences = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(String pref, boolean defaultValue) {
        return sharedPreferences.getBoolean(pref, defaultValue);
    }

    public static void setBoolean(String pref, boolean value) {
        sharedPreferences.edit().putBoolean(pref, value).commit();
    }

    public static int getInt(String pref, int defaultValue) {
        return sharedPreferences.getInt(pref, defaultValue);
    }

    public static void setInt(String pref, int value) {
        sharedPreferences.edit().putInt(pref, value).commit();
    }

    public static String getString(String pref, String defaultValue) {
        return sharedPreferences.getString(pref, defaultValue);
    }

    public static void setString(String pref, String value) {
        sharedPreferences.edit().putString(pref, value).commit();
    }

    public static float getFloat(String pref, float defaultValue) {
        return sharedPreferences.getFloat(pref, defaultValue);
    }

    public static void setFloat(String pref, float value) {
        sharedPreferences.edit().putFloat(pref, value).commit();
    }
}
