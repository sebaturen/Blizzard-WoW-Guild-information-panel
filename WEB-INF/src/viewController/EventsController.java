/**
 * File : EventsController.java
 * Desc : Events Controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.events.Event;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EventsController 
{    
    //Variable
    private final DBConnect dbConnect;
    private List<Event> activeEvents = new ArrayList<>();
    
    public EventsController()
    {
        dbConnect = new DBConnect();
    }
    
    private void getEventsDB()
    {
        List<Event> events = new ArrayList<>();
        try {
            JSONArray eventsDB = dbConnect.select(Event.EVENTS_TABLE_NAME,
                    new String[] { Event.EVENTS_TABLE_KEY },
                    "date >= ? ORDER BY date DESC",
                    new String[] { "now()" });
            for(int i = 0; i < eventsDB.size(); i++)
            {
                Event e = new Event( (Integer) ((JSONObject)eventsDB.get(i)).get(Event.EVENTS_TABLE_KEY));
                events.add(e);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(PollController.class, "Fail to load events - "+ ex);
        }
        this.activeEvents = events;
    }
    
    public List<Event> getEvents()
    {
        //if(this.polls.isEmpty()) 
            getEventsDB();
            System.out.println("ev Size> "+ this.activeEvents.size());
        return this.activeEvents;
    }
    
    public Event getEvent(int id)
    {
        return new Event(id);
    }
    
    /*public boolean newPoll(User owner, String pollQuest, int guildLevel, boolean moreOptions, 
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
        }
        newPoll.setIsData(true);
        return newPoll.saveInDB();
    }*/
}
