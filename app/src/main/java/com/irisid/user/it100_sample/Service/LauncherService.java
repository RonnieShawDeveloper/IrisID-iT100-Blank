package com.irisid.user.it100_sample.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.ActivationCallback;
import com.irisid.it100.callback.ActivationStatusCallback;
import com.irisid.it100.callback.AllUserCallback;
import com.irisid.it100.callback.ConnectCallback;
import com.irisid.it100.callback.DeviceNameChangeCallback;
import com.irisid.it100.callback.OperationModeCallback;
import com.irisid.it100.callback.RelaySettingCallback;
import com.irisid.it100.callback.ServerEnableCallback;
import com.irisid.it100.callback.TamperCallback;
import com.irisid.it100.callback.ThermalSettingJSONCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.it100.data.UserSimpleInfo;
import com.irisid.it100.listener.ChangeEventListener;
import com.irisid.it100.listener.ThermalStatusListener;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_CHANGE_DEVICENAME;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_CHANGE_OPERATIONMODE;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_CHANGE_THERMAL;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_DEVICE_ACTIVATION;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_START;

public class LauncherService extends Service {

    SharedPreferences prefSetting; // background saved value
    SharedPreferences.Editor editor;

    public static boolean mServiceState = false;

    Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //IT100.close();
        Logger.d("LauncherService @@onStartCommand()");

        long rtnOpen = 0 ;
        rtnOpen = IT100.open(this.getApplicationContext(), new ConnectCallback() {

            @Override
            public void initialized() {
                Logger.d("[ConnectCallback] Initialized");
                registerIrisSeviceEvent();

                Intent intent = new Intent(BROADCAST_SERVICE_START);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                // for canceling pre recog process
                IT100.abortCapture();
                IT100.enableServer(true, new ServerEnableCallback() {
                    @Override
                    public void onResult(int i, String s) {

                    }
                });
            }

            @Override
            public void finished() {
                Logger.d("[ConnectCallback] Finished");
                IrisApplication.restartApp(getApplicationContext());
            }

            @Override
            public void error_detected(String errType) {
                Logger.d("[ConnectCallback] error_detected error Type is "+ errType);
            }
        });
        if (rtnOpen == MessageType.ID_RTN_SUCCESS)// success
        {   }
        else if( rtnOpen == MessageType.ID_RTN_FAIL)// fail
        {   }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        Logger.d("LauncherService @@onCreate()");
        mServiceState = true;

        prefSetting = getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = prefSetting.edit();

