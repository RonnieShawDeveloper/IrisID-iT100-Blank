package com.irisid.user.it100_sample.Settings;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.CardReaderSettingCallback;
import com.irisid.it100.callback.CheckCardReaderConnectionCallback;
import com.irisid.it100.callback.CheckThermalConnectionCallback;
import com.irisid.it100.callback.ThermalSettingJSONCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_CHANGE_THERMAL;

public class ExternalDeviceFragment extends Fragment {

    LinearLayout _linearCardReader;
    LinearLayout _linearThermal;
    LinearLayout _linearThermalDetail;
    LinearLayout _linearThermalUnit;
    LinearLayout _linearThermalThreshold;
    LinearLayout _linearThermalCorrection;
    LinearLayout _linearThermalAlarm;
    LinearLayout _linearThermalAccess;
    LinearLayout _linearProgress;

    Switch _schCardReader;
    Switch _schThermal;
    TextView _txtCardOnOff;
    TextView _txtCardType;
    TextView _txtThermalOnOff;
    TextView _txtTempThreshold;
    TextView _txtThermalUnit;
    TextView _txtThermalCorrection;
    TextView _txtThresholdLimit;
    AppCompatCheckBox _ckTempAlarm;
    AppCompatCheckBox _ckAccessControl;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    int _cardReaderMode;
    int _thermalMode;
    int _thermalAccess;
    int _thermalAlarm;
    String _threshold;
    String _thermalUnit;
    String _correctionValue;

    final String THRESHOLD_CELSIUS_LIMIT = "  (15 - 50)";
    final String THRESHOLD_FAHRENHEIT_LIMIT = "  (60 - 120)";
    final int THRESHOLD_CELSIUS_MAX_VALUE = 50;
    final int THRESHOLD_CELSIUS_MIN_VALUE = 15;
    final int THRESHOLD_FAHRENHEIT_MAX_VALUE = 120;
    final int THRESHOLD_FAHRENHEIT_MIN_VALUE = 60;
    final int CORRECTION_MAX_VALUE = 20;
    final int CORRECTION_MIN_VALUE = -20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_external_device, container, false);
        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref.edit();
        initView(rootView);
        if(_thermalMode==1)
            checkAndGetThermalInfo();

