package com.irisid.user.it100_sample.Common.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.irisid.user.it100_sample_project.R;

public class BasicAlertDialog extends AlertDialog.Builder {

    Context _context;
    int _resId;
    public BasicAlertDialog(@NonNull Context context) {
        super(context);
        _context = context;
    }

    public BasicAlertDialog(@NonNull Context context, int themeResId) {
        super(context,themeResId);
        _context = context;
        _resId = themeResId;

    }

    @Override
    public AlertDialog show() {

        AlertDialog dialog = super.show();
        dialog.setCanceledOnTouchOutside(false);
        TextView textView = (TextView)dialog.findViewById(android.R.id.message);
        if(textView !=null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER_VERTICAL);

            if (_resId == R.style.AppCompatAlertDialogStyle)
                textView.setTextColor(_context.getResources().getColor(R.color.dialogMessage));
            else if (_resId == R.style.AppCompatAlertDialogErrorStyle) {
                dialog.setCanceledOnTouchOutside(true);
                textView.setTextColor(_context.getResources().getColor(R.color.settingBackground));
            }
        }

        return dialog;
    }
}
