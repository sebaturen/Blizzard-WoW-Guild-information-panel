/**
 * File : RaidDificultBoss.java
 * Desc : RaidDificultBoss Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.guild.raids;

import com.artOfWar.dbConnect.DBStructure;
import com.artOfWar.gameObject.Boss;
import com.artOfWar.gameObject.GameObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;

public class RaidDificultBoss extends GameObject
{
    //Atribute
    private int id;
    private int difiId;
    private Boss boss;
    private Date firstDefeated;
    private double itemLevelAvg;
    private double artifactPowerAvg = -1;
    
    public RaidDificultBoss(int id)
    {
        super(RAID_DIFICULT_BOSSES_TABLE_NAME, RAID_DIFICULT_BOSSES_TABLE_KEY, RAID_DIFICULT_BOSSES_TABLE_STRUCTURE);
        loadFromDB(id +"");
    }
    
    public RaidDificultBoss(int bossId, int diffId)
    {
        super(RAID_DIFICULT_BOSSES_TABLE_NAME, RAID_DIFICULT_BOSSES_TABLE_KEY, RAID_DIFICULT_BOSSES_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] { "boss_id", "difi_id" }, new String[] { bossId +"", diffId +"" });        
    }
    
    public RaidDificultBoss(JSONObject info)
    {
        super(RAID_DIFICULT_BOSSES_TABLE_NAME, RAID_DIFICULT_BOSSES_TABLE_KEY, RAID_DIFICULT_BOSSES_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        if(objInfo.containsKey(RAID_DIFICULT_BOSSES_TABLE_KEY))
        {//from DB
            this.id = (Integer) objInfo.get(RAID_DIFICULT_BOSSES_TABLE_KEY);
            this.boss = new Boss((Integer) objInfo.get("boss_id"));
            this.difiId = (Integer) objInfo.get("difi_id");
            this.itemLevelAvg = (Double) objInfo.get("itemLevelAvg");
            this.artifactPowerAvg = (Double) objInfo.get("itemLevelAvg");
            try { //2018-10-17 02:39:00
                this.firstDefeated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(objInfo.get("firstDefeated").toString());
            } catch (ParseException ex) {
                System.out.println("(DB) Fail to convert date from challenge group! "+ this.id);
            }
        }
        else
        {//load from RaiderIO      
            this.boss = new Boss(getBlizzSlugFromRaiderIO(objInfo.get("slug").toString()));
            if(!this.boss.isData()) System.exit(-1);
            try {
                this.firstDefeated = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(objInfo.get("firstDefeated").toString());
            } catch (ParseException ex) {
                System.out.println("(Blizz) Fail to convert date from challenge group! "+ this.id);
            }
            
            //Save Item Level AVG            
            if(objInfo.get("itemLevelAvg").getClass().equals(Long.class))
                this.itemLevelAvg = (Long) objInfo.get("itemLevelAvg");
            else
                this.itemLevelAvg = (Double) objInfo.get("itemLevelAvg");
            
            //Save artefact power (BFA)
            if(objInfo.containsKey("artifactPowerAvg"))
            {
                if(objInfo.get("artifactPowerAvg").getClass().equals(Long.class))
                    this.artifactPowerAvg = (Long) objInfo.get("artifactPowerAvg");
                else
                    this.artifactPowerAvg = (Double) objInfo.get("artifactPowerAvg");                
            }
        }     
        this.isData = true;
    }
    
    /***
     * In diferent case, the RaiderIO have an other slug, different to blizzardDB
     * @param slug
     * @return 
     */
    private String getBlizzSlugFromRaiderIO(String slug)
    {
        switch(slug)
        {
            case "felhounds-of-sargeras":
                return "fharg";
            case "antoran-high-command":
                return "admiral-svirax";
            case "eonar-the-life-binder":
                return "essence-of-eonar";
            case "the-coven-of-shivarra":
                return "noura-mother-of-flames";
            case "zekvoz-herald-of-nzoth":
                return "zekvoz";
            case "zul-reborn":
                return "zul";
            default:
                return slug;
        }
    }

    @Override
    public boolean saveInDB() 
    {    
        /* {"boss_id", "difi_id",
         * "firstDefeated", "itemLevelAvg", "artifactPowerAvg"};
         */
        setTableStructur(DBStructure.outKey(RAID_DIFICULT_BOSSES_TABLE_STRUCTURE));
        switch (saveInDBObj(new String[] {this.boss.getId() +"", this.difiId +"", 
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(firstDefeated), this.itemLevelAvg +"", this.artifactPowerAvg+""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;    
    }

    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
    public void setDifiId(int id) { this.difiId = id; }

    @Override
    public String getId() { return this.id +""; }
    public Boss getBoss() { return this.boss; }
    public Date getFirstDefeated() { return this.firstDefeated; }
    public double getItemLevelAvg() { return this.itemLevelAvg; }
    public double getArtifactPowerAvg() { return this.artifactPowerAvg; }
    
}
