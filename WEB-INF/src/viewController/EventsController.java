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
import com.blizzardPanel.User;
import com.blizzardPanel.gameObject.guild.Rank;
import com.google.gson.JsonArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventsController 
{    
    //Variable
    private final DBConnect dbConnect;
    private List<Event> activeEvents = new ArrayList<>();
    private List<Event> disableEvents = new ArrayList<>();
    
    public EventsController()
    {
        dbConnect = new DBConnect();
    }

    private void getEventsDB(boolean status, boolean expire)
    {
        List<Event> events = new ArrayList<>();
        try {
            JsonArray eventsDB = dbConnect.select(Event.EVENTS_TABLE_NAME,
                    new String[] { Event.EVENTS_TABLE_KEY },
                    "isEnable=? AND isHide=? ORDER BY date DESC",
                    new String[] {status? "1":"0", "0"});
            for(int i = 0; i < eventsDB.size(); i++)
            {
                Event e = new Event( eventsDB.get(i).getAsJsonObject().get(Event.EVENTS_TABLE_KEY).getAsInt() );
                if (e.isExpire() == expire) {
                    events.add(e);
                }
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(PollController.class, "Fail to load events - "+ ex);
        }
        if(status)
            this.activeEvents = events;
        else
            this.disableEvents = events;
    }

    public List<Event> getExpireEvents()
    {
        //if(this.polls.isEmpty())
        getEventsDB(true, true);
        return this.activeEvents;
    }

    public List<Event> getActiveEvents()
    {
        //if(this.polls.isEmpty())
        getEventsDB(true, false);
        return this.activeEvents;
    }

    public List<Event> getDisableEvents()
    {
        //if(this.polls.isEmpty())
        getEventsDB(false, false);
        return this.disableEvents;
    }
    
    //(user, eventTitle, eventDesc, eventDate))
    public boolean newEvent(User owner, String eventTitle, String eventDesc, String eventDate, int minLvl, int guildLvl)
    {
        Event newEvent = new Event();
        newEvent.setTitle(eventTitle);
        newEvent.setDesc(eventDesc);
        newEvent.setDate(eventDate);
        newEvent.setOwner(owner);
        newEvent.setMinLevel(minLvl);
        newEvent.setEnable(true);
        newEvent.setIsData(true);
        return newEvent.saveInDB();
    }


    public Event getEvent(int id)
    {
        return new Event(id);
    }
}
