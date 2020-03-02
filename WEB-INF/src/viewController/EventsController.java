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
import com.google.gson.JsonArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            JsonArray eventsDB = dbConnect.select(Event.EVENTS_TABLE_NAME,
                    new String[] { Event.EVENTS_TABLE_KEY },
                    "date >= ? ORDER BY date DESC",
                    new String[] { "now()" });
            for(int i = 0; i < eventsDB.size(); i++)
            {
                Event e = new Event( eventsDB.get(i).getAsJsonObject().get(Event.EVENTS_TABLE_KEY).getAsInt() );
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
    
    //(user, eventTitle, eventDesc, eventDate))
    public boolean newEvent(User owner, String eventTitle, String eventDesc, String eventDate)
    {
        Event newEvent = new Event();
        newEvent.setTitle(eventTitle);
        newEvent.setDesc(eventDesc);
        newEvent.setDate(eventDate);
        newEvent.setOwner(owner);
        newEvent.setIsData(true);
        return newEvent.saveInDB();
    }


    public Event getEvent(int id)
    {
        return new Event(id);
    }
}
