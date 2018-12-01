/**
 * File : Challenges.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.challenges;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.GameObject;
import static com.blizzardPanel.gameObject.GameObject.SAVE_MSG_INSERT_OK;
import static com.blizzardPanel.gameObject.GameObject.SAVE_MSG_UPDATE_OK;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Challenge extends GameObject
{
    //Challenges DB
    public static final String CHALLENGES_TABLE_NAME = "challenges";
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
        loadFromDB(id+"");
        loadGroups();
    }
    
    //Load to JSON
    public Challenge(JSONObject mapInfo)
    {
        super(CHALLENGES_TABLE_NAME, CHALLENGES_TABLE_KEY, CHALLENGES_TABLE_STRUCTURE);
        saveInternalInfoObject(mapInfo);
    }
    
    private void loadGroups()
    {
        try {
            //dbConnect, select * from challenge_groups where challenge_id = this.mapId;
            JSONArray dbGroups = dbConnect.select(ChallengeGroup.CHALLENGE_GROUPS_TABLE_NAME,
                                                new String[] {"group_id"},
                                                "challenge_id=? order by time_date desc limit 3;", //cargando los ulimots 3
                                                new String[] {this.mapId +""});
            for(int i = 0; i < dbGroups.size(); i++)
            {
                int gID = (Integer)((JSONObject)dbGroups.get(i)).get("group_id");
                ChallengeGroup cgDb = new ChallengeGroup( gID );
                if(cgDb.isData()) chGroups.add(cgDb);
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to load Groups from challenge "+ this.mapId);
        }
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject exInfo)
    {
        if(exInfo.get("id").getClass() == java.lang.Long.class)
        {//if info come to blizzAPI
            this.mapId = ((Long) exInfo.get("id")).intValue();
            this.mapName = exInfo.get("name").toString();
            //challenge time~
            if(exInfo.containsKey("bronzeCriteria"))
            {
                JSONObject bronzeCriterial = (JSONObject) exInfo.get("bronzeCriteria");
                this.bronzeTime[0] = ((Long) bronzeCriterial.get("hours")).intValue();
                this.bronzeTime[1] = ((Long) bronzeCriterial.get("minutes")).intValue();
                this.bronzeTime[2] = ((Long) bronzeCriterial.get("seconds")).intValue();
                this.bronzeTime[3] = ((Long) bronzeCriterial.get("milliseconds")).intValue();
            }
            if(exInfo.containsKey("silverCriteria"))
            {
                JSONObject bronzeCriterial = (JSONObject) exInfo.get("silverCriteria");
                this.silverTime[0] = ((Long) bronzeCriterial.get("hours")).intValue();
                this.silverTime[1] = ((Long) bronzeCriterial.get("minutes")).intValue();
                this.silverTime[2] = ((Long) bronzeCriterial.get("seconds")).intValue();
                this.silverTime[3] = ((Long) bronzeCriterial.get("milliseconds")).intValue();
            }
            if(exInfo.containsKey("goldCriteria"))
            {
                JSONObject bronzeCriterial = (JSONObject) exInfo.get("goldCriteria");
                this.goldTime[0] = ((Long) bronzeCriterial.get("hours")).intValue();
                this.goldTime[1] = ((Long) bronzeCriterial.get("minutes")).intValue();
                this.goldTime[2] = ((Long) bronzeCriterial.get("seconds")).intValue();
                this.goldTime[3] = ((Long) bronzeCriterial.get("milliseconds")).intValue();
            }
        }
        else
        {
            this.mapId = (Integer) exInfo.get("id");
            this.mapName = exInfo.get("map_name").toString();
            //Bronze time
            this.bronzeTime[0] = (Integer) exInfo.get("bronze_hours");
            this.bronzeTime[1] = (Integer) exInfo.get("bronze_minutes");
            this.bronzeTime[2] = (Integer) exInfo.get("bronze_seconds");
            this.bronzeTime[3] = (Integer) exInfo.get("bronze_milliseconds");
            //Silver time
            this.silverTime[0] = (Integer) exInfo.get("silver_hours");
            this.silverTime[1] = (Integer) exInfo.get("silver_minutes");
            this.silverTime[2] = (Integer) exInfo.get("silver_seconds");
            this.silverTime[3] = (Integer) exInfo.get("silver_milliseconds");
            //Gold time
            this.goldTime[0] = (Integer) exInfo.get("gold_hours");
            this.goldTime[1] = (Integer) exInfo.get("gold_minutes");
            this.goldTime[2] = (Integer) exInfo.get("gold_seconds");
            this.goldTime[3] = (Integer) exInfo.get("gold_milliseconds");
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
    public int isUpdateKey(ChallengeGroup cgroup)
    {
        try {
            //Gold
            Date cGold = new SimpleDateFormat("HH:mm:ss.SSS").parse(this.goldTime[0]+":"+this.goldTime[1]+":"+this.goldTime[2]+"."+this.goldTime[3]);
            Date cSilver = new SimpleDateFormat("HH:mm:ss.SSS").parse(this.silverTime[0]+":"+this.silverTime[1]+":"+this.silverTime[2]+"."+this.silverTime[3]);
            Date cBronze = new SimpleDateFormat("HH:mm:ss.SSS").parse(this.bronzeTime[0]+":"+this.bronzeTime[1]+":"+this.bronzeTime[2]+"."+this.bronzeTime[3]);
            //Group time
            Date gTime = new SimpleDateFormat("HH:mm:ss.SSS").parse(cgroup.getTimeHour()+":"+cgroup.getTimeMinutes()+":"+cgroup.getTimeSeconds()+"."+cgroup.getTimeMilliseconds());
            if(gTime.before(cGold)) return 3;
            if(gTime.before(cSilver)) return 2;
            if(gTime.before(cBronze)) return 1;
        } catch (ParseException ex) {
            Logs.saveLogln("Fail to convert time group "+ ex);
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