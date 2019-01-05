package com.varvoux.aurelie.moodtracker;

public class MoodUI {
    private int colorResources;
    private int smileyResources;
    private int position;

    public MoodUI(int colorResources, int smileyResources, int position) {
        this.colorResources = colorResources;
        this.smileyResources = smileyResources;
        this.position = position;
    }

    public int getColorResources() {
        return colorResources;
    }

    public int getSmileyResources() {
        return smileyResources;
    }

    public int getPosition() {
        return position;
    }
}

