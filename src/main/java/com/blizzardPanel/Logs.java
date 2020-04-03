/**
 * File : Logs.java
 * Desc : Control a logs file in proyect.
 *          Use "log4j2.xml configuration file!"
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import org.apache.logging.log4j.LogManager;

public class Logs {
    //private static UpdateController upControl = null;

    public static void errorLog(Class c, String ms) {
        //if(upControl != null)
        //    upControl.messageForAll("[Error] ["+ c.getName() +"]: " + ms);
        LogManager.getLogger(c.getName()).error(ms);
    }

    public static void fatalLog(Class c, String ms) {
        //if(upControl != null)
        //    upControl.messageForAll("[FATAL] ["+ c.getName() +"]: " + ms);
        LogManager.getLogger(c.getName()).fatal(ms);
    }

    public static void infoLog(Class c, String ms) {
        //if(upControl != null)
        //    upControl.messageForAll("[Info] ["+ c.getName() +"]: " + ms);
        LogManager.getLogger(c.getName()).info(ms);
    }

    public static void warningLog(Class c, String ms) {
        //if (upControl != null) {
        //    upControl.messageForAll("[WARNING] ["+ c.getName() +"]: "+ ms);
        //}
        LogManager.getLogger(c.getName()).info(ms);
    }

    //public static void setUpdateControl(UpdateController up) { upControl = up; }



}