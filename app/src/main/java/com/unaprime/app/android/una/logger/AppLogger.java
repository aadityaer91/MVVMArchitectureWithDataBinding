package com.unaprime.app.android.una.logger;

import android.util.Log;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.BuildConfig;
import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.utils.AppConstants;

public class AppLogger {
    public static void log(String message) {
        log(App.getAppContext().getString(R.string.app_name), message, AppConstants.LogLevel.DEBUG);
    }

    public static void log(String tag, String message) {
        log(tag, message, AppConstants.LogLevel.DEBUG);
    }

    public static void log(String tag, String message, AppConstants.LogLevel logLevel) {
        if (BuildConfig.DEBUG) {
            switch (logLevel) {

                case DEBUG:
                    Log.d(tag, message);
                    break;

                case INFO:
                    Log.i(tag, message);
                    break;

                case WARNING:
                    Log.w(tag, message);
                    break;

                case ERROR:
                    Log.e(tag, message);
                    break;

                default:
                    Log.v(tag, message);

            }
        }

    }

    public static void error(String message, Exception e) {
        if (BuildConfig.DEBUG)
            Log.e(App.getAppContext().getString(R.string.app_name), message, e);
    }

    /*public static void error(String tag, String message, Exception e) {
        if (BuildConfig.DEBUG)
            Log.e(tag, message, e);
    }*/

    public static void error(String tag, String message, Throwable e) {
        if (BuildConfig.DEBUG)
            Log.e(tag, message, e);
    }
}
