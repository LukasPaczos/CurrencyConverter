package com.lukaspaczos.currencyconverter.currency;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<Currency> {

    public CurrencyAdapter(Context c, List<Currency> items) {
        super(c, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CurrencyView eventView = (CurrencyView)convertView;
        if (null == eventView)
            eventView = CurrencyView.inflate(parent);
        eventView.setItem(getItem(position));
        return eventView;
    }
}

