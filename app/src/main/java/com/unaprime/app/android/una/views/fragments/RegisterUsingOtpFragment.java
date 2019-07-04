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
import com.unaprime.app.android.una.databinding.FragmentRegisterOtpBinding;
import com.unaprime.app.android.una.events.UISwitchEvent;
import com.unaprime.app.android.una.logger.AppLogger;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.services.responses.LoginResponseData;
import com.unaprime.app.android.una.services.responses.NoDataResponseData;
import com.unaprime.app.android.una.services.responses.RegisterOtpResponseData;
import com.unaprime.app.android.una.utils.AppConstants;
import com.unaprime.app.android.una.utils.AppUtils;
import com.unaprime.app.android.una.validator.RegisterValidator;
import com.unaprime.app.android.una.viewmodels.RegisterOtpViewModel;
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

public class RegisterUsingOtpFragment extends BaseFragment {

    private final String TAG = RegisterUsingOtpFragment.class.getSimpleName();
    RegisterOtpViewModel registerOtpViewModel;
    @BindView(R.id.tvMessage1)
    CustomTextView tvMessage1;
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
    @BindView(R.id.btnRegister)
    CustomButton btnRegister;
    Unbinder unbinder;

    public static RegisterUsingOtpFragment newInstance() {

        Bundle args = new Bundle();

        RegisterUsingOtpFragment fragment = new RegisterUsingOtpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentRegisterOtpBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_otp, container, false);
        registerOtpViewModel = new RegisterOtpViewModel(App.getAppSession().getAppContentProvider(), new RegisterValidator(getContext()));
        binding.setViewModel(registerOtpViewModel);
        unbinder = ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }

    @OnEditorAction(R.id.etOtp)
    public boolean otpDoneAction(TextView v, int actionId,
                                 KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
            btnRegister.performClick();
            return true;
        }
        return false;
    }

    @OnEditorAction(R.id.etMobileNumber)
    public boolean mobileDoneAction(TextView v, int actionId,
                                    KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
            btnRegister.performClick();
            return true;
        }
        return false;
    }

    @Override
    protected void bindViewModel() {
        registerOtpViewModel.getUserData()
                .takeUntil(stopEvent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResponseData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResponseData commonResponseData) {
                        if (commonResponseData instanceof NoDataResponseData) {
                            tilOtp.setVisibility(View.VISIBLE);
                            etMobileNumber.setEnabled(false);
                        } else if (commonResponseData instanceof RegisterOtpResponseData) {
                            RegisterOtpResponseData responseData = (RegisterOtpResponseData) commonResponseData;
                            if (AppUtils.isValidString(responseData.getTempUserId())) {
                                Bundle bundle = new Bundle();
                                bundle.putString("tempUserId", responseData.getTempUserId());
                                EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.RegisterFormFragmentLoad, bundle));
                            }
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

        registerOtpViewModel.getRegisterError()
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
                registerOtpViewModel.mobileNumberChange(etMobileNumber.getText().toString());
                break;

            case R.id.etOtp:
                registerOtpViewModel.otpChange(etOtp.getText().toString());
                break;
        }
    }

    @OnTextChanged(R.id.etMobileNumber)
    public void mobileNumberChanged(CharSequence value) {
        registerOtpViewModel.mobileNumberChange(value.toString());
    }

    @OnTextChanged(R.id.etOtp)
    public void otpChanged(CharSequence value) {
        registerOtpViewModel.otpChange(value.toString());
    }

    @OnClick({R.id.tvResentOtp, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvResentOtp:
                registerOtpViewModel.verifyAndGenerateOtp();
                break;
            case R.id.btnRegister:
                if (btnRegister.getText().toString().equalsIgnoreCase(getString(R.string.register_generate_otp))) {
                    registerOtpViewModel.verifyAndGenerateOtp();
                } else if (btnRegister.getText().toString().equalsIgnoreCase(getString(R.string.register_next))) {
                    if (registerOtpViewModel.formValidationStatus()) {
                        registerOtpViewModel.verifyRegisterOtp();
                    }
                }
                break;
        }
    }
}
