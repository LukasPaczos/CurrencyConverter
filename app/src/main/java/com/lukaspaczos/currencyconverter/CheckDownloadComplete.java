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
            Log.i("Download completed?", String.valueOf(true));
            RatesUpdate.parse(MainActivity.getActivity());
            Toast.makeText(MainActivity.getActivity(), R.string.update_finished, Toast.LENGTH_SHORT).show();
        }
    }
}
