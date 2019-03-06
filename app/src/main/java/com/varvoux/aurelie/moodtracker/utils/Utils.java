package com.varvoux.aurelie.moodtracker.utils;

import java.util.Calendar;

public class Utils {

    /**
     * We compare dates of each moods to the current date, the result tell us the difference of days
     * between the mood's date and the current date
     */
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
        return daysBetween - 1;
    }


    /**
     * This method tell us if the date of mCurrentMood is the same as the current date's date
     */
    public static boolean isSameDay(long date1, long date2) { // This method calculates if two dates in milliseconds are the same or not
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTimeInMillis(date1);
        calendar2.setTimeInMillis(date2);

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }
}

