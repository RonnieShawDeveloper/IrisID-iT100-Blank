package com.irisid.user.it100_sample.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.VideoView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.AdminLoginCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.ui.ClearableEditText;
import com.irisid.user.it100_sample.Common.ui.PasswordToggleEditText;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;


public class IdPwActivity extends BaseBackgroundActivity implements View.OnClickListener
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ImageView left_hide_img;
    ImageView right_hide_img;

    ClearableEditText id_edt;
    PasswordToggleEditText pw_edt;

    Button sign_in_btn;
    ScrollView scrollview;

    String get_id_pw_update;
    int paddingPixel=0;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_id_pw);

        changeStatusBarColor("#01000000");
        setDecorView();

        IrisApplication.activateBackButton(this, R.id.back_linear);

        pref = getSharedPreferences("settings", MODE_PRIVATE);
        editor = pref.edit();

        left_hide_img = (ImageView) findViewById(R.id.left_hide_img);
        right_hide_img = (ImageView) findViewById(R.id.right_hide_img);
        left_hide_img.setOnClickListener(this);
        right_hide_img.setOnClickListener(this);

        rootLayout = findViewById(R.id.rootview);
        videoView = (VideoView)findViewById(R.id.videoView);
        scrollview = (ScrollView) findViewById(R.id.scrollview);

        id_edt = (ClearableEditText) findViewById(R.id.id_edt);
        pw_edt = (PasswordToggleEditText) findViewById(R.id.pw_edt);

        sign_in_btn = (Button) findViewById(R.id.sign_in_btn);
        sign_in_btn.setEnabled(false);
        sign_in_btn.setTextColor(getResources().getColor(R.color.signinTextDisable, null));

        Intent intent1 = getIntent();
        get_id_pw_update = intent1.getStringExtra("id_pw_update");

        if(get_id_pw_update == null)
            sign_in_btn.setText(R.string.sign_in);
        else if(get_id_pw_update.equals("id_pw_update"))
            sign_in_btn.setText(R.string.update);
        else
            sign_in_btn.setText(R.string.sign_in);

        sign_in_btn.setOnClickListener(this);

        pw_edt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findViewById(R.id.linear_error).setVisibility(View.INVISIBLE);
                pw_edt.setSelection(pw_edt.getText().length());
                pw_edt.updateToggleIcon(s.toString(), true);
                pw_edt.getBackground().mutate().setColorFilter(null);
                id_edt.setCompoundDrawables(null, null, null, null);

                sign_in_btn.setEnabled(true);
                sign_in_btn.setTextColor(getResources().getColor(R.color.white, null));
                sign_in_btn.setGravity(Gravity.CENTER);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        int paddingDp = 64;
        float density = getResources().getDisplayMetrics().density;
        paddingPixel = (int)(paddingDp * density);

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                Rect r = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(r);
                int heightDiff = rootLayout.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    //Logger.e("TAG >>", "KEYBOARD UP " + scrollview.getY());
                    findViewById(R.id.linear_cover)
                            .setPadding(0,0,0, 0);

                } else {
                    setDecorView();
                    //ok now we know the keyboard is down...
                    //Logger.e("TAG >>", "KEYBOARD DOWN " + scrollview.getY());
                    findViewById(R.id.linear_cover)
                            .setPadding(0,0,0, paddingPixel);
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        setAdminMode(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.sign_in_btn:

                if(get_id_pw_update == null) {
                    sign_in();
                } else if(get_id_pw_update.equals("id_pw_update")) { // update
                    editor.putString("id", id_edt.getText().toString());
                    editor.putString("pw", pw_edt.getText().toString());
                    editor.commit();

                    finish();

                    overridePendingTransition(R.anim.fade,R.anim.load_fade_out);
                } else {
                    sign_in();
                }
                break;

            case R.id.left_hide_img:
                InputMethodManager imm;
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(left_hide_img.getWindowToken(), 0);
                break;

            case R.id.right_hide_img:
                InputMethodManager imm2;
                imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(right_hide_img.getWindowToken(), 0);
                break;

            default:
                break;
        }
    }

    public void sign_in() {

        final int errCount = pref.getInt(getResources().getString(R.string.prefer_key_login_error_count), 0);
        long errTime = pref.getLong(getResources().getString(R.string.prefer_key_login_error_time), 0);
        if(errCount>=5){

            //if(System.currentTimeMillis()-errTime < 600000){
            long diffTime = System.currentTimeMillis()-errTime;
            if(diffTime<0) {
                editor.putLong(getResources().getString(R.string.prefer_key_login_error_time), System.currentTimeMillis());
                editor.commit();
                return;
            }else if(diffTime>0 && diffTime<600000){
                String errMsg = String.format(getResources().getString(R.string.error_login_attempt),
                        (599-diffTime/1000)/60+1);
                new BasicAlertDialog(this)
                        .setTitle(getString(R.string.dlgtitle_notice))
                        .setMessage(errMsg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })
                        .show();
                return;
            }else{
                editor.putInt(getResources().getString(R.string.prefer_key_login_error_count), 0);
            }
        }
        editor.commit();
        long rtnAdmin = 0 ;
        rtnAdmin = IT100.adminLogin(id_edt.getText().toString(), pw_edt.getText().toString(), new AdminLoginCallback() {
            @Override
            public void adminLoginResult(int resultCode) {
                runOnUiThread(() -> {
                    if(resultCode == MessageType.ID_RTN_SUCCESS) {
                        showLoading("");
                        _ownerHandler.postDelayed(_loading, 1500);
                        editor.putInt(getResources().getString(R.string.prefer_key_login_error_count), 0);
                    }
                    else {
                        editor.putInt(getResources().getString(R.string.prefer_key_login_error_count), errCount+1);
                        if(errCount+1==5)
                            editor.putLong(getResources().getString(R.string.prefer_key_login_error_time), System.currentTimeMillis());
                        findViewById(R.id.linear_error).setVisibility(View.VISIBLE);
                        pw_edt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                    }
                    editor.commit();
                });
            }
        });

        if ( rtnAdmin  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnAdmin  == MessageType.ID_RTN_WRONG_PARA){}// fail
        else if( rtnAdmin  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnAdmin  == MessageType.ID_RTN_FAIL){}// fail
    }

    protected Handler _ownerHandler=new Handler();
    Runnable _loading = new Runnable() {
        @Override
        public void run() {

            if(getBaseContext()!=null)
                hideLoading();

            Intent intent = new Intent(IdPwActivity.this, SystemAdminActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold); // enter
        }
    };

    private void setAdminMode(boolean enable){
        JSONObject jparam = new JSONObject();
        try {
            jparam.put(MessageKeyValue.ADMIN_MODE_ENALBE, enable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setAdminMode(jparam.toString());
    }
}