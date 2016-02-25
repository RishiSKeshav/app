package com.rishi.app.app;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by amitrajula on 2/2/16.
 */
public class SharedMedia {

    private String id,name,path,fromId,fromName,fromdisplayPicture;
    private String date;


    public SharedMedia(){

    }

    public SharedMedia(String id,String name,String path,String date,String fromId,String fromName,String fromdisplayPicture){

        this.id= id;
        this.name = name;
        this.path = path;
        this.fromId = fromId;
        this.fromName = fromName;
        this.fromdisplayPicture = fromdisplayPicture;
        this.date = date;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }
    public void setName(String album_name){
        this.name = album_name;
    }

    public String getName(){
        return name;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String album_date) {
        this.date = album_date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }


    public String getFromdisplayPicture() {
        return fromdisplayPicture;
    }

    public void setFromdisplayPicture(String fromdisplayPicture) {
        this.fromdisplayPicture = fromdisplayPicture;
    }



}
