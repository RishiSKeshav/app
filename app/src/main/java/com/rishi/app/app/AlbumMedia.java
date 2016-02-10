package com.rishi.app.app;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.entity.StringEntity;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by amitrajula on 2/2/16.
 */
public class AlbumMedia implements Parcelable{

    private String name,path;
    private String date,count;
    private String id,albumId;

    public AlbumMedia(Parcel in) {
        readFromParcel(in);
    }

    public int describeContents() {
        return this.hashCode();
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(date);
        dest.writeString(id);
        dest.writeString(albumId);
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        name = in.readString();
        path = in.readString();
        date = in.readString();
        id = in.readString();
        albumId  = in.readString();

    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public AlbumMedia createFromParcel(Parcel in) {
            return new AlbumMedia(in);
        }

        public AlbumMedia[] newArray(int size) {
            return new AlbumMedia[size];
        }
    };


    public AlbumMedia(){

    }




    public AlbumMedia(String id,String name,String path,String date,String albumId){
        this.id = id;
        this.name = name;
        this.path = path;
        this.date = date;
        this.albumId = albumId;
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

    public void setAlbumId (String albumId){
        this.albumId = albumId;
    }

    public String getAlbumId (){
        return albumId;
    }


}
