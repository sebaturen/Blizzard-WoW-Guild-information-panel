/**
 * File : FactionAssaultControl.java
 * Desc : FactionAssaultControl controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.Logs;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FactionAssaultControl
{
    //Constante
    public static final String START_PST = "2018-12-12 3:00:00";
    public static final String START_MST = "2018-12-12 4:00:00";
    public static final String START_CST = "2018-12-12 5:00:00";
    public static final String START_EST = "2018-12-12 6:00:00";
    public static final String PST_TIME_ZONE = "America/Los_Angeles";
    public static final String MST_TIME_ZONE = "";
    public static final String CST_TIME_ZONE = "";
    public static final String EST_TIME_ZONE = "";
    
    public static final int INTERVAL_HOUR = 19;
    public static final int DURATION_HOUR = 7;
    
    //Atribute
    private String startTime;
    private String timeZone;
    
    public FactionAssaultControl()
    {
        this.startTime = START_PST;
        this.timeZone = PST_TIME_ZONE;
    }
    
    public boolean isCurrent()
    {
        //Start last assault
        Date lastAssault = getPrevieAssault();
        //End last assault
        Calendar finAssault = Calendar.getInstance();
        finAssault.setTime(lastAssault);
        finAssault.add(Calendar.HOUR, DURATION_HOUR);
        Date lastAssaultFinish = finAssault.getTime();
        //Current time in server
        Date serverTime = getServerTime();
        //serverTime.compareTo(lastAssault)); //1
        //serverTime.compareTo(lastAssaultFinish)); //-1
        return (serverTime.compareTo(lastAssault) > 0 && serverTime.compareTo(lastAssaultFinish) < 0);
    }
    
    //[hours][minute]
    public int[] getTimeRemaining(Date assault)
    {
        //Current time in server
        Date serverTime = getServerTime();
        long diffTime = serverTime.getTime() - assault.getTime();
        
        //Calcule remaining
        int[] timeRemaining = new int[2];
        timeRemaining[0] = (int) Math.abs((diffTime / (60 * 60 * 1000)));
        timeRemaining[1] = (int) Math.abs((diffTime / (60 * 1000) % 60));
        
        return timeRemaining;        
    }
    
    public Date getPrevieAssault()
    {
        try {
            //Current time in server
            Date serverTime = getServerTime();
            //base start time
            Date startTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.startTime);
            Calendar startTimeC = Calendar.getInstance();
            startTimeC.setTime(startTimeDate);
            //valid if is old...
            do {
                //Add interval assoult
                startTimeC.add(Calendar.HOUR, INTERVAL_HOUR);
            } while(serverTime.compareTo(startTimeC.getTime()) > 0);
            startTimeC.add(Calendar.HOUR, -INTERVAL_HOUR); //remove time
            return startTimeC.getTime();
        } catch (ParseException ex) {
            Logs.errorLog(FactionAssaultControl.class, "Fail to convert start time assault");
        }
        return null;        
    }
    
    public Date getNextAssault()
    {
        try {
            //Current time in server
            Date serverTime = getServerTime();
            //base start time
            Date startTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.startTime);
            Calendar startTimeC = Calendar.getInstance();
            startTimeC.setTime(startTimeDate);
            //valid if is old...
            do {
                //Add interval assoult
                startTimeC.add(Calendar.HOUR_OF_DAY, INTERVAL_HOUR);
            } while(serverTime.compareTo(startTimeC.getTime()) > 0);
            return startTimeC.getTime();
        } catch (ParseException ex) {
            Logs.errorLog(FactionAssaultControl.class, "Fail to convert start time assault");
        }
        return null;
    }
    
    public Date getServerTime()
    {
        Date currentTime = null;
        try {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeZone(TimeZone.getTimeZone(this.timeZone));
            currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    currentCalendar.get(Calendar.YEAR) +"-"+
                    (currentCalendar.get(Calendar.MONTH)+1) +"-"+ //calendar object start month in 0
                    currentCalendar.get(Calendar.DAY_OF_MONTH) +" "+
                    currentCalendar.get(Calendar.HOUR_OF_DAY) +":"+
                    currentCalendar.get(Calendar.MINUTE) +":"+
                    currentCalendar.get(Calendar.SECOND));
        } catch (ParseException ex) {
            Logs.errorLog(FactionAssaultControl.class, "Fail to get current time");
        }
        return currentTime;
    }
}