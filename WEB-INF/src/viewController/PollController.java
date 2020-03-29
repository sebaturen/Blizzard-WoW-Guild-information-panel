/**
 * File : PollController.java
 * Desc : Poll Controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.update.blizzard.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.guild.Rank;
import com.blizzardPanel.poll.Poll;
import com.blizzardPanel.poll.PollOption;
import com.google.gson.JsonArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PollController 
{    
    //Variable
    private final DBConnect dbConnect;
    private List<Poll> activePolls = new ArrayList<>();
    private List<Poll> disablePolls = new ArrayList<>();
    
    public PollController()
    {
        dbConnect = new DBConnect();
    }
    
    private void getPollsDB(boolean status)
    {
        List<Poll> polls = new ArrayList<>();
        try {
            JsonArray pollsDB = dbConnect.select(Poll.POLLS_TABLE_NAME,
                    new String[] { Poll.POLLS_TABLE_KEY },
                    "isEnable=? AND isHide=? ORDER BY id DESC",
                    new String[] {status? "1":"0", "0"});
            for(int i = 0; i < pollsDB.size(); i++)
            {
                Poll p = new Poll( pollsDB.get(i).getAsJsonObject().get(Poll.POLLS_TABLE_KEY).getAsInt());
                if(p.isEnable() == status)
                {
                    polls.add(p);
                }
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(PollController.class, "Fail to load polls - "+ ex);
        }
        if(status)
            this.activePolls = polls;
        else
            this.disablePolls = polls;
    }
    
    public List<Poll> getActivePolls()
    {
        //if(this.polls.isEmpty()) 
            getPollsDB(true);
        return this.activePolls;
    }
    
    public List<Poll> getDisablePolls()
    {
        //if(this.polls.isEmpty()) 
            getPollsDB(false);
        return this.disablePolls;        
    }
    
    public Poll getPoll(int id)
    {
        for(Poll p : this.activePolls)
        {
            if(p.getId() == id)
                return p;
        }
        for(Poll p : this.disablePolls)
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
        newPoll.setEnable(true);
        //Add options
        if (options == null || options.size() == 0)
            return false;
        int i = 0;
        for(String op : options)
        {
            if (op.length() != 0)
            {
                PollOption pollOp = new PollOption();
                pollOp.setOptionText(op);
                pollOp.setOwner(owner);
                pollOp.setIsData(true);
                newPoll.addOption(pollOp);
                i++;
            }
        }
        if (i == 0)
            return false;
        newPoll.setIsData(true);
        return newPoll.saveInDB();
    }
}
