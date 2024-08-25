package com.gypsee.sdk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String calcDiffTime(long start, long end) {
        long diff = end - start;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        StringBuffer res = new StringBuffer();

        if (diffDays > 0)
            res.append(diffDays + "d");

        if (diffHours > 0) {
            if (res.length() > 0) {
                res.append(" ");
            }
            res.append(diffHours + "h");
        }

        if (diffMinutes > 0) {
            if (res.length() > 0) {
                res.append(" ");
            }
            res.append(diffMinutes + "m");
        }

        if (diffSeconds > 0) {
            if (res.length() > 0) {
                res.append(" ");
            }

            res.append(diffSeconds + "s");
        } else if (diffSeconds == 0) {
            res.append("0s");
        }
        return res.toString();
    }

    public static int calcDiffTimeInSec(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        long diffSeconds = diff / 1000;

        if (diffSeconds > 0)
            return (int) diffSeconds;
        else return 0;

    }

    public static String getTimeIndhms(long totalSecs) {
        // Get msec from each, and subtract.
        long diffSeconds = totalSecs % 60;
        long diffMinutes = (totalSecs % 3600) / 60;
        long diffHours = totalSecs / (3600);


        System.out.println("Time in seconds: " + diffSeconds + " seconds.");
        System.out.println("Time in minutes: " + diffMinutes + " minutes.");
        System.out.println("Time in hours: " + diffHours + " hours.");

        if (diffHours > 0) {
            return diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
        } else if (diffMinutes > 0) {
            return diffMinutes + "m " + diffSeconds + "s";

        } else {
            return diffSeconds + "s";
        }

    }

    public static String getTimeInhms(long totalSecs) {
        // Get msec from each, and subtract.
        long diffSeconds = totalSecs % 60;
        long diffMinutes = (totalSecs % 3600) / 60;
        long diffHours = totalSecs / (3600);

        System.out.println("Time in seconds: " + diffSeconds + " seconds.");
        System.out.println("Time in minutes: " + diffMinutes + " minutes.");
        System.out.println("Time in hours: " + diffHours + " hours.");

        return (diffHours > 9 ? (diffHours) : "0" + diffHours) + ":" +
                (diffMinutes > 9 ? (diffMinutes) : "0" + diffMinutes) + ":" +
                (diffSeconds > 9 ? (diffSeconds) : "0" + diffSeconds);


    }

    public static String timeConvert(int time) {

        int days = time / 24 / 60;
        int hours = time / 60 % 24;
        int mins = time % 60;
        return ((days * 24) + hours) + "h " + mins + "min";
    }

    public static String parseDate(String incomingDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date myDate = null;
        String sMyDate = "";
        try {
            myDate = sdf.parse(incomingDate);
            sdf.applyPattern("EEE, d MMM yyyy, hh:mm a");
            sMyDate = sdf.format(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sMyDate;
    }

    public static String parseDatehm(String incomingDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date myDate = null;
        String sMyDate = "";
        try {
            myDate = sdf.parse(incomingDate);
            sdf.applyPattern("hh:mm a");
            sMyDate = sdf.format(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sMyDate;
    }

    public static boolean checkTechincalTeamTime() {

        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
        int minute = now.get(Calendar.MINUTE);

        Date date = parseDatehhmm(hour + ":" + minute);
        Date dateCompareOne = parseDatehhmm("10:00");
        Date dateCompareTwo = parseDatehhmm("18:00");

        if (dateCompareOne.before(date) && dateCompareTwo.after(date)) {
            //your logic
            return true;
        } else {
            return false;
        }

    }

    private static Date parseDatehhmm(String date) {

        final String inputFormat = "HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            return inputParser.parse(date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }
}
