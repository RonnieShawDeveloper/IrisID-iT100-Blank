package com.irisid.user.it100_sample.UserList;

import com.irisid.it100.data.UserInfo;
import org.json.JSONArray;
import java.io.Serializable;

public class Item extends UserInfo implements Serializable
{

    private String matched_guid;
    public boolean isNew = false;
    public boolean captureFace = true;
    public boolean captureIris = true;
    public boolean emptyBio = false;
    public String jsonCaptureStatus= null;

    public boolean isEmptyBio() {
        return emptyBio;
    }

    public void setEmptyBio(boolean emptyBio) {
        this.emptyBio = emptyBio;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isCaptureFace() {
        return captureFace;
    }

    public void setCaptureFace(boolean captureFace) {
        this.captureFace = captureFace;
    }

    public boolean isCaptureIris() {
        return captureIris;
    }

    public void setCaptureIris(boolean captureIris) {
        this.captureIris = captureIris;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean isActive) {
        active = isActive;
    }


    public Item() { }

    public String getUserId() {
        return userID;
    }

    public void setUser_id(String user_id) {
        this.userID = user_id;
    }

    public String getUser_guid() {
        return guid;
    }

    public void setUser_guid(String user_guid) {
        this.guid = user_guid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirst_name() {
        return firstName;
    }

    public void setFirst_name(String first_name) {
        this.firstName = first_name;
    }

    public String getLast_name() {
        return lastName;
    }

    public void setLast_name(String last_name) {
        this.lastName = last_name;
    }

    public String getEmail_address() {
        return emailAddr;
    }

    public void setEmail_address(String email_address) {
        this.emailAddr = email_address;
    }

    public String getPhone_number() {
        return phoneNum;
    }

    public void setPhone_number(String phone_number) {
        this.phoneNum = phone_number;
    }

    public byte[] getFace_img() {
        return faceImage;
    }

    public void setFace_img(byte[] face_img) {
        this.faceImage = face_img;
    }

    public byte[] getFace_small_img() {
        return faceSmallImage;
    }

    public void setFace_small_img(byte[] face_small_img) {
        this.faceSmallImage = face_small_img;
    }

    public byte[] getLeft_iris_img() {
        return lIrisImage;
    }

    public void setLeft_iris_img(byte[] left_iris_img) {
        this.lIrisImage = left_iris_img;
    }

    public byte[] getRight_iris_img() {
        return rIrisImage;
    }

    public void setRight_iris_img(byte[] right_iris_img) {
        this.rIrisImage = right_iris_img;
    }


    public String getIs_admin() {
        return role;
    }

    public void setIs_admin(String is_admin) {
        this.role = is_admin;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getRecogMode() {
        return recogMode;
    }

    public void setRecogMode(String recogMode) {
        this.recogMode = recogMode;
    }

    public long getEnrollTimestamp() {
        return enrollTimestamp;
    }

    public void setEnrollTimestamp(long enrollTimestamp) {
        this.enrollTimestamp = enrollTimestamp;
    }

    public String getFace_code() {
        return faceCode;
    }

    public void setFace_code(String face_code) {
        this.faceCode = face_code;
    }

    public String getLeft_iris_code() {
        return lIrisCode;
    }

    public void setLeft_iris_code(String left_iris_code) {
        this.lIrisCode = left_iris_code;
    }

    public String getRight_iris_code() {
        return rIrisCode;
    }

    public void setRight_iris_code(String right_iris_code) {
        this.rIrisCode = right_iris_code;
    }

    public String getMatched_guid() {
        return matched_guid;
    }

    public void setMatched_guid(String matched_guid) {
        this.matched_guid = matched_guid;
    }

    public JSONArray getCardInfo() {return jarrayCardItems;}

    public void setCardInfo(JSONArray card_info) {this.jarrayCardItems = card_info; }

}

