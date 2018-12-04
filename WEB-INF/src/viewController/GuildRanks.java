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
import com.blizzardPanel.gameObject.characters.Member;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GuildRanks 
{    
    private final DBConnect dbConnect = new DBConnect();
    
    public Rank[] getRanks( ) { return getRanks(true); }
    public Rank[] getRanks(boolean orderStat)
    {
        String order = (orderStat)? "ASC":"DESC";
        Rank[] ranks = null;
        try {
            JSONArray rankDB = dbConnect.select(Rank.GUILD_RANK_TABLE_NAME,
                    new String[] { Rank.GUILD_RANK_TABLE_KEY },
                    "1=? ORDER BY id "+ order,
                    new String[] {"1"});
            ranks = new Rank[rankDB.size()];
            for(int i = 0; i < rankDB.size(); i++)
            {
                ranks[i] = new Rank((Integer) ((JSONObject) rankDB.get(i)).get(Rank.GUILD_RANK_TABLE_KEY));
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to load ranks "+ ex);
        }
        return ranks;
    }
    
    public Member[] getMemberByRank(int rankId)
    {
        Member[] mb = null;
        try
        {
            JSONArray memberRank =  dbConnect.select(Member.GMEMBER_ID_NAME_TABLE_NAME +" gm, "+ Member.CHARACTER_INFO_TABLE_NAME +" c", 
                                                new String[] {"gm.internal_id"},
                                                "in_guild=? AND gm.internal_id = c.internal_id AND rank=? ORDER BY gm.rank ASC, c.level DESC, gm.member_name ASC", 
                                                new String[] {"1", rankId+""}, true);	
            mb = new Member[memberRank.size()];
            for(int i = 0; i < memberRank.size(); i++)
            {
                mb[i] = new Member((Integer) ((JSONObject)memberRank.get(i)).get(Member.GMEMBER_ID_NAME_TABLE_KEY));
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get Members by rank - "+ ex);
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
            Logs.saveLogln("Fail to save rank new info - "+ e);
        }
    }
}