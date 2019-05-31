package com.unaprime.app.android.una.interfaces;

import android.os.Bundle;

import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.CommonResponseData;

public interface RetrofitApiCallback {
    void onDataFetched(CommonResponseData appResponseData, WebserviceConstants.APICallbackIdentifiers syncAPI);

    void onError(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI);

    void onSuccess(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI);

}
