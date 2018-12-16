/**
 * File : Spell.java
 * Desc : Spell object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.exceptions.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.APIInfo;
import com.blizzardPanel.exceptions.ConfigurationException;
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
        loadFromDB(id);   
    }
    
    public Spell(JSONObject inf)
    {
        super(SPELLS_TABLE_NAME, SPELLS_TABLE_KEY, SPELLS_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject objInfo)
    {
        if(objInfo.get("id").getClass() == java.lang.Long.class) //if info come to blizzAPI or DB		
            this.id = ((Long) objInfo.get("id")).intValue();
        else
            this.id = (Integer) objInfo.get("id");
        this.name = objInfo.get("name").toString();
        this.icon = objInfo.get("icon").toString();
        this.description = objInfo.get("description").toString();
        this.castTime = objInfo.get("castTime").toString();
        if(objInfo.containsKey("cooldown") && objInfo.get("cooldown") != null) this.cooldown = objInfo.get("cooldown").toString();
        if(objInfo.containsKey("range") && objInfo.get("range") != null) this.range = objInfo.get("range").toString();
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
        try {
            return String.format(APIInfo.API_ITEM_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), size, this.icon) +".jpg";
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
            return null;
        }
    }
    
    @Override
    public void setId(int id) { this.id = id; }
}
