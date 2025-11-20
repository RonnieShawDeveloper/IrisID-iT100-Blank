package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.DeviceInfoCallback;
import com.irisid.it100.callback.NetworkInfoCallback;
import com.irisid.it100.data.DeviceInfo;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample_project.R;

import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class NetworkFragment extends Fragment
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    LinearLayout rootLayout;
    ScrollView scrollView;
    FrameLayout frameTotal;
    LinearLayout linearNetInfo;
    LinearLayout linearSave;
    LinearLayout linearError;
    LinearLayout linearProgress;

    AppCompatRadioButton ip_dhcp_radio;
    AppCompatRadioButton ip_static_radio;
    RadioGroup ip_radio_group;
    String m_ip_set_value;

    EditText ip_address_edt;
    EditText prefix_len_edt;
    EditText default_gateway_edt;
    EditText dns1_edt;
    EditText dns2_edt;
    TextView txtMacAddr;

    TextView txtError;

    boolean showSave = false;
    boolean showError = true;
    boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_network, container, false);

        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref.edit();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        rootLayout = (LinearLayout)rootView.findViewById(R.id.rootview);
        scrollView = (ScrollView)rootView.findViewById(R.id.network_scrollview);
        frameTotal = (FrameLayout)rootView.findViewById(R.id.frame_total_network_info);
        linearNetInfo = (LinearLayout)rootView.findViewById(R.id.linear_network_info);
        linearSave = (LinearLayout)rootView.findViewById(R.id.linear_save);
        linearError = (LinearLayout)rootView.findViewById(R.id.linear_network_error);
        linearProgress = (LinearLayout)rootView.findViewById(R.id.linear_progress);

        txtError = (TextView)rootView.findViewById(R.id.txt_neterror);
        ip_radio_group = (RadioGroup) rootView.findViewById(R.id.ip_radio_group);
        ip_dhcp_radio = (AppCompatRadioButton) rootView.findViewById(R.id.ip_dhcp_radio);
        ip_static_radio = (AppCompatRadioButton) rootView.findViewById(R.id.ip_static_radio);

        ip_address_edt = (EditText) rootView.findViewById(R.id.ip_address_edt);
        prefix_len_edt = (EditText) rootView.findViewById(R.id.prefix_len_edt);
        default_gateway_edt = (EditText) rootView.findViewById(R.id.default_gateway_edt);
        dns1_edt = (EditText)rootView.findViewById(R.id.dns1_edt);
        dns2_edt = (EditText)rootView.findViewById(R.id.dns2_edt);

        txtMacAddr      = (TextView)rootView.findViewById(R.id.txt_mac_content);

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            m_ip_set_value = pref.getString(getString(R.string.prefer_key_ip_settings_type), "DHCP");
            if (m_ip_set_value.equals("DHCP")) {
                ip_radio_group.check(R.id.ip_dhcp_radio);
                setIPsettingView(false);
            } else {  // static
                ip_radio_group.check(R.id.ip_static_radio);
                setIPsettingView(true);
            }
        }else{
            //Logger.d("Disconnected");
            showNetworkErrorMsg(getResources().getString(R.string.network_error_connect));
        }

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

        ip_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showError = false;
                showNetworkSettingSave();

                switch (checkedId)
                {
                    case R.id.ip_dhcp_radio:

                        m_ip_set_value = "DHCP";
                        linearNetInfo.setVisibility(View.INVISIBLE);
                        frameTotal.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.ip_static_radio:
                        m_ip_set_value = "static";

                        setIPsettingView(true);
                        break;
                }
                if(isFirst)
                    isFirst = false;
            }
        });

        ip_address_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ip_address_edt.getBackground().mutate().setColorFilter(null);
                if(ip_address_edt.isEnabled() && (!isFirst))
                    showSave = true;
                else
                    showSave = false;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        prefix_len_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prefix_len_edt.getBackground().mutate().setColorFilter(null);
                if(prefix_len_edt.isEnabled() && (!isFirst))
                    showSave = true;
                else
                    showSave = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        default_gateway_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                default_gateway_edt.getBackground().mutate().setColorFilter(null);
                if(default_gateway_edt.isEnabled() && (!isFirst))
                    showSave = true;
                else
                    showSave = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dns1_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dns1_edt.getBackground().mutate().setColorFilter(null);
                if(dns1_edt.isEnabled() && (!isFirst))
                    showSave = true;
                else
                    showSave = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dns2_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dns2_edt.getBackground().mutate().setColorFilter(null);
                if(dns2_edt.isEnabled() && (!isFirst))
                    showSave = true;
                else
                    showSave = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        (rootView.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSave = false;
                hideNetworkSettingSave();
                linearNetInfo.setVisibility(View.INVISIBLE);
                editClearFocus();

                getNetorkInfo();
            }
        });

        (rootView.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSave = false;
                hideNetworkSettingSave();
                editClearFocus();

                String setting_type;
                if(ip_radio_group.getCheckedRadioButtonId() == R.id.ip_dhcp_radio) {

                    long rtnNetworkInfo = 0 ;
                    rtnNetworkInfo = IT100.setNetworkInfo("DHCP",24,"", "8.8.8.8","8.8.4.4");

                    if ( rtnNetworkInfo  == MessageType.ID_RTN_SUCCESS){   }// success
                    else if( rtnNetworkInfo  == MessageType.ID_RTN_WRONG_PARA){}// fail
                    else if( rtnNetworkInfo  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                    else if( rtnNetworkInfo  == MessageType.ID_RTN_FAIL){}// fail

                    m_ip_set_value = "DHCP";
                    linearNetInfo.setVisibility(View.INVISIBLE);

                    editor.putString(getString(R.string.prefer_key_ip_settings_type), m_ip_set_value);
                    editor.commit();

                    frameTotal.setVisibility(View.VISIBLE);
                    linearProgress.setVisibility(View.VISIBLE);

                } else{

                    int result = netErrorCheck();

                    if(result>0) {

                        long rtnNetowrkInfo = 0 ;
                        rtnNetowrkInfo = IT100.setNetworkInfo(ip_address_edt.getText().toString(), Long.parseLong(prefix_len_edt.getText().toString()),
                                default_gateway_edt.getText().toString() , dns1_edt.getText().toString(), dns2_edt.getText().toString() );

                        if ( rtnNetowrkInfo  == MessageType.ID_RTN_SUCCESS){   }// success
                        else if( rtnNetowrkInfo  == MessageType.ID_RTN_WRONG_PARA){}// fail
                        else if( rtnNetowrkInfo  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                        else if( rtnNetowrkInfo  == MessageType.ID_RTN_FAIL){}// fail

                        setting_type = "static";

                        //editor = pref.edit();
                        editor.putString(getString(R.string.prefer_key_ip_settings_type), setting_type);
                        editor.putString(getString(R.string.prefer_key_ip_addr), ip_address_edt.getText().toString());
                        editor.putString(getString(R.string.prefer_key_prefix_len), prefix_len_edt.getText().toString());
                        editor.putString(getString(R.string.prefer_key_default_gateway), default_gateway_edt.getText().toString());
                        editor.putString(getString(R.string.prefer_key_dns1), dns1_edt.getText().toString());
                        editor.putString(getString(R.string.prefer_key_dns2), dns2_edt.getText().toString());
                        editor.commit();

                        linearProgress.setVisibility(View.VISIBLE);

                    }else if(result== -1){ //prefix error
                        prefix_len_edt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                    }else if(result == -2){ // IP address format error
                        ip_address_edt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                    }else if(result == -3){ // gatewary address format error
                        default_gateway_edt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                    }else if(result == -4){ // dns1 address format error
                        dns1_edt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                    }else if(result == -5){ // dns2 address format error
                        dns2_edt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
                    }

                    if(result<0)
                        new BasicToast(getContext()).makeText("Please check network info again. ").show();
                }

                //After several time, get network info.
                _ownerHandler.postDelayed(_loading, 10000);  //5s->10s
            }
        });

        registerNetworkCallback();

        return rootView;
    }

    ConnectivityManager connectivityManager;
    public void registerNetworkCallback(){
        try{
            connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(netInfoCallback);
            }

        }catch (Exception e){

        }
    }

    protected Handler _ownerHandler=new Handler();
    Runnable _loading = new Runnable() {
        @Override
        public void run() {
            getNetorkInfo();

        }
    };

    private void setIPsettingView(boolean isEnable){

            frameTotal.setVisibility(View.VISIBLE);
            linearNetInfo.setVisibility(View.VISIBLE);

            if(!showError)
                linearError.setVisibility(View.INVISIBLE);
            else
                linearError.setVisibility(View.VISIBLE);

        if(isEnable){
            ip_address_edt.setEnabled(true);
            prefix_len_edt.setEnabled(true);
            default_gateway_edt.setEnabled(true);
            dns1_edt.setEnabled(true);
            dns2_edt.setEnabled(true);

            ip_address_edt.setTextColor(Color.WHITE);
            prefix_len_edt.setTextColor(Color.WHITE);
            default_gateway_edt.setTextColor(Color.WHITE);
            dns1_edt.setTextColor(Color.WHITE);
            dns2_edt.setTextColor(Color.WHITE);
            txtMacAddr.setTextColor(Color.WHITE);
        }else{
            ip_address_edt.setEnabled(false);
            prefix_len_edt.setEnabled(false);
            default_gateway_edt.setEnabled(false);
            dns1_edt.setEnabled(false);
            dns2_edt.setEnabled(false);

            ip_address_edt.setTextColor(getResources().getColor(R.color.disableText, null));
            prefix_len_edt.setTextColor(getResources().getColor(R.color.disableText, null));
            default_gateway_edt.setTextColor(getResources().getColor(R.color.disableText, null));
            dns1_edt.setTextColor(getResources().getColor(R.color.disableText, null));
            dns2_edt.setTextColor(getResources().getColor(R.color.disableText, null));
            txtMacAddr.setTextColor(getResources().getColor(R.color.disableText, null));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void getNetorkInfo(){
        if(isFirst)
            isFirst = false;

        long rtnNetworkInfo = 0 ;
        rtnNetworkInfo = IT100.getNetworkInfo(new NetworkInfoCallback() {

            @Override
            public void networkInfoResult(final int resultCode, final boolean isDHCP, final String Ipaddress,
                               final String prefixLen, final String Gateway , final String dns1, final String dns2) {
                //Logger.d("[GetNetworkInfo] "+ Ipaddress+"|"+prefixLen+"|"+Gateway+"|"+dns1+"|"+dns2+"|"+isDHCP);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearProgress.setVisibility(View.GONE);
                        linearSave.setVisibility(View.INVISIBLE);
                        try
                        {

                            if (resultCode == 0)
                            {
                                showError = false;
                                frameTotal.setVisibility(View.VISIBLE);
                                linearNetInfo.setVisibility(View.VISIBLE);

                                if (isDHCP)
                                {
                                    ip_radio_group.check(R.id.ip_dhcp_radio);
                                    setIPsettingView(false);
                                }
                                else
                                {
                                    ip_radio_group.check(R.id.ip_static_radio);
                                    setIPsettingView(true);
                                }

                                if (!Ipaddress.equals(""))
                                    ip_address_edt.setText(Ipaddress);

                                if (!prefixLen.equals(""))
                                    prefix_len_edt.setText(prefixLen);

                                if (!Gateway.equals(""))
                                    default_gateway_edt.setText(Gateway);

                                if (!dns1.equals(""))
                                    dns1_edt.setText(dns1);

                                if (!dns2.equals(""))
                                    dns2_edt.setText(dns2);

                                hideNetworkSettingSave();
                                showSave = false;
                            }
                            else
                            {
                                String errorMsg = "";
                                if (resultCode == -2002)
                                    errorMsg = getResources().getString(R.string.network_error_connect);
                                else
                                    errorMsg = getResources().getString(R.string.network_error_ipaddress);

                                showError = true;
                                ip_radio_group.clearCheck();
                                showNetworkErrorMsg(errorMsg);
                            }
                        }catch (Exception e){}
                    }
                }, 10);
            }
        });

        if ( rtnNetworkInfo  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnNetworkInfo  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnNetworkInfo  == MessageType.ID_RTN_FAIL){}// fail

        long rtnDeviceInfo = IT100.getDeviceInfo(new DeviceInfoCallback() {

            @Override
            public void deviceInfoResult(DeviceInfo deviceInfo) {

                _macAddr = deviceInfo.macAddress;
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtMacAddr.setText(_macAddr.toUpperCase());
                        }
                    });
                }
            }
        });

        if ( rtnDeviceInfo  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnDeviceInfo  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnDeviceInfo  == MessageType.ID_RTN_FAIL){}// fail
    }
    String _macAddr;
    private void showNetworkErrorMsg(final String errMsg){
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    frameTotal.setVisibility(View.VISIBLE);
                    linearError.setVisibility(View.VISIBLE);
                    txtError.setText(errMsg);
                    linearSave.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    ConnectivityManager.NetworkCallback netInfoCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            LinkProperties prop = connectivityManager.getLinkProperties(network);

            getNetorkInfo();
            // get the now network state.
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            showNetworkErrorMsg(getResources().getString(R.string.network_error_connect));
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(netInfoCallback);
    }

    private int netErrorCheck(){
        String validIp = "^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";

        int res = 1;
        int iPrefix =0;
        try{
            iPrefix = Integer.parseInt(prefix_len_edt.getText().toString());
        }catch (NumberFormatException nfe){
            res = -1;
        }

        // prefix length range is 1~31
        if(iPrefix <1 || iPrefix >31)
            res =-1;

        // Check IP address
        if (!Pattern.matches(validIp, ip_address_edt.getText().toString() ))
            res =-2;

        // Check Gateway address
        if (!Pattern.matches(validIp, default_gateway_edt.getText().toString() ))
            res =-3;

        // Check DNS1 address
        if (!Pattern.matches(validIp, dns1_edt.getText().toString() ))
            res =-4;

        // Check DNS2 address
        if (!Pattern.matches(validIp, dns2_edt.getText().toString() ))
            res =-5;

        return res;
    }

    private void showNetworkSettingSave(){
        linearSave.setVisibility(View.VISIBLE);
    }

    private void hideNetworkSettingSave(){
        linearSave.setVisibility(View.INVISIBLE);
    }

    private void editClearFocus() {
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ip_address_edt.setFocusable(false);
                    prefix_len_edt.setFocusable(false);
                    default_gateway_edt.setFocusable(false);
                    dns1_edt.setFocusable(false);
                    dns2_edt.setFocusable(false);

                    // Set EditText to be focusable again
                    ip_address_edt.setFocusable(true);
                    ip_address_edt.setFocusableInTouchMode(true);
                    ip_address_edt.getBackground().mutate().setColorFilter(null);

                    prefix_len_edt.setFocusable(true);
                    prefix_len_edt.setFocusableInTouchMode(true);
                    prefix_len_edt.getBackground().mutate().setColorFilter(null);

                    default_gateway_edt.setFocusable(true);
                    default_gateway_edt.setFocusableInTouchMode(true);
                    default_gateway_edt.getBackground().mutate().setColorFilter(null);

                    dns1_edt.setFocusable(true);
                    dns1_edt.setFocusableInTouchMode(true);
                    dns1_edt.getBackground().mutate().setColorFilter(null);

                    dns2_edt.setFocusable(true);
                    dns2_edt.setFocusableInTouchMode(true);
                    dns2_edt.getBackground().mutate().setColorFilter(null);
                }
            });
        }

    }
}