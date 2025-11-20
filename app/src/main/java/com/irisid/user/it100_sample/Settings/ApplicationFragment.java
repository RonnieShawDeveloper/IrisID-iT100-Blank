package com.irisid.user.it100_sample.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.AppUpdateCallback;
import com.irisid.it100.callback.GPIOSettingCallback;
import com.irisid.it100.callback.RelaySettingCallback;
import com.irisid.it100.callback.TamperProtectionCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.user.it100_sample_project.BuildConfig;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class ApplicationFragment extends Fragment {

    AppCompatSpinner time_out_spin;
    AppCompatSpinner admin_time_out_spin;
    AppCompatCheckBox _ckTamperDeleteData;

    LinearLayout rootLayout;
    LinearLayout _linearError;
    LinearLayout _linearRelay;
    LinearLayout _linearRelayDetail;
    LinearLayout _linearRelayDelay;
    LinearLayout _linearGuideOnOff;
    LinearLayout _linearVoiceOnOff;
    LinearLayout _linearVoice;
    LinearLayout _linearVoiceDetail;
    LinearLayout _linearEnrollGuideOnOff;
    LinearLayout _linearTamperOnOff;
    LinearLayout _linearTamperDetail;

    TextView _txtRelayOnOff;
    TextView _txtRelayDelay;
    TextView _txtGuideOnOff;
    TextView _txtVoiceOnOff;
    TextView _txtEnrollGuideOnOff;
    TextView _txtTamperOnOff;

    Switch  _switchRelay;
    Switch  _switchGuide;
    Switch  _switchVoice;
    Switch  _switchEnrollGuide;
    Switch  _switchTamper;

    EditText editAppUpdate;

    //GPIO #0 setting New
    AppCompatSpinner g0Spinner;
    Switch schG0;
    ArrayAdapter g0TypeAdapter;
    TextView txtG0OnOff;

    LinearLayout _linearG0Spinner;
    LinearLayout _linearG0HighLow;
    LinearLayout _linearG0Detail0;
    LinearLayout _linearG0Detail1;
    LinearLayout _linearG0Timer;
    AppCompatCheckBox _ck0ForceOpen;
    AppCompatCheckBox _ck0NotOpen;
    AppCompatCheckBox _ck0HeldOpen;
    TextView _txtG0Timer;
    Button _btnG0High;
    Button _btnG0Low;

    //GPIO #1 setting New
    AppCompatSpinner g1Spinner;
    Switch schG1;
    ArrayAdapter g1TypeAdapter;
    TextView txtG1OnOff;

    LinearLayout _linearG1Spinner;
    LinearLayout _linearG1HighLow;
    LinearLayout _linearG1Detail0;
    LinearLayout _linearG1Detail1;
    LinearLayout _linearG1Timer;
    AppCompatCheckBox _ck1ForceOpen;
    AppCompatCheckBox _ck1NotOpen;
    AppCompatCheckBox _ck1HeldOpen;
    TextView _txtG1Timer;
    Button _btnG1High;
    Button _btnG1Low;

    //GPIO #0 setting
    //GPIO #1 setting

    ArrayAdapter timeAdapter;
    ArrayAdapter admin_timeAdapter;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    int value_time_out;
    int value_admin_time_out;
    int value_relay_time;

    final int TIMER_MIN_VALUE = 1;
    final int TIMER_RELAY_MAX_VALUE = 75;
    final int TIMER_GPIO_MAX_VALUE = 300;
    String[] time_list = {"1","2", "3", "4", "5", "6", "7", "8", "9", "10"};
    String[] admin_time_list = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    String[] gpio0_list = {
            MessageKeyValue.GPIO_MODE_ACCEPT,
            MessageKeyValue.GPIO_MODE_REJECT,
            MessageKeyValue.GPIO_MODE_EGRESS,
            MessageKeyValue.GPIO_MODE_FIRE_ALERT,
            MessageKeyValue.GPIO_MODE_DOOR_STATUS_ALERT
    };
    String[] gpio1_list = {
            MessageKeyValue.GPIO_MODE_ACCEPT,
            MessageKeyValue.GPIO_MODE_REJECT,
            MessageKeyValue.GPIO_MODE_EGRESS,
            MessageKeyValue.GPIO_MODE_FIRE_ALERT,
            MessageKeyValue.GPIO_MODE_DOOR_STATUS_ALERT
    };

    String value_app_url;
    String APP_UPDATE_URL;

    JSONObject _jsonGPIO0Setting;
    JSONObject _jsonGPIO1Setting;
    int positionGuide = 0;
    int enrollGuide = 0;
    int voiceGuide = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_application, container, false);
        rootLayout = (LinearLayout)rootView.findViewById(R.id.rootview);

        APP_UPDATE_URL = getString(R.string.it100_app_update_url);
        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref.edit();

        int relayMode = pref.getInt(getString(R.string.prefer_key_relay_on), 1);
        int tamperMode = pref.getInt(getString(R.string.prefer_key_tamper_protection), 1);
        int tamperDeleteData = pref.getInt(getString(R.string.prefer_key_tamper_protection_delete_data), 0);
        int positionGuide = pref.getInt(getString(R.string.prefer_key_position_guide), 0);
        int voiceGuide = pref.getInt(getString(R.string.prefer_key_voice_guide), 0);

		enrollGuide = pref.getInt(getString(R.string.prefer_key_enroll_guide), 1);
        value_time_out = pref.getInt(getString(R.string.prefer_key_time_out), 1);
        value_time_out=value_time_out-1;
        value_admin_time_out = pref.getInt(getString(R.string.prefer_key_time_out_admin), 4);
        value_admin_time_out = value_admin_time_out-1;
        value_relay_time = pref.getInt(getString(R.string.prefer_key_relay_time_out), 2);
        value_app_url= pref.getString(getString(R.string.prefer_key_app_url), "");

        // Relay setting
        _linearRelay = (LinearLayout) rootView.findViewById(R.id.linear_relay);
        _linearRelayDetail = (LinearLayout) rootView.findViewById(R.id.linear_relay_detail);
        _linearRelayDelay = (LinearLayout) rootView.findViewById(R.id.linear_relay_delay);
        _linearGuideOnOff = (LinearLayout) rootView.findViewById(R.id.linear_guide_onoff);
        _linearEnrollGuideOnOff = (LinearLayout) rootView.findViewById(R.id.linear_enroll_guide_onoff);
        _linearVoiceOnOff = (LinearLayout) rootView.findViewById(R.id.linear_voice_onoff);
        _linearVoice = (LinearLayout)rootView.findViewById(R.id.linear_voice);
        _linearVoiceDetail = (LinearLayout)rootView.findViewById(R.id.linear_voice_detail);
        _linearTamperOnOff = (LinearLayout)rootView.findViewById(R.id.linear_tamper_onoff);
        _linearTamperDetail = (LinearLayout)rootView.findViewById(R.id.linear_tamper_detail);

        _linearError = (LinearLayout) rootView.findViewById(R.id.linear_error);
        _txtRelayOnOff = (TextView) rootView.findViewById(R.id.txt_relay_onoff);
        _txtRelayDelay = (TextView) rootView.findViewById(R.id.txt_relay_delay_time);
        _txtGuideOnOff = (TextView) rootView.findViewById(R.id.txt_guide_onoff);
        _txtEnrollGuideOnOff = (TextView) rootView.findViewById(R.id.txt_enroll_guide_onoff);
        _txtVoiceOnOff = (TextView) rootView.findViewById(R.id.txt_voice_onoff);
        _txtTamperOnOff = (TextView) rootView.findViewById(R.id.txt_tamper_onoff);

        _switchRelay = (Switch) rootView.findViewById(R.id.sch_relay);
        _switchGuide = (Switch) rootView.findViewById(R.id.sch_guide);
        _switchEnrollGuide = (Switch) rootView.findViewById(R.id.sch_enroll_guide);
        _switchVoice = (Switch) rootView.findViewById(R.id.sch_voice);
        _switchTamper = (Switch) rootView.findViewById(R.id.sch_tamper);
        _ckTamperDeleteData = rootView.findViewById(R.id.ck_tamper_delete_data);
        _txtRelayDelay.setText(String.valueOf(value_relay_time));

        _switchRelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    _txtRelayOnOff.setText(getResources().getString(R.string.on));
                    _txtRelayOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                    _linearRelayDetail.setVisibility(View.VISIBLE);
                    editor.putInt(getString(R.string.prefer_key_relay_on), 1);
                }else{
                    _txtRelayOnOff.setText(getResources().getString(R.string.off));
                    _txtRelayOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                    _linearRelayDetail.setVisibility(View.GONE);
                    editor.putInt(getString(R.string.prefer_key_relay_on), 0);
                }
                editor.commit();
            }
        });

        _linearTamperOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_switchTamper.isChecked()){
                    if(_linearTamperDetail.getVisibility()==View.GONE)
                        _linearTamperDetail.setVisibility(View.VISIBLE);
                    else if(_linearTamperDetail.getVisibility()==View.VISIBLE)
                        _linearTamperDetail.setVisibility(View.GONE);
                }
            }
        });

        _switchTamper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    _txtTamperOnOff.setText(getResources().getString(R.string.on));
                    _txtTamperOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                    editor.putInt(getString(R.string.prefer_key_tamper_protection), 1);
                    _linearTamperDetail.setVisibility(View.VISIBLE);
                }else{
                    _txtTamperOnOff.setText(getResources().getString(R.string.off));
                    _txtTamperOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                    editor.putInt(getString(R.string.prefer_key_tamper_protection), 0);
                    _linearTamperDetail.setVisibility(View.GONE);
                }
            }
        });

        _ckTamperDeleteData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    editor.putInt(getString(R.string.prefer_key_tamper_protection_delete_data), 1);
                else
                    editor.putInt(getString(R.string.prefer_key_tamper_protection_delete_data), 0);
            }
        });
        
        _linearGuideOnOff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                _switchGuide.setChecked(!_switchGuide.isChecked());
                return false;
            }
        });
        _switchGuide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    _txtGuideOnOff.setText(getResources().getString(R.string.on));
                    _txtGuideOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                    editor.putInt(getString(R.string.prefer_key_position_guide), 1);
                }else{
                    _txtGuideOnOff.setText(getResources().getString(R.string.off));
                    _txtGuideOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                    editor.putInt(getString(R.string.prefer_key_position_guide), 0);
                }
                editor.commit();
            }
        });

        _linearEnrollGuideOnOff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                _switchEnrollGuide.setChecked(!_switchEnrollGuide.isChecked());
                return false;
            }
        });

        _switchEnrollGuide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    _txtEnrollGuideOnOff.setText(getResources().getString(R.string.on));
                    _txtEnrollGuideOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                    editor.putInt(getString(R.string.prefer_key_enroll_guide), 1);
                }else{
                    _txtEnrollGuideOnOff.setText(getResources().getString(R.string.off));
                    _txtEnrollGuideOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                    editor.putInt(getString(R.string.prefer_key_enroll_guide), 0);
                }
                editor.commit();
            }
        });

        _linearVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_switchVoice.isChecked()){
                    if(_linearVoiceDetail.getVisibility()==View.GONE)
                        _linearVoiceDetail.setVisibility(View.VISIBLE);
                    else if(_linearVoiceDetail.getVisibility()==View.VISIBLE)
                        _linearVoiceDetail.setVisibility(View.GONE);
                }
            }
        });

        _linearVoiceOnOff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                _switchVoice.setChecked(!_switchVoice.isChecked());
                return false;
            }
        });
        _switchVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    _linearVoiceDetail.setVisibility(View.VISIBLE);
                    _txtVoiceOnOff.setText(getResources().getString(R.string.on));
                    _txtVoiceOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                    editor.putInt(getString(R.string.prefer_key_voice_guide), 1);
                }else{
                    _linearVoiceDetail.setVisibility(View.GONE);
                    _txtVoiceOnOff.setText(getResources().getString(R.string.off));
                    _txtVoiceOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                    editor.putInt(getString(R.string.prefer_key_voice_guide), 0);
                }
                editor.commit();
            }
        });

        _linearRelayDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputRelayTime();
            }
        });

        if(relayMode==1) {
            _switchRelay.setChecked(true);
            _txtRelayOnOff.setText(getResources().getString(R.string.on));
            _txtRelayOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            _linearRelayDetail.setVisibility(View.VISIBLE);
        }else{
            _switchRelay.setChecked(false);
            _txtRelayOnOff.setText(getResources().getString(R.string.off));
            _txtRelayOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
            _linearRelayDetail.setVisibility(View.GONE);
        }

        if(tamperMode == 1) {
            _switchTamper.setChecked(true);
            _txtTamperOnOff.setText(getResources().getString(R.string.on));
            _txtTamperOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
        }else{
            _linearTamperDetail.setVisibility(View.GONE);
            _switchTamper.setChecked(false);
            _txtTamperOnOff.setText(getResources().getString(R.string.off));
            _txtTamperOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
        }

        if(tamperDeleteData == 1) _ckTamperDeleteData.setChecked(true);
        else _ckTamperDeleteData.setChecked(false);

        if(positionGuide==1) {
            _switchGuide.setChecked(true);
            _txtGuideOnOff.setText(getResources().getString(R.string.on));
            _txtGuideOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
        }else{
            _switchGuide.setChecked(false);
            _txtGuideOnOff.setText(getResources().getString(R.string.off));
            _txtGuideOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
        }

        if(enrollGuide==1) {
            _switchEnrollGuide.setChecked(true);
            _txtEnrollGuideOnOff.setText(getResources().getString(R.string.on));
            _txtEnrollGuideOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
        }else{
            _switchEnrollGuide.setChecked(false);
            _txtEnrollGuideOnOff.setText(getResources().getString(R.string.off));
            _txtEnrollGuideOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
        }

        if(voiceGuide==1) {
            _switchVoice.setChecked(true);
            _txtVoiceOnOff.setText(getResources().getString(R.string.on));
            _txtVoiceOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            _linearVoiceDetail.setVisibility(View.VISIBLE);
        }else{
            _switchVoice.setChecked(false);
            _txtVoiceOnOff.setText(getResources().getString(R.string.off));
            _txtVoiceOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
            _linearVoiceDetail.setVisibility(View.GONE);
        }

        // spinner
        time_out_spin = (AppCompatSpinner) rootView.findViewById(R.id.time_out_spin);
        admin_time_out_spin = (AppCompatSpinner) rootView.findViewById(R.id.admin_time_out_spin);

        timeAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, time_list);
        time_out_spin.setAdapter(timeAdapter);
        time_out_spin.setSelection(value_time_out);
        time_out_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView)view).setTextColor(Color.parseColor("#ffffff"));
                ((TextView)view).setTextSize(20);

                // store time_out value
                editor.putInt(getString(R.string.prefer_key_time_out), Integer.valueOf(time_list[position]));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        admin_timeAdapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, admin_time_list);
        admin_time_out_spin.setAdapter(admin_timeAdapter);
        admin_time_out_spin.setSelection(value_admin_time_out);
        admin_time_out_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(Color.parseColor("#ffffff"));
                ((TextView)view).setTextSize(20);

                // store admin_time_out
                editor.putInt(getString(R.string.prefer_key_time_out_admin), Integer.valueOf(admin_time_list[position]));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editAppUpdate =  (EditText)rootView.findViewById(R.id.edit_app_update);
        editAppUpdate.setText(value_app_url);
        editAppUpdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                _linearError.setVisibility(View.GONE);
                editAppUpdate.getBackground().mutate().setColorFilter(null);
                return false;
            }
        });

        (rootView.findViewById(R.id.btn_app_update)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAppUpdate.clearFocus();
                if(editAppUpdate.getText().toString().isEmpty()){
                    editAppUpdate.setCursorVisible(false);
                    _linearError.setVisibility(View.VISIBLE);
                    editAppUpdate.getBackground().mutate().setColorFilter(
                            getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                }else {
                    showUpdateDialog();
                }
            }
        });

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(r);
                int heightDiff = rootLayout.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    //Logger.d("@@keyboard  over 100");

                    int padding = 0;
                    int toPosition = 0;
                    if(editAppUpdate.isFocused()){
                        padding = 320;
                        toPosition = 900;
                    }
                    rootLayout.setPadding(0,0,0,padding);
                    //scrollView.scrollBy(0,toPosition );

                } else {
                    //ok now we know the keyboard is down...
                    rootLayout.setPadding(0,0,0,0);
                }
            }
        });



        initViewGPIO0Setting(rootView);
        initViewGPIO1Setting(rootView);
        registerCallbackListener();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        IT100.setRelaySettings(_switchRelay.isChecked(),
                IrisApplication.relay_time_out);

        // Set Tamper Protection Settings
        JSONObject jTamperProtection = new JSONObject();
        try {
            jTamperProtection.put(MessageKeyValue.TAMPER_SETTING_ENABLE, _switchTamper.isChecked());
            jTamperProtection.put(MessageKeyValue.TAMPER_SETTING_DELETE_DATA, _ckTamperDeleteData.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setTamperProtection(jTamperProtection.toString());

        setGPIOSettings();
    }

    private void initViewGPIO0Setting(View rootView){
        _linearG0Spinner = rootView.findViewById(R.id.linear_gpio0_spin);
        _linearG0HighLow = rootView.findViewById(R.id.linear_g0_highlow);
        _btnG0High = rootView.findViewById(R.id.btn_g0_high);
        _btnG0Low = rootView.findViewById(R.id.btn_g0_low);

        LinearLayout linearTouchG0High = rootView.findViewById(R.id.linear_touch_g0_high);
        LinearLayout linearTouchG0Low   = rootView.findViewById(R.id.linear_touch_g0_low);
        linearTouchG0High.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnG0High.setEnabled(true);
                _btnG0Low.setEnabled(false);
            }
        });
        linearTouchG0Low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnG0High.setEnabled(false);
                _btnG0Low.setEnabled(true);
            }
        });

        txtG0OnOff = rootView.findViewById(R.id.txt_gpio0_onoff);
        schG0 = rootView.findViewById(R.id.sch_gpio0);
        schG0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Logger.d("@schG0 onCheckedChanged " + isChecked );

                if(!isChecked) {
                    //auditView(false);
                    txtG0OnOff.setText(getResources().getString(R.string.off));
                    txtG0OnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                    _linearG0Spinner.setVisibility(View.INVISIBLE);
                    _linearG0HighLow.setVisibility(View.INVISIBLE);
                    _linearG0Detail0.setVisibility(View.GONE);
                    _linearG0Detail1.setVisibility(View.GONE);
                }else{
                    //auditView(true);
                    g0Spinner.setSelection(0);
                    txtG0OnOff.setText(getResources().getString(R.string.on));
                    txtG0OnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                    _linearG0Spinner.setVisibility(View.VISIBLE);
                    _linearG0HighLow.setVisibility(View.VISIBLE);
                }
            }
        });

        g0Spinner = rootView.findViewById(R.id.gpio0_type_spin);
        g0TypeAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, gpio0_list);
        g0Spinner.setAdapter(g0TypeAdapter);
        g0Spinner.setSelection(0);
        g0Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView)view).setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                ((TextView)view).setTextSize(20);

                if(position == 4){
                    _linearG0Detail0.setVisibility(View.VISIBLE);
                    _linearG0Detail1.setVisibility(View.VISIBLE);
                } else {
                    _linearG0Detail0.setVisibility(View.GONE);
                    _linearG0Detail1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        _ck0ForceOpen = rootView.findViewById(R.id.ck0_force_open);
        _ck0NotOpen = rootView.findViewById(R.id.ck0_not_open);
        _ck0HeldOpen = rootView.findViewById(R.id.ck0_held_open);
        _ck0HeldOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    _linearG0Timer.setVisibility(View.VISIBLE);
                else
                    _linearG0Timer.setVisibility(View.GONE);
            }
        });

        _linearG0Detail0 = rootView.findViewById(R.id.linear_g0_detail0);
        _linearG0Detail1 = rootView.findViewById(R.id.linear_g0_detail1);
        _txtG0Timer = rootView.findViewById(R.id.txt_g0_timer);
        _linearG0Timer = rootView.findViewById(R.id.linear_g0_timer);
        _linearG0Timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputGPIOTimer(R.id.txt_g0_timer);
            }
        });
    }

    private void initViewGPIO1Setting(View rootView){
        _linearG1Spinner = rootView.findViewById(R.id.linear_gpio1_spin);
        _linearG1HighLow = rootView.findViewById(R.id.linear_g1_highlow);
        _btnG1High = rootView.findViewById(R.id.btn_g1_high);
        _btnG1Low = rootView.findViewById(R.id.btn_g1_low);

        LinearLayout linearTouchG1High = rootView.findViewById(R.id.linear_touch_g1_high);
        LinearLayout linearTouchG1Low   = rootView.findViewById(R.id.linear_touch_g1_low);
        linearTouchG1High.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnG1High.setEnabled(true);
                _btnG1Low.setEnabled(false);
            }
        });
        linearTouchG1Low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btnG1High.setEnabled(false);
                _btnG1Low.setEnabled(true);
            }
        });

        txtG1OnOff = rootView.findViewById(R.id.txt_gpio1_onoff);
        schG1 = rootView.findViewById(R.id.sch_gpio1);
        schG1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.d("@schG0 onCheckedChanged " + isChecked );

                if(!isChecked) {
                    //auditView(false);
                    txtG1OnOff.setText(getResources().getString(R.string.off));
                    txtG1OnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                    _linearG1Spinner.setVisibility(View.INVISIBLE);
                    _linearG1HighLow.setVisibility(View.INVISIBLE);
                    _linearG1Detail0.setVisibility(View.GONE);
                    _linearG1Detail1.setVisibility(View.GONE);
                }else{
                    //auditView(true);
                    g1Spinner.setSelection(0);
                    txtG1OnOff.setText(getResources().getString(R.string.on));
                    txtG1OnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                    _linearG1Spinner.setVisibility(View.VISIBLE);
                    _linearG1HighLow.setVisibility(View.VISIBLE);
                }
            }
        });

        g1Spinner = rootView.findViewById(R.id.gpio1_type_spin);
        g1TypeAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, gpio1_list);
        g1Spinner.setAdapter(g1TypeAdapter);
        g1Spinner.setSelection(0);
        g1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView)view).setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                ((TextView)view).setTextSize(20);

                if(position == 4){
                    _linearG1Detail0.setVisibility(View.VISIBLE);
                    _linearG1Detail1.setVisibility(View.VISIBLE);
                } else {
                    _linearG1Detail0.setVisibility(View.GONE);
                    _linearG1Detail1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        _ck1ForceOpen = rootView.findViewById(R.id.ck1_force_open);
        _ck1NotOpen = rootView.findViewById(R.id.ck1_not_open);
        _ck1HeldOpen = rootView.findViewById(R.id.ck1_held_open);
        _ck1HeldOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    _linearG1Timer.setVisibility(View.VISIBLE);
                else
                    _linearG1Timer.setVisibility(View.GONE);
            }
        });

        _linearG1Detail0 = rootView.findViewById(R.id.linear_g1_detail0);
        _linearG1Detail1 = rootView.findViewById(R.id.linear_g1_detail1);
        _txtG1Timer = rootView.findViewById(R.id.txt_g1_timer);
        _linearG1Timer = rootView.findViewById(R.id.linear_g1_timer);
        _linearG1Timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputGPIOTimer(R.id.txt_g1_timer);
            }
        });

    }

    private void registerCallbackListener() {
        getRelaySetting();
        getGPIOSetting();
        getTamperProtectionSetting();
    }

    private void getRelaySetting() {
        IT100.getRelaySettings(new RelaySettingCallback() {
            @Override
            public void onResult(final JSONObject jsonObject) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean relayOn = jsonObject.optBoolean(MessageKeyValue.RELAY_SETTING_ENABLE, true);
                            int timer = Integer.valueOf(jsonObject.optString(MessageKeyValue.RELAY_SETTING_TIMER, "0"));

                            _switchRelay.setChecked(relayOn);
                            _txtRelayDelay.setText(String.valueOf(timer));

                            IrisApplication.relay_time_out = timer;
                            editor.putInt(getString(R.string.prefer_key_relay_time_out), timer);
                            editor.putInt(getString(R.string.prefer_key_relay_on), relayOn?1:0);
							editor.commit();
                        }
                    });
                }
            }
        });
    }

    private void getTamperProtectionSetting() {
        IT100.getTamperProtection(new TamperProtectionCallback() {
            @Override
            public void onResult(JSONObject jsonObject) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean tamperOn = jsonObject.optBoolean(MessageKeyValue.TAMPER_SETTING_ENABLE, true);
                            boolean tamperDetectDelData = jsonObject.optBoolean(MessageKeyValue.TAMPER_SETTING_DELETE_DATA, false);
                            _switchTamper.setChecked(tamperOn);
                            _ckTamperDeleteData.setChecked(tamperDetectDelData);
                            editor.putInt(getString(R.string.prefer_key_tamper_protection), tamperOn?1:0);
                            editor.putInt(getString(R.string.prefer_key_tamper_protection_delete_data), tamperDetectDelData?1:0);
                        	editor.commit();
						}
                    });
                }
            }
        });
    }

    private void setGPIOSettings(){
        if(_jsonGPIO0Setting==null || _jsonGPIO1Setting==null)
            return;

        JSONObject jGPIOSetting = new JSONObject();
        JSONObject j0DoorStatus = new JSONObject();
        JSONObject j1DoorStatus = new JSONObject();
        try {
            //GPIO0
            if(!schG0.isChecked()){
                _jsonGPIO0Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_OFF);
            }else {
                int selG0Type = g0Spinner.getSelectedItemPosition();
                if(selG0Type == 0){ // Accept
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_ACCEPT);
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_ACCEPT_OPEN,
                            _btnG0High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG0Type == 1){ //Reject
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_REJECT);
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_REJECT_OPEN,
                            _btnG0High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG0Type == 2){ // Egress
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_EGRESS);
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_EGRESS_OPEN,
                            _btnG0High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG0Type == 3){
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_FIRE_ALERT);
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_FIRE_ALERT_OPEN,
                            _btnG0High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG0Type == 4){
                    _jsonGPIO0Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_DOOR_STATUS_ALERT);
                    j0DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_INPUT,
                            _btnG0High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                    j0DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_FORCE_OPEN, _ck0ForceOpen.isChecked());
                    j0DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_NOT_OPEN, _ck0NotOpen.isChecked());
                    j0DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN, _ck0HeldOpen.isChecked());
                    if(_ck0HeldOpen.isChecked())
                        j0DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN_TIME_THREAD,
                                Integer.valueOf(_txtG0Timer.getText().toString()));
                }
                _jsonGPIO0Setting.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT, j0DoorStatus);
            }

            //GPIO1
            if(!schG1.isChecked()){
                _jsonGPIO1Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_OFF);
            }else {
                int selG1Type = g1Spinner.getSelectedItemPosition();
                if(selG1Type == 0){ // Allow
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_ACCEPT);
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_ACCEPT_OPEN,
                            _btnG1High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG1Type == 1){ //Reject
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_REJECT);
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_REJECT_OPEN,
                            _btnG1High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG1Type == 2){ // Egress
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_EGRESS);
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_EGRESS_OPEN,
                            _btnG1High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG1Type == 3){
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_FIRE_ALERT);
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_FIRE_ALERT_OPEN,
                            _btnG1High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                }else if(selG1Type == 4){
                    _jsonGPIO1Setting.put(MessageKeyValue.GPIO_MODE, MessageKeyValue.GPIO_MODE_DOOR_STATUS_ALERT);
                    j1DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_INPUT,
                            _btnG1High.isEnabled()? MessageKeyValue.GPIO_OUT_HIGH: MessageKeyValue.GPIO_OUT_LOW);
                    j1DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_FORCE_OPEN, _ck1ForceOpen.isChecked());
                    j1DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_NOT_OPEN, _ck1NotOpen.isChecked());
                    j1DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN, _ck1HeldOpen.isChecked());
                    if(_ck1HeldOpen.isChecked())
                        j1DoorStatus.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN_TIME_THREAD,
                                Integer.valueOf(_txtG1Timer.getText().toString()));
                }
                _jsonGPIO1Setting.put(MessageKeyValue.GPIO_DOOR_STATUS_ALERT, j1DoorStatus);
            }

            jGPIOSetting.put(MessageKeyValue.GPIO0, _jsonGPIO0Setting);
            jGPIOSetting.put(MessageKeyValue.GPIO1, _jsonGPIO1Setting);
            Logger.d("setGPIOSetting is "+ jGPIOSetting.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IT100.setGPIOSettings(jGPIOSetting.toString());
    }

    private void getGPIOSetting() {
        IT100.getGPIOSettings(new GPIOSettingCallback() {
            @Override
            public void onResult(final JSONObject jsonObject) {
                //GPIO #0
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //GPIO #0
                            _jsonGPIO0Setting= jsonObject.optJSONObject(MessageKeyValue.GPIO0);
                            String GPIO0_Mode = _jsonGPIO0Setting.optString(MessageKeyValue.GPIO_MODE, "off");
                            if(GPIO0_Mode.equals(MessageKeyValue.GPIO_MODE_OFF)){
                                schG0.setChecked(false);
                                txtG0OnOff.setText(getResources().getString(R.string.off));
                                txtG0OnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                                _linearG0Spinner.setVisibility(View.INVISIBLE);
                                _linearG0HighLow.setVisibility(View.INVISIBLE);
                            }else {
                                schG0.setChecked(true);

                                if (GPIO0_Mode.equals(MessageKeyValue.GPIO_MODE_ACCEPT)) {
                                    g0Spinner.setSelection(0);
                                    if (_jsonGPIO0Setting.optString(MessageKeyValue.GPIO_ACCEPT_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG0Low.setEnabled(true);
                                        _btnG0High.setEnabled(false);
                                    } else {
                                        _btnG0Low.setEnabled(false);
                                        _btnG0High.setEnabled(true);
                                    }
                                } else if (GPIO0_Mode.equals(MessageKeyValue.GPIO_MODE_REJECT)) {
                                    g0Spinner.setSelection(1);
                                    if (_jsonGPIO0Setting.optString(MessageKeyValue.GPIO_REJECT_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG0Low.setEnabled(true);
                                        _btnG0High.setEnabled(false);
                                    } else {
                                        _btnG0Low.setEnabled(false);
                                        _btnG0High.setEnabled(true);
                                    }
                                } else if (GPIO0_Mode.equals(MessageKeyValue.GPIO_MODE_EGRESS)) {
                                    g0Spinner.setSelection(2);
                                    if (_jsonGPIO0Setting.optString(MessageKeyValue.GPIO_EGRESS_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG0Low.setEnabled(true);
                                        _btnG0High.setEnabled(false);
                                    } else {
                                        _btnG0Low.setEnabled(false);
                                        _btnG0High.setEnabled(true);
                                    }
                                } else if (GPIO0_Mode.equals(MessageKeyValue.GPIO_MODE_FIRE_ALERT)) {
                                    g0Spinner.setSelection(3);
                                    if (_jsonGPIO0Setting.optString(MessageKeyValue.GPIO_FIRE_ALERT_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG0Low.setEnabled(true);
                                        _btnG0High.setEnabled(false);
                                    } else {
                                        _btnG0Low.setEnabled(false);
                                        _btnG0High.setEnabled(true);
                                    }
                                } else if (GPIO0_Mode.equals(MessageKeyValue.GPIO_MODE_DOOR_STATUS_ALERT)) {
                                    g0Spinner.setSelection(4);
                                    JSONObject jStatus = _jsonGPIO0Setting.optJSONObject(MessageKeyValue.GPIO_DOOR_STATUS_ALERT);
                                    if (jStatus.optString(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_INPUT, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG0Low.setEnabled(true);
                                        _btnG0High.setEnabled(false);
                                    } else {
                                        _btnG0Low.setEnabled(false);
                                        _btnG0High.setEnabled(true);
                                    }

                                    if (jStatus.optBoolean(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_FORCE_OPEN, false))
                                        _ck0ForceOpen.setChecked(true);
                                    else
                                        _ck0ForceOpen.setChecked(false);

                                    if (jStatus.optBoolean(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_NOT_OPEN, false))
                                        _ck0NotOpen.setChecked(true);
                                    else
                                        _ck0NotOpen.setChecked(false);

                                    if (jStatus.optBoolean(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN, false)) {
                                        _ck0HeldOpen.setChecked(true);
                                        _txtG0Timer.setText(String.valueOf(jStatus.optInt(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN_TIME_THREAD, 0)));
                                    } else
                                        _ck0HeldOpen.setChecked(false);
                                }
                            }

                            //GPIO #1
                            _jsonGPIO1Setting= jsonObject.optJSONObject(MessageKeyValue.GPIO1);
                            String GPIO1_Mode = _jsonGPIO1Setting.optString(MessageKeyValue.GPIO_MODE, "off");
                            if(GPIO1_Mode.equals(MessageKeyValue.GPIO_MODE_OFF)){
                                schG1.setChecked(false);
                                txtG1OnOff.setText(getResources().getString(R.string.off));
                                txtG1OnOff.setTextColor(getResources().getColor(R.color.disableText, null));
                                _linearG1Spinner.setVisibility(View.INVISIBLE);
                                _linearG1HighLow.setVisibility(View.INVISIBLE);
                            }else {
                                schG1.setChecked(true);

                                if (GPIO1_Mode.equals(MessageKeyValue.GPIO_MODE_ACCEPT)) {
                                    g1Spinner.setSelection(0);
                                    if (_jsonGPIO1Setting.optString(MessageKeyValue.GPIO_ACCEPT_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG1Low.setEnabled(true);
                                        _btnG1High.setEnabled(false);
                                    } else {
                                        _btnG1Low.setEnabled(false);
                                        _btnG1High.setEnabled(true);
                                    }
                                } else if (GPIO1_Mode.equals(MessageKeyValue.GPIO_MODE_REJECT)) {
                                    g1Spinner.setSelection(1);
                                    if (_jsonGPIO1Setting.optString(MessageKeyValue.GPIO_REJECT_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG1Low.setEnabled(true);
                                        _btnG1High.setEnabled(false);
                                    } else {
                                        _btnG1Low.setEnabled(false);
                                        _btnG1High.setEnabled(true);
                                    }
                                } else if (GPIO1_Mode.equals(MessageKeyValue.GPIO_MODE_EGRESS)) {
                                    g1Spinner.setSelection(2);
                                    if (_jsonGPIO1Setting.optString(MessageKeyValue.GPIO_EGRESS_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG1Low.setEnabled(true);
                                        _btnG1High.setEnabled(false);
                                    } else {
                                        _btnG1Low.setEnabled(false);
                                        _btnG1High.setEnabled(true);
                                    }
                                } else if (GPIO1_Mode.equals(MessageKeyValue.GPIO_MODE_FIRE_ALERT)) {
                                    g1Spinner.setSelection(3);
                                    if (_jsonGPIO1Setting.optString(MessageKeyValue.GPIO_FIRE_ALERT_OPEN, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG1Low.setEnabled(true);
                                        _btnG1High.setEnabled(false);
                                    } else {
                                        _btnG1Low.setEnabled(false);
                                        _btnG1High.setEnabled(true);
                                    }
                                } else if (GPIO1_Mode.equals(MessageKeyValue.GPIO_MODE_DOOR_STATUS_ALERT)) {
                                    g1Spinner.setSelection(4);
                                    JSONObject jStatus = _jsonGPIO1Setting.optJSONObject(MessageKeyValue.GPIO_DOOR_STATUS_ALERT);
                                    if (jStatus.optString(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_INPUT, "low").equals(
                                            MessageKeyValue.GPIO_OUT_LOW)) {
                                        _btnG1Low.setEnabled(true);
                                        _btnG1High.setEnabled(false);
                                    } else {
                                        _btnG1Low.setEnabled(false);
                                        _btnG1High.setEnabled(true);
                                    }

                                    if (jStatus.optBoolean(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_FORCE_OPEN, false))
                                        _ck1ForceOpen.setChecked(true);
                                    else
                                        _ck1ForceOpen.setChecked(false);

                                    if (jStatus.optBoolean(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_NOT_OPEN, false))
                                        _ck1NotOpen.setChecked(true);
                                    else
                                        _ck1NotOpen.setChecked(false);

                                    if (jStatus.optBoolean(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN, false)) {
                                        _ck1HeldOpen.setChecked(true);
                                        _txtG1Timer.setText(String.valueOf(jStatus.optInt(MessageKeyValue.GPIO_DOOR_STATUS_ALERT_HELD_OPEN_TIME_THREAD, 0)));
                                    } else
                                        _ck1HeldOpen.setChecked(false);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void inputRelayTime() {
        LinearLayout inputLayout = (LinearLayout) View.inflate(getContext(), R.layout.dialog_text_input, null);

        TextView tv = (TextView) inputLayout.findViewById(R.id.textview);
        if (tv != null)
            tv.setVisibility(View.GONE);

        EditText editText = (EditText) inputLayout.findViewById(R.id.edittext);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog inputDialog;
        inputDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delay_time)
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
                                int timerValue = Integer.parseInt(timeValue);
                                if(timerValue<TIMER_MIN_VALUE)
                                    timerValue = TIMER_MIN_VALUE;
                                else if(timerValue>TIMER_RELAY_MAX_VALUE)
                                    timerValue = TIMER_RELAY_MAX_VALUE;

                                IrisApplication.relay_time_out = timerValue;
                                editor.putInt(getString(R.string.prefer_key_relay_time_out), timerValue);
                                editor.commit();

                                _txtRelayDelay.setText(String.valueOf(timerValue));
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

    private void inputGPIOTimer(final int resourceId) {

        LinearLayout inputLayout = (LinearLayout) View.inflate(getContext(), R.layout.dialog_text_input, null);

        TextView tv = (TextView) inputLayout.findViewById(R.id.textview);
        if (tv != null)
            tv.setVisibility(View.GONE);

        EditText editText = (EditText) inputLayout.findViewById(R.id.edittext);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog inputDialog;
        inputDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.delay_time)
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
                                int timerValue = Integer.parseInt(timeValue);
                                if(timerValue < TIMER_MIN_VALUE)
                                    timerValue = TIMER_MIN_VALUE;
                                else if(timerValue > TIMER_GPIO_MAX_VALUE)
                                    timerValue = TIMER_GPIO_MAX_VALUE;

                                if(resourceId == R.id.txt_g0_timer){
                                    _txtG0Timer.setText(String.valueOf(timerValue));
                                } else if(resourceId == R.id.txt_g1_timer){
                                    _txtG1Timer.setText(String.valueOf(timerValue));
                                }

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

    

    Dialog pdialog;
    String _statusMsg = "";
    private void showUpdateDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content =  inflater.inflate(R.layout.dialog_id_pw, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.app_update_enter_idpw);
        builder.setView(content);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                TextInputEditText editID = (TextInputEditText) ((AlertDialog)dialog).findViewById(R.id.te_id);
                TextInputEditText editPW = (TextInputEditText) ((AlertDialog)dialog).findViewById(R.id.te_pw);

                JSONObject jobj = new JSONObject();
                JSONObject appinfo = new JSONObject();

                try {
                    jobj.put(MessageKeyValue.APP_UPDATE_URL, editAppUpdate.getText().toString());
                    jobj.put(MessageKeyValue.APP_UPDATE_ID, editID.getText().toString());
                    jobj.put(MessageKeyValue.APP_UPDATE_PW, editPW.getText().toString());
                    if(editAppUpdate.getText().toString().equals(APP_UPDATE_URL)){
                        appinfo.put(MessageKeyValue.APP_UPDATE_APPINFO_PN, BuildConfig.APPLICATION_ID);
                        appinfo.put(MessageKeyValue.APP_UPDATE_APPINFO_VN, BuildConfig.VERSION_NAME);
                        appinfo.put(MessageKeyValue.APP_UPDATE_APPINFO_VC, BuildConfig.VERSION_CODE);
                    }
                    jobj.put(MessageKeyValue.APP_UPDATE_APPINFO, appinfo);

                    editor.putString(getString(R.string.prefer_key_app_url), editAppUpdate.getText().toString());
                    editor.commit();

                    Logger.d("AppUpdate json is "+ jobj.toString(4));
                }catch (Exception e){ }

                pdialog = new Dialog(getActivity());
                pdialog.setCancelable(false);
                pdialog.setContentView(R.layout.progress_loading);
                pdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                pdialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                pdialog.getWindow().setStatusBarColor(0xaa000000);
                pdialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xaa000000));

                pdialog.show();

                final ImageView img_loading_frame = (ImageView)pdialog.findViewById(R.id.iv_frame_loading);
                final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
                img_loading_frame.post(new Runnable() {
                    @Override
                    public void run() {
                        frameAnimation.start();
                    }
                });

                final TextView tv_progress_message = (TextView)pdialog.findViewById(R.id.tv_progress_message);
                tv_progress_message.setText("Downloading...");  //System updating...
                tv_progress_message.setVisibility(View.VISIBLE);

                IT100.updateApp(jobj.toString(), new AppUpdateCallback() {
                    @Override
                    public void fail(int resultCode, String msg) {
                        pdialog.dismiss();
                        if (resultCode == -3) {
                            _statusMsg = getString(R.string.app_update_fail_connect_url);//"Can't connect the URL";
                        } else if (resultCode == -2) {
                            _statusMsg = getString(R.string.app_update_fail_file_error);//"There is a problem with the downloaded file";
                        } else if (resultCode == -1) {
                            _statusMsg = getString(R.string.app_update_fail_file_install);//"App Install error";
                        } else{
                            _statusMsg = msg;
                        }
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                new BasicAlertDialog(getContext())
                                        .setTitle(R.string.dlgtitle_err)
                                        .setMessage(_statusMsg)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                            }
                        }, 0);
                    }

                    @Override
                    public void success(int resultCode, String msg) {
                        pdialog.dismiss();
                    }

                    @Override
                    public void onStatus(int resultCode, String msg) {
                        showProgressMsg(msg);
                    }
                });
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void showProgressMsg(final String msg){
        if(pdialog==null)
            return;

        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) pdialog.findViewById(R.id.tv_progress_message)).setText(msg);
                }
            });
        }
    }
}
