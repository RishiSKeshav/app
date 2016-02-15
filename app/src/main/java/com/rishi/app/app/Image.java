package com.rishi.app.app;

import java.io.Serializable;

/**
 * Created by RishiS on 2/13/2016.
 */
public class Image implements Serializable {

    int id;
    String name;
    String path;
    String data_taken;
    String uuid;

    public Image(){

    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData_taken() {
        return data_taken;
    }

    public void setData_taken(String data_taken) {
        this.data_taken = data_taken;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
