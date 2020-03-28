/**
 * File : ItemMember.java
 * Desc : ItemMember Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.update.blizzard.Update;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.Spell;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CharacterItem extends GameObject
{
    //Item Member DB
    public static final String TABLE_NAME  = "character_items";
    public static final String TABLE_KEY   = "id";
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
    private JsonArray stats = new JsonArray();
    private int armor;
    private String context;
    private int azeriteLevel;
    private JsonArray azeritePower = new JsonArray();
    private int tooltipGemId = -1;
    private int tooltipEnchantId = -1;
    
    public CharacterItem(int id)
    {
        super(TABLE_NAME, TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public CharacterItem(String position, int memberId)
    {
        super(TABLE_NAME, TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] { "post_item", "member_id" }, new String[] { position, memberId+"" });
    }
    
    public CharacterItem(JsonObject inf)
    {
        super(TABLE_NAME, TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);
    }
       
    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        if(objInfo.has("member_id"))
        {   
            //load from DB
            this.id = objInfo.get("id").getAsInt();
            this.memberId = objInfo.get("member_id").getAsInt();
            this.item = new Item(objInfo.get("item_id").getAsInt());
            this.quality = objInfo.get("quality").getAsInt();
            this.ilevel = objInfo.get("ilevel").getAsInt();
            this.armor = objInfo.get("armor").getAsInt();
            this.azeriteLevel = objInfo.get("azerite_level").getAsInt();
            this.tooltipGemId = objInfo.get("tooltipGem_id").getAsInt();
            this.tooltipEnchantId = objInfo.get("toolTipEnchant_id").getAsInt();
            String stat = objInfo.get("stats").getAsString();
            String azerita = objInfo.get("azerite_power").getAsString();
            if(stat.length() > 2) //+2 because JSONArray use `minimo 1` '[]'
            {
                this.stats = JsonParser.parseString(stat).getAsJsonArray();
            }
            if(azerita.length() > 2) //+2 because JSONArray use `minimo 1` '[]'
            {
                this.azeritePower = JsonParser.parseString(azerita).getAsJsonArray();
            }
        }
        else
        {//load from blizzard
            this.item = new Item(objInfo.get("id").getAsInt());
            this.quality = objInfo.get("quality").getAsInt();
            this.ilevel = objInfo.get("itemLevel").getAsInt();
            this.stats = objInfo.get("stats").getAsJsonArray();
            this.armor = objInfo.get("armor").getAsInt();
            if(objInfo.has("azeriteItem") && objInfo.get("azeriteItem").getAsJsonObject().has("azeriteLevel"))
            {
                this.azeriteLevel = objInfo.get("azeriteItem").getAsJsonObject().get("azeriteLevel").getAsInt();
            }
            if(objInfo.has("azeriteEmpoweredItem"))
            {
                this.azeritePower = objInfo.get("azeriteEmpoweredItem").getAsJsonObject().get("azeritePowers").getAsJsonArray();
            }
            JsonObject toolTipe = objInfo.get("tooltipParams").getAsJsonObject();
            if(toolTipe.has("gem0"))
                this.tooltipGemId = toolTipe.get("gem0").getAsInt();
            if(toolTipe.has("enchant"))
                this.tooltipEnchantId = toolTipe.get("enchant").getAsInt();
             
        }
        this.position = objInfo.get("post_item").getAsString();
        this.context = objInfo.get("context").getAsString();
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
            for(int i = this.azeritePower.size()-1, j = 0; i >= 0 ; i--,j++)
            {
                JsonObject power = this.azeritePower.get(i).getAsJsonObject();
                int spellID = power.get("spellId").getAsInt();
                if(spellID != 0)
                {
                    Spell azPowerD = new Spell( spellID );
                    if(!azPowerD.isInternalData())
                    {
                        azPowerD = Update.shared.getSpellInformationBlizz(power.get("spellId").getAsInt());
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
            JsonObject stat = this.stats.get(i).getAsJsonObject();
            itemStats[i] = new Stat( stat.get("stat").getAsInt() );
            itemStats[i].setAmount( stat.get("amount").getAsInt() );
        }
        return itemStats;
    }
    
}