//        registerCallbackListener();
        return rootView;
    }

    private void initView(View root){
        _linearCardReader = (LinearLayout)root.findViewById(R.id.linear_card_reader);
        _linearThermal = (LinearLayout)root.findViewById(R.id.linear_thermal);
        _linearThermalDetail = (LinearLayout)root.findViewById(R.id.linear_thermal_detail);
        _linearThermalUnit = (LinearLayout)root.findViewById(R.id.linear_thermal_unit);
        _linearThermalAlarm = (LinearLayout)root.findViewById(R.id.linear_temp_alarm);
        _linearThermalAccess = (LinearLayout)root.findViewById(R.id.linear_temp_access);
        _linearProgress = (LinearLayout)root.findViewById(R.id.linear_progress);

        _linearThermalThreshold = (LinearLayout)root.findViewById(R.id.linear_thermal_threshold);
        _linearThermalCorrection = (LinearLayout)root.findViewById(R.id.linear_thermal_correction);

        _schCardReader = (Switch)root.findViewById(R.id.sch_card_reader);
        _schThermal = (Switch)root.findViewById(R.id.sch_thermal);

        _txtCardOnOff = (TextView) root.findViewById(R.id.txt_card_onoff);
        _txtCardType = (TextView) root.findViewById(R.id.txt_card_type);
        _txtThermalOnOff = (TextView) root.findViewById(R.id.txt_thermal_onoff);
        _txtTempThreshold = (TextView)root.findViewById(R.id.txt_temp_threshold);
        _txtThermalUnit = (TextView)root.findViewById(R.id.txt_thermal_unit);
        _txtThermalCorrection = (TextView)root.findViewById(R.id.txt_thermal_correction);
        _txtThresholdLimit = (TextView)root.findViewById(R.id.txt_threshold_limit);

        _ckTempAlarm = (AppCompatCheckBox)root.findViewById(R.id.ck_temp_alarm);
        _ckAccessControl= (AppCompatCheckBox)root.findViewById(R.id.ck_access_control);

        _linearThermal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_schThermal.isChecked()){
                    if(_linearThermalDetail.getVisibility()==View.GONE)
                        _linearThermalDetail.setVisibility(View.VISIBLE);
                    else if(_linearThermalDetail.getVisibility()==View.VISIBLE)
                        _linearThermalDetail.setVisibility(View.GONE);
                }
            }
        });

        _linearCardReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _schCardReader.setChecked(!_schCardReader.isChecked());
            }
        });
        _cardReaderMode = pref.getInt(getString(R.string.prefer_key_cardreader_mode), 1);
        _thermalMode = pref.getInt(getString(R.string.prefer_key_thermal_mode), 0);
        _thermalAccess = pref.getInt(getString(R.string.prefer_key_thermal_access), 0);
        _thermalAlarm = pref.getInt(getString(R.string.prefer_key_thermal_alarm), 0);
        _thermalUnit = pref.getString(getString(R.string.prefer_key_thermal_unit), MessageKeyValue.THERMAL_UNIT_C);
        _threshold = pref.getString(getString(R.string.prefer_key_thermal_threshold), "37.5");
        _correctionValue = pref.getString(getString(R.string.prefer_key_thermal_correction), "0");

        if(_cardReaderMode == 1) {
            _schCardReader.setChecked(true);
            cardReaderView(true);
        }else {
            _schCardReader.setChecked(false);
            cardReaderView(false);
        }

        if(_thermalMode == 1) {
            _schThermal.setChecked(true);
            thermalView(true);
        }else {
            _schThermal.setChecked(false);
            thermalView(false);
        }

        _schCardReader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //cardReaderView(true);
                    //getCardReaderSetting();
                    checkAndGetCardReaderInfo();
                }else{
                    cardReaderView(false);
                }

                JSONObject jCardReaderSetting = new JSONObject();
                try {
                    jCardReaderSetting.put(MessageKeyValue.CARD_READER_ENABLE, isChecked);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                IT100.setCardReaderSetting(jCardReaderSetting.toString());
            }
        });

        _schThermal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked) {
                     checkAndGetThermalInfo();
                 }else
                     thermalView(false);//thermalView(isChecked);
             }
         });

        _linearThermalUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTemperType();
            }
        });

        _linearThermalThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputThreshold();
            }
        });

        _linearThermalCorrection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCorrectionValue();
            }
        });

        _ckTempAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    editor.putInt(getString(R.string.prefer_key_thermal_alarm), 1);
                else
                    editor.putInt(getString(R.string.prefer_key_thermal_alarm), 0);
                editor.commit();
            }
        });

        _ckAccessControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    editor.putInt(getString(R.string.prefer_key_thermal_access), 1);
                else
                    editor.putInt(getString(R.string.prefer_key_thermal_access), 0);
                editor.commit();
            }
        });

        if(_thermalAccess==1)
            _ckAccessControl.setChecked(true);
        else
            _ckAccessControl.setChecked(false);

        if(_thermalAlarm==1)
            _ckTempAlarm.setChecked(true);
        else
            _ckTempAlarm.setChecked(false);

        _linearThermalAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ckAccessControl.setChecked(!_ckAccessControl.isChecked());
            }
        });

        _linearThermalAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ckTempAlarm.setChecked(!_ckTempAlarm.isChecked());
            }
        });
        getCardReaderSetting();
    }

//    private LocalActivateBroadcastReceiver mReceiver = new LocalActivateBroadcastReceiver();
//    public class LocalActivateBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(action.equals(BROADCAST_SERVICE_CHANGE_THERMAL)){
//                Logger.d("[ExternalDeviceFragment] onReceive @@  BROADCAST_SERVICE_CHANGE_THERMAL");


//                _thermalMode = pref.getInt(getString(R.string.prefer_key_thermal_mode), 0);
//                _thermalUnit = pref.getString(getString(R.string.prefer_key_thermal_unit), MessageKeyValue.THERMAL_UNIT_C);
//                _threshold = pref.getString(getString(R.string.prefer_key_thermal_threshold), "37.5");
//                _correctionValue = pref.getString(getString(R.string.prefer_key_thermal_correction), "0");
//
//                if(_thermalMode == 1) {
//                    _schThermal.setChecked(true);
//                    thermalView(true);
//                }else {
//                    _schThermal.setChecked(false);
//                    thermalView(false);
//                }
//
//                if(_thermalUnit.equals(MessageKeyValue.THERMAL_UNIT_C)) {
//                    _txtThermalUnit.setText(getString(R.string.temperature_c_unit));
//                    _thermalUnit = MessageKeyValue.THERMAL_UNIT_C;
//                }else {
//                    _txtThermalUnit.setText(getString(R.string.temperature_f_unit));
//                    _thermalUnit = MessageKeyValue.THERMAL_UNIT_F;
//                }
//                _txtTempThreshold.setText( _threshold);
//                _txtThermalCorrection.setText(_correctionValue);

