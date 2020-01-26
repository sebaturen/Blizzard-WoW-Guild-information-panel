/**
 * File : ItemMember.java
 * Desc : ItemMember Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.Spell;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CharacterItem extends GameObject
{
    //Item Member DB
    public static final String ITEMS_MEMBER_TABLE_NAME  = "character_items";
    public static final String ITEMS_MEMBER_TABLE_KEY   = "id";
    public static final String[] ITEMS_MEMBER_TABLE_STRUCTURE = {"id", "member_id", "item_id", "quality", "post_item",
                                                                "ilevel", "stats", "armor", "context", 
                                                                "azerite_level", "azerite_power", "tooltipGem_id", "toolTipEnchant_id"};
    public static final String[] ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE = {"item_id", "quality", "ilevel", "stats", "armor", "context", 
                                                                "azerite_level", "azerite_power", "tooltipGem_id", "toolTipEnchant_id"};
    public static final String[] ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE_VALUES = {"0", "0", "0", "0", "0", "0","0","0","0","0"};
    
    //Attribute
    private int id;
    private int memberId;
    private Item item;
    private int quality;
    private String position;
    private int ilevel;
    private JSONArray stats = new JSONArray();
    private int armor;
    private String context;
    private int azeriteLevel;
    private JSONArray azeritePower = new JSONArray();
    private int tooltipGemId = -1;
    private int tooltipEnchantId = -1;
    
    public CharacterItem(int id)
    {
        super(ITEMS_MEMBER_TABLE_NAME, ITEMS_MEMBER_TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public CharacterItem(String position, int memberId)
    {
        super(ITEMS_MEMBER_TABLE_NAME, ITEMS_MEMBER_TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] { "post_item", "member_id" }, new String[] { position, memberId+"" });
    }
    
    public CharacterItem(JSONObject inf)
    {
        super(ITEMS_MEMBER_TABLE_NAME, ITEMS_MEMBER_TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);
    }
       
    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        if(objInfo.containsKey("member_id"))
        {   
            //load from DB
            this.id = (Integer) objInfo.get("id");
            this.memberId = (Integer) objInfo.get("member_id");
            this.item = new Item((Integer) objInfo.get("item_id"));
            this.quality = (Integer) objInfo.get("quality");            
            this.ilevel = (Integer) objInfo.get("ilevel");
            this.armor = (Integer) objInfo.get("armor");
            this.azeriteLevel = (Integer) objInfo.get("azerite_level");
            this.tooltipGemId = (Integer) objInfo.get("tooltipGem_id");
            this.tooltipEnchantId = (Integer) objInfo.get("toolTipEnchant_id");
            try {
                JSONParser parser = new JSONParser();
                String stat = objInfo.get("stats").toString();
                String azerita = objInfo.get("azerite_power").toString();
                if(stat.length() > 2) //+2 becouse JSONArray use minimus '[]'
                {
                    this.stats = (JSONArray) parser.parse(stat);
                }
                if(azerita.length() > 2) //+2 becouse JSONArray use minimus '[]'
                {
                    this.azeritePower = (JSONArray) parser.parse(azerita);
                }
            } catch (ParseException ex) {
                Logs.errorLog(com.blizzardPanel.gameObject.characters.CharacterItem.class, "Fail to parse stats o azerita power from item "+ this.id +" - "+ ex);
            }
        }
        else
        {//load from blizzard
            this.item = new Item(((Long) objInfo.get("id")).intValue());
            this.quality = ((Long) objInfo.get("quality")).intValue();
            this.ilevel = ((Long) objInfo.get("itemLevel")).intValue();   
            this.stats = (JSONArray) objInfo.get("stats");
            this.armor = ((Long) objInfo.get("armor")).intValue();
            JSONObject aLeve = (JSONObject) objInfo.get("azeriteItem");
            if(aLeve != null && aLeve.containsKey("azeriteLevel"))
            {
                this.azeriteLevel = ((Long) aLeve.get("azeriteLevel")).intValue();                
            }
            if(objInfo.containsKey("azeriteEmpoweredItem"))
            {
                this.azeritePower = (JSONArray) ((JSONObject) objInfo.get("azeriteEmpoweredItem")).get("azeritePowers");            
            }
            JSONObject toolTipe = (JSONObject) objInfo.get("tooltipParams");
            if(toolTipe.containsKey("gem0"))
                this.tooltipGemId = ((Long) toolTipe.get("gem0")).intValue();
            if(toolTipe.containsKey("enchant"))
                this.tooltipEnchantId = ((Long) toolTipe.get("enchant")).intValue();
             
        }
        this.position = objInfo.get("post_item").toString();
        this.context = objInfo.get("context").toString();
        this.isData = true;
    }
        
    @Override
    public boolean saveInDB() 
    {
        /* {"member_id", "item_id", "quality", "post_item",
         * "ilevel", "stats", "armor", "context", 
         * "azerita_level", "azerita_power", "tooltipGem_id", "toolTipEnchant_id"};
         */
        setTableStructur(DBStructure.outKey(ITEMS_MEMBER_TABLE_STRUCTURE));
        switch (saveInDBObj(new String[] {this.memberId +"", this.item.getId() +"", this.quality +"", this.position,
                                        this.ilevel +"", this.stats.toString(), this.armor +"", this.context,
                                        this.azeriteLevel +"", this.azeritePower.toString(), this.tooltipGemId +"", this.tooltipEnchantId +""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }
    
    //Setters and Getters
    @Override
    public void setId(int id) { this.id = id; }
    public void setMemberId(int id) { this.memberId = id; }

    @Override
    public int getId() { return this.id; }
    public String getPosition() { return this.position; }
    public int getIlevel() { return ilevel; }
    public Item getItem() { return this.item; }
    public int getQuality() { return this.quality; }
    public int getArmor() { return this.armor; }
    public Item getGem() { if(this.tooltipGemId != -1) return new Item(this.tooltipGemId); else return null; }
    public int getAzeriteLevel() { return this.azeriteLevel; }
    public Spell[] getAzeritePower() 
    {
        Spell[] azPower = new Spell[this.azeritePower.size()];
        if(this.azeritePower.size() > 0)
        {
            Update up = null;     
            for(int i = this.azeritePower.size()-1, j = 0; i >= 0 ; i--,j++)
            {
                JSONObject power = (JSONObject) this.azeritePower.get(i);
                int spellID = ((Long) power.get("spellId")).intValue();
                if(spellID != 0)
                {
                    Spell azPowerD = new Spell( spellID );
                    if(!azPowerD.isInternalData())
                    {
                        try {
                            if (up == null) up = new Update();  
                            azPowerD = up.getSpellInformationBlizz(((Long) power.get("spellId")).intValue());
                        } catch (DataException | IOException | ParseException ex) {
                            Logs.errorLog(com.blizzardPanel.gameObject.characters.CharacterItem.class, "Fail to get azerita spell from blizz "+ spellID +" - "+ ex);
                        }
                    }
                    azPower[j] = azPowerD;
                }
            }            
            
        }
        return azPower;
    }
    public Stat[] getStats() 
    {
        Stat[] itemStats = new Stat[this.stats.size()];
        for(int i = 0; i < this.stats.size(); i++)
        {
            JSONObject stat = (JSONObject) this.stats.get(i);
            itemStats[i] = new Stat( ((Long)stat.get("stat")).intValue());
            itemStats[i].setAmount(((Long) stat.get("amount")).intValue());
        }
        return itemStats;
    }
    
}