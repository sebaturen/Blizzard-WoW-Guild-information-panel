/**
 * File : EventAsist.java
 * Desc : Detail User asist to specific event
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.events;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.poll.PollOption;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventAsist extends GameObject
{
    // DB Structure
    public static final String EVENTS_ASIST_TABLE_NAME = "events_asist";
    public static final String EVENTS_ASIST_TABLE_KEY = "id_asis";
    public static final String[] EVENTS_ASIST_TABLE_STRUCTURE = {"id_asis", "id_event", "user_id" };
    
    // Attribute
    private int idAsis = -1;
    private int idEvent;
    private User user;
    private List<EventAsistCharacter> eventCharacter = new ArrayList<>();
    
    public EventAsist()
    {
        super(EVENTS_ASIST_TABLE_NAME, EVENTS_ASIST_TABLE_KEY, EVENTS_ASIST_TABLE_STRUCTURE);
    }
    
    public EventAsist(int id)
    {
        super(EVENTS_ASIST_TABLE_NAME, EVENTS_ASIST_TABLE_KEY, EVENTS_ASIST_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.idAsis = objInfo.get("id_asis").getAsInt();
        this.idEvent = objInfo.get("id_event").getAsInt();
        this.user = new User(objInfo.get("user_id").getAsInt());
        loadCharacters();
        this.isData = true;
    }

    @Override
    public boolean saveInDB()
    {
        setTableStructur(DBStructure.outKey(EVENTS_ASIST_TABLE_STRUCTURE));
        switch (saveInDBObj(new String[] {this.idEvent+"", this.user.getId()+""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.eventCharacter.forEach((evChar) -> {
                    evChar.setAsisID(this.idAsis);
                    evChar.saveInDB();
                });
                return true;
        } 
        return false;
    }
    
    private void loadCharacters()
    {
        this.eventCharacter = new ArrayList<>();
        try {
            JsonArray charListDB = dbConnect.select(EventAsistCharacter.EVENTS_ASIST_CHAR_TABLE_NAME,
                    new String[] { EventAsistCharacter.EVENTS_ASIST_CHAR_TABLE_KEY },
                    "id_asis=?",
                    new String[] {this.idAsis+""});
            for(int i = 0; i < charListDB.size(); i++)
            {
                EventAsistCharacter eChar = new EventAsistCharacter( charListDB.get(i).getAsJsonObject().get(EventAsistCharacter.EVENTS_ASIST_CHAR_TABLE_KEY).getAsInt() );
                this.eventCharacter.add(eChar);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(EventAsist.class, "Fail to load characters from user asist - "+ this.idAsis +" - "+ ex);
        }
    }
    
    public boolean setCharacters(CharacterMember mainChar, List<CharacterMember> altersChar)
    {
        // Delete pre set elements
        for (EventAsistCharacter eChar : this.eventCharacter) {
            eChar.deleteFromDB();
        }
        this.eventCharacter = new ArrayList<>();
        // Add new characters
        EventAsistCharacter main = new EventAsistCharacter();
        main.setCharacterMember(mainChar);
        main.setIsMain(true);
        main.setIsData(true);
        this.eventCharacter.add(main);

        // Add alters
        for(CharacterMember alt : altersChar) {
            EventAsistCharacter altChar = new EventAsistCharacter();
            altChar.setCharacterMember(alt);
            altChar.setIsData(true);
            this.eventCharacter.add(altChar);
        }

        // Save Event assistance
        if(this.saveInDB()) {
            return true;
        }
        return false;
    }

    @Override
    public void setId(int id) { this.idAsis = id; }
    public void setEventId(int eId) { this.idEvent = eId; }
    public void setUser(User u) { this.user = u; }
    public void addAssistCharacter(EventAsistCharacter eaChar) { this.eventCharacter.add(eaChar); }
    public boolean isAssistCharacter(CharacterMember chm) {
        for(EventAsistCharacter eChar : this.eventCharacter)
        {
        }
        return false;
    }
    
    @Override
    public int getId() { return this.idAsis; }
    public int getEventId() { return this.idEvent; }
    public User getUser() { return this.user; }
    public List getEventAssistCharacter() { return this.eventCharacter; }
    public EventAsistCharacter getMainEventAssistCharacter()
    {
        for(EventAsistCharacter eChar : this.eventCharacter)
        {
            if(eChar.isMain()) return eChar;
        }
        return null;
    }
    
    public List<EventAsistCharacter> getAlterEventAssistCharacter()
    {
        List<EventAsistCharacter> alterChar = new ArrayList<>();
        for(EventAsistCharacter eChar : this.eventCharacter)
        {
            if(!eChar.isMain()) alterChar.add(eChar);
        }
        return alterChar;
    }
    public boolean removeParticiple() {
        try {
            this.eventCharacter.forEach((resl) -> {
                resl.deleteFromDB();
            });
            dbConnect.delete(EVENTS_ASIST_TABLE_NAME,
                    EVENTS_ASIST_TABLE_KEY +"=?",
                    new String[] { this.idAsis+"" });
            return true;
        } catch (SQLException | DataException ex) {
            Logs.errorLog(PollOption.class, "Fail to delete event assits in > "+ this.idAsis +"--"+ ex);
        }
        return false;
    }

    @Override
    public String toString() {
        return "EventAsist{" +
                "idAsis=" + idAsis +
                ", idEvent=" + idEvent +
                ", user=" + user +
                ", eventCharacter=" + eventCharacter +
                '}';
    }
}
