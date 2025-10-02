/**
 * Created by nekos on 14/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.ext;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;

public final class TimeExtensionKt {
    public static final Date today() {
        Date time = Calendar.getInstance().getTime();
        Intrinsics.checkNotNullExpressionValue(time, "getInstance().time");
        return time;
    }

    public static final Date yesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(6, -1);
        Date time = calendar.getTime();
        Intrinsics.checkNotNullExpressionValue(time, "calendar.time");
        return time;
    }

    public static final Date beginDay(Calendar calendar) {
        Intrinsics.checkNotNullParameter(calendar, "<this>");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(calendar.getTimeInMillis());
        calendar2.set(11, 0);
        calendar2.set(12, 0);
        calendar2.set(13, 0);
        calendar2.set(14, 0);
        return calendar2.getTime();
    }

    public static final String convertTime(Context context, long j) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        return new SimpleDateFormat("dd-MM-yyyy").format((Object) new Date(j));
    }

    public static final String milliSecondsToTimer2(String millisecond) {
        String str;
        String sb;
        String valueOf;
        Intrinsics.checkNotNullParameter(millisecond, "millisecond");
        if (Intrinsics.areEqual(millisecond, "null")) {
            return "00:00";
        }
        if (Long.parseLong(millisecond) < 1000) {
            return "00:01";
        }
        long j = 3600000;
        int parseLong = (int) (Long.parseLong(millisecond) / j);
        long j2 = 60000;
        int parseLong2 = (int) ((Long.parseLong(millisecond) % j) / j2);
        long j3 = 1000;
        int parseLong3 = (int) (((Long.parseLong(millisecond) % j) % j2) / j3);
        int parseLong4 = (int) (((Long.parseLong(millisecond) % j) % j2) % j3);
        String str2 = "";
        if (parseLong < 10) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append('0');
            sb2.append(parseLong);
            sb2.append(':');
            str = sb2.toString();
        } else {
            str = "";
        }
        if (parseLong2 < 10) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append('0');
            sb3.append(parseLong2);
            sb3.append(':');
            sb = sb3.toString();
        } else {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(parseLong2);
            sb4.append(':');
            sb = sb4.toString();
        }
        if (parseLong3 < 10) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append('0');
            sb5.append(parseLong3);
            valueOf = sb5.toString();
        } else {
            valueOf = String.valueOf(parseLong3);
        }
        if (parseLong4 > 0) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append('.');
            sb6.append(parseLong4);
            str2 = sb6.toString();
        }
        if (parseLong < 1) {
            return sb + valueOf + str2;
        }
        return str + sb + valueOf + str2;
    }

    public static final String milliSecondsToTimer(String millisecond) {
        String str;
        String sb;
        String valueOf;
        Intrinsics.checkNotNullParameter(millisecond, "millisecond");
        if (Intrinsics.areEqual(millisecond, "null")) {
            return "00:00";
        }
        if (Long.parseLong(millisecond) < 1000) {
            return "00:01";
        }
        long j = 3600000;
        int parseLong = (int) (Long.parseLong(millisecond) / j);
        long j2 = 60000;
        int parseLong2 = (int) ((Long.parseLong(millisecond) % j) / j2);
        int parseLong3 = (int) (((Long.parseLong(millisecond) % j) % j2) / 1000);
        if (parseLong < 10) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append('0');
            sb2.append(parseLong);
            sb2.append(':');
            str = sb2.toString();
        } else {
            str = "";
        }
        if (parseLong2 < 10) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append('0');
            sb3.append(parseLong2);
            sb3.append(':');
            sb = sb3.toString();
        } else {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(parseLong2);
            sb4.append(':');
            sb = sb4.toString();
        }
        if (parseLong3 < 10) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append('0');
            sb5.append(parseLong3);
            valueOf = sb5.toString();
        } else {
            valueOf = String.valueOf(parseLong3);
        }
        if (parseLong < 1) {
            return sb + valueOf;
        }
        return str + sb + valueOf;
    }

    public static final String milliSecondsToFullTimer(long j) {
        String str;
        String sb;
        String valueOf;
        long j2 = 3600000;
        int i = (int) (j / j2);
        long j3 = j % j2;
        long j4 = 60000;
        int i2 = (int) (j3 / j4);
        int i3 = (int) ((j3 % j4) / 1000);
        if (i < 10) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append('0');
            sb2.append(i);
            sb2.append(':');
            str = sb2.toString();
        } else {
            str = "";
        }
        if (i2 < 10) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append('0');
            sb3.append(i2);
            sb3.append(':');
            sb = sb3.toString();
        } else {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(i2);
            sb4.append(':');
            sb = sb4.toString();
        }
        if (i3 < 10) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append('0');
            sb5.append(i3);
            valueOf = sb5.toString();
        } else {
            valueOf = String.valueOf(i3);
        }
        return str + sb + valueOf;
    }

    public static final int milliSecondsToSeconds(Context context, long j) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        return (int) (j / 1000);
    }

    public static final int milliSecondsToSeconds(long j) {
        return (int) (j / 1000);
    }

    public static final String getCurrentTime() {
        String format = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        Intrinsics.checkNotNullExpressionValue(format, "SimpleDateFormat(\"ddMMyy…Default()).format(Date())");
        return format;
    }

    public static final String getTimeFormatFromSecond(String time) {
        String valueOf;
        String valueOf2;
        Intrinsics.checkNotNullParameter(time, "time");
        if (!(time.length() > 0)) {
            return "";
        }
        double d = 60;
        double parseDouble = (Double.parseDouble(time) % 3600) / d;
        if (parseDouble < 10.0d) {
            StringBuilder sb = new StringBuilder();
            sb.append('0');
            sb.append((int) parseDouble);
            valueOf = sb.toString();
        } else {
            valueOf = String.valueOf((int) parseDouble);
        }
        double parseDouble2 = Double.parseDouble(time) % d;
        if (parseDouble2 < 10.0d) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append('0');
            sb2.append((int) parseDouble2);
            valueOf2 = sb2.toString();
        } else {
            valueOf2 = String.valueOf((int) parseDouble2);
        }
        return valueOf + ':' + valueOf2;
    }

    public static final Pair<Integer, Integer> getTimeFormatFromSecondInDialog(String time) {
        Intrinsics.checkNotNullParameter(time, "time");
        double d = 60;
        return new Pair<>(Integer.valueOf((int) ((Double.parseDouble(time) % 3600) / d)), Integer.valueOf((int) (Double.parseDouble(time) % d)));
    }

    private static final String formatDecimal(double d) {
        int i = (int) d;
        int i2 = (int) ((100 * (d - i)) + 0.5d);
        if (i2 >= 100) {
            i++;
            i2 -= 100;
            if (i2 < 10) {
                i2 *= 10;
            }
        }
        if (i2 < 10) {
            return i + ".0" + i2;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append('.');
        sb.append(i2);
        return sb.toString();
    }

    public static final String setTimeCurrent(long j) {
        String valueOf;
        String valueOf2;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(j);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(minutes);
        if (minutes < 10) {
            StringBuilder sb = new StringBuilder();
            sb.append('0');
            sb.append(minutes);
            valueOf = sb.toString();
        } else {
            valueOf = String.valueOf(minutes);
        }
        if (seconds < 10) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append('0');
            sb2.append(seconds);
            valueOf2 = sb2.toString();
        } else {
            valueOf2 = String.valueOf(seconds);
        }
        return valueOf + ':' + valueOf2;
    }

}
