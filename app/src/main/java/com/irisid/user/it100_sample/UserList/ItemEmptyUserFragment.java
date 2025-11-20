package com.irisid.user.it100_sample.UserList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irisid.it100.IT100;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.irisid.user.it100_sample.UserList.ItemsListFragment.itemArrayList;

public class ItemEmptyUserFragment extends Fragment implements View.OnClickListener {

    public Item userItem;
    int showType = ConstData.USERDETAIL_SHOW_ENROLL;
    int result_code = 0;
    String errMsg = "";
    String preUserid;

    LinearLayout rootLayout;
    LinearLayout linear_warning;
    LinearLayout linear_dim;
    TextView txt_warning;

    Button btn_auth_mode;
    CheckBox checkbox_iris;
    CheckBox checkbox_face;

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

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_detail_new, container, false);

        rootLayout = (LinearLayout)view.findViewById(R.id.rootview);
        linear_warning = (LinearLayout) view.findViewById(R.id.linear_warning);
        txt_warning = (TextView) view.findViewById(R.id.txt_warning);
        linear_dim = (LinearLayout) view.findViewById(R.id.linear_dim);

        btn_auth_mode = (Button) view.findViewById(R.id.btn_auth_mode);
        btn_auth_mode.setOnClickListener(this);
        checkbox_iris = (CheckBox) view.findViewById(R.id.checkbox_iris);
        checkbox_face = (CheckBox)view.findViewById(R.id.checkbox_face);

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
    }

    int captureStatusFace = 0;
    int captureStatusLEye = 0;
    int captureStatusREye = 0;

    private void setInitView(){

        if(userItem.getRecogMode() == null){

            checkbox_iris.setChecked(false);
            checkbox_face.setChecked(false);
            btn_auth_mode.setEnabled(false);

            //in case of pending user, checkbox disable?
            if(userItem.getFace_img()==null || userItem.getFace_img().length==0){
                checkbox_face.setEnabled(false);
                checkbox_iris.setEnabled(false);
            }

        } else if(userItem.getRecogMode().equals(MessageKeyValue.AUTHMODE_IRIS_ONLY)){
            checkbox_iris.setChecked(true);
            checkbox_face.setChecked(false);
            btn_auth_mode.setEnabled(false);

        }else if (userItem.getRecogMode().equals(MessageKeyValue.AUTHMODE_FACE_ONLY)){
            checkbox_iris.setChecked(false);
            checkbox_face.setChecked(true);
            btn_auth_mode.setEnabled(false);

        }else{
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

        checkbox_iris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    btn_auth_mode.setEnabled(false);
                else{
                    if(checkbox_face.isChecked())
                        btn_auth_mode.setEnabled(true);
                }
            }
        });

        checkbox_face.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    btn_auth_mode.setEnabled(false);
                else{
                    if(checkbox_iris.isChecked())
                        btn_auth_mode.setEnabled(true);
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
            checkUserActive.setText(getResources().getString(R.string.user_active));
            linear_dim.setVisibility(View.GONE);
        }else{
            checkUserActive.setText(getResources().getString(R.string.user_inactive));
            linear_dim.setVisibility(View.VISIBLE);
        }
        checkUserActive.setChecked(userItem.active);
        checkUserActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    checkUserActive.setText(getResources().getString(R.string.user_inactive));
                    linear_dim.setVisibility(View.VISIBLE);
                }else{
                    checkUserActive.setText(getResources().getString(R.string.user_active));
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
                    tlUserId.setHelperText(getResources().getString(R.string.required));
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
                    case MessageType.ID_SINGLE_EYE_CAPTURED:
                        result_code =0;  //todo.. temp
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
                                txtLeyeError.setText(getResources().getString(R.string.capture_fail));
                            else if(captureStatusLEye == MessageKeyValue.CAPTURE_BIO_RESULT_LIVENESS_DETECTION_FAIL)
                                txtLeyeError.setText(getResources().getString(R.string.liveness_eye_detect_fail));
                        }

                        if(captureStatusREye!=MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS) {
                            linearReyeError.setVisibility(View.VISIBLE);
                            if(captureStatusREye== MessageKeyValue.CAPTURE_BIO_RESULT_CAPTURE_FAIL)
                                txtReyeError.setText(getResources().getString(R.string.capture_fail));
                            else if(captureStatusREye == MessageKeyValue.CAPTURE_BIO_RESULT_LIVENESS_DETECTION_FAIL)
                                txtReyeError.setText(getResources().getString(R.string.liveness_eye_detect_fail));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    linearGuideCapture.setVisibility(View.VISIBLE);
                }

            }
        }
