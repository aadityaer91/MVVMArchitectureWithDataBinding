package com.unaprime.app.android.una.providers;

import android.os.Build;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.AppSession;
import com.unaprime.app.android.una.BuildConfig;
import com.unaprime.app.android.una.interfaces.RetrofitApiCallback;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.ConfigResponseData;
import com.unaprime.app.android.una.services.responses.NoDataResponseData;
import com.unaprime.app.android.una.services.responses.HomepageResponseData;
import com.unaprime.app.android.una.services.responses.LoginResponseData;
import com.unaprime.app.android.una.services.responses.RegisterOtpResponseData;
import com.unaprime.app.android.una.services.retro.API;
import com.unaprime.app.android.una.services.retro.AppRetrofitCallback;
import com.unaprime.app.android.una.services.retro.RestClient;
import com.unaprime.app.android.una.utils.AppConstants;
import com.unaprime.app.android.una.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import okhttp3.ResponseBody;

/**
 * Created by aaditya on 14/11/17.
 */

public class AppContentProvider {

    private final String TAG = AppContentProvider.class.getSimpleName();
    WebDataProvider webDataProvider;
    OfflineDataProvider offlineDataProvider;

    public AppContentProvider() {
        this.webDataProvider = new WebDataProvider();
        this.offlineDataProvider = new OfflineDataProvider();
    }

    public boolean shouldFetchFromOnline(WebserviceConstants.APICallbackIdentifiers serviceName) {
        boolean fetchOnline = true;

        switch (serviceName) {
            case FetchConfig:
                //handle if we want to fetch data from offline storage
                fetchOnline = false;
                break;

            case FetchHomepageData:

                break;

            case VerifyLoginData:
                fetchOnline = false;
                break;

            case GenerateLoginOtp:
                fetchOnline = false;
                break;
            case GenerateRegisterOtp:
                fetchOnline = false;
                break;
            case VerifyRegisterOtp:
                fetchOnline = false;
                break;

            default:
                fetchOnline = true;
        }
        return fetchOnline;
    }

    public JSONObject addCommonRequestData(JSONObject requestJSONObject) {
        AppSession appSession = App.getAppSession();
        if (appSession != null) {
            try {
                requestJSONObject.put("appVersion", BuildConfig.VERSION_CODE);
                requestJSONObject.put("userId", appSession.getUserId());
                requestJSONObject.put("deviceToken", appSession.getFCMToken());
                requestJSONObject.put("uniqueId", appSession.getOSId());
                requestJSONObject.put("deviceType", AppConstants.DEVICE_TYPE);
                requestJSONObject.put("imei", appSession.getDeviceImei());
                requestJSONObject.put("timestamp", new Date().getTime());
                requestJSONObject.put("latitude", appSession.getLatitude());
                requestJSONObject.put("longitude", appSession.getLongitude());

                requestJSONObject.put("model", AppUtils.getModelName());
                requestJSONObject.put("sdk", AppUtils.getSdk());
                requestJSONObject.put("product", AppUtils.getProduct());
                requestJSONObject.put("ipaddress", AppUtils.getIpAddress(App.getAppContext()));
                requestJSONObject.put("macaddress", AppUtils.getMacAddress(App.getAppContext()));
                requestJSONObject.put("manufacturer", AppUtils.getManufacturer());
                requestJSONObject.put("apilevel", AppUtils.getAndroidVersion(Build.VERSION.SDK_INT));
                requestJSONObject.put("device", AppUtils.getDevice());

            } catch (JSONException e) {
                AppLogger.error(TAG, "Unable to create common paramaters", e);
            }

        }
        return requestJSONObject;
    }

    /*
    If you want to send data in secured way(encrypted), use WebserviceConstants.APIBaseIdentifiers.Secured & apply RestClient.secureAPIRequest() on your original json data
    or WebserviceConstants.APIBaseIdentifiers.Others for normal
     */
    public void requiredDataForConfiguration(JSONObject requestArgs, RetrofitApiCallback callback) {
        if (requestArgs == null) {
            requestArgs = new JSONObject();
        }
        if (shouldFetchFromOnline(WebserviceConstants.APICallbackIdentifiers.FetchConfig)) {
            API restClientAPIObject = RestClient.getRetrofitBuilder("", WebserviceConstants.APIBaseIdentifiers.Secured);
            AppRetrofitCallback<ResponseBody> appRetrofitCallback = webDataProvider.provideRetrofitCallbackObject(callback, WebserviceConstants.APICallbackIdentifiers.FetchConfig, ConfigResponseData.class);
            restClientAPIObject.fetchConfigData(RestClient.secureAPIRequest(addCommonRequestData(requestArgs))).enqueue(appRetrofitCallback);
        } else {
            webDataProvider.loadDataFromMock("config_response.json", callback, ConfigResponseData.class, WebserviceConstants.APICallbackIdentifiers.FetchConfig);
        }
    }

