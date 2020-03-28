/**
 * File : StatusMember.java
 * Desc : Status from members
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

public class CharacterStats extends GameObject
{
    //Status Member DB
    public static final String TABLE_NAME  = "character_stats";
    public static final String TABLE_KEY   = "character_id";
    public static final String[] STATUS_MEMBER_TABLE_STRUCTURE = {
                    "member_id", "health", "powerType", "power", "str", "agi", "int", "sta","speedRating",
                    "speedRatingBonus", "crit", "critRating", "haste", "hasteRating", "hasteRatingPercent",
                    "mastery", "masteryRating", "leech", "leechRating", "leechRatingBonus", "versatility",
                    "versatilityDamageDoneBonus", "versatilityHealingDoneBonus", "versatilityDamageTakenBonus",
                    "avoidanceRating", "avoidanceRatingBonus", "spellPen", "spellCrit", "spellCritRating",
                    "mana5", "mana5Combat", "armor", "dodge", "dodgeRating", "parry", "parryRating", "block",
                    "blockRating", "mainHandDmgMin", "mainHandDmgMax", "mainHandSpeed", "mainHandDps","offHandDmgMin",
                    "offHandDmgMax", "offHandSpeed", "offHandDps", "rangedDmgMin", "rangedDmgMax", "rangedSpeed",
                    "rangedDps"};
    
    private int memberId;
    private String powerType;
    private int health;
    private int power;
    private int str;
    private int agi;
    private int intStat;
    private int sta;
    private int speedRating;
    private int critRating;
    private int hasteRating;
    private int masteryRating;
    private int leech;
    private int leechRating;
    private int leechRatingBonus;
    private int versatility;
    private int avoidanceRating;
    private int spellPen;
    private int spellCritRating;
    private int mana5;
    private int mana5Combat;
    private int armor;
    private int dodgeRating;
    private int parryRating;
    private int blockRating;
    private int mainHandDmgMin;
    private int mainHandDmgMax;
    private int offHandDmgMin;
    private int offHandDmgMax;
    private int rangedDmgMin;
    private int rangedDmgMax;
    private double rangedSpeed;
    private double rangedDps;
    private double offHandSpeed;
    private double offHandDps;
    private double mainHandSpeed;
    private double mainHandDps;
    private double block;
    private double parry;
    private double dodge;
    private double spellCrit;
    private double avoidanceRatingBonus;
    private double versatilityDamageDoneBonus;
    private double versatilityHealingDoneBonus;
    private double versatilityDamageTakenBonus;
    private double hasteRatingPercent;
    private double mastery;
    private double haste;
    private double speedRatingBonus;
    private double crit;
    
    public CharacterStats(int memberId)
    {
        super(TABLE_NAME, TABLE_KEY, STATUS_MEMBER_TABLE_STRUCTURE);
        loadFromDB(memberId);
    }
    
    public CharacterStats(JsonObject info)
    {
        super(TABLE_NAME, TABLE_KEY, STATUS_MEMBER_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }
    
    @Override
    protected void saveInternalInfoObject(JsonObject objInfo) 
    {
        if(objInfo.has("member_id")) {//From DB
            this.memberId = objInfo.get("member_id").getAsInt();
        }

        this.health = objInfo.get("health").getAsInt();
        this.power = objInfo.get("power").getAsInt();
        this.str = objInfo.get("str").getAsInt();
        this.agi = objInfo.get("agi").getAsInt();
        this.intStat = objInfo.get("int").getAsInt();
        this.sta = objInfo.get("sta").getAsInt();
        this.speedRating = objInfo.get("speedRating").getAsInt();
        this.critRating = objInfo.get("critRating").getAsInt();
        this.hasteRating = objInfo.get("hasteRating").getAsInt();
        this.masteryRating = objInfo.get("masteryRating").getAsInt();
        this.leech = objInfo.get("leech").getAsInt();
        this.leechRating = objInfo.get("leechRating").getAsInt();
        this.leechRatingBonus = objInfo.get("leechRatingBonus").getAsInt();
        this.versatility = objInfo.get("versatility").getAsInt();
        this.avoidanceRating = objInfo.get("avoidanceRating").getAsInt();
        this.spellPen = objInfo.get("spellPen").getAsInt();
        this.spellCritRating = objInfo.get("spellCritRating").getAsInt();
        this.mana5 = objInfo.get("mana5").getAsInt();
        this.mana5Combat = objInfo.get("mana5Combat").getAsInt();
        this.armor = objInfo.get("armor").getAsInt();
        this.dodgeRating = objInfo.get("dodgeRating").getAsInt();
        this.parryRating = objInfo.get("parryRating").getAsInt();
        this.blockRating = objInfo.get("blockRating").getAsInt();
        this.mainHandDmgMin = objInfo.get("mainHandDmgMin").getAsInt();
        this.mainHandDmgMax = objInfo.get("mainHandDmgMax").getAsInt();
        this.offHandDmgMin = objInfo.get("offHandDmgMin").getAsInt();
        this.offHandDmgMax = objInfo.get("offHandDmgMax").getAsInt();
        this.rangedDmgMin = objInfo.get("rangedDmgMin").getAsInt();
        this.rangedDmgMax = objInfo.get("rangedDmgMax").getAsInt();
        this.powerType = objInfo.get("powerType").getAsString();        
        this.rangedSpeed = objInfo.get("rangedSpeed").getAsDouble();
        this.rangedDps = objInfo.get("rangedDps").getAsDouble();
        this.offHandSpeed = objInfo.get("offHandSpeed").getAsDouble();
        this.offHandDps = objInfo.get("offHandDps").getAsDouble();
        this.mainHandSpeed = objInfo.get("mainHandSpeed").getAsDouble();
        this.mainHandDps = objInfo.get("mainHandDps").getAsDouble();
        this.block = objInfo.get("block").getAsDouble();
        this.parry = objInfo.get("parry").getAsDouble();
        this.dodge = objInfo.get("dodge").getAsDouble();
        this.spellCrit = objInfo.get("spellCrit").getAsDouble();
        this.avoidanceRatingBonus = objInfo.get("avoidanceRatingBonus").getAsDouble();
        this.versatilityDamageDoneBonus = objInfo.get("versatilityDamageDoneBonus").getAsDouble();
        this.versatilityHealingDoneBonus = objInfo.get("versatilityHealingDoneBonus").getAsDouble();
        this.versatilityDamageTakenBonus = objInfo.get("versatilityDamageTakenBonus").getAsDouble();
        this.hasteRatingPercent = objInfo.get("hasteRatingPercent").getAsDouble();
        this.mastery = objInfo.get("mastery").getAsDouble();
        this.haste = objInfo.get("haste").getAsDouble();
        this.speedRatingBonus = objInfo.get("speedRatingBonus").getAsDouble();
        this.crit = objInfo.get("crit").getAsDouble();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        String[] values = {
                    this.memberId+"", this.health+"", this.powerType, this.power+"", this.str+"", this.agi+"", this.intStat+"", this.sta+"", this.speedRating+"",
                    this.speedRatingBonus+"", this.crit+"", this.critRating+"", this.haste+"", this.hasteRating+"", this.hasteRatingPercent+"",
                    this.mastery+"", this.masteryRating+"", this.leech+"", this.leechRating+"", this.leechRatingBonus+"", this.versatility+"",
                    this.versatilityDamageDoneBonus+"", this.versatilityHealingDoneBonus+"", this.versatilityDamageTakenBonus+"",
                    this.avoidanceRating+"", this.avoidanceRatingBonus+"", this.spellPen+"", this.spellCrit+"", this.spellCritRating+"",
                    this.mana5+"", this.mana5Combat+"", this.armor+"", this.dodge+"", this.dodgeRating+"", this.parry+"", this.parryRating+"", this.block+"",
                    this.blockRating+"", this.mainHandDmgMin+"", this.mainHandDmgMax+"", this.mainHandSpeed+"", this.mainHandDps+"",this.offHandDmgMin+"",
                    this.offHandDmgMax+"", this.offHandSpeed+"", this.offHandDps+"", this.rangedDmgMin+"", this.rangedDmgMax+"", this.rangedSpeed+"",
                    this.rangedDps+""};
        
        int valSave = saveInDBObj(values);
        switch (valSave)
        {
         case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
             return true;
        }
        return false;
    }

    //Setters and Getters
    @Override
    public void setId(int memberId) { this.memberId = memberId; }

    @Override
    public int getId() { return this.memberId;}

    public int getHealth() { return this.health; }
    public String getPowerType() { return this.powerType; }
    public int getPower() { return this.power; }
    public int getStr() { return this.str; }
    public int getAgi() { return this.agi; }
    public int getIntStat() { return this.intStat; }
    public String[] getBestStat() {
        String[] info = new String[2];
        if(this.str > this.agi && this.str > this.intStat)
        {
            info[0] = "strength";
            info[1] = this.str +"";
        }
        if(this.agi > this.str && this.agi > this.intStat)
        {
            info[0] = "agility";
            info[1] = this.agi +"";            
        }
        if(this.intStat > this.str && this.intStat > this.agi)
        {
            info[0] = "intellect";
            info[1] = this.intStat +"";
        }
        return info;
    }
    public int getSta() { return this.sta; }
    public int getSpeedRating() { return this.speedRating; }
    public double getSpeedRatingBonus() { return this.speedRatingBonus; }
    public double getCrit() { return this.crit; }
    public int getCritRating() { return this.critRating; }
    public double getHaste() { return this.haste; }
    public int getHasteRating() { return this.hasteRating; }
    public double getHasteRatingPercent() { return this.hasteRatingPercent; }
    public double getMastery() { return this.mastery; }
    public int getMasteryRating() { return this.masteryRating; }
    public int getLeech() { return this.leech; }
    public int getLeechRating() { return this.leechRating; }
    public int getLeechRatingBonus() { return this.leechRatingBonus; }
    public int getVersatility() { return this.versatility; }
    public double getVersatilityDamageDoneBonus() { return this.versatilityDamageDoneBonus; }
    public double getVersatilityHealingDoneBonus() { return this.versatilityHealingDoneBonus; }
    public double getVersatilityDamageTakenBonus() { return this.versatilityDamageTakenBonus; }
    public int getAvoidanceRating() { return this.avoidanceRating; }
    public double getAvoidanceRatingBonus() { return this.avoidanceRatingBonus; }
    public int getSpellPen() { return this.spellPen; }
    public double getSpellCrit() { return this.spellCrit; }
    public int getSpellCritRating() { return this.spellCritRating; }
    public int getMana5() { return this.mana5; }
    public int getMana5Combat() { return this.mana5Combat; }
    public int getArmor() { return this.armor; }
    public double getDodge() { return this.dodge; }
    public int getDodgeRating() { return this.dodgeRating; }
    public double getParry() { return this.parry; }
    public int getParryRating() { return this.parryRating; }
    public double getBlock() { return this.block; }
    public int getBlockRating() { return this.blockRating; }
    public int getMainHandDmgMin() { return this.mainHandDmgMin; }
    public int getMainHandDmgMax() { return this.mainHandDmgMax; }
    public double getMainHandSpeed() { return this.mainHandSpeed; }
    public double getMainHandDps() { return this.mainHandDps; }
    public int getOffHandDmgMin() { return this.offHandDmgMin; }
    public int getOffHandDmgMax() { return this.offHandDmgMax; }
    public double getOffHandSpeed() { return this.offHandSpeed; }
    public double getOffHandDps() { return this.offHandDps; }
    public int getRangedDmgMin() { return this.rangedDmgMin; }
    public int getRangedDmgMax() { return this.rangedDmgMax; }
    public double getRangedSpeed() { return this.rangedSpeed; }
    public double getRangedDps() { return this.rangedDps; }
    
    
    
}
