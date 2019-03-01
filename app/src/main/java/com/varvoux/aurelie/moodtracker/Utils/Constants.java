package com.varvoux.aurelie.moodtracker.utils;

import com.varvoux.aurelie.moodtracker.R;
import com.varvoux.aurelie.moodtracker.model.MoodUI;

public class Constants {

    public static final int DEFAULT_MOOD = 3; //Default MoodUI mood when app starts
    public static final int MAX_MOODS = 7; //Maximum size of the ArrayList for mood's history

    public static final String CURRENT_MOOD_KEY = "currentMood";//Default position key for SharedPreferences
    public static final String MOOD_LIST_KEY = "moodList"; //Default ArrayList history key for SharedPreferences
    public static final String USER_ENTRY_KEY = "userEntry"; //Default user comment key for SharedPrefences

    // This array contains MoodUI objects to display dynamically smileys and backgrounds
    public static final MoodUI[] moodsUITab = {new MoodUI(R.color.faded_red, R.drawable.smiley_sad),
            new MoodUI(R.color.warm_grey, R.drawable.smiley_disappointed),
            new MoodUI(R.color.cornflower_blue_65, R.drawable.smiley_normal),
            new MoodUI(R.color.light_sage, R.drawable.smiley_happy),
            new MoodUI(R.color.banana_yellow, R.drawable.smiley_super_happy)
    };

    //All colors used in HistoryActivity to display history bars
    public static final int[] moodColors = {R.color.faded_red, R.color.warm_grey, R.color.cornflower_blue_65,
            R.color.light_sage, R.color.banana_yellow};

    //This array contains all weights for the invisible view, which takes up space on the
    // mood bar according to its own weight
    public static final float invisibleViewWeight[] = {0.4f, 0.8f, 1.5f, 5, 300};
}