//            }
//        }
//    }

//    private void registerCallbackListener() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BROADCAST_SERVICE_CHANGE_THERMAL);
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);

        editor.putInt(getString(R.string.prefer_key_cardreader_mode), _cardReaderMode);
        editor.putInt(getString(R.string.prefer_key_thermal_mode), _thermalMode);
        editor.putString(getString(R.string.prefer_key_thermal_unit), _thermalUnit);
        editor.putString(getString(R.string.prefer_key_thermal_threshold),_threshold);
        editor.putString(getString(R.string.prefer_key_thermal_correction), _correctionValue);
        editor.commit();
        JSONObject jThermalInfo = new JSONObject();
        try {
            jThermalInfo.put(MessageKeyValue.THERMAL_ENABLE, _thermalMode==1? true:false);
            jThermalInfo.put(MessageKeyValue.THERMAL_UNIT, _thermalUnit);
            jThermalInfo.put(MessageKeyValue.THERMAL_THRESHOLD, Double.valueOf(_threshold));
            jThermalInfo.put(MessageKeyValue.THERMAL_CORRECTION, Double.valueOf(_correctionValue));
            jThermalInfo.put(MessageKeyValue.THERMAL_ACCESS_CONTROL, _ckAccessControl.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setThermalJSONSetting(jThermalInfo.toString());

        JSONObject jCardReaderSetting = new JSONObject();
        try {
            jCardReaderSetting.put(MessageKeyValue.CARD_READER_ENABLE, _cardReaderMode==1? true:false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setCardReaderSetting(jCardReaderSetting.toString());
    }

    boolean isThermalConnected = true;
    private void checkAndGetThermalInfo(){
        _linearProgress.setVisibility(View.VISIBLE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                IT100.checkThermalConnection(new CheckThermalConnectionCallback() {
                    @Override
                    public void onResult(final int i) {
                        if(getActivity()!=null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _linearProgress.setVisibility(View.GONE);
                                    if(i!=0)
                                        isThermalConnected = false;
                                    else
                                        isThermalConnected = true;

                                    if(isThermalConnected){
                                        _schThermal.setChecked(true);
                                        thermalView(true);
                                        getThermalInfo();
                                    }else{
                                        showErrorMsg(R.string.error_thermal_not_find);

                                        _schThermal.setChecked(false);
                                        thermalView(false);
                                    }

                                }
                            });
                        }
                    }
                });
            }
        }, 500);
    }

    private void checkAndGetCardReaderInfo(){
        _schCardReader.setEnabled(false);
        _linearCardReader.setClickable(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                _schCardReader.setEnabled(false);
                _linearCardReader.setClickable(false);
                IT100.checkCardReaderConnection(new CheckCardReaderConnectionCallback() {
                    @Override
                    public void onResult(final int returnCode, final String jparams) {
                        if(getActivity()!=null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _schCardReader.setEnabled(true);
                                    _linearCardReader.setClickable(true);

                                    if(returnCode==0){
                                        _schCardReader.setChecked(true);
                                        cardReaderView(true);
                                        getCardReaderSetting();

                                    }else{
                                        showErrorMsg(R.string.error_card_reader_not_find);
                                        _schCardReader.setChecked(false);
                                        cardReaderView(false);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }, 500);
    }

    private void showErrorMsg(int resid){
        new BasicAlertDialog(getActivity())
                .setTitle(getString(R.string.dlgtitle_notice))
                .setMessage(resid)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .show();
    }


    private void thermalView(boolean isOn){
        if(!isOn){
            _txtThermalOnOff.setText(getResources().getString(R.string.off));
            _txtThermalOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
            _linearThermalDetail.setVisibility(View.GONE);
            _thermalMode = 0;
        }else{
            _txtThermalOnOff.setText(getResources().getString(R.string.on));
            _txtThermalOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            _linearThermalDetail.setVisibility(View.VISIBLE);
            _thermalMode= 1;
        }
    }

    private void cardReaderView(boolean isOn){
        if(!isOn){
            _txtCardOnOff.setText(getResources().getString(R.string.off));
            _txtCardOnOff.setTextColor(getResources().getColor(R.color.disableText, null));
            _txtCardType.setText("");
            _cardReaderMode = 0;
        }else{
            _txtCardOnOff.setText(getResources().getString(R.string.on));
            //_txtCardOnOff.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            _txtCardOnOff.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            _cardReaderMode= 1;
        }
    }

    private void getThermalInfo() {
        IT100.getThermalJSONSetting(new ThermalSettingJSONCallback() {
            @Override
            public void onResult(final JSONObject jThermalInfo) {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _thermalUnit = jThermalInfo.optString(MessageKeyValue.THERMAL_UNIT);
                            _threshold = String.valueOf(jThermalInfo.optDouble(MessageKeyValue.THERMAL_THRESHOLD));
                            _correctionValue = String.valueOf(jThermalInfo.optDouble(MessageKeyValue.THERMAL_CORRECTION)); //temp
                            _ckAccessControl.setChecked(jThermalInfo.optBoolean(MessageKeyValue.THERMAL_ACCESS_CONTROL, false));

                            if (_thermalUnit.equals(MessageKeyValue.THERMAL_UNIT_C)) {
                                _txtThermalUnit.setText(getString(R.string.temperature_c_unit));
                                _thermalUnit = MessageKeyValue.THERMAL_UNIT_C;
                                _txtThresholdLimit.setText(THRESHOLD_CELSIUS_LIMIT);
                            } else {
                                _txtThermalUnit.setText(getString(R.string.temperature_f_unit));
                                _thermalUnit = MessageKeyValue.THERMAL_UNIT_F;
                                _txtThresholdLimit.setText(THRESHOLD_FAHRENHEIT_LIMIT);
                            }
                            _txtTempThreshold.setText(_threshold);
                            _txtThermalCorrection.setText(_correctionValue);
                        }
                    });
                }
            }
        });
    }

    private void getCardReaderSetting() {
        IT100.getCardReaderSetting(new CardReaderSettingCallback() {
            @Override
            public void onResult(final JSONObject jsonObject) {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _schCardReader.setChecked(jsonObject.optBoolean(MessageKeyValue.CARD_READER_ENABLE));
                            cardReaderView(jsonObject.optBoolean(MessageKeyValue.CARD_READER_ENABLE));
                            if (jsonObject.optBoolean(MessageKeyValue.CARD_READER_ENABLE))
                                _txtCardType.setText(jsonObject.optString(MessageKeyValue.CARD_READER_DEVICE_TYPE, ""));
                            else
                                _txtCardType.setText("");
                        }
                    });
                }
            }
        });
    }

    private void changeThermalUnit(final String unit){

        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Double dThreshold = Double.valueOf(_threshold);
                    Double dCorrection  = Double.valueOf(_correctionValue);
                    if(unit.equals(MessageKeyValue.THERMAL_UNIT_C)){
                        dThreshold = (dThreshold -32)/1.8;
                        dCorrection = dCorrection/1.8;

                    }else if(unit.equals(MessageKeyValue.THERMAL_UNIT_F)){
                        dThreshold = dThreshold*1.8 + 32;
                        dCorrection = dCorrection *1.8;
                    }

                    String sThreshold = String.format("%.2f", dThreshold);
                    _txtTempThreshold.setText(sThreshold);
                    _threshold = sThreshold;

                    _txtThermalCorrection.setText(String.valueOf(dCorrection));
                    _correctionValue = String.valueOf(dCorrection);
                }
            });

        }
    }

    int checkThermalUnitIndex = 0;
    private void selectTemperType(){
        String[] typeList = getActivity().getResources().getStringArray(R.array.temperature_type);

        if(_thermalUnit.equals(MessageKeyValue.THERMAL_UNIT_C))
            checkThermalUnitIndex =0;
        else
            checkThermalUnitIndex =1;

        AlertDialog inputDialog;
        inputDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.temperature_unit)
                .setSingleChoiceItems(typeList, checkThermalUnitIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if(checkThermalUnitIndex == position)
                            return;

                        if(position ==0){
                            _thermalUnit = MessageKeyValue.THERMAL_UNIT_C;
                            _txtThermalUnit.setText(getString(R.string.temperature_c_unit));
                            _txtThresholdLimit.setText(THRESHOLD_CELSIUS_LIMIT.toString());
                        }else if(position ==1){
                            _thermalUnit = MessageKeyValue.THERMAL_UNIT_F;
                            _txtThermalUnit.setText(getString(R.string.temperature_f_unit));
                            _txtThresholdLimit.setText(THRESHOLD_FAHRENHEIT_LIMIT);
                        }
                    }
                })

                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(_thermalUnit.equals(MessageKeyValue.THERMAL_UNIT_C) && checkThermalUnitIndex==0){

                        }else if(_thermalUnit.equals(MessageKeyValue.THERMAL_UNIT_F) && checkThermalUnitIndex==1){

                        }else
                            changeThermalUnit(_thermalUnit);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        inputDialog.show();
    }

    private void inputThreshold() {
        LinearLayout inputLayout = (LinearLayout) View.inflate(getContext(), R.layout.dialog_text_input, null);

        TextView tv = (TextView) inputLayout.findViewById(R.id.textview);
        if (tv != null)
            tv.setVisibility(View.GONE);

        EditText editText = (EditText) inputLayout.findViewById(R.id.edittext);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog inputDialog;
        inputDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.threshold_value)
                .setCancelable(false)
                .setView(inputLayout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText editText = (EditText)((AlertDialog) dialog).findViewById(R.id.edittext);
                        String value = editText.getText().toString().trim();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        if(!value.equals("")) {
                            value = checkThresholdLimit(value);
                            editor.putString(getString(R.string.prefer_key_thermal_threshold),value);
                            editor.commit();

                            _txtTempThreshold.setText(value);
                            _threshold = value;
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

    private void inputCorrectionValue() {
        LinearLayout inputLayout = (LinearLayout) View.inflate(getContext(), R.layout.dialog_text_input, null);

        TextView tv = (TextView) inputLayout.findViewById(R.id.textview);
        if (tv != null)
            tv.setVisibility(View.GONE);

        EditText editText = (EditText) inputLayout.findViewById(R.id.edittext);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
            |InputType.TYPE_NUMBER_FLAG_SIGNED);

        AlertDialog inputDialog;
        inputDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.thermal_correction)
                .setCancelable(false)
                .setView(inputLayout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText editText = (EditText)((AlertDialog) dialog).findViewById(R.id.edittext);
                        String correctionValue = editText.getText().toString().trim();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        if(!correctionValue.equals("")) {
                            correctionValue = checkCorrectionLimit(correctionValue);
                            editor.putString(getString(R.string.prefer_key_thermal_correction),correctionValue);
                            editor.commit();
                            _txtThermalCorrection.setText(correctionValue);
                            _correctionValue = correctionValue;
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
    private String checkThresholdLimit(String value){
        double iValue = Double.parseDouble(value);
        if(_thermalUnit == MessageKeyValue.THERMAL_UNIT_C){
            if(iValue < THRESHOLD_CELSIUS_MIN_VALUE)
                value = String.valueOf(THRESHOLD_CELSIUS_MIN_VALUE);
            else if(iValue > THRESHOLD_CELSIUS_MAX_VALUE)
                value = String.valueOf(THRESHOLD_CELSIUS_MAX_VALUE);
        }else if(_thermalUnit == MessageKeyValue.THERMAL_UNIT_F){
            if(iValue < THRESHOLD_FAHRENHEIT_MIN_VALUE)
                value = String.valueOf(THRESHOLD_FAHRENHEIT_MIN_VALUE);
            else if(iValue > THRESHOLD_FAHRENHEIT_MAX_VALUE)
                value = String.valueOf(THRESHOLD_FAHRENHEIT_MAX_VALUE);
        }
        return value;
    }

    private String checkCorrectionLimit(String value){
        double iValue = Double.parseDouble(value);

        if(iValue < CORRECTION_MIN_VALUE)
            value = String.valueOf(CORRECTION_MIN_VALUE);
        else if(iValue > CORRECTION_MAX_VALUE)
            value = String.valueOf(CORRECTION_MAX_VALUE);

        return value;
    }
}