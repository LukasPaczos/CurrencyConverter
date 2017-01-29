package com.lukaspaczos.currencyconverter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lukaspaczos.currencyconverter.currency.Currency;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

public class RatesUpdater {
    private static RatesUpdater instance;

    public static RatesUpdater getInstance() {
        if (instance == null) {
            instance = new RatesUpdater();
        }
        return instance;
    }

    public void update(final Activity activity) {
        final String url = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

        if (isNetworkAvailable(activity.getApplicationContext())) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new DownloadAsync(activity).execute(url);
                }
            });
        } else
            Toast.makeText(activity.getApplicationContext(), R.string.update_failed_no_internet, Toast.LENGTH_SHORT).show();
    }

    private class DownloadAsync extends AsyncTask<String, String, String> {
        Activity activity;
        Context context;

        public DownloadAsync(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            ((OnLoadingStateChangeListener) activity).onLoadingStarted();
        }

        @Override
        protected String doInBackground(String[] objects) {
            try {
                /*
                 * just to make an impression that some IMPORTANT operations are being done,
                 * also animation looks cooler when it last for more than a couple of milliseconds
                 */
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.update_error, Toast.LENGTH_SHORT).show();
            }
            try {
                URL url = new URL(objects[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                Log.d("CurrencyConverter", "Length of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                java.util.Scanner s = new java.util.Scanner(input).useDelimiter("\\A");
                String xml =  s.hasNext() ? s.next() : "";
                if (xml.isEmpty())
                    throw new Exception();

                PrefsManager.setString(PrefsManager.DATA, xml);

                input.close();
            } catch (Exception e) {
                Toast.makeText(context, R.string.update_error_ioe, Toast.LENGTH_SHORT).show();
            }

            parse(activity);

            return null;

        }

        @Override
        protected void onPostExecute(String uri) {
            ((OnLoadingStateChangeListener) activity).onLoadingFinished();
            // TODO: 28-Jan-17 change all those calls to interface
            Toast.makeText(activity, R.string.update_finished, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    public void parse(final Activity activity) {

        try {
            String data = PrefsManager.getString(PrefsManager.DATA, "");
            if (data.isEmpty())
                throw new Exception();

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(new StringReader(data));
            int event = myParser.getEventType();
            //adding euro, which is not in charts
            new Currency("EUR", "EURO", 1);
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("Cube")) {
                            String date = myParser.getAttributeValue(null, "time");
                            if (date != null) {
                                PrefsManager.setString(PrefsManager.DATE, date);
                            } else {
                                String shortName = myParser.getAttributeValue(null, "currency");
                                String rate = myParser.getAttributeValue(null, "rate");
                                if (shortName != null && rate != null) {
                                    new Currency(shortName, "", Double.valueOf(rate));
                                }
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:

                        break;
                }
                event = myParser.next();
            }

        } catch (XmlPullParserException e) {
            Toast.makeText(activity, R.string.update_error_xml, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, R.string.update_error_ioe, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(activity, R.string.update_error_ioe, Toast.LENGTH_SHORT).show();
        }

        List<String> longNames = Arrays.asList(activity.getResources().getStringArray(R.array.currencies_long));
        int i = 0;
        for (Currency c : Currency.list) {
            if (longNames.get(i) != null) {
                c.setLongName(longNames.get(i));
            }
            i++;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView dateTv = (TextView) activity.findViewById(R.id.date);
                dateTv.setText(PrefsManager.getString(PrefsManager.DATE, ""));
            }
        });
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