    public void fetchHomepageData(JSONObject requestArgs, RetrofitApiCallback callback) {
        if (shouldFetchFromOnline(WebserviceConstants.APICallbackIdentifiers.FetchHomepageData)) {
            API restClientAPIObject = RestClient.getRetrofitBuilder("", WebserviceConstants.APIBaseIdentifiers.Secured);
            AppRetrofitCallback<ResponseBody> appRetrofitCallback = webDataProvider.provideRetrofitCallbackObject(callback, WebserviceConstants.APICallbackIdentifiers.FetchHomepageData, HomepageResponseData.class);
            restClientAPIObject.fetchHomepageData(RestClient.secureAPIRequest(addCommonRequestData(requestArgs))).enqueue(appRetrofitCallback);
        } else {
            webDataProvider.loadDataFromMock("homepage_response.json", callback, HomepageResponseData.class, WebserviceConstants.APICallbackIdentifiers.FetchConfig);
        }
    }

    public void verifyLoginCreds(JSONObject requestArgs, RetrofitApiCallback callback) {
        if (shouldFetchFromOnline(WebserviceConstants.APICallbackIdentifiers.VerifyLoginData)) {
            API restClientAPIObject = RestClient.getRetrofitBuilder("", WebserviceConstants.APIBaseIdentifiers.Secured);
            AppRetrofitCallback<ResponseBody> appRetrofitCallback = webDataProvider.provideRetrofitCallbackObject(callback, WebserviceConstants.APICallbackIdentifiers.VerifyLoginData, LoginResponseData.class);
            restClientAPIObject.doLogin(RestClient.secureAPIRequest(addCommonRequestData(requestArgs))).enqueue(appRetrofitCallback);
        } else {
            webDataProvider.loadDataFromMock("login_response.json", callback, LoginResponseData.class, WebserviceConstants.APICallbackIdentifiers.VerifyLoginData);
        }
    }

    public void generateOtpForLogin(JSONObject requestArgs, RetrofitApiCallback callback) {
        if (shouldFetchFromOnline(WebserviceConstants.APICallbackIdentifiers.GenerateLoginOtp)) {
            API restClientAPIObject = RestClient.getRetrofitBuilder("", WebserviceConstants.APIBaseIdentifiers.Secured);
            AppRetrofitCallback<ResponseBody> appRetrofitCallback = webDataProvider.provideRetrofitCallbackObject(callback, WebserviceConstants.APICallbackIdentifiers.GenerateLoginOtp, NoDataResponseData.class);
            restClientAPIObject.generateLoginOtp(RestClient.secureAPIRequest(addCommonRequestData(requestArgs))).enqueue(appRetrofitCallback);
        } else {
            webDataProvider.loadDataFromMock("base_response.json", callback, NoDataResponseData.class, WebserviceConstants.APICallbackIdentifiers.GenerateLoginOtp);
        }
    }

    public void generateOtpForRegister(JSONObject requestArgs, RetrofitApiCallback callback) {
        if (shouldFetchFromOnline(WebserviceConstants.APICallbackIdentifiers.GenerateRegisterOtp)) {
            API restClientAPIObject = RestClient.getRetrofitBuilder("", WebserviceConstants.APIBaseIdentifiers.Secured);
            AppRetrofitCallback<ResponseBody> appRetrofitCallback = webDataProvider.provideRetrofitCallbackObject(callback, WebserviceConstants.APICallbackIdentifiers.GenerateRegisterOtp, NoDataResponseData.class);
            restClientAPIObject.generateRegisterOtp(RestClient.secureAPIRequest(addCommonRequestData(requestArgs))).enqueue(appRetrofitCallback);
        } else {
            webDataProvider.loadDataFromMock("base_response.json", callback, NoDataResponseData.class, WebserviceConstants.APICallbackIdentifiers.GenerateRegisterOtp);
        }
    }

    public void verifyRegisterOtp(JSONObject requestArgs, RetrofitApiCallback callback) {
        if (shouldFetchFromOnline(WebserviceConstants.APICallbackIdentifiers.VerifyRegisterOtp)) {
            API restClientAPIObject = RestClient.getRetrofitBuilder("", WebserviceConstants.APIBaseIdentifiers.Secured);
            AppRetrofitCallback<ResponseBody> appRetrofitCallback = webDataProvider.provideRetrofitCallbackObject(callback, WebserviceConstants.APICallbackIdentifiers.VerifyRegisterOtp, RegisterOtpResponseData.class);
            restClientAPIObject.verifyOtpForRegistration(RestClient.secureAPIRequest(addCommonRequestData(requestArgs))).enqueue(appRetrofitCallback);
        } else {
            webDataProvider.loadDataFromMock("register_otp_verify_response.json", callback, RegisterOtpResponseData.class, WebserviceConstants.APICallbackIdentifiers.VerifyRegisterOtp);
        }
    }
}
