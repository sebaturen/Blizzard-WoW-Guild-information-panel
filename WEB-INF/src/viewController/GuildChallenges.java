/**
 * File : Members.java
 * Desc : members.jsp view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.guild.challenges.Challenge;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class GuildChallenges
{
    //Variable
    private final DBConnect dbConnect;
    private Challenge[] challenges;

    public GuildChallenges()
    {
        dbConnect = new DBConnect();
        generateChallengeList();
    }
    
    private void generateChallengeList()
    {
        try
        {
            //Prepare list members
            List<Challenge> mList = new ArrayList<>();
            //Get members to DB			
            JSONArray dbList = dbConnect.select(Challenge.CHALLENGES_TABLE_NAME, 
                                                new String[] {"id"});	
            for(int i = 0; i < dbList.size(); i++)
            {
                Challenge ch = new Challenge( (Integer)((JSONObject)dbList.get(i)).get("id") );
                if(ch.isData()) mList.add(ch);
            }
            //Convert LIST to simple Member Array
            if(mList.size() > 0) this.challenges = mList.toArray(new Challenge[mList.size()]);
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLogln("Fail to load members lists - Members View Controller");
        }
    }
	
    public Challenge[] getChallengesList() { return this.challenges; }
}