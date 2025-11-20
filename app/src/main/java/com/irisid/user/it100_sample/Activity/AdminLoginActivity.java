package com.irisid.user.it100_sample.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.irisid.it100.IT100;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;
import com.irisid.user.it100_sample.UserList.ItemsListActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminLoginActivity extends BaseBackgroundActivity implements View.OnClickListener{

    public static AdminLoginActivity adminLoginActivity;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Button iris_recog_btn;
    Button iris_id_pw_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        setDecorView();

        Intent intent = getIntent();
        String m_State = intent.getStringExtra(ConstData.INTENT_ADMIN_LOGIN_TYPE);

        IrisApplication.activateBackButton(this, R.id.back_linear);
        ((TextView)findViewById(R.id.title)).setText(getResources().getText(R.string.admin_login));
        pref = getSharedPreferences("settings", MODE_PRIVATE);
        editor = pref.edit();
        adminLoginActivity = AdminLoginActivity.this;

        rootLayout = findViewById(R.id.rootview);
        videoView = (VideoView)findViewById(R.id.videoView);

        iris_recog_btn = (Button) findViewById(R.id.iris_recog_btn);
        iris_id_pw_btn = (Button) findViewById(R.id.iris_id_pw_btn);
        iris_recog_btn.setOnClickListener(this);
        iris_id_pw_btn.setOnClickListener(this);

        if(CaptureActivity.self!=null)
            CaptureActivity.self.finish();
        if(m_State!=null && m_State.equals(ConstData.INTENT_ADMIN_ONLY_BIOMETRICS)){
            findViewById(R.id.iris_id_pw_btn).setEnabled(false);
            ((TextView)findViewById(R.id.id_pw_txt)).setTextColor(getResources().getColor(R.color.buttonTextDisableAlpha, null));
        }else{
            findViewById(R.id.iris_id_pw_btn).setEnabled(true);
            ((TextView)findViewById(R.id.id_pw_txt)).setTextColor(getResources().getColor(R.color.white, null));
        }
    }

    boolean isResume= false;
    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        setAdminMode(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iris_recog_btn:
                if(isResume) {
                    Intent intent_recog = new Intent(this, CaptureActivity.class);
                    intent_recog.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_SYSTEM_ADMIN);
                    intent_recog.putExtra(ConstData.RECOG_TYPE, ConstData.RECOG_TYPE_SYSTEM_ADMIN);
                    startActivityForResult(intent_recog, IrisApplication.CaptureActivityValue);
                }
                break;

            case R.id.iris_id_pw_btn:
                Intent intent1 = new Intent(AdminLoginActivity.this, IdPwActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.fade,R.anim.hold);
                //finish();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IrisApplication.CaptureActivityValue) {

            if(resultCode == 3001){
                IT100.abortCapture();
            }

            if(ItemsListActivity.roleInDevice ==null)
                return;

            if(ItemsListActivity.roleInDevice.equals("")){
                new BasicToast(this).
                        makeText(getString(R.string.toast_admin_login_error)).show();

            }else {

                if (ItemsListActivity.roleInDevice.equals(MessageKeyValue.USER_ROLE_SUPER_ADMINISTRATOR)
                        || ItemsListActivity.roleInDevice.equals(MessageKeyValue.USER_ROLE_ADMINISTRATOR)
                        || ItemsListActivity.roleInDevice.equals(MessageKeyValue.USER_ROLE_MANAGER)) {
                    showLoading("");
                    _ownerHandler.postDelayed(_loading, 1500);

                    editor.putInt(getResources().getString(R.string.prefer_key_login_error_count), 0);
                    editor.commit();
                } else {
                    new BasicToast(this).
                            makeText(getString(R.string.toast_admin_login_error)).show();
                }
            }
        }
    }

    protected Handler _ownerHandler=new Handler();
    Runnable _loading = new Runnable() {
        @Override
        public void run() {
            hideLoading();

            Intent intent = new Intent(AdminLoginActivity.this, SystemAdminActivity.class);
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
