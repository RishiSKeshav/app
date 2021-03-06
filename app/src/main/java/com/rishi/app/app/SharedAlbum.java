package com.rishi.app.app;

/**
 * Created by amitrajula on 2/2/16.
 */
public class SharedAlbum {


    private String name,thumbnail,id;
    private String date,members;
    private String count;

    public SharedAlbum(){

    }

    public SharedAlbum(String id,String name,String thumbnail,String count,String date,String members){

        this.name = name;
        this.thumbnail = thumbnail;
        this.count = count;
        this.date = date;
        this.id = id;
        this.members= members;
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

    public String getMembers(){
        return members;
    }

    public void setMembers(String count){
        this.members = members;
    }

    public void setId (String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

}
