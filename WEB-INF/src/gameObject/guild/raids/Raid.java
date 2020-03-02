/**
 * File : Raid.java
 * Desc : Raid Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.raids;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Raid extends GameObject
{
    //Raid DB
    public static final String RAIDS_TABLE_NAME = "raids";
    public static final String RAIDS_TABLE_KEY  = "id";
    public static final String[] RAIDS_TABLE_STRUCTURE = {"id", "slug", "name", "total_boss"};
    
    //Atribute
    private int id;
    private String name;
    private String slug;
    private List<RaidDificult> dificults = new ArrayList<>();
    private int totalBoss = -1;
    
    public Raid(int id)
    {
        super(RAIDS_TABLE_NAME, RAIDS_TABLE_KEY, RAIDS_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public Raid(String slug)
    {
        super(RAIDS_TABLE_NAME, RAIDS_TABLE_KEY, RAIDS_TABLE_STRUCTURE);
        loadFromDBUniqued("slug", slug);
    }
    
    public Raid(JsonObject info)
    {
        super(RAIDS_TABLE_NAME, RAIDS_TABLE_KEY, RAIDS_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }
    
    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        if(objInfo.has(RAIDS_TABLE_KEY))
        {//info from DB
            this.id = objInfo.get("id").getAsInt();
            this.name = objInfo.get("name").getAsString();
            this.slug = objInfo.get("slug").getAsString();
            this.totalBoss = objInfo.get("total_boss").getAsInt();
            loadRaidDifficultFromDB();
        }
        else
        {//info from raiderIO
            this.name = "NOT DEFINED";
            this.slug = objInfo.get("raid").getAsString();
            JsonObject difficult = objInfo.get("encountersDefeated").getAsJsonObject();
            JsonObject rank = objInfo.get("rank").getAsJsonObject();
            loadRaidDificultFromRaiderIO(difficult, rank);
        }
        this.isData = true;
    }
    
    private void loadRaidDifficultFromDB()
    {
        try {
            JsonArray raidDifficult = dbConnect.select(RaidDificult.RAID_DIFICULTS_TABLE_NAME,
                                                    new String[] { RaidDificult.RAID_DIFICULTS_TABLE_KEY },
                                                    "raid_id=? ORDER BY difi_id DESC",
                                                    new String[] { this.id +""});
            if(raidDifficult.size() > 0)
            {
                for(int i = 0; i < raidDifficult.size(); i++)
                {                    
                    RaidDificult r = new RaidDificult( raidDifficult.get(i).getAsJsonObject().get(RaidDificult.RAID_DIFICULTS_TABLE_KEY).getAsInt() );
                    this.dificults.add(r);
                }
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Raid.class, "Fail to get a 'raid dificult' from Raid "+ this.name +" e: "+ ex);
        } 
    }
    
    private void loadRaidDificultFromRaiderIO(JsonObject difficult, JsonObject rank)
    {
        RaidDificult lfr = new RaidDificult(difficult.get("lfr").getAsJsonArray());
        RaidDificult normal = new RaidDificult(difficult.get("normal").getAsJsonArray());
        RaidDificult heroic = new RaidDificult(difficult.get("heroic").getAsJsonArray());
        RaidDificult mythic = new RaidDificult(difficult.get("mythic").getAsJsonArray());
        lfr.setName("Looking for Raid");
        this.dificults.add(lfr);
        //Normal difficult~
        normal.setName("Normal");
        normal.setRankWorld( rank.get("normal").getAsJsonObject().get("world").getAsInt());
        normal.setRankRegion( rank.get("normal").getAsJsonObject().get("region").getAsInt());
        normal.setRankRealm( rank.get("normal").getAsJsonObject().get("realm").getAsInt());
        this.dificults.add(normal);
        //Heroic difficult~
        heroic.setName("Heroic");
        heroic.setRankWorld( rank.get("heroic").getAsJsonObject().get("world").getAsInt());
        heroic.setRankRegion( rank.get("heroic").getAsJsonObject().get("region").getAsInt());
        heroic.setRankRealm( rank.get("heroic").getAsJsonObject().get("realm").getAsInt());
        this.dificults.add(heroic);
        //Mythic difficult~
        mythic.setName("Mythic");
        mythic.setRankWorld( rank.get("mythic").getAsJsonObject().get("world").getAsInt());
        mythic.setRankRegion( rank.get("mythic").getAsJsonObject().get("region").getAsInt());
        mythic.setRankRealm( rank.get("mythic").getAsJsonObject().get("realm").getAsInt());
        this.dificults.add(mythic);  
    }

    @Override
    public boolean saveInDB() 
    {
        /* {"slug", "name", "total_boss"}; */
        setTableStructur(DBStructure.outKey(RAIDS_TABLE_STRUCTURE));
        switch (saveInDBObj(new String[] {this.slug, this.name, this.totalBoss +""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                dificults.forEach((dif) -> {
                    RaidDificult dbTest = new RaidDificult(dif.getName(), this.id);
                    if(dbTest.isInternalData())
                    {
                        dif.setId(dbTest.getId());
                        dif.setIsInternalData(true);
                    }
                    dif.setRaidID(this.id);
                    dif.saveInDB();
                });
                return true;
        }
        return false;
    }

    @Override
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setTotalBoss(int total) { this.totalBoss = total; }

    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getSlug() { return this.slug; }
    public int getTotalBoss() { return this.totalBoss; }
    public List<RaidDificult> getDificults() { return this.dificults; }
    
}
