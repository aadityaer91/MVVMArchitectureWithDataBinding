package com.unaprime.app.android.una.services.responses;

import com.google.gson.annotations.SerializedName;

public class RegisterOtpResponseData extends CommonResponseData {
    @SerializedName("tempUserId")
    String tempUserId;

    public String getTempUserId() {
        return tempUserId;
    }
}
