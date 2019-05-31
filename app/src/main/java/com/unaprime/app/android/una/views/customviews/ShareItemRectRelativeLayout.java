package com.unaprime.app.android.una.views.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by aadityak on 17-Nov-16.
 */

public class ShareItemRectRelativeLayout extends RelativeLayout {
    public ShareItemRectRelativeLayout(Context context) {
        super(context);
    }

    public ShareItemRectRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareItemRectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int targetWidth = MeasureSpec.getSize(widthMeasureSpec); //Get the width that Android calculated
        int targetHeight = MeasureSpec.getSize(widthMeasureSpec)*11/10;
        super.onMeasure(MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(targetHeight, MeasureSpec.EXACTLY)); //Make the width also occupy the same width that it would be, no matter what
    }
}
