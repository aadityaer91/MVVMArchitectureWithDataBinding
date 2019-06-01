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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.BuildConfig;
import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.databinding.FragmentLoginOtpBinding;
import com.unaprime.app.android.una.events.UISwitchEvent;
import com.unaprime.app.android.una.interfaces.UniqueFragmentNaming;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.services.responses.LoginResponseData;
import com.unaprime.app.android.una.services.responses.NoDataResponseData;
import com.unaprime.app.android.una.utils.AppConstants;
import com.unaprime.app.android.una.validator.LoginValidator;
import com.unaprime.app.android.una.viewmodels.LoginOtpViewModel;
import com.unaprime.app.android.una.views.customviews.CustomButton;
import com.unaprime.app.android.una.views.customviews.CustomTextInputEditText;
import com.unaprime.app.android.una.views.customviews.CustomTextInputLayout;
import com.unaprime.app.android.una.views.customviews.CustomTextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class LoginUsingOtpFragment extends BaseFragment implements UniqueFragmentNaming {

    private final String TAG = LoginUsingOtpFragment.class.getSimpleName();
    LoginOtpViewModel loginOtpViewModel;
    Unbinder unbinder;
    @BindView(R.id.etMobileNumber)
    CustomTextInputEditText etMobileNumber;
    @BindView(R.id.tilMobileNumber)
    CustomTextInputLayout tilMobileNumber;
    @BindView(R.id.tvMobileNumberError)
    CustomTextView tvMobileNumberError;
    @BindView(R.id.etOtp)
    CustomTextInputEditText etOtp;
    @BindView(R.id.tilOtp)
    CustomTextInputLayout tilOtp;
    @BindView(R.id.tvOtpError)
    CustomTextView tvOtpError;
    @BindView(R.id.tvResentOtp)
    CustomTextView tvResentOtp;
    @BindView(R.id.tvRegister)
    CustomTextView tvRegister;
    @BindView(R.id.llMiddleText)
    LinearLayout llMiddleText;
    @BindView(R.id.tvTermCond)
    CustomTextView tvTermCond;
    @BindView(R.id.llBottomTextPart1)
    LinearLayout llBottomTextPart1;
    @BindView(R.id.tvPrivacyPol)
    CustomTextView tvPrivacyPol;
    @BindView(R.id.llBottomTextPart2)
    LinearLayout llBottomTextPart2;
    @BindView(R.id.btnLogin)
    CustomButton btnLogin;
    @BindView(R.id.tvVersionInfo)
    CustomTextView tvVersionInfo;

    public static LoginUsingOtpFragment newInstance() {

        Bundle args = new Bundle();

        LoginUsingOtpFragment fragment = new LoginUsingOtpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoginOtpBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_otp, container, false);
        loginOtpViewModel = new LoginOtpViewModel(App.getAppSession().getAppContentProvider(), new LoginValidator(getContext()));
        binding.setViewModel(loginOtpViewModel);
        unbinder = ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvVersionInfo.setText("App Version: " + BuildConfig.VERSION_NAME + " / " + BuildConfig.VERSION_CODE);
        /*etOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN) {
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });*/
    }

    @OnEditorAction(R.id.etOtp)
    public boolean otpDoneAction(TextView v, int actionId,
                                 KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
            btnLogin.performClick();
            return true;
        }
        return false;
    }

    @OnEditorAction(R.id.etMobileNumber)
    public boolean mobileDoneAction(TextView v, int actionId,
                                    KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
            btnLogin.performClick();
            return true;
        }
        return false;
    }

    @Override
    protected void bindViewModel() {
        loginOtpViewModel.getUserData()
                .takeUntil(stopEvent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResponseData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResponseData commonResponseData) {
                        //update UI here
                        if (commonResponseData instanceof NoDataResponseData) {
                            tilOtp.setVisibility(View.VISIBLE);
                            etMobileNumber.setEnabled(false);
                        } else if (commonResponseData instanceof LoginResponseData) {
                            App.getAppSession().saveUserDataOnLogin((LoginResponseData) commonResponseData);
                            EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.HomePageFragmentLoad));
                        } else {
                            AppLogger.log(TAG, "Don't know how to handle user data", AppConstants.LogLevel.WARNING);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        loginOtpViewModel.getLoginError()
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnFocusChange({R.id.etMobileNumber, R.id.etOtp})
    public void focusChanged(View view, boolean hasFocus) {
        if (hasFocus) {
            return;
        }
        switch (view.getId()) {
            case R.id.etMobileNumber:
                loginOtpViewModel.mobileNumberChange(etMobileNumber.getText().toString());
                break;

            case R.id.etOtp:
                loginOtpViewModel.otpChange(etOtp.getText().toString());
                break;
        }
    }

    @OnTextChanged(R.id.etMobileNumber)
    public void mobileNumberChanged(CharSequence value) {
        loginOtpViewModel.mobileNumberChange(value.toString());
    }

    @OnTextChanged(R.id.etOtp)
    public void otpChanged(CharSequence value) {
        loginOtpViewModel.otpChange(value.toString());
    }

    @OnClick({R.id.tvResentOtp, R.id.tvRegister, R.id.tvTermCond, R.id.tvPrivacyPol, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvResentOtp:
                loginOtpViewModel.verifyAndGenerateOtp();
                break;
            case R.id.tvRegister:
                break;
            case R.id.tvTermCond:
                break;
            case R.id.tvPrivacyPol:
                break;
            case R.id.btnLogin:
                if (btnLogin.getText().toString().equalsIgnoreCase(getString(R.string.login_generate_otp))) {
                    loginOtpViewModel.verifyAndGenerateOtp();
                } else if (btnLogin.getText().toString().equalsIgnoreCase(getString(R.string.login))) {
                    if (loginOtpViewModel.formValidationStatus()) {
                        loginOtpViewModel.verifyLoginOtp();
                    }
                }
                break;
        }
    }
}
