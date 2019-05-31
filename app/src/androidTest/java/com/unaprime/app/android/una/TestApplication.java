package com.unaprime.app.android.una;

import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.AppCompatActivity;

public class TestApplication extends App {

    public static <T extends AppCompatActivity> TestApplication get(ActivityTestRule<T> activityTestRule) {
        return (TestApplication) activityTestRule.getActivity().getApplication();
    }
}
