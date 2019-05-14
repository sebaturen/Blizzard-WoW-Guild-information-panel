/**
 * File : EventAsist.java
 * Desc : Detail User asist to specific event
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.events;

import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class EventsAsist extends GameObject
{
    //DB Structure
    public static final String EVENTS_ASIST_TABLE_NAME = "events_asist";
    public static final String EVENTS_ASIST_TABLE_KEY = "id_asis";
    public static final String[] EVENTS_ASIST_TABLE_STRUCTURE = {"id_asis", "id_event", "user_id" };
    
    //Atribute
    private int idAsis = -1;
    private int idEvent;
    private User user;
    private List<EventAsistCharacter> eventCharacter = new ArrayList<>();
    
    public EventsAsist()
    {
        super(EVENTS_ASIST_TABLE_NAME, EVENTS_ASIST_TABLE_KEY, EVENTS_ASIST_TABLE_STRUCTURE);
    }
    
    public EventsAsist(int id)
    {
        super(EVENTS_ASIST_TABLE_NAME, EVENTS_ASIST_TABLE_KEY, EVENTS_ASIST_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        this.idAsis = (Integer) objInfo.get("id_asis");
        this.idEvent = (Integer) objInfo.get("id_event");
        this.user = new User((Integer) objInfo.get("user_id"));
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        setTableStructur(DBStructure.outKey(EVENTS_ASIST_TABLE_STRUCTURE));
        /* {"id_event", "user_id" }; */
        switch (saveInDBObj(new String[] {this.idEvent+"", this.user.getId()+""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.eventCharacter.forEach((evChar) -> {
                    evChar.saveInDB();
                });
                return true;
        }
        return false;
    }

    @Override
    public void setId(int id) { this.idAsis = id; }
    public void setEventId(int eId) { this.idEvent = eId; }
    public void setUser(User u) { this.user = u; }
    public void addAsisCharacter(EventAsistCharacter eaChar) { this.eventCharacter.add(eaChar); }
    
    @Override
    public int getId() { return this.idAsis; }
    public int getEventId() { return this.idEvent; }
    public User getUser() { return this.user; }
    public List getEventAsistCharacter() { return this.eventCharacter; }
    
}
