package com.lukaspaczos.currencyconverter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.lukaspaczos.currencyconverter.currency.Currency;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

public class RatesUpdater {

    private static final String url = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private static RatesUpdater instance;
    private DownloadAsync downloadAsync;

    private OnLoadingStateChangeListener listener;

    public static RatesUpdater getInstance() {
        if (instance == null) {
            instance = new RatesUpdater();
        }
        return instance;
    }

    public void update() {
        update(null);
    }

    public void update(final OnLoadingStateChangeListener listener) {
        this.listener = listener;
        downloadAsync = new DownloadAsync(listener);
        downloadAsync.execute();
    }

    private class DownloadAsync extends AsyncTask<Void, Void, Boolean> {

        public DownloadAsync(OnLoadingStateChangeListener loadingStateChangeListener) {
            listener = loadingStateChangeListener;
        }

        @Override
        protected void onPreExecute() {
            if (listener != null)
                listener.onLoadingStarted();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (!isConnectedToInternet() || !isNetworkAvailable()) {
                    listener.onError(R.string.update_failed_no_internet);
                    return false;
                }

                /*
                 * just to make an impression that some IMPORTANT operations are being done,
                 * also animation looks cooler when it last for more than a couple of milliseconds
                 */
                Thread.sleep(1000);

                URL myUrl = new URL(url);
                URLConnection connection = myUrl.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                Log.d("CurrencyConverter", "Length of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(myUrl.openStream());
                java.util.Scanner s = new java.util.Scanner(input).useDelimiter("\\A");
                String xml = s.hasNext() ? s.next() : "";
                if (xml.isEmpty())
                    throw new Exception();

                PrefsManager.setString(PrefsManager.DATA, xml);

                input.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (listener != null)
                    listener.onError(R.string.update_error);

                return false;
            } catch (Exception e) {
                if (listener != null)
                    listener.onError(R.string.update_error_ioe);

                return false;
            }

            return parse();
        }

        @Override
        protected void onPostExecute(Boolean object) {
            if (listener != null && object != null && object)
                listener.onLoadingFinished();
        }
    }

    public boolean parse() {
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
            if (listener != null)
                listener.onError(R.string.update_error_xml);

            return false;
        } catch (IOException e) {
            if (listener != null)
                listener.onError(R.string.update_error_ioe);

            return false;
        } catch (Exception e) {
            if (listener != null)
                listener.onError(R.string.update_error_ioe);

            return false;
        }

        List<String> longNames = Arrays.asList(App.getContext().getResources().getStringArray(R.array.currencies_long));
        int i = 0;
        for (Currency c : Currency.list) {
            if (longNames.get(i) != null) {
                c.setLongName(longNames.get(i));
            }
            i++;
        }

        return true;
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isNetworkAvailable() {
        try {
            HttpURLConnection urlc = (HttpURLConnection)
                    (new URL("http://clients3.google.com/generate_204")
                            .openConnection());

            return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
