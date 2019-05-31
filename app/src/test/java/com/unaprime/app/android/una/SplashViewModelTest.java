package com.unaprime.app.android.una;

import android.os.Bundle;

import com.unaprime.app.android.una.providers.AppContentProvider;
import com.unaprime.app.android.una.services.WebserviceConstants;
import com.unaprime.app.android.una.services.responses.ConfigResponseData;
import com.unaprime.app.android.una.viewmodels.SplashViewModel;
import com.unaprime.app.android.una.views.fragments.BaseFragment;
import com.unaprime.app.android.una.views.fragments.HomepageFragment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplashViewModelTest {

    private SplashViewModel splashViewModel;

    @Mock
    AppContentProvider contentProvider;
    @Mock
    AppSession appSession;
    @Mock
    ConfigResponseData configResponseData;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Bundle bundle = new Bundle();
        bundle.putString("dt", "0");
        splashViewModel = new SplashViewModel(contentProvider, bundle, appSession);
    }

    @Test
    public void deepLinkingTestNull() {
        splashViewModel.fragmentToHandleRequestedAction();
        assertNull(appSession.getDeepLinkedFragmentInstance());
    }

    @Test
    public void fragmentAgainstDestinationTestNull() {
        Bundle bundle = new Bundle();
        bundle.putString("dt", "0");
        BaseFragment fragment = splashViewModel.fragmentToHandleDestination(bundle);
        assertNull(fragment);

    }

    @Test
    public void showForceUpdatePopupCalledTest() {
        when(configResponseData.getAppUpdateStatus()).thenReturn(1);
        when(configResponseData.getAppUpdateMessage()).thenReturn("Please update your app to latest version of UNA app to proceed");
        SplashViewModel tempViewModel = Mockito.spy(splashViewModel);
        tempViewModel.onDataFetched(configResponseData, WebserviceConstants.APICallbackIdentifiers.FetchConfig);
        verify(tempViewModel, times(1)).showForceUpdatePopup("Please update your app to latest version of UNA app to proceed", 1, true);
    }

    @Test
    public void showMaintenancePopupCalledTest() {
        when(configResponseData.getAppUpdateStatus()).thenReturn(0);
        when(configResponseData.getAppMaintenanceStatus()).thenReturn(1);
        when(configResponseData.getAppMaintenanceMessage()).thenReturn("Due to some technical issue, App is not available for use, currently");
        SplashViewModel tempViewModel = Mockito.spy(splashViewModel);
        tempViewModel.onDataFetched(configResponseData, WebserviceConstants.APICallbackIdentifiers.FetchConfig);
        verify(tempViewModel, times(1)).showForceUpdatePopup("Due to some technical issue, App is not available for use, currently", 1, false);
    }
}
