package com.unaprime.app.android.una.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.text.TextUtils;

import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.interfaces.RetrofitApiCallback;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.providers.AppContentProvider;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.services.responses.NoDataResponseData;
import com.unaprime.app.android.una.utils.AppUtils;
import com.unaprime.app.android.una.validator.LoginValidator;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class LoginOtpViewModel implements RetrofitApiCallback {
    private final String TAG = LoginViewModel.class.getSimpleName();
    AppContentProvider appContentProvider;
    LoginValidator loginValidator;
    private String mobileNumber = "";
    private String otp = "";

    private PublishSubject<CommonResponseData> userData = PublishSubject.create();
    private PublishSubject<String> loginError = PublishSubject.create();

    public ObservableField<String> mobileNumberError = new ObservableField<>();
    public ObservableField<String> otpError = new ObservableField<>();
    private ObservableBoolean isValid = new ObservableBoolean(false);
    public ObservableField<String> otpGenerated = new ObservableField<>();

    public LoginOtpViewModel(AppContentProvider appContentProvider, LoginValidator loginValidator) {
        this.appContentProvider = appContentProvider;
        this.loginValidator = loginValidator;
        otpGenerated.set(loginValidator.context.getString(R.string.login_generate_otp));
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

    public ObservableField<String> getOtpError() {
        return otpError;
    }

    public void mobileNumberChange(String value) {
        mobileNumber = value;
        mobileNumberError.set(loginValidator.validateMobileNumber(mobileNumber));
        updateValidationStatus();
    }

    public void otpChange(String value) {
        otp = value;
        otpError.set(loginValidator.validateOtp(otp));
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
                || otp.equals("");
    }

    private boolean hasError() {
        return mobileNumberError.get() != null
                || otpError.get() != null;
    }

    public void verifyAndGenerateOtp() {
        mobileNumberChange(mobileNumber);
        if (mobileNumberError.get() == null) {
            try {
                JSONObject requestJson = new JSONObject();
                requestJson.put("mobileNumber", mobileNumber);
                appContentProvider.generateOtpForLogin(requestJson, this);
            } catch (Exception e) {
                AppLogger.error(TAG, "Unable to create request object", e);
            }
        }
    }

    public void verifyLoginOtp() {
        mobileNumberChange(mobileNumber);
        otpChange(otp);
        if (isValid.get()) {
            try {
                JSONObject requestJson = new JSONObject();
                requestJson.put("mobileNumber", mobileNumber);
                requestJson.put("otp", otp);
                appContentProvider.verifyLoginCreds(requestJson, this);
            } catch (Exception e) {
                AppLogger.error(TAG, "Unable to create request object", e);
            }
        }
    }

    @Override
    public void onDataFetched(CommonResponseData appResponseData, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (syncAPI == WebserviceConstants.APICallbackIdentifiers.VerifyLoginData) {
            userData.onNext(appResponseData);
        }
    }

    @Override
    public void onError(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {

    }

    @Override
    public void onSuccess(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (syncAPI == WebserviceConstants.APICallbackIdentifiers.GenerateLoginOtp) {
            otpGenerated.set(loginValidator.context.getString(R.string.login));
            userData.onNext(new NoDataResponseData());
        }
    }
}
