package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.irisid.it100.IT100;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample_project.R;

public class NetworkFragment5 extends Fragment {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    View rootView;
    Button btnEthernet;
    Button btnWifi;
    LinearLayout linearLeft;
    LinearLayout linearRight;

    String _networkType;

    WifiManager wifiManager;

    static boolean btnWifiClick = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_network5, container, false);

        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), Context.MODE_PRIVATE);
        editor = pref.edit();

        wifiManager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        _networkType = pref.getString(getString(R.string.prefer_key_network_type), ConstData.NETWORK_TYPE_ETHERNET);

        linearLeft = (LinearLayout)rootView.findViewById(R.id.linear_touch_left);
        linearRight = (LinearLayout)rootView.findViewById(R.id.linear_touch_right);

        btnEthernet = (Button)rootView.findViewById(R.id.btn_ethernet);
        linearLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnWifiClick = false;
                //0. if connect ethernet and then return
                if(_networkType.equals(ConstData.NETWORK_TYPE_ETHERNET))
                    return;

                //1. Alert Dialog
                new BasicAlertDialog(getContext())
                        .setTitle(R.string.conn_ethernet_title)
                        .setMessage(R.string.conn_ethernet_desc)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //1-1. If user agree, disconnect ethernet network
                                btnEthernet.setEnabled(true);
                                btnWifi.setEnabled(false);
                                setEthernetOnOff(true);
                                wifiManager.setWifiEnabled(false);

                                try {
                                    if (getActivity() != null)
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_network, new TabEthernetFragment())
                                                .commitAllowingStateLoss();
                                }catch (Exception e){

                                }
                                _networkType = ConstData.NETWORK_TYPE_ETHERNET;

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

        btnWifi = (Button)rootView.findViewById(R.id.btn_wifi);
        linearRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnWifiClick = true;

                ///0. if connect wifi and then return
                if(_networkType.equals(ConstData.NETWORK_TYPE_WIFI))
                    return;

                //1. Alert Dialog
                new BasicAlertDialog(getContext())
                        .setTitle(R.string.conn_wifi_title)
                        .setMessage(R.string.conn_wifi_desc)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //1-1. If user agree, disconnect wifi network
                                setEthernetOnOff(false);
                                btnEthernet.setEnabled(false);
                                btnWifi.setEnabled(true);
                                try {
                                    if (getActivity() != null)
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_network, new TabWifiFragment())
                                                .commitAllowingStateLoss();
                                }catch (Exception e){}

                                _networkType = ConstData.NETWORK_TYPE_WIFI;
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
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork!=null)
            Logger.d("@NetworkFragment5 networktype is "+ _networkType + activeNetwork.getType() );

        if(activeNetwork!=null && (activeNetwork.getType()==(ConnectivityManager.TYPE_ETHERNET)||
                activeNetwork.getType()==(ConnectivityManager.TYPE_WIFI))){
            if(activeNetwork.getType()== ConnectivityManager.TYPE_ETHERNET){
                _networkType = ConstData.NETWORK_TYPE_ETHERNET;

                WifiManager wifiManager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(false);

                setEthernetNetworkView();
            }else if(activeNetwork.getType()== ConnectivityManager.TYPE_WIFI) {
                _networkType = ConstData.NETWORK_TYPE_WIFI;
                setWifiNetworkView(activeNetwork);
            }
        } else{
            if(_networkType.equals(ConstData.NETWORK_TYPE_ETHERNET)){
                setEthernetNetworkView();
            }else if(_networkType.equals(ConstData.NETWORK_TYPE_WIFI)) {
                setWifiNetworkView(activeNetwork);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        editor.putString(getString(R.string.prefer_key_network_type), _networkType);
        editor.commit();
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
            LinkProperties prop = connectivityManager.getLinkProperties(network);


            ConnectivityManager cm =
                    (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

            Logger.d("@onAvailable NetworkFragment5 activeNetwork"+ activeNetwork.toString());

            // prop.getLinkAddresses().
            Logger.d("@onAvailable NetworkFragment5"+ prop.getDomains()+"|"+prop.getLinkAddresses()+"|"+
                    prop.getDnsServers()+"|"+prop.toString());

        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.d("@onLost NetworkFragment3");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Logger.d("@onCapabilitiesChanged NetworkFragment3 "+ network.toString()+" || "+ networkCapabilities.toString());
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    };

    private void setEthernetNetworkView(){
        btnEthernet.setEnabled(true);
        btnWifi.setEnabled(false);

        try {
            if (getActivity() != null)
                getChildFragmentManager().beginTransaction()
                            .replace(R.id.fragment_network, new TabEthernetFragment())
                            .commitAllowingStateLoss();

        }catch (Exception e){

        }
    }

    private void setWifiNetworkView(NetworkInfo activeNetwork){
        btnEthernet.setEnabled(false);
        btnWifi.setEnabled(true);

        if(activeNetwork!=null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET)
                IT100.enableEthernet(false);
        }

        try {
            if (getActivity() != null)
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragment_network, new TabWifiFragment())
                        .commitAllowingStateLoss();
        }catch (Exception e){}
    }

    private void setEthernetOnOff(boolean onOff){
        IT100.enableEthernet(onOff);
    }

}
