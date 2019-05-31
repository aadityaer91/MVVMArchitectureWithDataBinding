package com.unaprime.app.android.una.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.AppSession;
import com.unaprime.app.android.una.events.UISwitchEvent;
import com.unaprime.app.android.una.interfaces.RetrofitApiCallback;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.providers.AppContentProvider;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.services.responses.ConfigResponseData;
import com.unaprime.app.android.una.utils.AppConstants;
import com.unaprime.app.android.una.utils.AppUtils;
import com.unaprime.app.android.una.views.fragments.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static com.unaprime.app.android.una.utils.AppUtils.isValidString;

public class SplashViewModel implements RetrofitApiCallback {
    private final String TAG = SplashViewModel.class.getSimpleName();
    AppContentProvider appContentProvider;
    Bundle fragmentArguments;
    ConfigResponseData configResponseData;
    private PublishSubject<Bundle> forceUpdatePopupData = PublishSubject.create();
    private PublishSubject<String> configError = PublishSubject.create();
    AppSession appSession;

    public SplashViewModel(AppContentProvider appContentProvider, Bundle arguments, AppSession appSession) {
        this.appContentProvider = appContentProvider;
        fragmentArguments = arguments;
        this.appSession = appSession;
    }

    public Observable<Bundle> getForceUpdatePopupData() {
        return forceUpdatePopupData.hide();
    }

    public Observable<String> getConfigError() {
        return configError.hide();
    }

    @Nullable
    public void fragmentToHandleRequestedAction() {
        BaseFragment fragment = null;

        Bundle actionData = fragmentArguments;
        if (actionData != null) {
            fragment = fragmentToHandleDestination(actionData);
        }

        if (fragment == null) {
            saveFragmentInstanceInSession(fragment, null);
        }
    }

    public BaseFragment fragmentToHandleDestination(Bundle actionData) {
        BaseFragment fragment = null;
        String destinationType = actionData.getString("dt", null);

        if (!isValidString(destinationType)) {
            AppLogger.log(TAG, "A value MUST be supplied for dt parameter to determine app destination", AppConstants.LogLevel.ERROR);
            fragment = null;
        } else if (destinationtypeRequiresLoggedIn(destinationType)) {
            fragment = fragmentAgainstDestination(destinationType, actionData);
        } else {
            fragment = fragmentAgainstDestination(destinationType, actionData);
        }

        saveFragmentInstanceInSession(fragment, destinationType);

        return fragment;
    }

    /**
     * Deep Linking
     * 0 - Homepage/Menu page
     * 1 - Notification Screen
     * 2 - Transactions (customer)
     * 3 - Transaction History (Merchant)
     * 4 - Loan Application for Pending Document (Merchant)
     * 5 - Loan Consent Screen (Merchant)
     **/

    private BaseFragment fragmentAgainstDestination(String destinationType, Bundle actionData) {
        BaseFragment fragment = null;
        switch (destinationType.trim()) {
            case "0":
                //fragment = new MenuPayReceive();
                break;

            case "1":
                /*fragment = new NotificationScreenFragment();
                try {
                    Bundle l_bundle = new Bundle();
                    fragment.setArguments(l_bundle);
                } catch (Exception e) {
                    AppLogger.error(TAG, "Unable to prepare arguments for deep linked fragment", e);
                }*/
                break;

            case "2":

                break;

            case "3":

                break;

            case "4":

                break;

            case "5":

                break;
        }
        return fragment;
    }

    boolean destinationtypeRequiresLoggedIn(String destinationType) {
        boolean required = false;
        switch (destinationType.trim()) {
            case "1":
                required = true;
                break;
            case "2":
                required = true;
                break;
            case "3":
                required = true;
                break;
            case "4":
                required = true;
                break;
            case "5":
                required = true;
                break;
        }
        return required;
    }

    private void saveFragmentInstanceInSession(BaseFragment fragmentToLoad, String destinationType) {
        appSession.saveDeepLinkedFragments(fragmentToLoad, destinationType);
    }

    public void fetchConfigData() {
        try {
            appContentProvider.requiredDataForConfiguration(null, this);
        } catch (Exception e) {
            AppLogger.error(TAG, "Unable to create request object", e);
        }
    }

    private void handleConfigResponseData() {
        try {
            if (configResponseData == null) {
                return;
            }
            int appUpdateStatus = configResponseData.getAppUpdateStatus();

            if (appUpdateStatus == 0) {
                //doing nothing
                checkIsAdmin();
            } else if (appUpdateStatus == 1) {
                // show force update popup
                showForceUpdatePopup(configResponseData.getAppUpdateMessage(), 1, true);
            } else if (appUpdateStatus == 2) {
                // show recommended popup
                showForceUpdatePopup(configResponseData.getAppUpdateMessage(), 2, true);
            }

        } catch (Exception e) {
            AppLogger.error(TAG, "Unable to handle Force update Api response", e);
        }
    }

    public void checkIsAdmin() {
        if (configResponseData == null) {
            return;
        }

        if (configResponseData.getAppMaintenanceStatus() == 0) {
            // perform login
            fragmentToHandleRequestedAction();
            loadFirstFragment();
        } else if (configResponseData.getAppMaintenanceStatus() == 1) {
            showForceUpdatePopup(configResponseData.getAppMaintenanceMessage(), 1, false);
        } else if (configResponseData.getAppMaintenanceStatus() == 2) {
            showForceUpdatePopup(configResponseData.getAppMaintenanceMessage(), 2, false);
        }
    }

    public void redirectToPlaystore(Activity activity) {
        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void showForceUpdatePopup(String messageToShow, int forceFlag, boolean flag) {
        Bundle bundle = new Bundle();
        bundle.putString("messageToShow", messageToShow);
        bundle.putInt("forceFlag", forceFlag);
        bundle.putBoolean("flag", flag);
        forceUpdatePopupData.onNext(bundle);
    }

    public void loadFirstFragment() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppUtils.isValidString(App.mAppSession.getUserId())) {
                    EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.HomePageFragmentLoad));
                } else {
                    EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.LoginFragmentLoad));
                }
            }
        }, 2000);

    }

    @Override
    public void onDataFetched(CommonResponseData appResponseData, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (appResponseData instanceof ConfigResponseData) {
            AppLogger.log(TAG, "Data received successfully");
            configResponseData = (ConfigResponseData) appResponseData;
            handleConfigResponseData();
        }
    }

    @Override
    public void onError(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (syncAPI == WebserviceConstants.APICallbackIdentifiers.FetchConfig) {
            if (bundle.containsKey("message"))
                configError.onNext(bundle.getString("message"));
        }
    }

    @Override
    public void onSuccess(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {

    }
}
