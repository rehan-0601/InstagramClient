package com.codepath.rmulla.instagramclient;

import java.util.ArrayList;

/**
 * Created by rmulla on 9/15/15.
 */
public class InstagramPhoto {
    public String username;
    public String caption;
    public String imageUrl;
    public int imageHeight;
    public int imageWidth;
    public int likesCount;
    public String postTime;
    public String location;
    public String profilePicUrl;
    //each comment is stored in the form:"username comment"
    public ArrayList<String> comments;
    public int commentsCount;

    public InstagramPhoto() {
        comments = new ArrayList<String>();
    }



}
