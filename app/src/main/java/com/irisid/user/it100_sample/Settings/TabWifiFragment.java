package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample_project.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.irisid.user.it100_sample.Settings.NetworkFragment5.btnWifiClick;

public class TabWifiFragment extends Fragment
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    View rootView;
    LinearLayout rootLayout;
    ScrollView scrollView;
    FrameLayout frameTotal;
    LinearLayout linearNetInfo;
    LinearLayout linearProgress;
    LinearLayout linearStatusSetting;

    EditText ip_address_edt;
    EditText prefix_len_edt;
    EditText default_gateway_edt;
    EditText dns1_edt;
    EditText dns2_edt;
    TextView txtMacAddr;
    TextView txtWifiStatus;

    ImageView imgWifiSetting;

    boolean showSave = false;
    boolean showError = true;
    boolean isFirst = true;

    WifiManager wifiManager;
    WifiInfo connectionInfo ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_fragment_wifi, container, false);

        Logger.d("@@TabWiFi  onCreateView");

        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref.edit();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        rootLayout = (LinearLayout)rootView.findViewById(R.id.rootview);
        scrollView = (ScrollView)rootView.findViewById(R.id.network_scrollview_wifi);
        frameTotal = (FrameLayout)rootView.findViewById(R.id.frame_total_network_info_wifi);
        linearNetInfo = (LinearLayout)rootView.findViewById(R.id.linear_network_info_wifi);
        linearProgress = (LinearLayout)rootView.findViewById(R.id.linear_progress_wifi);
        linearStatusSetting = (LinearLayout)rootView.findViewById(R.id.linear_status_setting);

        txtWifiStatus = (TextView)rootView.findViewById(R.id.txt_wifi_status);
        imgWifiSetting = (ImageView)rootView.findViewById(R.id.img_wifi_setting);

        ip_address_edt = (EditText) rootView.findViewById(R.id.ip_address_edt_wifi);
        prefix_len_edt = (EditText) rootView.findViewById(R.id.prefix_len_edt_wifi);
        default_gateway_edt = (EditText) rootView.findViewById(R.id.default_gateway_edt_wifi);
        dns1_edt = (EditText)rootView.findViewById(R.id.dns1_edt_wifi);
        dns2_edt = (EditText)rootView.findViewById(R.id.dns2_edt_wifi);

        txtMacAddr      = (TextView)rootView.findViewById(R.id.txt_mac_content_wifi);

        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        if(btnWifiClick){
            wifiManager.setWifiEnabled(true);
            startSettingsForWifi();
            btnWifiClick = false;
        }
        if(wifiManager.isWifiEnabled()){
            txtWifiStatus.setText("ON");
        }else{
            txtWifiStatus.setText("OFF");
            frameTotal.setVisibility(View.INVISIBLE);
        }
        linearProgress.setVisibility(View.VISIBLE);
        linearStatusSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsForWifi();
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

    ConnectivityManager.NetworkCallback netInfoCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);

            LinkProperties linkProperties = connectivityManager.getLinkProperties(network);

            // get the now network state.
            if ((wifiManager != null && wifiManager.isWifiEnabled())) {
                InetAddress address;
                for(int i=0; i<linkProperties.getLinkAddresses().size(); i++) {
                    address = linkProperties.getLinkAddresses().get(i).getAddress();
                    if (!address.isLoopbackAddress() && (address instanceof Inet4Address)) {
                        final int prefixLen = linkProperties.getLinkAddresses().get(i).getPrefixLength();

                        if(getActivity()!=null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        prefix_len_edt.setText(String.valueOf(prefixLen));
                                    }catch (Exception e){}
                                }
                            });
                        }
                    }
                }
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                txtWifiStatus.setText(getResources().getString(R.string.on));
                                getWIFINetorkInfo(getContext());
                            }catch (Exception e){}
                        }
                    });
                }
            }
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.d("@onLost  -----wifi -"+ wifiManager.getWifiState());
            try {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearNetInfo.setVisibility(View.VISIBLE);
                            linearProgress.setVisibility(View.VISIBLE);

                            setIPsettingView(false);

                            ip_address_edt.setText("---");
                            default_gateway_edt.setText("---");
                            dns1_edt.setText("---");
                            dns2_edt.setText("---");
                            txtMacAddr.setText("---");
                            showSave = false;
                        }
                    });
                }
            }catch (Exception e){

            }

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

    private void setIPsettingView(boolean isEnable){

        frameTotal.setVisibility(View.VISIBLE);
        linearNetInfo.setVisibility(View.VISIBLE);

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

            try {
                ip_address_edt.setTextColor(getResources().getColor(R.color.disableText, null));
                prefix_len_edt.setTextColor(getResources().getColor(R.color.disableText, null));
                default_gateway_edt.setTextColor(getResources().getColor(R.color.disableText, null));
                dns1_edt.setTextColor(getResources().getColor(R.color.disableText, null));
                dns2_edt.setTextColor(getResources().getColor(R.color.disableText, null));
                txtMacAddr.setTextColor(getResources().getColor(R.color.disableText, null));
            }catch (Exception e){}

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if((wifiManager!=null && wifiManager.isWifiEnabled())) {
            txtWifiStatus.setText(getResources().getString(R.string.on));
            getWIFINetorkInfo(getContext());
        }else {
            txtWifiStatus.setText(getResources().getString(R.string.off));
            frameTotal.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            if(rootView == null)
                return;

            if(wifiManager!=null && wifiManager.isWifiEnabled()) {
                txtWifiStatus.setText(getResources().getString(R.string.on));
                getWIFINetorkInfo(getContext());
            }else {
                txtWifiStatus.setText(getResources().getString(R.string.off));
                frameTotal.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void getWIFINetorkInfo(Context context){
        if(isFirst)
            isFirst = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return ;
        }
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            final DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showError = false;

                        if(getActivity()==null)
                            return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                frameTotal.setVisibility(View.VISIBLE);
                                linearNetInfo.setVisibility(View.VISIBLE);
                                setIPsettingView(false);

                                linearProgress.setVisibility(View.GONE);
                                ip_address_edt.setText(intToInetAddress(dhcpInfo.ipAddress).getHostAddress());
                                default_gateway_edt.setText(intToInetAddress(dhcpInfo.gateway).getHostAddress());
                                dns1_edt.setText(intToInetAddress(dhcpInfo.dns1).getHostAddress());
                                dns2_edt.setText(intToInetAddress(dhcpInfo.dns2).getHostAddress());
                                txtMacAddr.setText(getMacAddr());
                                showSave = false;
                            }
                        });
                    }
                }, 10);
            }
        }
    }
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }



    String _macAddr;
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("@@TabWifiFragment  onDestroy");
        connectivityManager.unregisterNetworkCallback(netInfoCallback);
    }

    private  String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return null;
        }
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid= connectionInfo.toString()+ "||"+ wifiManager.getDhcpInfo().toString()+ "||"+ wifiManager.getConfiguredNetworks().get(0).toString();
            }
        }
        return ssid;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    private void startSettingsForWifi() {
        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
                .putExtra("only_access_points", true)
                .putExtra(":settings:hide_drawer", true)
                .putExtra("extra_prefs_show_button_bar", true)
                .putExtra("extra_prefs_set_next_text", (String)null));
    }

}