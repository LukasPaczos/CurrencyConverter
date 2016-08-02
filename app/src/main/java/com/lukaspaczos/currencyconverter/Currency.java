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
        list.add(this);
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
}
