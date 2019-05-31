package com.unaprime.app.android.una.services.retro;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.AppSession;
import com.unaprime.app.android.una.BuildConfig;
import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.requests.APIRequestData;
import com.unaprime.app.android.una.utils.AppUtils;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aadityakumar on 10/04/18.
 */

public class RestClient {

    public static API getRetrofitBuilder(final String encryptedSHAHeaderKey, WebserviceConstants.APIBaseIdentifiers apiIdentifier) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS);


        try {
            httpClient = enableTls12OnPreLollipop(httpClient);
        } catch (Exception e) {
            AppLogger.error("RestClient", "Error while providing SSl socket", e);
        }

        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                RequestHeader requestHeaders = new RequestHeader(encryptedSHAHeaderKey);
                Request request = requestHeaders.buildRequestHeaders(builder)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        String baseURL;

        baseURL = APIBaseURL(apiIdentifier);

        if (baseURL.substring(baseURL.length() - 1).equalsIgnoreCase("/")) {
            //Do Nothing
        } else {
            baseURL = baseURL.concat("/");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(API.class);
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        //if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, null);
            client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

            ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build();

            List<ConnectionSpec> specs = new ArrayList<>();
            specs.add(cs);
            specs.add(ConnectionSpec.COMPATIBLE_TLS);
            specs.add(ConnectionSpec.CLEARTEXT);

            client.connectionSpecs(specs);
        } catch (Exception exc) {
            AppLogger.error("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
        }
        // }

        return client;
    }

    public static final String APIBaseURL(WebserviceConstants.APIBaseIdentifiers identifier) {
        String baseUrl = "";
        if (identifier == WebserviceConstants.APIBaseIdentifiers.Secured) {
            baseUrl = WebserviceConstants.getBaseUrlApp();
        } else if (identifier == WebserviceConstants.APIBaseIdentifiers.Others) {
            baseUrl = WebserviceConstants.getBaseUrlApp();
        } else {
            baseUrl = WebserviceConstants.getBaseUrlApp();
        }
        return baseUrl;
    }

    public static APIRequestData secureAPIRequest(JSONObject jsonRequestData) {
        APIRequestData apiRequestData = new APIRequestData();
        String requestJsonString = jsonRequestData.toString();
        AppSession appSession = App.getAppSession();
        String randomKey = RandomStringUtils.randomAlphanumeric(16).toUpperCase();
        AppLogger.log("RestClient : Okhttp: Data Arguments: ", requestJsonString);
        String inputString = AppUtils.encryptUsingAES(randomKey, requestJsonString);
        if (inputString != null) {
            AppLogger.log("RestClient : Okhttp: Secured Data: ", inputString);
        }
        String userId = appSession.getUserId();

        apiRequestData.setUserId(userId);
        try {
            apiRequestData.setData(URLEncoder.encode(inputString, "UTF-8"));
            apiRequestData.setChecksum(URLEncoder.encode(AppUtils.generateChecksum(inputString), "UTF-8"));

        if (AppUtils.isValidString(userId)) {
            String userPublicKey = appSession.getUserPublicKey();
            if (AppUtils.isValidString(userPublicKey)) {
                userPublicKey = AppUtils.extractActualPublicKey(userPublicKey);
                apiRequestData.setSalt(URLEncoder.encode(AppUtils.encryptUsingRSA(userPublicKey, randomKey), "UTF-8"));
            } else {
                appSession.performSignOut();
            }
        } else {
            apiRequestData.setSalt(URLEncoder.encode(AppUtils.encryptUsingRSA(App.getAppContext().getString(R.string.default_api_key), randomKey), "UTF-8"));
        }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return apiRequestData;

    }

    /*public static HashMap<String, String> secureApiArguments(HashMap<String, String> dataArguments) {
        HashMap<String, String> arguments = new HashMap<>();
        AppSession appSession = BaseApplication.getAppSession();

        String randomKey = RandomStringUtils.randomAlphanumeric(16).toUpperCase();
        String dataArgumentsInString = CommonUtils.mapToString(dataArguments);
        AppLog.v("RestClient : Okhttp: Data Arguments: ", dataArgumentsInString);
        String inputString = CommonUtils.encryptUsingAES(randomKey, dataArgumentsInString);

        String userId = appSession.getUserId();
        arguments.put("user_id", userId);
        arguments.put("token", appSession.getUniqueToken());
        arguments.put("data", inputString);
        arguments.put("checksum", CommonUtils.generateChecksum(inputString));
        if (ValidationUtils.isValidString(userId)) {
            String userPublicKey = appSession.getUserPublicKey();
            if (ValidationUtils.isValidString(userPublicKey)) {
                userPublicKey = CommonUtils.extractActualPublicKey(userPublicKey);
                arguments.put("salt", CommonUtils.encryptUsingRSA(userPublicKey, randomKey));
            } else {
                appSession.performSignOut();
            }
        } else {
            arguments.put("salt", CommonUtils.encryptUsingRSA(BaseApplication.getAppContext().getString(R.string.default_api_key), randomKey));
        }
//        AppLog.v("RestClient : Okhttp: random key: ", randomKey);
//        AppLog.v("RestClient : Okhttp: salt: ", arguments.get("salt"));
        return arguments;
    }*/
}
