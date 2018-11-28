/**
 * File : CharacterAchivementsList.java
 * Desc : Character Achivements List Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.achievement;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.blizzardAPI.APIInfo;
import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONObject;

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
        loadFromDB(id +"");
    }
    
    public CharacterAchivementsList(JSONObject info)
    {        
        super(PLAYER_ACHIEVEMENT_LIST_TABLE_NAME, PLAYER_ACHIEVEMENT_LIST_TABLE_KEY, PLAYER_ACHIEVEMENT_LIST_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        if(objInfo.get("id").getClass() == java.lang.Long.class)
        {//Info come to blizz
            this.id = ((Long) objInfo.get("id")).intValue();
            this.points = ((Long) objInfo.get("points")).intValue();
        }
        else
        {//Info come to DB
            this.id = (Integer) objInfo.get("id");
            this.points = (Integer) objInfo.get("points");
        }
        this.category = new CharacterAchivementsCategory((Integer) objInfo.get("category_id"));
        this.title = objInfo.get("title").toString();
        this.description = objInfo.get("description").toString();
        this.icon = objInfo.get("icon").toString();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        /* {"id", "category_id", "title", "points", "description", "icon"} */
        switch (saveInDBObj(new String[] {this.id+"", category.getId(), this.title, this.points +"", this.description, this.icon}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }

    //Getters and Setters
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }

    @Override
    public String getId() { return this.id +""; }
    public String getTitle() { return this.title; }
    public int getPoints() { return this.points; }
    public String getDescription() { return this.description; }
    public String getIconRenderURL() { return getIconRenderURL(56); }
    public String getIconRenderURL(int size) 
    {
        return String.format(APIInfo.API_ITEM_RENDER_URL, GeneralConfig.SERVER_LOCATION, size, this.icon) +".jpg";
    }
    public CharacterAchivementsCategory getCategory() { return this.category; }
    
    
}
