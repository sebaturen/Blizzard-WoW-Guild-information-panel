/**
 * File : GeneralConfig.java
 * Desc : General sistem configuration
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GeneralConfig 
{    
    //Server information
    public static String getStringConfig(String properity)
    {
        try {
            String val = (String) new InitialContext().lookup("java:comp/env/"+ properity);
            if(val.length() > 0)
            {
                return val;                
            }
            else
            {                        
                Logs.fatalLog(GeneralConfig.class, "FAIL IN CONFIGURATION! ("+ properity +")");
                System.exit(-1);
                return null;
            }
        } catch (NamingException ex) {            
            Logs.fatalLog(GeneralConfig.class, "FAIL IN CONFIGURATION! ("+ properity +") "+ ex);
            System.exit(-1);
            return null;
        }
        
    }
    
    public static int getIntConfig(String properity)
    {
        try {
            return(Integer) new InitialContext().lookup("java:comp/env/"+ properity);
        } catch (NamingException ex) {
            Logs.fatalLog(GeneralConfig.class, "FAIL IN CONFIGURATION! ("+ properity +") "+ ex);
            System.exit(-1);
            return -1;
        }        
    }
    
    public static boolean getBooleanConfig(String properity)
    {
        try {
            return(Boolean) new InitialContext().lookup("java:comp/env/"+ properity);
        } catch (NamingException ex) {
            Logs.fatalLog(GeneralConfig.class, "FAIL IN CONFIGURATION! ("+ properity +") "+ ex);
            System.exit(-1);
            return false;
        }        
    }
}
