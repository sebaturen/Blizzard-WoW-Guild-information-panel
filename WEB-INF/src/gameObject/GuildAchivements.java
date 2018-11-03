/**
 * File : GuildAchivements.java
 * Desc : GuildAchivements Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import org.json.simple.JSONObject;

public class GuildAchivements extends GameObject
{
    //Atribute
    private int id;
    private String title;
    private String description;
    private String icon;
    private int points;
    private String classification;
        
    //Constant
    private static final String TABLE_NAME = "guild_achievements_list";
    private static final String[] TABLE_STRUCTURE = {"id","title", "description", "icon", "points", "classification"};
    
    public GuildAchivements(int id)
    {
        super(TABLE_NAME,TABLE_STRUCTURE);
        loadFromDB(id+"");
    }

    public GuildAchivements(JSONObject exInfo)
    {
        super(TABLE_NAME,TABLE_STRUCTURE);
        saveInternalInfoObject(exInfo);
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject exInfo)
    {        
        if(exInfo.get("id").getClass() == java.lang.Long.class) //if info come to blizzAPI or DB
        {
            this.id = ((Long) exInfo.get("id")).intValue();
            this.points = ((Long) exInfo.get("points")).intValue();
        }
        else
        {
            this.id = ((Integer) exInfo.get("id"));
            this.points = ((Integer) exInfo.get("points"));
        }
        
        this.title = exInfo.get("title").toString();
        this.description = exInfo.get("description").toString();
        this.icon = exInfo.get("icon").toString();
        
        this.classification = exInfo.get("classification").toString();
        this.isData = true;
    }
	
    @Override
    public boolean saveInDB()
    {
        switch (saveInDBObj(new String[] {this.id +"", this.title, this.description, this.icon, this.points +"", this.classification}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }	
	
    //Getters
    public int getId() { return this.id; }
    public String getClassification() { return this.classification; }
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
    
}
