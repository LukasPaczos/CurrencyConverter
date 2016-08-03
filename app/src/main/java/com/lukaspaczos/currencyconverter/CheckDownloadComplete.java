package com.lukaspaczos.currencyconverter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class CheckDownloadComplete extends BroadcastReceiver {

    public static boolean isDownloadComplete = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            isDownloadComplete = true;
            Log.i("Download completed?", String.valueOf(isDownloadComplete));
            RatesUpdate.parse(MainActivity.contextMain);
            Toast.makeText(MainActivity.contextMain, "Update finished.", Toast.LENGTH_SHORT).show();
        }
    }
}
