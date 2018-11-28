/**
 * File : GeneralConfig.java
 * Desc : General sistem configuration
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

public interface GeneralConfig 
{    
    //Server information
    public static final String SERVER_LOCATION  = "us";
    public static final String GUILD_NAME       = "Art of War";
    public static final String GUILD_REALM      = "Ragnaros";
    //Web main URL
    public static final String MAIN_URL         = "http://artofwar.cl/";
    public static final String BLIZZAR_LINK     = "blizzLink.jsp";
    //If in page you want all visitor can see all members information, or only guild members
    public static final boolean REQUERID_LOGIN_TO_INFO = true;
    //Logs info
    public static final String LOGS_FILE_PATH   = "/opt/tomcat/logs/";
    
    /**
     * SET AN API ACCESS INFORMATION IN.
     * blizzardAPI/APIInfo.java
     */ 
    /** 
     * AND SET A DATA BASE INFORMATION IN.
     * dbConnect/DBConfig.java
     */
}
