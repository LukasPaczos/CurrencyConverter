package com.lukaspaczos.currencyconverter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RatesUpdate {
    private static DownloadManager downloadManager;
    public static final String FILE_NAME = "eurofxref-daily.xml";
    private static Context context;

    public static long update(Context c) {
        Log.i("RatesUpdate", "started");
        context = c;
        Uri dataUri = Uri.parse("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        return downloadData(dataUri, context);
    }

    private static long downloadData(Uri uri, Context context) {
        long downloadReference;

        Log.i("File exists", Boolean.toString(fileExists(FILE_NAME)));
        if (fileExists(FILE_NAME)) {
            if (deleteFile(FILE_NAME)) {
                Log.i("File deleted", "true");
            } else {
                Log.i("File deleted", "false");
            }
        }
        // Create request for android download manager
        downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        Toast.makeText(context, "Downloading exchange rates.", Toast.LENGTH_SHORT).show();

        //Setting title of request
        request.setTitle("Current exchange rates");

        //Setting description of request
        request.setDescription("Current exchange rates.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, FILE_NAME);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    private static boolean fileExists(String fileName) {
        File file = new File(context.getExternalFilesDir(null).toString() + "/Download/" + fileName);
        Log.i("File path", context.getExternalFilesDir(null).toString() + "/Download/" + fileName);
        return file.exists();
    }

    private static boolean deleteFile(String fileName) {
        File file = new File(context.getExternalFilesDir(null).toString() + "/Download/" + fileName);
        return file.delete();
    }

    public static void parse(Context context) {
        /*int i = 0;
        CheckDownloadComplete.isDownloadComplete = false;
        while (!CheckDownloadComplete.isDownloadComplete) {

        }
        if (i >= 100000) {
            Log.i("Downloading", "error");
            Toast.makeText(context, "Couldn't download new exchange rates.", Toast.LENGTH_SHORT).show();
        }*/
        Log.i("XMLParsing", "started");
        try {
            File xmlFile = new File(context.getExternalFilesDir(null).toString() + "/Download/" + FILE_NAME);
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
                            String date = "";
                            date = myParser.getAttributeValue(null, "time");
                            if (date != null) {
                                Log.i("Date from file", date);
                                SharedPreferences sharedPref = context.getSharedPreferences("default_currencies", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(context.getString(R.string.date), date);
                                editor.apply();
                                /*View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
                                TextView dateTv = (TextView) rootView.findViewById(R.id.date);
                                dateTv.setText(date);*/
                            }
                            else {
                                String shortName = "";
                                String rate = "";
                                shortName = myParser.getAttributeValue(null, "currency");
                                rate = myParser.getAttributeValue(null, "rate");
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
            Toast.makeText(context, "Error updating exchange rates.(XMLparse)", Toast.LENGTH_SHORT).show();
            Log.i("XMLParseException", e.getMessage());
        } catch (IOException e) {
            Toast.makeText(context, "Error updating exchange rates.(IOE)", Toast.LENGTH_SHORT).show();
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

        Log.i("XMLParsing", "finished");
        Log.i("RatesUpdate", "finished");
    }
}
