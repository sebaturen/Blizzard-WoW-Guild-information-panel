/**
 * File : PollOptionResult.java
 * Desc : PollOptionResult Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.poll;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

import java.sql.SQLException;

public class PollOptionResult extends GameObject
{
    //DB Structure
    public static final String POLL_OPTION_RESULTS_TABLE_NAME = "poll_option_result";
    public static final String POLL_OPTION_RESULTS_TABLE_KEY = "id";
    public static final String[] POLL_OPTION_RESULTS_TABLE_STRUCTURE = {"id", "poll_option_id", "owner_id", "date"};

    //Atribute
    private int id = -1;
    private int pollOptionId;
    private User owner;
    private String date;   
    
    public PollOptionResult(int pollOption, User owner)
    {
        super(POLL_OPTION_RESULTS_TABLE_NAME, POLL_OPTION_RESULTS_TABLE_KEY, POLL_OPTION_RESULTS_TABLE_STRUCTURE);
        this.pollOptionId = pollOption;
        this.owner = owner;
        this.date = Update.getCurrentTimeStamp();
        this.isData = true;
        saveInDB();
    }
    
    public PollOptionResult(int id)
    {
        super(POLL_OPTION_RESULTS_TABLE_NAME, POLL_OPTION_RESULTS_TABLE_KEY, POLL_OPTION_RESULTS_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();
        this.pollOptionId = objInfo.get("poll_option_id").getAsInt();
        this.owner = new User(objInfo.get("owner_id").getAsInt());
        this.date = objInfo.get("date").getAsString();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        setTableStructur(DBStructure.outKey(POLL_OPTION_RESULTS_TABLE_STRUCTURE));
        /* {"poll_option_id", "owner_id", "date"} */
        switch (saveInDBObj(new String[] {this.pollOptionId+"", this.owner.getId()+"", this.date}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }
    
    public boolean deleteFromDB()
    {
        try 
        {
            dbConnect.delete(POLL_OPTION_RESULTS_TABLE_NAME, 
                            "id=?",
                            new String[] { this.id+""});
            return true;
        } catch (SQLException | DataException ex) {
            Logs.errorLog(PollOptionResult.class, "Fail to delete options - "+ this.id +" - "+ this.owner.getBattleTag() +" - "+ ex);
        }
        return false;
    }
    
    //Getters and Setters
    @Override
    public int getId() { return this.id; }
    public int getPollOptionId() { return this.pollOptionId; }
    public User getOwner() { return this.owner; }
    public String getDate() { return this.date; }
    
    @Override
    public void setId(int id) { this.id = id; }
    public void setPollOptionId(int id) { this.pollOptionId = id; }
    public void setOwner(User u) { this.owner = u; }
    public void setDate(String s) { this.date = s; }
    
    
    
}