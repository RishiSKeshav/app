package com.rishi.app.app;

/**
 * Created by RishiS on 2/20/2016.
 */
public class SyncImages {

    int id;
    String name;
    String path;
    Boolean syncStatus;
    String link;

    public SyncImages(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Boolean syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
