package com.unaprime.app.android.una.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.AppSession;
import com.unaprime.app.android.una.MainActivity;
import com.unaprime.app.android.una.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomepageFragment extends BaseFragment {

    Unbinder unbinder;

    public static HomepageFragment newInstance() {

        Bundle args = new Bundle();

        HomepageFragment fragment = new HomepageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void bindViewModel() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleDeepLinkedInstance();
    }

    /*
     * This method is used to handle notification click
     * */

    private void handleDeepLinkedInstance() {
        AppSession appSession = App.getAppSession();
        BaseFragment fragmentToLoad = appSession.getDeepLinkedFragmentInstance();
        boolean isAllowed = appSession.getDestinationType() != null;// && appSession.validateUserType(appSession.getDestinationType());
        if (fragmentToLoad != null && isAllowed) {

            App.getAppSession().saveDeepLinkedFragments(null, null);
            MainActivity mainActivity = getMainActivity();
            if (mainActivity == null) {
                mainActivity = (MainActivity) App.getCurrentActivity();
            }
            mainActivity.switchFragment(fragmentToLoad, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tvSignOut)
    public void onViewClicked() {
        App.getAppSession().performSignOut();
    }
}
