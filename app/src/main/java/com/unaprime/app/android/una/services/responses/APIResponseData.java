package com.unaprime.app.android.una.services.responses;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class APIResponseData {
    @SerializedName("status")
    private int status = -1;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private JsonObject data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public JsonObject getData() {
        return data;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
