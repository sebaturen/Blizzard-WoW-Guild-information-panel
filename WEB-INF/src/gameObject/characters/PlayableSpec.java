/**
 * File : PlayableSpec.java
 * Desc : Playable spec info
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONObject;

public class PlayableSpec extends GameObject
{    
    //Playable Specs DB
    public static final String PLAYABLE_SPEC_TABLE_NAME = "playable_spec";
    public static final String PLAYABLE_SPEC_TABLE_KEY = "id";
    public static final String[] PLAYABLE_SPEC_TABLE_STRUCTURE = {"id", "slug", "class", "name", "role", "desc_male","desc_female"};
    
    private int id;
    private String slug;
    private PlayableClass pClass;
    private String name;
    private String role;
    private String desc_male;
    private String desc_female;
    
    public PlayableSpec(int id)
    {
        super(PLAYABLE_SPEC_TABLE_NAME, PLAYABLE_SPEC_TABLE_KEY, PLAYABLE_SPEC_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public PlayableSpec(String name, String role, int idClass)
    {
        super(PLAYABLE_SPEC_TABLE_NAME, PLAYABLE_SPEC_TABLE_KEY, PLAYABLE_SPEC_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] {"name", "role", "class"}, new String[] { name, role, idClass+"" });        
    }
    
    public PlayableSpec(JSONObject info)
    {
        super(PLAYABLE_SPEC_TABLE_NAME, PLAYABLE_SPEC_TABLE_KEY, PLAYABLE_SPEC_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        if(objInfo.get("id").getClass() == Long.class)
        {//Info come from blizz API
            this.id = ((Long) objInfo.get("id")).intValue();
            this.name = ((JSONObject) objInfo.get("name")).get(GeneralConfig.LENGUAJE_API_LOCALE).toString();
            this.slug = ((JSONObject) objInfo.get("name")).get("en_US").toString().replaceAll("\\s+","-").toLowerCase();
            this.pClass = new PlayableClass(((Long) ((JSONObject) objInfo.get("playable_class")).get("id")).intValue());
            this.role = ((JSONObject) objInfo.get("role")).get("type").toString();
            if(this.role.equals("DAMAGE")) this.role = "DPS";
            if(this.role.equals("HEALER")) this.role = "HEALING";
            this.desc_male = ((JSONObject) ((JSONObject) objInfo.get("gender_description")).get("male")).get(GeneralConfig.LENGUAJE_API_LOCALE).toString();
            this.desc_female = ((JSONObject) ((JSONObject) objInfo.get("gender_description")).get("female")).get(GeneralConfig.LENGUAJE_API_LOCALE).toString();
        }
        else
        {//Info come from DB
            this.id = (Integer) objInfo.get("id");
            this.name = objInfo.get("name").toString();
            this.slug = objInfo.get("slug").toString();
            this.pClass = new PlayableClass((Integer) objInfo.get("class"));
            this.role = objInfo.get("role").toString();
            this.desc_male = objInfo.get("desc_male").toString();
            this.desc_female = objInfo.get("desc_female").toString();
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        /* {"id", "slug", "name", "role"}; */
        switch (saveInDBObj(new String[] {this.id +"", this.slug, this.pClass.getId() +"", this.name, this.role, this.desc_male, this.desc_female}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }

    //Getters and Setters
    @Override
    public void setId(int id) { this.id = id;}

    @Override
    public int getId() { return this.id; }
    public String getSlug() { return this.slug; }
    public String getName() { return this.name; }
    public String getRole() { return this.role; }
    public String getDescript(int gender)
    {
        switch(gender)
        {
            case 1: //famele
                return this.desc_female;
            default: //0 - male
                return this.desc_male;
        }
    }
}
