package com.SmartAir.dailycheckin.presenter;

import java.text.SimpleDateFormat;

/**
 * Utility class used in DailyCheckInPresenter and HistoryActivity to get current date.
 */
public class Date {

    public static String getCurrentDate() {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(now);
    }
}
