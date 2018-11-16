/**
 * File : GameInfo.java
 * Desc : Get a last game info from DB
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.Logs;
import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.dbConnect.DBStructure;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameInfo 
{
    private final DBConnect dbConnect = new DBConnect();

    public String getLastDynamicUpdate()
    {
        String out = "";
        try
        {		
            JSONArray dateUpdate = dbConnect.select(DBStructure.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {"0"});
            if (dateUpdate.size() > 0)
            {
                out += (((JSONObject)dateUpdate.get(0)).get("update_time")).toString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLog("Fail to get a last dynamic update");
        }
        return out;
    }

    public String getLastStaticUpdate()
    {
        String out = "";
        try
        {		
            JSONArray dateUpdate = dbConnect.select(DBStructure.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {"1"});
            if (dateUpdate.size() > 0)
            {
                out += (((JSONObject)dateUpdate.get(0)).get("update_time")).toString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLog("Fail to get a last dynamic update");
        }
        return out;
    }
    
    public int[] getTokenWow()
    {
        int[] out = new int[3]; //[0-gold][1-silver][2-copper]
        
        try
        {
            JSONArray dateUpdate = dbConnect.select(DBStructure.WOW_TOKEN_TABLE_NAME,
                                                    new String[] {"price"},
                                                    "1=? order by "+ DBStructure.WOW_TOKEN_TABLE_KEY +" desc limit 1",
                                                    new String[] {"1"});
            if (dateUpdate.size() > 0)
            {
                String actuapPrice = (((JSONObject)dateUpdate.get(0)).get("price")).toString();
                out[0] = Integer.parseInt(actuapPrice.substring(0,actuapPrice.length()-4));
                out[1] = Integer.parseInt(actuapPrice.substring(actuapPrice.length()-4,actuapPrice.length()-2));
                out[2] = Integer.parseInt(actuapPrice.substring(actuapPrice.length()-2,actuapPrice.length()));
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLog("Fail to get a wow Token price");
        }
        return out;
    }
}