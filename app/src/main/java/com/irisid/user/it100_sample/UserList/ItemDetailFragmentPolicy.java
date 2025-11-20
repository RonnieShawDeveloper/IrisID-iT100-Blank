package com.irisid.user.it100_sample.UserList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.irisid.it100.IT100;
import com.irisid.it100.callback.CardInfoCallback;
import com.irisid.it100.callback.ChangeAccountCallback;
import com.irisid.it100.callback.DeleteUserCallback;
import com.irisid.it100.callback.EnrollUserCallback;
import com.irisid.it100.callback.UpdateUserCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Activity.CaptureActivity;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFacilityCode;
import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFormatArray;
import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFormatHashmap;
import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFormatNameList;
import static com.irisid.user.it100_sample.UserList.ItemsListFragment.itemArrayList;

public class ItemDetailFragmentPolicy extends Fragment implements View.OnClickListener {

    public Item userItem;
    int showType = ConstData.USERDETAIL_SHOW_ENROLL;
    int result_code = 0;
    String errMsg = "";
    String preUserid;
    int voice_guide;
    int enroll_guide;

    NestedScrollView nestedScrollView;
    LinearLayout rootLayout;
    LinearLayout linear_warning;
    LinearLayout linear_dim;
    LinearLayout linear_cardinfo;
    TextView txt_warning;

    Button btn_auth_mode;
    Button btn_card_mode;
    CheckBox checkbox_iris;
    CheckBox checkbox_face;
    CheckBox checkbox_card;

    ImageView admin_img;
    ImageView face_img;
    ImageView left_iris_img;
    ImageView right_iris_img;

    Button cancel_btn;
    Button delete_btn;
    Button save_btn;
    Button update_btn;
    Button idpw_btn;

    CheckBox checkUserActive;
    TextInputLayout tlUserId;
    TextInputLayout tlFirstName;
    TextInputLayout tlLastName;
    TextInputLayout tlEmail;
    TextInputLayout tlPhoneNum;

    TextInputEditText teUserId;
    TextInputEditText teFirstName;
    TextInputEditText teLastName;
    TextInputEditText teEmail;
    TextInputEditText tePhoneNum;

    TextInputLayout tlDepartment;
    TextInputLayout tlRole;
    AutoCompleteTextView teDepartment;
    AutoCompleteTextView teRole;

    LinearLayout linearGuideCapture;
    LinearLayout linearFaceError;
    LinearLayout linearLeyeError;
    LinearLayout linearReyeError;
    TextView txtLeyeError;
    TextView txtReyeError;
    public interface OnItemUpdateListener {
        void onUserListUpdate(String type);
    }
    private ItemDetailFragmentPolicy.OnItemUpdateListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            userItem = (Item) bundle.getSerializable(ConstData.USER_BUNDLE_USER_ITEM);
            showType = bundle.getInt(ConstData.USER_BUNDLE_SHOW_TYPE, ConstData.USERDETAIL_SHOW_ENROLL);
            result_code = bundle.getInt(ConstData.USER_BUNDLE_RESULT, 0);
            errMsg = bundle.getString(ConstData.USER_BUNDLE_ERROR_MSG, "");
        }else
            userItem = new Item();

        SharedPreferences pref;
        pref = getContext().getSharedPreferences(getString(R.string.prefer_name_setting), Context.MODE_PRIVATE);
        voice_guide = pref.getInt(getString(R.string.prefer_key_voice_guide), 0);
        enroll_guide = pref.getInt(getString(R.string.prefer_key_enroll_guide), 1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_detail_policy, container, false);

        rootLayout = (LinearLayout)view.findViewById(R.id.rootview);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.child_scroll);

        linear_warning = (LinearLayout) view.findViewById(R.id.linear_warning);
        txt_warning = (TextView) view.findViewById(R.id.txt_warning);
        linear_dim = (LinearLayout) view.findViewById(R.id.linear_dim);
        linear_cardinfo = (LinearLayout) view.findViewById(R.id.linear_cardinfo);

        btn_auth_mode = (Button) view.findViewById(R.id.btn_auth_mode);
        btn_auth_mode.setOnClickListener(this);
        btn_card_mode = (Button) view.findViewById(R.id.btn_card_andor_mode);
        btn_card_mode.setOnClickListener(this);
        checkbox_iris = (CheckBox) view.findViewById(R.id.checkbox_iris);
        checkbox_face = (CheckBox)view.findViewById(R.id.checkbox_face);
        checkbox_card = (CheckBox)view.findViewById(R.id.checkbox_card);

        cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(this);

        delete_btn = (Button) view.findViewById(R.id.delete_btn);
        delete_btn.setOnClickListener(this);

        save_btn = (Button) view.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);

        update_btn = (Button) view.findViewById(R.id.update_btn);
        update_btn.setOnClickListener(this);

        idpw_btn = (Button) view.findViewById(R.id.idpw_btn);
        idpw_btn.setOnClickListener(this);

        admin_img = (ImageView) view.findViewById(R.id.img_admin);
        face_img = (ImageView) view.findViewById(R.id.face_img);
        left_iris_img = (ImageView) view.findViewById(R.id.left_iris_img);
        right_iris_img = (ImageView) view.findViewById(R.id.right_iris_img);

        checkUserActive = (CheckBox) view.findViewById(R.id.ch_user_active);
        tlUserId = (TextInputLayout) view.findViewById(R.id.tl_userid);
        tlFirstName = (TextInputLayout) view.findViewById(R.id.tl_first_name);
        tlLastName = (TextInputLayout) view.findViewById(R.id.tl_last_name);
        tlEmail = (TextInputLayout) view.findViewById(R.id.tl_email);
        tlPhoneNum = (TextInputLayout) view.findViewById(R.id.tl_phone_num);

        teUserId = (TextInputEditText) view.findViewById(R.id.te_userid);
        teFirstName = (TextInputEditText) view.findViewById(R.id.te_first_name);
        teLastName = (TextInputEditText) view.findViewById(R.id.te_last_name);
        teEmail = (TextInputEditText) view.findViewById(R.id.te_email);
        tePhoneNum = (TextInputEditText) view.findViewById(R.id.te_phone_num);

        linearGuideCapture = (LinearLayout) view.findViewById(R.id.linear_guide_capture);
        linearFaceError = (LinearLayout) view.findViewById(R.id.linear_face_error);
        linearReyeError = (LinearLayout) view.findViewById(R.id.linear_reye_error);
        linearLeyeError = (LinearLayout) view.findViewById(R.id.linear_leye_error);
        txtLeyeError = (TextView) view.findViewById(R.id.txt_leye_error);
        txtReyeError = (TextView) view.findViewById(R.id.txt_reye_error);

        // policy
        tlDepartment = (TextInputLayout) view.findViewById(R.id.tl_department);
        tlRole = (TextInputLayout) view.findViewById(R.id.tl_role);
        teDepartment = (AutoCompleteTextView)view.findViewById(R.id.at_department);
        teRole = (AutoCompleteTextView)view.findViewById(R.id.at_role);

        setInitView();

        preUserid= userItem.getUserId();

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

