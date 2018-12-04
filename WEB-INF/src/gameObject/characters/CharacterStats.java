/**
 * File : StatusMember.java
 * Desc : Status from members
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONObject;

public class CharacterStats extends GameObject
{
    //Status Member DB
    public static final String STATUS_MEMBER_TABLE_NAME  = "character_stats";
    public static final String STATUS_MEMBER_TABLE_KEY   = "member_id";
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
    
    private int member_id;
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
        super(STATUS_MEMBER_TABLE_NAME, STATUS_MEMBER_TABLE_KEY, STATUS_MEMBER_TABLE_STRUCTURE);
        loadFromDB(memberId);
    }
    
    public CharacterStats(JSONObject info)
    {
        super(STATUS_MEMBER_TABLE_NAME, STATUS_MEMBER_TABLE_KEY, STATUS_MEMBER_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        if(objInfo.containsKey("member_id"))
        {//From DB
            this.member_id = (Integer) objInfo.get("member_id");
            this.health = (Integer) objInfo.get("health");
            this.power = (Integer) objInfo.get("power");
            this.str = (Integer) objInfo.get("str");
            this.agi = (Integer) objInfo.get("agi");
            this.intStat = (Integer) objInfo.get("int");
            this.sta = (Integer) objInfo.get("sta");
            this.speedRating = (Integer) objInfo.get("speedRating");
            this.critRating = (Integer) objInfo.get("critRating");
            this.hasteRating = (Integer) objInfo.get("hasteRating");
            this.masteryRating = (Integer) objInfo.get("masteryRating");
            this.leech = (Integer) objInfo.get("leech");
            this.leechRating = (Integer) objInfo.get("leechRating");
            this.leechRatingBonus = (Integer) objInfo.get("leechRatingBonus");
            this.versatility = (Integer) objInfo.get("versatility");
            this.avoidanceRating = (Integer) objInfo.get("avoidanceRating");
            this.spellPen = (Integer) objInfo.get("spellPen");
            this.spellCritRating = (Integer) objInfo.get("spellCritRating");
            this.mana5 = (Integer) objInfo.get("mana5");
            this.mana5Combat = (Integer) objInfo.get("mana5Combat");
            this.armor = (Integer) objInfo.get("armor");
            this.dodgeRating = (Integer) objInfo.get("dodgeRating");
            this.parryRating = (Integer) objInfo.get("parryRating");
            this.blockRating = (Integer) objInfo.get("blockRating");
            this.mainHandDmgMin = (Integer) objInfo.get("mainHandDmgMin");
            this.mainHandDmgMax = (Integer) objInfo.get("mainHandDmgMax");
            this.offHandDmgMin = (Integer) objInfo.get("offHandDmgMin");
            this.offHandDmgMax = (Integer) objInfo.get("offHandDmgMax");
            this.rangedDmgMin = (Integer) objInfo.get("rangedDmgMin");
            this.rangedDmgMax = (Integer) objInfo.get("rangedDmgMax");
        }
        else
        {//From blizz            
            this.health = ((Long) objInfo.get("health")).intValue();
            this.power = ((Long) objInfo.get("power")).intValue();
            this.str = ((Long) objInfo.get("str")).intValue();
            this.agi = ((Long) objInfo.get("agi")).intValue();
            this.intStat = ((Long) objInfo.get("int")).intValue();
            this.sta = ((Long) objInfo.get("sta")).intValue();
            this.speedRating = ((Double) objInfo.get("speedRating")).intValue();
            this.critRating = ((Long) objInfo.get("critRating")).intValue();
            this.hasteRating = ((Long) objInfo.get("hasteRating")).intValue();
            this.masteryRating = ((Long) objInfo.get("masteryRating")).intValue();
            this.leech = ((Double) objInfo.get("leech")).intValue();
            this.leechRating = ((Double) objInfo.get("leechRating")).intValue();
            this.leechRatingBonus = ((Double) objInfo.get("leechRatingBonus")).intValue();
            this.versatility = ((Long) objInfo.get("versatility")).intValue();
            this.avoidanceRating = ((Double) objInfo.get("avoidanceRating")).intValue();
            this.spellPen = ((Long) objInfo.get("spellPen")).intValue();
            this.spellCritRating = ((Long) objInfo.get("spellCritRating")).intValue();
            this.mana5 = ((Double) objInfo.get("mana5")).intValue();
            this.mana5Combat = ((Double) objInfo.get("mana5Combat")).intValue();
            this.armor = ((Long) objInfo.get("armor")).intValue();
            this.dodgeRating = ((Long) objInfo.get("dodgeRating")).intValue();
            this.parryRating = ((Long) objInfo.get("parryRating")).intValue();
            this.blockRating = ((Long) objInfo.get("blockRating")).intValue();
            this.mainHandDmgMin = ((Double) objInfo.get("mainHandDmgMin")).intValue();
            this.mainHandDmgMax = ((Double) objInfo.get("mainHandDmgMax")).intValue();
            this.offHandDmgMin = ((Double) objInfo.get("offHandDmgMin")).intValue();
            this.offHandDmgMax = ((Double) objInfo.get("offHandDmgMax")).intValue();
            this.rangedDmgMin = ((Double) objInfo.get("rangedDmgMin")).intValue();
            this.rangedDmgMax = ((Double) objInfo.get("rangedDmgMax")).intValue();
        }
        this.powerType = objInfo.get("powerType").toString();        
        this.rangedSpeed = (Double) objInfo.get("rangedSpeed");
        this.rangedDps = (Double) objInfo.get("rangedDps");
        this.offHandSpeed = (Double) objInfo.get("offHandSpeed");
        this.offHandDps = (Double) objInfo.get("offHandDps");
        this.mainHandSpeed = (Double) objInfo.get("mainHandSpeed");
        this.mainHandDps = (Double) objInfo.get("mainHandDps");
        this.block = (Double) objInfo.get("block");
        this.parry = (Double) objInfo.get("parry");
        this.dodge = (Double) objInfo.get("dodge");
        this.spellCrit = (Double) objInfo.get("spellCrit");
        this.avoidanceRatingBonus = (Double) objInfo.get("avoidanceRatingBonus");
        this.versatilityDamageDoneBonus = (Double) objInfo.get("versatilityDamageDoneBonus");
        this.versatilityHealingDoneBonus = (Double) objInfo.get("versatilityHealingDoneBonus");
        this.versatilityDamageTakenBonus = (Double) objInfo.get("versatilityDamageTakenBonus");
        this.hasteRatingPercent = (Double) objInfo.get("hasteRatingPercent");
        this.mastery = (Double) objInfo.get("mastery");
        this.haste = (Double) objInfo.get("haste");
        this.speedRatingBonus = (Double) objInfo.get("speedRatingBonus");
        this.crit = (Double) objInfo.get("crit");
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        String[] values = {
                    this.member_id+"", this.health+"", this.powerType, this.power+"", this.str+"", this.agi+"", this.intStat+"", this.sta+"", this.speedRating+"",
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
    public void setId(int id) { this.member_id = id; }

    @Override
    public int getId() { return this.member_id;}

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
