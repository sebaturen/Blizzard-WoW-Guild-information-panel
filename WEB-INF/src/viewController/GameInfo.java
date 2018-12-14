/**
 * File : GameInfo.java
 * Desc : Get a last game info from DB
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.AuctionItem;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameInfo 
{
    private final DBConnect dbConnect = new DBConnect();
    private String lastDynamicUpdate;
    private Date lastDynamicUpdateUpdate;
    private int[] outWowToken;
    private Date lastWowTokenUpdate;
    
    public GameInfo()
    {
        dbConnect.connectionVerification();
        getWowTokenValue();
    }
    
    private void loadLastDynamicUpdate()
    {
        try
        {		
            JSONArray dateUpdate = dbConnect.select(Update.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {Update.UPDATE_DYNAMIC +""});
            if (dateUpdate.size() > 0)
            {
                this.lastDynamicUpdate = (((JSONObject)dateUpdate.get(0)).get("update_time")).toString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLogln("Fail to get a last dynamic update");
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
            JSONArray dateUpdate = dbConnect.select(Update.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {Update.UPDATE_STATIC +""});
            if (dateUpdate.size() > 0)
            {
                out += (((JSONObject)dateUpdate.get(0)).get("update_time")).toString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLogln("Fail to get a last dynamic update");
        }
        return out;
    }
    
    private void getWowTokenValue()
    {
        this.outWowToken = new int[3]; //[0-gold][1-silver][2-copper]
        try
        {
            JSONArray dateUpdate = dbConnect.select(DBStructure.WOW_TOKEN_TABLE_NAME,
                                                    new String[] {"price"},
                                                    "1=? order by "+ DBStructure.WOW_TOKEN_TABLE_KEY +" desc limit 1",
                                                    new String[] {"1"});
            if (dateUpdate.size() > 0)
            {
                String actuapPrice = (((JSONObject)dateUpdate.get(0)).get("price")).toString();
                this.outWowToken = AuctionItem.dividePrice(Long.parseLong(actuapPrice));
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLogln("Fail to get a wow Token price");
        }
        lastWowTokenUpdate = new Date();
    }
    
    public int[] getTokenWow() 
    {
        if(this.outWowToken == null)
        {
            getWowTokenValue( );        
        }
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastWowTokenUpdate.compareTo(tenMinuteAgo) < 0)
            {
                getWowTokenValue( );
            }            
        }
        return this.outWowToken;
    }
    
    public boolean getDBStatus() { return dbConnect.connectionVerification(); }
}