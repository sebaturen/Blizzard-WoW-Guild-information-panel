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

public class FactionAssaultControl
{
    
    public static final String START_PST = "2019-4-14 18:00:00";
    public static final String START_MST = "";
    public static final String START_CST = "";
    public static final String START_EST = "";
    public static final int INTERVAL_HOUR = 19;
    public static final int DURATION_HOUR = 7;
    
    //Atribute
    private final String startTime;
    public FactionAssaultControl()
    {        
        switch(ServerTime.TIME_ZONE)
        {
            case "MST": this.startTime = START_MST; break;
            case "CST": this.startTime = START_CST; break;
            case "EST": this.startTime = START_EST; break;
            default: this.startTime = START_PST; break;                
        }
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
        Date serverTime = ServerTime.getServerTime();
        //serverTime.compareTo(lastAssault)); //1
        //serverTime.compareTo(lastAssaultFinish)); //-1
        return (serverTime.compareTo(lastAssault) > 0 && serverTime.compareTo(lastAssaultFinish) < 0);
    }
    
    //[hours][minute]
    public int[] getTimeRemaining(Date assault)
    {
        //Current time in server
        Date serverTime = ServerTime.getServerTime();
        long diffTime = serverTime.getTime() - assault.getTime();
        
        //Calcule remaining
        int[] timeRemaining = new int[2];
        timeRemaining[0] = (int) Math.abs((diffTime / (60 * 60 * 1000)));
        timeRemaining[1] = (int) Math.abs((diffTime / (60 * 1000) % 60));
        
        return timeRemaining;        
    }
    
    public int[] getTimeRemainingCurrentAssault( )
    {
        //Current time in server
        Date serverTime = ServerTime.getServerTime();
        Calendar cTime = Calendar.getInstance();
        cTime.setTime(getPrevieAssault());
        cTime.add(Calendar.HOUR_OF_DAY, DURATION_HOUR);
        //Calc diference
        long diffTime = cTime.getTime().getTime() - serverTime.getTime();
        
        //Calcule remaining
        int[] timeRemaining = new int[2];
        timeRemaining[0] = (int) Math.abs((diffTime / (60 * 60 * 1000)));
        timeRemaining[1] = (int) Math.abs((diffTime / (60 * 1000) % 60));
        
        return timeRemaining;
    }
    
    public Date getPrevieAssault()
    {
        //Get NEXT assault
        Date nextAssault = getNextAssault();
        Calendar previewAssault = Calendar.getInstance();
        previewAssault.setTime(nextAssault);
        //-19h Interval
        previewAssault.add(Calendar.HOUR, -INTERVAL_HOUR);
        return previewAssault.getTime();     
    }
    
    public Date getNextAssault()
    {
        try {
            //Current time in server
            Date serverTime = ServerTime.getServerTime();
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

}