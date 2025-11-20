package com.irisid.user.it100_sample.Activity;

import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_DEVICE_ACTIVATION;
import static com.irisid.user.it100_sample.IrisApplication.restartApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.AllUserCallback;
import com.irisid.it100.callback.ChangeAccountCallback;
import com.irisid.it100.callback.UserInfoCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.UserInfo;
import com.irisid.it100.data.UserSimpleInfo;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;


import java.util.ArrayList;

public class InitPasswordActivity extends Activity {

    EditText editPw;
    EditText editPwConfirm;
    TextView txtConfirm;
    LinearLayout linearConfirm;

    SharedPreferences pref1;
    SharedPreferences.Editor editor;
    Context  context;

    String admin_userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_password);

        context = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_SERVICE_DEVICE_ACTIVATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
		
        pref1 = getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref1.edit();

        editPw = findViewById(R.id.pw_edt);
        editPwConfirm = findViewById(R.id.pw_confirm_edt);

        txtConfirm = findViewById(R.id.txt_confirm);
        linearConfirm =findViewById(R.id.linear_confirm);
        linearConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sEditPw = editPw.getText().toString();

                if(sEditPw.equals(editPwConfirm.getText().toString())){

                    if(!sEditPw.equals("")) {
                        if(sEditPw.length()<4){
                            new BasicToast(getApplicationContext()).
                                    makeText(getResources().getString(R.string.err_pw_4digit)).show();
                            return;
                        }

                        IT100.getUserList("", 0, 100, new AllUserCallback() {
                            @Override
                            public void allUserResult(ArrayList<UserSimpleInfo> arrayList) {

                                for(int i= 0; i<arrayList.size(); i++){
                                    if(arrayList.get(i).role.equals(MessageKeyValue.USER_ROLE_ADMINISTRATOR)){
                                        admin_userid = arrayList.get(i).userID;
                                        IT100.getUserInfo(admin_userid, new UserInfoCallback() {
                                            @Override
                                            public void userInfoResult(int resultCode, UserInfo userInfo) {
                                                IT100.changeAccount(admin_userid, getString(R.string.init_admin_pw), editPw.getText().toString(),
                                                        userInfo.adminID, userInfo.adminID, new ChangeAccountCallback() {
                                                        //IT100.changeAccount(arrayList.get(i).userID, , editPw.getText().toString(),
                                                        //        getString(R.string.init_admin_id), getString(R.string.init_admin_id), new ChangeAccountCallback() {
                                                        @Override
                                                        public void changeAccountResult(int i) {
                                                            editor.putBoolean(getString(R.string.prefer_key_init_pw), true);
                                                            editor.commit();

                                                            IrisApplication.restartApp(getApplicationContext());
                                                            finish();
                                                        }
                                                    });
                                            }
                                        });

                                    }else{
                                        if(i==arrayList.size()-1){
                                            editor.putBoolean(getString(R.string.prefer_key_init_pw), true);
                                            editor.commit();

                                            IrisApplication.restartApp(getApplicationContext());
                                            finish();
                                        }else
                                            continue;
                                    }

                                }
                            }

                        });

                    }else{
                        new BasicToast(getApplicationContext()).
                                makeText(getResources().getString(R.string.err_pw_4digit)).show();

//                        editor.putBoolean(getString(R.string.prefer_key_init_pw), true);
//                        editor.commit();
//                        Intent intent;
//                        if(IrisApplication.isIDIS)
//                            intent = new Intent(getBaseContext(), IDISMainActivity.class);
//                        else
//                            intent = new Intent(getBaseContext(), MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//                        startActivity(intent);
//                        finish();
                    }

                }else{
                    new BasicToast(getApplicationContext()).
                            makeText(getResources().getString(R.string.err_pw_matched)).show();
                }
            }
        });

    }

    private LocalBroadcastReceiver mReceiver = new LocalBroadcastReceiver();
    public class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BROADCAST_SERVICE_DEVICE_ACTIVATION)){
                restartApp(context);
                finish();
            }

        }
    }
}