//        registerCallbackListener();
        return view;
    }

    private void setCardRegisterInfo(final String jparamCardinfo) {

        if(_dialogEnrollCard!=null)
            _dialogEnrollCard.dismiss();
        _dialogEnrollCard = null;

        if(jparamCardinfo==null)
            return;

        if(_cardArray!=null) {
            try {
                if(_cardArray.length()==0)
                    _cardArray.put(0, new JSONObject(jparamCardinfo));
                else
                    _cardArray.put(_cardArray.length()-1, new JSONObject(jparamCardinfo));
                refreshCardlistView();
                //Logger.d("yksong", _cardArray.toString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemDetailFragmentPolicy.OnItemUpdateListener) {
            listener = (ItemDetailFragmentPolicy.OnItemUpdateListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ItemDetailFragment.OnItemUpdateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        if(_dialogEnrollGuide!=null)
            _dialogEnrollGuide.dismiss();
        _dialogEnrollGuide = null;
    }
    int captureStatusFace = 0;
    int captureStatusLEye = 0;
    int captureStatusREye = 0;

    private void setInitView(){
        setAuthMode();
        setCardInfo(userItem.getCardInfo());

        checkbox_iris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    btn_auth_mode.setEnabled(false);
                    if(!checkbox_face.isChecked() && !checkbox_iris.isChecked())
                        btn_card_mode.setEnabled(false);
                }else{
                    if(checkbox_face.isChecked())
                        btn_auth_mode.setEnabled(true);

                    if((checkbox_face.isChecked() || checkbox_iris.isChecked())&& checkbox_card.isChecked())
                        btn_card_mode.setEnabled(true);
                }
            }
        });

        checkbox_face.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    btn_auth_mode.setEnabled(false);
                    if (!checkbox_face.isChecked() && !checkbox_iris.isChecked())
                        btn_card_mode.setEnabled(false);
                }else{
                    if(checkbox_iris.isChecked())
                        btn_auth_mode.setEnabled(true);
                    if((checkbox_face.isChecked() || checkbox_iris.isChecked()) && checkbox_card.isChecked() )
                        btn_card_mode.setEnabled(true);
                }
            }
        });

        checkbox_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    btn_card_mode.setEnabled(false);
                else{
                   if(!checkbox_face.isChecked() && !checkbox_iris.isChecked())
                       btn_card_mode.setEnabled(false);
                   else
                       btn_card_mode.setEnabled(true);
                }
            }
        });

        if(userItem.getIs_admin()!=null && userItem.getIs_admin().equals(MessageKeyValue.USER_ROLE_ADMINISTRATOR))
            admin_img.setVisibility(View.VISIBLE);
        else
            admin_img.setVisibility(View.GONE);

        if(userItem.getFace_img() !=null && !userItem.getFace_code().isEmpty()) {
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(userItem.getFace_img(), 0, userItem.getFace_img().length);
            face_img.setImageBitmap(faceBitmap);
        }

        if(userItem.getRight_iris_img() !=null && !userItem.getRight_iris_code().isEmpty()) {
            Bitmap right_iris_Bitmap = BitmapFactory.decodeByteArray(userItem.getRight_iris_img(), 0, userItem.getRight_iris_img().length);
            right_iris_img.setImageBitmap(right_iris_Bitmap);
        }

        if(userItem.getLeft_iris_img() !=null && !userItem.getLeft_iris_code().isEmpty()) {
            Bitmap left_iris_Bitmap = BitmapFactory.decodeByteArray(userItem.getLeft_iris_img(), 0, userItem.getLeft_iris_img().length);
            left_iris_img.setImageBitmap(left_iris_Bitmap);
        }

        if(userItem.active){
            checkUserActive.setText(safeGetResouces().getString(R.string.user_active));
            linear_dim.setVisibility(View.GONE);
        }else{
            checkUserActive.setText(safeGetResouces().getString(R.string.user_inactive));
            linear_dim.setVisibility(View.VISIBLE);
            linear_dim.setTouchscreenBlocksFocus(true);
        }
        checkUserActive.setChecked(userItem.active);
        checkUserActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    checkUserActive.setText(safeGetResouces().getString(R.string.user_inactive));
                    linear_dim.setVisibility(View.VISIBLE);
                }else{
                    checkUserActive.setText(safeGetResouces().getString(R.string.user_active));
                    linear_dim.setVisibility(View.GONE);
                }
            }
        });

        teUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0) {
                    tlUserId.setHelperTextEnabled(false);
                }else{
                    tlUserId.setHelperTextEnabled(true);
                    tlUserId.setHelperText(safeGetResouces().getString(R.string.required));
                }

                if(s.toString().contains(" ")){
                    //teUserId.setText(s.toString().replace(" ", ""));
                    teUserId.setError(safeGetResouces().getString(R.string.enroll_error_space_userid));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        teUserId.setText(userItem.getUserId()==null? "":userItem.getUserId());
        teFirstName.setText(userItem.getFirst_name()==null? "":userItem.getFirst_name());
        teLastName.setText(userItem.getLast_name()==null? "":userItem.getLast_name());
        teEmail.setText(userItem.getLast_name()==null? "":userItem.getEmail_address());
        tePhoneNum.setText(userItem.getPhone_number()==null? "":userItem.getPhone_number());
        teDepartment.setText(userItem.department == null? "": userItem.department);
        teRole.setText(userItem.role == null? "": userItem.role);

        linear_warning.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        save_btn.setVisibility(View.GONE);
        delete_btn.setVisibility(View.GONE);
        update_btn.setVisibility(View.GONE);
        idpw_btn.setVisibility(View.GONE);

        if(showType == ConstData.USERDETAIL_SHOW_ENROLL){
            checkUserActive.setChecked(true);
            cancel_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);
        }else if(showType == ConstData.USERDETAIL_SHOW_DETAIL){
            checkUserActive.setChecked(userItem.active);
            if(!userItem.getIs_admin().equals(MessageKeyValue.USER_ROLE_ADMINISTRATOR))
                delete_btn.setVisibility(View.VISIBLE);
            else
                idpw_btn.setVisibility(View.VISIBLE);

            update_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);

            if(userItem.jsonCaptureStatus!=null && !userItem.jsonCaptureStatus.isEmpty()) {
                //linearGuideCapture.setVisibility(View.GONE);
                try {
                    JSONObject jCaptureStatus = new JSONObject(userItem.jsonCaptureStatus);

                    captureStatusFace = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_FACE);
                    captureStatusLEye = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_LEFT_EYE);
                    captureStatusREye = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_RIGHT_EYE);

                    if(userItem.captureFace && captureStatusFace==MessageKeyValue.CAPTURE_BIO_RESULT_CAPTURE_FAIL)
                        linearFaceError.setVisibility(View.VISIBLE);

                    if(userItem.captureIris && captureStatusLEye!=MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS) {
                        linearLeyeError.setVisibility(View.VISIBLE);
                        if(captureStatusLEye== MessageKeyValue.CAPTURE_BIO_RESULT_CAPTURE_FAIL)
                            txtLeyeError.setText(safeGetResouces().getString(R.string.capture_fail));
                        else if(captureStatusLEye == MessageKeyValue.CAPTURE_BIO_RESULT_LIVENESS_DETECTION_FAIL)
                            txtLeyeError.setText(IrisApplication.getInstance().getResources().getString(R.string.liveness_eye_detect_fail));
                    }

                    if(userItem.captureIris && captureStatusREye!=MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS) {
                        linearReyeError.setVisibility(View.VISIBLE);
                        if(captureStatusREye== MessageKeyValue.CAPTURE_BIO_RESULT_CAPTURE_FAIL)
                            txtReyeError.setText(safeGetResouces().getString(R.string.capture_fail));
                        else if(captureStatusREye == MessageKeyValue.CAPTURE_BIO_RESULT_LIVENESS_DETECTION_FAIL)
                            txtReyeError.setText(safeGetResouces().getString(R.string.liveness_eye_detect_fail));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else if(showType == ConstData.USERDETAIL_SHOW_ERROR){
            cancel_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);

            teUserId.setText("");
            teFirstName.setText("");
            teLastName.setText("");
            teEmail.setText("");
            tePhoneNum.setText("");

            save_btn.setEnabled(false);
            linear_warning.setVisibility(View.VISIBLE);
            txt_warning.setText(errMsg!=null? errMsg : "");

            linear_dim.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

        }else if(showType == ConstData.USERDETAIL_SHOW_ERROR_DETAIL){ //for userbio modify
            cancel_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);
            save_btn.setEnabled(false);

            linear_warning.setVisibility(View.VISIBLE);
            txt_warning.setText(errMsg!=null? errMsg : "");

            linear_dim.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }


        face_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(showType == ConstData.USERDETAIL_SHOW_ERROR_DETAIL || showType == ConstData.USERDETAIL_SHOW_ERROR)
                    return false;

                String dialog_msg = "";
                String dialog_title = "";

                if(userItem.getRight_iris_code().equals("") && userItem.getLeft_iris_code().equals("")
                        && userItem.getFace_code().equals("")){
                    dialog_title= safeGetResouces().getString(R.string.add_bioinfo_dialog_title);
                    dialog_msg = safeGetResouces().getString(R.string.add_bioinfo_dialog);

                    //showCaptureConfirmDialog(dialog_title, dialog_msg);
                    showPrivacyDialog(false);
                }else {
                    dialog_title= safeGetResouces().getString(R.string.modify_bioinfo_dialog_title);
                    //showCaptureChoiceItemDialog(dialog_title);
                    showPrivacyDialog(true);
                }
                return false;
            }
        });

        // policy
    }

    public static boolean checkEmail(String email) {

        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    public static boolean isValidPhone(String phone) {

        String regex = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";
        CharSequence inputString = phone;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputString);
        if (m.matches())
            return true;
        else
            return false;
    }

    public static ItemDetailFragmentPolicy newInstance(Item item, int showType) {

        ItemDetailFragmentPolicy fragmentDemo = new ItemDetailFragmentPolicy();
        Bundle args = new Bundle();
        args.putSerializable(ConstData.USER_BUNDLE_USER_ITEM, item);
        args.putInt(ConstData.USER_BUNDLE_SHOW_TYPE, showType);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }



    public void saveImage(Bitmap bitmap, String filename)
    {
        File file = new File("/sdcard/DCIM/"+ filename);
        try (FileOutputStream out = new FileOutputStream(file))
        {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            getContext().sendBroadcast(intent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.save_btn:
                if(!validUserInfo())
                    return;

                try {
                    for(int i=0; i<_cardArray.length(); i++){
                        JSONObject jobj= _cardArray.getJSONObject(i);
                        if(jobj.optBoolean("isEmpty", false))
                            _cardArray.remove(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userItem.setUser_id(teUserId.getText().toString());
                userItem.setFirst_name(teFirstName.getText().toString());
                userItem.setLast_name((teLastName.getText().toString()));
                userItem.setEmail_address(teEmail.getText().toString());
                userItem.setPhone_number(tePhoneNum.getText().toString());
                userItem.setIs_admin(MessageKeyValue.USER_ROLE_USER);
                userItem.setRecogMode(_recogMode_value);
                userItem.setActive(checkUserActive.isChecked());
                userItem.setCardInfo(_cardArray);
                if(checkUserActive.isChecked())
                    userItem.status = MessageKeyValue.USER_STATUS_ACTIVE;
                else
                    userItem.status = MessageKeyValue.USER_STATUS_INACTIVE;
                long rtnEnroll = 0 ;
                rtnEnroll = IT100.enrollUser(userItem, new EnrollUserCallback() {
                    @Override
                    public void enrollUserResult(int resultCode, String jsonParam) {
                        JSONObject jsonObject= null;

                        try {
                            jsonObject = new JSONObject(jsonParam);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch (resultCode){
                            case MessageType.ID_SUCCESS:
                                if (listener != null) {
                                    listener.onUserListUpdate(ConstData.USERLIST_RELOAD);
                                }
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.flDetailContainer, new ItemEmptyFragment())
                                        .commit();

                                showSafeToast(safeGetResouces().getString(R.string.dlgtitle_success));

                                break;

                            case MessageType.ID_DUPLICATE_USER_ID :  // duplicated user ID
                                tlUserId.setErrorEnabled(true);
                                tlUserId.setError(safeGetResouces().getString(R.string.enroll_error_duplicate_userid));
                                break;

                            default:
                                showSafeToast(safeGetResouces().getString(R.string.dlgtitle_fail)+" : "
                                        + jsonObject.optString(MessageType.Message));
                                break;
                        }
                    }
                });

                if ( rtnEnroll  == MessageType.ID_RTN_SUCCESS){   }// success
                else if( rtnEnroll  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnEnroll  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                else if( rtnEnroll  == MessageType.ID_RTN_FAIL){}// fail

                break;

            case R.id.delete_btn:
                BasicAlertDialog builder = new BasicAlertDialog(getContext());
                builder.setTitle(getString(R.string.delete));
                builder.setMessage(R.string.delete_dialog);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        // Send delete event
                        long rtnDelete = 0 ;
                        rtnDelete = IT100.deleteUser(userItem.getUserId(), new DeleteUserCallback()
                        {

                            @Override
                            public void deleteUserResult(int resultCode) {

                                if (listener != null && resultCode ==0) {
                                    // delete doing
                                    //remove(userItem);
                                    listener.onUserListUpdate(ConstData.USERLIST_RELOAD);
                                }
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.flDetailContainer, new ItemEmptyFragment())
                                        .commit();
                            }
                        });

                        if ( rtnDelete  == MessageType.ID_RTN_SUCCESS){   }// success
                        else if( rtnDelete  == MessageType.ID_RTN_WRONG_PARA){}// fail
                        else if( rtnDelete  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                        else if( rtnDelete  == MessageType.ID_RTN_FAIL){}// fail

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;


            case R.id.update_btn:

                if(!validUserInfo())
                    return;

                try {
                    for(int i=0; i<_cardArray.length(); i++){
                        JSONObject jobj= _cardArray.getJSONObject(i);
                        if(jobj.optBoolean("isEmpty", false))
                            _cardArray.remove(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                userItem.setUser_id(teUserId.getText().toString());
                userItem.setFirst_name(teFirstName.getText().toString());
                userItem.setLast_name((teLastName.getText().toString()));
                userItem.setEmail_address(teEmail.getText().toString());
                userItem.setPhone_number(tePhoneNum.getText().toString());
                userItem.setRecogMode(_recogMode_value);
                userItem.setCardInfo(_cardArray);
                userItem.setActive(checkUserActive.isChecked());

                if(checkUserActive.isChecked())
                    userItem.status = MessageKeyValue.USER_STATUS_ACTIVE;
                else
                    userItem.status = MessageKeyValue.USER_STATUS_INACTIVE;

                long rtnUpdate = 0 ;
                rtnUpdate = IT100.updateUser(userItem, new UpdateUserCallback() {

                    @Override
                    public void updateUserResult(final int resultCode, final String jsonParam) {
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject jsonObject= null;
                                    try {
                                        jsonObject = new JSONObject(jsonParam);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(resultCode==0) {
                                        if (listener != null) {
                                            listener.onUserListUpdate(ConstData.USERLIST_RELOAD);
                                        }
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.flDetailContainer, new ItemEmptyFragment())
                                                .commit();
                                        showSafeToast(safeGetResouces().getString(R.string.dlgtitle_success));
                                    }else if(resultCode == MessageType.ID_DUPLICATE_USER_ID){
                                        tlUserId.setErrorEnabled(true);
                                        tlUserId.setError(safeGetResouces().getString(R.string.enroll_error_duplicate_userid));
                                    }else{
                                        showSafeToast(safeGetResouces().getString(R.string.dlgtitle_fail)+" : "
                                                + jsonObject.optString(MessageType.Message));
                                    }
                                }
                            });
                        }
                    }
                });

                if ( rtnUpdate  == MessageType.ID_RTN_SUCCESS){   }// success
                else if( rtnUpdate  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnUpdate  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                else if( rtnUpdate  == MessageType.ID_RTN_FAIL){}// fail

                break;

            case R.id.cancel_btn:
                if (listener != null) {
                    listener.onUserListUpdate(ConstData.USERLIST_REFRESH);
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flDetailContainer, new ItemEmptyFragment())
                        .commit();
                break;

            case R.id.idpw_btn:
                onChangePassword();
                break;

            case R.id.btn_auth_mode:
                String authMode = btn_auth_mode.getText().toString();
                if(authMode.equals(ConstData.AUTH_MODE_FUSION))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                else if(authMode.equals(ConstData.AUTH_MODE_OR))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                else if(authMode.equals(ConstData.AUTH_MODE_AND))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                break;

            case R.id.btn_card_andor_mode:
                String andOrMode = btn_card_mode.getText().toString();
                if(andOrMode.equals(ConstData.AUTH_MODE_AND))
                    btn_card_mode.setText(ConstData.AUTH_MODE_OR);
                else if(andOrMode.equals(ConstData.AUTH_MODE_OR))
                    btn_card_mode.setText(ConstData.AUTH_MODE_AND);

                break;
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
    }

    String _pwCurrent="";
    String _pwNew="";
    String _pwConfirm="";
    private void onChangePassword() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content =  inflater.inflate(R.layout.dialog_change_password, null);

        ((EditText)content.findViewById(R.id.pw_current_edt)).setText(_pwCurrent);
        ((EditText)content.findViewById(R.id.pw_new_edt)).setText(_pwNew);
        ((EditText)content.findViewById(R.id.pw_confirm_edt)).setText(_pwConfirm);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.pw_change);
        builder.setView(content);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText editCurrent = (EditText) ((AlertDialog)dialog).findViewById(R.id.pw_current_edt);
                        EditText editNew = (EditText) ((AlertDialog) dialog).findViewById(R.id.pw_new_edt);
                        EditText editConfirm = (EditText) ((AlertDialog) dialog).findViewById(R.id.pw_confirm_edt);

                        _pwCurrent = editCurrent.getText().toString();
                        _pwNew = editNew.getText().toString();
                        _pwConfirm = editConfirm.getText().toString();

                        if(_pwNew.length()<4){
                            new BasicAlertDialog(getContext())
                                    .setTitle(R.string.dlgtitle_err)
                                    .setMessage(R.string.err_pw_4digit)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                            return;
                        }

                        if(_pwNew.equals(_pwConfirm)){

                            IT100.changeAccount(userItem.getUserId(), _pwCurrent, _pwNew, "", "", new ChangeAccountCallback() {
                                @Override
                                public void changeAccountResult(int i) {
                                    if(i== MessageType.ID_MATCHER_ERROR){
                                        showSafeToast(safeGetResouces().getString(R.string.err_pw_invalid));
                                    }else if(i== MessageType.ID_SUCCESS){
                                        _pwCurrent = "";
                                        _pwNew = "";
                                        _pwConfirm = "";
                                        showSafeToast(safeGetResouces().getString(R.string.pw_changed));
                                    }
                                }
                            });

                        }else{
                            new BasicAlertDialog(getContext())
                                    .setTitle(R.string.dlgtitle_err)
                                    .setMessage(R.string.err_pw_matched)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                        }

                    }
                });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void remove(Item remove_item){
        for(Item item: itemArrayList){
            if(remove_item.getUserId().equals(item.userID)) {
                itemArrayList.remove(item);
                break;
            }
        }
    }

    private void modify(String pre_id, Item update_item){
        for(int i = 0 ; i< itemArrayList.size(); i++){
            Item item = itemArrayList.get(i);
            if(pre_id.equals(item.userID)){
                item.userID = update_item.userID;
                item.firstName = update_item.firstName;
                item.lastName = update_item.lastName;
                break;
            }
        }
    }

    private void setAuthMode() {

        //Logger.d("@@getRecogmode value is "+ userItem.getRecogMode());
        btn_card_mode.setText(ConstData.AUTH_MODE_AND);
        if(userItem.getRecogMode() == null){

            checkbox_iris.setChecked(false);
            checkbox_face.setChecked(false);
            checkbox_card.setChecked(false);
            btn_auth_mode.setEnabled(false);
            btn_card_mode.setEnabled(false);

            //in case of pending user, checkbox disable?
            if(userItem.getFace_img()==null || userItem.getFace_img().length==0){
                checkbox_face.setEnabled(false);
                checkbox_iris.setEnabled(false);
            }

        }else if(userItem.getRecogMode().contains("Card")){
            if(userItem.getRecogMode().length()<5) {
                btn_card_mode.setEnabled(false);
                btn_auth_mode.setEnabled(false);
                checkbox_face.setChecked(false);
                checkbox_iris.setChecked(false);
            }else{
                btn_card_mode.setEnabled(true);
                checkbox_card.setEnabled(true);
            }

            String temp;
            if(userItem.getRecogMode().contains("AndCard")){
                temp = userItem.getRecogMode().replace("AndCard", "");
                btn_card_mode.setText(ConstData.AUTH_MODE_AND);

                if(temp.equals(MessageKeyValue.AUTHMODE_FACE_AND_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                else if(temp.equals(MessageKeyValue.AUTHMODE_FACE_OR_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                else if(temp.equals(MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                else if(temp.equals(MessageKeyValue.AUTHMODE_FACE_ONLY)) {
                    btn_auth_mode.setEnabled(false);
                    checkbox_face.setChecked(true);
                    checkbox_iris.setChecked(false);
                }else if(temp.equals(MessageKeyValue.AUTHMODE_IRIS_ONLY)) {
                    btn_auth_mode.setEnabled(false);
                    checkbox_face.setChecked(false);
                    checkbox_iris.setChecked(true);
                } else //default authentication mode
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
            }else if(userItem.getRecogMode().contains("OrCard")){
                temp = userItem.getRecogMode().replace("OrCard", "");
                btn_card_mode.setText(ConstData.AUTH_MODE_OR);

                if(temp.equals(MessageKeyValue.AUTHMODE_FACE_AND_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                else if(temp.equals(MessageKeyValue.AUTHMODE_FACE_OR_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                else if(temp.equals(MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                else if(temp.equals(MessageKeyValue.AUTHMODE_FACE_ONLY)) {
                    btn_auth_mode.setEnabled(false);
                    checkbox_face.setChecked(true);
                    checkbox_iris.setChecked(false);
                }else if(temp.equals(MessageKeyValue.AUTHMODE_IRIS_ONLY)) {
                    btn_auth_mode.setEnabled(false);
                    checkbox_face.setChecked(false);
                    checkbox_iris.setChecked(true);
                } else //default authentication mode
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
            }else{
                btn_card_mode.setText(ConstData.AUTH_MODE_AND);
            }
        }else {
            btn_card_mode.setEnabled(false);
            checkbox_card.setChecked(false);

            if(userItem.getRecogMode().equals(MessageKeyValue.AUTHMODE_IRIS_ONLY)){
                checkbox_iris.setChecked(true);
                checkbox_face.setChecked(false);
                btn_auth_mode.setEnabled(false);

            } else if(userItem.getRecogMode().equals(MessageKeyValue.AUTHMODE_FACE_ONLY)){
                checkbox_iris.setChecked(false);
                checkbox_face.setChecked(true);
                btn_auth_mode.setEnabled(false);
            } else{
                checkbox_iris.setChecked(true);
                checkbox_face.setChecked(true);
                btn_auth_mode.setEnabled(true);

                if(userItem.getRecogMode().equals(MessageKeyValue.AUTHMODE_FACE_AND_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_AND);
                else if(userItem.getRecogMode().equals(MessageKeyValue.AUTHMODE_FACE_OR_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);
                else if(userItem.getRecogMode().equals(MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS))
                    btn_auth_mode.setText(ConstData.AUTH_MODE_FUSION);
                else //default authentication mode
                    btn_auth_mode.setText(ConstData.AUTH_MODE_OR);

                if(userItem.getFace_img()==null || userItem.getFace_img().length==0){
                    checkbox_face.setEnabled(false);
                    checkbox_iris.setEnabled(false);
                }
            }
        }
    }


    String _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS;
    private boolean validUserInfo() {
        String selectedAuthMode;
        String selectedAuthCardMode;

        if(((userItem.getFace_img()== null) || (userItem.getFace_img().length == 0)) &&
                ((userItem.getLeft_iris_img()== null) || (userItem.getLeft_iris_img().length == 0)) &&
                ((userItem.getRight_iris_img()== null) || (userItem.getRight_iris_img().length == 0))){

            if(_cardArray.length()==0){
                new BasicAlertDialog(getContext())
                        .setTitle("Empty Error")
                        .setMessage("Please capture biometrics or Register Card")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })
                        .show();

                return false;
            }else if(_cardArray.length()==1){
                try {
                    JSONObject jobj= _cardArray.getJSONObject(0);
                    if(jobj.optBoolean("isEmpty", false)){
                        new BasicAlertDialog(getContext())
                                .setTitle("Empty Error")
                                .setMessage("Please capture biometrics or Register Card")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                    }
                                })
                                .show();
                        return false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            new BasicAlertDialog(getContext())
//                    .setTitle("Biometrics empty Error")
//                    .setMessage("Please capture biometrics")
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Continue with delete operation
//                        }
//                    })
//                    .show();

        }

        selectedAuthCardMode = btn_card_mode.getText().toString();
        // All IRIS, FACE button selected.
        if(btn_auth_mode.isEnabled()){
            selectedAuthMode = btn_auth_mode.getText().toString();

            if(btn_card_mode.isEnabled()){
                if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_AND)) {
                    if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS_AND_CARD;
                    else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS_AND_CARD;
                    else
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS_AND_CARD;
                }else if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_OR)) {
                    if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS_OR_CARD;
                    else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS_OR_CARD;
                    else
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS_OR_CARD;
                }
            }else {
                if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS;
                else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS;
                else
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS;
            }

        }else{

            if(btn_card_mode.isEnabled()){
                if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_AND)) {
                    if (checkbox_iris.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_IRIS_AND_CARD;
                    } else if (checkbox_face.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_CARD;
                    }
                }else if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_OR)){
                    if (checkbox_iris.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_IRIS_OR_CARD;
                    } else if (checkbox_face.isChecked()) {
                        _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_CARD;
                    }
                }
            }else{
                if (checkbox_iris.isChecked()) {
                    _recogMode_value = MessageKeyValue.AUTHMODE_IRIS_ONLY;
                }else if(checkbox_face.isChecked()){
                    _recogMode_value = MessageKeyValue.AUTHMODE_FACE_ONLY;
                }else if(checkbox_card.isChecked()){
                    _recogMode_value = MessageKeyValue.AUTHMODE_CARD_ONLY;
                } else{
                    //if((userItem.getFace_img()== null) || userItem.getFace_img().length == 0) {
                    //    _recogMode_value = " ";
                    //}else {

                        new BasicAlertDialog(getContext())
                                .setTitle(R.string.select_mode)
                                .setMessage(R.string.select_mode_error)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                    }
                                })
                                .show();
                        return false;
                    //}
                }
            }
        }

       // Logger.d("@@ recogmode valuse is "+ _recogMode_value);
        if(teUserId.getText().toString().equals("")){
            tlUserId.setErrorEnabled(true);
            tlUserId.setError(safeGetResouces().getString(R.string.enroll_error_empty_userid));

            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
            }
            return false;
        }

        if(teUserId.getText().toString().contains(" ")){
            tlUserId.setErrorEnabled(true);

            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
            }
            return false;
        }

        return true;
    }

    //Card reader related
    JSONArray _cardArray=null;
    private void setCardInfo(JSONArray card_info){

//        if(card_info!=null)
//            Logger.d("setCardInfo  cards is "+ card_info.toString());

        JSONObject jEmptyCard = new JSONObject();
        try {
            jEmptyCard.put("isEmpty", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(card_info == null|| card_info.equals("")) {
            _cardArray = new JSONArray();
            _cardArray.put(jEmptyCard);
        }else
            _cardArray = card_info;

        if(_cardArray.length()==0){
            _cardArray.put(jEmptyCard);
        }
        //Logger.d("_cardArray 000  is "+ _cardArray.toString());

        refreshCardlistView();
    }

    private void cancelEnrollCard(){
        if(_dialogEnrollCard!=null)
            _dialogEnrollCard.dismiss();
        _dialogEnrollCard = null;

        IT100.readCard(null);
    }

    private void refreshCardlistView() {
        if(getActivity()==null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                linear_cardinfo.removeAllViews();

                try {
                    if(_cardArray.length()==0){
                        LinearLayout itemLayout= (LinearLayout)inflater.inflate(R.layout.panel_cardinfo_item, null, false);
                        linear_cardinfo.addView(itemLayout);

                        itemLayout.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                               // Logger.d("cardlist onTouch label is "+ v.getLabelFor());
                                if(_dialogEnrollCard==null || (_dialogEnrollCard!=null && !_dialogEnrollCard.isShowing()))
                                    onEnrollCard();
                                return false;
                            }
                        });
                    }else {
                        for (int n = 0; n < _cardArray.length(); n++) {
                            JSONObject cardItem = _cardArray.getJSONObject(n);

                            LinearLayout itemLayout = (LinearLayout)inflater.inflate(R.layout.panel_cardinfo_item, null, false);
                            linear_cardinfo.addView(itemLayout, n);

                            if (cardItem.has(MessageKeyValue.CARDINFO_CARDID) && cardItem.optLong(MessageKeyValue.CARDINFO_CARDID) != 0) {
                                //((TextView)itemLayout.findViewById(R.id.txt_cardid)).setText(cardItem.optString("cardID", ""));
                                ((TextInputEditText) itemLayout.findViewById(R.id.te_1st)).setText(cardItem.optString(MessageKeyValue.CARDINFO_CARDID, ""));
                                ((TextInputLayout) itemLayout.findViewById(R.id.tl_1st)).setHint(safeGetResouces().getString(R.string.card_id));
                            }else {
                                if(cardItem.has(MessageKeyValue.CARDINFO_WDATA)) {
                                    ((TextInputEditText) itemLayout.findViewById(R.id.te_1st)).setText(cardItem.optString(MessageKeyValue.CARDINFO_WDATA, ""));
                                    ((TextInputLayout) itemLayout.findViewById(R.id.tl_1st)).setHint(safeGetResouces().getString(R.string.card_wiegand_data));
                                }
                                ((TextInputLayout) itemLayout.findViewById(R.id.tl_2st)).setVisibility(View.GONE);
                            }
                            if(cardItem.has(MessageKeyValue.CARDINFO_FCODE)) {
                                ((TextInputEditText) itemLayout.findViewById(R.id.te_2st)).setText(cardItem.optString(MessageKeyValue.CARDINFO_FCODE, ""));
                                ((TextInputLayout) itemLayout.findViewById(R.id.tl_2st)).setHint(safeGetResouces().getString(R.string.card_facility_code));
                            }

                            ((LinearLayout) itemLayout.findViewById(R.id.linear_touch_region)).setLabelFor(n);
                            ((LinearLayout) itemLayout.findViewById(R.id.linear_touch_region)).setOnClickListener(new View.OnClickListener() {
                                //itemLayout.setLabelFor(n);
                                //itemLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(result_code==0) {
                                    try {
                                        JSONObject cardobj = (JSONObject)_cardArray.get(v.getLabelFor());
                                        if(!cardobj.optBoolean("isEmpty", false)){
                                            onDetailCard(cardobj, v.getLabelFor());
                                        }else {
                                            onEnrollCard();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        }
                                    }

                                }
                            });

                            if(n==_cardArray.length()-1)
                                ((ImageView)itemLayout.findViewById(R.id.img_item_add)).setVisibility(View.VISIBLE);
                            else
                                ((ImageView)itemLayout.findViewById(R.id.img_item_add)).setVisibility(View.INVISIBLE);

                            ((ImageView)itemLayout.findViewById(R.id.img_item_add)).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {


                                    addEmptyCardItem();
                                    return true;
                                }
                            });
                            //Logger.d("cardItem is " + cardItem.toString(4));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onDetailCard(JSONObject cardObj, final int position) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_card_detail);
        dialog.setTitle(R.string.enter_card_info);
        Button dialogDeleteButton = (Button) dialog.findViewById(R.id.btn_delete);

        JSONObject jCardFormat = wFormatHashmap.get(cardObj.optString(MessageKeyValue.CARDINFO_WFORMAT_ID));
        if(jCardFormat!=null) {
            ((EditText) dialog.findViewById(R.id.edit_totalbit)).setText(
                            jCardFormat.optString(MessageKeyValue.WFORMAT_NAME));

            if(jCardFormat.optInt("facilityCodeLen")==0) {
                dialog.findViewById(R.id.txt_fcode).setVisibility(View.GONE);
                dialog.findViewById(R.id.edit_fcode).setVisibility(View.GONE);
            }else{
                dialog.findViewById(R.id.txt_fcode).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.edit_fcode).setVisibility(View.VISIBLE);
                ((EditText) dialog.findViewById(R.id.edit_fcode)).setText(cardObj.optString(MessageKeyValue.CARDINFO_FCODE));
            }
        }else{
            ((EditText) dialog.findViewById(R.id.edit_totalbit)).setText("");
        }
        ((EditText) dialog.findViewById(R.id.edit_cardid)).setText(cardObj.optString(MessageKeyValue.CARDINFO_CARDID));
        ((EditText) dialog.findViewById(R.id.edit_wdata)).setText(cardObj.optString(MessageKeyValue.CARDINFO_WDATA));

        dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(_cardArray.length()>0) {
                    _cardArray.remove(position);
                }
                refreshCardlistView();
            }
        });

        Button dialogOKButton = (Button) dialog.findViewById(R.id.btn_ok);
        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //카드 정보 입력
            }
        });
        dialog.show();
    }


    private void onEnrollCardManual() {
        // temp code
        if(wFormatArray==null || wFormatArray.length()==0){
//            new BasicAlertDialog(getContext())
//                    .setTitle("Wiegand card Error")
//                    .setMessage("Can't wiegand card format know")
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Continue with delete operation
//                        }
//                    })
//                    .show();
            return;
        }


        String[] time_list = {"no data"};
        final Dialog dialog = new Dialog(getActivity());
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_card_enroll_manual);
        dialog.setTitle(R.string.enter_card_info);
        //wFormatNameList

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Spinner wformatSpin = (Spinner) dialog.findViewById(R.id.totalbit_spin);
        ArrayAdapter wformatSpinAdapter;
        if(wFormatNameList!=null) {
            wformatSpinAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, wFormatNameList.toArray(new String[0]));
        }else {
            wformatSpinAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, time_list);
        }
        wformatSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wformatSpin.setAdapter(wformatSpinAdapter);
        wformatSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jItemFormat = (JSONObject)wFormatArray.optJSONObject(position);
                if(jItemFormat!=null) {
                    //Logger.d("jItemForamt  is " + jItemFormat.toString(4));
                    EditText fCode = (EditText) dialog.findViewById(R.id.edit_fcode);
                    TextView txtFcode = (TextView) dialog.findViewById(R.id.txt_fcode);
                    if(jItemFormat.optInt("facilityCodeLen")==0) {
                        fCode.setVisibility(View.GONE);
                        fCode.setText("");
                        txtFcode.setVisibility(view.GONE);
                    }else{
                        fCode.setVisibility(View.VISIBLE);
                        if(wFacilityCode!=-1)
                            fCode.setText(String.valueOf(wFacilityCode));
                        txtFcode.setVisibility(view.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        EditText fCode = (EditText) dialog.findViewById(R.id.edit_fcode);
//        if((wFacilityCode!=-1) && (fCode.getVisibility()==View.VISIBLE))
//            fCode.setText(String.valueOf(wFacilityCode));
        EditText wData = (EditText) dialog.findViewById(R.id.edit_wdata);
        InputFilter inputFilterText = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                Pattern patern = Pattern.compile("^\\p{XDigit}+$");
                StringBuilder sb = new StringBuilder();

                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }

                    Matcher matcher = patern.matcher(String.valueOf(source.charAt(i)));
                    if (!matcher.matches()) {
                        return "";
                    }
                    sb.append(source.charAt(i));
                }
                return  sb.toString().toUpperCase();
            }
        };
        wData.setFilters(new InputFilter[] { inputFilterText });
        wData.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogEnrollButton = (Button) dialog.findViewById(R.id.btn_enroll);
        dialogEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((EditText)dialog.findViewById(R.id.edit_cardid)).getText().toString().equals("")){
                    new BasicAlertDialog(getContext())
                            .setTitle("Empty Card ID")
                            .setMessage("Please enter Card ID")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();

                    return;
                }

                //userdata에 저장
                JSONObject jCardItem = new JSONObject();

                try {
                    if(!(((EditText)dialog.findViewById(R.id.edit_cardid)).getText().toString().equals("")))
                        jCardItem.put(MessageKeyValue.CARDINFO_CARDID,
                                //Long.valueOf(((EditText)dialog.findViewById(R.id.edit_cardid)).getText().toString()));
                                new BigDecimal(((EditText)dialog.findViewById(R.id.edit_cardid)).getText().toString()));
                    if(!(((EditText)dialog.findViewById(R.id.edit_fcode)).getText().toString().equals("")))
                        jCardItem.put(MessageKeyValue.CARDINFO_FCODE,
                                Long.valueOf(((EditText)dialog.findViewById(R.id.edit_fcode)).getText().toString()));
                    jCardItem.put(MessageKeyValue.CARDINFO_WDATA,
                            ((EditText)dialog.findViewById(R.id.edit_wdata)).getText().toString());

                    int position = ((Spinner)dialog.findViewById(R.id.totalbit_spin)).getSelectedItemPosition();

                    JSONObject jsonTempObject;
                    JSONObject jsonWFormat= null;
                    if(wFormatNameList.size()>0) {
                    for(int i=0; i<wFormatArray.length(); i++){
                        jsonTempObject = wFormatArray.getJSONObject(i);

                        if(jsonTempObject.optString(MessageKeyValue.WFORMAT_NAME).equals(wFormatNameList.get(position)))
                            jsonWFormat = jsonTempObject;
                        }
                    }

                    if(jsonWFormat!=null)
                        jCardItem.put(MessageKeyValue.CARDINFO_WFORMAT_ID, jsonWFormat.optString(MessageKeyValue.WFORMAT_ID));

                    if(_cardArray.length()==0)
                        _cardArray.put(jCardItem);
                    else
                        _cardArray.put(_cardArray.length()-1, jCardItem );

                    refreshCardlistView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception ee){
                    ee.printStackTrace();
                }
                dialog.dismiss();
                //카드 정보 입력
            }
        });
        dialog.show();
    }

    Dialog _dialogEnrollCard= null;
    private void onEnrollCard() {

        if(_dialogEnrollCard!=null &&_dialogEnrollCard.isShowing())
            return;

        _dialogEnrollCard = new Dialog(getActivity());
        _dialogEnrollCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogEnrollCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogEnrollCard.setCancelable(false);
        _dialogEnrollCard.setContentView(R.layout.dialog_card_scan_ready);

        Button dialogCancelButton = (Button) _dialogEnrollCard.findViewById(R.id.btn_cancel);
        ImageView imgEnroll = (ImageView) _dialogEnrollCard.findViewById(R.id.img_card_scan);
        Glide.with(getContext()).load(R.drawable.card_scan_gif).into(imgEnroll);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEnrollCard();
            }
        });

        Button dialogManualButton = (Button) _dialogEnrollCard.findViewById(R.id.btn_manual);
        dialogManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEnrollCard();
                onEnrollCardManual();
            }
        });
        _dialogEnrollCard.show();

        IT100.readCard(new CardInfoCallback() {
            @Override
            public void cardInfoResult(int resultCode, String jparams) {
                JSONObject jobj = null;
                try {
                    jobj = new JSONObject(jparams);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(resultCode == 0){
                    setCardRegisterInfo(jobj.optJSONObject("card").toString());
                }else{
                    Logger.d("cardinfoResult is fail "+resultCode);
                    String toastMsg;
                    if(resultCode == MessageType.ID_TIMEOUT){
                        toastMsg = safeGetResouces().getString(R.string.card_fail_timeout);
                    }else{
                        toastMsg = safeGetResouces().getString(R.string.dlgtitle_fail)+" : "
                                + jobj.optString(MessageType.Message);
                    }
                    showSafeToast(toastMsg);

                    if(_dialogEnrollCard!=null)
                        _dialogEnrollCard.dismiss();
                    _dialogEnrollCard = null;
                }
            }
        });
    }

    private void addEmptyCardItem(){
        try {
            JSONObject jLastItem = (JSONObject)_cardArray.get(_cardArray.length()-1);
            if(jLastItem.optBoolean("isEmpty", false)) {
                if(_dialogEnrollCard==null) onEnrollCard();
                return;
            }

            String emptyItem = "";
            JSONObject jEmptyCard = new JSONObject();
            JSONObject jTestCard01 = new JSONObject();
            JSONObject jTestCard02 = new JSONObject();

            jEmptyCard.put("isEmpty", true);
            jTestCard01.put(MessageKeyValue.CARDINFO_CARDID, 1008909);
            jTestCard01.put(MessageKeyValue.CARDINFO_FCODE, 300);

            jTestCard02.put(MessageKeyValue.CARDINFO_CARDID, 1008999);
            jTestCard02.put(MessageKeyValue.CARDINFO_FCODE, 300);

            _cardArray.put(jEmptyCard);
            refreshCardlistView();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String getRecogMode(){

        String selectedAuthMode;
        String selectedAuthCardMode;
        String selectedRecogMode = " ";

        selectedAuthCardMode = btn_card_mode.getText().toString();
        // All IRIS, FACE button selected.
        if(btn_auth_mode.isEnabled()){
            selectedAuthMode = btn_auth_mode.getText().toString();

            if(btn_card_mode.isEnabled()){
                if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_AND)) {
                    if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_AND_IRIS_AND_CARD;
                    else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_OR_IRIS_AND_CARD;
                    else
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS_AND_CARD;
                }else if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_OR)) {
                    if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_AND_IRIS_OR_CARD;
                    else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_OR_IRIS_OR_CARD;
                    else
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS_OR_CARD;
                }
            }else {
                if (selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                    selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_AND_IRIS;
                else if (selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                    selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_OR_IRIS;
                else
                    selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS;
            }

        }else{

            if(btn_card_mode.isEnabled()){
                if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_AND)) {
                    if (checkbox_iris.isChecked()) {
                        selectedRecogMode = MessageKeyValue.AUTHMODE_IRIS_AND_CARD;
                    } else if (checkbox_face.isChecked()) {
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_AND_CARD;
                    }
                }else if(selectedAuthCardMode.equals(ConstData.AUTH_MODE_OR)){
                    if (checkbox_iris.isChecked()) {
                        selectedRecogMode = MessageKeyValue.AUTHMODE_IRIS_OR_CARD;
                    } else if (checkbox_face.isChecked()) {
                        selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_OR_CARD;
                    }
                }
            }else{
                if (checkbox_iris.isChecked()) {
                    selectedRecogMode = MessageKeyValue.AUTHMODE_IRIS_ONLY;
                }else if(checkbox_face.isChecked()){
                    selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_ONLY;
                }else if(checkbox_card.isChecked()){
                    selectedRecogMode = MessageKeyValue.AUTHMODE_CARD_ONLY;
                } else{
                    if((userItem.getFace_img()== null) || userItem.getFace_img().length == 0) {
                        selectedRecogMode = " ";
                    }else {
                    }
                }
            }
        }
        return selectedRecogMode;
    }

    private void showCaptureChoiceItemDialog(String title){
        BasicAlertDialog builder = new BasicAlertDialog(getContext());
        builder.setTitle(title);
        //builder.setMessage(dialog_msg);
        // Add a checkbox list
        String[] biometrics = {"Face", "Iris"};
        boolean[] checkedItems = {true, true};
        final JSONObject itemChecked = new JSONObject();
        try {
            itemChecked.put("face", true);
            itemChecked.put("iris", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setMultiChoiceItems(biometrics, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId, boolean isChecked) {
                // The user checked or unchecked a box
                //Logger.d("multichoice item "+ selectedItemId+ isChecked);
                try {
                    if(selectedItemId==0){
                        itemChecked.put("face", isChecked);
                    } else if(selectedItemId==1) {
                        itemChecked.put("iris", isChecked);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!itemChecked.optBoolean("face") && !itemChecked.optBoolean("iris")) {
                    new BasicAlertDialog(getContext())
                            .setTitle(R.string.select_type)
                            .setMessage(R.string.select_type_msg)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();
                    return;
                }

                userItem.setUser_id(teUserId.getText().toString());
                userItem.setFirst_name(teFirstName.getText().toString());
                userItem.setLast_name(teLastName.getText().toString());
                userItem.setEmail_address(teEmail.getText().toString());
                userItem.setPhone_number(tePhoneNum.getText().toString());
                userItem.recogMode = getRecogMode();

                userItem.setCaptureFace(itemChecked.optBoolean("face"));
                userItem.setCaptureIris(itemChecked.optBoolean("iris"));

                userItem.setActive(true);
                userItem.userConsent = true;
                ItemsListActivity.userItem = userItem;

                if(enroll_guide ==1)
                    onRemoveGlassesForEnroll();
                else {
                	Intent intent;
                	intent = new Intent(getActivity(), CaptureActivity.class);
                	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                	intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_MODIFY_BIO);
                	intent.putExtra(MessageKeyValue.USER_GUID, userItem.guid);
                	startActivityForResult(intent, IrisApplication.CaptureActivityValue);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void showCaptureConfirmDialog(String title, String msg){
        BasicAlertDialog builder = new BasicAlertDialog(getContext());
        builder.setTitle(title);
        builder.setMessage(msg);
        // Add a checkbox list

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                userItem.setUser_id(teUserId.getText().toString());
                userItem.setFirst_name(teFirstName.getText().toString());
                userItem.setLast_name(teLastName.getText().toString());
                userItem.setEmail_address(teEmail.getText().toString());
                userItem.setPhone_number(tePhoneNum.getText().toString());
                userItem.recogMode = getRecogMode();

                userItem.setCaptureFace(true);
                userItem.setCaptureIris(true);
                userItem.setActive(true);
                userItem.setEmptyBio(true);
                userItem.userConsent = true;
                ItemsListActivity.userItem = userItem;

                if(enroll_guide ==1)
                    onRemoveGlassesForEnroll();
                else {
                	Intent intent;
                	intent = new Intent(getActivity(), CaptureActivity.class);
                	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                	intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_MODIFY_BIO);
                	intent.putExtra(MessageKeyValue.USER_GUID, userItem.guid);
                	startActivityForResult(intent, IrisApplication.CaptureActivityValue);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    Dialog _dialogPrivacy;
    CheckBox _ckFace;
    CheckBox _ckIris;
    private void showPrivacyDialog(final boolean enableCheck){

        _dialogPrivacy = new Dialog(getActivity(), R.style.myDialog);
        _dialogPrivacy.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogPrivacy.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        _dialogPrivacy.setContentView(R.layout.dialog_privacy_agree);
        _dialogPrivacy.setCancelable(false);

        _ckFace = _dialogPrivacy.findViewById(R.id.ch_face);
        _ckIris = _dialogPrivacy.findViewById(R.id.ch_iris);

        _ckFace.setEnabled(enableCheck);
        _ckIris.setEnabled(enableCheck);

        _dialogPrivacy.findViewById(R.id.txt_privacy_ok).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(_dialogPrivacy!=null)
                    _dialogPrivacy.dismiss();
                _dialogPrivacy = null;

                if(!_ckFace.isChecked() && !_ckIris.isChecked()) {
                    new BasicAlertDialog(getContext())
                            .setTitle(R.string.select_type)
                            .setMessage(R.string.select_type_msg)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();
                    return false;
                }

                userItem.setUser_id(teUserId.getText().toString());
                userItem.setFirst_name(teFirstName.getText().toString());
                userItem.setLast_name(teLastName.getText().toString());
                userItem.setEmail_address(teEmail.getText().toString());
                userItem.setPhone_number(tePhoneNum.getText().toString());
                userItem.recogMode = getRecogMode();

                userItem.setCaptureFace(_ckFace.isChecked());
                userItem.setCaptureIris(_ckIris.isChecked());

                //Logger.e("@@check check " + _ckFace.isChecked()+ _ckIris.isChecked());
                userItem.setActive(true);
                if(!enableCheck)
                    userItem.setEmptyBio(true);

                userItem.userConsent = true;
                ItemsListActivity.userItem = userItem;

                if(enroll_guide ==1)
                    onRemoveGlassesForEnroll();
                else {
                    Intent intent;
                    intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_MODIFY_BIO);
                    intent.putExtra(MessageKeyValue.USER_GUID, userItem.guid);
                    startActivityForResult(intent, IrisApplication.CaptureActivityValue);
                }

                return false;
            }
        });

        _dialogPrivacy.findViewById(R.id.txt_privacy_cancel).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(_dialogPrivacy!=null)
                    _dialogPrivacy.dismiss();
                _dialogPrivacy = null;

                return false;
            }
        });

        _dialogPrivacy.show();

    }

    private void playCaptureResult(int resid, int volume) {

        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), resid);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mp = null;
            }
        });
    }

    Dialog _dialogEnrollGuide;
    static CountDownTimer countTimer;
    private void onRemoveGlassesForEnroll() {
        if(voice_guide ==1)
            playCaptureResult(R.raw.remove_glasses_mask, -1);

        Logger.d("@@ onRemoveGlassesForEnroll");
        _dialogEnrollGuide = new Dialog(getActivity(), R.style.myDialog);

        _dialogEnrollGuide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogEnrollGuide.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        _dialogEnrollGuide.setContentView(R.layout.dialog_remove_glasses);
        _dialogEnrollGuide.setCancelable(false);

        _dialogEnrollGuide.findViewById(R.id.txt_guide_ok).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(countTimer!=null){
                    countTimer.cancel();
                    countTimer.onFinish();
                    countTimer = null;
                }
                return false;
            }
        });

        //Waiting for 4~5 seconds
        countTimer=  new CountDownTimer(4000, 1000){
            public void onTick(long millisUntilFinished){}

            public  void onFinish(){
                if(_dialogEnrollGuide!=null)
                    _dialogEnrollGuide.dismiss();
                _dialogEnrollGuide = null;

                Intent intent;
                    intent = new Intent(getActivity(), CaptureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_MODIFY_BIO);
                intent.putExtra(MessageKeyValue.USER_GUID, userItem.guid);
                startActivityForResult(intent, IrisApplication.CaptureActivityValue);
            }
        }.start();

        _dialogEnrollGuide.show();
    }

    private void showSafeToast(final String msg){
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new BasicToast(getActivity())
                            .makeText(msg).show();
                }
            });
        }
    }

    private Resources safeGetResouces(){
        return IrisApplication.getInstance().getResources();
    }
}