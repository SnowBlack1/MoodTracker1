package com.varvoux.aurelie.moodtracker.Utils;

import com.varvoux.aurelie.moodtracker.model.MoodUI;
import com.varvoux.aurelie.moodtracker.R;

public class Utils {
    public static MoodUI[] getMoodsUI() {
        MoodUI[] moodsUITab = new MoodUI[5];
        moodsUITab[0] = new MoodUI(R.color.faded_red, R.drawable.smiley_sad, 0);
        moodsUITab[1] = new MoodUI(R.color.warm_grey, R.drawable.smiley_disappointed, 1);
        moodsUITab[2] = new MoodUI(R.color.cornflower_blue_65, R.drawable.smiley_normal, 2);
        moodsUITab[3] = new MoodUI(R.color.light_sage, R.drawable.smiley_happy, 3);
        moodsUITab[4] = new MoodUI(R.color.banana_yellow, R.drawable.smiley_super_happy, 4);

        return moodsUITab;
    }
}
