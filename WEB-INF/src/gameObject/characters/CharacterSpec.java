/**
 * File : Spec.java
 * Desc : Speciality object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Spell;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;

public class CharacterSpec extends GameObject
{
    //Specs  DB
    public static final String SPECS_TABLE_NAME = "character_specs";
    public static final String SPECS_TABLE_KEY = "id";
    public static final String[] SPECS_TABLE_STRUCTURE = {"id", "member_id", "spec_id", "enable",
                                                            "tier_0", "tier_1", "tier_2",
                                                            "tier_3", "tier_4", "tier_5",
                                                            "tier_6"};    
    //Constant
    public static final int MAX_SPELL_TALENTS = 7; //Actualy u can get max 7 differents talents
    
    //Atribute
    private int id;
    private int memberId;
    private PlayableSpec spec;
    private boolean enable;
    private Spell[] spells;
 

    public CharacterSpec(int specId)
    {        
        super(SPECS_TABLE_NAME, SPECS_TABLE_KEY, SPECS_TABLE_STRUCTURE);
        loadFromDB(specId);
    }
    
    public CharacterSpec(CharacterMember member, JsonObject specInfo, JsonArray talentsInfo)
    {
        super(SPECS_TABLE_NAME, SPECS_TABLE_KEY, SPECS_TABLE_STRUCTURE);
        this.memberId = member.getId();
        if (!member.isDelete())
            saveInfoFromBlizz(member, specInfo, talentsInfo);
    }
    
    private void saveInfoFromBlizz(CharacterMember member, JsonObject specInfo, JsonArray talentsInfo)
    {
        this.spells = new Spell[MAX_SPELL_TALENTS];
        this.spec = new PlayableSpec(specInfo.get("name").getAsString(), specInfo.get("role").getAsString(), member.getMemberClass().getId());
        if(this.spec.getName() == null)
        {
            Logs.errorLog(CharacterSpec.class, "Fail to get Spec Member> "+ this.memberId);
            Logs.errorLog(CharacterSpec.class, "\tSpec Name: "+ specInfo.get("name").getAsString());
            Logs.errorLog(CharacterSpec.class, "\tSpec Role: "+ specInfo.get("role").getAsString());
            Logs.errorLog(CharacterSpec.class, "\tClass ID: "+ member.getMemberClass().getId());
            System.exit(-1);
        }
        for(int i = 0; i < talentsInfo.size(); i++)
        {
            JsonObject talentLevel = talentsInfo.get(i).getAsJsonObject(); //get("tier") talent level
            if(talentsInfo.get(i) != null)
            {
                JsonObject skillBlizzDetail = talentLevel.get("spell").getAsJsonObject();
                int spellID = skillBlizzDetail.get("id").getAsInt();
                Spell sp = new Spell( spellID );
                if(!sp.isInternalData())
                {
                    sp = new Spell(skillBlizzDetail);
                    sp.saveInDB();
                }
                this.spells[ talentLevel.get("tier").getAsInt() ] = sp;
            }
        }
        this.isData = true;
    }
    
    @Override
    protected void saveInternalInfoObject(JsonObject specInfo)
    {        
        this.id = specInfo.get("id").getAsInt();
        this.memberId = specInfo.get("member_id").getAsInt();
        this.spec = new PlayableSpec(specInfo.get("spec_id").getAsInt());
        this.enable = specInfo.get("enable").getAsBoolean();
        this.spells = new Spell[MAX_SPELL_TALENTS];
        if(specInfo.get("tier_0").getAsInt() != 0) this.spells[0] = new Spell( specInfo.get("tier_0").getAsInt() );
        if(specInfo.get("tier_1").getAsInt() != 0) this.spells[1] = new Spell( specInfo.get("tier_1").getAsInt() );
        if(specInfo.get("tier_2").getAsInt() != 0) this.spells[2] = new Spell( specInfo.get("tier_2").getAsInt() );
        if(specInfo.get("tier_3").getAsInt() != 0) this.spells[3] = new Spell( specInfo.get("tier_3").getAsInt() );
        if(specInfo.get("tier_4").getAsInt() != 0) this.spells[4] = new Spell( specInfo.get("tier_4").getAsInt() );
        if(specInfo.get("tier_5").getAsInt() != 0) this.spells[5] = new Spell( specInfo.get("tier_5").getAsInt() );
        if(specInfo.get("tier_6").getAsInt() != 0) this.spells[6] = new Spell( specInfo.get("tier_6").getAsInt() );
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
            }
        }
        String isEnable = (this.enable)? "1":"0";
        
        /* {"member_id", "name", "role", "enable",
         * "tier_0", "tier_1", "tier_2",
         * "tier_3", "tier_4", "tier_5",
         * "tier_6"};
         */
        setTableStructur(DBStructure.outKey(SPECS_TABLE_STRUCTURE));
        int valSave = saveInDBObj(new String[] { this.memberId +"", this.spec.getId() +"", isEnable,
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
    public boolean isThisSpec(int specId) { return this.spec.getId() == specId; }
    public boolean isThisCharSpec(int id) { return (this.id == id); }
    @Override
    public int getId() { return this.id; }
    public PlayableSpec getSpec() { return this.spec; }
    public Spell[] getSpells() { return this.spells; }
    public int getMemberId() { return this.memberId; }
    @Override
    public void setId(int id) { this.id = id; }
    public void setEnable(boolean e) { this.enable = e; }
    public void setMemberId(int id) { this.memberId = id; }

    @Override
    public String toString() {
        return "CharacterSpec{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", spec=" + spec +
                ", enable=" + enable +
                ", spells=" + Arrays.toString(spells) +
                '}';
    }
}