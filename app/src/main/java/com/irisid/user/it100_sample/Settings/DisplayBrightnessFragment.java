package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.BrightnessCallback;
import com.irisid.it100.callback.LanguageCallback;
import com.irisid.it100.callback.VolumeCallback;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

public class DisplayBrightnessFragment extends Fragment {
    AppCompatSpinner lang_spin;
    AppCompatSeekBar brightness_seek;
    AppCompatSeekBar volume_seek;
    TextView brightness_txt;
    TextView volume_txt;

    LinearLayout linear_ime_setting;
    ArrayAdapter langAdapter;
    SoundPool sound_pool;
    int sound_beep_alert;
    int breofr_position;

    SettingsContentObserver mSettingsContentObserver;

    @Override
    public void onResume() {
        super.onResume();
        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_beep_alert = sound_pool.load(getContext(), R.raw.capture_sound, 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_display_brightness, container, false);

        lang_spin = (AppCompatSpinner) rootView.findViewById(R.id.lang_spin);
        brightness_seek = (AppCompatSeekBar) rootView.findViewById(R.id.brightness_seek);
        volume_seek = (AppCompatSeekBar) rootView.findViewById(R.id.volume_seek);
        brightness_txt = (TextView) rootView.findViewById(R.id.brightness_txt);
        volume_txt = (TextView)rootView.findViewById(R.id.volume_txt);

        linear_ime_setting = (LinearLayout)rootView.findViewById(R.id.linear_ime_setting);
        langAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item_lan, getActivity().getResources().getStringArray(R.array.language_option2));
        lang_spin.setAdapter(langAdapter);

