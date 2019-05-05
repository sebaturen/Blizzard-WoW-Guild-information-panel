/**
 * File : MythicPlusControl.java
 * Desc : MythicPlus view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.ServerTime;
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
    
    //From specific periode
    private void loadBestRun()
    {
        try {
            JSONArray keyListInDb = dbConnect.selectQuery(
                "SELECT DISTINCT extract.id " +
                "FROM " +
                "	( " +
                "		SELECT " +
                "			kd.id, " +
                "                       kd.completed_timestamp, " +
                "                       kd.keystone_level, " +
                "			kdm.character_internal_id, " +
                "			gmn.internal_id " +
                "		FROM " +
                "			keystone_dungeon_run kd, " +
                "                       keystone_dungeon_run_members kdm, " +
                "                       gMembers_id_name gmn " +
                "		WHERE " +
                "			kd.id = kdm.keystone_dungeon_run_id AND " +
                "			gmn.internal_id = kdm.character_internal_id AND " +
                "			kd.is_complete_in_time = 1 AND " +
                "			gmn.in_guild = 1 AND " +
                "			kd.completed_timestamp > "+ ServerTime.getSeasonTime() +" " +
                "		ORDER BY kd.keystone_level DESC, kd.completed_timestamp DESC " +
                "		LIMIT 50 " +
                "	) extract " +
                "ORDER BY extract.keystone_level DESC, extract.completed_timestamp DESC " +
                "LIMIT 3;"
            );
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
                    new String[] {ServerTime.getLastResetTime()+""});
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