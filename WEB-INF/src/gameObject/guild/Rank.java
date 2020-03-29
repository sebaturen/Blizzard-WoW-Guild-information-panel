/**
 * File : Rank.java
 * Desc : Guild Ranks (Officer, Member, GuildLeader, etc)
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

public class Rank extends GameObject
{
    //DB Rank structure
    public static final String TABLE_NAME = "guild_rank";
    public static final String TABLE_KEY = "id";
    public static final String[] GUILD_RANK_TABLE_STRUCTURE = {"id", "title"};
    
    //Atribute
    private int id = -1;
    private String title;
    
    public Rank(int id)
    {
        super(TABLE_NAME, TABLE_KEY, GUILD_RANK_TABLE_STRUCTURE);
        loadFromDB(id);
        if(!this.isInternalData)
        {
            this.id = id;
            this.title = id+"";
            this.isData = true;
            saveInDB();
        }
    }
    
    /**
     * Force load only if exist in DB
     * @param id
     * @param validExit 
     */
    public Rank(int id, boolean validExit)
    {
        super(TABLE_NAME, TABLE_KEY, GUILD_RANK_TABLE_STRUCTURE);
        loadFromDB(id);      
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();
        this.title = objInfo.get("title").getAsString();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        /* {"id", "title"}; */
        switch (saveInDBObj(new String[] {this.id +"", this.title}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;        
    }

    //Getters and Setters
    @Override
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public int getId() { return this.id; }
    public String getTitle() { return this.title; }
    
    
}
