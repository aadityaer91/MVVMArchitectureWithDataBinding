package com.unaprime.app.android.una;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.unaprime.app.android.una.events.UISwitchEvent;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.providers.AppContentProvider;
import com.unaprime.app.android.una.services.responses.LoginResponseData;
import com.unaprime.app.android.una.utils.AppConstants;
import com.unaprime.app.android.una.utils.AppUtils;
import com.unaprime.app.android.una.views.fragments.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import static android.Manifest.permission.READ_PHONE_STATE;
import static com.unaprime.app.android.una.utils.AppConstants.IMEI;
import static com.unaprime.app.android.una.utils.AppConstants.PREFERENCE;

public class AppSession {
    private boolean setupHasCompleted;
    private String fcmToken = "";

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;
    AppContentProvider appContentProvider;
    private BaseFragment deepLinkedFragmentInstance;
    private String destinationType;


    public AppSession() {
        //default constructor
        setupHasCompleted = false;
    }

    public void appLaunchSetup() {
        AppLogger.log("Starting app data setup ===================================>>>>>>");
        Context appContext = App.getAppContext();

        preferences = appContext.getSharedPreferences(PREFERENCE, 0);
        preferenceEditor = preferences.edit();

        appContentProvider = new AppContentProvider();

        setupHasCompleted = true;
    }

    public boolean isSetupHasCompleted() {
        return setupHasCompleted;
    }

    public AppContentProvider getAppContentProvider() {
        return appContentProvider;
    }

    public String getOSId() {
        try {
            return Settings.Secure.getString(App.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "";
        }
    }

    public void initializeIMEI() {
        Context appContext = App.getAppContext();
        Object object = appContext.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager mngr = null;
        String imei = "";
        if (object != null) {
            mngr = (TelephonyManager) object;
        } else {
            AppLogger.log("InitializeIMEI", "Could not get telephony manager!", AppConstants.LogLevel.WARNING);
        }
        if (mngr != null) {
            if (ActivityCompat.checkSelfPermission(appContext, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = mngr.getImei();
            } else {
                imei = mngr.getDeviceId();
            }
        }
        if (!AppUtils.isValidString(imei)) {
            imei = preferences.getString(IMEI, null);
            if (!AppUtils.isValidString(imei)) {
                imei = UUID.randomUUID().toString();
                preferenceEditor.putString(IMEI, imei);
                preferenceEditor.apply();
            }
        } else {
            preferenceEditor.putString(IMEI, imei);
            preferenceEditor.apply();
        }

    }

    public String getDeviceImei() {
        return preferences.getString(IMEI, "");
    }

    public String getUserId() {
        return preferences.getString(AppConstants.USER_ID, "");
    }

    public String getFCMToken() {
        return fcmToken;
    }

    public void updateFCMToken(String token) {
        fcmToken = token;
    }

    public String getLatitude() {
        return preferences.getString(AppConstants.CapturedLatitude, "0");
    }

    public String getLongitude() {
        return preferences.getString(AppConstants.CapturedLongitude, "0");
    }

    public String getUserPublicKey() {
        return preferences.getString(AppConstants.USER_PUBLIC_KEY, "");
    }

    public void performSignOut() {
        preferenceEditor.clear();
        preferenceEditor.apply();
        EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.LoginFragmentLoad));
    }

    public void saveDeepLinkedFragments(BaseFragment fragmentToLoad, String destinationType) {
        deepLinkedFragmentInstance = fragmentToLoad;
        this.destinationType = destinationType;
    }

    @Nullable
    public BaseFragment getDeepLinkedFragmentInstance() {
        return deepLinkedFragmentInstance;
    }

    @Nullable
    public String getDestinationType() {
        return destinationType;
    }

    public void saveUserDataOnLogin(LoginResponseData loginResponseData) {
        preferenceEditor.putString(AppConstants.USER_ID, loginResponseData.getUserId());
        preferenceEditor.putString(AppConstants.USER_PUBLIC_KEY, loginResponseData.getApiSecurityKey());
        preferenceEditor.commit();
    }


}
