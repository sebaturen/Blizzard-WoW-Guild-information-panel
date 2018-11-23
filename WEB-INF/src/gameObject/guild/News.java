/**
 * File : News.java
 * Desc : News Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.characters.CharacterAchivementsList;
import com.blizzardPanel.gameObject.characters.Member;
import java.util.Date;
import org.json.simple.JSONObject;

public class News extends GameObject
{
    //DB News structure
    public static final String GUILD_NEWS_TABLE_NAME = "guild_news";
    public static final String GUILD_NEWS_TABLE_KEY = "id";
    public static final String[] GUILD_NEWS_TABLE_STRUCTURE = {"id", "type", "member_id", "timestamp", 
                                            "item_id", "guild_achievement_id", "player_achievement_id"};
    //Atributes
    private int id;
    private String type;
    private Member member;
    private Date timeStamp;
    private Item item;
    private GuildAchievementsList gAchievement;
    private CharacterAchivementsList cAchievement;

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveInDB() 
    {
        String[] dbStruct;
        switch(type)
        {
            case "itemLoot":
                if id entonces poner id
                dbStruct = new String[] {"id", "type", "member_id", "timestamp", 
                            "item_id", "guild_achievement_id", "player_achievement_id"};
                break;
            case "playerAchievement":
                break;
            case "guildAchievement":
                break;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }

    @Override
    public String getId() { return this.id +""; }
    
    
}
