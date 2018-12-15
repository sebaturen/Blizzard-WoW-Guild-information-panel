/**
 * File : Item.java
 * Desc : Item object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.exceptions.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.APIInfo;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.exceptions.ConfigurationException;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Item extends GameObject
{
    //Item DB
    public static final String ITEM_TABLE_NAME = "items";
    public static final String ITEM_TABLE_KEY = "id";
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
        super(ITEM_TABLE_NAME,ITEM_TABLE_KEY,ITEM_TABLE_STRUCTURE);
        loadFromDB(id);
        if(!this.isInternalData)
        {
            try 
            {
                Update up = new Update();
                cloneItem(up.getItemFromBlizz(id));
            } catch (IOException | ParseException | DataException ex) {
                Logs.saveLogln("Fail to get item info from blizzard. - "+ ex);
            }
        }
    }
    
    public Item(JSONObject inf)
    {
        super(ITEM_TABLE_NAME,ITEM_TABLE_KEY,ITEM_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo)
    {
        if(objInfo.get("id").getClass() == java.lang.Long.class) //if info come to blizzAPI or DB
        {
            this.id = ((Long) objInfo.get("id")).intValue();
            JSONArray itemSpellBlizz = (JSONArray) objInfo.get("itemSpells");
            if(itemSpellBlizz.size() > 0)
            {
                int spellId = ((Long) ((JSONObject) itemSpellBlizz.get(0)).get("spellId")).intValue();
                this.itemSpell = new Spell(spellId);
                if(!this.itemSpell.isInternalData())
                {
                    try {
                        Update up = new Update();
                        this.itemSpell = up.getSpellInformationBlizz(spellId);
                    } catch (IOException | ParseException | DataException ex) {
                        Logs.saveLogln("Fail to get blizzard spell information "+ spellId +" - (spell in item) - "+ ex);
                    } catch (ConfigurationException ex) {
                        Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
                        System.exit(-1);
                    }
                }
            }
            else
            {
                this.itemSpell = new Spell(0);
            }
        }
        else
        {
            this.id = (Integer) objInfo.get("id");
            int spellId = 0;
            if(objInfo.get("itemSpell") != null)
            {
                spellId = (Integer) objInfo.get("itemSpell");
            }
            //allweys is declarate becouse in save we need a spell ID, and spell id 0 is a null spell.
            this.itemSpell = new Spell(spellId);
        }
        this.name = objInfo.get("name").toString();
        String iconUrl = "";
        //Not all item have a icon U_U
        if(objInfo.containsKey("icon")) iconUrl = objInfo.get("icon").toString();
        this.icon = iconUrl;
        if(objInfo.containsKey("gemInfo"))
        {//blizzard API info
            JSONObject vGam = (JSONObject) objInfo.get("gemInfo");
            JSONObject bonusInfo = (JSONObject) vGam.get("bonus");
            JSONObject typeInfo = (JSONObject) vGam.get("type");
            this.gemInfoBonusName = bonusInfo.get("name").toString();
            this.gemInfoType = typeInfo.get("type").toString();
        }
        else
        {//from DB      
            if(objInfo.get("gemInfo_bonus_name") != null)
                this.gemInfoBonusName = objInfo.get("gemInfo_bonus_name").toString();
            if(objInfo.get("gemInfo_type") != null)
                this.gemInfoType = objInfo.get("gemInfo_type").toString();
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
                Logs.saveLogln("New Item in DB "+ this.id +" - "+ this.name);
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
        try {
            return String.format(APIInfo.API_ITEM_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), size, this.icon) +".jpg";
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
            return null;
        }
    }
    
}
