package com.unaprime.app.android.una.services.retro;

import com.unaprime.app.android.una.logger.AppLogger;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by aaditya kumar on 15/05/2019.
 */
abstract public class AppRetrofitCallback<T> implements Callback<T> {

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        AppLogger.error("Retrofit", "Call   ===========>>>>>>>> F A I L E D  <<<<<<<===========" + call, t);
    }
}
