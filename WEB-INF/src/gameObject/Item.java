/**
 * File : Item.java
 * Desc : Item object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.update.blizzard.Update;

import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Item extends GameObject
{
    //Item DB
    public static final String TABLE_NAME = "items";
    public static final String TABLE_KEY = "id";
    public static final String[] ITEM_TABLE_STRUCTURE = {"id", "name", "icon", "itemSpell", "gemInfo_bonus_name", "gemInfo_type"};
    
    //Atribute
    private int id;
    private String name;
    private String icon;
    private Spell itemSpell;
    private String gemInfoBonusName;
    private String gemInfoType;
    
    public Item(int id)
    {
        super(TABLE_NAME,TABLE_KEY,ITEM_TABLE_STRUCTURE);
        loadFromDB(id);
        if(!this.isInternalData)
        {
            cloneItem(Update.shared.getItemFromBlizz(id));
        }
    }
    
    public Item(JsonObject inf)
    {
        super(TABLE_NAME,TABLE_KEY,ITEM_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        if (objInfo.has("itemSpells")) { // load from blizzard
            JsonArray itemSpellBlizz = objInfo.get("itemSpells").getAsJsonArray();
            if(itemSpellBlizz.size() > 0)
            {
                int spellId = itemSpellBlizz.get(0).getAsJsonObject().get("spellId").getAsInt();
                this.itemSpell = new Spell(spellId);
                if(!this.itemSpell.isInternalData())
                {
                    this.itemSpell = Update.shared.getSpellInformationBlizz(spellId);
                }
            }
            else
            {
                this.itemSpell = new Spell(0);
            }
        } else { // load from DB

            int spellId = 0;
            if(!objInfo.get("itemSpell").isJsonNull())
            {
                spellId = objInfo.get("itemSpell").getAsInt();
            }
            // alleys is decelerate because in save we need a spell ID, and spell id 0 is a null spell.
            this.itemSpell = new Spell(spellId);
        }
        this.id = objInfo.get("id").getAsInt();
        this.name = objInfo.get("name").getAsString();
        String iconUrl = "";
        //Not all item have a icon U_U
        if(objInfo.has("icon")) iconUrl = objInfo.get("icon").getAsString();
        this.icon = iconUrl;
        if(objInfo.has("gemInfo"))
        {//blizzard API info
            JsonObject vGam = objInfo.get("gemInfo").getAsJsonObject();
            JsonObject bonusInfo = vGam.get("bonus").getAsJsonObject();
            this.gemInfoBonusName = bonusInfo.get("name").getAsString();
            if(vGam.has("type"))
            {
                JsonObject typeInfo = vGam.get("type").getAsJsonObject();
                this.gemInfoType = typeInfo.get("type").getAsString();
            }
        }
        else
        {//from DB
            if (objInfo.has("gemInfo_bonus_name") && !objInfo.get("gemInfo_bonus_name").isJsonNull())
                this.gemInfoBonusName = objInfo.get("gemInfo_bonus_name").getAsString();
            if (objInfo.has("gemInfo_type") && !objInfo.get("gemInfo_type").isJsonNull())
                this.gemInfoType = objInfo.get("gemInfo_type").getAsString();
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        //'154129','Masterful Tidal Amethyst',,'+40 Mastery','PRISMATIC','154129'
        switch(saveInDBObj(new String[] {this.id +"", this.name, this.icon, this.itemSpell.getId()+"", this.gemInfoBonusName, this.gemInfoType}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;            
        }
        return false;
    }
    
    public void cloneItem(Item i)
    {
        if(i != null && i.isData())
        {
            //Copi atribute
            this.id = i.getId();
            this.name = i.getName();
            this.icon = i.getIcon();
            this.itemSpell = i.getItemSpell();
            this.gemInfoBonusName = i.getGemBonus();
            this.gemInfoType = i.getGemType();
            this.isInternalData = i.isInternalData();
            this.isData = i.isData();
            //Try save in DB if not exist
            if(!this.isInternalData)
            {
                saveInDB();
                Logs.infoLog(Item.class, "New Item in DB "+ this.id +" - "+ this.name);
            }
        }
    }
    
    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return id;}
    public String getName() { return this.name; }
    public String getGemBonus() { return this.gemInfoBonusName; }
    public String getGemType() { return this.gemInfoType; }
    public Spell getItemSpell() { return this.itemSpell; }
    public String getIcon() { return this.icon; }
    public String getIconRenderURL() { return getIconRenderURL(56); }
    public String getIconRenderURL(int size) 
    {
        return String.format(WoWAPIService.API_ITEM_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), size, this.icon) +".jpg";
    }
    
}
