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
import com.irisid.it100.callback.EnrollUserCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.user.it100_sample.Activity.CaptureActivity;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.ui.BasicAlertDialog;
import com.irisid.user.it100_sample.Common.ui.BasicToast;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFacilityCode;
import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFormatArray;
import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFormatHashmap;
import static com.irisid.user.it100_sample.UserList.ItemsListActivity.wFormatNameList;
import static com.irisid.user.it100_sample.UserList.ItemsListFragment.itemArrayList;

public class ItemEmptyUserFragmentWithCard extends Fragment implements View.OnClickListener {

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
    LinearLayout linearGuideCapture;
    LinearLayout linearFaceError;
    LinearLayout linearLeyeError;
    LinearLayout linearReyeError;
    TextView txtLeyeError;
    TextView txtReyeError;

    public interface OnItemUpdateListener {
        void onUserListUpdate(String type);
        void onDeleteNewUser();
    }
    private OnItemUpdateListener listener;

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
        final View view = inflater.inflate(R.layout.fragment_item_detail_with_card, container, false);

        rootLayout = (LinearLayout)view.findViewById(R.id.rootview);
        nestedScrollView=  (NestedScrollView) view.findViewById(R.id.child_scroll);
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
        save_btn = (Button) view.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);
        idpw_btn = (Button) view.findViewById(R.id.idpw_btn);
        idpw_btn.setOnClickListener(this);
        delete_btn = (Button) view.findViewById(R.id.delete_btn);
        delete_btn.setVisibility(View.GONE);
        update_btn = (Button) view.findViewById(R.id.update_btn);
        update_btn.setVisibility(View.GONE);

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
        return view;
    }

    private void setCardRegisterInfo(final String jparamCardinfo) {

        if(_dialogEnrollCard!=null)
            _dialogEnrollCard.dismiss();
        _dialogEnrollCard= null;

        if(jparamCardinfo==null)
            return;

        if(_cardArray!=null) {
            try {
                if(_cardArray.length()==0)
                    _cardArray.put(0, new JSONObject(jparamCardinfo));
                else
                    _cardArray.put(_cardArray.length()-1, new JSONObject(jparamCardinfo));
                refreshCardlistView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemUpdateListener) {
            listener = (OnItemUpdateListener) context;
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

                //			 if(!userItem.captureRIris && !userItem.captureLIris)
                //            checkbox_iris.setEnabled(false);
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

                //			if(!userItem.captureFace)
                //				checkbox_face.setEnabled(false);
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

        teUserId.setText(userItem.getUserId()==null? "":userItem.getUserId());
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


        teFirstName.setText((userItem.getFirst_name()==null || userItem.getFirst_name().equals("New User"))?
                "":userItem.getFirst_name());

        teLastName.setText(userItem.getLast_name()==null? "":userItem.getLast_name());
        teEmail.setText(userItem.getEmail_address()==null? "":userItem.getEmail_address());
        tePhoneNum.setText(userItem.getPhone_number()==null? "":userItem.getPhone_number());

        linear_warning.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        save_btn.setVisibility(View.GONE);
        idpw_btn.setVisibility(View.GONE);

        if(showType == ConstData.USERDETAIL_SHOW_ENROLL){
            checkUserActive.setChecked(true);
            cancel_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);

            if(result_code!=0){
                linear_warning.setVisibility(View.VISIBLE);
                switch(result_code){
                    case MessageType.ID_IRIS_CAPTURE_FAIL: // FaceCaptureFailed
                        errMsg = getString(R.string.enroll_error_capture);
                        break;
                    case MessageType.ID_FACE_CAPTURE_FAIL :
                        errMsg = getString(R.string.enroll_error_capture_face);
                        break;
                    case MessageType.ID_VERIFICATION_FAILED :
                        errMsg = getString(R.string.enroll_error_verified);
                        break;
                    case MessageType.ID_DUPLICATED_USER_FOUND :
                        errMsg = getString(R.string.enroll_error_already);
                        break;
                    case MessageType.ID_OCCLUDED_EYES: //OccludedEyesError
                        errMsg = getString(R.string.error_occluded_eyes);
                        break;
                    case MessageType.ID_FAKE_EYE_DETECTED:
                        errMsg = getString(R.string.capture_fail_fake_eye);
                        break;
                    case MessageType.ID_FAKE_EYE_DETECTED_FAILED:
                        errMsg = getString(R.string.liveness_eye_detect_fail);
                        break;
                    default:
                        errMsg = getString(R.string.enroll_error_verified);
                        break;
                }
                txt_warning.setText(errMsg!=null? errMsg : "");
            }else{
                linear_warning.setVisibility(View.GONE);
                if(userItem.jsonCaptureStatus!=null && !userItem.jsonCaptureStatus.isEmpty()) {
                    linearGuideCapture.setVisibility(View.GONE);
                    try {
                        JSONObject jCaptureStatus = new JSONObject(userItem.jsonCaptureStatus);
                        captureStatusFace = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_FACE);
                        captureStatusLEye = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_LEFT_EYE);
                        captureStatusREye = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_RIGHT_EYE);
                        if(captureStatusFace==MessageKeyValue.CAPTURE_BIO_RESULT_CAPTURE_FAIL)
                            linearFaceError.setVisibility(View.VISIBLE);
                        if(captureStatusLEye!=MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS) {
                            linearLeyeError.setVisibility(View.VISIBLE);
                            if(captureStatusLEye== MessageKeyValue.CAPTURE_BIO_RESULT_CAPTURE_FAIL)
                                txtLeyeError.setText(safeGetResouces().getString(R.string.capture_fail));
                            else if(captureStatusLEye == MessageKeyValue.CAPTURE_BIO_RESULT_LIVENESS_DETECTION_FAIL)
                                txtLeyeError.setText(safeGetResouces().getString(R.string.liveness_eye_detect_fail));
                        }
                        if(captureStatusREye!=MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS) {
                            linearReyeError.setVisibility(View.VISIBLE);
                            if(captureStatusREye== MessageKeyValue.CAPTURE_BIO_RESULT_CAPTURE_FAIL)
                                txtReyeError.setText(safeGetResouces().getString(R.string.capture_fail));
                            else if(captureStatusREye == MessageKeyValue.CAPTURE_BIO_RESULT_LIVENESS_DETECTION_FAIL)
                                txtReyeError.setText(safeGetResouces().getString(R.string.liveness_eye_detect_fail));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    linearGuideCapture.setVisibility(View.VISIBLE);
                }
            }
        }
        else if(showType == ConstData.USERDETAIL_SHOW_ERROR){
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

                if(showType == ConstData.USERDETAIL_SHOW_ERROR_DETAIL
                        || showType == ConstData.USERDETAIL_SHOW_ERROR)
                    return false;

                String dialog_title = "";
                String dialog_msg = "";

                //userItem
               // Logger.d("userItem is "+ userItem.toString());
                dialog_title= safeGetResouces().getString(R.string.add_bioinfo_dialog_title);
                dialog_msg = safeGetResouces().getString(R.string.add_bioinfo_dialog);
                //showCaptureChoiceItemDialog(dialog_title);
                //showCaptureConfirmDialog(dialog_title, dialog_msg);
                showPrivacyDialog(dialog_title, dialog_msg);

                return false;
            }
        });
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

    public static ItemEmptyUserFragmentWithCard newInstance(Item item, int showType) {

        ItemEmptyUserFragmentWithCard fragmentDemo = new ItemEmptyUserFragmentWithCard();
        Bundle args = new Bundle();
        args.putSerializable(ConstData.USER_BUNDLE_USER_ITEM, item);
        args.putInt(ConstData.USER_BUNDLE_SHOW_TYPE, showType);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
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
                //userItem.setIs_admin(MessageKeyValue.USER_ROLE_USER);
                userItem.setRecogMode(_recogMode_value);
                userItem.setActive(checkUserActive.isChecked());
                userItem.setCardInfo(_cardArray);
//                try {
//                    Logger.d("cardArray is "+ _cardArray.toString(4));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                if(captureStatusFace!=0){
                    userItem.setFace_code("");
                    userItem.setFace_img(null);
                }

                if(captureStatusLEye!=0){
                    userItem.setLeft_iris_code("");
                    userItem.setLeft_iris_img(null);
                }

                if(captureStatusREye!=0){
                    userItem.setRight_iris_code("");
                    userItem.setRight_iris_img(null);
                }

                if(checkUserActive.isChecked())
                    userItem.status = MessageKeyValue.USER_STATUS_ACTIVE;
                else
                    userItem.status = MessageKeyValue.USER_STATUS_INACTIVE;

                long rtnEnroll = 0 ;
                rtnEnroll = IT100.enrollUser(userItem, new EnrollUserCallback()
                {
                    @Override
                    public void enrollUserResult(int resultCode, String jsonParam)
                    {
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
                                if(voice_guide==1)
                                    playCaptureResult(R.raw.success_enroll, -1);
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


            case R.id.cancel_btn:
                if (listener != null) {
                    listener.onDeleteNewUser();
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

        if(result_code!=0){
            new BasicAlertDialog(getContext())
                    .setTitle("Biometrics Error")
                    .setMessage("Please capture biometrics again")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    })
                    .show();

            return false;
        }

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
                   // }
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
        //Logger.d("setCardInfo  cards is "+ card_info);
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
                        ((TextInputLayout) itemLayout.findViewById(R.id.tl_1st)).setHint(getResources().getString(R.string.card_id));
                    }else {
                        if(cardItem.has(MessageKeyValue.CARDINFO_WDATA)) {
                            ((TextInputEditText) itemLayout.findViewById(R.id.te_1st)).setText(cardItem.optString(MessageKeyValue.CARDINFO_WDATA, ""));
                            ((TextInputLayout) itemLayout.findViewById(R.id.tl_1st)).setHint(getResources().getString(R.string.card_wiegand_data));
                        }
                        ((TextInputLayout) itemLayout.findViewById(R.id.tl_2st)).setVisibility(View.GONE);
                    }
                    if(cardItem.has(MessageKeyValue.CARDINFO_FCODE)) {
                        ((TextInputEditText) itemLayout.findViewById(R.id.te_2st)).setText(cardItem.optString(MessageKeyValue.CARDINFO_FCODE, ""));
                        ((TextInputLayout) itemLayout.findViewById(R.id.tl_2st)).setHint(getResources().getString(R.string.card_facility_code));
                    }

                    ((LinearLayout) itemLayout.findViewById(R.id.linear_touch_region)).setLabelFor(n);
                    ((LinearLayout) itemLayout.findViewById(R.id.linear_touch_region)).setOnClickListener(new View.OnClickListener() {
                        //itemLayout.setLabelFor(n);
                        //itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(result_code==0) {
                                try {
                                    JSONObject cardobj = (JSONObject) _cardArray.get(v.getLabelFor());
                                    if (!cardobj.optBoolean("isEmpty", false)) {
                                        onDetailCard(cardobj, v.getLabelFor());
                                    } else {
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
                   // Logger.d("cardItem is " + cardItem.toString(4));
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

        // _jsonCardItem = _cardArray.getJSONObject(position);
        JSONObject jCardFormat = wFormatHashmap.get(cardObj.optString(MessageKeyValue.CARDINFO_WFORMAT_ID));
        if(jCardFormat!=null) {
            ((EditText) dialog.findViewById(R.id.edit_totalbit)).setText(
                    //jCardFormat.optString(MessageKeyValue.WFORMAT_TOTALBIT) + " | " +
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

        if(wFormatArray==null || wFormatArray.length()==0){
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

                JSONObject jCardItem = new JSONObject();

                try {
                    if(!(((EditText)dialog.findViewById(R.id.edit_cardid)).getText().toString().equals("")))
                        jCardItem.put(MessageKeyValue.CARDINFO_CARDID,
                                //Integer.valueOf(((EditText)dialog.findViewById(R.id.edit_cardid)).getText().toString()));
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
                    //Logger.d("cardinfoResult is fail "+resultCode);
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

            JSONObject jEmptyCard = new JSONObject();
            jEmptyCard.put("isEmpty", true);

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

   private void showCaptureConfirmDialog(String title, String msg){
        BasicAlertDialog builder = new BasicAlertDialog(getContext());
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
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
                try {
                    for(int i=0; i<_cardArray.length(); i++){
                        JSONObject jobj= _cardArray.getJSONObject(i);
                        if(jobj.optBoolean("isEmpty", false))
                            _cardArray.remove(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userItem.setCardInfo(_cardArray);
                userItem.userConsent = true;
                ItemsListActivity.userItem = userItem;

                if(enroll_guide ==1)
                    onRemoveGlassesForEnroll();
                else {
                	Intent intent = new Intent(getActivity(), CaptureActivity.class);
                	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                	intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_NEW_USER);
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
    private void showPrivacyDialog(String title, String msg){

        _dialogPrivacy = new Dialog(getActivity(), R.style.myDialog);
        _dialogPrivacy.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogPrivacy.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        _dialogPrivacy.setContentView(R.layout.dialog_privacy_agree);
        _dialogPrivacy.setCancelable(false);

        _dialogPrivacy.findViewById(R.id.txt_privacy_ok).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(_dialogPrivacy!=null)
                    _dialogPrivacy.dismiss();
                _dialogPrivacy = null;

                userItem.setUser_id(teUserId.getText().toString());
                userItem.setFirst_name(teFirstName.getText().toString());
                userItem.setLast_name(teLastName.getText().toString());
                userItem.setEmail_address(teEmail.getText().toString());
                userItem.setPhone_number(tePhoneNum.getText().toString());
                userItem.recogMode = getRecogMode();

                userItem.setCaptureFace(true);
                userItem.setCaptureIris(true);
                userItem.setActive(true);
                try {
                    for(int i=0; i<_cardArray.length(); i++){
                        JSONObject jobj= _cardArray.getJSONObject(i);
                        if(jobj.optBoolean("isEmpty", false))
                            _cardArray.remove(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userItem.setCardInfo(_cardArray);
                userItem.userConsent = true;
                ItemsListActivity.userItem = userItem;

                if(enroll_guide ==1)
                    onRemoveGlassesForEnroll();
                else {
                    Intent intent;
                    intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_NEW_USER);
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
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){
                if(_dialogEnrollGuide!=null)
                    _dialogEnrollGuide.dismiss();
                _dialogEnrollGuide = null;

                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_NEW_USER);
                intent.putExtra(MessageKeyValue.USER_GUID, userItem.guid);
                startActivityForResult(intent, IrisApplication.CaptureActivityValue);
            }
        }.start();

        _dialogEnrollGuide.show();
    }

}