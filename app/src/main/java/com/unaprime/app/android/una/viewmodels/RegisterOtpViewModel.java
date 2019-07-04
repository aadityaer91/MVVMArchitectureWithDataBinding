package com.unaprime.app.android.una.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;

import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.interfaces.RetrofitApiCallback;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.providers.AppContentProvider;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.services.responses.NoDataResponseData;
import com.unaprime.app.android.una.validator.RegisterValidator;

import org.json.JSONObject;

import io.reactivex.subjects.PublishSubject;

public class RegisterOtpViewModel implements RetrofitApiCallback {
    private final String TAG = RegisterOtpViewModel.class.getSimpleName();
    AppContentProvider appContentProvider;
    RegisterValidator registerValidator;

    private String mobileNumber = "";
    private String otp = "";

    private PublishSubject<CommonResponseData> userData = PublishSubject.create();
    private PublishSubject<String> registerError = PublishSubject.create();

    public ObservableField<String> mobileNumberError = new ObservableField<>();
    public ObservableField<String> otpError = new ObservableField<>();
    private ObservableBoolean isValid = new ObservableBoolean(false);
    public ObservableField<String> otpGenerated = new ObservableField<>();
    public ObservableField<String> textMessageChange = new ObservableField<>();

    public RegisterOtpViewModel(AppContentProvider appContentProvider, RegisterValidator registerValidator) {
        this.appContentProvider = appContentProvider;
        this.registerValidator = registerValidator;
        otpGenerated.set(registerValidator.context.getString(R.string.register_generate_otp));
        textMessageChange.set(registerValidator.context.getString(R.string.register_otp_text1));
    }

    public PublishSubject<CommonResponseData> getUserData() {
        return userData;
    }

    public PublishSubject<String> getRegisterError() {
        return registerError;
    }

    public ObservableField<String> getMobileNumberError() {
        return mobileNumberError;
    }

    public ObservableField<String> getOtpError() {
        return otpError;
    }

    public void mobileNumberChange(String value) {
        mobileNumber = value;
        mobileNumberError.set(registerValidator.validateMobileNumber(mobileNumber));
        updateValidationStatus();
    }

    public void otpChange(String value) {
        otp = value;
        otpError.set(registerValidator.validateOtp(otp));
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
                appContentProvider.generateOtpForRegister(requestJson, this);
            } catch (Exception e) {
                AppLogger.error(TAG, "Unable to create request object", e);
            }
        }
    }

    public void verifyRegisterOtp() {
        mobileNumberChange(mobileNumber);
        otpChange(otp);
        if (isValid.get()) {
            try {
                JSONObject requestJson = new JSONObject();
                requestJson.put("mobileNumber", mobileNumber);
                requestJson.put("otp", otp);
                appContentProvider.verifyRegisterOtp(requestJson, this);
            } catch (Exception e) {
                AppLogger.error(TAG, "Unable to create request object", e);
            }
        }
    }


    @Override
    public void onDataFetched(CommonResponseData appResponseData, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (syncAPI == WebserviceConstants.APICallbackIdentifiers.VerifyRegisterOtp) {
            userData.onNext(appResponseData);
        }
    }

    @Override
    public void onError(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {

    }

    @Override
    public void onSuccess(Bundle bundle, WebserviceConstants.APICallbackIdentifiers syncAPI) {
        if (syncAPI == WebserviceConstants.APICallbackIdentifiers.GenerateRegisterOtp) {
            otpGenerated.set(registerValidator.context.getString(R.string.register_next));
            textMessageChange.set(registerValidator.context.getString(R.string.register_otp_text2));
            userData.onNext(new NoDataResponseData());
        }
    }
}
