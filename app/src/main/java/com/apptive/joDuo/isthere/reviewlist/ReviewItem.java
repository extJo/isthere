package com.apptive.joDuo.isthere.reviewlist;

import android.graphics.drawable.Drawable;

/**
 * Created by joseong-yun on 2017. 6. 21..
 */

public class ReviewItem {

    private Drawable pictureDrawable;
    private String title;
    private String description;
    private Boolean like;

    public void setPicture(Drawable picture){
        pictureDrawable = picture;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setLike(Boolean like) {this.like = like;}

    public Drawable getPicture(){
        return pictureDrawable;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public Boolean getLike() {return like;}


}
