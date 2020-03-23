/**
 * File : RaidDificultBoss.java
 * Desc : RaidDificultBoss Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.raids;

import com.blizzardPanel.Logs;
import com.blizzardPanel.update.blizzard.Update;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.Boss;
import com.blizzardPanel.gameObject.GameObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

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
    
    public RaidDificultBoss(JsonObject info)
    {
        super(RAID_DIFICULT_BOSSES_TABLE_NAME, RAID_DIFICULT_BOSSES_TABLE_KEY, RAID_DIFICULT_BOSSES_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        if(objInfo.has(RAID_DIFICULT_BOSSES_TABLE_KEY))
        {//from DB
            this.id = objInfo.get(RAID_DIFICULT_BOSSES_TABLE_KEY).getAsInt();
            this.boss = new Boss(objInfo.get("boss_id").getAsInt());
            this.difiId = objInfo.get("difi_id").getAsInt();
            this.itemLevelAvg = objInfo.get("itemLevelAvg").getAsDouble();
            this.artifactPowerAvg = objInfo.get("itemLevelAvg").getAsDouble();
            try { //2018-10-17 02:39:00
                this.firstDefeated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(objInfo.get("firstDefeated").getAsString());
            } catch (ParseException ex) {
                Logs.errorLog(RaidDificultBoss.class, "(DB) Fail to convert date from challenge group! "+ this.id);
            }
        }
        else
        {//load from RaiderIO      
            this.boss = new Boss(getBlizzSlugFromRaiderIO(objInfo.get("slug").getAsString()));
            if(!this.boss.isData())
            {
                Logs.infoLog(RaidDificultBoss.class, "Boss '"+ objInfo.get("slug").getAsString() +"' not have info in DB, update to blizzard");
                try {
                    //In internal DB not have boss, so we need load a new boss DB...
                    Update.shared.getBossInformation();
                    this.boss = new Boss(getBlizzSlugFromRaiderIO(objInfo.get("slug").getAsString()));
                    if(!this.boss.isData())
                    {
                        Logs.errorLog(RaidDificultBoss.class, "FAIL (EXIT) TO GET BOSS INFO!! "+ objInfo.get("slug").getAsString());
                        //System.exit(-1);
                    }
                } catch (IOException ex) {
                    Logs.errorLog(RaidDificultBoss.class, "FAIL (EXIT) TO UPDATE BOSS LIST!! "+ ex);
                    //System.exit(-1);
                }
            }
            else
            {                
                try {
                    this.firstDefeated = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(objInfo.get("firstDefeated").getAsString());
                } catch (ParseException ex) {
                    Logs.errorLog(RaidDificultBoss.class, "(Blizz) Fail to convert date from challenge group! "+ this.id +" - "+ ex);
                }

                //Save Item Level AVG
                this.itemLevelAvg = objInfo.get("itemLevelAvg").getAsDouble();
                //Save artifact power (BFA)
                if(objInfo.has("artifactPowerAvg"))
                {
                    this.artifactPowerAvg = objInfo.get("artifactPowerAvg").getAsLong();
                }
            }
        }     
        this.isData = true;
    }
    
    /***
     * In different case, the RaiderIO have an other slug, different to blizzardDB
     * @param slug
     * @return 
     */
    private String getBlizzSlugFromRaiderIO(String slug)
    {
        switch(slug)
        {
            //******************Battle For Azeroth****************************//
            //-----------Ny'alotha, the Waking City
            case "wrathion-the-black-emperor":
                return "wrathion";
            case "the-prophet-skitra":
                return "prophet-skitra";
            case "ra-den-the-despoiled":
                return "raden-the-despoiled";
            //-----------Battle of Dazar'alor
            case "high-tinker-mekkatorque":
                return "mekkatorque";
            //-----------Uldir    
            case "zekvoz-herald-of-nzoth":
                return "zekvoz";
            case "zul-reborn":
                return "zul";
            case "mythrax-the-unraveler":
                return "mythrax";
            //******************Legion****************************************//
            //-----------Antorus, the Burning Throne
            case "eonar-the-life-binder":
                return "the-defense-of-eonar";
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

    @Override
    public String toString() {
        return "RaidDificultBoss{" +
                "id=" + id +
                ", difiId=" + difiId +
                ", boss=" + boss +
                ", firstDefeated=" + firstDefeated +
                ", itemLevelAvg=" + itemLevelAvg +
                ", artifactPowerAvg=" + artifactPowerAvg +
                '}';
    }
}
