/**
 * File : GameInfo.java
 * Desc : Get a last game info from DB
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.update.blizzard.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.WoWToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GameInfo
{
    private final DBConnect dbConnect = new DBConnect();
    private static boolean configState = false;
    private String lastDynamicUpdate;
    private Date lastDynamicUpdateUpdate;
    private WoWToken outWoWToken;
    private Date lastWoWTokenUpdate;
    private List<WoWToken> outWoWTokenHistory;
    private Date lastWoWTokenHistoryUpdate;

    public GameInfo()
    {
        dbConnect.connectionVerification();
        getWowTokenValue();
    }

    private void loadLastDynamicUpdate()
    {
        try
        {
            JsonArray dateUpdate = dbConnect.select(Update.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {Update.UPDATE_TYPE_DYNAMIC +""});
            if (dateUpdate.size() > 0)
            {
                this.lastDynamicUpdate = dateUpdate.get(0).getAsJsonObject().get("update_time").getAsString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(GameInfo.class, "Fail to get a last dynamic update");
        }
        this.lastDynamicUpdateUpdate = new Date();
    }

    public String getLastDynamicUpdate()
    {
        if(this.lastDynamicUpdate == null)
            loadLastDynamicUpdate();
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastDynamicUpdateUpdate.compareTo(tenMinuteAgo) < 0)
            {
                loadLastDynamicUpdate( );
            }
        }
        return this.lastDynamicUpdate;
    }

    public String getLastStaticUpdate()
    {
        String out = "";
        try
        {
            JsonArray dateUpdate = dbConnect.select(Update.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {Update.UPDATE_TYPE_STATIC +""});
            if (dateUpdate.size() > 0)
            {
                out += dateUpdate.get(0).getAsJsonObject().get("update_time").getAsString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(GameInfo.class, "Fail to get a last dynamic update");
        }
        return out;
    }

    private void getWowTokenValue()
    {
        try
        {
            JsonArray dateUpdate = dbConnect.select(DBStructure.WOW_TOKEN_TABLE_NAME,
                                                    new String[] {"price"},
                                                    "1=? order by "+ DBStructure.WOW_TOKEN_TABLE_KEY +" desc limit 1",
                                                    new String[] {"1"});
            if (dateUpdate.size() > 0)
            {
                this.outWoWToken = new WoWToken(dateUpdate.get(0).getAsJsonObject().get("price").getAsLong());
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(GameInfo.class, "Fail to get a wow Token price");
        }
        lastWoWTokenUpdate = new Date();
    }

    public WoWToken getWoWToken()
    {
        if(this.outWoWToken == null)
        {
            getWowTokenValue( );
        }
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastWoWTokenUpdate.compareTo(tenMinuteAgo) < 0)
            {
                getWowTokenValue( );
            }
        }
        return this.outWoWToken;
    }

    public void getWoWTokenHistoryValue(int count)
    {
        this.outWoWTokenHistory = new ArrayList<>();
        try
        {
            JsonArray dateUpdate = dbConnect.select(DBStructure.WOW_TOKEN_TABLE_NAME,
                    new String[] {"last_updated_timestamp", "price"},
                    "1=? order by "+ DBStructure.WOW_TOKEN_TABLE_KEY +" desc limit "+ count,
                    new String[] {"1"});
            if (dateUpdate.size() > 0)
            {
                for(Object wToken : dateUpdate) {
                    WoWToken wT = new WoWToken((((JsonObject)wToken).get("price").getAsLong()));
                    wT.setLastUpdate(Long.parseLong(((JsonObject)wToken).get("last_updated_timestamp").getAsString()));
                    this.outWoWTokenHistory.add(wT);
                }
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(GameInfo.class, "Fail to get a wow Token price "+ e);
        }
        lastWoWTokenHistoryUpdate = new Date();
    }

    public List<WoWToken> getWoWTokenHistory() { return getWoWTokenHistory(5); }
    public List<WoWToken> getWoWTokenHistory(int count)
    {
        if (count < 5) count = 5;
        if(this.outWoWTokenHistory == null)
        {
            getWoWTokenHistoryValue(count);
        }
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastWoWTokenHistoryUpdate.compareTo(tenMinuteAgo) < 0)
            {
                getWoWTokenHistoryValue(count);
            }
        }
        return this.outWoWTokenHistory;
    }

    public boolean getSystemStatus()
    {
        //Valid all config have a value
        if(!configState)
        {
            //If any config have an error, the GeneralConfig.java break the system (System.exit(-1));
            //Guild information
            GeneralConfig.getStringConfig("GUILD_NAME");
            GeneralConfig.getStringConfig("GUILD_REALM");
            GeneralConfig.getStringConfig("SERVER_LOCATION");
            //Locales:
            GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE");
            //Web main URL
            GeneralConfig.getStringConfig("BLIZZAR_LINK");
            GeneralConfig.getStringConfig("BLIZZAR_LINK");
            boolean v = GeneralConfig.getBooleanConfig("REQUERID_LOGIN_TO_INFO"); //bolean!
            //Blizzard API
            GeneralConfig.getStringConfig("CLIENT_ID");
            GeneralConfig.getStringConfig("CLIENT_SECRET");
            configState = true;
        }
        return dbConnect.connectionVerification();
    }
}
