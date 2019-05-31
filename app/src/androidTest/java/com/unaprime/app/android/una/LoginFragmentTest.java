package com.unaprime.app.android.una;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.unaprime.app.android.una.views.fragments.LoginFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    LoginFragment loginFragment;
    MainActivity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        loginFragment = LoginFragment.newInstance();
        mainActivity = activityTestRule.getActivity();
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, loginFragment).commitAllowingStateLoss();
    }


    @Test
    public void mobileNumberError() {
        edtMobile().perform(typeText("12345"), closeSoftKeyboard());
        checkMobileError(mainActivity.getString(R.string.login_mobile_number_error));

        onView(allOf(withId(R.id.tietPassword), isDisplayed())).perform(typeText("12"), closeSoftKeyboard());
        onView(allOf(withId(R.id.tvPasswordError), isDisplayed())).check(matches(isDisplayed()));
    }

    @Test
    public void mobileNumberValidationSuccess() {
        edtMobile().perform(typeText("1234567890"), closeSoftKeyboard());
        onView(allOf(withId(R.id.tvMobileNumberError), isDisplayed())).check(matches(withText("")));
    }

    @Test
    public void passwordError() {
        onView(allOf(withId(R.id.tietPassword), isDisplayed())).perform(typeText("1234567"), closeSoftKeyboard());
        onView(allOf(withId(R.id.tvPasswordError), isDisplayed())).check(matches(withText("")));
    }

    private void checkMobileError(String message) {
        onView(allOf(withId(R.id.tvMobileNumberError), withText(message), isDisplayed())).check(matches(withText(message)));
    }

    private ViewInteraction edtMobile() {
        return onView(allOf(withId(R.id.tietMobileNumber), isDisplayed()));
    }

    @After
    public void destroy() {
        activityTestRule.getActivity().finish();
    }

}
