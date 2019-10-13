package com.deo.tanis.safechat.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String uid;
    public String imageUrl;







    public User(String name, String email, String uid, String imageUrl) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.imageUrl = imageUrl;


    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }


    public User() {

    }

}