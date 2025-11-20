package com.irisid.user.it100_sample.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.ActivationStatusCallback;
import com.irisid.it100.callback.CaptureStatusCallback;
import com.irisid.it100.callback.CheckThermalConnectionCallback;
import com.irisid.it100.callback.MaskSettingCallback;
import com.irisid.it100.callback.OperationModeCallback;
import com.irisid.it100.callback.RecogCallback;
import com.irisid.it100.callback.RecogWithThermalCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.it100.data.UserInfo;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;
import com.irisid.user.it100_sample.Service.LauncherService;
import com.irisid.user.it100_sample.UserList.ItemsListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_CHANGE_DEVICENAME;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_CHANGE_OPERATIONMODE;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_DEVICE_ACTIVATION;
import static com.irisid.user.it100_sample.IrisApplication.BROADCAST_SERVICE_START;

public class MainActivity extends BaseBackgroundActivity implements View.OnClickListener{

    SharedPreferences pref1;
    SharedPreferences.Editor editor;

    Calendar calendar;

    TextView time_txt;
    TextView date_txt;
    Button clock_in_btn;
    Button clock_out_btn;
    TextView txtDevicename;

    LinearLayout linear_date1;
    LinearLayout linear_date2;
    TextView time_txt2;
    TextView date_txt2;

    LinearLayout result_layout;
    int get_time_out;
    int thermal_onoff;

    static Thread myThread;
    static Runnable runnable;
    public static MainActivity self;
    public static Handler mResultHandler;

    static boolean active = false;

    Animation upAnim;
    Animation downAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(IrisApplication.value_recog_mode.equals(ConstData.RECOG_MODE_INTERACTIVE)) {
            setContentView(R.layout.activity_main);
            clock_in_btn = (Button) findViewById(R.id.clock_in_btn);
            clock_out_btn = (Button) findViewById(R.id.clock_out_btn);
            clock_in_btn.setOnClickListener(this);
            clock_out_btn.setOnClickListener(this);
        } else {
            setContentView(R.layout.activity_main_continuous);
        }

        self = this;

        pref1 = getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref1.edit();

        upAnim = AnimationUtils.loadAnimation(self, R.anim.load_fade_in);
        downAnim  = AnimationUtils.loadAnimation(self, R.anim.load_fade_out);

        findViewById(R.id.back_linear).setVisibility(View.INVISIBLE);
        findViewById(R.id.linear_logo).setOnClickListener(this);

        rootLayout = findViewById(R.id.rootview);
        videoView = (VideoView)findViewById(R.id.videoView);
        time_txt = (TextView) findViewById(R.id.time_txt);
        date_txt = (TextView) findViewById(R.id.date_txt);
        txtDevicename = (TextView)findViewById(R.id.txt_devicename);

