/**
 * File : News.java
 * Desc : News Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.gameObject.guild.achievement.GuildAchievementsList;
import com.blizzardPanel.Logs;
import com.blizzardPanel.update.blizzard.Update;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.characters.achievement.CharacterAchivementsList;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class New extends GameObject
{
    //DB News structure
    public static final String GUILD_NEWS_TABLE_NAME = "guild_news";
    public static final String GUILD_NEWS_TABLE_KEY = "id";
    public static final String[] GUILD_NEWS_TABLE_STRUCTURE = {"id", "type", "member_id", "timestamp", "context",
                                            "item_id", "guild_achievement_id", "player_achievement_id"};
    //Atributes
    private int id;
    private String type;
    private CharacterMember member;
    private String context;
    private Date timeStamp;
    private Item item;
    private GuildAchievementsList gAchievement;
    private CharacterAchivementsList cAchievement;
    
    public New(int id)
    {
        super(GUILD_NEWS_TABLE_NAME, GUILD_NEWS_TABLE_KEY, GUILD_NEWS_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public New(String type, String timestamp, String member_name)
    {
        super(GUILD_NEWS_TABLE_NAME, GUILD_NEWS_TABLE_KEY, GUILD_NEWS_TABLE_STRUCTURE);
        
        CharacterMember loadMember = new CharacterMember(member_name, GeneralConfig.getStringConfig("GUILD_REALM"));    
        try { //2018-10-17 02:39:00
            timestamp = Update.parseUnixTime(timestamp);
            timestamp = getDBDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp));
        } catch (ParseException ex) {
            Logs.errorLog(New.class, "Fail to convert date from guild news! "+ this.id +" - "+ ex);
        }
        //Load only if member have a info
        if(loadMember.isInternalData())
            loadFromDBUniqued(new String[] {"type", "timestamp", "member_id"}, new String[] { type, timestamp, loadMember.getId()+"" });
        
    }
    
    public New(JsonObject inf)
    {
        super(GUILD_NEWS_TABLE_NAME, GUILD_NEWS_TABLE_KEY, GUILD_NEWS_TABLE_STRUCTURE);
        saveInternalInfoObject(inf);        
    }
            
    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        String dateStamp = objInfo.get("timestamp").getAsString();
        if(objInfo.has("id"))
        {//Load from dB
            this.id = objInfo.get("id").getAsInt();
            this.member = new CharacterMember(objInfo.get("member_id").getAsInt());
        }
        else
        {//Load from blizz
            dateStamp = Update.parseUnixTime(objInfo.get("timestamp").getAsString());
            this.member = new CharacterMember(objInfo.get("character").getAsString(), GeneralConfig.getStringConfig("GUILD_REALM"));
        }
        try { //2018-10-17 02:39:00
            this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStamp);
        } catch (ParseException ex) {
            Logs.errorLog(New.class, "Fail to convert date from guild news! "+ this.id +" - "+ ex);
        }
        this.type = objInfo.get("type").getAsString();
        this.context = objInfo.get("context").getAsString();

        if (objInfo.has("id")) { // load from DB
            switch (this.type) {
                case "itemLoot": case "itemCraft": case "itemPurchase":
                    this.item = new Item(objInfo.get("item_id").getAsInt());
                    break;
                case "playerAchievement":
                    this.cAchievement = new CharacterAchivementsList(objInfo.get("player_achievement_id").getAsInt());
                    break;
                case "guildAchievement":
                    this.gAchievement = new GuildAchievementsList(objInfo.get("guild_achievement_id").getAsInt());
                    break;
            }
        } else { // load from blizz
            switch (this.type) {
                case "itemLoot": case "itemCraft": case "itemPurchase":
                    this.item = new Item(objInfo.get("itemId").getAsInt());
                    break;
                case "playerAchievement":
                    int cAchId = objInfo.get("achievement").getAsJsonObject().get("id").getAsInt();
                    this.cAchievement = new CharacterAchivementsList(cAchId);
                    break;
                case "guildAchievement":
                    int gAchId = objInfo.get("achievement").getAsJsonObject().get("id").getAsInt();
                    this.gAchievement = new GuildAchievementsList(gAchId);
                    break;
            }
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        String[] dbStruct = new String[0];
        String[] val = new String[0];
        switch(this.type)
        {
            case "itemLoot": case "itemCraft": case "itemPurchase":
                if(this.id != 0)
                {
                    dbStruct = new String[] {"id", "type", "member_id", "timestamp", "context", "item_id"};                    
                    val = new String[] {this.id +"", this.type, this.member.getId()+"", getDBDate(this.timeStamp), this.context, this.item.getId()+"" };
                }
                else
                {
                    dbStruct = new String[] {"type", "member_id", "timestamp", "context", "item_id"};     
                    val = new String[] { this.type, this.member.getId()+"", getDBDate(this.timeStamp), this.context, this.item.getId()+"" };               
                }
                break;
            case "playerAchievement":
                if(this.id != 0)
                {
                    dbStruct = new String[] {"id", "type", "member_id", "timestamp", "context", "player_achievement_id"};
                    val = new String[] {this.id +"", this.type, this.member.getId()+"", getDBDate(this.timeStamp), this.context, this.cAchievement.getId()+"" };
                }
                else
                {
                    dbStruct = new String[] {"type", "member_id", "timestamp", "context", "player_achievement_id"};
                    val = new String[] {this.type, this.member.getId()+"", getDBDate(this.timeStamp), this.context, this.cAchievement.getId()+"" };
                }
                break;
            case "guildAchievement":
                if(this.id != 0)
                {
                    dbStruct = new String[] {"id", "type", "member_id", "timestamp", "context", "guild_achievement_id"};
                    val = new String[] {this.id +"", this.type, this.member.getId()+"", getDBDate(this.timeStamp), this.context, this.gAchievement.getId()+"" };
                }
                else
                {                    
                    dbStruct = new String[] {"type", "member_id", "timestamp", "context", "guild_achievement_id"};
                    val = new String[] {this.type, this.member.getId()+"", getDBDate(this.timeStamp), this.context, this.gAchievement.getId()+"" };
                }
                break;
        }
        setTableStructur(dbStruct);
        //Only if member exist in guild
        if(this.member.isInternalData())
        {
            switch(saveInDBObj(val))
            {            
                case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                    return true;
            }            
        }
        return false;
    }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return this.id; }
    public String getType() { return this.type; }
    public CharacterMember getMember() { return this.member; }
    public String getContext() { return this.context; }
    public Date getTimeStamp() { return this.timeStamp; }
    public String getTimeStampString() { return getDBDate(this.timeStamp); }
    public Item getItem() { return this.item; }
    public GuildAchievementsList getGuildAchievement() { return this.gAchievement; }
    public CharacterAchivementsList getCharacterAchievement() { return this.cAchievement; }
    
    
}
