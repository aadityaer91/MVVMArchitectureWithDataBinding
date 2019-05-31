package com.unaprime.app.android.una.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.databinding.FragmentLoginBinding;
import com.unaprime.app.android.una.events.AppStatusEvent;
import com.unaprime.app.android.una.events.UISwitchEvent;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.validator.LoginValidator;
import com.unaprime.app.android.una.viewmodels.LoginViewModel;
import com.unaprime.app.android.una.views.customviews.CustomTextInputEditText;
import com.unaprime.app.android.una.views.customviews.CustomTextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class LoginFragment extends BaseFragment {

    private final String TAG = LoginFragment.class.getSimpleName();
    LoginViewModel loginViewModel;
    @BindView(R.id.tietMobileNumber)
    CustomTextInputEditText tietMobileNumber;
    @BindView(R.id.tvMobileNumberError)
    CustomTextView tvMobileNumberError;
    @BindView(R.id.tietPassword)
    CustomTextInputEditText tietPassword;
    @BindView(R.id.tvPasswordError)
    CustomTextView tvPasswordError;
    @BindView(R.id.tvLogin)
    CustomTextView tvLogin;
    Unbinder unbinder;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        loginViewModel = new LoginViewModel(App.getAppSession().getAppContentProvider(), new LoginValidator(getContext()));
        binding.setViewModel(loginViewModel);
        unbinder = ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tietPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN) {
                    tvLogin.performClick();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void bindViewModel() {
        loginViewModel.getUserData()
                .takeUntil(stopEvent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResponseData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResponseData commonResponseData) {
                        //update UI here
                        EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.HomePageFragmentLoad));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        loginViewModel.getLoginError()
                .takeUntil(stopEvent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @OnFocusChange({R.id.tietMobileNumber, R.id.tietPassword})
    public void focusChanged(View view, boolean hasFocus) {
        if (hasFocus) {
            return;
        }
        switch (view.getId()) {
            case R.id.tietMobileNumber:
                loginViewModel.mobileNumberChange(tietMobileNumber.getText().toString());
                break;

            case R.id.tietPassword:
                loginViewModel.passwordChange(tietPassword.getText().toString());
                break;
        }
    }

    @OnTextChanged(R.id.tietMobileNumber)
    public void mobileNumberChanged(CharSequence value) {
        loginViewModel.mobileNumberChange(value.toString());
    }

    @OnTextChanged(R.id.tietPassword)
    public void passwordChanged(CharSequence value) {
        loginViewModel.passwordChange(value.toString());
    }

    @OnClick(R.id.tvLogin)
    public void onViewClicked() {
        AppLogger.log(TAG, "Login button clicked");
        if (loginViewModel.formValidationStatus()) {
            loginViewModel.verifyLoginCreds();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
