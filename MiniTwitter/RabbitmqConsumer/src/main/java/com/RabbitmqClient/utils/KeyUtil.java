package com.RabbitmqClient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KeyUtil {

    private static final int MS_IN_ONE_MINUTE = 60 * 1000;
    private static final int MS_IN_ONE_HOUR = 60 * MS_IN_ONE_MINUTE;
    private static final int MS_IN_ONE_DAY = 24 * MS_IN_ONE_HOUR;

    private static final int DAYS_TO_KEEP_OLD_DATA = 5;


    public static String getDailyTimestamp(String timestamp) throws ParseException {
        if (timestamp.length() < 8)
            throw new IllegalArgumentException("Input timestamp does not have enough characters.");
        return timestamp.substring(0, 8);
    }

    public static String getHourlyTimestamp(String timestamp) throws ParseException {
        if (timestamp.length() < 10)
            throw new IllegalArgumentException("Input timestamp does not have enough characters.");
        return timestamp.substring(0, 10);
    }


    public static List<String> getDailyTimestampsBetween(String start, String end)
            throws ParseException {
        return getTimestampsBetween(start, end, MS_IN_ONE_DAY, "yyyyMMdd");
    }

    public static List<String> getHourlyTimestampsBetween(String start, String end)
            throws ParseException {
        return getTimestampsBetween(start, end, MS_IN_ONE_HOUR, "yyyyMMddHH");
    }

    public static List<String> getMinutewiseTimestampsBetween(String start, String end)
            throws ParseException {
        return getTimestampsBetween(start, end, MS_IN_ONE_MINUTE, "yyyyMMddHHmm");
    }

    public static List<String> getMinutewiseTimestampsBetween(String start, String end, long interval)
            throws ParseException {
        return getTimestampsBetween(start, end, interval, "yyyyMMddHHmm");
    }

    private static List<String> getTimestampsBetween(String start, String end, long interval, String format)
            throws ParseException {

        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date startDate = dateFormat.parse(start);
        Date endDate = dateFormat.parse(end);

        List<String> result = new ArrayList<String>();
        while (startDate.before(endDate)) {
            Date temp = new Date(startDate.getTime() + interval);
            result.add(dateFormat.format(temp));
            startDate = temp;
        }

        return result;
    }

    public static String floorTimestampToHour(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        long flooredTimestamp = date.getTime() - MS_IN_ONE_HOUR;
        return format.format(new Date(flooredTimestamp));
    }

    public static String floorTimestampToMinute(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        long flooredTimestamp = (date.getTime() / MS_IN_ONE_MINUTE) * MS_IN_ONE_MINUTE;
        return format.format(flooredTimestamp - MS_IN_ONE_MINUTE);
    }

    public static String floorTimestampToSecondMinute(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        long intervalMs = 2 * MS_IN_ONE_MINUTE;
        long flooredTimestamp = (date.getTime() / MS_IN_ONE_MINUTE) * MS_IN_ONE_MINUTE;
        return format.format(flooredTimestamp - intervalMs);
    }


    public static String floorTimestampToFifthMinute(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        long intervalMs = 5 * MS_IN_ONE_MINUTE;
        long flooredTimestamp = (date.getTime() / intervalMs) * intervalMs;
        return format.format(flooredTimestamp - intervalMs);
    }

    public static String floorTimestampToDay(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        long flooredTimestamp = date.getTime() - MS_IN_ONE_DAY;
        return format.format(new Date(flooredTimestamp));
    }


    public static List<String> getKeysForSmallDailyTable(String start, String end)
            throws ParseException {
        List<String> ret = new ArrayList<String>();
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

        if (start.endsWith("55")) {
            long tmp = format.parse(start).getTime() + 10 * MS_IN_ONE_MINUTE;
            start = format.format(new Date(tmp));
        }

        start = start.substring(0, 10) + "55";
        while (start.compareTo(end) < 0) {
            ret.add(start);
            long tmp = format.parse(start).getTime() + 10 * MS_IN_ONE_MINUTE;
            start = format.format(new Date(tmp)).substring(0, 10) + "55";
        }

        return ret;
    }

    public static List<String> getKeysForLargeDailyTable(String start, String end)
            throws ParseException {

        List<String> ret = new ArrayList<String>();
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");

        if (start.endsWith("23")) {
            long tmp = format.parse(start).getTime() + 2 * MS_IN_ONE_HOUR;
            start = format.format(new Date(tmp));
        }

        start = start.substring(0, 8) + "23";
        while (start.compareTo(end) < 0) {
            ret.add(start);
            long tmp = format.parse(start).getTime() + 2 * MS_IN_ONE_HOUR;
            start = format.format(new Date(tmp)).substring(0, 8) + "23";
        }

        return ret;
    }

    public static String getMaxKeyToBeDeleted() {
        Date now = new Date();
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Date result = new Date(now.getTime() - DAYS_TO_KEEP_OLD_DATA * 24 * MS_IN_ONE_HOUR);
        return format.format(result);
    }


    public static String makeStatsDate(String timestamp) {
        return timestamp.substring(0, 8);
    }


    public static int makeHourId(String timestamp) {
        if (timestamp.length() >= 10) {
            return Integer.parseInt(timestamp.substring(8, 10));
        }
        return 0;

    }

    public static int makeMinuteId(String timestamp) {
        if (timestamp.length() >= 12) {
            return Integer.parseInt(timestamp.substring(10, 12));
        }
        return 0;
    }


}