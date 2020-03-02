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
            JsonArray rankDB = dbConnect.select(Rank.GUILD_RANK_TABLE_NAME,
                    new String[] { Rank.GUILD_RANK_TABLE_KEY },
                    "1=? ORDER BY id "+ order,
                    new String[] {"1"});
            ranks = new Rank[rankDB.size()];
            for(int i = 0; i < rankDB.size(); i++)
            {
                ranks[i] = new Rank( rankDB.get(i).getAsJsonObject().get(Rank.GUILD_RANK_TABLE_KEY).getAsInt() );
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(GuildRanks.class, "Fail to load ranks "+ ex);
        }
        return ranks;
    }
    
    public CharacterMember[] getMemberByRank(int rankId)
    {
        CharacterMember[] mb = null;
        try
        {
            JsonArray memberRank =  dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME +" gm, "+ CharacterMember.CHARACTER_INFO_TABLE_NAME +" c",
                                                new String[] {"gm.internal_id"},
                                                "in_guild=? AND gm.internal_id = c.internal_id AND rank=? ORDER BY gm.rank ASC, c.level DESC, gm.member_name ASC", 
                                                new String[] {"1", rankId+""}, true);	
            mb = new CharacterMember[memberRank.size()];
            for(int i = 0; i < memberRank.size(); i++)
            {
                mb[i] = new CharacterMember( memberRank.get(i).getAsJsonObject().get(CharacterMember.GMEMBER_ID_NAME_TABLE_KEY).getAsInt() );
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(GuildRanks.class, "Fail to get Members by rank - "+ ex);
        }
        return mb;
    }
    
    public void setRankInfo(String sId, String title)
    {
        try
        {
            int id = Integer.parseInt(sId);
            if(id != 0 && id != 1) //prevent guild master and officers change
            {
                Rank r = new Rank(id, true);
                if(r.isInternalData())
                {
                    title = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
                    r.setTitle(title);
                    r.saveInDB();
                }
            }
        } catch (NumberFormatException e) {
            Logs.errorLog(GuildRanks.class, "Fail to save rank new info - "+ e);
        }
    }
}