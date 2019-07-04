package com.unaprime.app.android.una.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unaprime.app.android.una.R;

public class RegisterFormFragment extends BaseFragment {

    public static RegisterFormFragment newInstance(Bundle args) {
        if (args == null) {
            args = new Bundle();
        }
        RegisterFormFragment fragment = new RegisterFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_form, container, false);
        return view;
    }

    @Override
    protected void bindViewModel() {

    }
}
