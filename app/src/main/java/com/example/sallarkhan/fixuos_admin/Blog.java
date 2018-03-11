package com.example.sallarkhan.fixuos_admin;

import android.app.Notification;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sallar Khan on 9/9/2017.
 */

public class Blog {
    private String uid,title,desc,image;

    public Blog() {
    }



    public Blog(String uid , String title, String desc, String image) {
        this.uid = uid;
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public void setUid(String username) {
        this.uid = username;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }




}
