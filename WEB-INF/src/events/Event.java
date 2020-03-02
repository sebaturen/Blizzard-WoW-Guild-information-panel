/**
 * File : Event.java
 * Desc : Detail event
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.events;

import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Event extends GameObject
{
    //DB Structure
    public static final String EVENTS_TABLE_NAME = "events";
    public static final String EVENTS_TABLE_KEY = "id";
    public static final String[] EVENTS_TABLE_STRUCTURE = {"id", "title", "desc", "date", "owner_id"};
    
    //Atribute
    private int id = -1;
    private String title;
    private String desc;
    private User owner;
    private String date;
    private List<EventAsist> eventAsis = new ArrayList<>();
    
    public Event()
    {
        super(EVENTS_TABLE_NAME, EVENTS_TABLE_KEY, EVENTS_TABLE_STRUCTURE);
    }
    
    public Event(int id)
    {
        super(EVENTS_TABLE_NAME, EVENTS_TABLE_KEY, EVENTS_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();
        this.title = objInfo.get("title").getAsString();
        this.desc = objInfo.get("desc").getAsString();
        this.date = objInfo.get("date").getAsString();
        this.owner = new User(objInfo.get("owner_id").getAsInt());
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        setTableStructur(DBStructure.outKey(EVENTS_TABLE_STRUCTURE));
        /* {"title", "desc", "date", "owner_id"}; */
        switch (saveInDBObj(new String[] {this.title, this.desc, this.date, this.owner.getId()+""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.eventAsis.forEach((evAsis) -> {
                    evAsis.saveInDB();
                });
                System.out.println("Data is saved! ");
                return true;
        }
        return false;
    }    
    
    public boolean addCharactersFormUser(User user, CharacterMember mainChar, List<CharacterMember> altersChar)
    {
        EventAsist eAsis = getAsistDetail(user);
        if(eAsis == null) 
        {
            eAsis = new EventAsist();
            eAsis.setEventId(this.id);
            eAsis.setUser(user);
            eAsis.setIsData(true);
        }
        if(eAsis.setCharacters(mainChar, altersChar))
        {
            return eAsis.saveInDB();
        }
        return false;
    }

    @Override
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setOwner(User owner) { this.owner = owner; }
    public void setDate(String date) { this.date = date; }

    @Override
    public int getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getDesc() { return this.desc; }
    public User getOwner() { return this.owner; }
    public String getDate() { return this.date; }
    public List getEventAsist() { return this.eventAsis; }
    public EventAsist getAsistDetail(User user)
    {
        for(EventAsist eAsis : this.eventAsis)
        {
            if(eAsis.getUser().getId() == user.getId())
                return eAsis;
        }
        return null;
    }
    
}
