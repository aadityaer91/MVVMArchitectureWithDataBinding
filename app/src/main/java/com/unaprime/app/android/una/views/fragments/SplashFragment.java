package com.unaprime.app.android.una.views.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.unaprime.app.android.una.App;
import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.databinding.FragmentSplashBinding;
import com.unaprime.app.android.una.events.UISwitchEvent;
import com.unaprime.app.android.una.services.responses.CommonResponseData;
import com.unaprime.app.android.una.utils.AppUtils;
import com.unaprime.app.android.una.viewmodels.SplashViewModel;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static java.lang.System.exit;

public class SplashFragment extends BaseFragment {

    private final String TAG = SplashFragment.class.getSimpleName();
    public Dialog forceUpdateDialog;
    SplashViewModel splashViewModel;
    Unbinder unbinder;
    @BindView(R.id.ivSplashLogo)
    ImageView ivSplashLogo;

    public static SplashFragment newInstance(Bundle args) {

        if (args == null) {
            args = new Bundle();
            args.putBoolean("isArgumentsAvailable", false);
        } else {
            args.putBoolean("isArgumentsAvailable", true);
        }


        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentSplashBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);
        splashViewModel = new SplashViewModel(App.getAppSession().getAppContentProvider(), getArguments(), App.getAppSession());
        binding.setViewModel(splashViewModel);
        unbinder = ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        handleAppLaunch();
    }

    private void handleAppLaunch() {
        if (forceUpdateDialog != null) {
            if (forceUpdateDialog.isShowing()) {
                forceUpdateDialog.dismiss();
            }
        }
        if (AppUtils.isNetworkAvailable(getContext())) {
            //here we need to call actual fetch config api
            splashViewModel.fetchConfigData();

            //loadLoginForNow();//temporary
        } else {
            if (getView() != null) {
                Snackbar snackbar = Snackbar
                        .make(getView(), "Please check your internet connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleAppLaunch();
                            }
                        });
                snackbar.show();
            }
        }
    }

    public void showForceUpdatePopup(String message, final int forceFlag, final boolean flag) {
        forceUpdateDialog = new Dialog(getContext(), R.style.AppTheme);
        forceUpdateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forceUpdateDialog.setCancelable(false);
        forceUpdateDialog.setCanceledOnTouchOutside(false);
        forceUpdateDialog.setContentView(R.layout.dialog_force_update);
        forceUpdateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        final TextView later = (TextView) forceUpdateDialog.findViewById(R.id.tvLater);
        TextView update = (TextView) forceUpdateDialog.findViewById(R.id.tvUpdate);
        TextView message_ToShow = (TextView) forceUpdateDialog.findViewById(R.id.tvMessage);

        message_ToShow.setText(message);

        if (forceFlag > 0 && flag) {
            if (forceFlag == 1) {
                later.setText("Quit");
            } else if (forceFlag == 2) {
                later.setText("Later");
            }
        }

        if (forceFlag > 1 && !flag) {
            if (forceFlag == 1) {
                later.setText("QUIT");
                update.setVisibility(View.GONE);
            } else if (forceFlag == 2) {
                update.setText("OK");
                later.setVisibility(View.GONE);
            }
        }


        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close App
                if (forceFlag > 0 && flag) {
                    if (forceFlag == 1) {
                        if (getActivity() != null) {
                            getActivity().finishAffinity();
                        }
                        exit(0);
                    } else if (forceFlag == 2) {
                        if (forceUpdateDialog != null) {
                            if (forceUpdateDialog.isShowing()) {
                                forceUpdateDialog.dismiss();
                            }
                        }
                        splashViewModel.checkIsAdmin();
                    }
                }

                if (forceFlag > 0 && !flag) {
                    if (forceFlag == 1) {

                        if (getActivity() != null) {
                            getActivity().finishAffinity();
                        }
                        exit(0);
                    }
                }


            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to play store.
                if (forceUpdateDialog != null) {
                    if (forceUpdateDialog.isShowing()) {
                        forceUpdateDialog.dismiss();
                    }
                }

                if (!flag && forceFlag == 2) {
                    splashViewModel.fragmentToHandleRequestedAction();
                    splashViewModel.loadFirstFragment();
                } else {
                    splashViewModel.redirectToPlaystore(getActivity());
                }

            }
        });

        forceUpdateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        forceUpdateDialog.show();
    }


    public void loadLoginForNow() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new UISwitchEvent(UISwitchEvent.EventType.LoginFragmentLoad));
            }
        }, 2000);
    }

    @Override
    protected void bindViewModel() {
        splashViewModel.getForceUpdatePopupData()
                .takeUntil(stopEvent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bundle>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bundle popupData) {
                        showForceUpdatePopup(popupData.getString("messageToShow"), popupData.getInt("forceFlag"), popupData.getBoolean("flag"));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        splashViewModel.getConfigError()
                .takeUntil(stopEvent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String message) {
                        if (getView() != null) {
                            Snackbar snackbar = Snackbar
                                    .make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            handleAppLaunch();
                                        }
                                    });
                            snackbar.show();
                        }
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
}
