package com.unaprime.app.android.una;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.unaprime.app.android.una.views.fragments.SplashFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class SplashFragmentTest {

    SplashFragment splashFragment;
    MainActivity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Bundle bundle = new Bundle();
        bundle.putString("dt", "0");
        splashFragment = SplashFragment.newInstance(bundle);
        mainActivity = activityTestRule.getActivity();
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, splashFragment).commitAllowingStateLoss();
    }

    @Test
    public void logoVisibleTest() {
        onView(allOf(withId(R.id.ivAppLogo), isDisplayed())).check(matches(isDisplayed()));
    }

    @Test
    public void showForceUpdatePopUpWithQuit() {
        onView(allOf(withText("Please update your app to latest version of UNA app to proceed"), isDisplayed())).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.tvLater), isDisplayed())).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.tvLater), withText("Quit"), isDisplayed())).inRoot(isDialog()).check(matches(isDisplayed()));
    }

}
