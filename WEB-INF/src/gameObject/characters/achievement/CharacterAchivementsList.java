/**
 * File : CharacterAchivementsList.java
 * Desc : Character Achivements List Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.achievement;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.blizzardAPI.WoWAPIService;
import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

public class CharacterAchivementsList extends GameObject
{
    //DB TABLE STRUCTURE
    public static final String PLAYER_ACHIEVEMENT_LIST_TABLE_NAME = "player_achievement_list";
    public static final String PLAYER_ACHIEVEMENT_LIST_TABLE_KEY = "id";
    public static final String[] PLAYER_ACHIEVEMENT_LIST_TABLE_STRUCTURE = { "id", "category_id", "title", "points", 
                                                                                "description", "icon"};

    //Atribute
    private int id;
    private CharacterAchivementsCategory category;
    private String title;
    private int points;
    private String description;
    private String icon;
    
    public CharacterAchivementsList(int id)
    {
        super(PLAYER_ACHIEVEMENT_LIST_TABLE_NAME, PLAYER_ACHIEVEMENT_LIST_TABLE_KEY, PLAYER_ACHIEVEMENT_LIST_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public CharacterAchivementsList(JsonObject info)
    {        
        super(PLAYER_ACHIEVEMENT_LIST_TABLE_NAME, PLAYER_ACHIEVEMENT_LIST_TABLE_KEY, PLAYER_ACHIEVEMENT_LIST_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();
        this.points = objInfo.get("points").getAsInt();
        this.category = new CharacterAchivementsCategory(objInfo.get("category_id").getAsInt());
        this.title = objInfo.get("title").getAsString();
        this.description = objInfo.get("description").getAsString();
        this.icon = objInfo.get("icon").getAsString();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        /* {"id", "category_id", "title", "points", "description", "icon"} */
        switch (saveInDBObj(new String[] {this.id+"", category.getId()+"", this.title, this.points +"", this.description, this.icon}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }

    //Getters and Setters
    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return this.id; }
    public String getTitle() { return this.title; }
    public int getPoints() { return this.points; }
    public String getDescription() { return this.description; }
    public String getIconRenderURL() { return getIconRenderURL(56); }
    public String getIconRenderURL(int size) 
    {
        return String.format(WoWAPIService.API_ITEM_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), size, this.icon) +".jpg";
    }
    public CharacterAchivementsCategory getCategory() { return this.category; }
    
    
}
