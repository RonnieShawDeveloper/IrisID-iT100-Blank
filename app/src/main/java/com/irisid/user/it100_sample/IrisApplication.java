package com.irisid.user.it100_sample;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.View;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.DoorInfoCallback;
import com.irisid.it100.callback.DoorInterfaceCallback;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Activity.MainActivity;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample_project.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class IrisApplication extends Application {

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";

    public static final String BROADCAST_SERVICE_START = "service_start";
    public static final String BROADCAST_SERVICE_CHANGE_DEVICENAME = "change_devicename";
    public static final String BROADCAST_SERVICE_DEVICE_ACTIVATION = "device_activate";
    public static final String BROADCAST_SERVICE_CHANGE_OPERATIONMODE = "change_operationmode";
    public static final String BROADCAST_SERVICE_CHANGE_THERMAL = "change_thermal";

    private static IrisApplication instance;
    public static final int CaptureActivityValue = 200 ;

    int brightness;
    int volume;
    int current_brightness;
    int current_volume;

    public static String video_get_clickFilePath;
    public static String image_get_clickFilePath;
    public static String display_lang;
    public static String value_auth = ConstData.ADMIN_AUTH_ID_PW;
    public static String value_recog_mode = ConstData.RECOG_MODE_INTERACTIVE;
    public static int relay_time_out =2;
    public static Boolean isActivate = false;
    public static Boolean isDebug = false;
    public static Boolean isAdminActive = false;
    public static String _deviceName ="";
    public static String activateType = "";
    public static Boolean restartMain = false;

    SharedPreferences pref; // background saved value
    SharedPreferences pref2; //  settings saved value
    SharedPreferences.Editor editor2;

    @Override
    public void onCreate() {
        super.onCreate();
        setPreferenceValue();
        instance = this;
    }

    public static IrisApplication getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        long rtnClose = 0 ;
        rtnClose = IT100.close();

        if ( rtnClose  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnClose  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnClose  == MessageType.ID_RTN_FAIL){}// fail
    }

    private void setPreferenceValue(){

        pref = getSharedPreferences(getString(R.string.prefer_name_wallpaper_path), MODE_PRIVATE);
        video_get_clickFilePath = pref.getString(getString(R.string.prefer_key_video_filepath), null);
        image_get_clickFilePath = pref.getString(getString(R.string.prefer_key_image_filepath), null);
//        Logger.e("MAIN FIRST >>", "image_get_clickFilePath : " + image_get_clickFilePath);

        pref2 = getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor2 = pref2.edit();

        display_lang = pref2.getString("display_lang", "en");
        brightness = pref2.getInt(getString(R.string.prefer_key_brightness), 0);
        volume = pref2.getInt(getString(R.string.prefer_key_volume), 0);
        //value_auth = pref2.getString("value_auth", AUTH_TYPE_ALL);
        value_auth = pref2.getString(getString(R.string.prefer_key_admin_login_type), ConstData.ADMIN_AUTH_ID_PW);
        _deviceName = pref2.getString(getString(R.string.prefer_key_device_name), "");

        value_recog_mode = pref2.getString(getString(R.string.prefer_key_recog_mode), ConstData.RECOG_MODE_INTERACTIVE);
        isActivate = pref2.getBoolean(getString(R.string.prefer_key_activate_state), false);
        relay_time_out = pref2.getInt(getString(R.string.prefer_key_relay_time_out), 2);

        Locale sysLocale = getResources().getConfiguration().locale;
        String strLanguage = sysLocale.getLanguage();

        if(!display_lang.equals(strLanguage)) {

            if(strLanguage.equals("en") || strLanguage.equals("ko") || strLanguage.equals("fr")
                || strLanguage.equals("zh") || strLanguage.equals("ja") || strLanguage.equals("de")
                || strLanguage.equals("es") || strLanguage.equals("it")) {
                editor2.putString("display_lang", strLanguage);
                editor2.apply();
            }
            display_lang = pref2.getString("display_lang", "en");
//            Logger.e("TAG >>>>>>>>>>>>>>", "Changed >>>  Get display_lang pref2 : "+display_lang);
        }

        try {
            current_brightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(brightness > 255) { // auto
            editor2.putInt(getString(R.string.prefer_key_brightness), 256);
            editor2.apply();
        }else if(brightness != current_brightness){
            editor2.putInt(getString(R.string.prefer_key_brightness), current_brightness);
            editor2.apply();
        }

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        current_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(volume != current_volume) {
            editor2.putInt(getString(R.string.prefer_key_volume), current_volume);
            editor2.apply();
        }
    }

    public static boolean activateBackButton(final Activity activity, int backbtnID) {
        View lv;
        try {
            lv=(View)activity.findViewById(backbtnID);
            lv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    activity.finish();
                }
            });

            return true;
        } catch(Exception e) {
            return false;
        } finally {
            lv=null;
        }
    }

    public static Timer mTimer = null;
    public void resetTimer() {
        int time_out = 60000 * pref2.getInt(getString(R.string.prefer_key_time_out_admin), 4);

        if(mTimer!=null){
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                IT100.abortCapture();
                clearAllOtherActivity(MainActivity.self);
            }
        }, time_out);
    }

    public static void cancelTimer(){

        if(mTimer!=null){
            mTimer.cancel();
            mTimer.purge();
        }
    }

    public static Timer mTimer2 = null;
    public void relayOpen(){
        long rtnRelay = 0;
        rtnRelay = IT100.setRelay(1);

        if (rtnRelay == MessageType.ID_RTN_SUCCESS) {
        }// success
        else if (rtnRelay == MessageType.ID_RTN_WRONG_PARA) {
        }// fail
        else if (rtnRelay == MessageType.ID_RTN_NOT_OPENED_FAIL) {
        }// fail
        else if (rtnRelay == MessageType.ID_RTN_FAIL) {
        }// fail

        int time_out = 1000* relay_time_out;//pref2.getInt("relay_time_out", 2);
        if(mTimer2!=null){
            mTimer2.cancel();
            mTimer2.purge();
        }
        mTimer2 = new Timer();
        mTimer2.schedule(new TimerTask() {
            @Override
            public void run() {
                //Logger.d("Relay timer  000000");
                long rtnRelay = 0;
                rtnRelay = IT100.setRelay(0);
                if (rtnRelay == MessageType.ID_RTN_SUCCESS) {
                }// success
                else if (rtnRelay == MessageType.ID_RTN_WRONG_PARA) {
                }// fail
                else if (rtnRelay == MessageType.ID_RTN_NOT_OPENED_FAIL) {
                }// fail
                else if (rtnRelay == MessageType.ID_RTN_FAIL) {
                }// fail
            }
        }, time_out);

    }

    public HashSet<Activity > activityHashSet=new HashSet<Activity>();

    public void registerActivity(Activity activity) {
        activityHashSet.add(activity);
    }

    public void unregisterActivity(Activity activity) {
        activityHashSet.remove(activity);
    }

    public void clearAllOtherActivity(Activity exceptActivity)  {
        ArrayList<Activity> activityList=new ArrayList<Activity>();
        Iterator<Activity> it=activityHashSet.iterator();
        Activity activity;
        try {
            while (it.hasNext()) {

                activity = it.next();
                if (activity != exceptActivity) {
                    activityList.add(activity);
                    try {
                        activity.finish();
                    } catch (Exception e) {

                    }
                }
            }
        } catch(Exception e){

        }

        for(int i=0;i<activityList.size();i++) {
            activityHashSet.remove(activityList.get(i));
        }

    }


    public static void restartApp(Context context){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        if(intent == null)
            return;

        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);//IntentCompat.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        if (MainActivity.self != null)
            MainActivity.self.finishAffinity();

    }

    public void setDeviceName(String deviceName) {
        _deviceName = deviceName;
        editor2.putString(getString(R.string.prefer_key_device_name), deviceName);
        editor2.apply();
    }

    public void doorOpen(){
        long rtnDoor = 0;
        rtnDoor = IT100.openDoor(new DoorInfoCallback() {
            @Override
            public void doorInfoResult(int resultCode, String resultMsg) {

            }
        });

        if (rtnDoor == MessageType.ID_RTN_SUCCESS) {
        }// success
        else if (rtnDoor == MessageType.ID_RTN_WRONG_PARA) {
        }// fail
        else if (rtnDoor == MessageType.ID_RTN_NOT_OPENED_FAIL) {
        }// fail
        else if (rtnDoor == MessageType.ID_RTN_FAIL) {
        }// fail

        int time_out = 1000* relay_time_out;//pref2.getInt("relay_time_out", 2);
        if(mTimer2!=null){
            mTimer2.cancel();
            mTimer2.purge();
        }
        mTimer2 = new Timer();
        mTimer2.schedule(new TimerTask() {
            @Override
            public void run() {
                //Logger.d("Relay timer  000000");
                long rtnDoor = 0;
                rtnDoor = IT100.closeDoor(new DoorInfoCallback() {
                    @Override
                    public void doorInfoResult(int i, String s) {

                    }
                });
                if (rtnDoor == MessageType.ID_RTN_SUCCESS) {
                }// success
                else if (rtnDoor == MessageType.ID_RTN_WRONG_PARA) {
                }// fail
                else if (rtnDoor == MessageType.ID_RTN_NOT_OPENED_FAIL) {
                }// fail
                else if (rtnDoor == MessageType.ID_RTN_FAIL) {
                }// fail
            }
        }, time_out);

    }

//    private void setDoorInterface(){
//        IT100.setDoorInterfaceSettings("Front Door", true, false);
//    }

    private void getDoorInterface(){
        IT100.getDoorInterfaceSettings(new DoorInterfaceCallback() {
            @Override
            public void onResult(String doorName, boolean enableRelay, boolean enableWiegand) {

            }
        });
    }
}