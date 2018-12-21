/**
 * File : MythicPlusControl.java
 * Desc : MythicPlus view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.KeystoneDungeon.KeystoneDungeonRun;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class MythicPlusControl 
{    
    //Variable
    private final DBConnect dbConnect;
    private KeystoneDungeonRun[] keyRun;
    private Date lastKeyRunUpdate;

    public MythicPlusControl()
    {
        dbConnect = new DBConnect();
    }
    
    private void loadKeyRun()
    {
        try {
            JSONArray keyListInDb = dbConnect.select(
                    KeystoneDungeonRun.KEYSTONE_DUNGEON_RUN_TABLE_NAME,
                    new String[] {"id"},
                    "1=? order by `completed_timestamp` desc",
                    new String[] {"1"});
            this.keyRun = new KeystoneDungeonRun[keyListInDb.size()];
            for(int i = 0; i < keyListInDb.size(); i++)
            {
                this.keyRun[i] = new KeystoneDungeonRun((Integer) ((JSONObject) keyListInDb.get(i)).get("id"));
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(MythicPlusControl.class, "Fail to get last keystone - "+ ex);
        }
        lastKeyRunUpdate = new Date();        
    }
    
    public KeystoneDungeonRun[] getLastKeyRun() 
    {         
        if(this.keyRun == null)
            loadKeyRun();
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastKeyRunUpdate.compareTo(tenMinuteAgo) < 0)
            {
                loadKeyRun();
            }
        }
        return this.keyRun;
    }
    
}