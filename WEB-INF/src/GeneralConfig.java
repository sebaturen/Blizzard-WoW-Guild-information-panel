/**
 * File : GeneralConfig.java
 * Desc : General sistem configuration
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import com.blizzardPanel.exceptions.ConfigurationException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GeneralConfig 
{    
    //Server information
    public static String getStringConfig(String properity) throws ConfigurationException
    {
        try {
            return(String) new InitialContext().lookup("java:comp/env/"+ properity);
        } catch (NamingException ex) {
            throw new ConfigurationException("NOT CORRECT CONFIGURATION! - "+ properity);
        }
    }
    
    public static int getINTConfig(String properity) throws ConfigurationException
    {
        try {
            return(Integer) new InitialContext().lookup("java:comp/env/"+ properity);
        } catch (NamingException ex) {
            throw new ConfigurationException("NOT CORRECT CONFIGURATION! - "+ properity);
        }        
    }
    
    public static boolean getBooleanConfig(String properity) throws ConfigurationException
    {
        try {
            return(Boolean) new InitialContext().lookup("java:comp/env/"+ properity);
        } catch (NamingException ex) {
            throw new ConfigurationException("NOT CORRECT CONFIGURATION! - "+ properity);
        }        
    }
}
