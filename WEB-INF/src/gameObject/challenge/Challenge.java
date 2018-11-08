/**
 * File : Challenges.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.challenge;

import com.artOfWar.DataException;
import com.artOfWar.gameObject.GameObject;
import static com.artOfWar.gameObject.GameObject.SAVE_MSG_INSERT_OK;
import static com.artOfWar.gameObject.GameObject.SAVE_MSG_UPDATE_OK;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Challenge extends GameObject
{
    //Attribute
    private int mapId;
    private String mapName;
    private List<ChallengeGroup> chGroups;
    
    public Challenge(int id)
    {
        super(CHALLENGES_TABLE_NAME, CHALLENGES_TABLE_KEY, CHALLENGES_TABLE_STRUCTURE);
        chGroups = new ArrayList<>();
        loadFromDB(id+"");
        loadGroups();
    }
    
    //Load to JSON
    public Challenge(JSONObject mapInfo)
    {
        super(CHALLENGES_TABLE_NAME, CHALLENGES_TABLE_KEY, CHALLENGES_TABLE_STRUCTURE);
        chGroups = new ArrayList<>();
        saveInternalInfoObject(mapInfo);
    }
    
    private void loadGroups()
    {
        try {
            //dbConnect, select * from challenge_groups where challenge_id = this.mapId;
            JSONArray dbGroups = dbConnect.select(CHALLENGE_GROUPS_TABLE_NAME,
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
            System.out.println("Fail to load Groups from challenge "+ this.mapId);
        }
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject exInfo)
    {
        if(exInfo.get("id").getClass() == java.lang.Long.class)
        {//if info come to blizzAPI
            this.mapId = ((Long) exInfo.get("id")).intValue();
            this.mapName = exInfo.get("name").toString();
        }
        else
        {
            this.mapId = (Integer) exInfo.get("id");
            this.mapName = exInfo.get("map_name").toString();
        }
        this.isData = true;		
    }
    
    @Override
    public boolean saveInDB()
    {
        /* {"id", "map_name"}; */
        int saveInDBReturn = saveInDBObj(new String[] {this.mapId +"", this.mapName});
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
    public void setId(String mapId) { this.mapId = Integer.parseInt(mapId); }
    public void setMapName(String mapName) { this.mapName = mapName; }
    public void addChallengeGroup(ChallengeGroup chGroup) { this.chGroups.add(chGroup); }
    public int getMapId() { return this.mapId; }
    public String getMapName() { return this.mapName; }
    public List<ChallengeGroup> getChallengeGroups() { return this.chGroups; }
    
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