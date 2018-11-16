/**
 * File : Logs.java
 * Desc : Control a logs file in proyect.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar;

import com.artOfWar.viewController.UpdateControl;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logs {
    
    public static final String LOG_FILE = "/opt/tomcat/logs/artOfWarUpdate.log";
    private static UpdateControl upControl = null;
    
    public static void saveLog(String s) { saveLog(s, true); }
    public static void saveLog(String s, boolean nline)
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
            BufferedWriter out = new BufferedWriter(new FileWriter(LOG_FILE, true));
            out.write(wS);
            out.close();
        } catch (IOException ex) {
            System.out.println("Fail to save log! "+ ex);
        }
    }
    
    public static void publicLog(UpdateControl up) { upControl = up; }

}
