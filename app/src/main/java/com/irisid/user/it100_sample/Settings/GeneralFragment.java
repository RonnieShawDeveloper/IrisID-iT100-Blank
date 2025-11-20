package com.irisid.user.it100_sample.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.DeviceInfoCallback;
import com.irisid.it100.callback.SyslogStatusCallback;
import com.irisid.it100.callback.UdpServerStatusCallback;
import com.irisid.it100.data.DeviceInfo;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.BuildConfig;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GeneralFragment extends Fragment
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    FrameLayout rootLayout;
    LinearLayout linearDeviceName;
    LinearLayout linearSave;
    LinearLayout linearUpdate;
    LinearLayout linearReboot;
    LinearLayout linearReset;
    LinearLayout linearEthernetProxy;

    TextView txtSerialNum;
    TextView txtKernelVer;
    TextView txtSWVer;
    TextView txtHwVer;
    TextView txtAndroidVer;
    TextView txtDeviceType;
    TextView txtBuildType;

    TextInputLayout tiDeviceName;
    TextInputEditText editDeviceName;

    String _deviceName;
    String _serialNum;
    String _macAddr;
    String _kernelVer;
    String _swVer;
    String _hwVer;
    String _androidVer;
    String _deviceType;

    //Send Log to UDP server
    EditText editIP;
    EditText editPort;
    AppCompatSpinner spinLogLevel;
    ArrayAdapter logLevelAdapter;
    Button btnStartStop;
    String _sendLogIP;
    String _sendLogPort;
    String _UDPServer;
    int    _sendLogLevel;
    boolean _sendLogIsStart;

    int ckCount = 0;
    boolean showSave = false;
    String[] logLevelList = {"Verbose","Debug", "Info", "Warning", "Error", "Fatal", "Silent"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        //Logger.e("TAG >> ", "GeneralFragment");

        rootLayout = (FrameLayout)rootView.findViewById(R.id.rootview);
        linearDeviceName = (LinearLayout)rootView.findViewById(R.id.linear_device_name);
        linearSave = (LinearLayout)rootView.findViewById(R.id.linear_save);
        linearUpdate = (LinearLayout) rootView.findViewById(R.id.linear_update);
        linearReboot = (LinearLayout) rootView.findViewById(R.id.linear_reboot);
        linearReset = (LinearLayout) rootView.findViewById(R.id.linear_factory_reset);
        linearEthernetProxy  = (LinearLayout) rootView.findViewById(R.id.linear_ethernet_proxy);

        tiDeviceName = (TextInputLayout)rootView.findViewById(R.id.textField_device_name);
        editDeviceName = (TextInputEditText)rootView.findViewById(R.id.edit_device_name);
        txtDeviceType   = (TextView)rootView.findViewById(R.id.layout_11).findViewById(R.id.txt_content);
        txtSerialNum    = (TextView)rootView.findViewById(R.id.layout_12).findViewById(R.id.txt_content);
        txtHwVer        = (TextView)rootView.findViewById(R.id.layout_13).findViewById(R.id.txt_content);
        txtSWVer       = (TextView)rootView.findViewById(R.id.layout_14).findViewById(R.id.txt_content);
        txtKernelVer    = (TextView)rootView.findViewById(R.id.layout_21).findViewById(R.id.txt_content);
        txtAndroidVer   = (TextView)rootView.findViewById(R.id.layout_22).findViewById(R.id.txt_content);
        txtBuildType    = (TextView)rootView.findViewById(R.id.layout_23).findViewById(R.id.txt_content);

        //Send Log to UDP server
        editIP = rootView.findViewById(R.id.ip_address_edt);
        editPort = rootView.findViewById(R.id.port_edt);
        btnStartStop = rootView.findViewById(R.id.btn_start_stop);
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!_sendLogIsStart) {
                    IT100.startSyslog(editIP.getText().toString(), editPort.getText().toString(),
                            spinLogLevel.getSelectedItemPosition(), false);

                    Logger.d("loglevel selected position is "+ spinLogLevel.getSelectedItemPosition());

                    editor.putString(getString(R.string.prefer_key_log_udp_ip), editIP.getText().toString());
                    editor.putString(getString(R.string.prefer_key_log_udp_port), editPort.getText().toString());
                    editor.putInt(getString(R.string.prefer_key_log_level), spinLogLevel.getSelectedItemPosition());
                    editor.putBoolean(getString(R.string.prefer_key_log_start_server), false);
                    editor.commit();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnStartStop.setText("stop");
                            }
                        });
                    }
                }else{
                    IT100.stopSyslog();
                    editor.putBoolean(getString(R.string.prefer_key_log_start_server), false);
                    editor.commit();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnStartStop.setText("start");
                            }
                        });
                    }
                }
                _sendLogIsStart = !_sendLogIsStart;
            }
        });

        spinLogLevel = rootView.findViewById(R.id.log_level_spin);
        logLevelAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, logLevelList);
        spinLogLevel.setAdapter(logLevelAdapter);
        spinLogLevel.setSelection(_sendLogLevel);
        spinLogLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(Color.parseColor("#dddddd"));
                ((TextView)view).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        linearEthernetProxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogHostPort();
            }
        });
        //set title
        ((TextView)rootView.findViewById(R.id.layout_11).findViewById(R.id.txt_title))
                .setText(getResources().getString(R.string.device_type));
        ((TextView)rootView.findViewById(R.id.layout_12).findViewById(R.id.txt_title))
                .setText(getResources().getString(R.string.serial_num));
        ((TextView)rootView.findViewById(R.id.layout_13).findViewById(R.id.txt_title))
                .setText(getResources().getString(R.string.hardware_version));
        ((TextView)rootView.findViewById(R.id.layout_14).findViewById(R.id.txt_title))
                .setText(getResources().getString(R.string.software_version));
        ((TextView)rootView.findViewById(R.id.layout_21).findViewById(R.id.txt_title))
                .setText(getResources().getString(R.string.kernel_version));
        ((TextView)rootView.findViewById(R.id.layout_22).findViewById(R.id.txt_title))
                .setText(getResources().getString(R.string.android_version));

        ((TextView)rootView.findViewById(R.id.layout_23).findViewById(R.id.txt_title))
                .setText(getResources().getString(R.string.build_type));

        ((TextView)rootView.findViewById(R.id.txt_appver)).setText("v"+ BuildConfig.VERSION_NAME);
        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), Context.MODE_PRIVATE);
        editor = pref.edit();

        _deviceName = pref.getString(getString(R.string.prefer_key_device_name), "");
        _serialNum = pref.getString(getString(R.string.prefer_key_serial_num), "");
        _macAddr = pref.getString(getString(R.string.prefer_key_mac_addr), "");
        _kernelVer = pref.getString(getString(R.string.prefer_key_ver_kernel), "");
        _swVer = pref.getString(getString(R.string.prefer_key_ver_app), "");
        _hwVer =  pref.getString(getString(R.string.prefer_key_ver_hw), "");
        _androidVer = pref.getString(getString(R.string.prefer_key_ver_android), "");
        _deviceType = pref.getString(getString(R.string.prefer_key_device_type), "");

        _sendLogIP      = pref.getString(getString(R.string.prefer_key_log_udp_ip), "");
        _sendLogPort    = pref.getString(getString(R.string.prefer_key_log_udp_port), "");
        _sendLogLevel   = pref.getInt(getString(R.string.prefer_key_log_level), 0);
        if(_sendLogLevel>logLevelList.length-1)
            _sendLogLevel = 0;
        _sendLogIsStart = pref.getBoolean(getString(R.string.prefer_key_log_start_server), false);

        refreshUI();

        long rtnDeviceInfo = IT100.getDeviceInfo(new DeviceInfoCallback() {

            @Override
            public void deviceInfoResult(DeviceInfo deviceInfo) {

                _deviceName = deviceInfo.deviceName;
                IrisApplication._deviceName = _deviceName;
                _serialNum = deviceInfo.serialNumber;
                //_serialNum = modifySerialNumber(deviceInfo.serialNumber);

                _macAddr = deviceInfo.macAddress;
                _kernelVer = deviceInfo.kernelVersion;
                _swVer = deviceInfo.softwareVersion;
                _hwVer = deviceInfo.hardwareVersion;
                _androidVer = deviceInfo.androidVersion;
                _deviceType = deviceInfo.deviceType;

                editor.putString(getString(R.string.prefer_key_device_name), _deviceName);
                editor.putString(getString(R.string.prefer_key_serial_num), _serialNum);
                editor.putString(getString(R.string.prefer_key_mac_addr), _macAddr);
                editor.putString(getString(R.string.prefer_key_ver_kernel), _kernelVer);
                editor.putString(getString(R.string.prefer_key_ver_app), _swVer);
                editor.putString(getString(R.string.prefer_key_ver_hw), _hwVer);
                editor.putString(getString(R.string.prefer_key_ver_android), _androidVer);
                editor.putString(getString(R.string.prefer_key_device_type), _deviceType);
                editor.commit();

                refreshUI();
            }
        });


        if ( rtnDeviceInfo  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnDeviceInfo  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnDeviceInfo  == MessageType.ID_RTN_FAIL){}// fail

        IT100.getSyslogStatus(new SyslogStatusCallback() {
            @Override
            public void onResult(JSONObject jsonObject) {

                try {
                    Logger.d("getLog status "+ jsonObject.toString(4) );

                    _sendLogIP      = jsonObject.optString(MessageKeyValue.LOGSERVICE_UDP_IP, "");
                    _sendLogPort    = jsonObject.optString(MessageKeyValue.LOGSERVICE_UDP_PORT, "");
                    _UDPServer = jsonObject.optString(MessageKeyValue.LOGSERVICE_UDP_SERVER_URL, "");
                    _sendLogLevel   = jsonObject.optInt(MessageKeyValue.LOGSERVICE_LOG_LEVEL, 0);
                    if(_sendLogLevel>logLevelList.length-1)
                        _sendLogLevel = 0;
                    _sendLogIsStart = ((jsonObject.optString(MessageKeyValue.LOGSERVICE_STATUS,
                            MessageKeyValue.LOGSERVICE_STATUS_STOPPED)).equals(
                                    MessageKeyValue.LOGSERVICE_STATUS_RUNNING))? true : false;

                    editor.putString(getString(R.string.prefer_key_log_udp_ip), _sendLogIP);
                    editor.putString(getString(R.string.prefer_key_log_udp_port), _sendLogPort);
                    editor.putInt(getString(R.string.prefer_key_log_level), _sendLogLevel);
                    editor.putBoolean(getString(R.string.prefer_key_log_start_server), _sendLogIsStart);
                    editor.commit();

                    refreshUI();

                } catch (JSONException e) {
                    e.printStackTrace();
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


                } else {
                    //ok now we know the keyboard is down...
                    rootLayout.setPadding(0,0,0,0);

                    if(showSave)
                        showNetworkSettingSave();
                }
            }
        });

        linearReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BasicAlertDialog(getContext())
                        .setTitle(R.string.reboot)
                        .setMessage(R.string.reboot_dialog)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IT100.reboot();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        linearReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BasicAlertDialog(getContext())
                        .setTitle(R.string.factory_reset)
                        .setMessage(R.string.factory_reset_dialog)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IT100.factoryReset();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        try {
            linearUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("IT100.SETTINGS.UPDATE");
                    startActivity(intent);
                }
            });
        }catch (Exception e){

        }

        (rootView.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNetworkSettingSave();
                editDeviceName.setFocusable(false);
                editDeviceName.getBackground().mutate().setColorFilter(null);
                showSave = false;
                tiDeviceName.setCounterEnabled(false);
                tiDeviceName.setErrorEnabled(false);

            }
        });

        (rootView.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDeviceName.getText().toString().isEmpty()){
                    tiDeviceName.setErrorEnabled(true);
                    tiDeviceName.setError(getResources().getText(R.string.general_error_devicename_empty));
                    editDeviceName.getBackground().mutate().setColorFilter(
                            getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                }else if(editDeviceName.getText().length()>30) {
                    tiDeviceName.setErrorEnabled(true);
                    tiDeviceName.setError(getResources().getText(R.string.general_error_devicename_too_long));
                    editDeviceName.getBackground().mutate().setColorFilter(
                            getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                }else {

                    hideNetworkSettingSave();
                    editDeviceName.setFocusable(false);
                    editDeviceName.getBackground().mutate().setColorFilter(null);
                    tiDeviceName.setCounterEnabled(false);
                    tiDeviceName.setErrorEnabled(false);

                    long rtnDeviceName = 0 ;
                    rtnDeviceName = IT100.setDeviceName(editDeviceName.getText().toString());

                    if ( rtnDeviceName  == MessageType.ID_RTN_SUCCESS){   }// success
                    else if( rtnDeviceName  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                    else if( rtnDeviceName  == MessageType.ID_RTN_FAIL){}// fail

                    _deviceName = editDeviceName.getText().toString();
                    showSave = false;
                    refreshUI();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        editDeviceName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSave = true;
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                editDeviceName.getBackground().mutate().setColorFilter(null);
                tiDeviceName.setCounterEnabled(true);
                tiDeviceName.setErrorEnabled(false);
                return false;
            }
        });
    }

    private void refreshUI() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    editDeviceName.setText(_deviceName.toString());
                    txtDeviceType.setText(_deviceType);
                    txtSerialNum.setText(_serialNum);
                    txtKernelVer.setText(_kernelVer);
                    txtSWVer.setText(_swVer);
                    txtHwVer.setText(_hwVer);
                    txtAndroidVer.setText(_androidVer);
                    txtBuildType.setText(getBuildType());

                    editIP.setText(_sendLogIP);
                    editPort.setText(_sendLogPort);
                    if(_sendLogLevel<logLevelList.length)
                        spinLogLevel.setSelection(_sendLogLevel);
                    if(_sendLogIsStart)
                        btnStartStop.setText("STOP");
                    else
                        btnStartStop.setText("START");
                }
            });
        }
    }

    private String  modifySerialNumber(String sn) {

        String productCode = "";
        String mm = "";
        String rot = "";
        String yy = "";
        String number = "";

        productCode = sn.substring(8, 10);
        mm = sn.substring(6,8);
        rot = sn.substring(10, 11);
        yy = sn.substring(4,6);
        number = sn.substring(11, 16);

        return productCode+mm+rot+yy+number;
    }


    private String getBuildType() {
        String line = "";
        Process ifc = null;
        try {
            ifc = Runtime.getRuntime().exec("getprop ro.device.type");
            BufferedReader bis = new BufferedReader(new InputStreamReader(ifc.getInputStream()));
            line = bis.readLine();
        } catch (IOException e) {
        }
        ifc.destroy();

        if(line.equals("0"))
            line = "Development";
        else if(line.equals("1"))
            line = "Production";
        else{
            line = "---";
        }

        return line;
    }

    private void showNetworkSettingSave(){
        linearSave.setVisibility(View.VISIBLE);
    }

    private void hideNetworkSettingSave(){
        linearSave.setVisibility(View.INVISIBLE);
    }

    String _statusMsg = "";
    private void showDialogHostPort() {
        LayoutInflater inflater = getLayoutInflater();
        View content =  inflater.inflate(R.layout.dialog_host_port, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter host(IP), port");
        builder.setView(content);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                TextInputEditText editHost = ((AlertDialog)dialog).findViewById(R.id.te_host);
                TextInputEditText editPort = ((AlertDialog)dialog).findViewById(R.id.te_port);

                IT100.setEthernetProxy(editHost.getText().toString(), editPort.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}

