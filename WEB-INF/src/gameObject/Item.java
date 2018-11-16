/**
 * File : Item.java
 * Desc : Item object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import org.json.simple.JSONObject;

public class Item extends GameObject
{
    //Item DB
    public static final String ITEM_TABLE_NAME = "items";
    public static final String ITEM_TABLE_KEY = "id";
    public static final String[] ITEM_TABLE_STRUCTURE = {"id", "name", "icon", "gemInfo_bonus_name", "gemInfo_type"};
    
    //Atribute
    private int id;
    private String name;
    private String icon;
    private String gemInfoBonusName;
    private String gemInfoType;
    
    public Item(int id)
    {
        super(ITEM_TABLE_NAME,ITEM_TABLE_KEY,ITEM_TABLE_STRUCTURE);
        loadFromDB(id+"");
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
            this.id = ((Long) objInfo.get("id")).intValue();
        else
            this.id = (Integer) objInfo.get("id");
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
        switch(saveInDBObj(new String[] {this.id +"", this.name, this.icon, this.gemInfoBonusName, this.gemInfoType}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;            
        }
        return false;
    }

    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }

    @Override
    public String getId() { return id+"";}
    
    
}