        mSettingsContentObserver = new SettingsContentObserver(new Handler(), getContext());
        getContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);

        getAndSetValues();
        registerUIListener();

        return rootView;
    }

    @Override
    public void onDestroy() {
        if(sound_pool != null) {
            sound_pool.release();
            sound_pool = null;
        }
        getContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
        super.onDestroy();
    }

    private void getAndSetBrightness(){
        long rtnBrightness = 0 ;
        rtnBrightness = IT100.getBrightness(new BrightnessCallback() {

            @Override
            public void brightnessResult(int brightness)
            {
                brightness_seek.setEnabled(true);
                brightness_seek.setProgress(brightness);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            brightness_txt.setText(String.valueOf(brightness_seek.getProgress()));
                        }
                    });
                }
            }
        });
        if ( rtnBrightness  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnBrightness  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnBrightness  == MessageType.ID_RTN_FAIL){}// fail
    }

    private void getAndSetVolume() {
        long rtnVolume = 0 ;
        rtnVolume =IT100.getVolume(new VolumeCallback()
        {

            @Override
            public void volumeResult(int volume)
            {
                volume_seek.setProgress((int)(volume));
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            volume_txt.setText(String.valueOf(volume_seek.getProgress()));
                        }
                    });
                }
            }
        });

        if ( rtnVolume  == MessageType.ID_RTN_SUCCESS){   }// successl
        else if( rtnVolume  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnVolume  == MessageType.ID_RTN_FAIL){}// fail

    }

    private void getAndSetLanguage(){
        long rtnLanguage = 0 ;
        rtnLanguage =IT100.getLanguage(new LanguageCallback()
        {

            @Override
            public void languageResult(String language)
            {
                if(language.equals("en")){lang_spin.setSelection(0); breofr_position = 0; }
                else if(language.equals("ko")){lang_spin.setSelection(1); breofr_position = 1; }
                else if(language.equals("tr")){lang_spin.setSelection(2); breofr_position = 2; }
                else if(language.equals("ar")){lang_spin.setSelection(3); breofr_position = 3; }
                else if(language.equals("zh_TW")){lang_spin.setSelection(4); breofr_position = 4; }

                else if(language.equals("zh")){lang_spin.setSelection(5); breofr_position = 5; }
                else if(language.equals("ja")){lang_spin.setSelection(6); breofr_position = 6; }
                else if(language.equals("fr")){lang_spin.setSelection(7); breofr_position = 7; }
                else if(language.equals("de")){lang_spin.setSelection(8); breofr_position = 8; }
                else if(language.equals("es")){lang_spin.setSelection(9); breofr_position = 9; }
                else if(language.equals("it")){lang_spin.setSelection(10); breofr_position = 10; }
                else if(language.equals("pt")){lang_spin.setSelection(11); breofr_position = 11; }

                else{}
            }
        });

        if ( rtnLanguage  == MessageType.ID_RTN_SUCCESS){   }// successl
        else if( rtnLanguage  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnLanguage  == MessageType.ID_RTN_FAIL){}// fail
    }

    private void getAndSetValues(){

        // get and set Brightness value
        getAndSetBrightness();

        // get and set current device volume
        getAndSetVolume();

        // get and set current language
        getAndSetLanguage();

    }

    private void registerUIListener() {

        lang_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                long rtnLanguage = 0 ;
                if(breofr_position == position)
                    return;
                switch (position){
                    case 0:
                        rtnLanguage = IT100.setLanguage("en");
                        IrisApplication.display_lang= "en";
                        break;
                    case 1:
                        rtnLanguage = IT100.setLanguage("ko");
                        IrisApplication.display_lang= "ko";
                        break;
                    case 2:
                        rtnLanguage = IT100.setLanguage("tr");
                        IrisApplication.display_lang= "tr";
                        break;
                    case 3:
                        rtnLanguage = IT100.setLanguage("ar");
                        IrisApplication.display_lang= "ar";
                        break;
                    case 4:
                        rtnLanguage = IT100.setLanguage("zh_TW");
                        IrisApplication.display_lang= "zh_TW";
                        break;
                    case 5:
                        rtnLanguage = IT100.setLanguage("zh");
                        IrisApplication.display_lang= "zh";
                        break;
                    case 6:
                        rtnLanguage = IT100.setLanguage("ja");
                        IrisApplication.display_lang= "ja";
                        break;
                    case 7:
                        rtnLanguage = IT100.setLanguage("fr");
                        IrisApplication.display_lang= "fr";
                        break;
                    case 8:
                        rtnLanguage = IT100.setLanguage("de");
                        IrisApplication.display_lang= "de";
                        break;
                    case 9:
                        rtnLanguage = IT100.setLanguage("es");
                        IrisApplication.display_lang= "es";
                        break;
                    case 10:
                        rtnLanguage = IT100.setLanguage("it");
                        IrisApplication.display_lang= "it";
                        break;
                    case 11:
                        rtnLanguage = IT100.setLanguage("pt");
                        IrisApplication.display_lang= "pt";
                        break;

                }

                if ( rtnLanguage  == MessageType.ID_RTN_SUCCESS){   }// success
                else if( rtnLanguage  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnLanguage  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                else if( rtnLanguage  == MessageType.ID_RTN_FAIL){}// fail
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }

        });

        brightness_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("TAG >> ", "onProgressChanged : " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int brightness_value = seekBar.getProgress();

                long rtnBrightness = 0 ;
                rtnBrightness = IT100.setBrightness(brightness_value);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            brightness_txt.setText(String.valueOf(brightness_seek.getProgress()));
                        }
                    });
                }
                if ( rtnBrightness  == MessageType.ID_RTN_SUCCESS){   }// success
                else if( rtnBrightness  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnBrightness  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                else if( rtnBrightness  == MessageType.ID_RTN_FAIL){}// fail
            }
        });

        volume_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.e("TAG >> ", "onProgressChanged : " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.e("TAG >> ", "onStopTrackingTouch : " + seekBar.getProgress());

                int volume_value = seekBar.getProgress();

                long rtnVolume = 0;
                rtnVolume = IT100.setVolume(volume_value);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            volume_txt.setText(String.valueOf(volume_seek.getProgress()));
                        }
                    });
                }
                if ( rtnVolume  == MessageType.ID_RTN_SUCCESS){   }// success
                else if( rtnVolume  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnVolume  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                else if( rtnVolume  == MessageType.ID_RTN_FAIL){}// fail

                sound_pool.play(sound_beep_alert, 1f, 1f, 0, 0, 1f);
            }
        });

        linear_ime_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                        .putExtra("only_access_points", true)
                        .putExtra(":settings:hide_drawer", true)
                        .putExtra("extra_prefs_show_button_bar", true)
                        .putExtra("extra_prefs_set_next_text", (String)null));
            }
        });
    }


    public class SettingsContentObserver extends ContentObserver {
        Context mContext;
        public SettingsContentObserver(Handler handler, Context context) {
            super(handler);
            this.mContext = context;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            // get and set Brightness value
            getAndSetBrightness();

            // get and set current device volume
            getAndSetVolume();

        }
    }
}
