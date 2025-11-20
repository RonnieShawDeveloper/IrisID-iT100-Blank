package com.irisid.user.it100_sample.Common.util;

import android.util.Log;
import com.irisid.user.it100_sample_project.BuildConfig;


public class Logger {
    private static final String TAG_LAUNCHER   = "Launcher >> ";

    public static final void e(String message) {
        if (BuildConfig.DEBUG){
            Logger.e(TAG_LAUNCHER, message);
        }
    }

    public static final void w(String message) {
        if (BuildConfig.DEBUG){
            Logger.w(TAG_LAUNCHER, message);
        }
    }

    public static final void i(String message) {
        if (BuildConfig.DEBUG){
            Logger.i(TAG_LAUNCHER, message);
        }
    }
    public static final void d(String message){
        if (BuildConfig.DEBUG){
            Logger.d(TAG_LAUNCHER, message);
        }
    }
    public static final void v(String message) {
        if (BuildConfig.DEBUG){
            Logger.v(TAG_LAUNCHER, message);
        }
    }

    public static final void e(String TAG, String message) {
        if (BuildConfig.DEBUG){
            Log.e(TAG, message);
        }
    }

    public static final void w(String TAG, String message) {
        if (BuildConfig.DEBUG){
            Log.w(TAG, message);
        }
    }

    public static final void i(String TAG, String message) {
        if (BuildConfig.DEBUG){
            Log.i(TAG, message);
        }
    }

    public static final void d(String TAG, String message) {
        if (BuildConfig.DEBUG){
            Log.d(TAG, message);
        }
    }

    public static final void v(String TAG, String message) {
        if (BuildConfig.DEBUG){
            Log.v(TAG, message);
        }
    }
}

