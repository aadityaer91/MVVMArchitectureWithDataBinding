package com.unaprime.app.android.una.views.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;

import com.unaprime.app.android.una.R;
import com.unaprime.app.android.una.logger.AppLogger;

public class SpinkitProgressDialog extends AppCompatDialogFragment {
    private final String TAG = SpinkitProgressDialog.class.getSimpleName();
    public boolean started = false;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        //super.setupDialog(dialog, style);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_spinkit_progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppLogger.log("Dialog resumed");
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    AppLogger.log("Caught key code:" + keyCode + ", keyEvent:" + event);
                    return true;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        started = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        started = false;
    }
}
