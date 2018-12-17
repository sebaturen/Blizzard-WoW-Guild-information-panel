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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    
    public Raid(JSONObject info)
    {
        super(RAIDS_TABLE_NAME, RAIDS_TABLE_KEY, RAIDS_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        if(objInfo.containsKey(RAIDS_TABLE_KEY))
        {//info from DB
            this.id = (Integer) objInfo.get("id");
            this.name = objInfo.get("name").toString();
            this.slug = objInfo.get("slug").toString();
            this.totalBoss = (Integer) objInfo.get("total_boss");
            loadRaidDificultFromDB();
        }
        else
        {//info from raiderIO
            this.name = "NOT DEFINED";
            this.slug = objInfo.get("raid").toString();
            JSONObject dificult = (JSONObject) objInfo.get("encountersDefeated");
            JSONObject rank = (JSONObject) objInfo.get("rank");
            loadRaidDificultFromRaiderIO(dificult, rank);            
        }
        this.isData = true;
    }
    
    private void loadRaidDificultFromDB()
    {
        try {
            JSONArray raidDificult = dbConnect.select(RaidDificult.RAID_DIFICULTS_TABLE_NAME,
                                                    new String[] { RaidDificult.RAID_DIFICULTS_TABLE_KEY },
                                                    "raid_id=? ORDER BY difi_id DESC",
                                                    new String[] { this.id +""});
            if(raidDificult.size() > 0)
            {
                for(int i = 0; i < raidDificult.size(); i++)
                {                    
                    RaidDificult r = new RaidDificult( (Integer) ((JSONObject) raidDificult.get(i)).get(RaidDificult.RAID_DIFICULTS_TABLE_KEY) ); 
                    this.dificults.add(r);
                }
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Raid.class, "Fail to get a 'raid dificult' from Raid "+ this.name +" e: "+ ex);
        } 
    }
    
    private void loadRaidDificultFromRaiderIO(JSONObject dificult, JSONObject rank)
    {
        RaidDificult lfr = new RaidDificult((JSONArray) dificult.get("lfr"));
        lfr.setName("Looking for Raid");
        this.dificults.add(lfr);
        //Normal dificult~
        RaidDificult normal = new RaidDificult((JSONArray) dificult.get("normal"));
        normal.setName("Normal");
        normal.setRankWorld( ((Long) ((JSONObject)rank.get("normal")).get("world")).intValue() );
        normal.setRankRegion( ((Long) ((JSONObject)rank.get("normal")).get("region")).intValue() );
        normal.setRankRealm( ((Long) ((JSONObject)rank.get("normal")).get("realm")).intValue() );
        this.dificults.add(normal);
        //Heroic dificult~
        RaidDificult heroic = new RaidDificult((JSONArray) dificult.get("heroic"));
        heroic.setName("Heroic");
        heroic.setRankWorld( ((Long) ((JSONObject)rank.get("heroic")).get("world")).intValue() );
        heroic.setRankRegion( ((Long) ((JSONObject)rank.get("heroic")).get("region")).intValue() );
        heroic.setRankRealm( ((Long) ((JSONObject)rank.get("heroic")).get("realm")).intValue() );
        this.dificults.add(heroic);
        //Mythic dificult~
        RaidDificult mythic = new RaidDificult((JSONArray) dificult.get("mythic"));
        mythic.setName("Mythic");
        mythic.setRankWorld( ((Long) ((JSONObject)rank.get("mythic")).get("world")).intValue() );
        mythic.setRankRegion( ((Long) ((JSONObject)rank.get("mythic")).get("region")).intValue() );
        mythic.setRankRealm( ((Long) ((JSONObject)rank.get("mythic")).get("realm")).intValue() );   
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
