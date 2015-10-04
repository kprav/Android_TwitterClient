package com.codepath.apps.twitterclient.helpers;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilites {

    // TODO: Check for nulls in all the methods

    // Example: Sun Oct 04 04:01:34 +0000 2015
    private static final String TWEET_CREATED_TIME_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

    public static Date stringToDate(String input) {
        SimpleDateFormat formatter = new SimpleDateFormat(TWEET_CREATED_TIME_FORMAT);
        Date date = null;
        try {
            date = formatter.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long dateToEpoch(Date date) {
        long epoch = 0;
        if (date != null)
            epoch = date.getTime();
        return epoch;
    }

    public static String getRelativeTime(String time) {
        Date date = stringToDate(time);
        long epoch = dateToEpoch(date);
        String relativeCreationTime = getRelativeTime(epoch);
        Log.d("TIME", relativeCreationTime);
        return format(relativeCreationTime);
    }

    public static String getRelativeTime(long time) {
        return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
    }

    private static String format(String inputTime) {
        String time = null;
        time = inputTime.replaceAll("ago", "");
        time = time.replaceAll("seconds", "s");
        time = time.replaceAll("second", "s");
        time = time.replaceAll("secs", "s");
        time = time.replaceAll("sec", "s");
        time = time.replaceAll("minutes", "m");
        time = time.replaceAll("minute", "m");
        time = time.replaceAll("mins", "m");
        time = time.replaceAll("min", "m");
        time = time.replaceAll("hours", "h");
        time = time.replaceAll("hour", "h");
        time = time.replaceAll("hrs", "h");
        time = time.replaceAll("hr", "h");
        time = time.replaceAll("in", "");
        time = time.replaceAll("\\s", "");
        return time;
    }
}
