package com.unaprime.app.android.una.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unaprime.app.android.una.MainActivity;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.utils.AppConstants;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


public abstract class BaseFragment extends Fragment {
    private final String TAG = BaseFragment.class.getSimpleName();

    protected FragmentManager getSupportFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }

    protected @Nullable
    MainActivity getMainActivity() {
        Activity currentActivity = getActivity();
        if (currentActivity instanceof MainActivity) {
            return (MainActivity) currentActivity;
        }
        return null;
    }

    protected void showLoadingDialog() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            AppLogger.log(TAG, "Couldn't get main activity!", AppConstants.LogLevel.ERROR);
            return;
        }
        mainActivity.showLoadingDialog();
    }

    protected void closeLoadingDialog() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            AppLogger.log(TAG, "Couldn't get main activity!", AppConstants.LogLevel.ERROR);
            return;
        }
        mainActivity.closeLoadingDialog();
    }

    protected void popBackStack() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            AppLogger.log(TAG, "Couldn't get main activity!", AppConstants.LogLevel.ERROR);
            return;
        }
        mainActivity.popBackStack();
    }

    protected void goHome() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) {
            AppLogger.log(TAG, "Couldn't get main activity!", AppConstants.LogLevel.ERROR);
            return;
        }
        mainActivity.popBackStack(true);
    }

    private static final int START = 0;
    private static final int STOP = 1;
    private PublishSubject<Integer> stopEvent = PublishSubject.create();
    private PublishSubject<Integer> startEvent = PublishSubject.create();

    public Observable<Integer> stopEvent() {
        return stopEvent.hide();
    }

    public Observable<Integer> startEvent() {
        return startEvent.hide();
    }

    protected abstract void bindViewModel();

    @Override
    public void onStart() {
        super.onStart();
        startEvent.onNext(START);
        bindViewModel();
    }

    @Override
    public void onStop() {
        stopEvent.onNext(STOP);
        super.onStop();
    }
}
