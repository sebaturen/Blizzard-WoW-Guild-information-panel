/**
 * File : Event.java
 * Desc : Detail event
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.events;

import com.blizzardPanel.DataException;
import com.blizzardPanel.DiscordBot;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.guild.Rank;
import com.blizzardPanel.poll.Poll;
import com.blizzardPanel.poll.PollOption;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event extends GameObject
{
    //DB Structure
    public static final String EVENTS_TABLE_NAME = "events";
    public static final String EVENTS_TABLE_KEY = "id";
    public static final String[] EVENTS_TABLE_STRUCTURE = {"id", "title", "desc", "date", "owner_id", "min_rank", "min_level", "isEnable", "isHide"};
    
    //Atribute
    private int id = -1;
    private String title;
    private String desc;
    private User owner;
    private String date;
    private List<EventAsist> eventAsis = new ArrayList<>();
    private int minLevel;
    private Rank minRank;
    private boolean isEnable;
    private boolean isHide;
    
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
        this.isEnable = objInfo.get("isEnable").getAsBoolean();
        this.isHide = objInfo.get("isHide").getAsBoolean();
        this.minRank = new Rank(objInfo.get("min_rank").getAsInt());
        this.minLevel = objInfo.get("min_level").getAsInt();
        this.isData = true;
    }

    private void loadEventAssist() {
        this.eventAsis = new ArrayList<>();
        try {
            JsonArray eventDb = dbConnect.select(EventAsist.EVENTS_ASIST_TABLE_NAME,
                    new String[] {EventAsist.EVENTS_ASIST_TABLE_KEY},
                    "id_event=?",
                    new String[] {this.id +""});
            for(int i = 0; i < eventDb.size(); i++)
            {
                this.eventAsis.add(new EventAsist(eventDb.get(i).getAsJsonObject().get(EventAsist.EVENTS_ASIST_TABLE_KEY).getAsInt() ));
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Poll.class, "Fail to get event assist in event - "+ this.id +" - "+ ex);
        }
    }

    @Override
    public boolean saveInDB() 
    {
        setTableStructur(DBStructure.outKey(EVENTS_TABLE_STRUCTURE));
        /* {"title", "desc", "date", "owner_id"}; */
        switch (saveInDBObj(new String[] {this.title, this.desc, this.date, this.owner.getId()+"", this.minRank.getId()+"", this.minLevel+"", (this.isEnable)? "1":"0", (this.isHide)? "1":"0" }))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.eventAsis.forEach((evAsis) -> {
                    evAsis.saveInDB();
                });
                return true;
        }
        return false;
    }    
    
    public boolean addCharactersFormUser(User user, CharacterMember mainChar, List<CharacterMember> altersChar)
    {
        EventAsist eAsis = getAssistDetail(user);
        if(eAsis == null)
        {
            eAsis = new EventAsist();
            eAsis.setEventId(this.id);
            eAsis.setUser(user);
            eAsis.setIsData(true);
        }
        if(eAsis.setCharacters(mainChar, altersChar))
        {
            if(eAsis.saveInDB()) {
                this.eventAsis.add(eAsis);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setOwner(User owner) { this.owner = owner; }
    public void setDate(String date) { this.date = date; }
    public void setMinLevel(int lvl) { this.minLevel = lvl; }
    public void setMinRank(Rank minRank) { this.minRank = minRank; }

    public void setEnable(boolean enable) {
        this.isEnable = enable;
        this.saveInDB();
    }

    public void setHide() {
        this.isHide = true;
        this.saveInDB();
    }

    @Override
    public int getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getDesc() { return this.desc; }
    public User getOwner() { return this.owner; }
    public String getStringDate() { return this.date; }
    public Date getDate() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.date);
        } catch (ParseException e) {
            Logs.errorLog(this.getClass(), "FAILED - to parse date "+ e);
        }
        return new Date();
    }
    public Rank getMinRank() { return this.minRank; }
    public int getMinLevel() { return this.minLevel; }
    public boolean isEnable() { return this.isEnable; }
    public boolean isExpire() {
        Date today = new Date();
        return !today.before(this.getDate());
    }
    public boolean isHide() { return this.isHide; }
    public List<EventAsist> getEventAssist() { loadEventAssist(); return this.eventAsis; }
    public EventAsist getAssistDetail(User user)
    {
        if (this.eventAsis.isEmpty()) loadEventAssist();
        for(EventAsist eAsis : this.eventAsis)
        {
            if(eAsis.getUser().getId() == user.getId())
                return eAsis;
        }
        return null;
    }
    public boolean removeParticiple(User user) {
        loadEventAssist();
        for(EventAsist eAssist: this.eventAsis) {
            if (eAssist.getUser().getId() == user.getId())
                return eAssist.removeParticiple();
        }
        return false;
    }
    public int totalAssist() {
        loadEventAssist();
        return this.eventAsis.size();
    }
    
}
