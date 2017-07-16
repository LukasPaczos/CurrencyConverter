package com.lukaspaczos.currencyconverter;

import android.support.annotation.UiThread;

public interface OnLoadingStateChangeListener {

    @UiThread
    void onLoadingStarted();

    @UiThread
    void onLoadingFinished();

    void onError(int stringRes);
}
