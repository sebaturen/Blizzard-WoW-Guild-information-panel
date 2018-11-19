/**
 * File : Spell.java
 * Desc : Spell object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.blizzardAPI.APIInfo;
import org.json.simple.JSONObject;

public class Spell extends GameObject
{
    //Spells DB
    public static final String SPELLS_TABLE_NAME = "spells";
    public static final String SPELLS_TABLE_KEY = "id";
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
        super(SPELLS_TABLE_NAME, SPELLS_TABLE_KEY, SPELLS_TABLE_STRUCTURE);
        loadFromDB(id +"");   
    }
    
    public Spell(JSONObject inf)
    {
        super(SPELLS_TABLE_NAME, SPELLS_TABLE_KEY, SPELLS_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject exInfo)
    {
        if(exInfo.get("id").getClass() == java.lang.Long.class) //if info come to blizzAPI or DB		
            this.id = ((Long) exInfo.get("id")).intValue();
        else
            this.id = (Integer) exInfo.get("id");
        this.name = exInfo.get("name").toString();
        this.icon = exInfo.get("icon").toString();
        this.description = exInfo.get("description").toString();
        this.castTime = exInfo.get("castTime").toString();
        if(exInfo.containsKey("cooldown") && exInfo.get("cooldown") != null) this.cooldown = exInfo.get("cooldown").toString();
        if(exInfo.containsKey("range") && exInfo.get("range") != null) this.range = exInfo.get("range").toString();
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
    public String getId() { return this.id +""; }
    public int getIntId() { return this.id; }
    public String getName() { return this.name; }
    public String getDesc() { return this.description; }
    public boolean isPasive() { return (this.castTime.equals("Passive")); }
    public String getIconRenderURL() { return getIconRenderURL(56); }
    public String getIconRenderURL(int size) 
    {
        return String.format(APIInfo.API_ITEM_RENDER_URL, APIInfo.SERVER_LOCATION, size, this.icon) +".jpg";
    }
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
}
