/**
 * File : Challenges.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.challenges;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.GameObject;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Challenge extends GameObject
{
    //Challenges DB
    public static final String CHALLENGES_TABLE_NAME = "guild_challenges";
    public static final String CHALLENGES_TABLE_KEY = "id";
    public static final String[] CHALLENGES_TABLE_STRUCTURE = {"id", "map_name",
                                                        "bronze_hours", "bronze_minutes", "bronze_seconds", "bronze_milliseconds",
                                                        "silver_hours", "silver_minutes", "silver_seconds", "silver_milliseconds",
                                                        "gold_hours", "gold_minutes", "gold_seconds", "gold_milliseconds"};
    //Attribute
    private int mapId;
    private String mapName;
    private List<ChallengeGroup> chGroups = new ArrayList<>();
    private int[] bronzeTime = new int[4]; //[h][m][s][ms]
    private int[] silverTime = new int[4]; //[h][m][s][ms]
    private int[] goldTime = new int[4];   //[h][m][s][ms]
    
    public Challenge(int id)
    {
        super(CHALLENGES_TABLE_NAME, CHALLENGES_TABLE_KEY, CHALLENGES_TABLE_STRUCTURE);
        loadFromDB(id);
        loadGroups();
    }
    
    //Load to JSON
    public Challenge(JsonObject mapInfo)
    {
        super(CHALLENGES_TABLE_NAME, CHALLENGES_TABLE_KEY, CHALLENGES_TABLE_STRUCTURE);
        saveInternalInfoObject(mapInfo);
    }
    
    private void loadGroups()
    {
        try {
            //dbConnect, select * from challenge_groups where challenge_id = this.mapId;
            JsonArray dbGroups = dbConnect.select(ChallengeGroup.CHALLENGE_GROUPS_TABLE_NAME,
                                                new String[] {"group_id"},
                                                "challenge_id=? order by time_date desc limit 3;", //Load last 3 groups
                                                new String[] {this.mapId +""});
            for(int i = 0; i < dbGroups.size(); i++)
            {
                int gID = dbGroups.get(i).getAsJsonObject().get("group_id").getAsInt();
                ChallengeGroup cgDb = new ChallengeGroup( gID );
                if(cgDb.isData()) chGroups.add(cgDb);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Challenge.class, "Fail to load Groups from challenge "+ this.mapId);
        }
    }
    
    @Override
    protected void saveInternalInfoObject(JsonObject exInfo)
    {
        this.mapId = exInfo.get("id").getAsInt();
        if (exInfo.has("name")) { // from blizzard
            this.mapName = exInfo.get("name").getAsString();
            //challenge time~
            if(exInfo.has("bronzeCriteria"))
            {
                JsonObject bronzeCriteria = exInfo.get("bronzeCriteria").getAsJsonObject();
                this.bronzeTime[0] = bronzeCriteria.get("hours").getAsInt();
                this.bronzeTime[1] = bronzeCriteria.get("minutes").getAsInt();
                this.bronzeTime[2] = bronzeCriteria.get("seconds").getAsInt();
                this.bronzeTime[3] = bronzeCriteria.get("milliseconds").getAsInt();
            }
            if(exInfo.has("silverCriteria"))
            {
                JsonObject bronzeCriteria = exInfo.get("silverCriteria").getAsJsonObject();
                this.silverTime[0] = bronzeCriteria.get("hours").getAsInt();
                this.silverTime[1] = bronzeCriteria.get("minutes").getAsInt();
                this.silverTime[2] = bronzeCriteria.get("seconds").getAsInt();
                this.silverTime[3] = bronzeCriteria.get("milliseconds").getAsInt();
            }
            if(exInfo.has("goldCriteria"))
            {
                JsonObject bronzeCriteria = exInfo.get("goldCriteria").getAsJsonObject();
                this.goldTime[0] = bronzeCriteria.get("hours").getAsInt();
                this.goldTime[1] = bronzeCriteria.get("minutes").getAsInt();
                this.goldTime[2] = bronzeCriteria.get("seconds").getAsInt();
                this.goldTime[3] = bronzeCriteria.get("milliseconds").getAsInt();
            }
        } else { // from DB
            this.mapName = exInfo.get("map_name").getAsString();
            //Bronze time
            this.bronzeTime[0] = exInfo.get("bronze_hours").getAsInt();
            this.bronzeTime[1] = exInfo.get("bronze_minutes").getAsInt();
            this.bronzeTime[2] = exInfo.get("bronze_seconds").getAsInt();
            this.bronzeTime[3] = exInfo.get("bronze_milliseconds").getAsInt();
            //Silver time
            this.silverTime[0] = exInfo.get("silver_hours").getAsInt();
            this.silverTime[1] = exInfo.get("silver_minutes").getAsInt();
            this.silverTime[2] = exInfo.get("silver_seconds").getAsInt();
            this.silverTime[3] = exInfo.get("silver_milliseconds").getAsInt();
            //Gold time
            this.goldTime[0] = exInfo.get("gold_hours").getAsInt();
            this.goldTime[1] = exInfo.get("gold_minutes").getAsInt();
            this.goldTime[2] = exInfo.get("gold_seconds").getAsInt();
            this.goldTime[3] = exInfo.get("gold_milliseconds").getAsInt();
        }
        this.isData = true;		
    }
    
    @Override
    public boolean saveInDB()
    {
        /*
         * {"id", "map_name",
         * "bronze_hours", "bronze_minutes", "bronze_seconds", "bronze_milliseconds",
         * "silver_hours", "silver_minutes", "silver_seconds", "silver_milliseconds",
         * "gold_hours", "gold_minutes", "gold_seconds", "gold_milliseconds"};
         */
        int saveInDBReturn = saveInDBObj(new String[] {this.mapId +"", this.mapName,
                            this.bronzeTime[0] +"", this.bronzeTime[1] +"", this.bronzeTime[2] +"", this.bronzeTime[3] +"",
                            this.silverTime[0] +"", this.silverTime[1] +"", this.silverTime[2] +"", this.silverTime[3] +"",
                            this.goldTime[0]   +"", this.goldTime[1]   +"", this.goldTime[2]   +"", this.goldTime[3]   +""});
        switch (saveInDBReturn)
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                saveInDBGroups();
                return true;

        }
        return false;
    }
    
    public void saveInDBGroups()
    {
        chGroups.forEach((chG) -> {
            chG.saveInDB();
        });
    }

    //Getters/Setters
    @Override
    public void setId(int mapId) { this.mapId = mapId; }
    public void setMapName(String mapName) { this.mapName = mapName; }
    public void addChallengeGroup(ChallengeGroup chGroup) { this.chGroups.add(chGroup); }
    @Override
    public int getId() { return this.mapId; }
    public String getMapName() { return this.mapName; }
    public List<ChallengeGroup> getChallengeGroups() { return this.chGroups; }
    public int isUpdateKey(ChallengeGroup cGroup)
    {
        try {
            //Gold
            Date cGold = new SimpleDateFormat("HH:mm:ss.SSS").parse(this.goldTime[0]+":"+this.goldTime[1]+":"+this.goldTime[2]+"."+this.goldTime[3]);
            Date cSilver = new SimpleDateFormat("HH:mm:ss.SSS").parse(this.silverTime[0]+":"+this.silverTime[1]+":"+this.silverTime[2]+"."+this.silverTime[3]);
            Date cBronze = new SimpleDateFormat("HH:mm:ss.SSS").parse(this.bronzeTime[0]+":"+this.bronzeTime[1]+":"+this.bronzeTime[2]+"."+this.bronzeTime[3]);
            //Group time
            Date gTime = new SimpleDateFormat("HH:mm:ss.SSS").parse(cGroup.getTimeHour()+":"+cGroup.getTimeMinutes()+":"+cGroup.getTimeSeconds()+"."+cGroup.getTimeMilliseconds());
            if(gTime.before(cGold)) return 3;
            if(gTime.before(cSilver)) return 2;
            if(gTime.before(cBronze)) return 1;
        } catch (ParseException ex) {
            Logs.errorLog(Challenge.class, "Fail to convert time group "+ ex);
        }
        return -1;
    }
    
    @Override
    public String toString()
    {
        String out = "Challenge: "+ this.mapId +" *--"+ this.mapName +"--*";
        out += "\n\tGroups!!-----------------------\n";
        for(ChallengeGroup chG : chGroups)
        {
            out += chG.toString() +"\n";
            out += "\t.....................................\n";
        }
        return out;
    }
}