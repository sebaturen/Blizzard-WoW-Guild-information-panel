/**
 * File : GuildRanks.java
 * Desc : guild_rank.jsp view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.guild.Rank;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.google.gson.JsonArray;

import java.sql.SQLException;

public class GuildRanks 
{    
    private final DBConnect dbConnect = new DBConnect();
    
    public Rank[] getRanks( ) { return getRanks(true); }
    public Rank[] getRanks(boolean orderStat)
    {
        String order = (orderStat)? "ASC":"DESC";
        Rank[] ranks = null;
        try 
        {
            JsonArray rankDB = dbConnect.select(Rank.TABLE_NAME,
                    new String[] { Rank.TABLE_KEY },
                    "1=? ORDER BY id "+ order,
                    new String[] {"1"});
            ranks = new Rank[rankDB.size()];
            for(int i = 0; i < rankDB.size(); i++)
            {
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(GuildRanks.class, "Fail to load ranks "+ ex);
        }
        return ranks;
    }
    
    public CharacterMember[] getMemberByRank(int rankId)
    {
        CharacterMember[] mb = null;
        return mb;
    }
    
    public void setRankInfo(String sId, String title)
    {
        try
        {
            int id = Integer.parseInt(sId);
            if(id != 0 && id != 1) //prevent guild master and officers change
            {
            }
        } catch (NumberFormatException e) {
            Logs.errorLog(GuildRanks.class, "Fail to save rank new info - "+ e);
        }
    }
}