package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irisid.user.it100_sample_project.R;

public class SettingOptionsFragment extends Fragment
{
    LinearLayout mGeneralOption;
    LinearLayout mApplicationOption;
    LinearLayout mModeOption;
    LinearLayout mDisplayOption;
    LinearLayout mWallpaperOption;
    LinearLayout mDateTimeOption;
    LinearLayout mNetworkOption;
    LinearLayout mActivationOption;
    LinearLayout mExternalDeviceOption;

    TextView general_txt;
    TextView application_txt;
    TextView mode_txt;
    TextView display_bright_txt;
    TextView wallpaper_txt;
    TextView date_time_txt;
    TextView network_txt;
    TextView activation_txt;
    TextView external_device_txt;

    interface OnOptionClickListener {
        void onOptionSelected(String option);
    }

    private OnOptionClickListener mCallback;
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try {
            mCallback = (OnOptionClickListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement SettingOptionsFragment.OnOptionClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_settings_options_view, container, false);

        mGeneralOption = (LinearLayout) rootView.findViewById(R.id.generalOption);
        mApplicationOption = (LinearLayout) rootView.findViewById(R.id.applicationOption);
        mModeOption = (LinearLayout)rootView.findViewById(R.id.modeOption);
        mDisplayOption = (LinearLayout) rootView.findViewById(R.id.displayOption);
        mWallpaperOption = (LinearLayout) rootView.findViewById(R.id.wallpaperOption);
        mDateTimeOption = (LinearLayout) rootView.findViewById(R.id.datetimeOption);
        mNetworkOption = (LinearLayout) rootView.findViewById(R.id.networkOption);
        mActivationOption = (LinearLayout) rootView.findViewById(R.id.activation_Option);
        mExternalDeviceOption = (LinearLayout) rootView.findViewById(R.id.externalOption);

        general_txt = (TextView) rootView.findViewById(R.id.general_txt);

        application_txt = (TextView) rootView.findViewById(R.id.application_txt);
        mode_txt = (TextView) rootView.findViewById(R.id.mode_txt);
        display_bright_txt = (TextView) rootView.findViewById(R.id.display_bright_txt);
        wallpaper_txt = (TextView) rootView.findViewById(R.id.wallpaper_txt);
        date_time_txt = (TextView) rootView.findViewById(R.id.date_time_txt);
        network_txt = (TextView) rootView.findViewById(R.id.network_txt);
        activation_txt = (TextView) rootView.findViewById(R.id.activation_txt);
        external_device_txt = (TextView) rootView.findViewById(R.id.external_device_txt);

        initView();
        mGeneralOption.setBackgroundColor(getResources().getColor(R.color.black, null));
        general_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
        //general_txt.setTextColor(Color.parseColor("#03DAC6"));

        mGeneralOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mGeneralOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                general_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_GENERAL);
            }
        });

        mApplicationOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mApplicationOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                application_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_APPLICATION);
            }
        });

        mModeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mModeOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                mode_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_MODE);
            }
        });

        mDisplayOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mDisplayOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                display_bright_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_DISPLAY);
            }
        });

        mWallpaperOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mWallpaperOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                wallpaper_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_WALLPAPER);
            }
        });

        mDateTimeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mDateTimeOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                date_time_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_DATETIME);
            }
        });

        mNetworkOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mNetworkOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                network_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_NETWORK);
            }
        });

        mActivationOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mActivationOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                activation_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_ACTIVATION);
            }
        });

        mExternalDeviceOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                mExternalDeviceOption.setBackgroundColor(getResources().getColor(R.color.black, null));
                external_device_txt.setTextColor(getResources().getColor(R.color.settingTextSelect, null));

                mCallback.onOptionSelected(SettingsActivity.SETTING_OPTION_EXTERNAL_DEVICE);
            }
        });

        return rootView;
    }

    private void initView(){
        mGeneralOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mApplicationOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mModeOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mDisplayOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mWallpaperOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mDateTimeOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mNetworkOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mActivationOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));
        mExternalDeviceOption.setBackgroundColor(getResources().getColor(R.color.settingBackground, null));

        general_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));

        application_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
        mode_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
        display_bright_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
        wallpaper_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
        date_time_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
        network_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
        activation_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
        external_device_txt.setTextColor(getResources().getColor(R.color.settingTextNormal, null));
    }
}
