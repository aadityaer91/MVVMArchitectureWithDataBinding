package com.unaprime.app.android.una.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.logger.AppLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.WIFI_SERVICE;
import static com.unaprime.app.android.una.utils.AppConstants.UNAVAILABLE;

public class AppUtils {

    public static Boolean isValidString(Object string) {
        boolean result = true;
        if (string == null) {
            result = false;
        } else if (!(string instanceof String)) {
            result = false;
        } else {
            String aString = (String) string;
            if (aString.isEmpty()) {
                result = false;
            } else if (aString.replace(" ", "").isEmpty()) {
                result = false;
            }
        }
        return result;
    }

    public static String readMockJson(String fileName) {
        String jsonString = null;
        try {
            AssetManager assetManager = App.getAppContext().getAssets();
            InputStream is = assetManager.open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @Nullable
    public static Object convertJsonToPojo(String jsonString, Class clz) {
        Gson gson = new Gson();
        Object pojoObject;
        try {
            pojoObject = gson.fromJson(jsonString, clz);
        } catch (Exception e) {
            Log.e("Utils", "Unable to convert Json to Pojo", e);
            pojoObject = null;
        }
        return pojoObject;
    }

    public static String convertPojoToJson(Object obj) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        return jsonString;
    }

    public static String getModelName() {
        String myDeviceModel = android.os.Build.MODEL;
        if (!isValidString(myDeviceModel)) {
            myDeviceModel = UNAVAILABLE;
        }
        return myDeviceModel;
    }

    public static String getDevice() {
        String device = android.os.Build.DEVICE;
        if (!isValidString(device)) {
            device = UNAVAILABLE;
        }
        return device;
    }

    public static String getSdk() {
        String sdk = android.os.Build.VERSION.SDK;
        if (!isValidString(sdk)) {
            sdk = UNAVAILABLE;
        }
        return sdk;
    }

    public static String getProduct() {
        String product = android.os.Build.PRODUCT;
        if (!isValidString(product)) {
            product = UNAVAILABLE;
        }
        return product;
    }

    public static String getIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = null;
        if (wm != null) {
            ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        }
        if (!isValidString(ipAddress)) {
            ipAddress = UNAVAILABLE;
        }
        return ipAddress;
    }

    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = null;
        if (wifiManager != null) {
            wInfo = wifiManager.getConnectionInfo();
        }
        String macAddress = null;
        if (wInfo != null) {
            macAddress = wInfo.getMacAddress();
        }
        if (!isValidString(macAddress)) {
            macAddress = UNAVAILABLE;
        }
        return macAddress;
    }

    public static String getManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        if (isValidString(manufacturer)) {
            manufacturer = UNAVAILABLE;
        }
        return manufacturer;
    }

    public static String getAndroidVersion(int apilevel) {
        switch (apilevel) {
            case 15:
                return "ICE_CREAM_SANDWICH_MR1";
            case 16:
                return "JELLY_BEAN";
            case 17:
                return "JELLY_BEAN_MR1";
            case 18:
                return "JELLY_BEAN_MR2";
            case 19:
                return "KITKAT";
            case 20:
                return "KITKAT_WATCH";
            case 21:
                return "LOLLIPOP";
            case 22:
                return "LOLLIPOP_MR1";
            case 23:
                return "M";
            case 24:
                return "N";
            case 25:
                return "N_MR1";
            case 26:
                return "O";
            case 27:
                return "O_MR1";
            case 28:
                return "P";
            default:
                return UNAVAILABLE;
        }

    }

    //aes encrypt
    public static String encryptUsingAES(String key, String inputString) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] aBytes = cipher.doFinal(inputString.getBytes());
            String base64 = Base64.encodeToString(aBytes, Base64.DEFAULT).trim();
            return base64;
        } catch (Exception ex) {
            AppLogger.error("AppUtils", "Exception occured in encrypt :", ex);
        }
        return null;
    }

    public static String extractActualPublicKey(String publicKeyString) {
        String publicKeyPEM = publicKeyString.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("\n", "");

        return publicKeyPEM;
    }

    //RSA encrypt
    public static String encryptUsingRSA(String key, String inputString) {
        try {
            /*String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----\n", "");
            publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
            publicKeyPEM = publicKeyPEM.replace("\n", "");*/
            String publicKeyPEM = key;

            String encoded = "";
            byte[] encrypted = null;

            byte[] publicBytes = Base64.decode(publicKeyPEM, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING"); //or try with "RSA"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encrypted = cipher.doFinal(inputString.getBytes());
            encoded = Base64.encodeToString(encrypted, Base64.DEFAULT);

            return encoded;

        } catch (Exception ex) {
            AppLogger.error("AppUtils", "Exception occurred in encryption :", ex);
        }
        return null;
    }

    //checksum md5
    public static String generateChecksum(String inputString) {
        String checksum = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputString.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            checksum = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checksum;
    }

    public static Bundle bundleFromQueryString(Uri uri) {
        Bundle bundle = new Bundle();
        for (String param : uri.getQueryParameterNames()) {
            bundle.putString(param, uri.getQueryParameter(param));
        }
        return bundle;
    }

    public static boolean isNetworkAvailable(Context mContext) {
        boolean connection = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo net_info = cm.getActiveNetworkInfo();
                connection = net_info != null && net_info.isConnected();
            }
        } catch (Exception e) {
        }
        return connection;
    }

}
