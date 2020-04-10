package com.blizzardPanel;

import com.blizzardPanel.gameObject.characters.CharacterMember;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GeneralConfig {

    // Server information
    public static String getStringConfig(String property) {
        try {
            String val = (String) new InitialContext().lookup("java:comp/env/"+ property);
            if(val.length() > 0) {
                return val;
            } else {
                Logs.fatalLog(GeneralConfig.class, "FAILED IN CONFIGURATION! ("+ property +")");
                System.exit(-1);
                return null;
            }
        } catch (NamingException ex) {
            Logs.fatalLog(GeneralConfig.class, "FAILED IN CONFIGURATION! ("+ property +") "+ ex);
            System.exit(-1);
            return null;
        }
    }

    public static int getIntConfig(String property) {
        try {
            return(Integer) new InitialContext().lookup("java:comp/env/"+ property);
        } catch (NamingException ex) {
            Logs.fatalLog(GeneralConfig.class, "FAILED IN CONFIGURATION! ("+ property +") "+ ex);
            System.exit(-1);
            return -1;
        }
    }

    public static boolean getBooleanConfig(String property) {
        try {
            return(Boolean) new InitialContext().lookup("java:comp/env/"+ property);
        } catch (NamingException ex) {
            Logs.fatalLog(GeneralConfig.class, "FAILED IN CONFIGURATION! ("+ property +") "+ ex);
            System.exit(-1);
            return false;
        }
    }

    public static String getDateFormat(String locale) {
        switch (locale) {
            case "es_MX":
                return "HH:mm - dd/MM/yyyy";
            default:
                return "HH:mm - MM/dd/yyyy";
        }
    }

    public static CharacterMember getMember(long charId) {
        return new CharacterMember.Builder(charId).fullLoad(true).build();
    }
}
