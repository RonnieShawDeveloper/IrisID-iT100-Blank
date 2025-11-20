package com.irisid.user.it100_sample.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import java.io.File;


public abstract class BaseBackgroundActivity extends Activity
{
    String video_get_clickFilePath;
    String image_get_clickFilePath;

    SharedPreferences pref;
    WallpaperManager wm;
    Uri background_uri;

    View rootLayout;
    VideoView videoView;
    IrisApplication irisApplication = null;
    Uri default_uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDecorView();
        wm = WallpaperManager.getInstance(this);

        irisApplication =  (IrisApplication) getApplication();
        irisApplication.registerActivity(this);

        default_uri = Uri.parse("android.resource://" + getPackageName() + "/raw/wp_m_07");
    }

    @Override
    protected void onResume() {
        super.onResume();
        rootLayout.setBackground(wm.peekDrawable());

        pref = getSharedPreferences(getString(R.string.prefer_name_wallpaper_path), MODE_PRIVATE);
        video_get_clickFilePath = pref.getString(getString(R.string.prefer_key_video_filepath), null);
        image_get_clickFilePath = pref.getString(getString(R.string.prefer_key_image_filepath), null);

        if (video_get_clickFilePath == null && image_get_clickFilePath == null) {
            videoView.setVideoURI(default_uri);
            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            videoView.setVisibility(View.VISIBLE);

        } else if (video_get_clickFilePath != null && image_get_clickFilePath == null) {
            background_uri = Uri.parse(video_get_clickFilePath);

            if(video_get_clickFilePath.startsWith("file://") ||
                    video_get_clickFilePath.startsWith("/sdcard/") ){
                File fileTemp = new File(background_uri.getPath());
                if (!fileTemp.exists()) {
                    background_uri = default_uri;

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(getString(R.string.prefer_key_image_filepath), null);
                    editor.putString(getString(R.string.prefer_key_video_filepath), null);
                    editor.apply();
                }
            }

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            videoView.setVideoURI(background_uri);
            videoView.start();
            videoView.setVisibility(View.VISIBLE);

        }else
            videoView.setVisibility(View.GONE);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(videoView.isPlaying() || videoView.isActivated()) {
            videoView.setVideoURI(default_uri);
            videoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoView.isPlaying() || videoView.isActivated())
            videoView.stopPlayback();

        if(irisApplication != null)
            irisApplication.unregisterActivity(this);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if(this.getClass().getSimpleName().equals("MainActivity") ||
                this.getClass().getSimpleName().equals("IDISMainActivity"))
            return;

        if(irisApplication != null)
            irisApplication.resetTimer();
    }

    Dialog pdialog;

    public void showLoading(String message){
        showLoading(message, 0, false);
    }

    public void showLoading(final String message , int size, boolean isSystemLoading) {

        if (isFinishing()) {
            return;
        }

        if (pdialog != null && pdialog.isShowing()) {
            loadingMsg(message);
        } else {

            pdialog = new Dialog(this);
            pdialog.setCancelable(false);
            if(isSystemLoading)
                pdialog.setContentView(R.layout.progress_loading_system);
            else
                pdialog.setContentView(R.layout.progress_loading);
            pdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            pdialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            pdialog.getWindow().setStatusBarColor(0xaa000000);
            pdialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xaa000000));

            pdialog.show();

        }

        final ImageView img_loading_frame = (ImageView)pdialog.findViewById(R.id.iv_frame_loading);
        if(size>0) {
            img_loading_frame.setMinimumWidth(size);
            img_loading_frame.setMinimumHeight(size);
        }

        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        /////////////////////////////////////////////////////
        final TextView tv_progress_message = (TextView)pdialog.findViewById(R.id.tv_progress_message);
        if (isSystemLoading) {
            tv_progress_message.setVisibility(View.VISIBLE);
            tv_progress_message.setText(message);

            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {

                int count = 0;
                String percent = "";
                @Override
                public void run() {
                    count++;

                    if(count<10)
                        percent="(20%)";
                    else if (count<30)
                        percent="(50%)";
                    else if (count<50)
                        percent="(80%)";

                    if (count % 5 == 1)
                        tv_progress_message.setText(message+". "+percent);
                    else if (count % 5 == 2)
                        tv_progress_message.setText(message+".. "+percent);
                    else if (count % 5 == 3)
                        tv_progress_message.setText(message+"... "+percent);
                    else if (count % 5 == 4)
                        tv_progress_message.setText(message+".... "+percent);
                    else if (count % 5 == 0)
                        tv_progress_message.setText(message+"..... "+percent);

                    handler.postDelayed(this, 500);
                }
            };
            handler.postDelayed(runnable, 1 * 1000);
        }
        /////////////////////////////////////////////////////

        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setVisibility(View.VISIBLE);
            tv_progress_message.setText(message);
        }
    }


    public void loadingMsg(String message) {

        if (pdialog == null || !pdialog.isShowing()) {
            return;
        }

        TextView txt_progress_msg = (TextView)pdialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            txt_progress_msg.setVisibility(View.VISIBLE);
            txt_progress_msg.setText(message);
        }
    }

    public void hideLoading() {
        if (pdialog != null && pdialog.isShowing()) {
            pdialog.dismiss();
        }
    }

    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
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
}