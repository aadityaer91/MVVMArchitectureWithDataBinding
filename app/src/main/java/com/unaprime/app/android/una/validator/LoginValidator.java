package com.unaprime.app.android.una.validator;

import android.content.Context;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.utils.AppUtils;

public class LoginValidator {
    public Context context;

    public LoginValidator(Context context) {
        this.context = context;
    }

    public String validateMobileNumber(String mobileNumber) {
        return validMobileNumber(mobileNumber) ? null : context.getString(R.string.login_mobile_number_error);
        //return validMobileNumber(mobileNumber) ? null : "Please enter valid mobile number";
    }

    private boolean validMobileNumber(String mobileNumber) {
        return AppUtils.isValidString(mobileNumber) && mobileNumber.length() == 10;
    }

    public String validatePassword(String password) {
        return validPassword(password) ? null : context.getString(R.string.login_password_error);
    }

    private boolean validPassword(String password) {
        return AppUtils.isValidString(password) && password.length() >= 6;
    }

    public String validateOtp(String otp) {
        return validOtp(otp) ? null : context.getString(R.string.login_otp_error);
    }

    private boolean validOtp(String otp) {
        return AppUtils.isValidString(otp) && otp.length() >= 6;
    }

}
