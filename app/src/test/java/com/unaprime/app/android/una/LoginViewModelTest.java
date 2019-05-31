package com.unaprime.app.android.una;

import android.content.ContentProvider;
import android.content.Context;

import com.unaprime.app.android.una.providers.AppContentProvider;
import com.unaprime.app.android.una.validator.LoginValidator;
import com.unaprime.app.android.una.viewmodels.LoginViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class)
public class LoginViewModelTest {
    private LoginViewModel viewModel;
    private TestSubscriber<String> testSubscriber = TestSubscriber.create();

    @Mock
    AppContentProvider contentProvider;
    @Mock
    Context context;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        viewModel = new LoginViewModel(contentProvider, new LoginValidator(context));
    }

    @Test
    public void mobileNumberError_invalidMobile_showError() {
        when(context.getString(R.string.login_mobile_number_error)).thenReturn("Please enter valid mobile number");
        viewModel.mobileNumberChange("12345");
        assertEquals("Please enter valid mobile number", viewModel.mobileNumberError.get());
    }
}
