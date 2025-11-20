package com.irisid.user.it100_sample.UserList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.DeviceWiegandFormatsCallback;
import com.irisid.it100.callback.GlobalWiegandFormatsCallback;
import com.irisid.it100.callback.UserInfoCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.it100.data.UserInfo;
import com.irisid.it100.listener.UserListChangeListener;
import com.irisid.user.it100_sample.Common.ConstData;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//public class ItemsListActivity extends AppCompatActivity implements
public class ItemsListActivity extends FragmentActivity implements
        ItemsListFragment.OnItemSelectedListener,
        //ItemDetailFragment.OnItemUpdateListener,
        ItemDetailFragmentNew.OnItemUpdateListener,
        ItemEmptyUserFragment.OnItemUpdateListener,
        ItemEmptyUserFragmentWithCard.OnItemUpdateListener,
        ItemDetailFragmentWithCard.OnItemUpdateListener,
        ItemDetailFragmentPolicy.OnItemUpdateListener,
        View.OnClickListener {

    private FragmentManager fragmentManager;

    Button enroll_fab;

    ItemsListFragment itemsListFragment;
    FrameLayout fragmentItemDetail;
   // ItemDetailFragment fragmentItem;
    ItemDetailFragmentNew fragmentItem;
    // Management as static

    public static Item userItem = null;
    public static int result_value = -1 ;
    public static String roleInDevice = "";
    public static String firstName = "";
    public static String lastName = "";
    public static JSONArray wFormatArray = null;
    public static long wFacilityCode = -1;
    IrisApplication irisApplication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_mgr);
        irisApplication =  (IrisApplication) getApplication();
        irisApplication.registerActivity(this);

        changeStatusBarColor("#01000000");
        setDecorView();

        IrisApplication.activateBackButton(this, R.id.back_linear);
        findViewById(R.id.titlebar_linear).setBackgroundColor(getColor(R.color.settingTitleBackground));
        ((TextView)findViewById(R.id.title)).setText(getResources().getText(R.string.user_management));

        itemsListFragment = new ItemsListFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentItemsList, itemsListFragment)
                .commit();

        fragmentManager.beginTransaction()
                    .replace(R.id.flDetailContainer, new ItemEmptyFragment())
                    .commit();

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    setDecorView();
                }
            }
        });
        IT100.setUserListChangeListener(new UserListChangeListener()
        {
            @Override
            public void changed()
            {
                Log.d("IT100" , "user list changed");
            }
        });
        getGlobalWiegandFormat();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            default:
                break;
        }
    }

    @Override
    public void onItemSelected(Item item, boolean addUser) {

        if (addUser) {
            showNewUser(item, 0);
        } else {
            long rtnUserInfo = 0;
            rtnUserInfo = IT100.getUserInfo(item.getUserId(), new UserInfoCallback() {

                @Override
                public void userInfoResult(int resultCode , UserInfo userInfo) {

                    if(resultCode == MessageType.ID_USER_NOT_FOUND){
                        return;

                    }else if(resultCode == MessageType.ID_SUCCESS) {
                        Item item = new Item();
                        item.setUser_guid(userInfo.guid);
                        item.setUser_id(userInfo.userID);
                        item.setTitle(userInfo.title);
                        item.setFirst_name(userInfo.firstName);
                        item.setLast_name(userInfo.lastName);
                        item.setEmail_address(userInfo.emailAddr);
                        item.setPhone_number(userInfo.phoneNum);
                        item.setLeft_iris_img(userInfo.lIrisImage);
                        item.setRight_iris_img(userInfo.rIrisImage);
                        item.setFace_img(userInfo.faceImage);
                        item.setIs_admin(userInfo.role);
                        item.setRecogMode(userInfo.recogMode);
                        item.setEnrollTimestamp(userInfo.enrollTimestamp);
                        item.setActive(userInfo.active);
                        item.setAdminID(userInfo.adminID);
                        item.setAdminPassword(userInfo.adminPassword);
                        item.setLeft_iris_code(userInfo.lIrisCode);
                        item.setRight_iris_code(userInfo.rIrisCode);
                        item.setFace_code(userInfo.faceCode);
                        item.setFace_small_img(userInfo.faceSmallImage);
                        item.setCardInfo(userInfo.jarrayCardItems);
                        item.department = userInfo.department;
                        item.status = userInfo.status;
                        item.userDefined1 = userInfo.userDefined1;
                        item.userDefined2 = userInfo.userDefined2;
                        item.userDefined3 = userInfo.userDefined3;
                        item.userDefined4 = userInfo.userDefined4;
                        item.userDefined5 = userInfo.userDefined5;
                        item.userConsent = false; //userInfo.userConsent
                        showUserInfo(item, ConstData.USERDETAIL_SHOW_DETAIL);
                    }
                }
            });
            if (rtnUserInfo == MessageType.ID_RTN_SUCCESS) {
            }// success
            else if (rtnUserInfo == MessageType.ID_RTN_WRONG_PARA) {
            }// fail
            else if (rtnUserInfo == MessageType.ID_RTN_NOT_OPENED_FAIL) {
            }// fail
            else if (rtnUserInfo == MessageType.ID_RTN_FAIL) {
            }// fail
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

    private void showNewUser(Item item, int result){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        ItemEmptyUserFragment itemEmptyUserFragment = new ItemEmptyUserFragment();
        ItemEmptyUserFragmentWithCard itemEmptyUserFragmentWithCard = new ItemEmptyUserFragmentWithCard();
        Bundle args = new Bundle();
        args.putSerializable(ConstData.USER_BUNDLE_USER_ITEM, item);
        args.putInt(ConstData.USER_BUNDLE_SHOW_TYPE, ConstData.USERDETAIL_SHOW_ENROLL);
        args.putInt(ConstData.USER_BUNDLE_RESULT, result);
        itemEmptyUserFragment.setArguments(args);
        itemEmptyUserFragmentWithCard.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.flDetailContainer, itemEmptyUserFragment);
        ft.replace(R.id.flDetailContainer, itemEmptyUserFragmentWithCard);
        ft.commit();
    }
    private void showUserInfo(Item item, int showType){
        showUserInfo(item, showType, null);
    }

    private void showUserInfo(Item item, int showType, String erroMsg){

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        //Todo..  get card reader info
        ItemDetailFragmentNew itemDetailFragmentNew = new ItemDetailFragmentNew();
        //ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
        ItemDetailFragmentWithCard itemDetailFragmentWithCard = new ItemDetailFragmentWithCard();
        ItemDetailFragmentPolicy itemDetailFragmentPolicy = new ItemDetailFragmentPolicy();

        Bundle args = new Bundle();
        args.putSerializable(ConstData.USER_BUNDLE_USER_ITEM, item);
        args.putInt(ConstData.USER_BUNDLE_SHOW_TYPE, showType);
        args.putString(ConstData.USER_BUNDLE_ERROR_MSG, erroMsg);

        itemDetailFragmentNew.setArguments(args);
        itemDetailFragmentWithCard.setArguments(args);
        itemDetailFragmentPolicy.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.flDetailContainer, itemDetailFragmentNew);
        //ft.replace(R.id.flDetailContainer, itemDetailFragmentWithCard);
        ft.replace(R.id.flDetailContainer, itemDetailFragmentPolicy);
        ft.commit();
    }

    @Override
    public void onUserListUpdate(String type) {

        if(type.equals(ConstData.USERLIST_REFRESH)){
            itemsListFragment.clearFocus();
        }else if(type.equals(ConstData.USERLIST_RELOAD)){
            itemsListFragment.getUserCount();
            itemsListFragment.getSearchUserList("", 0, 100, false);
        }
    }

    @Override
    public void onDeleteNewUser() {
        //Logger.d("onDeleteNewUser @@");
        itemsListFragment.deleteNewUser();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 3001){
            IT100.abortCapture();
        }

        if(resultCode ==MessageType.IModifyUserBio){

            try {
                switch(result_value){
                    case MessageType.ID_MATCH_OK:
                        if(userItem.jsonCaptureStatus!=null && !userItem.jsonCaptureStatus.isEmpty()) {

                            try {
                                JSONObject jCaptureStatus = new JSONObject(userItem.jsonCaptureStatus);

                                Logger.d(" Result captureStatus = " +  jCaptureStatus.toString(4));
                                int captureStatusFace = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_FACE);
                                int captureStatusLEye = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_LEFT_EYE);
                                int captureStatusREye = jCaptureStatus.optInt(MessageKeyValue.CAPTURE_BIO_STATUS_RIGHT_EYE);

                                if(userItem.emptyBio== true){
                                    showUserInfo(userItem, ConstData.USERDETAIL_SHOW_DETAIL);
                                }else {
                                    if (userItem.isCaptureFace() && captureStatusFace != MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS)
                                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.enroll_error_capture_face));
                                    else if (userItem.isCaptureIris() && (captureStatusLEye != MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS &&
                                            captureStatusREye != MessageKeyValue.CAPTURE_BIO_RESULT_SUCCESS)) {
                                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.enroll_error_capture_iris));
                                    } else {
                                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_DETAIL);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            showUserInfo(userItem, ConstData.USERDETAIL_SHOW_DETAIL);
                        }

                        break;
                    case MessageType.ID_IRIS_CAPTURE_FAIL: // FaceCaptureFailed
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.enroll_error_capture));
                        break;
                    case MessageType.ID_FACE_CAPTURE_FAIL : //IrisCaptureFailed = 11061000
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.enroll_error_capture_face));
                        break;
                    case MessageType.ID_VERIFICATION_FAILED : // ReturnVerificationFailed
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.enroll_error_verified));
                        break;
                    case MessageType.ID_FAKE_EYE_DETECTED :
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.capture_fail_fake_eye));
                        break;
                    case MessageType.ID_FAKE_EYE_DETECTED_FAILED :
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.liveness_eye_detect_fail));
                        break;
                    case MessageType.ID_DUPLICATED_USER_FOUND:
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.enroll_error_already));
                        break;
                    case MessageType.ID_OCCLUDED_EYES: //OccludedEyesError
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.error_occluded_eyes)); //Open your eyes wider and retry
                        break;
                    default:
                        showUserInfo(userItem, ConstData.USERDETAIL_SHOW_ERROR_DETAIL, getString(R.string.enroll_error_verified));
                        break;
                }

            }   catch (Exception e) {

            }
        } else if(resultCode == 40007) { // MessageType.NewUserEnroll
            try {
                showNewUser(userItem, result_value);

            } catch (Exception e) {

            }
        }
    }

    static Map<String, JSONObject> wFormatHashmap = new HashMap<String, JSONObject>();
    static ArrayList wFormatNameList ;
    private void getDeviceWiegandFormat() {
        IT100.getDeviceWiegandFormats(new DeviceWiegandFormatsCallback(){
            @Override
            public void onDeviceWiegandFormats(int result, String jparam) {
                if (jparam != null) {
                    wFormatNameList = new ArrayList<String>();
                    // Logger.d(jparam);
                    try {
                        JSONObject jobjTotal = new JSONObject(jparam);
                        JSONArray jsonArrayFormatIDs = jobjTotal.optJSONArray(MessageKeyValue.WDEVICE_IN_FORMAT_IDS);
                        JSONObject jobjFormatItem;
                        if (jsonArrayFormatIDs != null) {
                            for (int i = 0; i < jsonArrayFormatIDs.length(); i++) {
                                jobjFormatItem = (JSONObject) wFormatHashmap.get(jsonArrayFormatIDs.getString(i));

                                if(jobjFormatItem!=null) {
                                    wFormatNameList.add(jobjFormatItem.optString(MessageKeyValue.WFORMAT_NAME));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getGlobalWiegandFormat(){
        IT100.getGlobalWiegandFormats(new GlobalWiegandFormatsCallback() {
            @Override
            public void onGlobalWiegandFormats(int resCode, String globalSetting) {
                if(resCode ==0){
                    try {
                        JSONObject jGlobalSetting = new JSONObject(globalSetting);
                        JSONObject jWiegandOut;
                        //Logger.d(jGlobalSetting.toString(4));

                        wFormatArray = jGlobalSetting!=null ?jGlobalSetting.optJSONArray(MessageKeyValue.WFORMAT): null;
                        jWiegandOut = jGlobalSetting!=null ? jGlobalSetting.optJSONObject(MessageKeyValue.WOUT): null;

                        wFormatHashmap.clear();
                        if(wFormatArray!=null) {
                            //wFormatArray = new JSONArray(formatList);
                            JSONObject wFormat ;
                            for (int i = 0; i < wFormatArray.length(); i++) {
                                wFormat = wFormatArray.optJSONObject(i);
                                //Logger.d("wFormat is "+ wFormat.toString(4));
                                wFormatHashmap.put(wFormat.optString(MessageKeyValue.WFORMAT_ID),
                                        wFormat);
                            }
                            //Logger.d(wFormatArray.toString(4));
                        }

                        if(jWiegandOut!=null){
                            wFacilityCode = jWiegandOut.optLong(MessageKeyValue.WOUT_DEFAULT_FCODE, -1);
                        }
                        getDeviceWiegandFormat();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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