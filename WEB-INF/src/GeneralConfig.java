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
     * "en_US": "Horde",
     * "es_MX": "Horda",
     * "pt_BR": "Horda",
     * "de_DE": "Horde",
     * "es_ES": "Horda",
     * "fr_FR": "Horde",
     * "it_IT": "Orda",
     * "pt_PT": "Horda",
     * "ru_RU": "Орда",
     * "ko_KR": "호드",
     * "zh_TW": "部落",
     * "zh_CN": "部落"
     */
    public static final String LENGUAJE_API_LOCALE  = "en_US";
    //Web main URL
    public static final String MAIN_URL         = "http://artofwar.cl/";
    public static final String BLIZZAR_LINK     = "blizzLink.jsp";
    //If in page you want all visitor can see all members information, or only guild members
    public static final boolean REQUERID_LOGIN_TO_INFO = true;
    //Logs info
    public static final String LOGS_FILE_PATH   = "/opt/tomcat/logs/";
    public static final String LOGS_FILE_USER_OWNER = "tomcat";
    
    /**
     * SET AN API ACCESS INFORMATION IN.
     * blizzardAPI/APIInfo.java
     */ 
    
    //Aplication Info
    public static final String CLIENT_ID        = "";
    public static final String CLIENT_SECRET    = "";
    /** 
     * AND SET A DATA BASE INFORMATION IN.
     * dbConnect/DBConfig.java
     */
    
    //DB information
    public static final String DB_NAME      = "";
    public static final String DB_USER      = "";
    public static final String DB_PASSWORD  = "";
    
}
