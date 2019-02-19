package com.varvoux.aurelie.moodtracker.utils;

import com.varvoux.aurelie.moodtracker.model.MoodUI;
import com.varvoux.aurelie.moodtracker.R;

import java.util.Calendar;

public class Utils {

    public static MoodUI[] moodsUITab = {new MoodUI(R.color.faded_red, R.drawable.smiley_sad, 0),
            new MoodUI(R.color.warm_grey, R.drawable.smiley_disappointed, 1),
            new MoodUI(R.color.cornflower_blue_65, R.drawable.smiley_normal, 2),
            new MoodUI(R.color.light_sage, R.drawable.smiley_happy, 3),
            new MoodUI(R.color.banana_yellow, R.drawable.smiley_super_happy, 4)
    };

    public static int[] moodColors = {R.color.faded_red, R.color.warm_grey, R.color.cornflower_blue_65,
            R.color.light_sage, R.color.banana_yellow};


    public static float invisibleViewWeight[] = {0.4f, 0.8f, 1.5f, 5, 300};


    public static int diffDays(long startDateMillis, long endDateMillis) {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTimeInMillis(startDateMillis);
        endCalendar.setTimeInMillis(endDateMillis);

        Calendar currentCalendar = (Calendar) startCalendar.clone();
        int daysBetween = 0;
        while (currentCalendar.before(endCalendar)) {
            currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween-1;
    }
}

