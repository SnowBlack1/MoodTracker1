package com.varvoux.aurelie.moodtracker.model;

import com.varvoux.aurelie.moodtracker.utils.Constants;

/**
 * This class contains all informations needed to save a mood
 */

public class MoodHistory {

    private int mPosition; // Position of the mood
    private String userComment; // Comment saved by user
    private long date; //Mood's date

    public MoodHistory() {
        date = System.currentTimeMillis();
        mPosition = Constants.DEFAULT_MOOD;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
