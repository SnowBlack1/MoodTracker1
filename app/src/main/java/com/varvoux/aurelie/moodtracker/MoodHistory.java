package com.varvoux.aurelie.moodtracker;

public class MoodHistory {

    private int mPosition;
    private String userComment;
    private long date;

    public MoodHistory() {
        date = System.currentTimeMillis();
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

    public MoodHistory(int position, String userComment, long date) {
        mPosition = position;
        this.userComment = userComment;
        this.date = date;
    }

    public MoodHistory(int position, long date) {
        mPosition = position;
        this.date = date;
    }

    public MoodHistory(int position) {
        mPosition = position;
    }
}
