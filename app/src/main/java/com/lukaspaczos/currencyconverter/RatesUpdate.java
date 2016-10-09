package com.lukaspaczos.currencyconverter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lukaspaczos.currencyconverter.currency.Currency;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RatesUpdate {
    public static final String FILE_NAME = "eurofxref-daily.xml";

    public static long update(Context c) {
        Log.i("RatesUpdate", "started");
        Uri dataUri = Uri.parse("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        return downloadData(dataUri, c);
    }

    private static long downloadData(Uri uri, Context context) {
        long downloadReference;

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(MainActivity.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        //Setting title of request
        request.setTitle("Current exchange rates");

        //Setting description of request
        request.setDescription("Current exchange rates.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, FILE_NAME);

        if (isNetworkAvailable(context)) {
            Log.i("File exists", Boolean.toString(fileExists(context, FILE_NAME)));
            if (fileExists(context, FILE_NAME)) {
                if (deleteFile(context, FILE_NAME)) {
                    Log.i("File deleted", "true");
                } else {
                    Log.i("File deleted", "false");
                }
            }
            //Enqueue download and save into referenceId
            downloadReference = downloadManager.enqueue(request);
            Log.i("Download", "started");
            Toast.makeText(context, R.string.update_downloading, Toast.LENGTH_SHORT).show();
        } else {
            Log.i("Download", "no internet connection");
            Log.i("Update", "failed");
            downloadReference = -1;
            Toast.makeText(context, R.string.update_failed_no_internet, Toast.LENGTH_SHORT).show();
        }

        return downloadReference;
    }

    private static boolean fileExists(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(null) + "/Download/" + fileName);
        Log.i("File path", context.getExternalFilesDir(null) + "/Download/" + fileName);
        return file.exists();
    }

    private static boolean deleteFile(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(null) + "/Download/" + fileName);
        return file.delete();
    }

    public static void parse(Context context) {
        Log.i("XMLParsing", "started");

        SharedPreferences sharedPref = context.getSharedPreferences("default_currencies", Context.MODE_PRIVATE);

        try {
            File xmlFile = new File(context.getExternalFilesDir(null) + "/Download/" + FILE_NAME);
            FileReader fileReader = new FileReader(xmlFile);
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(fileReader);
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
                                Log.i("Date from file", date);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(context.getString(R.string.date), date);
                                editor.apply();
                            }
                            else {
                                String shortName = myParser.getAttributeValue(null, "currency");
                                String rate = myParser.getAttributeValue(null, "rate");
                                if (shortName != null && rate != null) {
                                    Log.i("shortName", shortName);
                                    Log.i("rate", rate);
                                    new Currency(shortName, "", Double.valueOf(rate));
                                } else {
                                    Log.i("shortName", "null");
                                    Log.i("rate", "null");
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
            Toast.makeText(context, R.string.update_error_xml, Toast.LENGTH_SHORT).show();
            Log.i("XMLParseException", e.getMessage());
        } catch (IOException e) {
            Toast.makeText(context, R.string.update_error_ioe, Toast.LENGTH_SHORT).show();
            Log.i("IOException", e.getMessage());
        }

        List<String> longNames = Arrays.asList(context.getResources().getStringArray(R.array.currencies_long));
        int i = 0;
        for (Currency c: Currency.list) {
            if (longNames.get(i) != null) {
                c.setLongName(longNames.get(i));
            }
            i++;
        }

        TextView date = (TextView) ((Activity)context).getWindow()
        .getDecorView().findViewById(android.R.id.content).findViewById(R.id.date);
        date.setText(sharedPref.getString(context.getString(R.string.date),
                context.getString(R.string.default_date)));
        Log.i("XMLParsing", "finished");
        Log.i("RatesUpdate", "finished");
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
