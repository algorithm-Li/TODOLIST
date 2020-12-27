package com.example.todolist.Utils;

import android.text.format.DateUtils;

import java.util.concurrent.TimeUnit;

/**
 * 时间格式化工具
 * @author Algorithm
 */
public class TimeFormatUtil {
    public static String formatTime(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        return DateUtils.formatElapsedTime(new StringBuilder(8), seconds);
    }
}
