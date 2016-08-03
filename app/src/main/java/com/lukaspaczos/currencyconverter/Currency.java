package com.lukaspaczos.currencyconverter;

import java.util.ArrayList;

public class Currency {
    private String shortName;
    private String longName;
    private double rate;
    public static ArrayList<Currency> list = new ArrayList<>();

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

    @Override
    public String toString() {
        return this.getShortName() + " - " + this.getLongName();
    }
}
