package com.example.base_project.util;

import java.util.concurrent.TimeUnit;

public class DurationFormatter {
    public static String format(int i) {
        long j = (long) i;
        int hours = (int) TimeUnit.MILLISECONDS.toHours(j);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(j) % 60);
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(j) % 60);
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds));
        }
        return String.format("%02d:%02d", Integer.valueOf(minutes), Integer.valueOf(seconds));
    }
}
