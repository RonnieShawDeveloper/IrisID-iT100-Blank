package com.irisid.user.it100_sample.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.BiometricsInfoCallback;
import com.irisid.it100.callback.CaptureStatusCallback;
import com.irisid.it100.callback.RecogCallback;
import com.irisid.it100.callback.RecogWithThermalCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.it100.data.UserInfo;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.Common.ui.BorderTextView;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;
import com.irisid.user.it100_sample.UserList.Item;
import com.irisid.user.it100_sample.UserList.ItemsListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.irisid.user.it100_sample.Activity.MainActivity.isRecoging;

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener
{
    LinearLayout linear_back;
    LinearLayout result_layout;
    LinearLayout result_layout_iris;
    LinearLayout linear_capture_state;

    ImageView camera_line_img;
    ImageView result_face_img;
    ImageView result_iris_r;
    ImageView result_iris_l;
    TextView result_id_txt;
    TextView result_name_txt;
    TextView result_date_txt;
    ImageView result_img;
    TextView result_txt;
    TextView score_txt;
    TextView time_txt;
    TextView thermal_txt;
    ImageView img_anim_center;
    static BorderTextView txt_guide;

    ImageView img_capture_state;
    ImageView img_glasses_off;
    ImageView img_mask_off;

    String m_State; // capture type ( enroll, recog, etc....)
    static String m_recogType; // Recog type: Clock in, Clock out, System admin

    public final int GODOWN =0;
    public final int GOUP =1;
    public final int GORIGHT = 2;
    public final int GOLEFT = 3;
    public final int GOFAR =4;
    public final int GOCLOSE = 5;
    public static int directState=-1;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    SurfaceHolder holder;
    public static SurfaceView m_surfaceView = null;

    int get_time_out;
    String thermal_threshold;
    String thermal_unit;
    int thermal_onoff;
    int thermal_access;
    int thermal_alarm;
    int position_guide;
    int voice_guide;
    int mask_access;
    int mask_voice;
    int mask_detect;
	int enroll_guide;
	
    Double _userTemperature = 0.0;
    Boolean _maskOn = false;
    static Handler delayHandler;

    static SoundPool sound_pool = null;
    static int sound_focus;
    static int sound_thermal_alert;

    boolean isInRange= false;
    public String _guid;

    public static CaptureActivity self = null;
    IrisApplication irisApplication = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        self = this;

        irisApplication=  (IrisApplication) getApplication();
        irisApplication.registerActivity(this);
        irisApplication.resetTimer();

        overridePendingTransition(0,0 );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capture);

        findViewById(R.id.linear_logo).setOnClickListener(this);

        setDecorView();

        findViewById(R.id.root_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                findViewById(R.id.root_layout).getWindowVisibleDisplayFrame(r);
                int heightDiff = findViewById(R.id.root_layout).getRootView().getHeight() - (r.bottom - r.top);

                if(heightDiff>20){
                    setDecorView();
                }
            }
        });
		
        pref = getSharedPreferences(getString(R.string.prefer_name_setting), MODE_PRIVATE);
        editor = pref.edit();

        // ui success duration
        get_time_out = pref.getInt(getString(R.string.prefer_key_time_out), 1);
        get_time_out = get_time_out * 1000;
        thermal_threshold = pref.getString(getString(R.string.prefer_key_thermal_threshold), "38");
        thermal_unit = pref.getString(getString(R.string.prefer_key_thermal_unit), MessageKeyValue.THERMAL_UNIT_C);
        thermal_onoff = pref.getInt(getString(R.string.prefer_key_thermal_mode), 0);
        thermal_access = pref.getInt(getString(R.string.prefer_key_thermal_access), 0);
        thermal_alarm = pref.getInt(getString(R.string.prefer_key_thermal_alarm), 0);
        position_guide = pref.getInt(getString(R.string.prefer_key_position_guide), 0);
        voice_guide = pref.getInt(getString(R.string.prefer_key_voice_guide), 0);
        mask_detect = pref.getInt(getString(R.string.prefer_key_mask_mode), 1);
        mask_access = pref.getInt(getString(R.string.prefer_key_mask_access), 1);
        mask_voice = pref.getInt(getString(R.string.prefer_key_mask_voice_guide), 1);
        enroll_guide = pref.getInt(getString(R.string.prefer_key_enroll_guide), 1);

        Intent intent = getIntent();
        m_State = intent.getStringExtra(ConstData.CAPTURE_STATE);
        if(m_State == null)
            m_State = "null";

        m_recogType = intent.getStringExtra(ConstData.RECOG_TYPE);
        if(m_recogType == null)
            m_recogType = ConstData.RECOG_TYPE;

        if(m_State.equals(ConstData.CAPTURE_STATE_MODIFY_BIO)){
            setEnrollCaptureUI();
        }else if(m_State.equals(ConstData.CAPTURE_STATE_ENROLL_USER_BY_RESTAPI)) {
            if(voice_guide ==1 && enroll_guide ==1)
                playCaptureResult(R.raw.remove_glasses_mask, -1, true);
            setEnrollCaptureUI();
        }else if(m_State.equals(ConstData.CAPTURE_STATE_CLOCK_IN_CONTINUOUS)) {

        }else if(m_State.equals(ConstData.CAPTURE_STATE_SYSTEM_ADMIN)){

        } else if (m_State.equals(ConstData.CAPTURE_STATE_RECOG_BY_RESTAPI)) {

        }

        else if (m_State.equals(ConstData.CAPTURE_STATE_CLOCK_IN_OUT_INTERACTIVE)) {
            findViewById(R.id.back_linear).setVisibility(View.INVISIBLE);
        } else {
            setEnrollCaptureUI();
            initUserItem();
        }
        delayHandler = new Handler();

        initView();

        m_surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = m_surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {

                    long rtnDisplay = 0;
                    rtnDisplay = IT100.setPreviewDisplay(surfaceHolder.getSurface(),MessageType.BG_BLUR);

                    if ( rtnDisplay  == MessageType.ID_RTN_SUCCESS)// success
                    {   }
                    else if( rtnDisplay  == MessageType.ID_RTN_WRONG_PARA)// fail
                    {   }
                    else if( rtnDisplay  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
                    {   }
                    else if( rtnDisplay  == MessageType.ID_RTN_FAIL)// fail
                    {   }

                    long rtnCaptureStatus = IT100.setCaptureStatusCallback(new CaptureStatusCallback() {
                        @Override
                        public void startPreview(String param) {
                            isInOutStatus = false; //test

                            JSONObject jParam;
                            if(param!=null) {
                                try {
                                    jParam = new JSONObject(param);
                                    if (jParam.optBoolean(MessageKeyValue.CAPTURE_START_BY_RESTAPI)) {
                                        m_State =  ConstData.CAPTURE_STATE_ENROLL_USER_BY_RESTAPI;
                                        if(voice_guide ==1 && enroll_guide ==1)
                                            playCaptureResult(R.raw.remove_glasses_mask, -1, true);
                                        setEnrollCaptureUI();

                                        if(delayHandler!=null) {
                                            delayHandler.removeCallbacksAndMessages(null);
                                        }
                                        result_layout.setVisibility(View.GONE);

                                        if(countTimer!=null)
                                            countTimer.onFinish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            directState=-1;
                        }
                        @Override
                        public void stopPreview(){
                            isInOutStatus = false; 
                            if(IrisApplication.value_recog_mode.equals(ConstData.RECOG_MODE_CONTINUOUS)){
                                if(!(m_State.equals(ConstData.CAPTURE_STATE_ENROLL_USER_BY_RESTAPI))) {
                                    Intent resultIntent = new Intent();
                                    setResult(5000, resultIntent);
                                    captureFinish(false);
                                }else
                                    captureFinish(false);
                            }else{
                                if((m_State.equals(ConstData.CAPTURE_STATE_ENROLL_USER_BY_RESTAPI))) {
                                    captureFinish(false);
                                }else if(m_State.equals(ConstData.CAPTURE_STATE_MODIFY_BIO)||
                                        m_State.equals(ConstData.CAPTURE_STATE_NEW_USER)) {
                                    captureFinish(false);
                                } else if (m_State.equals(ConstData.CAPTURE_STATE_RECOG_BY_RESTAPI)) {
                                    captureFinish(false);
                                }
                            }
                            isPlayCamera = false;
                        }

                        @Override
                        public void inRange(final String param){
                            isInOutStatus = true; 
                            arrowImageVisible(View.GONE);
                            directState = -1;
                            /*
                             inRange jparam is
                             {
                                "type" : "tof|face"
                             }
                             */

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject jParam;
                                    try {
                                        jParam = new JSONObject(param);
                                        if(jParam.optString(MessageKeyValue.CAPTURE_TYPE, MessageKeyValue.CAPTURE_TYPE_FACE).equals(MessageKeyValue.CAPTURE_TYPE_FACE)){

                                            camera_line_img.setBackgroundResource(R.drawable.camera_green_box_line);
                                            camera_line_img.setVisibility(View.VISIBLE);
                                            if (isInRange != true) {
                                                if (sound_pool == null) {
                                                    sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                                                    sound_focus = sound_pool.load(self, R.raw.focus, 1);
                                                } else
                                                    sound_pool.play(sound_focus, 1, 1, 0, 0, 0);
                                            }
                                            isInRange = true;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(linear_capture_state.getVisibility()== View.VISIBLE){
                                        img_capture_state.setBackgroundTintList(getColorStateList(R.color.white));
                                        img_glasses_off.setImageTintList(getColorStateList(R.color.white));
                                        img_mask_off.setImageTintList(getColorStateList(R.color.white));
                                    }
                                }
                            });
                        }

                        @Override
                        public void outRange(final String param) {
                            /*
                             outRange jparam is
                             {
                                "type" : "tof"|"face",
                                "outPosition":{

                                    "far": true|false,
                                    "close": true|false,
                                    "left": true|false,
                                    "right": true|false,
                                    "up": true|false,
                                    "down": true|false,
                                    "dist" : (int)
                                }
                             }
                             */
                            //Logger.d("CaptureActivity >> outRange " + isInRange);
							runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isInOutStatus = true;
                                    arrowImageVisible(View.GONE);
                                    if(linear_capture_state.getVisibility()== View.VISIBLE){
                                        img_capture_state.setBackgroundTintList(getColorStateList(R.color.disableText));
                                        img_glasses_off.setImageTintList(getColorStateList(R.color.disableText));
                                        img_mask_off.setImageTintList(getColorStateList(R.color.disableText));
                                    }
                                    JSONObject jParam;
                                    JSONObject jDirection;
                                    try {
                                        jParam = new JSONObject(param);
                                        if(jParam.optString(MessageKeyValue.CAPTURE_TYPE, MessageKeyValue.CAPTURE_TYPE_FACE).equals(MessageKeyValue.CAPTURE_TYPE_FACE)){
                                            isInRange = false;
                                            camera_line_img.setBackgroundResource(R.drawable.camera_orange_box_line);
                                            camera_line_img.setVisibility(View.VISIBLE);
                                        }

                                        if(jParam.has(MessageKeyValue.CAPTURE_OUT_POSITION) && (position_guide==1 || voice_guide==1)){

                                            jDirection = new JSONObject(jParam.optString(MessageKeyValue.CAPTURE_OUT_POSITION));

                                            if (jDirection.optBoolean(MessageKeyValue.CAPTURE_OUT_POSITION_CLOSE)&& directState!=GOFAR) {
                                                //Logger.d("far away");
                                                UIDirection(GOFAR);
                                                directState = GOFAR;
                                            }else if (jDirection.optBoolean(MessageKeyValue.CAPTURE_OUT_POSITION_FAR) && directState!=GOCLOSE){
                                                //Logger.d("close your eye");
                                                UIDirection(GOCLOSE);
                                                directState = GOCLOSE;
                                            } else {
                                                if ((jDirection.optInt(MessageKeyValue.CAPTURE_OUT_POSITION_DIST) >0) && (jDirection.optInt(MessageKeyValue.CAPTURE_OUT_POSITION_DIST) < 300)) {
                                                    //Logger.d("far away");
                                                    UIDirection(GOFAR);
                                                    directState = GOFAR;
                                                    return;
                                                }

                                                if(jDirection.optBoolean(MessageKeyValue.CAPTURE_OUT_POSITION_RIGHT) && directState!=GOLEFT) {
                                                    //Logger.d("go left");
                                                    UIDirection(GOLEFT);
                                                    directState = GOLEFT;
                                                }
                                                if(jDirection.optBoolean(MessageKeyValue.CAPTURE_OUT_POSITION_LEFT)&& directState!=GORIGHT) {
                                                    //Logger.d("go right");
                                                    UIDirection(GORIGHT);
                                                    directState = GORIGHT;
                                                }
                                                if(jDirection.optBoolean(MessageKeyValue.CAPTURE_OUT_POSITION_UP)&& directState!=GODOWN) {
                                                    //Logger.d("go down");
                                                    UIDirection(GODOWN);
                                                    directState = GODOWN;
                                                }
                                                if(jDirection.optBoolean(MessageKeyValue.CAPTURE_OUT_POSITION_DOWN)&& directState!=GOUP) {
                                                    //Logger.d("go up");
                                                    UIDirection(GOUP);
                                                    directState = GOUP;
                                                }
                                            }
                                        }else{
                                            directState = -1;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    });
                    if ( rtnCaptureStatus  == MessageType.ID_RTN_SUCCESS)// success
                    {   }
                    else if( rtnCaptureStatus  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
                    {   }
                    else if( rtnCaptureStatus  == MessageType.ID_RTN_FAIL)// fail
                    {   }

                    if (m_State.equals(ConstData.CAPTURE_STATE_MODIFY_BIO)) {
                        final Item userItem = ItemsListActivity.userItem;
                        final boolean selectFace = userItem.isCaptureFace();
                        final boolean selectIris = userItem.isCaptureIris();

                        _guid = userItem.guid;

                        if (_guid != null && !_guid.equals("")) {
                            long rtnCapture = 0;

                            rtnCapture = IT100.captureBiometrics(_guid, new BiometricsInfoCallback() {
                                @Override
                                public void biometricsInfoResult(int resultCode, String userid, byte[] leftIrisImage, byte[] rightIrisImage,
                                                                 byte[] faceImage, byte[] faceSmallImage, String leftIrisCode, String rightIrisCode, String faceCode,
                                                                 String jsonCaptureStatus) {

                                    if (resultCode == MessageType.ID_DUPLICATED_USER_FOUND) { //ERROR_MATCHER_DUPLICATED_USER_FOUND //everything is OK and duplicated

                                        if(userItem.getActive() && userid.equals(userItem.getUserId()))
                                            ItemsListActivity.result_value = 0;
                                        else {
                                            initUserItem();
                                            ItemsListActivity.result_value = MessageType.ID_DUPLICATED_USER_FOUND;
                                        }

                                        if(selectFace) {
                                            userItem.setFace_img(faceImage);
                                            userItem.setFace_small_img(faceSmallImage);
                                            userItem.setFace_code(faceCode);
                                        }

                                        if(selectIris) {
                                            userItem.setLeft_iris_img(leftIrisImage);
                                            userItem.setLeft_iris_code(leftIrisCode);
                                            userItem.setRight_iris_img(rightIrisImage);
                                            userItem.setRight_iris_code(rightIrisCode);
                                        }
                                        userItem.jsonCaptureStatus = jsonCaptureStatus;

                                        delayHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setResult(MessageType.IModifyUserBio);
                                                //finish();
                                                captureFinish(false);
                                                overridePendingTransition(0, 0);
                                            }
                                        }, 100);

                                    }else {
                                        //ItemsListActivity.user_id = userid;
                                        ItemsListActivity.result_value = resultCode;
                                        if(selectFace) {
                                            userItem.setFace_img(faceImage);
                                            userItem.setFace_small_img(faceSmallImage);
                                            userItem.setFace_code(faceCode);
                                        }

                                        if(selectIris) {
                                            userItem.setLeft_iris_img(leftIrisImage);
                                            userItem.setLeft_iris_code(leftIrisCode);
                                            userItem.setRight_iris_img(rightIrisImage);
                                            userItem.setRight_iris_code(rightIrisCode);
                                        }
                                        userItem.jsonCaptureStatus = jsonCaptureStatus;

                                        if(resultCode== MessageType.ID_RTN_SUCCESS && userItem.getActive()){//ItemsListActivity.isActivie) {
                                            // In case there are no duplicate users_Error here
                                            // Bio information for new unregistered people may be saved
                                            ItemsListActivity.result_value = 0;
                                        }

                                        delayHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setResult(MessageType.IModifyUserBio);
                                                //finish();
                                                captureFinish(false);
                                                overridePendingTransition(0, 0);
                                            }
                                        }, 100);
                                    }
                                }
                            });

                            if ( rtnCapture  == MessageType.ID_RTN_SUCCESS)// success
                            {   }
                            else if( rtnCapture  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
                            {   }
                            else if( rtnCapture  == MessageType.ID_RTN_FAIL)// fail
                            {   }
                        }
                    } else if(m_State.equals(ConstData.CAPTURE_STATE_ENROLL_USER_BY_RESTAPI)) {

                    }else if(m_State.equals(ConstData.CAPTURE_STATE_NEW_USER)) {
                        _guid= "9999"; //temp value;

                        final Item userItem = ItemsListActivity.userItem;
                        boolean selectFace = userItem.captureFace;
                        boolean selectIris  = userItem.captureIris;
                        JSONObject jobjBioOption = new JSONObject();
                        jobjBioOption.put(MessageKeyValue.CAPTURE_BIO_OPTION_FACE, selectFace);
                        jobjBioOption.put(MessageKeyValue.CAPTURE_BIO_OPTION_IRIS, selectIris);

                        long rtnCapture = 0;
                        rtnCapture = IT100.captureBiometrics(_guid, new BiometricsInfoCallback(){

                            @Override
                            public void biometricsInfoResult(int resultCode, String userid,  byte[] leftIrisImage, byte[] rightIrisImage,
                                                             byte[] faceImage, byte[] faceSmallImage, String leftIrisCode, String rightIrisCode, String faceCode,
                                                             String jsonCaptureStatus) {
                                Logger.d("CaptureActivity >> biometricsInfoResult " + resultCode);
                                ItemsListActivity.result_value =  resultCode;
                                userItem.setFace_img(faceImage);
                                userItem.setFace_small_img(faceSmallImage);
                                userItem.setFace_code(faceCode);
                                userItem.setLeft_iris_img(leftIrisImage);
                                userItem.setLeft_iris_code(leftIrisCode);
                                userItem.setRight_iris_img(rightIrisImage);
                                userItem.setRight_iris_code(rightIrisCode);
                                userItem.jsonCaptureStatus = jsonCaptureStatus;

                                ItemsListActivity.userItem = userItem;
                                delayHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(40007);
                                        captureFinish(false);
                                        overridePendingTransition(0,0);
                                    }
                                }, 100);
                            }
                        });
                        if ( rtnCapture  == MessageType.ID_RTN_SUCCESS)// success
                        {   }
                        else if( rtnCapture  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
                        {   }
                        else if( rtnCapture  == MessageType.ID_RTN_FAIL)// fail
                        {   }

                    }
                    else {

                        if(m_recogType.equals(ConstData.RECOG_TYPE_CLOCK_IN_CONTINUOUS)){

                        }else {
                            if (thermal_onoff == 1)
                                requestRcogUserWithThermal();
                            else
                                requestRecogUser();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                m_surfaceView = null;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_linear:
                ItemsListActivity.roleInDevice = null;
                if(_dialogWaitRecogCard!=null&&_dialogWaitRecogCard.isShowing())
                    return;
                if(!isPlayCamera)
                    captureFinish(true);
                break;
            case R.id.linear_logo: {
                if(m_recogType.equals(ConstData.RECOG_TYPE_SYSTEM_ADMIN))
                    return;

                if((m_State.equals(ConstData.CAPTURE_STATE_MODIFY_BIO) ||
                        m_State.equals(ConstData.CAPTURE_STATE_NEW_USER)))
                    return;

                if (_dialogWaitRecogCard != null && _dialogWaitRecogCard.isShowing())
                    return;

                //captureFinish(true);
                isRecoging= false;
                IT100.abortCapture();
                finish();

                Intent intent;
                if(IrisApplication.isAdminActive && IrisApplication.value_auth.equals(ConstData.ADMIN_AUTH_BIO)){
                    intent = new Intent(CaptureActivity.this, AdminLoginActivity.class);
                    intent.putExtra(ConstData.INTENT_ADMIN_LOGIN_TYPE, ConstData.INTENT_ADMIN_ONLY_BIOMETRICS);
                    startActivity(intent);

                }else if(!IrisApplication.isAdminActive || IrisApplication.value_auth.equals(ConstData.ADMIN_AUTH_ID_PW)){
                    intent = new Intent(CaptureActivity.this, IdPwActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade,R.anim.hold);
                }else{
                    intent = new Intent(CaptureActivity.this, AdminLoginActivity.class);
                    startActivity(intent);
                }
            }
            break;

            default:
                break;
        }
    }

    private void initUserItem(){
        ItemsListActivity.result_value = -1 ;
        ItemsListActivity.roleInDevice = "";
    }

    private void initView() {
        linear_back = (LinearLayout)findViewById(R.id.back_linear);
        linear_back.setOnClickListener(this);

        camera_line_img = (ImageView) findViewById(R.id.camera_line_img);

        result_layout = (LinearLayout) findViewById(R.id.result_layout);
        result_layout.setVisibility(View.GONE);
        result_layout_iris = (LinearLayout)findViewById(R.id.result_iris);
        linear_capture_state = (LinearLayout)findViewById(R.id.linear_capture_state);
        img_capture_state = (ImageView)findViewById(R.id.img_capture_state);
        img_glasses_off = (ImageView)findViewById(R.id.img_glasses_Off);
        img_mask_off = (ImageView)findViewById(R.id.img_mask_Off);

        result_face_img = (ImageView) findViewById(R.id.result_face_img);
        result_iris_r = (ImageView) findViewById(R.id.result_iris_r);
        result_iris_l = (ImageView) findViewById(R.id.result_iris_l);
        result_id_txt = (TextView) findViewById(R.id.result_id_txt);
        result_name_txt = (TextView) findViewById(R.id.result_name_txt);
        result_date_txt = (TextView) findViewById(R.id.date_txt);
        result_img = (ImageView) findViewById(R.id.result_img);
        result_txt = (TextView) findViewById(R.id.result_txt);
        score_txt = (TextView) findViewById(R.id.score_txt);
        time_txt = (TextView) findViewById(R.id.time_txt);
        thermal_txt = (TextView) findViewById(R.id.txt_thermal);
        img_anim_center = (ImageView)findViewById(R.id.img_ani_center);
        txt_guide = (BorderTextView) findViewById(R.id.txt_guide);
        arrowImageVisible(View.GONE);
    }

    private void captureFinish(boolean cancelEvent) {
        sendCancelEvent = cancelEvent;
        if(cancelEvent) {
            isRecoging= false;
            setResult(3001);
        }
        finish();
    }

    boolean isPlayCamera = false;
    boolean sendCancelEvent = true;
    @Override
    protected void onResume() {
        super.onResume();
        //initView();
        Logger.d("CaptureActivity >> onResume");
        //////////////////////////////////
        if(sound_pool == null) {
            sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            sound_focus = sound_pool.load(this, R.raw.focus, 1);
            sound_thermal_alert = sound_pool.load(this, R.raw.gentle_warning, 2);
        }

        stopTimerRunnable = new StopCountDownRunner();
        stopTimerThread = new Thread(stopTimerRunnable);
        stopTimerThread.start();

        SurfaceView m_surfaceView_thermal = (SurfaceView) findViewById(R.id.surfaceView_thermal);
        m_surfaceView_thermal.setZOrderMediaOverlay(true);
        SurfaceHolder holder_thermal = m_surfaceView_thermal.getHolder();
        holder_thermal.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                IT100.setThermalPreviewDisplay(holder.getSurface());
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}
            @Override
            public void surfaceDestroyed(SurfaceHolder holder){}
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        IT100.setCaptureStatusCallback(null);
        IT100.setThermalPreviewDisplay(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(countTimer!=null)
            countTimer.cancel();
        countTimer = null;

        if (sendCancelEvent) {
//            long rtnAbort = 0;
            Logger.d("CaptureActivity >>>>> onDestroy ");
//            rtnAbort =  IT100.abortCapture();
//            isRecoging= false;
//            if ( rtnAbort  == MessageType.ID_RTN_SUCCESS){   }// success
//            else if( rtnAbort  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
//            else if( rtnAbort  == MessageType.ID_RTN_FAIL){}// fail
        }
        m_surfaceView = null;
        IT100.setPreviewDisplay(null,MessageType.BG_NONE);
        if(irisApplication != null)
            irisApplication.unregisterActivity(this);

        System.gc();

        delayHandler.removeCallbacksAndMessages(null);
        self = null;

        directState = -1;

        isInOutStatus = false;
        stopTimerFirst = true;
        if(stopTimerThread != null)
            stopTimerThread.interrupt();
        stopTimerThread = null;
    }


    private void requestRcogUserWithThermal() {
        Logger.d("CaptureActivity >> requestRcogUserWithThermal ");
        long rtnRecog = 0;
        rtnRecog = IT100.recogUserWithThermal(m_recogType, mRecogWithThermalCallback);

        if ( rtnRecog  == MessageType.ID_RTN_SUCCESS)// success
        {   }
        else if( rtnRecog  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
        {   }
        else if( rtnRecog  == MessageType.ID_RTN_FAIL)// fail
        {   }
    }

    public int counter = 0;
    static Dialog _dialogWaitRecogCard= null;
    static CountDownTimer countTimer;
    private void onWaitRecogTouchCard() {
        _dialogWaitRecogCard = new Dialog(this, R.style.myDialog);
        _dialogWaitRecogCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogWaitRecogCard.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        _dialogWaitRecogCard.setCancelable(false);
        _dialogWaitRecogCard.setContentView(R.layout.dialog_card_scan_ready_count);

        ImageView imgEnroll = (ImageView) _dialogWaitRecogCard.findViewById(R.id.img_card_scan);
        Glide.with(this).load(R.drawable.card_scan_gif).into(imgEnroll);

        final TextView txtCount = (TextView) _dialogWaitRecogCard.findViewById(R.id.txt_count);
        final LinearLayout linearCount = (LinearLayout) _dialogWaitRecogCard.findViewById(R.id.linear_count);
        linearCount.setVisibility(View.VISIBLE);
        // Waiting for 4~5 seconds
        countTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                counter++;
                if((5-counter)<1)
                    linearCount.setVisibility(View.GONE);
                else
                    txtCount.setText(String.valueOf(5 - counter));
            }
            public  void onFinish(){
                if(_dialogWaitRecogCard!=null)
                    _dialogWaitRecogCard.dismiss();
                _dialogWaitRecogCard = null;
            }
        }.start();

        _dialogWaitRecogCard.show();
    }

    static RecogWithThermalCallback mRecogWithThermalCallback = new RecogWithThermalCallback() {
        @Override
        public void fail(final int result_value, final UserInfo userInfo, byte[] leftIrisImage, byte[] rightIrisImage, byte[] faceImage,
                         final double score, final String time, double userTemperature, JSONObject jExtraInfo) {
            directState = -1;
            isRecoging= false;

            if(countTimer!=null)
                countTimer.onFinish();

            if(self==null)
                return;

            if (self.m_State.equals(ConstData.CAPTURE_STATE_SYSTEM_ADMIN)) {
                ItemsListActivity.roleInDevice = "";
                self.captureFinish(false);
                return;
            }

            self._userTemperature = userTemperature;
            if(jExtraInfo.optString(MessageKeyValue.RECOG_MASK_DETECTION).equals(MessageKeyValue.RECOG_MASK_DETECTION_DETECTED))
                self._maskOn= true;
            else
                self._maskOn= false;

//            UserInfo userInfo = new UserInfo();
//            userInfo.faceImage = faceImage;
//            userInfo.rIrisImage = rightIrisImage;
//            userInfo.lIrisImage = leftIrisImage;
            if(userInfo.faceImage==null)
                userInfo.faceImage = faceImage;
            if(userInfo.rIrisImage==null)
                userInfo.rIrisImage = rightIrisImage;
            if(userInfo.lIrisImage==null)
                userInfo.lIrisImage = leftIrisImage;

            self.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.arrowImageVisible(View.GONE);
                    self.showResultDialog(result_value, userInfo, score, time);
                }
            });
        }

        @Override
        public void success(final int result_value, final UserInfo userInfo, byte[] auditFaceImage,
                            final double score, final String time, double userTemperature, JSONObject jExtraInfo) {
            directState = -1;
            isRecoging = false;
            if(countTimer!=null)
                countTimer.onFinish();

            if(self==null)
                return;

            String firstName = userInfo.firstName;
            String lastName = userInfo.lastName;
            String roleInDevice = userInfo.role;


            if (self.m_State.equals(ConstData.CAPTURE_STATE_SYSTEM_ADMIN)) {
                ItemsListActivity.roleInDevice = roleInDevice;
                ItemsListActivity.firstName = firstName;
                ItemsListActivity.lastName = lastName;

                self.captureFinish(false);
                return;
            }
            self._userTemperature = userTemperature;
            if(jExtraInfo.optString(MessageKeyValue.RECOG_MASK_DETECTION).equals(MessageKeyValue.RECOG_MASK_DETECTION_DETECTED))
                self._maskOn= true;
            else
                self._maskOn= false;

            self.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.arrowImageVisible(View.GONE);
                    self.showResultDialog(result_value, userInfo, score, time);
                }
            });
        }

        @Override
        public void onStatus(final int result_value, String s) {
            if(self==null)
                return;

            if(countTimer!=null)
                countTimer.onFinish();

            self.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(result_value== MessageType.ID_WAIT_CARD_VERIFICATION) {
                        self.onWaitRecogTouchCard();
                    }else if(result_value == MessageType.ID_WAIT_BIO_VERIFICATION) {
                        //wait
                        //new BasicToast(IrisApplication.getInstance())
                        //        .makeText(self.getResources().getString(R.string.card_verified_wait_bio)).show();
                    }else if(result_value == MessageType.ID_CARD_VERIFIED_WAIT_AUDITIMAGE){
                        new BasicToast(IrisApplication.getInstance())
                                .makeText(self.getResources().getString(R.string.capture_for_audit_image)).show();
                    }
                }
            });
        }
    };

    static RecogCallback mRecogCallback = new RecogCallback() {
        @Override
        public void fail(final int result_value, final UserInfo userInfo, byte[] leftIrisImage, byte[] rightIrisImage, byte[] faceImage,
                         final double score, final String time, JSONObject jExtraInfo) {
            directState = -1;
            isRecoging = false;
            if(countTimer!=null)
                countTimer.onFinish();

            if(self==null)
                return;


            if (self.m_State.equals(ConstData.CAPTURE_STATE_SYSTEM_ADMIN)) {
                ItemsListActivity.roleInDevice = "";
                self.captureFinish(false);
                return;
            }

            if(jExtraInfo.optString(MessageKeyValue.RECOG_MASK_DETECTION).equals(MessageKeyValue.RECOG_MASK_DETECTION_DETECTED))
                self._maskOn= true;
            else
                self._maskOn= false;

            if(userInfo.faceImage==null)
                userInfo.faceImage = faceImage;
            if(userInfo.rIrisImage==null)
                userInfo.rIrisImage = rightIrisImage;
            if(userInfo.lIrisImage==null)
                userInfo.lIrisImage = leftIrisImage;

            self.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.arrowImageVisible(View.GONE);
                    self.showResultDialog(result_value, userInfo, score, time);
                }
            });
        }

        @Override
        public void success(final int result_value, final UserInfo userInfo, byte[] auditFaceImage,
                            final double score, final String time, JSONObject jExtraInfo) {            isRecoging= false;
            directState = -1;
            Logger.d("CaptureActivity  >>  success ");
            if(countTimer!=null)
                countTimer.onFinish();

            if(self==null)
                return;

            String firstName = userInfo.firstName;
            String lastName = userInfo.lastName;
            String roleInDevice = userInfo.role;


            self.isPlayCamera = true;
            if (self.m_State.equals(ConstData.CAPTURE_STATE_SYSTEM_ADMIN)) {
                if(result_value == MessageType.ID_SUCCESS|| result_value == MessageType.ID_CARD_VERIFIED) {
                    ItemsListActivity.roleInDevice = roleInDevice;
                    ItemsListActivity.firstName = firstName;
                    ItemsListActivity.lastName = lastName;
                }
                self.captureFinish(false);
                return;
            }
            Logger.e( "requestRecogUser jExtraInfo is = " + jExtraInfo.toString());
            if(jExtraInfo.optString(MessageKeyValue.RECOG_MASK_DETECTION).equals(MessageKeyValue.RECOG_MASK_DETECTION_DETECTED))
                self._maskOn= true;
            else
                self._maskOn= false;

            self.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.arrowImageVisible(View.GONE);
                    self.showResultDialog(result_value, userInfo, score, time);
                }
            });
        }

        @Override
        public void onStatus(final int result_value, String s) {

            if(self==null)
                return;

            if(countTimer!=null)
                countTimer.onFinish();

            self.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(result_value== MessageType.ID_WAIT_CARD_VERIFICATION) {
                        self.onWaitRecogTouchCard();
                    }else if(result_value == MessageType.ID_WAIT_BIO_VERIFICATION) {
                        //wait
                        //new BasicToast(IrisApplication.getInstance())
                        //        .makeText(self.getResources().getString(R.string.card_verified_wait_bio)).show();
                    }else if(result_value == MessageType.ID_CARD_VERIFIED_WAIT_AUDITIMAGE){
                        new BasicToast(IrisApplication.getInstance())
                                .makeText(self.getResources().getString(R.string.capture_for_audit_image)).show();
                    }
                }
            });
        }
    };

    private void requestRecogUser() {
        long rtnRecog = 0;
        rtnRecog = IT100.recogUser(m_recogType, mRecogCallback);

        if ( rtnRecog  == MessageType.ID_RTN_SUCCESS)// success
        {   }
        else if( rtnRecog  == MessageType.ID_RTN_NOT_OPENED_FAIL)// fail
        {   }
        else if( rtnRecog  == MessageType.ID_RTN_FAIL)// fail
        {   }
    }

    public void showResultDialog(final int result_value, UserInfo userInfo , double score, String time){
        self.isPlayCamera = true;
        boolean accessGranted = true;
        String user_id = userInfo.userID;
        String firstName = userInfo.firstName;
        String lastName = userInfo.lastName;

        byte[] leftIrisImage = userInfo.lIrisImage;
        byte[] rightIrisImage = userInfo.rIrisImage;
        byte[] faceImage = userInfo.faceImage;

        ////////////////////////// Common //////////////////////////
        result_layout.setVisibility(View.VISIBLE);
        //Log.e("userTemperature" , "userTemperature = " + userTemperature);

        camera_line_img.setVisibility(View.VISIBLE);
        camera_line_img.setBackgroundResource(R.drawable.camera_green_box_line);

        if (faceImage != null) {
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(faceImage, 0, faceImage.length);
            result_face_img.setImageBitmap(faceBitmap);
        }else{
            result_face_img.setImageBitmap(null);
        }

        if (rightIrisImage != null) {
            Bitmap right_iris_Bitmap = BitmapFactory.decodeByteArray(rightIrisImage, 0, rightIrisImage.length);
            result_iris_r.setImageBitmap(right_iris_Bitmap);
        }else{
            result_iris_r.setImageBitmap(null);
        }

        if (leftIrisImage != null) {
            Bitmap left_iris_Bitmap = BitmapFactory.decodeByteArray(leftIrisImage, 0, leftIrisImage.length);
            result_iris_l.setImageBitmap(left_iris_Bitmap);
        }else{
            result_iris_l.setImageBitmap(null);
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
        String strDate = dateFormat.format(calendar.getTime());
        result_date_txt.setText(strDate);

        Double dTime = 0.0;
        if (time != null && !time.isEmpty()) {
            dTime = Double.valueOf(time);
        }
        Double dScore = Double.valueOf(score);
        if(dScore.equals(Double.NaN))
            score = 0.0;
        score_txt.setText(String.format("%.2f", score));
        time_txt.setText(String.format("%.2fs", dTime));

        ////////////////////////// ///// //////////////////////////

        String resultTemp="";
        if(thermal_onoff==1){
            resultTemp = String.format("%.1f ", _userTemperature);
            if(thermal_unit.equals(MessageKeyValue.THERMAL_UNIT_C)) {
                resultTemp = resultTemp + getString(R.string.temperature_c_unit);
            }else{
                resultTemp = resultTemp+ getString(R.string.temperature_f_unit);
            }

            ((TextView)findViewById(R.id.txt_result_thermal)).setText(resultTemp);
            findViewById(R.id.txt_result_thermal).setBackground(getResources().getDrawable(R.drawable.btn_5dp_success, null));
            findViewById(R.id.linear_result_thermal).setVisibility(View.VISIBLE);

            if(_userTemperature<=0 || _userTemperature.equals(Double.NaN))
                findViewById(R.id.linear_result_thermal).setVisibility(View.GONE);
            if(Double.valueOf(String.format(java.util.Locale.US,"%.1f ", _userTemperature)) > Double.valueOf(thermal_threshold)){
                ((TextView)findViewById(R.id.txt_result_thermal)).setBackground(getResources().getDrawable(R.drawable.btn_5dp_fail, null));
                //sound alarm
                if(thermal_alarm==1){
                    if (sound_pool == null) {
                        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                        sound_thermal_alert = sound_pool.load(self, R.raw.gentle_warning, 2);
                    } else
                        sound_pool.play(sound_thermal_alert, 1, 1, 0, 0, 0);
                }

//                if(thermal_access==1){
//                    accessGranted = false;
//                }
            }
        }

        if (result_value == MessageType.ID_SUCCESS || result_value == MessageType.ID_CARD_VERIFIED
                || result_value == MessageType.ID_ACCESS_GRANTED) {
            result_id_txt.setText(user_id);
            if(IrisApplication.display_lang.contains("zh") || IrisApplication.display_lang.equals("ja") ||
                    IrisApplication.display_lang.contains("ko")){
                result_name_txt.setText(lastName + " " + firstName);
            }else
                result_name_txt.setText(firstName + " " + lastName);
            result_id_txt.setVisibility(View.VISIBLE);
            result_name_txt.setVisibility(View.VISIBLE);

            if (result_value == MessageType.ID_SUCCESS)
                result_txt.setText(getResources().getText(R.string.recog_success_0));
            else if (result_value == MessageType.ID_CARD_VERIFIED)
                result_txt.setText(getResources().getText(R.string.recog_success_card_verified_11091006));
            else if (result_value == MessageType.ID_ACCESS_GRANTED)
                result_txt.setText(getResources().getText(R.string.recog_success_access_granted_11091036));

            result_txt.setTextColor(getResources().getColor(R.color.captureSuccessText, null));
            result_img.setImageResource(R.drawable.icon_identified_success);
            result_layout_iris.setVisibility(View.GONE);

//            if ((pref.getInt(getString(R.string.prefer_key_relay_on), 1) == 1)  && accessGranted==true)
//                irisApplication.relayOpen();

            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult(MessageType.IRecogResp);
                    //captureFinish(true);
                    captureFinish(false);
                    overridePendingTransition(0, 0);
                }
            }, get_time_out);

            if(voice_guide==1) {    
                if(mask_detect ==1 && mask_voice ==1 && _maskOn == false){
                    playCaptureResult(R.raw.puton_mask, -1, true);
                }else{
                    if(result_value == MessageType.ID_SUCCESS)
                        playCaptureResult(R.raw.identified, -1, true);
                    else if (result_value == MessageType.ID_CARD_VERIFIED)
                        playCaptureResult(R.raw.verified, -1, true);
                    else if (result_value == MessageType.ID_ACCESS_GRANTED)
                        playCaptureResult(R.raw.access_granted, -1, true);
                }
            }

        }else {
            if(countTimer!=null)
                countTimer.onFinish();

            result_layout_iris.setVisibility(View.VISIBLE);
            result_img.setImageResource(R.drawable.icon_identified_failed);

            if (result_value == MessageType.ID_FAKE_EYE_DETECTED)
                result_txt.setText(getResources().getText(R.string.recog_fail_fake_eye_detected_11071010));
            else if(result_value == MessageType.ID_FAKE_EYE_DETECTED_FAILED)
                result_txt.setText(getResources().getText(R.string.recog_fail_fake_eye_detect_fail_11071011));
            else if (result_value == MessageType.ID_INACTIVE_USER)
                result_txt.setText(getResources().getText(R.string.recog_fail_inactive_user_11091003));
//            else if (result_value == MessageType.ID_WRONG_AUTHENTICATION_MODE) // MessageType.ID_WRONG_AUTHENTICATION_MODE //11091004
//                result_txt.setText(getResources().getText(R.string.capture_fail_wrong_auth_mode));
            else if (result_value == MessageType.ID_CARD_NOT_VERIFIED)
                result_txt.setText(getResources().getText(R.string.recog_fail_card_not_verified_11091007));
            else if (result_value == MessageType.ID_BIO_CARD_MISMATCH)
                result_txt.setText(getResources().getText(R.string.recog_fail_bio_card_mismatch_11091010));
            else if (result_value == MessageType.ID_WAIT_CARD_TIMEOUT)
                result_txt.setText(getResources().getText(R.string.recog_fail_wait_card_timeout_11091012));
//            else if (result_value == MessageType.ID_WAIT_BIO_TIMEOUT)
//                result_txt.setText(getResources().getText(R.string.recog_fail_wait_bio_timeout_11091011));
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
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult(MessageType.IRecogResp);
                    //captureFinish(true);
                    captureFinish(false);
                }
            }, get_time_out);

            if(voice_guide==1) {
                if (result_value == MessageType.ID_MASK_REQUIRED)
                    playCaptureResult(R.raw.puton_mask, -1, true);
                else if (result_value == MessageType.ID_USER_TEMPERATURE_HIGH)
                    playCaptureResult(R.raw.high_temperature, -1, true);
                else if(result_value == MessageType.ID_INACTIVE_USER)
                    playCaptureResult(R.raw.not_permission, -1, true);
                    else if (result_value == MessageType.ID_POLICY_NOT_GRANTED ||
                            result_value == MessageType.ID_ACCESS_NOT_GRANTED ||
                            result_value == MessageType.ID_PACS_TIMEOUT)
                    playCaptureResult(R.raw.not_permission, -1, true);
                else if(result_value == MessageType.ID_WAIT_CARD_TIMEOUT ||
                        result_value == MessageType.ID_CARD_NOT_VERIFIED ||
                            result_value == MessageType.ID_BIO_CARD_MISMATCH) // ||
                            //result_value == MessageType.ID_WAIT_BIO_TIMEOUT)
                    playCaptureResult(R.raw.not_verified, -1, true);
                else
                    playCaptureResult(R.raw.not_identified, -1, true);
            }
        }
    }

    public void setDecorView() {
        final View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    AudioManager mAudioManager;
    int current_volume;

    MediaPlayer mediaPlayer;

    private void playCaptureResult(int resid, int volume, boolean stopAndPlay) {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        current_volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if(volume>-1) {
            mAudioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, // Stream type
                    //13, // Index
                    volume,
                    AudioManager.FLAG_PLAY_SOUND // Flags
            );
        }

        if (mediaPlayer == null && getResources() != null && self!=null) {
            mediaPlayer = MediaPlayer.create(self, resid);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    //mp = null;
                    mediaPlayer = null;
                    mAudioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC, // Stream type
                            current_volume, // Index
                            AudioManager.FLAG_PLAY_SOUND // Flags
                    );
                }
            });
        }else{
            // mediaPlayer.stop();
            if(stopAndPlay) {
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(self, resid);
                mediaPlayer.start(); // no need to call prepare(); create() does that for you
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        //mp = null;
                        mediaPlayer = null;
                        mAudioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC, // Stream type
                                current_volume, // Index
                                AudioManager.FLAG_PLAY_SOUND // Flags
                        );
                    }
                });
            }
        }
    }

    public void arrowImageVisible(int visible){
        findViewById(R.id.img_ani_center).setVisibility(visible);
        txt_guide.setVisibility(visible);
    }

    AnimationDrawable frameAnimation;
    public void animationArrow(int direction){
        if(frameAnimation!=null)
            frameAnimation.stop();

        if(direction == GODOWN){
            img_anim_center.setVisibility(View.VISIBLE);
            img_anim_center.setBackgroundResource(R.drawable.anim_down);

            txt_guide.setVisibility(View.VISIBLE);
            txt_guide.setText(getResources().getString(R.string.go_down));
            frameAnimation = (AnimationDrawable) img_anim_center.getBackground();
            frameAnimation.start();

        }else if(direction == GOUP){
            img_anim_center.setVisibility(View.VISIBLE);
            img_anim_center.setBackgroundResource(R.drawable.anim_up);

            txt_guide.setVisibility(View.VISIBLE);
            txt_guide.setText(getResources().getString(R.string.go_up));
            frameAnimation = (AnimationDrawable) img_anim_center.getBackground();
            frameAnimation.start();

        }else if(direction == GORIGHT){
            img_anim_center.setVisibility(View.VISIBLE);
            img_anim_center.setBackgroundResource(R.drawable.anim_right);

            txt_guide.setVisibility(View.VISIBLE);
            txt_guide.setText(getResources().getString(R.string.go_right));
            frameAnimation = (AnimationDrawable) img_anim_center.getBackground();
            frameAnimation.start();

        } else if(direction == GOLEFT){
            img_anim_center.setVisibility(View.VISIBLE);
            img_anim_center.setBackgroundResource(R.drawable.anim_left);

            txt_guide.setVisibility(View.VISIBLE);
            txt_guide.setText(getResources().getString(R.string.go_left));
            frameAnimation = (AnimationDrawable) img_anim_center.getBackground();
            frameAnimation.start();

        }else if(direction == GOCLOSE){
            img_anim_center.setVisibility(View.VISIBLE);
            img_anim_center.setBackgroundResource(R.drawable.anim_forward);

            txt_guide.setVisibility(View.VISIBLE);
            txt_guide.setText(getResources().getString(R.string.go_forward));
            frameAnimation = (AnimationDrawable) img_anim_center.getBackground();
            frameAnimation.start();

        }else if(direction == GOFAR){
            img_anim_center.setVisibility(View.VISIBLE);
            img_anim_center.setBackgroundResource(R.drawable.anim_backward);
            txt_guide.setVisibility(View.VISIBLE);
            txt_guide.setText(getResources().getString(R.string.go_backward));

            frameAnimation = (AnimationDrawable) img_anim_center.getBackground();
            frameAnimation.start();
        }
    }

    private void UIDirection(int direction){
        if(voice_guide==1) {

            if (direction == GOFAR && directState != GOFAR) {
                playCaptureResult(R.raw.move_back, -1, false);
            } else if (direction == GOCLOSE) {
                playCaptureResult(R.raw.move_closer, -1, false);
            }else if (direction == GOLEFT) {
                playCaptureResult(R.raw.move_left, -1, false);
            }else if (direction == GORIGHT) {
                playCaptureResult(R.raw.move_right, -1, false);
            } else {
                playCaptureResult(R.raw.move_guidebox, -1, false);
            }
        }

        if(position_guide ==1)
            animationArrow(direction);
    }

    private void setEnrollCaptureUI(){
        findViewById(R.id.surfaceView_thermal).setVisibility(View.INVISIBLE);
        findViewById(R.id.linear_enroll).setVisibility(View.VISIBLE);
        findViewById(R.id.linear_capture_state).setVisibility(View.VISIBLE);
    }

    Thread stopTimerThread;
    Runnable stopTimerRunnable;
    private boolean isInOutStatus = false;
    private boolean stopTimerFirst = true;
    class StopCountDownRunner implements Runnable {
        @Override
        public void run() {
            Logger.d("@StopCountDownRunner run");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Logger.d("@StopCountDownRunner run Interrupted not");
                    Thread.sleep(15000);

                    if(!isInOutStatus && !stopTimerFirst)
                        captureFinish(true);

                    if(stopTimerFirst)
                        stopTimerFirst = false;

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    };

}