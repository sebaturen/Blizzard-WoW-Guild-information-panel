/**
 * File : GuildAchivements.java
 * Desc : GuildAchivements Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.achievement;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.blizzardAPI.WoWAPIService;
import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

public class GuildAchievementsList extends GameObject
{
    //Guild Achivements lists DB
    public static final String GUILD_ACHIEVEMENTS_LISTS_TABLE_NAME = "guild_achievements_list";
    public static final String GUILD_ACHIEVEMENTS_LISTS_TABLE_KEY = "id";
    public static final String[] GUILD_ACHIEVEMENTS_LISTS_TABLE_STRUCTURE = {"id", "title", "description",
                                                                            "icon", "points", "classification"};
    //Atribute
    private int id;
    private String title;
    private String description;
    private String icon;
    private int points;
    private String classification;

    public GuildAchievementsList(int id)
    {
        super(GUILD_ACHIEVEMENTS_LISTS_TABLE_NAME, GUILD_ACHIEVEMENTS_LISTS_TABLE_KEY, GUILD_ACHIEVEMENTS_LISTS_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    public GuildAchievementsList(JsonObject exInfo)
    {
        super(GUILD_ACHIEVEMENTS_LISTS_TABLE_NAME, GUILD_ACHIEVEMENTS_LISTS_TABLE_KEY, GUILD_ACHIEVEMENTS_LISTS_TABLE_STRUCTURE);
        saveInternalInfoObject(exInfo);
    }
    
    @Override
    protected void saveInternalInfoObject(JsonObject exInfo)
    {
        this.id = exInfo.get("id").getAsInt();
        this.points = exInfo.get("points").getAsInt();
        this.title = exInfo.get("title").getAsString();
        this.description = exInfo.get("description").getAsString();
        this.icon = exInfo.get("icon").getAsString();
        this.classification = exInfo.get("classification").getAsString();
        this.isData = true;
    }
	
    @Override
    public boolean saveInDB()
    {
        /* {"id", "title", "description",
         * "icon", "points", "classification"};
         */
        switch (saveInDBObj(new String[] {this.id +"", this.title, this.description, 
                                          this.icon, this.points +"", this.classification}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }	
	
    //Getters AND setters
    @Override
    public void setId(int id) { this.id = id; }
    @Override
    public int getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getClassification() { return this.classification; }
    public String getIconRenderURL() { return getIconRenderURL(56); }
    public String getIconRenderURL(int size) 
    {
        return String.format(WoWAPIService.API_ITEM_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), size, this.icon) +".jpg";
    }
}
