/**
 * File : RaidDificult.java
 * Desc : RaidDificult Object
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

public class RaidDificult extends GameObject
{
    //Raid dificult DB
    public static final String RAID_DIFICULTS_TABLE_NAME = "guild_raid_dificults";
    public static final String RAID_DIFICULTS_TABLE_KEY  = "difi_id";
    public static final String[] RAID_DIFICULTS_TABLE_STRUCTURE = {"difi_id", "raid_id", "name", 
                                                                "rank_world", "rank_region", "rank_realm"};
    //Atribute
    private int id;
    private int raidId;
    private String name;
    private int rankWorld = -1;
    private int rankRegion = -1;
    private int rankRealm = -1;
    private List<RaidDificultBoss> bosses = new ArrayList<>();
    
    public RaidDificult(int id)
    {
        super(RAID_DIFICULTS_TABLE_NAME, RAID_DIFICULTS_TABLE_KEY, RAID_DIFICULTS_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public RaidDificult(String name, int raiderId)
    {
        super(RAID_DIFICULTS_TABLE_NAME, RAID_DIFICULTS_TABLE_KEY, RAID_DIFICULTS_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] {"name", "raid_id"}, new String[] { name, raiderId +"" });
    }
    
    public RaidDificult(JsonArray info)
    {
        super(RAID_DIFICULTS_TABLE_NAME, RAID_DIFICULTS_TABLE_KEY, RAID_DIFICULTS_TABLE_STRUCTURE);
        loadBossFromRaiderIO(info); //Not have internal data only boss info~
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        //from DB
        this.id = objInfo.get("difi_id").getAsInt();
        this.raidId = objInfo.get("raid_id").getAsInt();
        this.name = objInfo.get("name").getAsString();
        this.rankWorld = objInfo.get("rank_world").getAsInt();
        this.rankRegion = objInfo.get("rank_region").getAsInt();
        this.rankRealm = objInfo.get("rank_realm").getAsInt();
        loadBossFromDB();
        this.isData = true;
    }
    
    private void loadBossFromRaiderIO(JsonArray info)
    {
        for(int i = 0; i < info.size(); i++)
        {
            JsonObject bossInfo = info.get(i).getAsJsonObject();
            //Usar raid dificult boss...
            RaidDificultBoss rdBoss = new RaidDificultBoss(bossInfo);
            this.bosses.add(rdBoss);
        }
        this.isData = true;
    }
    
    private void loadBossFromDB()
    {
        try {
            JsonArray dificultBosses = dbConnect.select(RaidDificultBoss.RAID_DIFICULT_BOSSES_TABLE_NAME,
                                                    new String[] { RaidDificultBoss.RAID_DIFICULT_BOSSES_TABLE_KEY },
                                                    "difi_id=? ORDER BY firstDefeated DESC",
                                                    new String[] { this.id +""});
            for(int i = 0; i < dificultBosses.size(); i++)
            {
                //Usar raider dificult boss...
                RaidDificultBoss rdBoss = new RaidDificultBoss(dificultBosses.get(i).getAsJsonObject().get(RaidDificultBoss.RAID_DIFICULT_BOSSES_TABLE_KEY).getAsInt());
                this.bosses.add(rdBoss);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(RaidDificult.class, "Fail to get bosses in dificult raid "+ this.id +" - "+ ex);
        }
    }

    @Override
    public boolean saveInDB() 
    {
        /* {"raid_id", "name", 
         * "rank_world", "rank_region", "rank_realm"};
         */
        setTableStructur(DBStructure.outKey(RAID_DIFICULTS_TABLE_STRUCTURE));
        switch (saveInDBObj(new String[] {this.raidId +"", this.name, 
                            this.rankWorld +"", this.rankRegion +"", this.rankRealm +""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.bosses.forEach((rdBoss) -> {
                    rdBoss.setDifiId(this.id);
                    RaidDificultBoss rdBossDB = new RaidDificultBoss(rdBoss.getBoss().getId(), this.id);
                    if(rdBossDB.isInternalData())
                    {
                        rdBoss.setId(rdBossDB.getId());
                        rdBoss.setIsInternalData(true);
                    }
                    rdBoss.saveInDB();
                });
                return true;

        }
        return false; 
    }

    @Override
    public void setId(int id) { this.id = id; }
    public void setRankWorld(int c) { this.rankWorld = c; }
    public void setRankRegion(int c) { this.rankRegion = c; }
    public void setRankRealm(int c) { this.rankRealm = c; }
    public void setRaidID(int id) { this.raidId = id; }
    public void setName(String name) { this.name = name; }

    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public int getRankWorld() { return this.rankWorld; }
    public int getRankRegion() { return this.rankRegion; }
    public int getRankRealm() { return this.rankRealm; }
    public List<RaidDificultBoss> getDificultBoss() { return this.bosses; }

    @Override
    public String toString() {
        return "RaidDificult{" +
                "id=" + id +
                ", raidId=" + raidId +
                ", name='" + name + '\'' +
                ", rankWorld=" + rankWorld +
                ", rankRegion=" + rankRegion +
                ", rankRealm=" + rankRealm +
                ", bosses=" + bosses +
                '}';
    }
}
