package com.lukaspaczos.currencyconverter.currency;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lukaspaczos.currencyconverter.R;

public class CurrencyView extends LinearLayout{
    private TextView nameView;
    private ImageView flagView;

    public CurrencyView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.currency_view_children, this, true);
        setupChildren();
    }

    public CurrencyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.currency_view_children, this, true);
        setupChildren();
    }

    public CurrencyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.currency_view_children, this, true);
        setupChildren();
    }

    public static CurrencyView inflate(ViewGroup parent) {
        CurrencyView eventView = (CurrencyView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_view, parent, false);
        return eventView;
    }

    private void setupChildren() {
        nameView = (TextView) findViewById(R.id.currency_name);
        flagView = (ImageView) findViewById(R.id.currency_flag);
    }

    public void setItem(Currency item) {
        nameView.setText(item.getShortName() + " - " + item.getLongName());
        flagView.setBackgroundResource(Currency.flagsMap.get(item.getShortName()));
    }
}