        linear_date1 = (LinearLayout)findViewById(R.id.linear_date1);
        linear_date2 = (LinearLayout)findViewById(R.id.linear_date2);
        time_txt2 = (TextView) findViewById(R.id.time_txt2);
        date_txt2 = (TextView)findViewById(R.id.date_txt2);
        result_layout = (LinearLayout) findViewById(R.id.result_layout);
        if (!LauncherService.mServiceState) {
            Intent i = new Intent(this, LauncherService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(i);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_SERVICE_START);
        filter.addAction(BROADCAST_SERVICE_CHANGE_DEVICENAME);
        filter.addAction(BROADCAST_SERVICE_DEVICE_ACTIVATION);
        filter.addAction(BROADCAST_SERVICE_CHANGE_OPERATIONMODE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        IntentFilter datetimeFilter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        datetimeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        datetimeFilter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(m_timeChangedReceiver, datetimeFilter);

        mResultHandler = new Handler();
		isRecoging = false;

        if (savedInstanceState != null) {
            Boolean data = savedInstanceState.getBoolean(ConstData.CHECK_IS_RECOGING);
            isRecoging = data;
        }
    }

    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
                    action.equals(Intent.ACTION_DATE_CHANGED)) {
                doWork();
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ConstData.CHECK_IS_RECOGING, isRecoging);
    }
    protected void onResume() {
        super.onResume();

        get_time_out = pref1.getInt(getString(R.string.prefer_key_time_out), 1);
        thermal_onoff = pref1.getInt(getString(R.string.prefer_key_thermal_mode), 0);
        get_time_out = get_time_out * 1000;
        active = true;

        String tempMode = pref1.getString(getString(R.string.prefer_key_recog_mode),
                ConstData.RECOG_MODE_INTERACTIVE);

        if(pref!=null) {
            if (!tempMode.equals(IrisApplication.value_recog_mode)) {
                IrisApplication.value_recog_mode = tempMode;
                IrisApplication.restartApp(getApplicationContext());
            }
        }

        if(IrisApplication.restartMain) {
            IrisApplication.restartMain = false;
            IrisApplication.restartApp(getApplicationContext());
        }
        txtDevicename.setText(IrisApplication._deviceName);

        if(IT100.messageSender!=null) {
            getActivationState();
            getOperationMode();
            checkThermalConnection();
            getMaskSettings();
        }else{
            Intent i = new Intent(this, LauncherService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(i);
        }
        IrisApplication.cancelTimer();

        runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IrisApplication.display_lang = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            IrisApplication.display_lang = Resources.getSystem().getConfiguration().locale.getLanguage();
        }

        if(IT100.messageSender!=null)
            setAdminMode(false);
        if(result_layout!=null)
            result_layout.setVisibility(View.INVISIBLE);

        if(_dialogWaitBioCapture!=null)
            _dialogWaitBioCapture.dismiss();
        _dialogWaitBioCapture = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
        dateChecker=0;

        IT100.setCaptureStatusCallback(null);

        rtnCaptureStatus = -1;

        if(myThread != null)
            myThread.interrupt();
        myThread = null;
        mResultHandler.removeCallbacksAndMessages(null);

        if(recogHandler!=null) {
            recogHandler.removeCallbacksAndMessages(null);
            recogHandler = null;
        }
        editor.putBoolean(getString(R.string.prefer_key_activate_state), IrisApplication.isActivate);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        unregisterReceiver(m_timeChangedReceiver);
    }


    private void getActivationState(){

            long rtnActivateState = 0;
            rtnActivateState = IT100.getActivationStatus(new ActivationStatusCallback() {

                @Override
                public void activationStatusResult(String activationType, String discoverITMS, String deviceActivated , String  serialNumber, String siteKeyPassPhrase , String APIKeyPassPhrase ,
                                                   String  siteKey, String apiKey , String url) {
                    IrisApplication.isActivate = deviceActivated.equals("true") ? true : false;
                    IrisApplication.activateType = activationType;
                    registerCaptureCallback();
                }
            });

            if ( rtnActivateState  == MessageType.ID_RTN_SUCCESS)// success
            {   }
            else if ( rtnActivateState  == MessageType.ID_RTN_NOT_OPENED_FAIL)// success
            {   }
            else if( rtnActivateState  == MessageType.ID_RTN_FAIL)// fail
            {   }
    }

    private void getOperationMode() {
        IT100.getOperationMode(new OperationModeCallback(){
            @Override
            public void operationModeResult(String mode){

                if(mode.equals("")){
                    IT100.setOperationMode(MessageType.INTERACTIVE_MODE);
                    mode = ConstData.RECOG_MODE_INTERACTIVE;
                }

                editor.putString(getString(R.string.prefer_key_recog_mode), mode);
                editor.commit();

                if (!mode.equals(IrisApplication.value_recog_mode)) {
                    IT100.abortCapture();
                    IrisApplication.value_recog_mode = mode;
                    IrisApplication.restartApp(getApplicationContext());
                }
            }
        });
    }

    private void getMaskSettings() {
        IT100.getMaskSettings(new MaskSettingCallback() {
            @Override
            public void onResult(JSONObject jsonObject) {
                Boolean isMaskDetect = jsonObject.optBoolean(MessageKeyValue.MASK_DETECT_ENABLE, false);
                Boolean isAccessControl = jsonObject.optBoolean(MessageKeyValue.MASK_ACCESS_CONTROL, false);
                editor.putInt(getString(R.string.prefer_key_mask_mode), isMaskDetect? 1:0);
                editor.putInt(getString(R.string.prefer_key_mask_access), isAccessControl? 1:0);
                editor.commit();
            }
        });
    }


    private void checkThermalConnection() {
        IT100.checkThermalConnection(new CheckThermalConnectionCallback() {
            @Override
            public void onResult(final int i) {
                if(i!=0){
                    editor.putInt(getString(R.string.prefer_key_thermal_mode), 0);
                    editor.commit();
                    thermal_onoff = 0;
                }
            }
        });

    }

    private void getDeviceInfo() {

        long rtnDeviceName = 0;
        rtnDeviceName = IT100.getDeviceName();

        if (rtnDeviceName  == MessageType.ID_RTN_SUCCESS)// success
        {   }
        else if( rtnDeviceName  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
        {   }
        else if( rtnDeviceName  == MessageType.ID_RTN_FAIL)// fail
        {   }

        getActivationState();
        getOperationMode();
        checkThermalConnection();
    }

    public void playSound(Uri uri) {
        final MediaPlayer player = MediaPlayer.create(this, uri);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.release();
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }
    String mRecogType=ConstData.RECOG_TYPE_CLOCK_IN;
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.linear_logo: {
                Intent intent;
                isRecoging= false;
                IT100.abortCapture();
                if (IrisApplication.isActivate) {
                    if(IrisApplication.isAdminActive && IrisApplication.value_auth.equals(ConstData.ADMIN_AUTH_BIO)){
                        intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                        intent.putExtra(ConstData.INTENT_ADMIN_LOGIN_TYPE, ConstData.INTENT_ADMIN_ONLY_BIOMETRICS);
                        startActivity(intent);
                    }else if(!IrisApplication.isAdminActive || IrisApplication.value_auth.equals(ConstData.ADMIN_AUTH_ID_PW)){
                        intent = new Intent(MainActivity.this, IdPwActivity.class);
                        startActivity(intent);
                    }else{
                        intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    intent = new Intent(MainActivity.this, IdPwActivity.class);
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.fade, R.anim.hold);

                break;
            }

            case R.id.clock_in_btn:
            case R.id.clock_out_btn:
                if(!IrisApplication.isActivate)
                    return;

                if(CaptureActivity.self!=null) {
                    CaptureActivity.self.onResume();
                    return;
                }

                v.startAnimation(buttonClick);
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_CLOCK_IN_OUT_INTERACTIVE);
                if(v.getId() == R.id.clock_in_btn)
                    intent.putExtra(ConstData.RECOG_TYPE, ConstData.RECOG_TYPE_CLOCK_IN);
                else
                    intent.putExtra(ConstData.RECOG_TYPE, ConstData.RECOG_TYPE_CLOCK_OUT);
                //startActivity(intent);
                startActivityForResult(intent, 8000);

                break;

            default:
                break;
        }
    }

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    class CountDownRunner implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork(); // update date and time thread
                    Thread.sleep(10000);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    };

    static int dateChecker = 0;

    String strTime;
    String strDate;
    public void doWork() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    calendar = Calendar.getInstance();
                    strTime = DateUtils.formatDateTime(self, calendar.getTimeInMillis(),
                            DateUtils.FORMAT_SHOW_TIME);
                    strDate = DateUtils.formatDateTime(self, calendar.getTimeInMillis(),
                            DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE);

                    time_txt.setText(strTime);
                    date_txt.setText(strDate);
                    time_txt2.setText(strTime);
                    date_txt2.setText(strDate);

                    if(dateChecker==2){
                        dateChecker=0;
                        dateTimeLayoutChange();
                    }else if(dateChecker==0) {
                        dateChecker++;
                    }else{
                        dateChecker++;
                        dateTimeLayoutChange();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    long rtnCaptureStatus = -1;
    private LocalBroadcastReceiver mReceiver = new LocalBroadcastReceiver();
    public class LocalBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BROADCAST_SERVICE_START)){
                getDeviceInfo(); //Get info (Device name, activation )
                rtnCaptureStatus= -1;
                isRecoging = false;
            }else if(action.equals(BROADCAST_SERVICE_CHANGE_DEVICENAME)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtDevicename.setText(IrisApplication._deviceName);
                        }
                    });
            }else if(action.equals(BROADCAST_SERVICE_DEVICE_ACTIVATION)){
                registerCaptureCallback();
            }else if(action.equals(BROADCAST_SERVICE_CHANGE_OPERATIONMODE)){
                if(active)
                    getOperationMode();
            }

        }
    }

    private void registerCaptureCallback(){
        if(rtnCaptureStatus>-1)
            return;
        // MainActivity is active or not
        if(!active)
            return;

        rtnCaptureStatus = IT100.setCaptureStatusCallback(new CaptureStatusCallback() {
            @Override
            public void startPreview(String param){
                rtnCaptureStatus = IT100.setCaptureStatusCallback((null));

                if(_dialogWaitBioCapture!=null)
                    _dialogWaitBioCapture.dismiss();
                _dialogWaitBioCapture = null;

                Logger.e("MainActivity >> startPreview");
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_CLOCK_IN_CONTINUOUS);
                JSONObject jParam;
                if(param!=null) {
                    try {
                        jParam = new JSONObject(param);
                        //Logger.d("InRange@@ param is" + jParam.toString(4));
                        if (jParam.optBoolean(MessageKeyValue.CAPTURE_START_BY_RESTAPI)) {
                            intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_ENROLL_USER_BY_RESTAPI);
                        }
                        else if(jParam.optBoolean(MessageKeyValue.CAPTURE_START_BY_RECOG)){
                            intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_RECOG_BY_RESTAPI);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                intent.putExtra(ConstData.RECOG_TYPE,  ConstData.RECOG_TYPE_CLOCK_IN_CONTINUOUS);
                startActivityForResult(intent, 5000);
            }

            @Override
            public void stopPreview(){ }

            @Override
            public void inRange(String s) {
            }

            @Override
            public void outRange(String s) {
            }
        });
        if ( rtnCaptureStatus  == MessageType.ID_RTN_SUCCESS)// success
        {   }
        else if( rtnCaptureStatus  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
        {   }
        else if( rtnCaptureStatus  == MessageType.ID_RTN_FAIL)// fail
        {   }

        //if(IrisApplication.value_recog_mode.equals(ConstData.RECOG_MODE_CONTINUOUS)
        if(IrisApplication.value_recog_mode.contains(ConstData.RECOG_MODE_CONTINUOUS)
                && (IrisApplication.isActivate)) {
            if(!isRecoging) {
                if(CaptureActivity.self!=null) {

                    if(recogHandler!=null)
                        return;

                    recogHandler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            //what ever you do here will be done after 3 seconds delay.
                            requestRecogUser();
                            recogHandler = null;
                        }
                    };
                    recogHandler.postDelayed(r, 2000);
                }else {
                    isRecoging = true;
                    requestRecogUser();
                }
            }
        }
    }

    RecogWithThermalCallback mMainRecogWithThermalCallback = new RecogWithThermalCallback() {
        @Override
        public void fail(int result_value, UserInfo userInfo, byte[] leftIrisImage, byte[] rightIrisImage, byte[] faceImage,
                         double score, String time, double userTemperature, JSONObject jExtraInfo) {
            isRecoging = false;
           // Logger.d("MainActivity mMainRecogWithThermalCallback userTemperature is "+ userTemperature);
            if(CaptureActivity.self!=null) {
                CaptureActivity.self.mRecogWithThermalCallback.fail(result_value, userInfo, leftIrisImage, rightIrisImage,faceImage,
                        score, time, userTemperature, jExtraInfo);
                return;
            }

            if(userInfo.faceImage==null)
                userInfo.faceImage = faceImage;
            if(userInfo.rIrisImage==null)
                userInfo.rIrisImage = rightIrisImage;
            if(userInfo.lIrisImage==null)
                userInfo.lIrisImage = leftIrisImage;

            if(result_value == MessageType.ID_CAPTURE_INVALID_STATE){
                //if(IrisApplication.value_recog_mode.equals(ConstData.RECOG_MODE_CONTINUOUS)) {
                if(IrisApplication.value_recog_mode.contains(ConstData.RECOG_MODE_CONTINUOUS)) {

                    Handler handler=new Handler();
                    Runnable r=new Runnable() {
                        public void run() {
                            //what ever you do here will be done after 3 seconds delay.
                            requestRecogUser();
                        }
                    };
                    handler.postDelayed(r, 5000);
                }
            }else
                showResultDialog(result_value, userInfo, score, time);
        }

        @Override
        public void success(int result_value, UserInfo userInfo, byte[] auditFaceImage, double score, String time, double userTemperature, JSONObject jExtraInfo) {
            //Logger.d("MainActivity mMainRecogWithThermalCallback userTemperature is "+ userTemperature);
            isRecoging = false;
            if(CaptureActivity.self!=null) {
                CaptureActivity.self.mRecogWithThermalCallback.success(result_value, userInfo, auditFaceImage,
                        score, time, userTemperature, jExtraInfo);
                return;
            }
            showResultDialog(result_value, userInfo, score, time);
        }

        @Override
        public void onStatus(final int result_value, String msg) {
            if(CaptureActivity.self!=null) {
                CaptureActivity.self.mRecogWithThermalCallback.onStatus(result_value,msg);
                return;
            }

            if(result_value == MessageType.ID_WAIT_BIO_VERIFICATION
                    || result_value == MessageType.ID_CARD_VERIFIED_WAIT_AUDITIMAGE) {

                onWaitBioCapture();
                //wait

            }
        }
    };

    Handler recogHandler = null;
    RecogCallback mMainRecogCallback = new RecogCallback() {
        @Override
        public void fail(int result_value, UserInfo userInfo, byte[] leftIrisImage, byte[] rightIrisImage, byte[] faceImage, double score, String time, JSONObject jExtraInfo) {
            isRecoging = false;
            if(CaptureActivity.self!=null) {
                CaptureActivity.self.mRecogCallback.fail(result_value, userInfo, leftIrisImage, rightIrisImage,faceImage, score, time, jExtraInfo);
                return;
            }

            if(userInfo.faceImage==null)
                userInfo.faceImage = faceImage;
            if(userInfo.rIrisImage==null)
                userInfo.rIrisImage = rightIrisImage;
            if(userInfo.lIrisImage==null)
                userInfo.lIrisImage = leftIrisImage;

            if(result_value == MessageType.ID_CAPTURE_INVALID_STATE){
                //if(IrisApplication.value_recog_mode.equals(ConstData.RECOG_MODE_CONTINUOUS)) {
                if(IrisApplication.value_recog_mode.contains(ConstData.RECOG_MODE_CONTINUOUS)) {

                    if(recogHandler!=null)
                        return;

                    recogHandler = new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                //what ever you do here will be done after 5 seconds delay.
                                requestRecogUser();
                                recogHandler = null;
                            }
                        };
                    recogHandler.postDelayed(r, 5000);
                }
            }else
                showResultDialog(result_value, userInfo, score, time);
        }

        @Override
        public void success(int result_value, UserInfo userInfo,  byte[] auditFaceImage, double score, String time, JSONObject jExtraInfo) {
            isRecoging = false;
            if(CaptureActivity.self!=null) {
                CaptureActivity.self.mRecogCallback.success(result_value, userInfo,  auditFaceImage, score, time, jExtraInfo);
                return;
            }
            showResultDialog(result_value, userInfo, score, time);
        }

        @Override
        public void onStatus(final int result_value, String msg) {

            if(CaptureActivity.self!=null) {
                CaptureActivity.self.mRecogCallback.onStatus(result_value,msg);
                return;
            }
            if(result_value == MessageType.ID_WAIT_BIO_VERIFICATION
                    || result_value == MessageType.ID_CARD_VERIFIED_WAIT_AUDITIMAGE) {
                //wait
                onWaitBioCapture();
            }else if(result_value == MessageType.ID_WAIT_PACS_VERIFICATION) { // ReturnWaitPACSVerification
                new BasicToast(IrisApplication.getInstance())
                        .makeText(self.getResources().getString(R.string.wait_checking_permission)).show();  // wait_checking_permission
            }
        }
    };

    static boolean isRecoging = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IrisApplication.CaptureActivityValue) {
            if (ItemsListActivity.roleInDevice == null)
                return;

            if (ItemsListActivity.roleInDevice.equals(MessageKeyValue.USER_ROLE_SUPER_ADMINISTRATOR)
                    || ItemsListActivity.roleInDevice.equals(MessageKeyValue.USER_ROLE_ADMINISTRATOR)
                    || ItemsListActivity.roleInDevice.equals(MessageKeyValue.USER_ROLE_MANAGER)) {

                Intent intent = new Intent(MainActivity.this, SystemAdminActivity.class);
                startActivity(intent);
            }
        }
        Logger.d("requestCode + resultCode is "+ requestCode + "||"+ resultCode);
        if(resultCode == 5000){
            isRecoging = true;
        } else if(resultCode == 3001){
            IT100.abortCapture();
        } else
            isRecoging = false;

    }

    private void dateTimeLayoutChange(){
        if(linear_date2.getVisibility()==View.INVISIBLE) {
            linear_date2.setVisibility(View.VISIBLE);
            linear_date2.startAnimation(upAnim);
            linear_date1.startAnimation(downAnim);
            linear_date1.setVisibility(View.INVISIBLE);
        }else if(linear_date1.getVisibility()==View.INVISIBLE){
            linear_date1.setVisibility(View.VISIBLE);
            linear_date1.startAnimation(upAnim);
            linear_date2.startAnimation(downAnim);
            linear_date2.setVisibility(View.INVISIBLE);
        }
    }

    /* For Recognition Card in Continuous mode */
    public void showResultDialog(final int result_value, final UserInfo userInfo , final double score, final String time){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isRecoging = false;
                if(_dialogWaitBioCapture!=null)
                    _dialogWaitBioCapture.dismiss();
                _dialogWaitBioCapture = null;

                LinearLayout result_layout_iris = (LinearLayout)findViewById(R.id.result_iris);

                ImageView result_face_img = (ImageView) findViewById(R.id.result_face_img);
                ImageView result_iris_r = (ImageView) findViewById(R.id.result_iris_r);
                ImageView result_iris_l = (ImageView) findViewById(R.id.result_iris_l);
                TextView result_id_txt = (TextView) findViewById(R.id.result_id_txt);
                TextView result_name_txt = (TextView) findViewById(R.id.result_name_txt);
                TextView result_date_txt = (TextView) findViewById(R.id.result_date_txt);
                ImageView result_img = (ImageView) findViewById(R.id.result_img);
                TextView result_txt = (TextView) findViewById(R.id.result_txt);
                TextView score_txt = (TextView) findViewById(R.id.score_txt);
                TextView time_txt = (TextView) findViewById(R.id.result_time_txt);

                initResult();
                String user_id = "";
                String firstName = "";
                String lastName = "";

                byte[] leftIrisImage = null;
                byte[] rightIrisImage = null;
                byte[] faceImage = null;

                if(userInfo!=null){
                    user_id = userInfo.userID;
                    firstName = userInfo.firstName;
                    lastName = userInfo.lastName;

                    leftIrisImage = userInfo.lIrisImage;
                    rightIrisImage = userInfo.rIrisImage;
                    faceImage = userInfo.faceImage;
                }

                ////////////////////////// Common //////////////////////////
                result_layout.setVisibility(View.VISIBLE);
                //Log.e("userTemperature" , "userTemperature = " + userTemperature);

                //camera_line_img.setVisibility(View.VISIBLE);
                //camera_line_img.setBackgroundResource(R.drawable.camera_green_box_line);

                if (faceImage != null) {
                    Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceImage, 0, faceImage.length);
                    result_face_img.setImageBitmap(faceBitmap);
                }

                if (rightIrisImage != null) {
                    Bitmap right_iris_Bitmap = BitmapFactory.decodeByteArray(rightIrisImage, 0, rightIrisImage.length);
                    result_iris_r.setImageBitmap(right_iris_Bitmap);
                }

                if (leftIrisImage != null) {
                    Bitmap left_iris_Bitmap = BitmapFactory.decodeByteArray(leftIrisImage, 0, leftIrisImage.length);
                    result_iris_l.setImageBitmap(left_iris_Bitmap);
                }

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
                String strDate = dateFormat.format(calendar.getTime());
                result_date_txt.setText(strDate);

                Double dTime = 0.0;
                if (time != null && !time.isEmpty()) {
                    dTime = Double.valueOf(time);
                }
                score_txt.setText(String.format("%.2f", score));
                time_txt.setText(String.format("%.2fs", dTime));

                ////////////////////////// ///// //////////////////////////
                if(result_value == MessageType.ID_SUCCESS || result_value == MessageType.ID_CARD_VERIFIED
                        || result_value == MessageType.ID_ACCESS_GRANTED){
                    //Logger.d("MainActivity >>  showResultDialog  .. SUCCESS");

                    result_img.setImageResource(R.drawable.icon_identified_success);
                    result_txt.setTextColor(getResources().getColor(R.color.captureSuccessText, null));
                    if(result_value == MessageType.ID_SUCCESS)
                        result_txt.setText(getResources().getText(R.string.recog_success_0));
                    else if(result_value == MessageType.ID_CARD_VERIFIED)
                        result_txt.setText(getResources().getText(R.string.recog_success_card_verified_11091006));
                    else if (result_value == MessageType.ID_ACCESS_GRANTED)
                        result_txt.setText(getResources().getText(R.string.recog_success_access_granted_11091036));


                    result_id_txt.setText(user_id);
                    result_name_txt.setText(firstName + " " + lastName);

                    result_id_txt.setVisibility(View.VISIBLE);
                    result_name_txt.setVisibility(View.VISIBLE);
                    result_layout_iris.setVisibility(View.GONE);

//            if (pref.getInt(getString(R.string.prefer_key_relay_on), 1) == 1)
//                irisApplication.relayOpen();

                    mResultHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Dismiss Result dialog
                            result_layout.setVisibility(View.GONE);

                            //if(IrisApplication.value_recog_mode.equals(ConstData.RECOG_MODE_CONTINUOUS)) {
                            if(IrisApplication.value_recog_mode.contains(ConstData.RECOG_MODE_CONTINUOUS)) {
                                requestRecogUser();
                            }
                        }
                    }, get_time_out);

                }else {
                    //Logger.d("MainActivity >>  showResultDialog  .. FAIL");
                    result_layout_iris.setVisibility(View.VISIBLE);
                    result_img.setImageResource(R.drawable.icon_identified_failed);

                    if (result_value == MessageType.ID_FAKE_EYE_DETECTED)
                        result_txt.setText(getResources().getText(R.string.recog_fail_fake_eye_detected_11071010));
                    else if(result_value == MessageType.ID_FAKE_EYE_DETECTED_FAILED)
                        result_txt.setText(getResources().getText(R.string.recog_fail_fake_eye_detect_fail_11071011));
                    else if (result_value == MessageType.ID_INACTIVE_USER)
                        result_txt.setText(getResources().getText(R.string.recog_fail_inactive_user_11091003));
                    else if (result_value == MessageType.ID_CARD_NOT_VERIFIED)
                        result_txt.setText(getResources().getText(R.string.recog_fail_card_not_verified_11091007));
                    else if (result_value == MessageType.ID_BIO_CARD_MISMATCH)
                        result_txt.setText(getResources().getText(R.string.recog_fail_bio_card_mismatch_11091010));
                    else if (result_value == MessageType.ID_WAIT_CARD_TIMEOUT)
                        result_txt.setText(getResources().getText(R.string.recog_fail_wait_card_timeout_11091012));
//                    else if (result_value == MessageType.ID_WAIT_BIO_TIMEOUT)
//                        result_txt.setText(getResources().getText(R.string.recog_fail_wait_bio_timeout_11091011));
                    else if (result_value == MessageType.ID_CAPTURE_TIMEOUT)
                        result_txt.setText(getResources().getText(R.string.capture_fail_timeout));
                    else if (result_value == MessageType.ID_MASK_REQUIRED)
                        result_txt.setText(getResources().getText(R.string.recog_fail_wear_mask));
                    else if (result_value == MessageType.ID_USER_TEMPERATURE_HIGH)
                        result_txt.setText(getResources().getText(R.string.recog_fail_temperature_high));
                    else if (result_value == MessageType.ID_POLICY_NOT_GRANTED)
                        result_txt.setText(getResources().getText(R.string.recog_fail_policy_not_granted_11091023));
                    else if (result_value == MessageType.ID_PACS_TIMEOUT)  //PACS timed out
                        result_txt.setText(getResources().getText(R.string.recog_fail_pacs_timeout_11091014));
                    else if (result_value == MessageType.ID_ACCESS_NOT_GRANTED)
                        result_txt.setText(getResources().getText(R.string.recog_fail_access_not_granted_11091037));
                    else
                        result_txt.setText(getResources().getText(R.string.capture_fail_identify));

                    result_txt.setTextColor(getResources().getColor(R.color.captureFailText, null));
                    result_id_txt.setVisibility(View.GONE);
                    result_name_txt.setVisibility(View.GONE);

//            long rtnRelay = 0;
//            rtnRelay = IT100.setRelay(0);
//            if (rtnRelay == MessageType.ID_RTN_SUCCESS)// success
//            {
//            } else if (rtnRelay == MessageType.ID_RTN_WRONG_PARA)// fail
//            {
//            } else if (rtnRelay == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
//            {
//            } else if (rtnRelay == MessageType.ID_RTN_FAIL)// fail
//            {
//            }
                    mResultHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Dismiss Result dialog
                            result_layout.setVisibility(View.GONE);

                            //if(IrisApplication.value_recog_mode.equals(ConstData.RECOG_MODE_CONTINUOUS)) {
                            if(IrisApplication.value_recog_mode.contains(ConstData.RECOG_MODE_CONTINUOUS)) {
                                requestRecogUser();
                            }
                        }
                    }, get_time_out);
                }
            }
        });
    }

    public void initResult(){
        ImageView result_face_img = (ImageView)findViewById(R.id.result_face_img);
        ImageView result_iris_r = (ImageView)findViewById(R.id.result_iris_r);
        ImageView result_iris_l = (ImageView)findViewById(R.id.result_iris_l);

        result_face_img.setImageBitmap(null);
        result_iris_r.setImageBitmap(null);
        result_iris_l.setImageBitmap(null);
    }

    private void setAdminMode(boolean enable){
        JSONObject jparam = new JSONObject();
        try {
            jparam.put(MessageKeyValue.ADMIN_MODE_ENALBE, enable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setAdminMode(jparam.toString());

    }

    private void requestRecogUser(){
      //  isRecoging = true;
        mResultHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRecoging = true;
                if(thermal_onoff==0)
                    IT100.recogUser(ConstData.RECOG_TYPE_CLOCK_IN, mMainRecogCallback);
                else
                    IT100.recogUserWithThermal(ConstData.RECOG_TYPE_CLOCK_IN, mMainRecogWithThermalCallback);
            }
        }, 300);

    }
    static Dialog _dialogWaitBioCapture=null;
    AnimationDrawable frameAnimation;
    private void onWaitBioCapture() {
        if(_dialogWaitBioCapture!=null)
            return;

        _dialogWaitBioCapture = new Dialog(this, R.style.myDialog);
        _dialogWaitBioCapture.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogWaitBioCapture.setCancelable(true);
        _dialogWaitBioCapture.setContentView(R.layout.dialog_wait_capture_bio);

        ImageView imgEnroll = (ImageView) _dialogWaitBioCapture.findViewById(R.id.img_card_scan);
        imgEnroll.setBackgroundResource(R.drawable.anim_wait_bio);
        frameAnimation = (AnimationDrawable) imgEnroll.getBackground();
        frameAnimation.start();

        _dialogWaitBioCapture.show();

        playCaptureResult(R.raw.move_closer, -1);
    }

    private void playCaptureResult(int resid, int volume) {

        MediaPlayer mediaPlayer = MediaPlayer.create(self, resid);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mp = null;
            }
        });
    }
}