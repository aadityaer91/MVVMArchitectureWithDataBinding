package com.unaprime.app.android.una.services.retro;

import com.unaprime.app.android.una.services.requests.APIRequestData;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by aadityakumar on 10/04/18.
 */

public interface API {

    @POST("app/config")
    Call<ResponseBody> fetchConfigData(@Body APIRequestData data);

    @POST("user/login")
    Call<ResponseBody> doLogin(@Body APIRequestData data);

    @POST("user/homepage")
    Call<ResponseBody> fetchHomepageData(@Body APIRequestData data);

    @POST("user/generateOtp")
    Call<ResponseBody> generateLoginOtp(@Body APIRequestData data);


}
