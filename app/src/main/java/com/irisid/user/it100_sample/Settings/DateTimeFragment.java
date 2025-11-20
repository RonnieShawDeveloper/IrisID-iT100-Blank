package com.irisid.user.it100_sample.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.irisid.it100.IT100;

import com.irisid.it100.callback.AutoTimeCallback;
import com.irisid.it100.callback.NtpServerCallback;
import com.irisid.it100.callback.TimeCallback;
import com.irisid.it100.callback.TimeFormatCallback;
import com.irisid.it100.callback.TimeZoneCallback;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample_project.R;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeFragment extends Fragment implements View.OnClickListener
{
    LinearLayout rootLayout;
    LinearLayout linearTimezone;

    Button date_btn;
    Button time_btn;

    DatePicker date_picker;
    TimePicker time_picker;

    RelativeLayout date_layout;
    RelativeLayout time_layout;
    LinearLayout linearSave;

    String m_date_value=null;
    String m_time_value=null;
    int m_time_zone_value;
    String auto_date_time_value;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    AppCompatSpinner time_zone_spin;
    ArrayAdapter time_zone_Adapter;
    AppCompatCheckBox auto_date_time_check;
    AppCompatCheckBox _ch_24format;
    EditText ntp_name_edt;

    private SimpleDateFormat _dateFormatDisplay = new SimpleDateFormat("yyyy.MM.dd");
    private SimpleDateFormat _dateFormatSet = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat _timeFormatDisplay = new SimpleDateFormat("hh:mm a");
    private SimpleDateFormat _timeFormatSet = new SimpleDateFormat("HH:mm");//new SimpleDateFormat("hh:mm");

    boolean isFirst = true;
    String[] timezoneIDs ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_date_time, container, false);

        rootLayout = (LinearLayout)rootView.findViewById(R.id.rootview);

        linearTimezone = (LinearLayout) rootView.findViewById(R.id.linear_timezone);

        date_btn = (Button) rootView.findViewById(R.id.date_btn);
        time_btn = (Button) rootView.findViewById(R.id.time_btn);
        date_picker = (DatePicker) rootView.findViewById(R.id.date_picker);
        time_picker = (TimePicker) rootView.findViewById(R.id.time_picker);
        date_layout = (RelativeLayout) rootView.findViewById(R.id.date_layout);
        time_layout= (RelativeLayout) rootView.findViewById(R.id.time_layout);
        linearSave = (LinearLayout)rootView.findViewById(R.id.linear_save);

        time_zone_spin = (AppCompatSpinner) rootView.findViewById(R.id.time_zone_spin);
        auto_date_time_check = (AppCompatCheckBox) rootView.findViewById(R.id.auto_date_time_check);
        _ch_24format = (AppCompatCheckBox ) rootView.findViewById(R.id.ch_24format) ;

        ntp_name_edt = (EditText) rootView.findViewById(R.id.ntp_name_edt);

        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), Context.MODE_PRIVATE);
        editor = pref.edit();

        date_btn.setOnClickListener(this);
        time_btn.setOnClickListener(this);

        date_layout.setVisibility(View.VISIBLE);
        time_layout.setVisibility(View.INVISIBLE);

        date_btn.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
        time_btn.setTextColor(Color.WHITE);

        colorizeDatePicker(date_picker, R.color.date_time_text);
        colorizeTimePicker(time_picker, R.color.date_time_text);

        registerListener();

        m_time_zone_value = pref.getInt(getString(R.string.prefer_key_timezone_int), 0);
        ntp_name_edt.setText(pref.getString(getString(R.string.prefer_key_ntp_server_name),
                getString(R.string.prefer_key_default_ntp_server_name)));
        //Logger.d("DateTimeFragment >>", "time_zone_int : " + m_time_zone_value);
        timezoneIDs = getActivity().getResources().getStringArray(R.array.time_zone_id);

        time_zone_Adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, getActivity().getResources().getStringArray(R.array.time_zone_displayname));
        time_zone_spin.setAdapter(time_zone_Adapter);
        time_zone_spin.setSelection(m_time_zone_value);

        (rootView.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearSave.setVisibility(View.INVISIBLE);

                ntp_name_edt.setFocusable(false);
                ntp_name_edt.getBackground().mutate().setColorFilter(null);
            }
        });
        (rootView.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearSave.setVisibility(View.INVISIBLE);

                if(!auto_date_time_check.isChecked()){

                    if(ntp_name_edt.isEnabled() && ntp_name_edt.isFocusable()){
                        editor.putString(getString(R.string.prefer_key_ntp_server_name), ntp_name_edt.getEditableText().toString());
                        editor.commit();
                        IT100.setNtpServer(ntp_name_edt.getEditableText().toString());

                    }
                    long rtnDate = 0 ;
                    rtnDate = IT100.setDateAndTime(m_date_value + "T" + m_time_value);

                    if ( rtnDate  == MessageType.ID_RTN_SUCCESS){   }// success
                    else if( rtnDate  == MessageType.ID_RTN_WRONG_PARA){}// fail
                    else if( rtnDate  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                    else if( rtnDate  == MessageType.ID_RTN_FAIL){}// fail

                    new BasicToast(getContext()).makeText("Saved").show();
                }

                ntp_name_edt.setFocusable(false);
                ntp_name_edt.getBackground().mutate().setColorFilter(null);

            }
        });

        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        getContext().registerReceiver(m_timeChangedReceiver, filter);

        long trnDate = 0 ;
        trnDate = IT100.getDateAndTime(new TimeCallback() {
            @Override
            public void timeResult(String datatime) {
                //Log.d("GetDateAndTime" ,datatime);
            }
        });

        if ( trnDate  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( trnDate  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( trnDate  == MessageType.ID_RTN_FAIL){}// fail


        long trnNtp = 0 ;
        trnNtp = IT100.getNtpServer(new NtpServerCallback() {
            @Override
            public void ntpServerResult(final String ntpAddrress) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ntp_name_edt.setText(ntpAddrress);
                        }
                    });
                }
            }
        });

        if ( trnNtp  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( trnNtp  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( trnNtp  == MessageType.ID_RTN_FAIL){}// fail

        return rootView;
    }

    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIME_TICK) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detailContainer, new DateTimeFragment())
                        .commit();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        auto_date_time_value = pref.getString(getString(R.string.prefer_key_auto_date_time), "no_auto");
        m_time_zone_value = pref.getInt(getString(R.string.prefer_key_timezone_int), 0);

        // whether auto time setting or not

        long rtnAutoTime = 0 ;
        rtnAutoTime = IT100.getAutoTime(new AutoTimeCallback() {

            @Override
            public int autoTimeResult(final int i) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(i == 1) {
                                auto_date_time_check.setChecked(true);
                            }else
                                auto_date_time_check.setChecked(false);
                        }
                    });
                }
                return 0;
            }
        });

        if ( rtnAutoTime  == MessageType.ID_RTN_SUCCESS){   }// success
        else if( rtnAutoTime  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
        else if( rtnAutoTime  == MessageType.ID_RTN_FAIL){}// fail

        long rtn_timezone = IT100.getTimeZone(new TimeZoneCallback() {

            @Override
            public void timeZoneResult(final String timeZone) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time_zone_spin.setSelection(getPositionFromTimezoneid(timeZone));
                        }
                    });
                }
            }
        });
        if (rtn_timezone == MessageType.ID_RTN_SUCCESS) {
            // success
        } else if (rtn_timezone == MessageType.ID_RTN_NOT_OPENED_FAIL) {
            // not opened yet
        } else if (rtn_timezone == MessageType.ID_RTN_FAIL) {
            // fail
        }

        IT100.getTimeFormat(new TimeFormatCallback() {
            @Override
            public void onResult(final int value) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(value==12){
                                _ch_24format.setChecked(false);
                            }else{
                                _ch_24format.setChecked(true);
                            }
                        }
                    });
                }
            }
        });

        setDateTimeView();

        // Initial datepicker value setting
        Calendar c = Calendar.getInstance();
        m_date_value = _dateFormatSet.format(c.getTime());
        date_btn.setText(_dateFormatDisplay.format(c.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_btn:
                date_btn.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                time_btn.setTextColor(Color.WHITE);
                date_layout.setVisibility(View.VISIBLE);
                time_layout.setVisibility(View.INVISIBLE);
                break;

            case R.id.time_btn:
                time_btn.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
                date_btn.setTextColor(Color.WHITE);
                date_layout.setVisibility(View.INVISIBLE);
                time_layout.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {

        getContext().unregisterReceiver(m_timeChangedReceiver);
        super.onDestroy();
    }

    private void registerListener(){
        // when time value change
        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                // It got TImePicker in System, set the button
                m_time_value = String.format("%02d:%02d",hourOfDay, minute );

                try {
                    time_btn.setText(_timeFormatDisplay.format(_timeFormatSet.parse(m_time_value)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!auto_date_time_check.isChecked())
                    linearSave.setVisibility(View.VISIBLE);
            }
        });

        // Set button text from DatePicker date, time
        date_picker.init(date_picker.getYear(), date_picker.getMonth(), date_picker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar c = Calendar.getInstance();
                c.set(year, monthOfYear, dayOfMonth);

                _dateFormatDisplay = new SimpleDateFormat("yyyy.MM.dd");
                _dateFormatSet = new SimpleDateFormat("yyyy-MM-dd");

                date_btn.setText(_dateFormatDisplay.format(c.getTime()));
                m_date_value = _dateFormatSet.format(c.getTime());

                if (!auto_date_time_check.isChecked())
                    linearSave.setVisibility(View.VISIBLE);
            }
        });

        time_zone_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_time_zone_value = pref.getInt(getString(R.string.prefer_key_timezone_int), 0);

                if(m_time_zone_value == position){ // selected same time zone

                } else { // selected different time zone
                    long rtnTimezone = 0 ;
                    rtnTimezone = IT100.setTimeZone(timezoneIDs[position]);

                    if ( rtnTimezone  == MessageType.ID_RTN_SUCCESS){   }// success
                    else if( rtnTimezone  == MessageType.ID_RTN_WRONG_PARA){}// fail
                    else if( rtnTimezone  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                    else if( rtnTimezone  == MessageType.ID_RTN_FAIL){}// fail
                    editor.putInt(getString(R.string.prefer_key_timezone_int), position);
                    editor.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        auto_date_time_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                m_time_zone_value = pref.getInt(getString(R.string.prefer_key_timezone_int), 0);
                //Log.d("DateTimeFragment >>", "time_zone_int : " + m_time_zone_value);

                if(auto_date_time_check.isChecked()) {

                    if(ntp_name_edt.getText().toString().equals("")) {
                        ntp_name_edt.setText(getString(R.string.prefer_key_default_ntp_server_name));
                    }
                    IT100.setNtpServer(ntp_name_edt.getText().toString());
                    IT100.setAutoTime(1);

                    ntp_name_edt.setEnabled(false);
                    ntp_name_edt.setTextColor(getResources().getColor(R.color.disableText, null));

                    setAutoCheckView(true);
                    setDateTimeView();

                } else {
                    IT100.setAutoTime(0);

                    setAutoCheckView(false);
                    ntp_name_edt.setEnabled(true);
                    ntp_name_edt.setTextColor(Color.WHITE);
                }
            }
        });

        _ch_24format.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    IT100.setTimeFormat(24);
                    editor.putInt(getString(R.string.prefer_key_time_12_24), 24);
                    editor.commit();
                    _ch_24format.setTextColor(Color.WHITE);
                }else{
                    IT100.setTimeFormat(12);
                    editor.putInt(getString(R.string.prefer_key_time_12_24), 12);
                    editor.commit();
                    _ch_24format.setTextColor(getResources().getColor(R.color.disableText, null));
                }
            }
        });

        ntp_name_edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);

                ntp_name_edt.getBackground().mutate().setColorFilter(null);
                linearSave.setVisibility(View.VISIBLE);
                return false;
            }
        });

        ntp_name_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ntp_name_edt.getBackground().mutate().setColorFilter(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(r);
                int heightDiff = rootLayout.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...

                } else {
                    //ok now we know the keyboard is down...
                    rootLayout.setPadding(0,0,0,0);
                }
            }
        });
    }

    private void setDateTimeView() {
        // It get time in system ,set this TImePicker.
        Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(c.HOUR_OF_DAY);
        int minute = c.get(c.MINUTE);
        int year = c.get(c.YEAR);
        int month = c.get(c.MONTH);
        int dayOfMonth = c.get(c.DAY_OF_MONTH);

        //check setting time
        m_time_value = _timeFormatSet.format(c.getTime());
        time_btn.setText(_timeFormatDisplay.format(c.getTime()));

        time_picker.setIs24HourView(false);
        time_picker.setHour(hourOfDay);
        time_picker.setMinute(minute);

        date_picker.updateDate(year, month, dayOfMonth);

        linearSave.setVisibility(View.INVISIBLE);
    }


    private int getPositionFromTimezoneid(String timezoneID){

        for(int i=0 ; i<timezoneIDs.length; i++){
            if(timezoneIDs[i].equals(timezoneID))
                return i;
        }

        return 0;
    }

    private void setAutoCheckView(boolean isAuto){

        if(isAuto){
            date_btn.setEnabled(false);
            date_btn.setTextColor(Color.GRAY);
            date_picker.setEnabled(false);
            time_btn.setEnabled(false);
            time_btn.setTextColor(Color.GRAY);
            time_picker.setEnabled(false);
            auto_date_time_check.setTextColor(Color.WHITE);
            colorizeDatePicker(date_picker, R.color.date_time_divider);
            colorizeTimePicker(time_picker, R.color.date_time_divider);

            editor.putString(getString(R.string.prefer_key_auto_date_time), "auto");
            editor.commit();

        }else{
            date_btn.setEnabled(true);
            date_btn.setTextColor(getResources().getColor(R.color.settingTextSelect, null));
            date_picker.setEnabled(true);
            time_btn.setEnabled(true);
            time_btn.setTextColor(Color.WHITE);
            time_picker.setEnabled(true);
            auto_date_time_check.setTextColor(Color.LTGRAY);
            colorizeDatePicker(date_picker, R.color.date_time_text);
            colorizeTimePicker(time_picker, R.color.date_time_text);

            time_zone_spin.setEnabled(true);
            time_zone_Adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, getActivity().getResources().getStringArray(R.array.time_zone_displayname));
            time_zone_spin.setAdapter(time_zone_Adapter);
            time_zone_spin.setSelection(m_time_zone_value);

            editor.putString(getString(R.string.prefer_key_auto_date_time), "no_auto");
            editor.commit();
        }
    }

    public static void colorizeDatePicker(DatePicker datePicker, int id) {
        Resources system = Resources.getSystem();
        int dayId = system.getIdentifier("day", "id", "android");
        int monthId = system.getIdentifier("month", "id", "android");
        int yearId = system.getIdentifier("year", "id", "android");

        NumberPicker dayPicker = (NumberPicker) datePicker.findViewById(dayId);
        NumberPicker monthPicker = (NumberPicker) datePicker.findViewById(monthId);
        NumberPicker yearPicker = (NumberPicker) datePicker.findViewById(yearId);

        setDateDividerColor(dayPicker);
        setDateDividerColor(monthPicker);
        setDateDividerColor(yearPicker);

        numberPickerTextColor(dayPicker, datePicker.getResources().getColor(id));
        numberPickerTextColor(monthPicker, monthPicker.getResources().getColor(id));
        numberPickerTextColor(yearPicker, yearPicker.getResources().getColor(id));
    }

    private static void setDateDividerColor(NumberPicker picker) {
        if (picker == null)
            return;

        final int count = picker.getChildCount();
        for (int i = 0; i < count; i++) {
            try {
                Field dividerField = picker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(picker.getResources().getColor(R.color.date_time_divider));
                dividerField.set(picker, colorDrawable);
                picker.invalidate();

                Field dividerField_2 = picker.getClass().getDeclaredField("mSelectionDividerHeight");
                dividerField_2.setAccessible(true);
                dividerField_2.set(picker, 1);
            } catch (Exception e) {
                Log.e("setDividerColor", ">>>");
            }
        }
    }

    private static void numberPickerTextColor(NumberPicker view, int color) {
        for (int i = 0, j = view.getChildCount(); i < j; i++) {
            View t0 = view.getChildAt(i);
            if (t0 instanceof EditText) {
                try {
                    Field t1 = view.getClass().getDeclaredField("mSelectorWheelPaint");
                    t1.setAccessible(true);

                    ((Paint) t1.get(view)).setColor(color);
                    ((Paint) t1.get(view)).setTextSize(20);
                    ((EditText) t0).setTextColor(color);
                    ((EditText) t0).setTextSize(20);

                    view.invalidate();
                } catch (Exception e) {
                }
            }

            View t2 = view.getChildAt(i);
            if(t2 instanceof TextView) {
                try {
                    Field t3 = view.getClass().getDeclaredField("mSelectorWheelPaint");
                    t3.setAccessible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void colorizeTimePicker(TimePicker timePicker, int id) {
        Resources system = Resources.getSystem();
        int hourNumberPickerId = system.getIdentifier("hour", "id", "android");
        int minuteNumberPickerId = system.getIdentifier("minute", "id", "android");
        int ampmNumberPickerId = system.getIdentifier("amPm", "id", "android");

        NumberPicker hourNumberPicker = (NumberPicker) timePicker.findViewById(hourNumberPickerId);
        NumberPicker minuteNumberPicker = (NumberPicker) timePicker.findViewById(minuteNumberPickerId);
        NumberPicker ampmNumberPicker = (NumberPicker) timePicker.findViewById(ampmNumberPickerId);

        setTimeDividerColor(hourNumberPicker);
        setTimeDividerColor(minuteNumberPicker);
        setTimeDividerColor(ampmNumberPicker);

        numberPickerTextColor(hourNumberPicker, hourNumberPicker.getResources().getColor(id));
        numberPickerTextColor(minuteNumberPicker, minuteNumberPicker.getResources().getColor(id));
        numberPickerTextColor(ampmNumberPicker, ampmNumberPicker.getResources().getColor(id));
    }

    private void setTimeDividerColor(NumberPicker number_picker) {
        final int count = number_picker.getChildCount();

        for(int i = 0; i < count; i++) {
            try {
                Field dividerField = number_picker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.date_time_divider));
                dividerField.set(number_picker,colorDrawable);
                number_picker.invalidate();

                Field dividerField_2 = number_picker.getClass().getDeclaredField("mSelectionDividerHeight");
                dividerField_2.setAccessible(true);
                dividerField_2.set(number_picker, 1);
            } catch(NoSuchFieldException e) {
            } catch(IllegalAccessException e) {
            } catch(IllegalArgumentException e) {
            }
        }
    }

}