/**
 * File : Spell.java
 * Desc : Spell object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonObject;

public class Spell extends GameObject
{
    //Spells DB
    public static final String TABLE_NAME = "spells";
    public static final String TABLE_KEY = "id";
    public static final String[] SPELLS_TABLE_STRUCTURE = {"id", "name", "icon", "description",
                                                           "castTime", "cooldown", "range"};
    //Atribute
    private int id;
    private String name;
    private String icon;
    private String description;
    private String castTime;
    private String cooldown;
    private String range;
 
    public Spell(int id)
    {
        super(TABLE_NAME, TABLE_KEY, SPELLS_TABLE_STRUCTURE);
        loadFromDB(id);   
    }
    
    public Spell(JsonObject inf)
    {
        super(TABLE_NAME, TABLE_KEY, SPELLS_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);
    }
    
    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        if (objInfo.isJsonNull()) {
            this.id = 0;
            this.name = "NULL SPELL";
        } else {
            this.id = objInfo.get("id").getAsInt();
            this.name = objInfo.get("name").getAsString();
            this.icon = objInfo.get("icon").getAsString();
            this.description = objInfo.get("description").getAsString();
            this.castTime = objInfo.get("castTime").getAsString();
            if(objInfo.has("cooldown") && !objInfo.get("cooldown").isJsonNull()) this.cooldown = objInfo.get("cooldown").getAsString();
            if(objInfo.has("range") && !objInfo.get("range").isJsonNull()) this.range = objInfo.get("range").getAsString();
        }
        this.isData = true;
    }    
    
    @Override
    public boolean saveInDB()
    {
        //if(this.isInternalData) return true; //if preview save...
        /* {"id", "name", "icon", "description",
         * "castTime", "cooldown", "range"};
         */
        switch (saveInDBObj(new String[] {this.id +"", this.name, this.icon, this.description,
                                            this.castTime, this.cooldown, this.range}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }
    
    //Getters and Setters
    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getDesc() { return this.description; }
    public boolean isPasive() { return (this.castTime.equals("Passive")); }
    public String getIconRenderURL() { return getIconRenderURL(56); }
    public String getIconRenderURL(int size) 
    {
        return String.format(WoWAPIService.API_ITEM_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), size, this.icon) +".jpg";
    }
    
    @Override
    public void setId(int id) { this.id = id; }
}
