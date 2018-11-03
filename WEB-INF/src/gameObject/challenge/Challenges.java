/**
 * File : Challenges.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.challenge;

import com.artOfWar.gameObject.GameObject;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class Challenges extends GameObject
{
    //Attribute
    private int mapId;
    private String mapName;
    private List<ChallengeGroup> chGroups;
    
    //Constant
    private static final String TABLE_NAME = "challenges";
    private static final String[] TABLE_STRUCTURE = {"id", "map_name"};

    public Challenges()
    {
        super(TABLE_NAME,TABLE_STRUCTURE);
        chGroups = new ArrayList<>();
    }
    
    //Load to JSON
    public Challenges(JSONObject mapInfo)
    {
        super(TABLE_NAME,TABLE_STRUCTURE);
        chGroups = new ArrayList<>();
        saveInternalInfoObject(mapInfo);
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
        //System.out.println("Challenge: "+ toString());
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
    
    @Override
    public String toString()
    {
        String out = "Challenge: "+ this.mapId +" *--"+ mapName +"--*";
        out += "\n\tGroups!!-----------------------\n";
        for(ChallengeGroup chG : chGroups)
        {
            out += chG.toString() +"\n";
            out += "\t.....................................\n";
        }
        return out;
    }
}