package com.gypsee.sdk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtiliser {
  public static   String getDateinddMMMYY(String date1) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
            Date date = null;
            date = format1.parse(date1);
            return format2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
}
