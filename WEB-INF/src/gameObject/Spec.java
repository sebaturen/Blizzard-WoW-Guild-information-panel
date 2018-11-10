/**
 * File : Spec.java
 * Desc : Speciality object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBStructure;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Spec extends GameObject
{
    //Atribute
    private int id;
    private int memberId;
    private String name;
    private String role;
    private boolean enable;
    private Spell[] spells;
 
    //Constant
    public static final int MAX_SPELL_TALENTS = 7; //Actualy u can get max 6 differents talents

    public Spec(int specId)
    {        
        super(SPECS_TABLE_NAME, SPECS_TABLE_KEY, SPECS_TABLE_STRUCTURE);
        loadFromDB(specId +"");
    }
    
    public Spec(int memberId, JSONObject specInfo, JSONArray talentsInfo)
    {
        super(SPECS_TABLE_NAME, SPECS_TABLE_KEY, SPECS_TABLE_STRUCTURE);
        this.memberId = memberId;
        saveInfoFromBlizz(specInfo, talentsInfo);
    }
    
    private void saveInfoFromBlizz(JSONObject specInfo, JSONArray talentsInfo)
    {
        this.spells = new Spell[MAX_SPELL_TALENTS];
        this.name = specInfo.get("name").toString();
        this.role = specInfo.get("role").toString();
        for(int i = 0; i < talentsInfo.size(); i++)
        {
            JSONObject talentLevl = (JSONObject) talentsInfo.get(i); //get("tier") talent level 
            if(talentsInfo.get(i) != null)
            {
                JSONObject skillBlizzDetail = (JSONObject) talentLevl.get("spell");
                Spell skill = new Spell( ((Long) skillBlizzDetail.get("id")).intValue() );
                if(!skill.isData())
                {
                    skill = new Spell(skillBlizzDetail);
                }
                this.spells[ ((Long) talentLevl.get("tier")).intValue() ] = skill;
            }
        }
        this.isData = true;
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject specInfo) 
    {        
        this.id = (Integer) specInfo.get("id");
        this.memberId = (Integer) specInfo.get("member_id");
        this.name = specInfo.get("name").toString();
        this.role = specInfo.get("role").toString();
        this.enable = (Boolean) specInfo.get("enable");
        this.spells = new Spell[MAX_SPELL_TALENTS];
        if((Integer) specInfo.get("tier_0") != 0) this.spells[0] = new Spell( (Integer) specInfo.get("tier_0") );
        if((Integer) specInfo.get("tier_1") != 0) this.spells[0] = new Spell( (Integer) specInfo.get("tier_1") );
        if((Integer) specInfo.get("tier_2") != 0) this.spells[0] = new Spell( (Integer) specInfo.get("tier_2") );
        if((Integer) specInfo.get("tier_3") != 0) this.spells[0] = new Spell( (Integer) specInfo.get("tier_3") );
        if((Integer) specInfo.get("tier_4") != 0) this.spells[0] = new Spell( (Integer) specInfo.get("tier_4") );
        if((Integer) specInfo.get("tier_5") != 0) this.spells[0] = new Spell( (Integer) specInfo.get("tier_5") );
        if((Integer) specInfo.get("tier_6") != 0) this.spells[0] = new Spell( (Integer) specInfo.get("tier_6") );
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    { 
        String[] spellID = new String[MAX_SPELL_TALENTS];
        for(int j = 0; j < this.spells.length; j++)
        {
            if(this.spells[j] != null)
            {
                spellID[j] = this.spells[j].getId() +"";
                (this.spells[j]).saveInDB();
            }
        }
        String isEnable = (this.enable)? "1":"0";  
        
        /* {"member_id", "name", "role", "enable",
         * "tier_0", "tier_1", "tier_2",
         * "tier_3", "tier_4", "tier_5",
         * "tier_6"};
         */
        setTableStructur(DBStructure.outKey(SPECS_TABLE_STRUCTURE));
        int valSave = saveInDBObj(new String[] { this.memberId +"", this.name, this.role, isEnable,
                                                spellID[0], spellID[1], spellID[2], 
                                                spellID[3], spellID[4], spellID[5],
                                                spellID[6]});
        switch (valSave)
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }

    //Getters and Setters
    public boolean isEnable() { return this.enable; }
    public boolean isThisSpec(String sName, String sRole) { return this.name.equals(sName) && this.role.equals(sRole); }
    public boolean isThisSpec(int id) { return (this.id == id); }
    @Override
    public String getId() { return this.id +""; }
    public String getName() { return this.name; }
    public String getRole() { return this.role; }
    public Spell[] getSpells() { return this.spells; }
    public void setEnable(boolean e) { this.enable = e; }
    public void setMemberId(int id) { this.memberId = id; }
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
        
}