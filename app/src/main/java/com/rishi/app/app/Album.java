package com.rishi.app.app;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by amitrajula on 2/2/16.
 */
public class Album {

    private String name,thumbnail;
    private String date;
    private String count;

    public Album(){

    }

    public Album(String name,String thumbnail,String count,String date){

        this.name = name;
        this.thumbnail = thumbnail;
        this.count = count;
        this.date = date;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCount(){
        return count;
    }

    public void setCount(String count){
        this.count = count;
    }
}
