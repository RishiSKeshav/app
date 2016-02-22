package com.rishi.app.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by RishiS on 2/13/2016.
 */
public class Image implements Parcelable {

    int id;
    String name;
    String path;
    String link;

    public Image(Parcel in) {
        readFromParcel(in);
    }

    public int describeContents() {
        return this.hashCode();
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(link);
        dest.writeInt(id);

    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        name = in.readString();
        path = in.readString();
        link=in.readString();
        id = in.readInt();

    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };




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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
