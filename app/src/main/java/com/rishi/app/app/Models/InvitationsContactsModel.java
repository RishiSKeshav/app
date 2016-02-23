package com.rishi.app.app.Models;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by amitrajula on 2/2/16.
 */
public class InvitationsContactsModel {

    private String name,number;

    public InvitationsContactsModel(){

    }

    public InvitationsContactsModel(String name,String number){
        this.number = number;
        this.name = name;

    }


    public void setNumber(String number){
            this.number = number;
    }

    public String getNumber(){
        return number;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }



}
