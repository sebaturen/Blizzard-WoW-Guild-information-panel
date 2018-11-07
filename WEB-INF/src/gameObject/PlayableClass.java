/**
 * File : PlayableClass.java
 * Desc : Playable class object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import org.json.simple.JSONObject;

public class PlayableClass extends GameObject
{	
    //Attribute
    private int id;
    private String enName;

    public PlayableClass(int id)
    {
        super(PLAYABLE_CLASS_TABLE_NAME, PLAYABLE_CLASS_TABLE_KEY, PLAYABLE_CLASS_TABLE_STRUCTURE);
        loadFromDB(id+"");
    }
	
    public PlayableClass(JSONObject exInfo)
    {
        super(PLAYABLE_CLASS_TABLE_NAME, PLAYABLE_CLASS_TABLE_KEY, PLAYABLE_CLASS_TABLE_STRUCTURE);
        saveInternalInfoObject(exInfo);
    }
	
    @Override
    protected void saveInternalInfoObject(JSONObject exInfo)
    {		
        if(exInfo.containsKey("name")) //if info come to blizzAPI or DB
        {
            this.id = ((Long) exInfo.get("id")).intValue();
            this.enName = ((JSONObject) exInfo.get("name")).get("en_US").toString();
        }
        else
        {
            this.id = (Integer) exInfo.get("id");
            this.enName = exInfo.get("en_US").toString();
        }
        this.isData = true;
    }
	
    @Override
    public boolean saveInDB()
    {
        /* {"id", "en_US"}; */
        switch (saveInDBObj(new String[] {this.id +"", this.enName}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }	
	
    //Getters
    public int getId() { return this.id; }
    public String getEnName() { return this.enName; }
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
	
}