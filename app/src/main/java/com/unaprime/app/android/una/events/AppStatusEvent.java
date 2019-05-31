package com.unaprime.app.android.una.events;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by aaditya on 10/02/2018.
 */

public class AppStatusEvent extends BaseEvent {
    public EventType myEventType;
    Bundle extras;
    public enum EventType{
        StatusForcedLogout,
        StatusAccessDenied

    }
    public AppStatusEvent(EventType type){
        myEventType = type;
    }

    public AppStatusEvent(EventType myEventType, @Nullable Bundle includeExtras) {
        this.myEventType = myEventType;
        this.extras = includeExtras;
    }

    @Nullable
    public Bundle getExtras() {
        return extras;
    }

    public void setExtras(Bundle extras) {
        this.extras = extras;
    }
}
