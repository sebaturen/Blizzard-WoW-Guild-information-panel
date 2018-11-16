/**
 * File : ItemMember.java
 * Desc : ItemMember Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.characters;

import com.artOfWar.DataException;
import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.dbConnect.DBStructure;
import com.artOfWar.gameObject.GameObject;
import com.artOfWar.gameObject.Item;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ItemMember extends GameObject
{
    //Item Member DB
    public static final String ITEMS_MEMBER_TABLE_NAME  = "items_member";
    public static final String ITEMS_MEMBER_TABLE_KEY   = "id";
    public static final String[] ITEMS_MEMBER_TABLE_STRUCTURE = {"id", "member_id", "item_id", "post_item",
                                                                "ilevel", "stats", "armor", "context", 
                                                                "azerita_level", "azerita_power", "tooltipGem_id", "toolTipEnchant_id"};
    //Attribute
    private int id;
    private int memberId;
    private Item item;
    private String position;
    private int ilevel;
    private JSONArray stats = new JSONArray();
    private int armor;
    private String context;
    private int azeritaLevel;
    private JSONArray azeritaPower = new JSONArray();
    private int tooltipGemId = -1;
    private int tooltipEnchantId = -1;
    
    public ItemMember(int id)
    {
        super(ITEMS_MEMBER_TABLE_NAME, ITEMS_MEMBER_TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        loadFromDB(id +"");
    }
    
    public ItemMember(String position, int memberId)
    {
        super(ITEMS_MEMBER_TABLE_NAME, ITEMS_MEMBER_TABLE_KEY, ITEMS_MEMBER_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] { "post_item", "member_id" }, new String[] { position, memberId+"" });
    }
    
    public ItemMember (JSONObject inf)
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
            this.item = loadItem((Integer) objInfo.get("item_id"));
            this.ilevel = (Integer) objInfo.get("ilevel");
            this.armor = (Integer) objInfo.get("armor");
            this.azeritaLevel = (Integer) objInfo.get("azerita_level");
            this.tooltipGemId = (Integer) objInfo.get("tooltipGem_id");
            this.tooltipEnchantId = (Integer) objInfo.get("toolTipEnchant_id");
            try {
                JSONParser parser = new JSONParser();
                String stat = objInfo.get("stats").toString();
                String azerita = objInfo.get("azerita_power").toString();
                if(stat.length() > 0)
                {
                    this.stats = (JSONArray) parser.parse(stat);
                }
                if(azerita.length() > 0)
                {
                    this.azeritaPower = (JSONArray) parser.parse(azerita);
                }
            } catch (ParseException ex) {
                Logger.getLogger(ItemMember.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {//load from blizzard
            this.item = loadItem(((Long) objInfo.get("id")).intValue());   
            this.ilevel = ((Long) objInfo.get("itemLevel")).intValue();   
            this.stats = (JSONArray) objInfo.get("stats");
            this.armor = ((Long) objInfo.get("armor")).intValue();
            JSONObject aLeve = (JSONObject) objInfo.get("azeriteItem");
            if(objInfo.containsKey("azeriteLevel"))
            {
                this.azeritaLevel = ((Long) aLeve.get("azeriteLevel")).intValue();                
            }
            if(objInfo.containsKey("azeriteEmpoweredItem"))
            {
                this.azeritaPower = (JSONArray) ((JSONObject) objInfo.get("azeriteEmpoweredItem")).get("azeritePowers");            
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
    
    private Item loadItem(int id)
    {
        Item it = new Item(id);
        if(!it.isInternalData())
        {
            try {
                Update up = new Update();
                it = up.getItemFromBlizz(id);
                it.saveInDB();
            } catch (IOException | ParseException | DataException ex) {
                System.out.println("Fail to get item info from blizzard.");
            }
        }
        return it;
    }
    
    @Override
    public boolean saveInDB() 
    {
        /* {"member_id", "item_id", "post_item",
         * "ilevel", "stats", "armor", "context", 
         * "azerita_level", "azerita_power", "tooltipGem_id", "toolTipEnchant_id"};
         */
        setTableStructur(DBStructure.outKey(ITEMS_MEMBER_TABLE_STRUCTURE));
        switch (saveInDBObj(new String[] {this.memberId +"", this.item.getId() +"", this.position,
                                        this.ilevel +"", this.stats.toString(), this.armor +"", this.context,
                                        this.azeritaLevel +"", this.azeritaPower.toString(), this.tooltipGemId +"", this.tooltipEnchantId +""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }
    
    //Setters and Getters
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
    public void setMemberId(int id) { this.memberId = id; }

    @Override
    public String getId() { return this.id+""; }
    public String getPosition() { return this.position; }
    public int getIlevel() { return ilevel; }
    
}