package com.lukaspaczos.currencyconverter.currency;

import com.lukaspaczos.currencyconverter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Currency {
    private String shortName;
    private String longName;
    private double rate;
    public static ArrayList<Currency> list = new ArrayList<>();
    public static Map<String, Integer> flagsMap = new HashMap<>();

    public Currency(String shortName, String longName, double rate) {
        this.shortName = shortName;
        this.longName = longName;
        this.rate = rate;
        addToList(this);
    }

    private void addToList(Currency currency) {
        boolean exists = false;
        Currency existing = null;
        for (Currency c : list) {
            if (c.getShortName().equals(currency.getShortName())) {
                exists = true;
                existing = c;
            }
        }
        if (!exists) {
            list.add(currency);
        } else {
            if (existing.rate != currency.rate) {
                existing.rate = currency.rate;
            }
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public double getRate() {
        return rate;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public static void loadFlags() {
        flagsMap.put("AUD", R.drawable.aud);
        flagsMap.put("BGN", R.drawable.bgn);
        flagsMap.put("BRL", R.drawable.brl);
        flagsMap.put("CAD", R.drawable.cad);
        flagsMap.put("CHF", R.drawable.chf);
        flagsMap.put("CNY", R.drawable.cny);
        flagsMap.put("CZK", R.drawable.czk);
        flagsMap.put("DKK", R.drawable.dkk);
        flagsMap.put("EUR", R.drawable.eur);
        flagsMap.put("GBP", R.drawable.gbp);
        flagsMap.put("HKD", R.drawable.hkd);
        flagsMap.put("HRK", R.drawable.hrk);
        flagsMap.put("HUF", R.drawable.huf);
        flagsMap.put("IDR", R.drawable.idr);
        flagsMap.put("ILS", R.drawable.ils);
        flagsMap.put("INR", R.drawable.inr);
        flagsMap.put("JPY", R.drawable.jpy);
        flagsMap.put("KRW", R.drawable.krw);
        flagsMap.put("MXN", R.drawable.mxn);
        flagsMap.put("MYR", R.drawable.myr);
        flagsMap.put("NOK", R.drawable.nok);
        flagsMap.put("NZD", R.drawable.nzd);
        flagsMap.put("PHP", R.drawable.php);
        flagsMap.put("PLN", R.drawable.pln);
        flagsMap.put("RON", R.drawable.ron);
        flagsMap.put("RUB", R.drawable.rub);
        flagsMap.put("SEK", R.drawable.sek);
        flagsMap.put("SGD", R.drawable.sgd);
        flagsMap.put("THB", R.drawable.thb);
        flagsMap.put("TRY", R.drawable.tur);
        flagsMap.put("USD", R.drawable.usd);
        flagsMap.put("ZAR", R.drawable.zar);
    }

    @Override
    public String toString() {
        return this.getShortName() + " - " + this.getLongName();
    }
}
