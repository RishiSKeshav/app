package com.rishi.app.app;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by amitrajula on 2/2/16.
 */
public class FacebookFriends {

    private String id,name,emailId,mobileNo,displayPicture,facebookId;
    private boolean isSelected;


    public FacebookFriends(){

    }

    public FacebookFriends(String id,String name,String emailId,
                           String mobileNo,String displayPicture,String facebookId,boolean isSelected){
        this.id = id;
        this.name = name;
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.displayPicture = displayPicture;
        this.facebookId = facebookId;
        this.isSelected = isSelected;


    }


    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void s(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }


    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo(){
        return mobileNo;
    }

    public void setMobileNo(String mobileNo){
            this.mobileNo = mobileNo;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture (){this.displayPicture = displayPicture;}

    public String getFacebookId(){
        return  facebookId;
    }

    public void setFacebookId(){
        this.facebookId = facebookId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
