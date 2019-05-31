package com.unaprime.app.android.una.providers;

import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.interfaces.RetrofitApiCallback;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.APIResponseData;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.services.retro.AppRetrofitCallback;
import com.unaprime.app.android.una.utils.AppConstants;
import com.unaprime.app.android.una.utils.AppUtils;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.unaprime.app.android.una.utils.AppConstants.SERVICE_NAME;

/**
 * Created by aaditya on 15/11/2017.
 */
public class WebDataProvider {

    private String TAG = WebDataProvider.class.getSimpleName();


    public void requestForData(JSONObject jsonRequestData, RetrofitApiCallback retrofitApiCallback, WebserviceConstants.APICallbackIdentifiers serviceName, final Class clz) {

        //callWebService(jsonRequestData, retrofitApiCallback, serviceName, clz);
    }

    public AppRetrofitCallback<ResponseBody> provideRetrofitCallbackObject(final RetrofitApiCallback retrofitApiCallback, final WebserviceConstants.APICallbackIdentifiers serviceName, final Class clz) {

        return new AppRetrofitCallback<ResponseBody>() {

            WebserviceConstants.APICallbackIdentifiers currentServiceName = serviceName;
            RetrofitApiCallback apiCallback = retrofitApiCallback;
            Class dataConverterClass = clz;

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //validateAPIResponse(response.body(), apiCallback, currentServiceName, dataConverterClass);

                if (response == null) {
                    return;
                }
                if (response.body() == null) {
                    AppLogger.log(TAG, "API response is null, returning", AppConstants.LogLevel.ERROR);
                    fireAPIResponseError(App.getAppContext().getResources().getString(R.string.internal_server_error), apiCallback, currentServiceName);
                    return;
                }
                try {
                    JSONObject tempObject = new JSONObject(response.body().string());
                    //System.out.println("JSOn object:" + tempObject);
                    String actualData = tempObject.toString();
                    boolean validateBody = performBodyDataValidation(actualData, response.headers(), currentServiceName);
                    if (validateBody) {
                        /*APIResponseData apiResponseData = (APIResponseData) CommonUtils.convertJsonToPojo(actualData, APIResponseData.class);
                        validateAPIResponse(apiResponseData, apiCallback, dataConverterClass, syncAPI);*/

                        performJsonToRootPojoConversion(actualData, dataConverterClass, apiCallback, currentServiceName);
                    } else {
                        AppLogger.log(TAG, "####### Checksum matched failed ######", AppConstants.LogLevel.ERROR);
                        fireAPIResponseError("Something went wrong", apiCallback, currentServiceName);
                    }
                } catch (Exception e) {
                    AppLogger.error(TAG, "Error while handling response body", e);
                    fireAPIResponseError("Something went wrong", apiCallback, currentServiceName);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                super.onFailure(call, t);
                fireAPIResponseError("Something went wrong", apiCallback, currentServiceName);
            }
        };
    }

   /* public void callWebService(JSONObject jsonRequestData, RetrofitApiCallback retrofitApiCallback, WebserviceConstants.APICallbackIdentifiers serviceName, final Class clz) {

        API restClientAPIObject = RestClient.getRetrofitBuilder("", WebserviceConstants.APIBaseIdentifiers.Secured);
        AppRetrofitCallback<APIResponseData> appRetrofitCallback = provideRetrofitCallbackObject(retrofitApiCallback, serviceName, clz);

        APIRequestData apiRequestData;

        switch (serviceName) {
            case FetchConfig:
                apiRequestData = RestClient.secureAPIRequest(jsonRequestData);
                restClientAPIObject.fetchConfigData(apiRequestData).enqueue(appRetrofitCallback);
                break;
            case VerifyPincodeData:
                apiRequestData = RestClient.secureAPIRequest(commonRequestData);
                restClientAPIObject.verifyPincodeData(apiRequestData).enqueue(appRetrofitCallback);
                break;

            default:
                AppLogger.log("Don't know how to handle, this service call", TAG, AppConstants.LogLevel.WARNING);

        }

    }*/

    public boolean performBodyDataValidation(String body, Headers headers, WebserviceConstants.APICallbackIdentifiers syncAPIs) {

        boolean valid = true;

        if (shouldSkipResponseValidation(syncAPIs)) {
            valid = true;
        } else {
            try {
                String tempJsonString = body;
                //System.out.println("body data:" + tempJsonString);
                String encodedChecksumFromServer = headers.get("una-content-md5");
                //System.out.println("header checksum data:" + encodedChecksumFromServer);
                byte[] checksumInBytes = Base64.decode(encodedChecksumFromServer, Base64.DEFAULT);
                String checksumInString = new String(checksumInBytes, "UTF-8");
                //System.out.println("decoded checksum from server: " + checksumInString);
                String newChecksumString = AppUtils.generateChecksum(tempJsonString);
                //System.out.println("new checksum data for body:" + newChecksumString);


                if (checksumInString.equalsIgnoreCase(newChecksumString)) {
                    valid = true;
                } else {
                    valid = false;
                }

            } catch (Exception e) {
                valid = false;
            }
        }
        return valid;
    }

    private boolean shouldSkipResponseValidation(WebserviceConstants.APICallbackIdentifiers serviceName) {
        boolean skip = false;
        switch (serviceName) {
            case FetchConfig:
                skip = true;
                break;

            case FetchHomepageData:
                skip = true;
                break;

            default:
                skip = true;
                break;

        }
        return skip;
    }

