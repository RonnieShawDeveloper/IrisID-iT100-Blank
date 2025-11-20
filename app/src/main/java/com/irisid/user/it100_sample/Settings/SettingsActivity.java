package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

public class SettingsActivity extends AppCompatActivity implements SettingOptionsFragment.OnOptionClickListener
{
    private boolean isTwoPane;
    private FragmentManager fragmentManager;

    public static final String SETTING_OPTION_GENERAL = "general";
    public static final String SETTING_OPTION_APPLICATION = "application";
    public static final String SETTING_OPTION_MODE = "mode";
    public static final String SETTING_OPTION_DISPLAY = "display";
    public static final String SETTING_OPTION_WALLPAPER= "wallpaper";
    public static final String SETTING_OPTION_DATETIME= "date_time";
    public static final String SETTING_OPTION_NETWORK= "network";
    public static final String SETTING_OPTION_ACTIVATION= "activation";
    public static final String SETTING_OPTION_EXTERNAL_DEVICE ="external_device";
    IrisApplication irisApplication = null;

    Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        irisApplication=  (IrisApplication) getApplication();
        irisApplication.registerActivity(this);

        setContentView(R.layout.activity_settings);
        changeStatusBarColor("#01000000");
        setDecorView();

        _context = this;
        irisApplication.activateBackButton(this, R.id.back_linear);

        findViewById(R.id.titlebar_linear).setBackgroundColor(getColor(R.color.settingTitleBackground));
        ((TextView)findViewById(R.id.title)).setText(getResources().getText(R.string.settings));

        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.detailContainer) != null) {
            isTwoPane = true;
        } else {
            isTwoPane = false;
        }

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, new SettingOptionsFragment())
                    .commit();
        }

        //Load Display Settings Fragment by default in the details pane
        if (isTwoPane) {
            fragmentManager.beginTransaction()
                    .replace(R.id.detailContainer, new GeneralFragment())
                    .commit();
        }

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    setDecorView();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onOptionSelected(String option) {
        Fragment fragment = null;

        if (isTwoPane) {
            switch (option)
            {
                case SETTING_OPTION_GENERAL:
                    fragment = new GeneralFragment();
                    break;

                case SETTING_OPTION_APPLICATION:
                    fragment = new ApplicationFragment();
                    break;

                case SETTING_OPTION_MODE:
                    fragment = new ModesFragment();
                    break;

                case SETTING_OPTION_DISPLAY:
                    fragment = new DisplayBrightnessFragment();
                    break;

                case SETTING_OPTION_WALLPAPER:
                    fragment = new WallpaperFragment();
                    break;

                case SETTING_OPTION_DATETIME:
                    fragment = new DateTimeFragment();
                    break;

                case SETTING_OPTION_NETWORK:
                    fragment = new NetworkFragment5();
                    break;

                case SETTING_OPTION_ACTIVATION:
                    fragment = new ActivationFragment();
                    break;

                case SETTING_OPTION_EXTERNAL_DEVICE:
                    fragment = new ExternalDeviceFragment();
                    break;
            }

            if(fragment !=null){
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade, R.anim.hold)
                        .replace(R.id.detailContainer, fragment)
                        .commit();
            }

        } else {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(irisApplication != null)
            irisApplication.unregisterActivity(this);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if(irisApplication != null)
            irisApplication.resetTimer();
    }

    private void changeStatusBarColor(String color){
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