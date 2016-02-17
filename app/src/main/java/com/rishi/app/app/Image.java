package com.rishi.app.app;

import java.io.Serializable;

/**
 * Created by RishiS on 2/13/2016.
 */
public class Image implements Serializable {

    int id;
    String name;
    String path;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
