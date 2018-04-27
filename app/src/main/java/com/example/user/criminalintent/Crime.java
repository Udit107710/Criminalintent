package com.example.user.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by user on 27-12-2017.
 */

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mNumber;


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect(){
        return mSuspect;
    }

    public void setSuspect( String suspect){ mSuspect = suspect; }

    public String getNumber(){ return mNumber; }

    public void setNumber(String Number){ mNumber = Number; }

    public UUID getId() {
        return mId;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    public Crime(){
        this(UUID.randomUUID());
    }
    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }

}
