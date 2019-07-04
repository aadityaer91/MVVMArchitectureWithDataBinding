package com.unaprime.app.android.una;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.unaprime.app.android.una.events.UISwitchEvent;
import com.unaprime.app.android.una.interfaces.UniqueFragmentNaming;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.utils.AppConstants;
import com.unaprime.app.android.una.utils.AppUtils;
import com.unaprime.app.android.una.views.dialogs.SpinkitProgressDialog;
import com.unaprime.app.android.una.views.fragments.BaseFragment;
import com.unaprime.app.android.una.views.fragments.HomepageFragment;
import com.unaprime.app.android.una.views.fragments.LoginUsingOtpFragment;
import com.unaprime.app.android.una.views.fragments.RegisterFormFragment;
import com.unaprime.app.android.una.views.fragments.RegisterUsingOtpFragment;
import com.unaprime.app.android.una.views.fragments.SplashFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.unaprime.app.android.una.events.UISwitchEvent.EventType.RegisterFormFragmentLoad;
import static com.unaprime.app.android.una.events.UISwitchEvent.EventType.SplashFragmentLoad;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    SpinkitProgressDialog progressDialogLoading;
    boolean isShowingProgressDialog;
    private Bundle requestedUriData;
    private Bundle gcmNotificationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialogLoading = new SpinkitProgressDialog();
        progressDialogLoading.setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        listenFragmentChange();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        resumeMainFragment();
    }

    /**
     * Listening Fragment change event to switch UI, if any
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UISwitchEvent event) {
        uiSwitchEvent(event);
    }

    public void uiSwitchEvent(UISwitchEvent event) {
        Bundle bundleArgs = event.getExtras();
        switch (event.myEventType) {

            case SplashFragmentLoad:
                switchFragment(SplashFragment.newInstance(bundleArgs), false);
                break;

            case LoginFragmentLoad:
                //switchFragment(LoginFragment.newInstance(), false);
                switchFragment(LoginUsingOtpFragment.newInstance(), false);
                break;

            case HomePageFragmentLoad:
                switchFragment(HomepageFragment.newInstance(), false);
                break;
            case RegisterOtpFragmentLoad:
                switchFragment(RegisterUsingOtpFragment.newInstance(), true);
                break;

            case RegisterFormFragmentLoad:
                switchFragment(RegisterFormFragment.newInstance(bundleArgs), true);
                break;

            default:
                AppLogger.log(TAG, "Don't know how to handle this status event!");

        }
    }

    public void switchFragment(BaseFragment fragment) {
        replaceMainFragment(fragment, true, true);
    }

    public void switchFragment(BaseFragment fragment, boolean addToBack) {
        replaceMainFragment(fragment, addToBack, true);
    }

    /**
     * Handling Intent & action based on params, if any
     *
     * @param intent
     */
   /* public void handleIntent(Intent intent) {
        Intent requestingIntent = intent;
        if (requestingIntent != null) {
            //Handle here if you want to perform some action according to new Intent of already launched app
        }
        resumeMainFragment();
    }*/
    public void handleIntent(Intent intent) {
        Intent requestingIntent = intent;
        if (requestingIntent != null) {
            Uri intentUri = requestingIntent.getData();
            if (intentUri != null) {
                requestedUriData = AppUtils.bundleFromQueryString(requestingIntent.getData());
            } else {
                requestedUriData = null;
            }
            gcmNotificationData = convertExtrasForNotification(requestingIntent.getExtras());

            manageFragmentsStack(true, true);

            int flags = getIntent().getFlags();
            if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
                // The activity was launched from history
                // remove extras here

                requestedUriData = null;
                gcmNotificationData = null;
            }

            if (requestedUriData != null) {
                requestedUriData.putBoolean("deepLinked", true);
                loadSplashScreen(requestingIntent, requestedUriData);
            } else if (gcmNotificationData != null) {
                gcmNotificationData.putBoolean("deepLinked", true);
                loadSplashScreen(requestingIntent, gcmNotificationData);
            } else {
                loadSplashScreen(requestingIntent, null);
            }
        } else {
            loadSplashScreen(requestingIntent, null);
        }
    }

    /**
     * Overriden method responsible for handling new intent launched, if some intent of an app already running
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        //please use local intent object, otherwise getIntent will ....
        handleIntent(intent);
    }

    /**
     * Method responsible to handle fragment load & resume if already loaded
     */
    public void resumeMainFragment() {

        BaseFragment nextFragment = currentTopFragment();

        if (nextFragment == null) {
            nextFragment = SplashFragment.newInstance(null);
        }
        replaceMainFragment(nextFragment, false, false);

    }

    /**
     * Method handling fragment load, transition & backstack
     *
     * @param fragment
     * @param addToBack
     * @param addTransitions
     */
    protected void replaceMainFragment(BaseFragment fragment, boolean addToBack, boolean addTransitions) {
        FragmentManager manager = getSupportFragmentManager();
        String fragmentClassName = simpleClassName(fragment);
        if (fragment instanceof UniqueFragmentNaming) {
            Fragment foundFragment = manager.findFragmentByTag(fragmentClassName);
            if (foundFragment != null) {
                AppLogger.log(TAG, "Found existing instance of unique fragment, popping instead of pushing!");
                try {
                    if (fragment instanceof HomepageFragment) {
                        popBackStack(true);

                    } else {
                        manager.popBackStack(fragmentClassName, 0);
                    }
                } catch (IllegalStateException e) {
                    supportFinishAfterTransition();
                }
                return;
            }
        }

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        if (addTransitions) {
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        }

        BaseFragment tempCurrentTopFragment = currentTopFragment();
        if (tempCurrentTopFragment != null && (tempCurrentTopFragment instanceof HomepageFragment)) {
            fragmentTransaction.replace(R.id.mainFragmentContainer, fragment, fragmentClassName);
        } else if (fragment instanceof SplashFragment || fragment instanceof HomepageFragment || fragment instanceof LoginUsingOtpFragment) {
            fragmentTransaction.replace(R.id.mainFragmentContainer, fragment, fragmentClassName);
        } else {
            fragmentTransaction.add(R.id.mainFragmentContainer, fragment, fragmentClassName);
        }

        if (addToBack) {
            fragmentTransaction.addToBackStack(fragmentClassName);
        }
        if (isFinishing()) {
            //Don't do anything, commit allowing state loss will also cause a crash because the current activity is going away
            AppLogger.log(TAG, "Activity is finishing, not doing replace main fragment!", AppConstants.LogLevel.ERROR);
            return;
        }
        if (manager.isDestroyed()) {
            AppLogger.log(TAG, "Activity is destroyed, not doing replace main fragment!", AppConstants.LogLevel.ERROR);
            return;
        }

        fragmentTransaction.commitAllowingStateLoss();

    }

    /**
     * Method which will handle going back to previos fragment from current fragment
     */
    public void popBackStack() {
        //Empty params function to allow single popping without hassle
        popBackStack(false);
    }

    /**
     * Method can lead to first fragment by clearing fragment backstack
     *
     * @param allTheWayHome
     */
    public void popBackStack(boolean allTheWayHome) {
        popBackStack(allTheWayHome, false);
    }

    /**
     * Method can lead to first fragment by clearing fragment backstack immediately
     *
     * @param allTheWayHome
     * @param popImmediate
     */
    public void popBackStack(boolean allTheWayHome, boolean popImmediate) {

        int popFlags = 0;
        if (allTheWayHome) {
            popFlags = FragmentManager.POP_BACK_STACK_INCLUSIVE;
        }
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() == 0) { //DKG: There's nowhere to go back to
            if (allTheWayHome) {
                if (currentTopFragment() instanceof SplashFragment) {
                    SplashFragment splashFragment = (SplashFragment) currentTopFragment();
                    if (splashFragment != null) {
                        //do somthing if you want to refresh
                    }
                } else {
                    Log.e("MainActivity", "Non home page fragment is at bottom of stack");
                    replaceMainFragment(SplashFragment.newInstance(null), false, false);
                }

            } else {
                finish();
                return;
            }
        }
        if (isFinishing()) {
            Log.e("MainActivity", "Activity is finishing, not doing pop back stack!");
            return;
        }
        try {
            if (popImmediate) {
                manager.popBackStackImmediate(null, popFlags);
            } else {
                manager.popBackStack(null, popFlags);
            }
        } catch (IllegalStateException e) {
            supportFinishAfterTransition();
        }
    }

    /**
     * Method used for getting current loaded fragment on top of fragment backstack
     *
     * @return
     */
    @Nullable
    protected BaseFragment currentTopFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.mainFragmentContainer);
        if (fragment == null) {
            return null;
        }
        if (!(fragment instanceof BaseFragment)) {
            return null;
        }
        return (BaseFragment) fragment;
    }

    public void listenFragmentChange() {
        FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //appController.currentFragment = currentTopFragment();
                    }
                }, 200);

            }
        });
    }

    /**
     * method just for fetching class name from object
     *
     * @param object
     * @return
     */
    @Nullable
    public static String simpleClassName(Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().getSimpleName();
    }

    public void showLoadingDialog() {
        AppLogger.log(TAG, "Showing loading dialog");
        if (progressDialogLoading.isAdded()) {
            AppLogger.log(TAG, "Already added, not doing anything");
            return;
        }
        if (progressDialogLoading.started) {
            AppLogger.log(TAG, "Already started, returning");
            return;
        }
        Dialog actualDialog = progressDialogLoading.getDialog();
        if (actualDialog != null) {
            if (actualDialog.isShowing()) {
                AppLogger.log(TAG, "Progress dialog is already showing, not doing anything");
                return;
            }
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment loadingFragment = manager.findFragmentByTag("SPINKITLOADING");
        if (loadingFragment != null) {
            AppLogger.log(TAG, "Found loading fragment tag, returning");
            return;
        }
        if (isShowingProgressDialog) {
            AppLogger.log(TAG, "Bypassed all framework checks, but found our boolean flag to be set, returning");
            return;
        }
        isShowingProgressDialog = true;
        try {
            progressDialogLoading.show(manager, "SPINKITLOADING");
        } catch (IllegalStateException e) {
            supportFinishAfterTransition();
        }
    }

    public void closeLoadingDialog() {
        if (!progressDialogLoading.started) {
            AppLogger.log(TAG, "Already stopped, returning");
            return;
        }
        Dialog actualDialog = progressDialogLoading.getDialog();
        if (actualDialog != null) {
            if (!actualDialog.isShowing()) {
                AppLogger.log(TAG, "Progress dialog is already hidden, not doing anything");
                return;
            }
        }

        progressDialogLoading.dismissAllowingStateLoss();
        isShowingProgressDialog = false;
        AppLogger.log(TAG, "Destroyed progress dialog");
    }

    @Override
    public void onBackPressed() {
        popBackStack();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public Bundle convertExtrasForNotification(Bundle mainExtras) {
        Bundle bundle = null;
        if (mainExtras != null) {
            bundle = new Bundle();

            String title = mainExtras.getString("title");
            String message = mainExtras.getString("message");
            String type = mainExtras.getString("type");
            if (!AppUtils.isValidString(type)) {
                type = "text";
            }

            String imageUrl = mainExtras.getString("imgUrl");
            String loanLeadId = mainExtras.getString("llId");
            String destinationType = mainExtras.getString("dt");
            String destinationTypeUrl = mainExtras.getString("dtUrl");

            if (AppUtils.isValidString(title)) {
                bundle.putString("title", title);
            }
            if (AppUtils.isValidString(message)) {
                bundle.putString("message", message);
            }
            if (AppUtils.isValidString(type)) {
                bundle.putString("type", type);
            }
            if (AppUtils.isValidString(imageUrl)) {
                bundle.putString("imgUrl", imageUrl);
            }
            if (AppUtils.isValidString(loanLeadId)) {
                bundle.putString("llId", loanLeadId);
            }
            if (AppUtils.isValidString(destinationType)) {
                bundle.putString("dt", destinationType);
            }
            if (AppUtils.isValidString(destinationTypeUrl)) {
                bundle.putString("dtUrl", destinationTypeUrl);
            }

        }
        return bundle;
    }

    public void manageFragmentsStack(boolean allTheWayHome, boolean popImmediate) {
        int popFlags = 0;

        if (allTheWayHome) {
            popFlags = FragmentManager.POP_BACK_STACK_INCLUSIVE;
        }
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) { //DKG: There's nowhere to go back to
            if (isFinishing()) {
                AppLogger.log(TAG, "Activity is finishing, not doing pop back stack!", AppConstants.LogLevel.ERROR);
                return;
            }
            try {
                if (popImmediate) {
                    manager.popBackStackImmediate(null, popFlags);
                } else {
                    manager.popBackStack(null, popFlags);
                }
            } catch (IllegalStateException e) {
                supportFinishAfterTransition();
            }
        }
    }

    private void loadSplashScreen(Intent intent, Bundle arguments) {
        EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.SplashFragmentLoad, arguments));
    }

}
