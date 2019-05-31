package com.unaprime.app.android.una.services;

import com.unaprime.app.android.una.BuildConfig;

public class WebserviceConstants {
    public enum APIBaseIdentifiers {
        Secured,
        Others
    }

    public enum APICallbackIdentifiers {
        NoSync,
        FetchConfig,
        VerifyLoginData,
        FetchHomepageData
    }

    public static String getBaseUrlApp() {
        return BuildConfig.APIScheme + "://" + BuildConfig.APIHostName + "/" + BuildConfig.APIURLPath1Prefix;
    }
}
