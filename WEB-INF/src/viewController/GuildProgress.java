/**
 * File : GuildProgress.java
 * Desc : Get a guild progress
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.guild.raids.Raid;
import com.google.gson.JsonArray;

import java.sql.SQLException;

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
            JsonArray raidDB = dbConnect.select(Raid.RAIDS_TABLE_NAME,
                    new String[] {Raid.RAIDS_TABLE_KEY},
                    "1=? ORDER BY id desc",
                    new String[] {"1"});
            this.raids = new Raid[raidDB.size()];
            for(int i = 0; i < raidDB.size(); i++)
            {
                Raid r = new Raid( raidDB.get(i).getAsJsonObject().get(Raid.RAIDS_TABLE_KEY).getAsInt() );
                this.raids[i] = r;
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(GuildProgress.class, "Error get a guilds raids "+ ex);
        }
    }
    
    public Raid[] getRaids() { return this.raids; }
}
