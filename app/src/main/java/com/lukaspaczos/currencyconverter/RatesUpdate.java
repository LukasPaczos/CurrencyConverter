package com.lukaspaczos.currencyconverter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Lukas Paczos on 01-Aug-16.
 */
public class RatesUpdate {
    private static DownloadManager downloadManager;
    private static final String FILE_NAME = "ExchangeRates.xml";
    private static Context context;

    public static void update(Context c) {
        context = c;
        Uri dataUri = Uri.parse("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        downloadData(dataUri, context);
    }

    private static long downloadData(Uri uri, Context context) {
        long downloadReference;

        Log.i("File exists", Boolean.toString(fileExists(FILE_NAME)));
        if(fileExists(FILE_NAME)) {
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
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE
                | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Setting title of request
        request.setTitle("Current exchange rates");

        //Setting description of request
        request.setDescription("Current exchange rates.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, FILE_NAME);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        Log.i("RatesUpdate", "started");
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
}
