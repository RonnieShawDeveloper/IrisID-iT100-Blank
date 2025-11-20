package com.irisid.user.it100_sample.UserList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.irisid.user.it100_sample.Common.ui.ClearableEditText;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.irisid.user.it100_sample.UserList.ItemsListFragment.itemArrayList;

public class ItemDetailFragment extends Fragment implements View.OnClickListener {

    public Item userItem;
    int showType = ConstData.USERDETAIL_SHOW_ENROLL;
    String errMsg = "";
    String preUserid;

    LinearLayout rootLayout;
    LinearLayout linear_warning;
    LinearLayout linear_error;
    TextView txt_warning;
    TextView txt_error;

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

    ClearableEditText userid_edt;
    ClearableEditText first_name_edt;
    ClearableEditText last_name_edt;
    ClearableEditText email_edt;
    ClearableEditText phone_edt;

    public interface OnItemUpdateListener {
        void onUserListUpdate(String type);
    }
    private ItemDetailFragment.OnItemUpdateListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            userItem = (Item) bundle.getSerializable(ConstData.USER_BUNDLE_USER_ITEM);
            showType = bundle.getInt(ConstData.USER_BUNDLE_SHOW_TYPE, ConstData.USERDETAIL_SHOW_ENROLL);
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
        final View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        rootLayout = (LinearLayout)view.findViewById(R.id.rootview);
        linear_warning = (LinearLayout) view.findViewById(R.id.linear_warning);
        txt_warning = (TextView) view.findViewById(R.id.txt_warning);
        linear_error = (LinearLayout) view.findViewById(R.id.linear_error);
        txt_error = (TextView) view.findViewById(R.id.txt_error);

        btn_auth_mode = (Button) view.findViewById(R.id.btn_auth_mode);
        btn_auth_mode.setOnClickListener(this);
        checkbox_iris = (CheckBox) view.findViewById(R.id.checkbox_iris);
        checkbox_face = (CheckBox)view.findViewById(R.id.checkbox_face);

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

        userid_edt = (ClearableEditText) view.findViewById(R.id.id_edt);
        first_name_edt = (ClearableEditText) view.findViewById(R.id.first_name_edt);
        last_name_edt = (ClearableEditText) view.findViewById(R.id.last_name_edt);
        email_edt = (ClearableEditText) view.findViewById(R.id.email_edt);
        phone_edt = (ClearableEditText) view.findViewById(R.id.phone_edt);

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
        if (context instanceof ItemDetailFragment.OnItemUpdateListener) {
            listener = (ItemDetailFragment.OnItemUpdateListener) context;
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

    private void setInitView(){
        userid_edt.setHint("(*) "+ userid_edt.getHint());

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
            else
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

        if(userItem.getFace_img() !=null) {
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(userItem.getFace_img(), 0, userItem.getFace_img().length);
            face_img.setImageBitmap(faceBitmap);
        }

        if(userItem.getRight_iris_img() !=null) {
            Bitmap right_iris_Bitmap = BitmapFactory.decodeByteArray(userItem.getRight_iris_img(), 0, userItem.getRight_iris_img().length);
            right_iris_img.setImageBitmap(right_iris_Bitmap);
        }

        if(userItem.getLeft_iris_img() !=null) {
            Bitmap left_iris_Bitmap = BitmapFactory.decodeByteArray(userItem.getLeft_iris_img(), 0, userItem.getLeft_iris_img().length);
            left_iris_img.setImageBitmap(left_iris_Bitmap);
        }

        userid_edt.setText(userItem.getUserId()==null? "":userItem.getUserId());
        first_name_edt.setText(userItem.getFirst_name()==null? "":userItem.getFirst_name());
        last_name_edt.setText(userItem.getLast_name()==null? "":userItem.getLast_name());
        email_edt.setText(userItem.getEmail_address()==null? "":userItem.getEmail_address());
        phone_edt.setText(userItem.getPhone_number()==null? "":userItem.getPhone_number());

        linear_warning.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        save_btn.setVisibility(View.GONE);
        delete_btn.setVisibility(View.GONE);
        update_btn.setVisibility(View.GONE);
        idpw_btn.setVisibility(View.GONE);

        if(showType == ConstData.USERDETAIL_SHOW_ENROLL){
            cancel_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);

        }else if(showType == ConstData.USERDETAIL_SHOW_DETAIL){
            if(!userItem.getIs_admin().equals(MessageKeyValue.USER_ROLE_ADMINISTRATOR))
                delete_btn.setVisibility(View.VISIBLE);
            else
                idpw_btn.setVisibility(View.VISIBLE);

            update_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);

        }else if(showType == ConstData.USERDETAIL_SHOW_ERROR){
            cancel_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);

