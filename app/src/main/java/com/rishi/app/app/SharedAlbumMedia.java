package com.rishi.app.app;

import java.util.ArrayList;

/**
 * Created by amitrajula on 2/2/16.
 */
public class SharedAlbumMedia {


    private String name,id,displayPicture;
    private ArrayList<Media> media = new ArrayList<>();

    public SharedAlbumMedia(){

    }

    public SharedAlbumMedia(String id,String name,String displayPicture,ArrayList<Media> media){

        this.name = name;

        this.displayPicture =displayPicture;
        this.id = id;
        this.media = media;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }



    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public void setId (String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setMedia (ArrayList<Media> media){
        this.media = media;
    }

    public ArrayList<Media> getMedia() {
        return media;
    }

}
