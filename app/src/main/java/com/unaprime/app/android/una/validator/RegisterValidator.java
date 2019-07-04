package com.unaprime.app.android.una.validator;

import android.content.Context;

import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.utils.AppUtils;

public class RegisterValidator {
    public Context context;

    public RegisterValidator(Context context) {
        this.context = context;
    }

    public String validateMobileNumber(String mobileNumber) {
        return validMobileNumber(mobileNumber) ? null : context.getString(R.string.login_mobile_number_error);
    }

    private boolean validMobileNumber(String mobileNumber) {
        return AppUtils.isValidString(mobileNumber) && mobileNumber.length() == 10;
    }

    public String validateOtp(String otp) {
        return validOtp(otp) ? null : context.getString(R.string.login_otp_error);
    }

    private boolean validOtp(String otp) {
        return AppUtils.isValidString(otp) && otp.length() >= 6;
    }
}
