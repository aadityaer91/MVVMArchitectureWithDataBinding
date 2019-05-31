package com.unaprime.app.android.una.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.interfaces.RetrofitApiCallback;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.providers.AppContentProvider;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.validator.LoginValidator;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static com.unaprime.app.android.una.services.WebserviceConstants.APICallbackIdentifiers.VerifyLoginData;

public class LoginViewModel implements RetrofitApiCallback {

    private final String TAG = LoginViewModel.class.getSimpleName();
    AppContentProvider appContentProvider;
    LoginValidator loginValidator;
    private String mobileNumber = "";
    private String password = "";

    private PublishSubject<CommonResponseData> userData = PublishSubject.create();
    private PublishSubject<String> loginError = PublishSubject.create();

    public ObservableField<String> mobileNumberError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();
    private ObservableBoolean isValid = new ObservableBoolean(false);

    public LoginViewModel(AppContentProvider appContentProvider, LoginValidator loginValidator) {
        this.appContentProvider = appContentProvider;
        this.loginValidator = loginValidator;
    }

    public Observable<CommonResponseData> getUserData() {
        return userData.hide();
    }

    public Observable<String> getLoginError() {
        return loginError.hide();
    }

    public ObservableField<String> getMobileNumberError() {
        return mobileNumberError;
    }

    public ObservableField<String> getPasswordError() {
        return passwordError;
    }

    public void mobileNumberChange(String value) {
        mobileNumber = value;
        mobileNumberError.set(loginValidator.validateMobileNumber(mobileNumber));
        updateValidationStatus();
    }

    public void passwordChange(String value) {
        password = value;
        passwordError.set(loginValidator.validatePassword(password));
        updateValidationStatus();
    }

    public boolean formValidationStatus() {
        return isValid.get();
    }

    private void updateValidationStatus() {
        isValid.set(!hasEmptyData() && !hasError());
    }

    private boolean hasEmptyData() {
        return mobileNumber.equals("")
                || password.equals("");
    }

    private boolean hasError() {
        return mobileNumberError.get() != null
                || passwordError.get() != null;
    }

    public void verifyLoginCreds() {
        if (isValid.get()) {
            try {
                JSONObject requestJson = new JSONObject();
                requestJson.put("mobileNumber", mobileNumber);
                requestJson.put("password", password);
                appContentProvider.verifyLoginCreds(requestJson, this);
            } catch (Exception e) {
                AppLogger.error(TAG, "Unable to create request object", e);
            }
        }
    }

    @Override
    public void onDataFetched(CommonResponseData appResponseData, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (syncAPI == VerifyLoginData) {
            userData.onNext(appResponseData);
        }
    }

    @Override
    public void onError(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (syncAPI == VerifyLoginData) {
            if (bundle.containsKey("message"))
                loginError.onNext(bundle.getString("message"));
        }
    }

    @Override
    public void onSuccess(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {

    }
}
