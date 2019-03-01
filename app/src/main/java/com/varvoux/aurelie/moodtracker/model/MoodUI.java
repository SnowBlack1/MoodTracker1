package com.varvoux.aurelie.moodtracker.model;

/**
 * This class contains all informations to display moods in MainActivity
 */

public class MoodUI {
    private int colorResources; //Mood's background color
    private int smileyResources; //Mood's smiley

    public MoodUI(int colorResources, int smileyResources) {
        this.colorResources = colorResources;
        this.smileyResources = smileyResources;
    }

    public int getColorResources() {
        return colorResources;
    }

    public int getSmileyResources() {
        return smileyResources;
    }

}