            userid_edt.setText("");
            first_name_edt.setText("");
            last_name_edt.setText("");
            email_edt.setText("");
            phone_edt.setText("");

            save_btn.setEnabled(false);
            linear_warning.setVisibility(View.VISIBLE);
            txt_warning.setText(errMsg!=null? errMsg : "");

        }else if(showType == ConstData.USERDETAIL_SHOW_ERROR_DETAIL){ //for userbio modify
            cancel_btn.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);
            save_btn.setEnabled(false);

            linear_warning.setVisibility(View.VISIBLE);
            txt_warning.setText(errMsg!=null? errMsg : "");
        }

        userid_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                linear_error.setVisibility(View.GONE);
                userid_edt.getBackground().mutate().setColorFilter(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userid_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    first_name_edt.setCompoundDrawables(null, null, null, null);
                    last_name_edt.setCompoundDrawables(null, null, null, null);
                    email_edt.setCompoundDrawables(null, null, null, null);
                    phone_edt.setCompoundDrawables(null, null, null, null);
                }
            }
        });

        first_name_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userid_edt.setCompoundDrawables(null, null, null, null);
                    last_name_edt.setCompoundDrawables(null, null, null, null);
                    email_edt.setCompoundDrawables(null, null, null, null);
                    phone_edt.setCompoundDrawables(null, null, null, null);
                }
            }
        });


        last_name_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userid_edt.setCompoundDrawables(null, null, null, null);
                    first_name_edt.setCompoundDrawables(null, null, null, null);
                    email_edt.setCompoundDrawables(null, null, null, null);
                    phone_edt.setCompoundDrawables(null, null, null, null);
                }
            }
        });


        email_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userid_edt.setCompoundDrawables(null, null, null, null);
                    first_name_edt.setCompoundDrawables(null, null, null, null);
                    last_name_edt.setCompoundDrawables(null, null, null, null);
                    phone_edt.setCompoundDrawables(null, null, null, null);
                }
            }
        });

        phone_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    first_name_edt.setCompoundDrawables(null, null, null, null);
                    last_name_edt.setCompoundDrawables(null, null, null, null);
                    email_edt.setCompoundDrawables(null, null, null, null);
                    userid_edt.setCompoundDrawables(null, null, null, null);
                }
            }
        });

        face_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(showType == ConstData.USERDETAIL_SHOW_ERROR_DETAIL || showType == ConstData.USERDETAIL_SHOW_ERROR)
                    return false;

                String dialog_msg = "";
                String dialog_title = "";

                //userItem
                if(userItem.active || (userItem.getFace_img().length >0) ) {
                    dialog_title= getResources().getString(R.string.modify_bioinfo_dialog_title);
                    dialog_msg = getResources().getString(R.string.modify_bioinfo_dialog);
                }else {
                    dialog_title= getResources().getString(R.string.add_bioinfo_dialog_title);
                    dialog_msg = getResources().getString(R.string.add_bioinfo_dialog);
                }

                BasicAlertDialog builder = new BasicAlertDialog(getContext());
                builder.setTitle(dialog_title);
                builder.setMessage(dialog_msg);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        userItem.setUser_id(userid_edt.getText().toString());

                        userItem.setFirst_name(first_name_edt.getText().toString());
                        userItem.setLast_name(last_name_edt.getText().toString());
                        userItem.setEmail_address(email_edt.getText().toString());
                        userItem.setPhone_number(phone_edt.getText().toString());
                        userItem.recogMode = _recogMode_value;
                        userItem.role = _recogMode_value;
                        userItem.setActive(userItem.active  || (userItem.getFace_img().length >0));
                        ItemsListActivity.userItem = userItem;


                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_MODIFY_BIO);
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

    public static ItemDetailFragment newInstance(Item item, int showType) {

        ItemDetailFragment fragmentDemo = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        args.putInt("type", showType);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.save_btn:
                if(!validUserInfo())
                    return;

                userItem.setUser_id(userid_edt.getText().toString());
                userItem.setFirst_name(first_name_edt.getText().toString());
                userItem.setLast_name((last_name_edt.getText().toString()));
                userItem.setEmail_address(email_edt.getText().toString());
                userItem.setPhone_number(phone_edt.getText().toString());
                userItem.setIs_admin(MessageKeyValue.USER_ROLE_USER);
                userItem.setRecogMode(_recogMode_value);

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
                                linear_error.setVisibility(View.VISIBLE);
                                txt_error.setText(getResources().getString(R.string.enroll_error_duplicate_userid));
                                userid_edt.getBackground().mutate().setColorFilter(
                                        getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
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

                userItem.setUser_id(userid_edt.getText().toString());
                userItem.setFirst_name(first_name_edt.getText().toString());
                userItem.setLast_name((last_name_edt.getText().toString()));
                userItem.setEmail_address(email_edt.getText().toString());
                userItem.setPhone_number(phone_edt.getText().toString());
                userItem.setRecogMode(_recogMode_value);

                //if(userItem.face_img!=null && userItem.face_img.length > 0 && !userItem.getActive())
                if(userItem.faceImage!=null && userItem.faceImage.length > 0 )
                    userItem.setActive(true);

                long rtnUpdate = 0 ;
                rtnUpdate = IT100.updateUser(userItem, new UpdateUserCallback() {

                    @Override
                    public void updateUserResult(int resultCode, String jsonParam) {
                        if(resultCode==0) {
                            //modify(preUserid, userItem);
                            if (listener != null) {
                                listener.onUserListUpdate(ConstData.USERLIST_RELOAD);
                            }
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.flDetailContainer, new ItemEmptyFragment())
                                    .commit();
                            new BasicToast(getContext())
                                    .makeText(getResources().getString(R.string.dlgtitle_success)).show();
                        }else if(resultCode == MessageType.ID_DUPLICATE_USER_ID){
                            linear_error.setVisibility(View.VISIBLE);
                            txt_error.setText(getResources().getString(R.string.enroll_error_duplicate_userid));
                            userid_edt.getBackground().mutate().setColorFilter(
                                    getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);
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

    String _recogMode_value = MessageKeyValue.AUTHMODE_FACE_FUSION_IRIS;
    private boolean validUserInfo() {
        String selectedAuthMode;
        // All IRIS, FACE button selected.
        if(btn_auth_mode.isEnabled()){
            selectedAuthMode = btn_auth_mode.getText().toString();

            if(selectedAuthMode.equals("AND"))
                _recogMode_value = MessageKeyValue.AUTHMODE_FACE_AND_IRIS;
            else if(selectedAuthMode.equals("OR"))
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

        if(userid_edt.getText().toString().equals("")){
            linear_error.setVisibility(View.VISIBLE);
            txt_error.setText(getResources().getString(R.string.enroll_error_empty_userid));
            userid_edt.getBackground().mutate().setColorFilter(
                    getResources().getColor(R.color.errorText), PorterDuff.Mode.SRC_ATOP);

            return false;
        }

        return true;

    }
}