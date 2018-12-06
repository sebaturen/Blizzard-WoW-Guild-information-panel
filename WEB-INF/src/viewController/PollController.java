/**
 * File : PollController.java
 * Desc : Poll Controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.guild.Rank;
import com.blizzardPanel.poll.Poll;
import com.blizzardPanel.poll.PollOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PollController 
{    
    //Variable
    private final DBConnect dbConnect;
    private List<Poll> polls = new ArrayList<>();
    
    public PollController()
    {
        dbConnect = new DBConnect();
    }
    
    private void getPollsDB()
    {
        try {
            JSONArray pollsDB = dbConnect.select(Poll.POLLS_TABLE_NAME,
                    new String[] { Poll.POLLS_TABLE_KEY },
                    "isEnable=?",
                    new String[] {"1"});
            for(int i = 0; i < pollsDB.size(); i++)
            {
                Poll p = new Poll( (Integer) ((JSONObject)pollsDB.get(i)).get(Poll.POLLS_TABLE_KEY));
                if(p.isEnable())
                {
                    this.polls.add(p);
                }
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to load polls - "+ ex);
        }    
    }
    
    public List<Poll> getPolls()
    {
        if(this.polls.isEmpty()) getPollsDB();
        return this.polls;
    }
    
    public Poll getPoll(int id)
    {
        for(Poll p : this.polls)
        {
            if(p.getId() == id)
                return p;
        }
        return null;
    }
    
    public boolean newPoll(User owner, String pollQuest, int guildLevel, boolean moreOptions, 
                            boolean multiOptions, boolean limiDate, 
                            String limitDateSet, List<String> options)
    {
        Poll newPoll = new Poll();
        newPoll.setUser(owner);
        newPoll.setPollQuestion(pollQuest);
        newPoll.setMinRank(new Rank(guildLevel));
        newPoll.setCanAddMoreOptions(moreOptions);
        newPoll.setMultiSelect(multiOptions);
        newPoll.setIsLimitDate(limiDate);
        if(limiDate)
        {
            if(limitDateSet != null && limitDateSet.length() > 0)
                newPoll.setEndDate(limitDateSet);
            else
                return false;
        }
        newPoll.setIsEnable(true);
        newPoll.setStartDate(Update.getCurrentTimeStamp());
        //Add options
        for(String op : options)
        {
            PollOption pollOp = new PollOption();
            pollOp.setOptionText(op);
            pollOp.setOwner(owner);
            pollOp.setDate(Update.getCurrentTimeStamp());
            pollOp.setIsData(true);
            newPoll.addOption(pollOp);
            System.out.println("opt> "+ pollOp.getOptionText());
        }
        newPoll.setIsData(true);
        return newPoll.saveInDB();
    }
}
