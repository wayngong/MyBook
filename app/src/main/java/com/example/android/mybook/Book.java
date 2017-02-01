package com.example.android.mybook;

import android.graphics.Bitmap;

import java.util.ArrayList;

import static android.R.attr.thumb;
import static android.R.attr.thumbnail;

/**
 * Created by gongkai on 2017/1/30.
 */

public class Book {
    final String NO_TITLE_PROVIDED = null;
    final Bitmap NO_THUMBNAIL_PROVIDED = null;
    final ArrayList<String> NO_AUTHORS_PROVIDED = null;
    final String NO_PUBLISH_TIME_PROVIDED = null;

    private String mTitle = NO_TITLE_PROVIDED;
    private Bitmap mThumbnail = NO_THUMBNAIL_PROVIDED;
    private ArrayList<String> mAuthors = NO_AUTHORS_PROVIDED;
    private String mPublishTime = NO_PUBLISH_TIME_PROVIDED;

    // Constructor for book
    public Book(String title, ArrayList<String> authors, String publishTime, Bitmap thumbnail){
        mTitle = title;
        mAuthors = authors;
        mPublishTime = publishTime;
        mThumbnail = thumbnail;
    }

    public Book(String title, ArrayList<String> authors, String publishTime){
        mTitle = title;
        mAuthors = authors;
        mPublishTime = publishTime;
    }

    // Has Title for book
    public boolean hasTitle(){
        return mTitle!=NO_TITLE_PROVIDED;
    }

    // get Title for book
    public String getTitle(){
        return mTitle;
    }

    // Has Thumbnail for book or not
    public boolean hasThumbnail(){
        return (mThumbnail != NO_THUMBNAIL_PROVIDED);
    }

    // Get Thumbnail for book
    public Bitmap getThumbnail(){
        return mThumbnail;
    }

    // Set Authors
    public void setAuthors(ArrayList<String> authors){
        mAuthors = authors;
    }

    // Has Author or Not
    public boolean hasAuthor(){
        return (mAuthors != null && mAuthors.size() > 0);
    }

    // Get Authors
    public ArrayList<String> getAuthors(){
        return mAuthors;
    }

    // Has Publish Time or Not
    public boolean hasPublishTime(){
        return mPublishTime!=NO_PUBLISH_TIME_PROVIDED;
    }

    // Get Publish Time
    public String getPublishTime(){
        return mPublishTime;
    }
}
