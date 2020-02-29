/**
 * File : PlayableClass.java
 * Desc : Playable class object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONObject;

public class PlayableClass extends GameObject
{	
    //Playable Class DB
    public static final String PLAYABLE_CLASS_TABLE_NAME = "playable_class";
    public static final String PLAYABLE_CLASS_TABLE_KEY = "id";
    public static final String[] PLAYABLE_CLASS_TABLE_STRUCTURE = {"id", "slug", "name"};
    
    //Attribute
    private int id;
    private String slug;
    private String name;

    public PlayableClass(int id)
    {
        super(PLAYABLE_CLASS_TABLE_NAME, PLAYABLE_CLASS_TABLE_KEY, PLAYABLE_CLASS_TABLE_STRUCTURE);
        loadFromDB(id);
    }
	
    public PlayableClass(JSONObject exInfo)
    {
        super(PLAYABLE_CLASS_TABLE_NAME, PLAYABLE_CLASS_TABLE_KEY, PLAYABLE_CLASS_TABLE_STRUCTURE);
        saveInternalInfoObject(exInfo);
    }
	
    @Override
    protected void saveInternalInfoObject(JSONObject exInfo)
    {		
        if(exInfo.get("id").getClass() == java.lang.Long.class) //if info come to blizzAPI or DB
        {
            this.id = ((Long) exInfo.get("id")).intValue();
            this.slug = ((JSONObject) exInfo.get("name")).get("en_US").toString().replaceAll("\\s+","-").toLowerCase();
            this.name = ((JSONObject) exInfo.get("name")).get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).toString();
        }
        else
        {
            this.id = (Integer) exInfo.get("id");
            this.slug = exInfo.get("slug").toString();
            this.name = exInfo.get("name").toString();
        }
        this.isData = true;
    }
	
    @Override
    public boolean saveInDB()
    {
        /* {"id", "en_US"}; */
        switch (saveInDBObj(new String[] {this.id +"", this.slug, this.name}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }	
	
    //Getters
    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getSlug() { return this.slug; }
    @Override
    public void setId(int id) { this.id = id; }
	
}