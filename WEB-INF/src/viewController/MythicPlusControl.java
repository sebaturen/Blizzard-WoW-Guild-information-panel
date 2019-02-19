/**
 * File : MythicPlusControl.java
 * Desc : MythicPlus view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeonRun;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class MythicPlusControl 
{    
    //public static final String WEEK_CHANGE_FIRST_TIME;
    public static final int RESET_DAY = Calendar.THURSDAY; //dey of week
    
    //Variable
    private final DBConnect dbConnect;
    private KeystoneDungeonRun[] keyBestRun;
    private Date lastKeyBestRunUpdate;
    private KeystoneDungeonRun[] keyThisWeek;
    private Date lastKeyThisWeekUpdate;

    public MythicPlusControl()
    {
        dbConnect = new DBConnect();
    }
    
    private void loadBestRun()
    {
        try {
            JSONArray keyListInDb = dbConnect.select(
                    KeystoneDungeonRun.KEYSTONE_DUNGEON_RUN_TABLE_NAME,
                    new String[] {"id"},
                    "is_complete_in_time=? AND completed_timestamp > ? order by keystone_level DESC, completed_timestamp DESC limit 3",
                    new String[] {"1", "1548633770000"});
            this.keyBestRun = new KeystoneDungeonRun[keyListInDb.size()];
            for(int i = 0; i < keyListInDb.size(); i++)
            {
                this.keyBestRun[i] = new KeystoneDungeonRun((Integer) ((JSONObject) keyListInDb.get(i)).get("id"));
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(MythicPlusControl.class, "Fail to get last keystone - "+ ex);
        }
        lastKeyBestRunUpdate = new Date();        
    }
    
    private void loadWeekRun()
    {
        try {
            JSONArray keyListInDb = dbConnect.select(
                    KeystoneDungeonRun.KEYSTONE_DUNGEON_RUN_TABLE_NAME,
                    new String[] {"id"},
                    "completed_timestamp > ? order by completed_timestamp DESC",
                    new String[] {"1550586670000"});
            this.keyThisWeek = new KeystoneDungeonRun[keyListInDb.size()];
            for(int i = 0; i < keyListInDb.size(); i++)
            {
                this.keyThisWeek[i] = new KeystoneDungeonRun((Integer) ((JSONObject) keyListInDb.get(i)).get("id"));
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(MythicPlusControl.class, "Fail to get last keystone - "+ ex);
        }
        lastKeyThisWeekUpdate = new Date();   
    }
    
    public KeystoneDungeonRun[] getWeekKeyRun()
    {
        if(this.keyThisWeek == null)
            loadWeekRun();
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastKeyThisWeekUpdate.compareTo(tenMinuteAgo) < 0)
            {
                loadWeekRun();
            }
        }
        return this.keyThisWeek;
    }
    
    public KeystoneDungeonRun[] getLastBestKeyRun() 
    {         
        if(this.keyBestRun == null)
            loadBestRun();
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastKeyBestRunUpdate.compareTo(tenMinuteAgo) < 0)
            {
                loadBestRun();
            }
        }
        return this.keyBestRun;
    }
    
}