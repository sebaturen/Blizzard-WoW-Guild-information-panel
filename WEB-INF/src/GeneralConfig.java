/**
 * File : GeneralConfig.java
 * Desc : General sistem configuration
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

public interface GeneralConfig 
{    
    //Server information
    public static final String GUILD_NAME       = "Art of War";
    public static final String GUILD_REALM      = "Ragnaros";
    public static final String SERVER_LOCATION  = "us";
    /**
     * LOCALES:
     * "en_US", "es_MX", "pt_BR", "de_DE", "es_ES", "fr_FR", "it_IT",
     * "pt_PT", "ru_RU", "ko_KR", "zh_TW", "zh_CN"
     */
    public static final String LENGUAJE_API_LOCALE  = "en_US";
    //Web main URL
    public static final String MAIN_URL         = "http://artofwar.cl/";
    public static final String BLIZZAR_LINK     = "blizzLink.jsp";
    //If in page you want all visitor can see all members information, or only guild members
    public static final boolean REQUERID_LOGIN_TO_INFO = true;
    //Logs info
    public static final String LOGS_FILE_PATH       = "/opt/tomcat/logs/";
    public static final String LOGS_FILE_USER_OWNER = "tomcat";
    
    /**
     * SET AN API ACCESS INFORMATION IN.
     * blizzardAPI/APIInfo.java
     */ 
    
    //Aplication Info
    public static final String CLIENT_ID        = "";
    public static final String CLIENT_SECRET    = "";
}
