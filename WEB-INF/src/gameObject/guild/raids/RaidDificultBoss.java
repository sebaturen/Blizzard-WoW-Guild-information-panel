/**
 * File : RaidDificultBoss.java
 * Desc : RaidDificultBoss Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.raids;

import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.Boss;
import com.blizzardPanel.gameObject.GameObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;

public class RaidDificultBoss extends GameObject
{
    //Raid dificult bosses DB
    public static final String RAID_DIFICULT_BOSSES_TABLE_NAME = "guild_raid_dificult_bosses";
    public static final String RAID_DIFICULT_BOSSES_TABLE_KEY  = "r_d_boss_id";
    public static final String[] RAID_DIFICULT_BOSSES_TABLE_STRUCTURE = {"r_d_boss_id", "boss_id", "difi_id",
                                                                    "firstDefeated", "itemLevelAvg", "artifactPowerAvg"};
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
        loadFromDB(id);
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
                Logs.errorLog(RaidDificultBoss.class, "(DB) Fail to convert date from challenge group! "+ this.id);
            }
        }
        else
        {//load from RaiderIO      
            this.boss = new Boss(getBlizzSlugFromRaiderIO(objInfo.get("slug").toString()));
            if(!this.boss.isData())
            {
                Logs.errorLog(RaidDificultBoss.class, "FAIL (EXIT) TO GET BOSS INFO!! "+ getBlizzSlugFromRaiderIO(objInfo.get("slug").toString()));
                System.exit(-1);
            }
            try {
                this.firstDefeated = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(objInfo.get("firstDefeated").toString());
            } catch (ParseException ex) {
                Logs.errorLog(RaidDificultBoss.class, "(Blizz) Fail to convert date from challenge group! "+ this.id +" - "+ ex);
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
            //******************Battle For Azeroth****************************//
            //-----------Uldir    
            case "zekvoz-herald-of-nzoth":
                return "zekvoz";
            case "zul-reborn":
                return "zul";
            //******************Legion****************************************//
            //-----------Antorus, the Burning Throne
            case "felhounds-of-sargeras":
                return "fharg";
            case "antoran-high-command":
                return "admiral-svirax";
            case "eonar-the-life-binder":
                return "essence-of-eonar";
            case "the-coven-of-shivarra":
                return "noura-mother-of-flames";
            //----------The Emerald Nightmare
            case "dragons-of-nightmare":
                return "ysondre";
            case "ilgynoth-the-heart-of-corruption":
                return "ilgynoth";
            //----------The Nighthold
            case "grand-magistrix-elisande":
                return "elisande";
            //----------Tomb of Sargeras
            case "demonic-inquisition":
                return "atrigan";
            case "sisters-of-the-moon":
                return "huntress-kasparian";
            case "the-desolate-host":
                return "engine-of-souls";
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
    public void setId(int id) { this.id = id; }
    public void setDifiId(int id) { this.difiId = id; }

    @Override
    public int getId() { return this.id; }
    public Boss getBoss() { return this.boss; }
    public Date getFirstDefeated() { return this.firstDefeated; }
    public double getItemLevelAvg() { return this.itemLevelAvg; }
    public double getArtifactPowerAvg() { return this.artifactPowerAvg; }
    
}
