package com.unaprime.app.android.una.views.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.unaprime.app.android.una.utils.AppUtils;

/**
 * Created by aaditya kumar on 15/05/2019.
 */

public class CustomTextView extends AppCompatTextView {
    public static final String sAttribute = "typeFaceType";
    public static final String sScheme = "http://schemas.android.com/apk/res-auto";

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final String typeFaceType = attrs.getAttributeValue(sScheme, sAttribute);
        applyFont(typeFaceType);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final String typeFaceType = attrs.getAttributeValue(sScheme, sAttribute);
        applyFont(typeFaceType);
    }

    private void applyFont(String type) {
        if (!AppUtils.isValidString(type)) {
            type = "thin";
        }

        if (type.equalsIgnoreCase("thin")) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/MuseoSans-100_0.otf");
            setTypeface(tf);
        } else if (type.equalsIgnoreCase("medium")) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/MuseoSans-300_0.otf");
            setTypeface(tf);
        } else if (type.equalsIgnoreCase("thick")) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/museosans-500.otf");
            setTypeface(tf);
        }
    }
}
