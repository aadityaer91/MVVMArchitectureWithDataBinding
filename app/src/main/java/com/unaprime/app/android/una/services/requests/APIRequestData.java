package com.unaprime.app.android.una.services.requests;

public class APIRequestData {
    String userId;
    String data;
    String salt;
    String checksum;
    String appType;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
}
