package com.unaprime.app.android.una.services.responses;

import com.google.gson.annotations.SerializedName;

public class ConfigResponseData extends CommonResponseData {
    @SerializedName("appUpdateStatus")
    int appUpdateStatus;

    @SerializedName("appUpdateMessage")
    String appUpdateMessage;

    @SerializedName("appMaintenanceStatus")
    int appMaintenanceStatus;

    @SerializedName("appMaintenanceMessage")
    String appMaintenanceMessage;

    public int getAppUpdateStatus() {
        return appUpdateStatus;
    }

    public String getAppUpdateMessage() {
        return appUpdateMessage;
    }

    public int getAppMaintenanceStatus() {
        return appMaintenanceStatus;
    }

    public String getAppMaintenanceMessage() {
        return appMaintenanceMessage;
    }
}
