package com.unaprime.app.android.una.services.responses;

import com.google.gson.annotations.SerializedName;

public class LoginResponseData extends CommonResponseData {
    @SerializedName("userId")
    String userId;

    @SerializedName("apiSecurityKey")
    String apiSecurityKey;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiSecurityKey() {
        return apiSecurityKey;
    }

    public void setApiSecurityKey(String apiSecurityKey) {
        this.apiSecurityKey = apiSecurityKey;
    }
}
