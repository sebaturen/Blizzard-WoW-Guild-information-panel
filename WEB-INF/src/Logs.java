/**
 * File : Logs.java
 * Desc : Control a logs file in proyect.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import com.blizzardPanel.viewController.UpdateControl;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs 
{
    
    public static final String LOG_FILE_PREFIX = GeneralConfig.LOGS_FILE_PATH + GeneralConfig.GUILD_NAME;
    public static final String LOG_FILE_EXTENCION = "Update.log";
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
            try (BufferedWriter out = new BufferedWriter(getFile())) {
                out.write(wS);
            }
        } catch (IOException ex) {
            System.out.println("Fail to save log! "+ ex);
        }
    }
    
    private static FileWriter getFile() throws IOException
    {
        FileWriter logFile = new FileWriter(getFileLogPath(), true);
        //Set file owner!
        Path path = Paths.get(getFileLogPath());
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(GeneralConfig.LOGS_FILE_USER_OWNER);
        Files.setOwner(path, userPrincipal);

        return logFile;        
    }
    
    private static String getFileLogPath()
    {
        return LOG_FILE_PREFIX +"."+ getCurrentTimeStamp() +"."+ LOG_FILE_EXTENCION;
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
