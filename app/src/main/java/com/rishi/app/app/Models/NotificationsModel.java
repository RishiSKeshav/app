package com.rishi.app.app.Models;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by amitrajula on 2/2/16.
 */
public class NotificationsModel {

    private String id,message,datathumbnail;
    private String date;
    private String from,dataId,dataName;

    public NotificationsModel(){

    }

    public NotificationsModel(String id,String from,String messgage,String datathumbnail,String date,String dataId,String dataName){
        this.id = id;
        this.from  = from;
        this.message = messgage;
        this.datathumbnail = datathumbnail;
        this.date = date;
        this.dataId = dataId;
        this.dataName = dataName;
    }


    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setFrom(String from){
        this.from = from;
    }

    public String getFrom(){
        return from;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }


    public String getDatathumbnail() {
        return datathumbnail;
    }

    public void setDatathumbnail(String datathumbnail) {
        this.datathumbnail = datathumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDataId(){
        return dataId;
    }

    public void setDataId(String dataId){
        this.dataId = dataId;
    }

    public String getDataName(){
        return dataName;
    }

    public void setDataName(String dataName){
        this.dataName = dataName;
    }
}
