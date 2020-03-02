/**
 * File : Race.java
 * Desc : Race object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

public class PlayableRace extends GameObject
{	
    //Races DB
    public static final String RACES_TABLE_NAME = "playable_races";
    public static final String RACES_TABLE_KEY = "id";
    public static final String[] RACES_TABLE_STRUCTURE = {"id", "mask", "side", "name"};
    
    //Attribute
    private int id;
    private int mask;
    private String side;
    private String name;

    public PlayableRace(int id)
    {
        super(RACES_TABLE_NAME, RACES_TABLE_KEY, RACES_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    public PlayableRace(JsonObject exInfo)
    {
        super(RACES_TABLE_NAME, RACES_TABLE_KEY, RACES_TABLE_STRUCTURE);
        saveInternalInfoObject(exInfo);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject exInfo)
    {
        this.id = exInfo.get("id").getAsInt();
        this.mask = exInfo.get("mask").getAsInt();
        this.side = exInfo.get("side").getAsString();
        this.name = exInfo.get("name").getAsString();
        this.isData = true;		
    }

    @Override
    public boolean saveInDB()
    {
        /* {"id", "mask", "side", "name"}; */
        switch (saveInDBObj(new String[] {this.id +"", this.mask +"", this.side +"", this.name}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }

    //Getters
    @Override
    public int getId() { return this.id; }
    public int getMask() { return this.mask; }
    public String getSide() { return this.side; }
    public String getName() { return this.name; }
    @Override
    public void setId(int id) { this.id = id; }
	
}