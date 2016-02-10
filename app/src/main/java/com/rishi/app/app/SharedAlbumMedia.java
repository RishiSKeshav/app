package com.rishi.app.app;

import java.util.ArrayList;

/**
 * Created by amitrajula on 2/2/16.
 */
public class SharedAlbumMedia {


    private String firstName,lastName,id,displayPicture;
    private ArrayList<Media> media = new ArrayList<>();

    public SharedAlbumMedia(){

    }

    public SharedAlbumMedia(String id,String firstName,String lastName,String displayPicture,ArrayList<Media> media){

        this.firstName = firstName;
        this.lastName = lastName;
        this.displayPicture =displayPicture;
        this.id = id;
        this.media = media;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getFirstName(){
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
