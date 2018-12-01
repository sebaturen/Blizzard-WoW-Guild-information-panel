/**
 * File : Rank.java
 * Desc : Guild Ranks
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONObject;

public class Rank extends GameObject
{
    //DB Rank structure
    public static final String GUILD_RANK_TABLE_NAME = "guild_rank";
    public static final String GUILD_RANK_TABLE_KEY = "id";
    public static final String[] GUILD_RANK_TABLE_STRUCTURE = {"id", "title"};
    
    //Atribute
    private int id = -1;
    private String title;
    
    public Rank(int id)
    {
        super(GUILD_RANK_TABLE_NAME, GUILD_RANK_TABLE_KEY, GUILD_RANK_TABLE_STRUCTURE);
        loadFromDB(id+"");
        if(!this.isInternalData)
            this.id = id;
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        this.id = (Integer) objInfo.get("id");
        this.title = objInfo.get("title").toString();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Getters and Setters
    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return this.id; }
    public String getTitle() { if(this.title == null) return this.id+""; else return this.title; }
    
    
}
