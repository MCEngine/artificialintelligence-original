package io.github.mcengine.common.artificialintelligence.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FunctionCallingLoaderUtilTime {

    public static String getFormattedTime(TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }

    public static String getFormattedTime(String zoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(zoneId));
        return sdf.format(new Date());
    }

    public static String getZoneLabel(String prefix, int hour, int minute) {
        String sign = hour >= 0 ? "plus" : "minus";
        int absHour = Math.abs(hour);
        return String.format("{time_%s_%s_%02d_%02d}", prefix, sign, absHour, minute);
    }
}
