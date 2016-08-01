package com.lukaspaczos.currencyconverter;

import java.util.List;

public class Currency {
    private String shortName;
    private String longName;
    private double rate;
    public static List<Currency> list;

    public Currency(String shortName, String longName, double rate) {
        this.shortName = shortName;
        this.longName = longName;
        this.rate = rate;
        list.add(this);
    }
}
