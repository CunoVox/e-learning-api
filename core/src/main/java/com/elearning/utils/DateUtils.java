package com.elearning.utils;

import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@UtilityClass
@ExtensionMethod(Extensions.class)
public class DateUtils {

    public final static String MINUTE = "minute";
    public final static String HOURLY = "hourly";
    public final static String DAILY = "daily";
    public final static String MONTHLY = "monthly";
    public final static String YEARLY = "yearly";
    public final static String TYPE_DATE_1 = "yyyy-MM-dd";
    public final static String TYPE_DATE_2 = "yyyy-MM-dd hh:mm:ss ZZZ";
    public final static String TYPE_DATE_3 = "yyyy-MM-dd'T'HH:mm:ssZ";
    public final static String TYPE_DATE_4 = "yyyy-MM-dd hh:mm:ss";

    public Date customDate(Date fromDate) {
        if (null != fromDate) {
            SimpleDateFormat sdfd = new SimpleDateFormat("dd");
            int day = Integer.parseInt(sdfd.format(fromDate));
            SimpleDateFormat sdfm = new SimpleDateFormat("MM");
            int month = Integer.parseInt(sdfm.format(fromDate));
            SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");
            int year = Integer.parseInt(sdfy.format(fromDate));

            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            fromDate = cal.getTime();
        }
        return fromDate;
    }

    public int getNumberDaysBetween2Dates(Date start, Date end) {
        if (null == start || null == end) return 0;
        start = customDate(start);
        end = customDate(end);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(start);
        c2.setTime(end);
        double noDay = 1.0 * (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
        noDay = Math.round(noDay);
        return (int) noDay;
    }

    public Date getDateAfterNumberTimes(Date date, int number, String type) {
        Calendar cal = Calendar.getInstance();
        if (null != date) {
            cal.setTimeInMillis(date.getTime());
        }
        if (type.equalsIgnoreCase(MINUTE)) {
            cal.add(Calendar.MINUTE, +number);
        } else if (type.equalsIgnoreCase(HOURLY)) {
            cal.add(Calendar.HOUR, +number);
        } else if (type.equalsIgnoreCase(DAILY)) {
            cal.add(Calendar.DAY_OF_YEAR, +number);
        } else if (type.equalsIgnoreCase(MONTHLY)) {
            cal.add(Calendar.MONTH, +number);
        } else if (type.equalsIgnoreCase(YEARLY)) {
            cal.add(Calendar.YEAR, +number);
        }
        return cal.getTime();
    }

    public Date getDateBeforeNumberTimes(Date date, int number, String type) {
        Calendar cal = Calendar.getInstance();
        if (null != date) {
            cal.setTimeInMillis(date.getTime());
        }
        if (type.equalsIgnoreCase(HOURLY)) {
            cal.add(Calendar.HOUR, -number);
        } else if (type.equalsIgnoreCase(DAILY)) {
            cal.add(Calendar.DAY_OF_YEAR, -number);
        } else if (type.equalsIgnoreCase(MONTHLY)) {
            cal.add(Calendar.MONTH, -number);
        } else if (type.equalsIgnoreCase(YEARLY)) {
            cal.add(Calendar.YEAR, -number);
        }
        return cal.getTime();
    }

    public String convertDateToString(Date date, String type) {
        if (null != date) {
            SimpleDateFormat sdf = new SimpleDateFormat(type);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
            return sdf.format(date);
        }
        return "";
    }

    @SneakyThrows
    public Date convertStringToDate(String dateString, String type) {
        if (!dateString.isBlankOrNull()) {
            SimpleDateFormat sdf = new SimpleDateFormat(type);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
            return sdf.parse(dateString);
        }
        return null;
    }

    public long currentTimestampSecond() {
        return Instant.now().getEpochSecond();
    }

    public static Date convertLongSecondsToDate(long datetime) {
        return new Date(TimeUnit.SECONDS.toMillis(datetime));
    }

    public static String convertDateFormat(String dateFrom, String typeFrom, String typeTo) {
        Date date = convertStringToDate(dateFrom, typeFrom);
        return convertDateToString(date, typeTo);
    }

    public static long convertDateToLongSecond(Date date) {
        return date.getTime() / 1000;
    }
}
