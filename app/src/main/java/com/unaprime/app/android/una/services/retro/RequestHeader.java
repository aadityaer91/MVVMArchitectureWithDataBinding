package com.unaprime.app.android.una.services.retro;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.BuildConfig;
import com.unaprime.app.android.una.logger.AppLogger;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by aaditya on 17/01/2018.
 */

public class RequestHeader {

    private final String KEY_APP_VER_NAME = "X-Ftcash-App-Version";
    private final String KEY_APP_VER_CODE = "X-Ftcash-App-Version-Code";
    private final String KEY_ACCEPT_HEADER = "Accept";
    private final String VALUE_ACCEPT_HEADER = "application/x-www-form-urlencoded";
    private final String KEY_REQUEST_HASH = "Secret-H";
    private final String KEY_APICONSTANT_HASH = "Public-H";
    private final String encryptedSHAKey;

    public RequestHeader(String encryptedSHAKey) {
        this.encryptedSHAKey = encryptedSHAKey;
    }

    public Request.Builder buildRequestHeaders(Request.Builder builder) {
        for (Map.Entry<String, String> entry : getApplicationHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
            AppLogger.log("Retrofit", "RequestHeader-" + "Key:" + entry.getKey() + ", Value: " + entry.getValue());
        }
        return builder;
    }

    private Map<String, String> getApplicationHeaders() {
        Map<String, String> appHeaders = new HashMap<>();
        appHeaders.put(KEY_APP_VER_NAME, BuildConfig.VERSION_NAME);
        appHeaders.put(KEY_APP_VER_CODE, String.valueOf(BuildConfig.VERSION_CODE));
        appHeaders.put(KEY_ACCEPT_HEADER, VALUE_ACCEPT_HEADER);
        appHeaders.put(KEY_REQUEST_HASH, encryptedSHAKey);
        //appHeaders.put(KEY_APICONSTANT_HASH, BaseApplication.getApplication().getResources().getString(R.string.app_public_id));

        return appHeaders;
    }

    private String readAppVersion() {
        try {
            Context appContext = App.getAppContext();
            PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new String();
        }
    }
}
