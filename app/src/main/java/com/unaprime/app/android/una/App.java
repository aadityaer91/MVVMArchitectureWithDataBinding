package com.unaprime.app.android.una;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.unaprime.app.android.una.logger.AppLogger;

public class App extends MultiDexApplication {
    private final String TAG = App.class.getSimpleName();
    public static Context mAppContext;
    public static FragmentActivity currentActivity;
    public static AppSession mAppSession;
    private static DisplayImageOptions imageDisplayOptions;
    private static DisplayImageOptions nonFadeDisplayImageOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        mAppSession = new AppSession();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        imageDisplayOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(false)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(350, true, true, false))
                .build();
        nonFadeDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(false)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();


        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppLogger.log(TAG, "Activity Created" + activity.getClass().getSimpleName());

                if (activity instanceof FragmentActivity) {
                    currentActivity = (FragmentActivity) activity;
                }
                if (!mAppSession.isSetupHasCompleted()) {
                    mAppSession.appLaunchSetup();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                AppLogger.log(TAG, "Activity Started:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                AppLogger.log(TAG, "Activity Resumed:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                AppLogger.log(TAG, "Activity Paused:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                AppLogger.log(TAG, "Activity Stopped:" + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                AppLogger.log(TAG, "Activity Instance Saved:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppLogger.log(TAG, "Activity Destroyed:" + activity.getLocalClassName());
            }
        });
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static FragmentActivity getCurrentActivity() {
        return currentActivity;
    }

    public static AppSession getAppSession() {
        return mAppSession;
    }

    public static DisplayImageOptions defaultDisplayImageOptions() {
        return imageDisplayOptions;
    }

    public static DisplayImageOptions nonFadingDisplayImageOptions() {
        return nonFadeDisplayImageOptions;
    }


}
