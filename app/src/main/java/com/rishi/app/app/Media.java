package com.rishi.app.app;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by amitrajula on 2/2/16.
 */
public class Media {

    private String name,path;
    private String date;
    private String id;

    public Media(){

    }

    public Media(String id,String name,String path,String date){
        this.id = id;
        this.name = name;
        this.path = path;
        this.date = date;
            }

    public Media (String path){
        this.path = path;
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

    public void setPath(String thumbnail) {
        this.path = path;
    }

}
