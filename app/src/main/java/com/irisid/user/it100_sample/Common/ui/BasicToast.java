package com.irisid.user.it100_sample.Common.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.irisid.user.it100_sample_project.R;

public class BasicToast extends Toast {

    Context _context;
    private static final int SHORT_TOAST_DURATION = 2000;
    public BasicToast(@NonNull Context context) {
        super(context);
        _context = context;
    }

    public Toast makeText(String msg) {

        Toast toast = Toast.makeText(_context, msg, Toast.LENGTH_LONG );
        View view = toast.getView();
        view.getBackground().setColorFilter(
                _context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(Color.WHITE);
        return toast;
    }

    public Toast makeLongText(String msg, long durationInMillis) {

        final Toast t = Toast.makeText(_context, msg, Toast.LENGTH_LONG);
        View view = t.getView();
        view.getBackground().setColorFilter(
                _context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        new CountDownTimer(Math.max(durationInMillis - SHORT_TOAST_DURATION, 1000), 1000) {
            @Override
            public void onFinish() {
                t.show();  //t.cancel();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                t.show();
            }
        }.start();
        return t;
    }

}