        //IT100.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mServiceState = false;

    }

    private void registerIrisSeviceEvent() {
        Logger.d("[registerIrisSeviceEvent]");
        registerListener();
    }

    Handler handler = new Handler();
    private void registerListener() {
        if(IT100.messageSender!=null) {
            Logger.d("LauncherService >> registerListener  IT100.messageSender!=null");

            // Register Temper Callback Event
            long rtnTamper = 0;
            rtnTamper = IT100.setTamperCallback(new TamperCallback() {

                @Override
                public void pressed() {
                    Logger.d("[Tamper] press");
                }

                @Override
                public void released() {
                    Logger.d("[Tamper] released");
                }
            });

            if (rtnTamper == MessageType.ID_RTN_SUCCESS)// success
            {
            } else if (rtnTamper == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
            {
            } else if (rtnTamper == MessageType.ID_RTN_FAIL)// fail
            {
            }

            // Register Device Name Change Callback Event
            long rtnDeviceNameChange = 0;
            rtnDeviceNameChange = IT100.setDeviceNameChangeListener(new DeviceNameChangeCallback() {
                @Override
                public void changeEvent(String deviceName) {

                    IrisApplication irisApplication = (IrisApplication) getApplication();
                    irisApplication.setDeviceName(deviceName);

                    Intent intent = new Intent(BROADCAST_SERVICE_CHANGE_DEVICENAME);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            });

            if (rtnDeviceNameChange == MessageType.ID_RTN_SUCCESS)// success
            {
            } else if (rtnDeviceNameChange == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
            {
            } else if (rtnDeviceNameChange == MessageType.ID_RTN_FAIL)// fail
            {
            }

            // Register Activation Change Callback Event
            long rtnActivation = 0;
            rtnActivation = IT100.setActivationCallback(new ActivationCallback() {
                @Override
                public void activation() {
                    IrisApplication.isActivate = true;

                    getActivationState();
                }

                @Override
                public void activationFailed() {
                }
            });
            if (rtnActivation == MessageType.ID_RTN_SUCCESS)// success
            {
            } else if (rtnActivation == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
            {
            } else if (rtnActivation == MessageType.ID_RTN_FAIL)// fail
            {
            }

            // Register Thermal Information Change Event
            long rtnThermal = 0;
            rtnThermal = IT100.setThermalStatusListener(new ThermalStatusListener() {
                @Override
                public void connect() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new BasicToast(getApplicationContext()).
                                    makeText("Thermal device connected").show();
                        }
                    });

                    IT100.getThermalJSONSetting(new ThermalSettingJSONCallback() {
                        @Override
                        public void onResult(JSONObject jobj) {

                            try {
                                Logger.d("LauncherService  Thermal onChange "+ jobj.toString(4));
                                editor.putInt(getString(R.string.prefer_key_thermal_mode),
                                        jobj.optBoolean(MessageKeyValue.THERMAL_ENABLE) ? 1:0);
                                editor.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

                @Override
                public void onChange(String jparams) {
                    Logger.d(jparams);
                    if(jparams!=null){
                        JSONObject jobj;
                        try {
                            jobj = new JSONObject(jparams);
                            editor.putInt(getString(R.string.prefer_key_thermal_mode),
                                    jobj.optBoolean(MessageKeyValue.THERMAL_ENABLE) ? 1:0);
                            editor.putString(getString(R.string.prefer_key_thermal_unit),
                                    jobj.optString(MessageKeyValue.THERMAL_UNIT));
                            editor.putString(getString(R.string.prefer_key_thermal_threshold),
                                    jobj.optString(MessageKeyValue.THERMAL_THRESHOLD));
                            editor.putString(getString(R.string.prefer_key_thermal_correction),
                                    jobj.optString(MessageKeyValue.THERMAL_CORRECTION));
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(BROADCAST_SERVICE_CHANGE_THERMAL);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                }

                @Override
                public void disconnect() {
                    editor.putInt(getString(R.string.prefer_key_thermal_mode), 0);
                    editor.commit();
//                    new BasicToast(getApplicationContext()).
//                            makeText("Thermal Device Disconnect").show();
                }
            });
            if (rtnThermal == MessageType.ID_RTN_SUCCESS)// success
            {
            } else if (rtnThermal == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
            {
            } else if (rtnThermal == MessageType.ID_RTN_FAIL)// fail
            {
            }

            getAdminUserStatus();
            // Register Change Event
            IT100.setChangeEventListener(new ChangeEventListener(){
                @Override
                public void changeEvent(String changeValue ){

                    switch (changeValue){
                        case MessageType.OperationMode:
                            IT100.getOperationMode(new OperationModeCallback(){
                                @Override
                                public void operationModeResult(String mode) {
                                    Intent intent = new Intent(BROADCAST_SERVICE_CHANGE_OPERATIONMODE);
                                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                }
                            });

                            break;

                        case MessageType.RelaySettings:
                            IT100.getRelaySettings(new RelaySettingCallback() {
                                @Override
                                public void onResult(JSONObject jsonObject) {
                                    boolean relayOn = jsonObject.optBoolean(MessageKeyValue.RELAY_SETTING_ENABLE);
                                    int timer = Integer.valueOf(jsonObject.optString(MessageKeyValue.RELAY_SETTING_TIMER, "0"));

                                    IrisApplication.relay_time_out = timer;

                                    editor.putInt(getString(R.string.prefer_key_relay_time_out), timer);
                                    editor.putInt(getString(R.string.prefer_key_relay_on), relayOn? 1:0);
                                    editor.commit();
                                }
                            });
                            break;

                    case MessageType.DeviceDeactivated: //"DeviceDeactivated":
                        editor.putBoolean(getString(R.string.prefer_key_init_pw), false);
                        editor.putBoolean(getString(R.string.prefer_key_activate_state), false);
                        editor.putInt(getResources().getString(R.string.prefer_key_login_error_count), 0);
                        editor.putLong(getResources().getString(R.string.prefer_key_login_error_time), 0);
                        editor.putString(getString(R.string.prefer_key_admin_login_type),
                                ConstData.ADMIN_AUTH_ID_PW);

                        editor.commit();
                        break;
						
					case MessageType.ModifyUserResponse: //"ModifyUserResponse":
                        getAdminUserStatus();
                        break;
                    }
                }
            } );
        }
    }

    private void getActivationState(){

        long rtnActivateState = 0;
        rtnActivateState = IT100.getActivationStatus(new ActivationStatusCallback() {

            @Override
            public void activationStatusResult(String activationType, String discoverITMS, String deviceActivated , String  serialNumber, String siteKeyPassPhrase , String APIKeyPassPhrase ,
                                               String  siteKey, String apiKey , String url) {
                IrisApplication.isActivate = deviceActivated.equals("true") ? true : false;
                IrisApplication.activateType = activationType;


                Intent intent = new Intent(BROADCAST_SERVICE_DEVICE_ACTIVATION);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });

        if ( rtnActivateState  == MessageType.ID_RTN_SUCCESS)// success
        {   }
        else if ( rtnActivateState  == MessageType.ID_RTN_NOT_OPENED_FAIL)// success
        {   }
        else if( rtnActivateState  == MessageType.ID_RTN_FAIL)// fail
        {   }
    }

    private void getAdminUserStatus() {
        IT100.getUserList(MessageKeyValue.USER_ROLE, MessageKeyValue.USER_ROLE_ADMINISTRATOR,
                0, 1000, new AllUserCallback() {
            @Override
            public void allUserResult(ArrayList<UserSimpleInfo> arrayList) {
                if (arrayList != null && arrayList.size() > 0) {
                    final UserSimpleInfo adminUser = arrayList.get(0);

                    if (!adminUser.status.equals(MessageKeyValue.USER_ACTIVE)) {
                        IrisApplication.isAdminActive = false;
                    } else {
                        IrisApplication.isAdminActive = true;
                    }
                }
            }
        });
    }
}
