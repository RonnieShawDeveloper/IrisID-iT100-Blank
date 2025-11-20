package com.irisid.user.it100_sample.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.BarcodeCaptureCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanQRCodeActivity extends AppCompatActivity implements View.OnClickListener
{
    LinearLayout linear_back;
    SurfaceHolder holder;

    public static SurfaceView m_surfaceView = null;
    public static ScanQRCodeActivity self = null;
    static Handler qrDelayHandler;

    IrisApplication irisApplication = null;

    ImageView imgQrLine;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        self = this;

        Logger.d("ScanQRCodeActivity >> onCreate()");
        irisApplication=  (IrisApplication) getApplication();
        irisApplication.registerActivity(this);
        qrDelayHandler = new Handler();

        overridePendingTransition(0,0 );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capture_barcode);

        setDecorView();
        setAdminMode(true);
        initView();

        m_surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = m_surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    //Logger.d("ScanQRCodeActivity >> surfaceCreated()");
                    IT100.setPreviewDisplay(surfaceHolder.getSurface(),MessageType.BG_BLUR);
                    IT100.startBarcodeScan(new BarcodeCaptureCallback() {
                        @Override
                        public void onResult(int resultCode, final String msg) {
                            Logger.d("@@readQRcode " + msg);
                            qrDelayHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.putExtra(ConstData.QRCODE_MESSAGE, msg);
                                    setResult(200, intent);
                                    finish();
                                }
                            }, 1500);

                            playCaptureResult(R.raw.beep_short, -1);
                            imgQrLine.setImageResource(R.drawable.qr_green);
                            ((TextView)findViewById(R.id.txt_guide)).setText(getResources().getString(R.string.qr_authenticate_success));
                            findViewById(R.id.linear_result).setVisibility(View.VISIBLE);
                            findViewById(R.id.img_qr_bg).setVisibility(View.VISIBLE);
                            findViewById(R.id.back_linear).setVisibility(View.INVISIBLE);
                        }
                    });

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
                finish();
                break;

            default:
                break;
        }
    }

    private void initView() {
        linear_back = (LinearLayout)findViewById(R.id.back_linear);
        linear_back.setOnClickListener(this);
        imgQrLine = (ImageView)findViewById(R.id.img_qr_line);
        ((TextView)findViewById(R.id.txt_guide)).
                setText(getResources().getString(R.string.qr_scan_guide));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("ScanQRCodeActivity >> onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("ScanQRCodeActivity >> onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        IT100.stopBarcodeScan();

        if(irisApplication != null)
            irisApplication.unregisterActivity(this);

        System.gc();
        self = null;
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

    private void playCaptureResult(int resid, int volume) {

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

        if(mediaPlayer==null) {
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

    private void setAdminMode(boolean enable){
        JSONObject jparam = new JSONObject();
        try {
            jparam.put(MessageKeyValue.ADMIN_MODE_ENALBE, enable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IT100.setAdminMode(jparam.toString());
    }

}