package com.irisid.user.it100_sample.Settings;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.AllUserCallback;
import com.irisid.it100.callback.CountermeasureCallback;
import com.irisid.it100.callback.DeviceRecogModeCallback;
import com.irisid.it100.callback.MaskSettingCallback;
import com.irisid.it100.callback.OperationModeCallback;
import com.irisid.it100.callback.SaveAuditFaceImageCallback;
import com.irisid.it100.callback.SavingModeEnableCallback;
import com.irisid.it100.callback.SavingModeStatusCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.it100.data.UserSimpleInfo;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ModesFragment extends Fragment implements View.OnClickListener {

    RadioGroup mode_radio_group;
    AppCompatRadioButton mode_continuous;
    AppCompatRadioButton mode_interactive;

    RadioGroup admin_radio_group;
    AppCompatRadioButton admin_all;
    AppCompatRadioButton admin_bio;
    AppCompatRadioButton admin_idpw;

    LinearLayout rootLayout;
    LinearLayout _linearMask;
    LinearLayout _linearMaskDetail;
    LinearLayout _linearMaskVoiceGuide;
    LinearLayout _linearMaskAccessControl;
    TextView _txtMaskOnOff;
    Switch _schMask;
    AppCompatCheckBox _ckMaskAccess;
    AppCompatCheckBox _ckMaskTextGuide;
    AppCompatCheckBox _ckMaskVoiceGuide;
    AppCompatCheckBox _ckCounterFace;
    AppCompatCheckBox _ckCounterEye;

    RadioGroup recogRadioGroup;
    AppCompatRadioButton recogModeUser;
    AppCompatRadioButton recogModeDevice;

    Button btn_auth_mode;
    Button btn_card_mode;
    CheckBox checkbox_iris;
    CheckBox checkbox_face;
    CheckBox checkbox_card;

    AppCompatCheckBox ckAuditEnable;
    AppCompatSpinner auditSpinner;
    LinearLayout linearAuditSpinner;
    TextView txtAuditOnOff;
    Switch schAudit;
    ArrayAdapter auditTypeAdapter;

    LinearLayout linearSaving;
    LinearLayout linearSavingDetail;
    LinearLayout linearScreenTimeout;
    TextView txtSavingOnOff;
    TextView txtScreenTimeout;
    Switch schSavingOnOff;
	
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String value_recog_mode;
    String value_auth_mode;
    String value_device_recog_mode;
    String value_save_audit;
    String value_admin_auth;

    int _maskMode;
    int _maskAccess;
    int _maskTextGuide;
    int _maskVoiceGuide;
    int _savingMode;
    int _savingScreenTimeout;
    int _cmFace;
    int _cmEye;

    final int TIMEOUT_MIN_VALUE = 1;
    final int TIMEOUT_MAX_VALUE = 10; //1800;

    String[] audit_list = {
            MessageKeyValue.AUDIT_SAVE_IMAGE_ALL,
            MessageKeyValue.AUDIT_SAVE_IMAGE_SUCCESS,
            MessageKeyValue.AUDIT_SAVE_IMAGE_FAILED,
            MessageKeyValue.AUDIT_SAVE_IMAGE_UNAUTHORIZED,
            MessageKeyValue.AUDIT_SAVE_IMAGE_EXCEPT_CARD_ONLY
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_modes, container, false);
        rootLayout = (LinearLayout)rootView.findViewById(R.id.rootview);

        initView(rootView);

        setInitValue();

        setViewListener();

        registerCallbackListener();

        return rootView;
    }

    private void setInitValue(){
        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref.edit();
        value_recog_mode = pref.getString(getString(R.string.prefer_key_recog_mode), ConstData.RECOG_MODE_INTERACTIVE);
		value_admin_auth = pref.getString(getString(R.string.prefer_key_admin_login_type), ConstData.ADMIN_AUTH_ID_PW);
        value_auth_mode = pref.getString(getString(R.string.prefer_key_auth_mode), ConstData.AUTH_MODE_POLICY_INDIVIDUAL);
        value_device_recog_mode = pref.getString(getString(R.string.prefer_key_device_auth_mode), MessageKeyValue.AUTHMODE_FACE_OR_IRIS);
        _maskMode = pref.getInt(getString(R.string.prefer_key_mask_mode), 0);
        _maskAccess = pref.getInt(getString(R.string.prefer_key_mask_access), 1);
        _maskTextGuide = pref.getInt(getString(R.string.prefer_key_mask_text_guide), 1);
        _maskVoiceGuide = pref.getInt(getString(R.string.prefer_key_mask_voice_guide), 1);
        _cmEye = pref.getInt(getString(R.string.prefer_key_counter_iris), 0);
        _cmFace = pref.getInt(getString(R.string.prefer_key_counter_face), 0);
        value_save_audit = pref.getString(getString(R.string.prefer_key_save_audit_type),
                MessageKeyValue.AUDIT_SAVE_IMAGE_ALL);
        _savingMode = pref.getInt(getString(R.string.prefer_key_saving_mode), 0);
        _savingScreenTimeout = pref.getInt(getString(R.string.prefer_key_screen_time_out), 2);

        if(value_recog_mode == null || value_recog_mode.equals(ConstData.RECOG_MODE_INTERACTIVE)){
            mode_radio_group.check(R.id.mode_interactive);
        }else{
            mode_radio_group.check(R.id.mode_continuous);
        }

        if(value_admin_auth == null || value_admin_auth.equals(ConstData.ADMIN_AUTH_ID_PW)){
            admin_radio_group.check(R.id.admin_mode_idpw);
        }else if(value_admin_auth.equals(ConstData.ADMIN_AUTH_ALL)){
            admin_radio_group.check(R.id.admin_mode_all);
        }else if(value_admin_auth.equals(ConstData.ADMIN_AUTH_BIO)){
            admin_radio_group.check(R.id.admin_mode_bio);
        }else
            admin_radio_group.check(R.id.admin_mode_idpw);

        if(_maskMode == 1) {
            Logger.d("init set mask on");
            _schMask.setChecked(true);
            maskView(true);
        }else {
            Logger.d("init set mask off");
            _linearMaskDetail.setVisibility(View.GONE);
            _schMask.setChecked(false);
            maskView(false);
        }

        if(_maskAccess == 1)
            _ckMaskAccess.setChecked(true);
        else
            _ckMaskAccess.setChecked(false);

        if(_maskTextGuide == 1)
            _ckMaskTextGuide.setChecked(true);
        else
            _ckMaskTextGuide.setChecked(false);

        if(_maskVoiceGuide == 1)
            _ckMaskVoiceGuide.setChecked(true);
        else
            _ckMaskVoiceGuide.setChecked(false);
        if(_cmEye == 1)
            _ckCounterEye.setChecked(true);
        else
            _ckCounterEye.setChecked(false);

        if(_cmFace == 1)
            _ckCounterFace.setChecked(true);
        else
            _ckCounterFace.setChecked(false);
        if(value_save_audit.equals(MessageKeyValue.AUDIT_SAVE_IMAGE_NONE)){
            schAudit.setChecked(false);
            auditView(false);
        }else{
            schAudit.setChecked(true);
            auditView(true);
        }

        if(_savingMode == 1) {
            schSavingOnOff.setChecked(true);
            savingView(true);
        }else {
            schSavingOnOff.setChecked(false);
            savingView(false);
        }
        txtScreenTimeout.setText(String.valueOf(_savingScreenTimeout));
    }

    private void setViewListener(){

        mode_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mode_continuous:

                        IT100.setOperationMode(MessageType.CONTINUOUS_MODE);
                        editor.putString(getString(R.string.prefer_key_recog_mode), ConstData.RECOG_MODE_CONTINUOUS);
                        editor.commit();
                        break;

                    case R.id.mode_interactive:

                        IT100.setOperationMode(MessageType.INTERACTIVE_MODE);
                        editor.putString(getString(R.string.prefer_key_recog_mode), ConstData.RECOG_MODE_INTERACTIVE);
                        editor.commit();
                        break;
                }
            }
        });

        admin_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.admin_mode_all:
                        editor.putString(getString(R.string.prefer_key_admin_login_type), ConstData.ADMIN_AUTH_ALL);
                        editor.commit();
                        IrisApplication.value_auth = ConstData.ADMIN_AUTH_ALL;
                        break;

                    case R.id.admin_mode_bio:
                        editor.putString(getString(R.string.prefer_key_admin_login_type), ConstData.ADMIN_AUTH_BIO);
                        editor.commit();
                        IrisApplication.value_auth = ConstData.ADMIN_AUTH_BIO;
                        break;

                    case R.id.admin_mode_idpw:
                        editor.putString(getString(R.string.prefer_key_admin_login_type), ConstData.ADMIN_AUTH_ID_PW);
                        editor.commit();
                        IrisApplication.value_auth = ConstData.ADMIN_AUTH_ID_PW;
                        break;
                }
            }
        });
		
        _linearMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_schMask.isChecked()){
                    if(_linearMaskDetail.getVisibility()==View.GONE)
                        _linearMaskDetail.setVisibility(View.VISIBLE);
                    else if(_linearMaskDetail.getVisibility()==View.VISIBLE)
                        _linearMaskDetail.setVisibility(View.GONE);
                }
            }
        });

        _schMask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.d("@schMask onCheckedChanged " + isChecked );

                if(!isChecked) {
                    maskView(false);
                    _linearMaskDetail.setVisibility(View.GONE);
                }else{
                    maskView(true);
                }
            }
        });

        _linearMaskAccessControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ckMaskAccess.setChecked(!_ckMaskAccess.isChecked());
            }
        });

        _ckMaskAccess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    editor.putInt(getString(R.string.prefer_key_mask_access), 1);
                else
                    editor.putInt(getString(R.string.prefer_key_mask_access), 0);
                editor.commit();
            }
        });
        _linearMaskVoiceGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ckMaskVoiceGuide.setChecked(!_ckMaskVoiceGuide.isChecked());
            }
        });

        _ckMaskVoiceGuide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    editor.putInt(getString(R.string.prefer_key_mask_voice_guide), 1);
                else
                    editor.putInt(getString(R.string.prefer_key_mask_voice_guide), 0);
                editor.commit();
            }
        });
        _ckCounterEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCountermeasure();
            }
        });

        _ckCounterFace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCountermeasure();
            }
        });

        recogRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.recog_mode_user:
                        checkbox_iris.setEnabled(false);
                        checkbox_face.setEnabled(false);
                        checkbox_card.setEnabled(false);
                        btn_auth_mode.setEnabled(false);
                        btn_card_mode.setEnabled(false);
                        break;

                    case R.id.recog_mode_device:
                        checkbox_iris.setEnabled(true);
                        checkbox_face.setEnabled(true);
                        checkbox_card.setEnabled(true);
                        btn_auth_mode.setEnabled(true);
                        btn_card_mode.setEnabled(true);
                        setAuthMode(true,MessageKeyValue.AUTHMODE_FACE_OR_IRIS);

                        break;
                }
            }
        });

        checkbox_iris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    btn_auth_mode.setEnabled(false);
                    if(!checkbox_face.isChecked() && !checkbox_iris.isChecked())
                        btn_card_mode.setEnabled(false);
                }else{
                    if(checkbox_face.isChecked())
                        btn_auth_mode.setEnabled(true);

                    if((checkbox_face.isChecked() || checkbox_iris.isChecked())&& checkbox_card.isChecked())
                        btn_card_mode.setEnabled(true);
                }
            }
        });

        checkbox_face.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    btn_auth_mode.setEnabled(false);
                    if (!checkbox_face.isChecked() && !checkbox_iris.isChecked())
                        btn_card_mode.setEnabled(false);
                }else{
                    if(checkbox_iris.isChecked())
                        btn_auth_mode.setEnabled(true);
                    if((checkbox_face.isChecked() || checkbox_iris.isChecked()) && checkbox_card.isChecked() )
                        btn_card_mode.setEnabled(true);
                }
            }
        });

        checkbox_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    btn_card_mode.setEnabled(false);
                else{
                    if(!checkbox_face.isChecked() && !checkbox_iris.isChecked())
                        btn_card_mode.setEnabled(false);
                    else
                        btn_card_mode.setEnabled(true);
                }
            }
        });

        if(value_auth_mode == null || value_auth_mode.equals(ConstData.AUTH_MODE_POLICY_INDIVIDUAL)){
            recogRadioGroup.check(R.id.recog_mode_user);
        }else{
            recogRadioGroup.check(R.id.recog_mode_device);
            setAuthMode(true, value_device_recog_mode);
        }

        schAudit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.d("@schAudit onCheckedChanged " + isChecked );

                if(!isChecked) {
                    auditView(false);
                }else{
                    auditView(true);
                }
            }
        });

        schSavingOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.d("@schSavingOnOff onCheckedChanged " + isChecked );

                if(!isChecked) {
                    savingView(false);
                }else{
                    savingView(true);

                }
            }
        });

        linearScreenTimeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputScreenTimeout();
            }
        });


    }
    private void initView(View rootView) {
        // radio group - Recognition Mode
        mode_radio_group = rootView.findViewById(R.id.radio_mode_group);
        mode_continuous = rootView.findViewById(R.id.mode_continuous);
        mode_interactive = rootView.findViewById(R.id.mode_interactive);
        // radio group - Adnimistrator Authentication Mode
        admin_radio_group = (RadioGroup) rootView.findViewById(R.id.admin_mode_group);
        admin_all = (AppCompatRadioButton) rootView.findViewById(R.id.admin_mode_all);
        admin_bio = (AppCompatRadioButton) rootView.findViewById(R.id.admin_mode_bio);
        admin_idpw = (AppCompatRadioButton) rootView.findViewById(R.id.admin_mode_idpw);

        btn_auth_mode = rootView.findViewById(R.id.btn_auth_mode);
        btn_auth_mode.setOnClickListener(this);
        btn_card_mode = rootView.findViewById(R.id.btn_card_andor_mode);
        btn_card_mode.setOnClickListener(this);
        checkbox_iris = rootView.findViewById(R.id.checkbox_iris);
        checkbox_face = rootView.findViewById(R.id.checkbox_face);
        checkbox_card = rootView.findViewById(R.id.checkbox_card);

        recogRadioGroup = rootView.findViewById(R.id.radio_recog_mode_group);
        recogModeUser = rootView.findViewById(R.id.recog_mode_user);
        recogModeDevice = rootView.findViewById(R.id.recog_mode_device);

        _linearMask = rootView.findViewById(R.id.linear_mask);
        _linearMaskDetail = rootView.findViewById(R.id.linear_mask_detail);
        _txtMaskOnOff = rootView.findViewById(R.id.txt_mask_onoff);
        _schMask = rootView.findViewById(R.id.sch_mask);
        _ckMaskAccess = rootView.findViewById(R.id.ck_mask_access);
        _ckMaskTextGuide = rootView.findViewById(R.id.ck_mask_text_guide);
        _ckMaskVoiceGuide = rootView.findViewById(R.id.ck_mask_voice_guide);
        _ckCounterEye = rootView.findViewById(R.id.ck_cm_eye);
        _ckCounterFace = rootView.findViewById(R.id.ck_cm_face);

        _linearMaskVoiceGuide = rootView.findViewById(R.id.linear_mask_voice_guide);
        _linearMaskAccessControl = rootView.findViewById(R.id.linear_mask_access_control);

        linearAuditSpinner = rootView.findViewById(R.id.linear_audit_spin);
        txtAuditOnOff = rootView.findViewById(R.id.txt_audit_onoff);
        schAudit = rootView.findViewById(R.id.sch_audit);

        auditSpinner = rootView.findViewById(R.id.audit_type_spin);
        auditTypeAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, audit_list);
        auditSpinner.setAdapter(auditTypeAdapter);
        auditSpinner.setSelection(0);
        auditSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView)view).setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                ((TextView)view).setTextSize(20);

                // store time_out value
                editor.putString(getString(R.string.prefer_key_save_audit_type),
                        getAuditType(position));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        linearSaving = rootView.findViewById(R.id.linear_saving);
        linearSavingDetail = rootView.findViewById(R.id.linear_saving_detail);
        linearScreenTimeout = rootView .findViewById(R.id.linear_screen_timeout);
        txtSavingOnOff = rootView.findViewById(R.id.txt_saving_onoff);
        txtScreenTimeout = rootView.findViewById(R.id.txt_screen_timeout);
        schSavingOnOff = rootView.findViewById(R.id.sch_saving);
    }

    private void maskView(boolean isOn){
        if(!isOn){
            _txtMaskOnOff.setText(getResources().getString(R.string.off));
            _txtMaskOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
            _maskMode = 0;
        }else{
            _txtMaskOnOff.setText(getResources().getString(R.string.on));
            _txtMaskOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            _maskMode= 1;
        }
    }


    private void auditView(boolean isOn){
        if(!isOn){
            txtAuditOnOff.setText(getResources().getString(R.string.off));
            txtAuditOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
            linearAuditSpinner.setVisibility(View.INVISIBLE);

        }else{
            txtAuditOnOff.setText(getResources().getString(R.string.on));
            txtAuditOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            linearAuditSpinner.setVisibility(View.VISIBLE);
        }
    }

    private void savingView(boolean isOn){
        if(!isOn){
            linearSavingDetail.setVisibility(View.GONE);
            txtSavingOnOff.setText(getResources().getString(R.string.off));
            txtSavingOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
            _savingMode = 0;

        }else{
            linearSavingDetail.setVisibility(View.VISIBLE);
            txtSavingOnOff.setText(getResources().getString(R.string.on));
            txtSavingOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            _savingMode = 1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LocalBroadcastManager.getInstance(
//                getContext()).unregisterReceiver(mReceiver);

        String lastAuthMode;
        if(recogRadioGroup.getCheckedRadioButtonId()== R.id.recog_mode_device) {
            Logger.d("recogRadioGroup.getCheckedRadioButtonId() is device " );
            lastAuthMode= getAuthMode();
            //IT100.setDeviceRecogMode(lastAuthMode);
            IT100.setDeviceRecogMode(true, lastAuthMode);
            editor.putString(getString(R.string.prefer_key_auth_mode), ConstData.AUTH_MODE_POLICY_DEVICE);
            editor.putString(getString(R.string.prefer_key_device_auth_mode), lastAuthMode);
        }else{
            //lastAuthMode = "off";
            lastAuthMode= getAuthMode();
            //IT100.setDeviceRecogMode(lastAuthMode);
            IT100.setDeviceRecogMode(false, lastAuthMode);
            editor.putString(getString(R.string.prefer_key_auth_mode), ConstData.AUTH_MODE_POLICY_INDIVIDUAL);
            editor.putString(getString(R.string.prefer_key_device_auth_mode), lastAuthMode);
        }

        editor.putInt(getString(R.string.prefer_key_mask_mode), _schMask.isChecked()? 1:0);
        editor.commit();
        setCountermeasure();

        JSONObject jMaskSetting = new JSONObject();
        try {
            jMaskSetting.put(MessageKeyValue.MASK_DETECT_ENABLE, _schMask.isChecked());
            jMaskSetting.put(MessageKeyValue.MASK_ACCESS_CONTROL, _ckMaskAccess.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setMaskSettings(jMaskSetting.toString());

        IT100.setAuditFaceImage(getAuditType(auditSpinner.getSelectedItemPosition()));

        editor.putInt(getString(R.string.prefer_key_saving_mode), schSavingOnOff.isChecked()? 1:0);
        JSONObject jSavingSetting = new JSONObject();
        try {
            jSavingSetting.put(MessageKeyValue.SAVING_MODE, schSavingOnOff.isChecked());
            jSavingSetting.put(MessageKeyValue.SAVING_MODE_SCREEN_TIMEOUT, _savingScreenTimeout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setPowerSavingMode(jSavingSetting.toString(), new SavingModeEnableCallback() {
            @Override
            public void onResult(int resultCode, String resultMsg) {

            }
        });
    }

//    private ModesFragment.LocalBroadcastReceiver mReceiver = new ModesFragment.LocalBroadcastReceiver();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auth_mode:
                String authMode = btn_auth_mode.getText().toString();
                if(authMode.equals(ConstData.AUTH_MODE_FUSION))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                else if(authMode.equals(ConstData.AUTH_MODE_OR))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                else if(authMode.equals(ConstData.AUTH_MODE_AND))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                break;

            case R.id.btn_card_andor_mode:
                String andOrMode = btn_card_mode.getText().toString();
                if(andOrMode.equals(ConstData.AUTH_MODE_AND))
                    btn_card_mode.setText(ConstData.AUTH_MODE_OR);
                else if(andOrMode.equals(ConstData.AUTH_MODE_OR))
                    btn_card_mode.setText(ConstData.AUTH_MODE_AND);

                break;
        }
    }

    private void registerCallbackListener() {
        getOperationMode();
        getDeviceAuthMode();
        getCounterMeasure();
        getMaskSettings();
        getSaveAuditImage();
        getSavingMode();
        getAdminUserStatus();
    }

    private void getSavingMode() {
        IT100.getPowerSavingMode(new SavingModeStatusCallback() {
            @Override
            public void statusResult(final JSONObject jsonObject) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Boolean enableSaving = jsonObject.optBoolean(MessageKeyValue.SAVING_MODE, false);
                            _savingScreenTimeout = Integer.valueOf(jsonObject.optString(MessageKeyValue.SAVING_MODE_SCREEN_TIMEOUT, "5"));

                            txtScreenTimeout.setText(String.valueOf(_savingScreenTimeout));
                            if(!enableSaving) {
                                schSavingOnOff.setChecked(false);
                            }else{
                                schSavingOnOff.setChecked(true);
                            }
                        }
                    });
                }
            }
        });
    }

    private void getSaveAuditImage() {
        IT100.getAuditFaceImage(new SaveAuditFaceImageCallback() {
            @Override
            public void onResult(final String value) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(value.equals(MessageKeyValue.AUDIT_SAVE_IMAGE_NONE)) {
                                schAudit.setChecked(false);
                                auditView(false);
                            }else{
                                schAudit.setChecked(true);
                                auditView(true);
                                auditSpinner.setSelection(getAuditListIndex(value));
                            }
                        }
                    });
                }
            }
        });
    }

    private void getMaskSettings() {

        IT100.getMaskSettings(new MaskSettingCallback() {
              @Override
              public void onResult(final JSONObject jsonObject) {
                  if(getActivity()!=null){
                      getActivity().runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Boolean isMaskDetect = jsonObject.optBoolean(MessageKeyValue.MASK_DETECT_ENABLE, false);
                              Boolean isAccessControl = jsonObject.optBoolean(MessageKeyValue.MASK_ACCESS_CONTROL, false);

                              _schMask.setChecked(isMaskDetect);
                              _ckMaskAccess.setChecked(isAccessControl);
                          }
                      });
                  }
              }
        });
    }

    private void getCounterMeasure() {

        IT100.getCountermeasure(new CountermeasureCallback() {
            @Override
            public void onResult(final JSONObject jsonObject) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Boolean isCountermeasureFace = jsonObject.optBoolean(MessageKeyValue.COUNTER_MEASURE_FACE, false);
                            Boolean isCountermeasureIris = jsonObject.optBoolean(MessageKeyValue.COUNTER_MEASURE_IRIS, false);

                            _ckCounterEye.setChecked(isCountermeasureIris);
                            _ckCounterFace.setChecked(isCountermeasureFace);
                        }
                    });
                }
            }
        });
    }

    private void setCountermeasure(){
        JSONObject jobj = new JSONObject();
        try {
            jobj.put(MessageKeyValue.COUNTER_MEASURE_FACE, _ckCounterFace.isChecked());
            jobj.put(MessageKeyValue.COUNTER_MEASURE_IRIS, _ckCounterEye.isChecked());
            jobj.put(MessageKeyValue.COUNTER_MEASURE_LENS, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setCountermeasure(jobj.toString());

        editor.putInt(getString(R.string.prefer_key_counter_face), _ckCounterFace.isChecked()? 1:0);
        editor.putInt(getString(R.string.prefer_key_counter_iris), _ckCounterEye.isChecked()? 1:0);
        editor.commit();
    }


    private void getDeviceAuthMode() {
        IT100.getDeviceRecogMode(new DeviceRecogModeCallback() {
            @Override
            public void onResult(JSONObject jsonObject) {
                Boolean enableDeviceMode = jsonObject.optBoolean(MessageKeyValue.DEVICE_RECOG_MODE_ENABLE, false);
                String recogMode = jsonObject.optString(MessageKeyValue.DEVICE_RECOG_MODE, "");
                setAuthMode(enableDeviceMode, recogMode);
            }
        });
    }

    private void getOperationMode() {
        IT100.getOperationMode(new OperationModeCallback(){
            @Override
            public void operationModeResult(final String mode) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (mode) {
                                case MessageKeyValue.OPMODE_CONTINUOUS:
                                    mode_radio_group.check(R.id.mode_continuous);
                                    break;

                                case MessageKeyValue.OPMODE_INTERACTIVE:
                                    mode_radio_group.check(R.id.mode_interactive);
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    private void getAdminUserStatus() {
        IT100.getUserList(MessageKeyValue.USER_ROLE, MessageKeyValue.USER_ROLE_ADMINISTRATOR,
                0, 1000, new AllUserCallback() {
            @Override
            public void allUserResult(ArrayList<UserSimpleInfo> arrayList) {
                if(arrayList!=null && arrayList.size()>0) {
                    final UserSimpleInfo adminUser = arrayList.get(0);
                    if(getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!adminUser.status.equals(MessageKeyValue.USER_ACTIVE)) {
                                    IrisApplication.isAdminActive = false;

                                    admin_radio_group.check(R.id.admin_mode_idpw);
                                    admin_bio.setEnabled(false);
                                    admin_all.setEnabled(false);
                                    admin_bio.setTextColor(getResources().getColor(R.color.disableText, null));
                                    admin_all.setTextColor(getResources().getColor(R.color.disableText, null));
                                } else {
                                    IrisApplication.isAdminActive = true;
                                    admin_bio.setEnabled(true);
                                    admin_all.setEnabled(true);
                                    admin_bio.setTextColor(getResources().getColor(R.color.white, null));
                                    admin_all.setTextColor(getResources().getColor(R.color.white, null));
                                }
                            }
                        });
                    }
                }
            }
        });
    }
	
    String _recogMode_value;
    private String getAuthMode() {
        String selectedAuthMode;
        String selectedAuthCardMode;

        selectedAuthCardMode = btn_card_mode.getText().toString();
        // All IRIS, FACE button selected.
        if(btn_auth_mode.isEnabled()){
            selectedAuthMode = btn_auth_mode.getText().toString();

            if(btn_card_mode.isEnabled()){
                if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_AND)) {
                    if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS_AND_CARD;
                    else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS_AND_CARD;
                    else
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS_AND_CARD;
                }else if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_OR)) {
                    if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS_OR_CARD;
                    else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS_OR_CARD;
                    else
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS_OR_CARD;
                }
            }else {
                if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS;
                else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS;
                else
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS;
            }

        }else{

            if(btn_card_mode.isEnabled()){
                if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_AND)) {
                    if (checkbox_iris.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_IRIS_AND_CARD;
                    } else if (checkbox_face.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_CARD;
                    }
                }else if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_OR)){
                    if (checkbox_iris.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_IRIS_OR_CARD;
                    } else if (checkbox_face.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_CARD;
                    }
                }
            }else{
                if (checkbox_iris.isChecked()) {
                    _recogMode_value = MessageKeyValue.AUTHMODE_IRIS_ONLY;
                }else if(checkbox_face.isChecked()){
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_ONLY;
                }else if(checkbox_card.isChecked()){
                    _recogMode_value = MessageKeyValue.AUTHMODE_CARD_ONLY;
                } else{

                }
            }
        }

        return _recogMode_value;
    }

    private void setAuthMode(final boolean enableDeviceMode, final String authMode) {
        Logger.d("@@getRecogmode value is " + authMode);

        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (authMode == null || !enableDeviceMode || authMode.equals("")) {
                        recogRadioGroup.check(R.id.recog_mode_user);
                    } else {
                        recogRadioGroup.check(R.id.recog_mode_device);
                        if (authMode.contains("Card")) {
                            checkbox_card.setEnabled(true);
                            checkbox_card.setChecked(true);
                            if (authMode.length() < 5) {
                                btn_card_mode.setEnabled(false);
                                btn_auth_mode.setEnabled(false);
                                checkbox_face.setChecked(false);
                                checkbox_iris.setChecked(false);
                            } else {
                                btn_card_mode.setEnabled(true);
                                //  checkbox_card.setEnabled(true);
                                //   checkbox_card.setChecked(true);
                            }

                            String temp;
                            if (authMode.contains("AndCard")) {
                                temp = authMode.replace("AndCard", "");
                                btn_card_mode.setText(ConstData.AUTH_MODE_AND);

                                if (temp.equals(MessageKeyValue.AUTHMODE_FACE_AND_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                                else if (temp.equals(MessageKeyValue.AUTHMODE_FACE_OR_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                                else if (temp.equals(MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                                else if (temp.equals(MessageKeyValue.AUTHMODE_FACE_ONLY)) {
                                    btn_auth_mode.setEnabled(false);
                                    checkbox_face.setChecked(true);
                                    checkbox_iris.setChecked(false);
                                } else if (temp.equals(MessageKeyValue.AUTHMODE_IRIS_ONLY)) {
                                    btn_auth_mode.setEnabled(false);
                                    checkbox_face.setChecked(false);
                                    checkbox_iris.setChecked(true);
                                } else //default authentication mode
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                            } else if (authMode.contains("OrCard")) {
                                temp = authMode.replace("OrCard", "");
                                btn_card_mode.setText(ConstData.AUTH_MODE_OR);

                                if (temp.equals(MessageKeyValue.AUTHMODE_FACE_AND_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                                else if (temp.equals(MessageKeyValue.AUTHMODE_FACE_OR_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                                else if (temp.equals(MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                                else if (temp.equals(MessageKeyValue.AUTHMODE_FACE_ONLY)) {
                                    btn_auth_mode.setEnabled(false);
                                    checkbox_face.setChecked(true);
                                    checkbox_iris.setChecked(false);
                                } else if (temp.equals(MessageKeyValue.AUTHMODE_IRIS_ONLY)) {
                                    btn_auth_mode.setEnabled(false);
                                    checkbox_face.setChecked(false);
                                    checkbox_iris.setChecked(true);
                                } else //default authentication mode
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                            } else {
                                btn_card_mode.setText(ConstData.AUTH_MODE_AND);
                            }
                        } else {
                            btn_card_mode.setEnabled(false);
                            checkbox_card.setChecked(false);

                            if (authMode.equals(MessageKeyValue.AUTHMODE_IRIS_ONLY)) {
                                checkbox_iris.setChecked(true);
                                checkbox_face.setChecked(false);
                                btn_auth_mode.setEnabled(false);

                            } else if (authMode.equals(MessageKeyValue.AUTHMODE_FACE_ONLY)) {
                                checkbox_iris.setChecked(false);
                                checkbox_face.setChecked(true);
                                btn_auth_mode.setEnabled(false);
                            } else {
                                checkbox_iris.setChecked(true);
                                checkbox_face.setChecked(true);
                                btn_auth_mode.setEnabled(true);

                                if (authMode.equals(MessageKeyValue.AUTHMODE_FACE_AND_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                                else if (authMode.equals(MessageKeyValue.AUTHMODE_FACE_OR_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                                else if (authMode.equals(MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS))
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                                else //default authentication mode
                                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                            }
                        }
                    }
                }
            });
        }

    }

    private int getAuditListIndex(String value){
        int index = 0;
        if(value.equals(MessageKeyValue.AUDIT_SAVE_IMAGE_ALL))
            index = 0;
        else if(value.equals(MessageKeyValue.AUDIT_SAVE_IMAGE_SUCCESS))
            index = 1;
        else if(value.equals(MessageKeyValue.AUDIT_SAVE_IMAGE_FAILED))
            index = 2;
        else if(value.equals(MessageKeyValue.AUDIT_SAVE_IMAGE_UNAUTHORIZED))
            index = 3;
        else if(value.equals(MessageKeyValue.AUDIT_SAVE_IMAGE_EXCEPT_CARD_ONLY))
            index = 4;

        return index;
    }

    private String getAuditType(int position){
        String type= MessageKeyValue.AUDIT_SAVE_IMAGE_NONE;

        if(schAudit.isChecked()){
            switch (position){
                case 0:
                    type = MessageKeyValue.AUDIT_SAVE_IMAGE_ALL;
                    break;
                case 1:
                    type = MessageKeyValue.AUDIT_SAVE_IMAGE_SUCCESS;
                    break;
                case 2:
                    type = MessageKeyValue.AUDIT_SAVE_IMAGE_FAILED;
                    break;
                case 3:
                    type = MessageKeyValue.AUDIT_SAVE_IMAGE_UNAUTHORIZED;
                    break;
                case 4:
                    type = MessageKeyValue.AUDIT_SAVE_IMAGE_EXCEPT_CARD_ONLY;
                    break;
            }
        }

        return type;
    }

    private void inputScreenTimeout() {
        LinearLayout inputLayout = (LinearLayout) View.inflate(getContext(), R.layout.dialog_text_input, null);

        TextView tv = (TextView) inputLayout.findViewById(R.id.textview);
        if (tv != null)
            tv.setVisibility(View.GONE);

        EditText editText = (EditText) inputLayout.findViewById(R.id.edittext);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog inputDialog;
        inputDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.saving_screen_timeout)
                .setCancelable(false)
                .setView(inputLayout)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText editText = (EditText)((AlertDialog) dialog).findViewById(R.id.edittext);
                        String timeValue = editText.getText().toString().trim();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        if(!timeValue.equals("")) {
                            try {
                                _savingScreenTimeout = Integer.parseInt(timeValue);
                                if(_savingScreenTimeout<TIMEOUT_MIN_VALUE)
                                    _savingScreenTimeout = TIMEOUT_MIN_VALUE;
                                else if(_savingScreenTimeout>TIMEOUT_MAX_VALUE)
                                    _savingScreenTimeout = TIMEOUT_MAX_VALUE;

                                editor.putInt(getString(R.string.prefer_key_screen_time_out), _savingScreenTimeout);
                                editor.commit();
                                txtScreenTimeout.setText(String.valueOf(_savingScreenTimeout));
                            }catch (Exception e){

                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText)((AlertDialog) dialog).findViewById(R.id.edittext);

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                })
                .create();

        inputDialog.show();
    }
}