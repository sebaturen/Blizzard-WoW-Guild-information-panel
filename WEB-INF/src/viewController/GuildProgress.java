/**
 * File : GuildProgress.java
 * Desc : Get a guild progress
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.dbConnect.DBStructure;
import com.artOfWar.gameObject.guild.raids.Raid;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GuildProgress 
{    
    //Variable
    private final DBConnect dbConnect;
    private Raid[] raids;

    public GuildProgress()
    {
        dbConnect = new DBConnect();
        getGuildRaids();
    }
    
    private void getGuildRaids()
    {
        try {
            JSONArray raidDB = dbConnect.select(DBStructure.RAIDS_TABLE_NAME,
                    new String[] {DBStructure.RAIDS_TABLE_KEY},
                    "1=? ORDER BY id desc",
                    new String[] {"1"});
            this.raids = new Raid[raidDB.size()];
            for(int i = 0; i < raidDB.size(); i++)
            {
                Raid r = new Raid((Integer) ((JSONObject) raidDB.get(i)).get(DBStructure.RAIDS_TABLE_KEY));
                this.raids[i] = r;
            }
        } catch (SQLException | DataException ex) {
            System.out.println("Error get a guilds raids "+ ex);
        }
    }
    
    public Raid[] getRaids() { return this.raids; }
}
