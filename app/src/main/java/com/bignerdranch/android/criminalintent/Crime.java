package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;
import android.text.format.DateFormat;

/**
 * Created by sredorta on 9/19/2016.
 */

public class Crime {
    private String mTitle;
    private UUID mId;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;


    public Crime() {
        this(UUID.randomUUID());
        mDate = new Date();

    }
    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
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

    public UUID getId() {
        return mId;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
