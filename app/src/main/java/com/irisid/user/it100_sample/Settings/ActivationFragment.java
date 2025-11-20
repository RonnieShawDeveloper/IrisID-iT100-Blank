package com.irisid.user.it100_sample.Settings;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.irisid.it100.IT100;
import com.irisid.it100.callback.ActivationStatusCallback;
import com.irisid.it100.callback.CheckInCallback;
import com.irisid.it100.callback.ItmsIpAddressCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Activity.ScanQRCodeActivity;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_DEVICE_ACTIVATION;


public class ActivationFragment extends Fragment
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    FrameLayout rootLayout;
    ScrollView scroll_activate;

    LinearLayout linear_itms;
    LinearLayout linear_standalone;
    LinearLayout linear_itms_cloud;
    LinearLayout linear_ip;
    LinearLayout linear_find_btn;
    LinearLayout linear_deactive;
    LinearLayout linear_pass_sitekey_dedit;
    LinearLayout linear_pass_apikey_dedit;

    EditText editText_ip;
    EditText editText_port;
    Button btn_find_iTMS;
    Button btn_check;
    Button btn_activate;
    Button btn_deactive;

    RadioGroup activation_radio_group;
    AppCompatRadioButton itms_radio;
    AppCompatRadioButton standalone_radio;
    AppCompatRadioButton itms_cloud_radio;

    TextInputLayout passSite_layout;
    TextInputLayout passApi_layout;
    TextInputEditText passSite_edt;
    TextInputEditText passApi_edt;
    TextInputEditText passSite_dedt;
    TextInputEditText passApi_dedt;
    TextInputEditText site_dedt;
    TextInputEditText api_dedt;

    //TextView serial_num;
    TextView server_url;
    TextView result_txt;
    TextView txt_title;

    RadioButton activation_manual;
    RadioButton activation_auto;

    String tempDiscoverITMS;
    String tempActivateType;
    ImageView img_qr;
    ImageView imgSitekeyInfo;
    ImageView imgApikeyInfo;

    //iTMS cloud
    RadioButton icloud_qr;
    RadioButton icloud_text;
    Button btn_check_cloud;
    TextView result_txt_cloud;
    EditText editText_code;
    EditText editText_ITMSCloudAddr;
    ImageView img_qr_code;

    final int REQUEST_CODE_QR = 800;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activationinfo, container, false);

        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref.edit();

        rootLayout = (FrameLayout) rootView.findViewById(R.id.rootview);
        //Layout Deactivate
        linear_deactive = (LinearLayout)rootView.findViewById(R.id.linear_deactivate);
        btn_deactive = (Button)rootView.findViewById(R.id.btn_deactivate);

        linear_pass_sitekey_dedit = (LinearLayout) rootView.findViewById(R.id.linear_pass_sitekey_dedit);
        linear_pass_apikey_dedit = (LinearLayout) rootView.findViewById(R.id.linear_pass_apikey_dedit);
        passSite_dedt= (TextInputEditText) rootView.findViewById(R.id.pass_sitekey_dedit);
        passApi_dedt = (TextInputEditText) rootView.findViewById(R.id.pass_apikey_dedit);
        site_dedt = (TextInputEditText) rootView.findViewById(R.id.sitekey_dedit);
        api_dedt = (TextInputEditText) rootView.findViewById(R.id.apikey_dedit);
        //serial_num = (TextView) rootView.findViewById(R.id.serial_num);
        server_url = (TextView) rootView.findViewById(R.id.server_url);
        txt_title = (TextView) rootView.findViewById(R.id.txt_title);

        //Layout Activate
        scroll_activate = (ScrollView)rootView.findViewById(R.id.scroll_active);
        linear_itms = (LinearLayout)rootView.findViewById(R.id.linear_itms);
        linear_standalone = (LinearLayout)rootView.findViewById(R.id.linear_standlone);
        linear_itms_cloud = rootView.findViewById(R.id.linear_itms_cloud);

        //whether itms or standalone
        activation_radio_group = (RadioGroup) rootView.findViewById(R.id.activation_radio_group);
        itms_radio = (AppCompatRadioButton) rootView.findViewById(R.id.activation_itms);
        standalone_radio = (AppCompatRadioButton) rootView.findViewById(R.id.activation_alone);
        itms_cloud_radio = rootView.findViewById(R.id.activation_itms_cloud);

        itms_radio.setChecked(true);
        linear_itms.setVisibility(View.VISIBLE);
        linear_standalone.setVisibility(View.GONE);
        linear_itms_cloud.setVisibility(View.GONE);

        //Standalone Method
        passSite_layout = (TextInputLayout) rootView.findViewById(R.id.textField_sitekey);
        passApi_layout = (TextInputLayout) rootView.findViewById(R.id.textField_apikey);
        passSite_edt = (TextInputEditText) rootView.findViewById(R.id.sitekey_edt);
        passApi_edt = (TextInputEditText) rootView.findViewById(R.id.apikey_edt);

        btn_activate = (Button) rootView.findViewById(R.id.btn_activate);

        //iTMS Method
        linear_ip = (LinearLayout)rootView.findViewById(R.id.linear_ip);
        linear_find_btn = (LinearLayout)rootView.findViewById(R.id.linear_find_btn);

        editText_ip = (EditText)rootView.findViewById(R.id.ip_address_edt);
        editText_port = (EditText)rootView.findViewById(R.id.port_edt);
        btn_find_iTMS = (Button)rootView.findViewById(R.id.btn_find_iTMS);

        activation_manual = (RadioButton)rootView.findViewById(R.id.activation_manual);
        activation_auto = (RadioButton)rootView.findViewById(R.id.activation_auto);
        activation_auto.setChecked(true);
        linear_ip.setVisibility(View.GONE);
        linear_find_btn.setVisibility(View.VISIBLE);

        btn_check = (Button)rootView.findViewById(R.id.btn_check);
        result_txt = (TextView) rootView.findViewById(R.id.result_txt);
        img_qr = (ImageView) rootView.findViewById(R.id.img_qr);
        imgSitekeyInfo = (ImageView)rootView.findViewById(R.id.img_sitekey_info);
        imgApikeyInfo = (ImageView)rootView.findViewById(R.id.img_apikey_info);

        //iTMS Cloud
        icloud_qr  = rootView.findViewById(R.id.activation_code_qr);
        icloud_text = rootView.findViewById(R.id.activation_code_text);
        btn_check_cloud = (Button)rootView.findViewById(R.id.btn_check_cloud);
        result_txt_cloud = (TextView) rootView.findViewById(R.id.result_txt_cloud);
        editText_code = rootView.findViewById(R.id.edit_code);
        editText_ITMSCloudAddr = rootView.findViewById(R.id.edit_ITMSCloudAddr);
        img_qr_code = rootView.findViewById(R.id.img_qr_code);
        editText_code.setEnabled(false);

        imgSitekeyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(safeGetResouces().getString(R.string.popupinfo_sitekey), imgSitekeyInfo);
            }
        });

        imgApikeyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(safeGetResouces().getString(R.string.popupinfo_apikey), imgApikeyInfo);
            }
        });

        registerViewListener();
        registerCallbackListener();

        if(IrisApplication.isActivate) {
            linear_deactive.setVisibility(View.VISIBLE);
            scroll_activate.setVisibility(View.GONE);
        }else {
            itms_radio.setChecked(true);
            linear_itms.setVisibility(View.VISIBLE);
            linear_standalone.setVisibility(View.GONE);
            linear_itms_cloud.setVisibility(View.GONE);

            linear_deactive.setVisibility(View.GONE);
            scroll_activate.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    private void registerViewListener() {

        activation_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Logger.d("setOnCheckedChangeListener@@");
                long rtnActivationType =  MessageType.ID_RTN_FAIL;
                switch (checkedId) {

                    case R.id.activation_itms:
                        linear_itms.setVisibility(View.VISIBLE);
                        linear_standalone.setVisibility(View.GONE);
                        linear_itms_cloud.setVisibility(View.GONE);
                        rtnActivationType= IT100.setActivationType(MessageType.ID_MODE_ITMS);
                        break;

                    case R.id.activation_alone:
                        linear_itms.setVisibility(View.GONE);
                        linear_standalone.setVisibility(View.VISIBLE);
                        linear_itms_cloud.setVisibility(View.GONE);
                        rtnActivationType = IT100.setActivationType(MessageType.ID_MODE_STANDALONE);
                        break;

                    case R.id.activation_itms_cloud:
                        linear_itms.setVisibility(View.GONE);
                        linear_standalone.setVisibility(View.GONE);
                        linear_itms_cloud.setVisibility(View.VISIBLE);
                        rtnActivationType= IT100.setActivationType(MessageType.ID_MODE_ITMS); //temp
                        break;
                }

                if (rtnActivationType == MessageType.ID_RTN_SUCCESS) {
                    // success
                } else if (rtnActivationType == MessageType.ID_RTN_WRONG_PARA) {
                    // wrong type
                } else if (rtnActivationType == MessageType.ID_RTN_NOT_OPENED_FAIL) {
                    // not opened yet
                } else if (rtnActivationType == MessageType.ID_RTN_FAIL) {
                    // failure
                }

            }
        });

        passSite_edt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passSite_layout.setErrorEnabled(false);
                if (count == 0) {
                    passSite_edt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15);
                } else {
                    passSite_edt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
                }

            }
            public void afterTextChanged(Editable s) {
            }
        });

        passSite_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    passSite_layout.setCounterEnabled(true);
                else {
                    passSite_layout.setCounterEnabled(false);
                }
            }
        });

        passApi_edt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    passApi_edt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15);
                } else {
                    passApi_edt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });

        passApi_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    passApi_layout.setCounterEnabled(true);
                else
                    passApi_layout.setCounterEnabled(false);
            }
        });


        btn_find_iTMS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showLoading("Wait..");
                _ownerHandler.postDelayed(_loading, 2500);

            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if(editText_port.isFocused())
                    imm.hideSoftInputFromWindow(editText_port.getWindowToken(), 0);
                else if(editText_ip.isFocused())
                    imm.hideSoftInputFromWindow(editText_ip.getWindowToken(), 0);

                String ip = editText_ip.getText().toString();
                String port = editText_port.getText().toString();

                if (ip.length()< 2){
                    new BasicToast(getContext()).makeText("Missing port number").show();
                    return;
                }
                result_txt.setText(safeGetResouces().getString(R.string.wait_progress_text));

                long rtnCheckin = 0 ;
                rtnCheckin = IT100.checkInItms(ip, port, new CheckInCallback() {


                    @Override
                    public void checkInResult(final int resultCode, final String msg) {
                        if(getActivity()!= null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultCode == 0)
                                        result_txt.setText("iT100 is successfully checked in to the iTMS.");
                                    else
                                        result_txt.setText("Error: Failed to check in(code " + resultCode + ")" + "\n" + msg);
                                    //result_txt.setText("result : " + resultCode +"\nmsg : " + msg);
                                }
                            });
                        }
                    }
                });

                if ( rtnCheckin  == MessageType.ID_RTN_SUCCESS){   }// success
                else if( rtnCheckin  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnCheckin  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnCheckin  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                else if( rtnCheckin  == MessageType.ID_RTN_FAIL){}// fail
            }
        });

        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 //1. If site key is empty, this is error
                if(passSite_edt.getText().toString().isEmpty()){

                    passSite_layout.setErrorEnabled(true);
                    passSite_layout.setError(safeGetResouces().getString(R.string.activate_error_empty_sitekey));

                }else {

                 //2. request activation
                    long rtnStandalone = 0 ;
                    rtnStandalone = IT100.standaloneActivation(passSite_edt.getText().toString(), passApi_edt.getText().toString() );

                    if ( rtnStandalone  == MessageType.ID_RTN_SUCCESS){   }// success
                    else if( rtnStandalone  == MessageType.ID_RTN_WRONG_PARA){}// fail
                    else if( rtnStandalone  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                    else if( rtnStandalone  == MessageType.ID_RTN_FAIL){}// fail
                }
            }
        });

        activation_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                long rtnItmsDiscoveryMode;
                if (isChecked){
                    linear_ip.setVisibility(View.VISIBLE);
                    btn_check.setVisibility(View.VISIBLE);

                    linear_find_btn.setVisibility(View.GONE);
                    rtnItmsDiscoveryMode = IT100.setItmsDiscoveryMode(MessageType.ID_MODE_MANUAL);
                } else {
                    linear_ip.setVisibility(View.GONE);
                    linear_find_btn.setVisibility(View.VISIBLE);
                    result_txt.setText("");
                    rtnItmsDiscoveryMode = IT100.setItmsDiscoveryMode(MessageType.ID_MODE_AUTO);
                }

                if (rtnItmsDiscoveryMode == MessageType.ID_RTN_SUCCESS) {
                    // success
                } else if (rtnItmsDiscoveryMode == MessageType.ID_RTN_WRONG_PARA) {
                    // wrong type
                } else if (rtnItmsDiscoveryMode == MessageType.ID_RTN_NOT_OPENED_FAIL) {
                    // not opened yet
                } else if (rtnItmsDiscoveryMode == MessageType.ID_RTN_FAIL) {
                    // failure
                }

            }
        });

        activation_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                long rtnItmsDiscoveryMode;
                if (isChecked){
                    linear_ip.setVisibility(View.GONE);
                    linear_find_btn.setVisibility(View.VISIBLE);
                    result_txt.setText("");
                    rtnItmsDiscoveryMode= IT100.setItmsDiscoveryMode(MessageType.ID_MODE_AUTO);
                }else{
                    rtnItmsDiscoveryMode= IT100.setItmsDiscoveryMode(MessageType.ID_MODE_MANUAL);
                }
                if (rtnItmsDiscoveryMode == MessageType.ID_RTN_SUCCESS) {
                    // success
                } else if (rtnItmsDiscoveryMode == MessageType.ID_RTN_WRONG_PARA) {
                    // wrong type
                } else if (rtnItmsDiscoveryMode == MessageType.ID_RTN_NOT_OPENED_FAIL) {
                    // not opened yet
                } else if (rtnItmsDiscoveryMode == MessageType.ID_RTN_FAIL) {
                    // failure
                }

            }
        });

        icloud_qr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    img_qr_code.setVisibility(View.VISIBLE);
                    result_txt_cloud.setText("");
                    editText_code.setEnabled(false);
                    editText_code.setText("");
                    editText_code.setTextColor(getResources().getColor(R.color.disableText, null));

                }else{
                    img_qr_code.setVisibility(View.GONE);
                    result_txt_cloud.setText("");
                    editText_code.setEnabled(true);
                    editText_code.setTextColor(getResources().getColor(R.color.white, null));

                }
            }
        });

        img_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR);
            }
        });

        btn_check_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "CheckInToiTMSCloud" / "code": "<check-in code>"
                String sCheckinCode = editText_code.getText().toString();
                String sCheckinITMSCloudAddr =editText_ITMSCloudAddr.getText().toString();

                if(sCheckinCode.isEmpty()){
                    new BasicToast(getContext()).
                            makeText("Please enter check in code ").show();
                }else {
                    IT100.checkInItmsCloud(sCheckinCode ,sCheckinITMSCloudAddr, new CheckInCallback() {
                        @Override
                        public void checkInResult(int resultCode, String msg) {
                            if (resultCode == 0)
                                result_txt_cloud.setText("iT100 is successfully checked in to the iTMS Cloud.");
                            else
                                result_txt_cloud.setText("Error: Failed to check in(code " + resultCode + ")" + "\n" + msg);
                        }
                    });
                }
            }
        });

        btn_deactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BasicAlertDialog builder = new BasicAlertDialog(getContext());
                builder.setTitle(R.string.deactivate);
                builder.setMessage(R.string.deactivate_dialog);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View content =  inflater.inflate(R.layout.progress, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);

                        builder.setView(content);
                        TextView textView = (TextView)content.findViewById(R.id.loading_msg);
                        textView.setText(safeGetResouces().getString(R.string.deactivating));
                        builder.show();

                        IrisApplication.isActivate = false;
                        editor.putBoolean(safeGetResouces().getString(R.string.prefer_key_activate_state), false);
                        editor.putBoolean(safeGetResouces().getString(R.string.prefer_key_init_pw), false);
                        editor.putInt(safeGetResouces().getString(R.string.prefer_key_login_error_count), 0);
                        editor.putLong(safeGetResouces().getString(R.string.prefer_key_login_error_time), 0);
                        editor.putString(safeGetResouces().getString(R.string.prefer_key_admin_login_type), ConstData.ADMIN_AUTH_ID_PW);
                        editor.commit();

                        long rtnDeactivaion = IT100.deactivate();
                        if(rtnDeactivaion == MessageType.ID_RTN_FAIL){}
                        else if(rtnDeactivaion == MessageType.ID_RTN_SUCCESS){}
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
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
                }
            }
        });
    }

    private LocalActivateBroadcastReceiver mReceiver = new LocalActivateBroadcastReceiver();
    public class LocalActivateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BROADCAST_SERVICE_DEVICE_ACTIVATION)){
                getActivationState();
            }
        }
    }

    private void registerCallbackListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_SERVICE_DEVICE_ACTIVATION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);

        //get activation status info
        getActivationState();
    }
    String _siteKeyPassPhrase;
    String _APIKeyPassPhrase;
    String _siteKey;
    String _apikey;
    String _serverurl;
    String _serialNumber;
    private void getActivationState(){

        long rtnActivationState = 0 ;
        rtnActivationState = IT100.getActivationStatus(new ActivationStatusCallback()
        {

            @Override
            public void activationStatusResult(String activationType, String discoverITMS, String deviceActivated , String  serialNumber, String siteKeyPassPhrase , String APIKeyPassPhrase , String  siteKey, String apiKey , String url) {

                //Logger.d("GetActivateState@  "+ deviceActivated+"|"+ activationType+"|"+discoverITMS+"|"+serialNumber+"|"+siteKeyPassPhrase+"|"+APIKeyPassPhrase+"|"+siteKey+"|"+apiKey+"|"+url);
                tempDiscoverITMS = discoverITMS;
                tempActivateType = activationType;

                _siteKeyPassPhrase = siteKeyPassPhrase;
                _APIKeyPassPhrase = APIKeyPassPhrase;
                _siteKey = siteKey;
                _apikey = apiKey;
                _serverurl = url;
                _serialNumber = serialNumber;

                IrisApplication.activateType = tempActivateType;
                if (deviceActivated.equals("true")) {
                    IrisApplication.isActivate = true;
                    editor.putBoolean(safeGetResouces().getString(R.string.prefer_key_activate_state), true);
                    editor.commit();

                } else {
                    IrisApplication.isActivate = false;
                    editor.putBoolean(safeGetResouces().getString(R.string.prefer_key_activate_state), false);
                    editor.commit();
                }
                refreshUI();
            }
        });
        if ( rtnActivationState  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnActivationState  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnActivationState  == MessageType.ID_RTN_FAIL){}// fail

    }

    protected Handler _ownerHandler=new Handler();
    Runnable _loading = new Runnable() {
        @Override
        public void run() {
            //hideLoading();
            long rtnItms = 0 ;
            rtnItms = IT100.getItmsIpAddress(new ItmsIpAddressCallback() {

                @Override
                public void itmsIpAddressResult(final String Ipaddress, final String port) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoading();
                                if(Ipaddress.equals("")){
                                    new BasicAlertDialog(getContext())
                                    //new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle)
                                            .setTitle(safeGetResouces().getString(R.string.dlgtitle_notice))
                                            .setMessage(safeGetResouces().getString(R.string.activate_error_not_find))
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Continue with delete operation
                                                }
                                            })
                                            .show();
                                    return;
                                }else{
                                    editText_ip.setText(Ipaddress);
                                    editText_port.setText(port);
                                    linear_ip.setVisibility(View.VISIBLE);
                                    linear_find_btn.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });

            if ( rtnItms  == MessageType.ID_RTN_SUCCESS){   }// success
            else if( rtnItms  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
            else if( rtnItms  == MessageType.ID_RTN_FAIL){}// fail
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);

        initValue();
    }

    Dialog pdialog;
    public void showLoading(String message) {

        if (getActivity().isFinishing()) {
            return;
        }

        if (pdialog != null && pdialog.isShowing()) {
            loadingMsg(message);
        } else {

            pdialog = new Dialog(getActivity());
            pdialog.setCancelable(false);
            pdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pdialog.setContentView(R.layout.progress_loading);
            pdialog.show();
        }

        final ImageView img_loading_frame = (ImageView)pdialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView)pdialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void loadingMsg(String message) {

        if (pdialog == null || !pdialog.isShowing()) {
            return;
        }

        TextView txt_progress_msg = (TextView)pdialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            txt_progress_msg.setText(message);
        }
    }

    public void hideLoading() {
        if (pdialog != null && pdialog.isShowing()) {
            pdialog.dismiss();
        }
    }
    private void generateQRCode(String activateInfo){
        Bitmap bitmap= null;
        activateInfo = activateInfo.replaceAll("\\\\", "");
        activateInfo = activateInfo.replace("https://", "https:\\/\\/");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
                BitMatrix bitMatrix = multiFormatWriter.encode(activateInfo,
                        BarcodeFormat.QR_CODE, 400, 400,
                        new java.util.HashMap(){{put(com.google.zxing.EncodeHintType.MARGIN,0);}}
                );
                final int width = bitMatrix.getWidth();
                final int height = bitMatrix.getHeight();
                int[] pixels = new int[width * height];
                for (int y = 0; y < height; y++) {
                    int offset = y * width;
                    for (int x = 0; x < width; x++) {
                       // pixels[offset + x] = bitMatrix.get(x, y) ? Color.WHITE : Color.TRANSPARENT;
                        pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                    }
                }
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        }catch (WriterException e){
            e.printStackTrace();
        }
        if(bitmap!=null){
            img_qr.setImageBitmap(bitmap);
        }
    }


    private void refreshUI(){
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (IrisApplication.isActivate) {
                        linear_deactive.setVisibility(View.VISIBLE);
                        scroll_activate.setVisibility(View.GONE);

                        if(_siteKeyPassPhrase.isEmpty())
                            passSite_dedt.setText("--");
                        else
                            passSite_dedt.setText(_siteKeyPassPhrase);

                        if(_APIKeyPassPhrase.isEmpty())
                            passApi_dedt.setText("--");
                        else
                            passApi_dedt.setText(_APIKeyPassPhrase);

                        site_dedt.setText(_siteKey);
                        api_dedt.setText(_apikey);
                        //serial_num.setText(_serialNumber);
                        server_url.setText(_serverurl);

                        if(tempActivateType.equals(MessageKeyValue.ACTIVATION_TYPE_ITMS))
                            txt_title.setText("Activated by iTMS");
                        else if(tempActivateType.equals(MessageKeyValue.ACTIVATION_TYPE_ITMSCLOUD))
                            txt_title.setText("Activated by iTMS Cloud");
                        else
                            txt_title.setText("Activated as Standalone");

                        try {
                            JSONObject activateJson = new JSONObject();
                            activateJson.put(MessageKeyValue.DEVICE_SERIAL_NUMBER, _serialNumber);
                            activateJson.put(MessageKeyValue.DEVICE_SITEKEY_PASSPHRASE, _siteKeyPassPhrase);
                            activateJson.put(MessageKeyValue.DEVICE_APIKEY_PASSPHRASE, _APIKeyPassPhrase);
                            activateJson.put(MessageKeyValue.DEVICE_SITEKEY, _siteKey);
                            activateJson.put(MessageKeyValue.DEVICE_APIKEY, _apikey);
                            activateJson.put(MessageKeyValue.DEVICE_URL, _serverurl);
                            generateQRCode(activateJson.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        linear_deactive.setVisibility(View.GONE);
                        scroll_activate.setVisibility(View.VISIBLE);
                        itms_radio.setChecked(true);

                        if(tempActivateType!=null && tempActivateType.equals(MessageKeyValue.ACTIVATION_TYPE_ITMS)) {
                            activation_radio_group.check(R.id.activation_itms);

                            if (tempDiscoverITMS != null && tempDiscoverITMS.equals(MessageKeyValue.ITMS_MODE_AUTO))
                                activation_auto.setChecked(true);
                            else
                                activation_manual.setChecked(true);
                        }else{
                            activation_radio_group.check(R.id.activation_alone);
                        }
                    }
                }
            });
        }
    }

    private void displayPopupWindow(String content , View anchorView){
        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.panel_popup, null);
        popup.setContentView(layout);

        ((TextView)layout.findViewById(R.id.title)).setText(content);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(380);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        //popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QR) {

            if(data!=null) {
                String jcode = data.getStringExtra(ConstData.QRCODE_MESSAGE);

                JSONObject jsonCode = null;
                try {
                    jsonCode = new JSONObject(jcode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(jsonCode == null){
                    new BasicToast(getContext()).
                            makeText("Error while scanning the QR code.").show();
                }else
                    editText_code.setText(jsonCode.optString("code", ""));
            }

        }
    }

    private void initValue(){
        passSite_layout = null;
        passApi_layout = null;
        passSite_edt = null;
        passApi_edt = null;
        passSite_dedt = null;
        passApi_dedt  = null;

        _siteKeyPassPhrase = null;
        _APIKeyPassPhrase = null;
        _siteKey = null;
        _apikey = null;
    }

    private Resources safeGetResouces(){
        return IrisApplication.getInstance().getResources();
    }
}