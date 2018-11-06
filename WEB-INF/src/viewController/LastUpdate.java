/**
 * File : LastUpdate.java
 * Desc : Get a last DB update
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.dbConnect.DBConnect;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author 세바
 */
public class LastUpdate {
    
    private final DBConnect dbConnect = new DBConnect();
    
    public String getLastDynamicUpdate()
    {
        String out = "";
        try
        {		
            JSONArray dateUpdate = dbConnect.select("update_timeline", 
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
            System.out.println("Fail to get a last dynamic update");
        }
        return out;
    }
    
    public String getLastStaticUpdate()
    {
        String out = "";
        try
        {		
            JSONArray dateUpdate = dbConnect.select("update_timeline", 
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
            System.out.println("Fail to get a last dynamic update");
        }
        return out;
    }
}