    public void fireAPIResponseError(String message, RetrofitApiCallback apiCallback, WebserviceConstants.APICallbackIdentifiers serviceName) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putInt(SERVICE_NAME, serviceName.ordinal());
        apiCallback.onError(bundle, serviceName);
    }

    public void performJsonToRootPojoConversion(final String jsonString, final Class dataConverterClass, final RetrofitApiCallback apiCallback, final WebserviceConstants.APICallbackIdentifiers serviceName) {

        Observable<APIResponseData> apiResponseDataObservable = Observable.fromCallable(new Callable<APIResponseData>() {
            @Override
            public APIResponseData call() throws Exception {
                return (APIResponseData) AppUtils.convertJsonToPojo(jsonString, APIResponseData.class);
            }
        });

        Observer<APIResponseData> observer = new Observer<APIResponseData>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(APIResponseData apiResponseData) {
                validateAPIResponse(apiResponseData, apiCallback, dataConverterClass, serviceName);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };

        apiResponseDataObservable
                //.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void validateAPIResponse(APIResponseData apiResponseData, RetrofitApiCallback apiCallback, Class dataConverterClass, WebserviceConstants.APICallbackIdentifiers serviceName) {
        if (apiResponseData == null) {
            AppLogger.log("Api response is null, returning....", TAG, AppConstants.LogLevel.ERROR);
            fireAPIResponseError(App.getAppContext().getResources().getString(R.string.internal_server_error), apiCallback, serviceName);
            return;
        }
        if (apiResponseData.getStatus() == -1) {
            AppLogger.log("Api response status is not available, returning....", TAG, AppConstants.LogLevel.ERROR);
            fireAPIResponseError(App.getAppContext().getResources().getString(R.string.internal_server_error), apiCallback, serviceName);
            return;
        }

        if (apiResponseData.getStatus() == 402) {
            AppLogger.log("Api response status is under maintenance, blocking App for use", TAG, AppConstants.LogLevel.ERROR);
            Toast.makeText(App.getAppContext(), apiResponseData.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (apiResponseData.getStatus() == 403) {
            AppLogger.log("Api response status is 403, doing Forced Logout", TAG, AppConstants.LogLevel.ERROR);
            App.getAppSession().performSignOut();
            return;
        }

        if (apiResponseData.getStatus() != 1) {
            AppLogger.log("Api response status is not 1, returning....", TAG, AppConstants.LogLevel.ERROR);
            Toast.makeText(App.getAppContext(), apiResponseData.getMessage(), Toast.LENGTH_LONG).show();
            fireAPIResponseError(apiResponseData.getMessage(), apiCallback, serviceName);
            return;
        }

        parseAPIResponse(apiResponseData, apiCallback, dataConverterClass, serviceName);
    }

    public void parseAPIResponse(APIResponseData apiResponseData, RetrofitApiCallback apiCallback, Class dataConverterClass, WebserviceConstants.APICallbackIdentifiers serviceName) {
        String dataString = null;
        if (apiResponseData.getData() != null) {
            String tempString = apiResponseData.getData().toString();
            if (!tempString.equals("{}")) {
                dataString = tempString;
            }
        }

        if (serviceName.ordinal() < 0) {
            AppLogger.log(TAG, "For response, Service name is not valid, returning", AppConstants.LogLevel.ERROR);
            return;
        }

        if (!AppUtils.isValidString(dataString)) {
            AppLogger.log(TAG, "In response, Data is not valid, triggering API Success event", AppConstants.LogLevel.ERROR);
            Bundle bundle = new Bundle();
            bundle.putInt(SERVICE_NAME, serviceName.ordinal());
            bundle.putString("message", apiResponseData.getMessage());
            apiCallback.onSuccess(bundle, serviceName);
        }

        performJsonToChildPojoConversion(dataString, apiCallback, dataConverterClass, serviceName);
    }

    public void performJsonToChildPojoConversion(final String jsonString, final RetrofitApiCallback apiCallback, final Class dataConverterClass, final WebserviceConstants.APICallbackIdentifiers serviceName) {

        Observable<CommonResponseData> commonResponseDataObservable = Observable.fromCallable(new Callable<CommonResponseData>() {
            @Override
            public CommonResponseData call() throws Exception {
                return (CommonResponseData) AppUtils.convertJsonToPojo(jsonString, dataConverterClass);
            }
        });

        Observer<CommonResponseData> commonResponseDataObserver = new Observer<CommonResponseData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(CommonResponseData commonResponseData) {
                apiCallback.onDataFetched(commonResponseData, serviceName);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        commonResponseDataObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonResponseDataObserver);
    }


    public void loadDataFromMock(String fileName, RetrofitApiCallback callback, final Class dataConverterClass, WebserviceConstants.APICallbackIdentifiers serviceName) {
        String mockJson = AppUtils.readMockJson(fileName);
        /*APIResponseData mockResponseData = new APIResponseData();
        mockResponseData.setData(new JsonObject(mockJson));*/

        //performJsonToChildPojoConversion(mockJson, callback, dataConverterClass, serviceName);

        AppLogger.log(TAG, "Mock Json from file:: " + mockJson);
        performJsonToRootPojoConversion(mockJson, dataConverterClass, callback, serviceName);
    }
}

