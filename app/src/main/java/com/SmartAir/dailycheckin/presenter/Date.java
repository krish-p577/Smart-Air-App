package com.SmartAir.dailycheckin.presenter;

import java.text.SimpleDateFormat;

public class Date {
    public static String getCurrentDate() {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(now);
    }
}