//        else if(showType == ItemsListActivity.USER_DETAIL_MODE){
//            checkUserActive.setChecked(userItem.active);
//            if(!userItem.getIs_admin().equals(MessageType.Administrator))
//                delete_btn.setVisibility(View.VISIBLE);
//            else
//                idpw_btn.setVisibility(View.VISIBLE);
//
//            update_btn.setVisibility(View.VISIBLE);
//            cancel_btn.setVisibility(View.VISIBLE);
//
//        }
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

                if(showType==ConstData.USERDETAIL_SHOW_ERROR_DETAIL || showType==ConstData.USERDETAIL_SHOW_ERROR)
                    return false;

                String dialog_title = "";
                String dialog_msg = "";

                Logger.d("userItem is "+ userItem.toString());
                dialog_title= getResources().getString(R.string.add_bioinfo_dialog_title);
                dialog_msg = getResources().getString(R.string.add_bioinfo_dialog);
                //showCaptureChoiceItemDialog(dialog_title);
                showCaptureConfirmDialog(dialog_title, dialog_msg);
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

    public static ItemEmptyUserFragment newInstance(Item item, int showType) {

        ItemEmptyUserFragment fragmentDemo = new ItemEmptyUserFragment();
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

                userItem.setUser_id(teUserId.getText().toString());
                userItem.setFirst_name(teFirstName.getText().toString());
                userItem.setLast_name((teLastName.getText().toString()));
                userItem.setEmail_address(teEmail.getText().toString());
                userItem.setPhone_number(tePhoneNum.getText().toString());
                userItem.setIs_admin(MessageKeyValue.USER_ROLE_USER);
                userItem.setRecogMode(_recogMode_value);
                userItem.setActive(checkUserActive.isChecked());

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

                                new BasicToast(getContext())
                                        .makeText(getResources().getString(R.string.dlgtitle_success)).show();
                                break;

                            case MessageType.ID_DUPLICATE_USER_ID :  // duplicated user ID
                                tlUserId.setErrorEnabled(true);
                                tlUserId.setError(getResources().getString(R.string.enroll_error_duplicate_userid));
                                break;

                            default:
                                new BasicToast(getContext())
                                        .makeText(getResources().getString(R.string.dlgtitle_fail)+" : "
                                                + jsonObject.optString(MessageType.Message)).show();
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
                //new user 삭제
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
                                        new BasicToast(getContext())
                                                .makeText(getResources().getString(R.string.err_pw_invalid)).show();
                                    }else if(i== MessageType.ID_SUCCESS){
                                        _pwCurrent = "";
                                        _pwNew = "";
                                        _pwConfirm = "";
                                        new BasicToast(getContext())
                                                .makeText(getResources().getString(R.string.pw_changed)).show();
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

    String _recogMode_value =MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS;
    private boolean validUserInfo() {
        String selectedAuthMode;
        // All IRIS, FACE button selected.

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
            new BasicAlertDialog(getContext())
                    .setTitle("Biometrics empty Error")
                    .setMessage("Please capture biometrics")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    })
                    .show();
            return false;
        }

        if(btn_auth_mode.isEnabled()){
            selectedAuthMode = btn_auth_mode.getText().toString();

            if(selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS;
            else if(selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                _recogMode_value = MessageKeyValue.AUTHMODE_FACE_OR_IRIS;
            else
                _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS;

        }else{
            if (checkbox_iris.isChecked()) {
                _recogMode_value = MessageKeyValue.AUTHMODE_IRIS_ONLY;
            }else if(checkbox_face.isChecked()){
                _recogMode_value = MessageKeyValue.AUTHMODE_FACE_ONLY;
            }else{
                if((userItem.getFace_img()== null) || userItem.getFace_img().length == 0) {
                    _recogMode_value = " ";
                }else {

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
                }
            }
        }

        if(teUserId.getText().toString().equals("")){
            tlUserId.setErrorEnabled(true);
            tlUserId.setError(getResources().getString(R.string.enroll_error_empty_userid));
            return false;
        }

        return true;
    }

    private String getRecogMode(){
        String selectedAuthMode;
        String selectedRecogMode = " ";
        if(btn_auth_mode.isEnabled()){
            selectedAuthMode = btn_auth_mode.getText().toString();

            if(selectedAuthMode.equals(ConstData.AUTH_MODE_AND))
                selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_AND_IRIS;
            else if(selectedAuthMode.equals(ConstData.AUTH_MODE_OR))
                selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_OR_IRIS;
            else
                selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS;

        }else{
            if (checkbox_iris.isChecked()) {
                selectedRecogMode = MessageKeyValue.AUTHMODE_IRIS_ONLY;
            }else if(checkbox_face.isChecked()){
                selectedRecogMode = MessageKeyValue.AUTHMODE_FACE_ONLY;
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
                ItemsListActivity.userItem = userItem;

                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_NEW_USER);
                intent.putExtra(MessageKeyValue.USER_GUID, userItem.guid);
                startActivityForResult(intent, IrisApplication.CaptureActivityValue);
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

}