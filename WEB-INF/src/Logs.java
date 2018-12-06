/**
 * File : Logs.java
 * Desc : Control a logs file in proyect.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import com.blizzardPanel.viewController.UpdateControl;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs 
{
    
    public static final String LOG_FILE_PREFIX = GeneralConfig.LOGS_FILE_PATH + GeneralConfig.GUILD_NAME;
    public static final String LOG_FILE_EXTENCION = "-Update.log";
    private static UpdateControl upControl = null;
    
    public static void saveLogln(String s) { saveLog(s, true); }
    public static void saveLog(String s) { saveLog(s, false); }
    private static void saveLog(String s, boolean nline)
    {
        try
        {
            String wS = s;
            System.out.print(wS);            
            if(nline)
            {
                wS += "\n";
                System.out.println("");
            }
            if(upControl != null) upControl.onMessage(wS, null);            
            //Save in file
            try (BufferedWriter out = new BufferedWriter(new FileWriter(getFailLog(), true))) {
                out.write(wS);
            }
        } catch (IOException ex) {
            System.out.println("Fail to save log! "+ ex);
        }
    }
    
    private static String getFailLog()
    {
        return LOG_FILE_PREFIX+getCurrentTimeStamp()+LOG_FILE_EXTENCION;
    }
    
    public static void publicLog(UpdateControl up) { upControl = up; }
    
    /**
     * Get a current time string yyyy-MM-dd
     * @return 
     */
    public static String getCurrentTimeStamp() 
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

}
