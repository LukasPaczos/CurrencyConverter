package com.lukaspaczos.currencyconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Context contextMain;
    private static final double GBPPLN_RATE = 5.17;
    private double input = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextMain = this;

        calculateOutcome(input);

        TextView startingAmount = (TextView) findViewById(R.id.currency_starting);
        startingAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(contextMain);
                alertDialog.setTitle(R.string.converter_choose_amount);
                final EditText inputView = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputView.setLayoutParams(lp);
                inputView.setSingleLine(true);
                inputView.setMaxLines(1);
                inputView.setLines(1);
                inputView.setRawInputType(Configuration.KEYBOARD_12KEY);
                alertDialog.setView(inputView);
                alertDialog.setCancelable(true);
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        input = Double.valueOf(inputView.getText().toString());
                        calculateOutcome(input);
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void calculateOutcome(double input) {
        TextView amountFrom = (TextView) findViewById(R.id.currency_starting);
        amountFrom.setText(String.format("%.2f", input));
        double outcome = input * GBPPLN_RATE;
        TextView amountOutcome = (TextView) findViewById(R.id.currency_outcome);
        amountOutcome.setText(String.format("%.2f", outcome));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
