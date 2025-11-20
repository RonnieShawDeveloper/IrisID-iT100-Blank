package com.irisid.user.it100_sample.Common.ui;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.irisid.user.it100_sample_project.R;

public class ClearableEditText extends AppCompatEditText
{

    public ClearableEditText(Context context)
    {
        super(context);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr)
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

    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    private void init()
    {
        if (isInEditMode()) return;
        updateDeleteIcon(isFocused());

        addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                updateDeleteIcon(s.toString(), isFocused());
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP){
                    if(isClearIconTouched(event)){
                        setText("");
                        requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void updateDeleteIcon(boolean focused)
    {
        updateDeleteIcon(null, focused);
    }

    private void updateDeleteIcon(final String text, final boolean focused)
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
                   setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_input_clear, 0);
                }
            }
        });
    }

    private boolean isClearIconTouched(MotionEvent event){
        final int touchPointX = (int) event.getX();

        final int widthOfView = getWidth();
        final int compoundPadding =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                        ? getCompoundPaddingEnd()
                        : getCompoundPaddingRight();

        return touchPointX >= widthOfView - compoundPadding;
    }
}
