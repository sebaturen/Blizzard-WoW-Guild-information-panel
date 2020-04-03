/**
 * File : ServerTime.java
 * Desc : ServerTime controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.Logs;
import com.blizzardPanel.update.blizzard.WoWAPIService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class ServerTime {

    //Constant
    public static final String PST_TIME_ZONE = "America/Los_Angeles";
    public static final String MST_TIME_ZONE = "";
    public static final String CST_TIME_ZONE = "";
    public static final String EST_TIME_ZONE = "";
    public static final String TIME_ZONE = "PST";
    private static ServerTime svObject;
    public static final int MAINTENANCE_DAY = Calendar.TUESDAY;
    public static final int MAINTENANCE_HOUR = 7;

    //Attribute
    private final String timeZone;

    public ServerTime() {
        switch(ServerTime.TIME_ZONE) {
            case "MST": this.timeZone = MST_TIME_ZONE; break;
            case "CST": this.timeZone = CST_TIME_ZONE; break;
            case "EST": this.timeZone = EST_TIME_ZONE; break;
            default: this.timeZone = PST_TIME_ZONE; break;
        }
    }

    /**
     *  Get current server time.
     * @return Date time
     */
    public static Date getServerTime() {
        Date currentTime = null;
        if(svObject == null) svObject = new ServerTime();
        try {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeZone(TimeZone.getTimeZone(svObject.getTimeZone()));
            currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    currentCalendar.get(Calendar.YEAR) +"-"+
                            (currentCalendar.get(Calendar.MONTH)+1) +"-"+ //calendar object start month in 0
                            currentCalendar.get(Calendar.DAY_OF_MONTH) +" "+
                            currentCalendar.get(Calendar.HOUR_OF_DAY) +":"+
                            currentCalendar.get(Calendar.MINUTE) +":"+
                            currentCalendar.get(Calendar.SECOND));
        } catch (ParseException ex) {
            Logs.errorLog(ServerTime.class, "Failed to get current time");
        }
        return currentTime;
    }

    /**
     * Get last maintenance time in long
     *
     * @return
     */
    public static long getLastResetTime() {
        Date sTime = getServerTime();
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(sTime);

        if (sCalendar.get(Calendar.DAY_OF_WEEK) > MAINTENANCE_DAY) {
            return getCurrentMaintenance(sCalendar);
        } else if (sCalendar.get(Calendar.DAY_OF_WEEK) == MAINTENANCE_DAY) {
            if (sCalendar.get(Calendar.HOUR_OF_DAY) >= MAINTENANCE_HOUR) {
                return getCurrentMaintenance(sCalendar);
            } else {
                return getPreviewMaintenance(sCalendar);
            }
        } else {
            return getPreviewMaintenance(sCalendar);
        }
    }

    private static long getPreviewMaintenance(Calendar serverCalendar) {
        do {
            serverCalendar.add(Calendar.DAY_OF_MONTH, -1);
        } while (serverCalendar.get(Calendar.DAY_OF_WEEK) != MAINTENANCE_DAY);
        serverCalendar.set(Calendar.HOUR_OF_DAY, MAINTENANCE_HOUR);
        return serverCalendar.getTimeInMillis();

    }

    private static long getCurrentMaintenance(Calendar serverCalendar) {
        serverCalendar.set(Calendar.DAY_OF_WEEK, MAINTENANCE_DAY);
        serverCalendar.set(Calendar.HOUR_OF_DAY, MAINTENANCE_HOUR);
        return serverCalendar.getTimeInMillis();
    }

    private String getTimeZone() {
        return this.timeZone;
    }

}
