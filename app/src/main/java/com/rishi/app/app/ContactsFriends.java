package com.rishi.app.app;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by amitrajula on 2/2/16.
 */
public class ContactsFriends {

    private String id,name,emailId,mobileNo,displayPicture,facebookId,number;


    public ContactsFriends(){

    }

    public ContactsFriends(String id,String name,String emailId,String number,
                           String mobileNo,String displayPicture,String facebookId){
        this.id = id;
        this.name = name;
        this.emailId = emailId;
        this.number = number;
        this.mobileNo = mobileNo;
        this.displayPicture = displayPicture;
        this.facebookId = facebookId;

    }


    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setName(String firstName){
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

    public String getNumber() {
        return number;
    }

    public void setNumber (String number){
        this.number= number;
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
}
