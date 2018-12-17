/**
 * File : GuildAchivement.java
 * Desc : Guild Achivement Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.achievement;

import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.GameObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;

public class GuildAchievement extends GameObject
{
    //TABLE STRUCTURE
    public static final String GUILD_ACHIEVEMENTS_TABLE_NAME = "guild_achievements";
    public static final String GUILD_ACHIEVEMENTS_TABLE_KEY = "achievement_id";
    public static final String[] GUILD_ACHIEVEMENTS_TABLE_STRUCTURE = {"achievement_id", "time_completed"};
    
    //Atribute
    private int achivementId;
    private GuildAchievementsList achievement;
    private Date timeCompleted;
    
    public GuildAchievement(int ahId)
    {
        super(GUILD_ACHIEVEMENTS_TABLE_NAME, GUILD_ACHIEVEMENTS_TABLE_KEY, GUILD_ACHIEVEMENTS_TABLE_STRUCTURE);
        loadFromDB(ahId);
    }
    
    public GuildAchievement(JSONObject info)
    {
        super(GUILD_ACHIEVEMENTS_TABLE_NAME, GUILD_ACHIEVEMENTS_TABLE_KEY, GUILD_ACHIEVEMENTS_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        this.achivementId = (Integer) objInfo.get("achievement_id");
        this.achievement = new GuildAchievementsList(this.achivementId);
        try { //2018-10-17 02:39:00
            this.timeCompleted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(objInfo.get("time_completed").toString());
        } catch (ParseException ex) {
            Logs.errorLog(GuildAchievement.class, "(DB) Fail to convert date from challenge group! "+ this.achivementId +" - "+ ex);
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {        
        /* {"achievement_id", "time_completed"} */
        switch (saveInDBObj(new String[] {this.achivementId +"", getDBDate(this.timeCompleted)}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }

    @Override
    public void setId(int id) 
    { 
        this.achivementId = id; 
        this.achievement = new GuildAchievementsList(this.achivementId); 
    }

    @Override
    public int getId() { return this.achivementId; }
    public GuildAchievementsList getAchievement() { return this.achievement; }
    public Date getTimeCompleted() { return this.timeCompleted; }
    
    
}
