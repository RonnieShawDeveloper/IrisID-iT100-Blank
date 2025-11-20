package com.irisid.user.it100_sample.Common;

public class ConstData {

    public static final String RECOG_TYPE = "recogType";
    public static final String RECOG_TYPE_CLOCK_IN = "Clock in";
    public static final String RECOG_TYPE_CLOCK_OUT = "Clock out";
    public static final String RECOG_TYPE_TRANSFER = "Transfer";
    public static final String RECOG_TYPE_NONE = "NONE";
    public static final String RECOG_TYPE_SYSTEM_ADMIN = "System admin";
    public static final String RECOG_TYPE_CLOCK_IN_CONTINUOUS = "Clock in continuous";

    public static final String RECOG_MODE = "recogMode";
    public static final String RECOG_MODE_CONTINUOUS= "continuous";
    public static final String RECOG_MODE_INTERACTIVE = "interactive";

    public static final String TRANSACTION_TYPE                 = "transactionType";
    public static final String TRANSACTION_TYPE_UI_BASIC        = "basic";
    public static final String TRANSACTION_TYPE_UI_TRANSFER     = "transfer";
    public static final String TRANSACTION_TYPE_UI_EXTEND       = "extend";

    public static final String CAPTURE_STATE = "state";
    public static final String CAPTURE_STATE_ENROLL_USER = "user_enrollment";
    public static final String CAPTURE_STATE_ENROLL_USER_BY_RESTAPI = "user_enrollment_by_restapi";
    public static final String CAPTURE_STATE_RECOG_BY_RESTAPI = "recog_by_restapi";
    public static final String CAPTURE_STATE_CLOCK_IN_CONTINUOUS = "recog_in_continuous";
    public static final String CAPTURE_STATE_CLOCK_IN_OUT_INTERACTIVE = "recog_in_out_interactive";
    public static final String CAPTURE_STATE_BARCODE_AND_RECOG = "barcode_recog";
    public static final String CAPTURE_STATE_MODIFY_BIO = "user_modifybio";
    public static final String CAPTURE_STATE_NEW_USER = "new_user";
    public static final String CAPTURE_STATE_SYSTEM_ADMIN = "amdin";

    public static final String ADMIN_AUTH_ALL = "all";
    public static final String ADMIN_AUTH_BIO = "biometrics";
    public static final String ADMIN_AUTH_ID_PW = "id_password";

    public static final String AUTH_TYPE_ALL = "all";
    public static final String AUTH_TYPE_IRIS_RECOG = "iris_recog";
    public static final String AUTH_TYPE_ID_PW = "id_password";

    public static final String AUTH_MODE_AND = "AND";
    public static final String AUTH_MODE_OR = "OR";
    public static final String AUTH_MODE_FUSION = "FUSION";

    public static final String USERLIST_RELOAD = "reload";
    public static final String USERLIST_REFRESH = "refresh";

    public static final String AUTH_MODE_POLICY_INDIVIDUAL= "individual";
    public static final String AUTH_MODE_POLICY_DEVICE  = "device";

    public static final String USER_BUNDLE_USER_ITEM = "item";
    public static final String USER_BUNDLE_SHOW_TYPE = "type";
    public static final String USER_BUNDLE_RESULT = "result";
    public static final String USER_BUNDLE_ERROR_MSG = "errMsg";
    public static final String USER_BUNDLE_INFO_USERID      = "userID";
    public static final String USER_BUNDLE_INFO_FIRSTNAME   = "firstName";
    public static final String USER_BUNDLE_INFO_LASTNAME    = "lastName";
    public static final String USER_BUNDLE_INFO_FACEIMAGE   = "faceImage";
    public static final String USER_BUNDLE_INFO_SCORE       = "score";
    public static final String USER_BUNDLE_INFO_TIME        = "time";

    public static final String NETWORK_TYPE_WIFI        = "wifi";
    public static final String NETWORK_TYPE_ETHERNET    = "ethernet";

    public static final String INTENT_ADMIN_LOGIN_TYPE  = "admin_login_type";
    public static final String INTENT_ADMIN_ONLY_BIOMETRICS = "only_biometrics";

    public static final String CHECK_IS_RECOGING    = "isRecoging";

    public static final String QRCODE_MESSAGE           = "qr_message";

    //CostCenter JSON key data
    public static final String COSTCENTERS      = "costCenters";
    public static final String COSTCENTER_ID    = "id";
    public static final String COSTCENTER_PARENTID  = "parentId";
    public static final String COSTCENTER_NAME      = "name";
    public static final String COSTCENTER_TREEINDEX = "treeIndex";
    public static final String COSTCENTER_PAYROLLCODE   = "payrollCode";
    public static final String COSTCENTER_DESCIPTION    = "description";
    public static final String COSTCENTER_EXTERNALID    = "externalID";
    public static final String COSTCENTER_LOCATIONCODE  = "locationCode";
    public static final String COSTCENTER_INDEX     = "index";
    public static final String COSTCENTER_VALUE     = "value";

    public static final int USERDETAIL_SHOW_DETAIL = 100;
    public static final int USERDETAIL_SHOW_ENROLL = 101;
    public static final int USERDETAIL_SHOW_ERROR = 200;
    public static final int USERDETAIL_SHOW_ERROR_DETAIL = 201;


}
