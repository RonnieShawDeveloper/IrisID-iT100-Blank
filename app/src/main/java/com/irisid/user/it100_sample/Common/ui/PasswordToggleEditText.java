package com.irisid.user.it100_sample.Common.ui;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.irisid.user.it100_sample_project.R;

public class PasswordToggleEditText extends AppCompatEditText
{

    public PasswordToggleEditText(Context context)
    {
        super(context);
        init();
    }

    public PasswordToggleEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PasswordToggleEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface Callback
    {
        void beforeClear(EditText editText);
        void afterClear(EditText editText);
    }

    private Callback callback;
    private int showMode = 1;
    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    private void init()
    {
        if (isInEditMode()) return;
        updateToggleIcon(isFocused());

        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isToggleIconTouched(event)) {
                    if (callback != null) callback.beforeClear(PasswordToggleEditText.this);

                    if(showMode == 0 ){
                        showMode = 1;
                    }else
                        showMode = 0;

                    updateToggleIcon(true);
                    if (callback != null) callback.afterClear(PasswordToggleEditText.this);
                    return true;
                }
            }
            return false;
            }
        });
    }

    public void updateToggleIcon(boolean focused)
    {
        updateToggleIcon(null, focused);
    }

    public void updateToggleIcon(final String text, final boolean focused)
    {
        final String currentText = (text != null) ? text : getText().toString();
        post(new Runnable()
        {
            @Override
            public void run()
            {
                if (TextUtils.isEmpty(currentText) || !focused)
                {
                    setCompoundDrawables(null, null, null, null);
                }
                else
                {
                    if(showMode==0) {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_hide, 0);
                        setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    else {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_show, 0);
                        setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            }
        });
    }

    private boolean isToggleIconTouched(MotionEvent event){
        final int touchPointX = (int) event.getX();

        final int widthOfView = getWidth();
        final int compoundPadding =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                        ? getCompoundPaddingEnd()
                        : getCompoundPaddingRight();

        return touchPointX >= widthOfView - compoundPadding;
    }
}
