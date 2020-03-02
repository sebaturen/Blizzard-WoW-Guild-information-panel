/**
 * File : PlayableSpec.java
 * Desc : Playable spec info
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

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
    private String descMale;
    private String descFemale;
    
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
    
    public PlayableSpec(JsonObject info)
    {
        super(PLAYABLE_SPEC_TABLE_NAME, PLAYABLE_SPEC_TABLE_KEY, PLAYABLE_SPEC_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        if (objInfo.has("slug")) { // from DB
            this.name = objInfo.get("name").getAsString();
            this.slug = objInfo.get("slug").getAsString();
            this.pClass = new PlayableClass(objInfo.get("class").getAsInt());
            this.role = objInfo.get("role").getAsString();
            this.descMale = objInfo.get("desc_male").getAsString();
            this.descFemale = objInfo.get("desc_female").getAsString();
        } else { // from blizzard
            this.name = objInfo.get("name").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).getAsString();
            this.slug =  objInfo.get("name").getAsJsonObject().get("en_US").getAsString().replaceAll("\\s+","-").toLowerCase();
            this.pClass = new PlayableClass( objInfo.get("playable_class").getAsJsonObject().get("id").getAsInt() );
            this.role = objInfo.get("role").getAsJsonObject().get("type").getAsString();
            if(this.role.equals("DAMAGE")) this.role = "DPS";
            if(this.role.equals("HEALER")) this.role = "HEALING";
            this.descMale = objInfo.get("gender_description").getAsJsonObject().get("male").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).getAsString();
            this.descFemale = objInfo.get("gender_description").getAsJsonObject().get("female").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).getAsString();
        }
        this.id = objInfo.get("id").getAsInt();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        /* {"id", "slug", "name", "role"}; */
        switch (saveInDBObj(new String[] {this.id +"", this.slug, this.pClass.getId() +"", this.name, this.role, this.descMale, this.descFemale}))
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
    public String getDecrypt(int gender)
    {
        switch(gender)
        {
            case 1: // female
                return this.descFemale;
            default: //0 - male
                return this.descMale;
        }
    }

    @Override
    public String toString() {
        return "PlayableSpec{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", pClass=" + pClass +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", descMale='" + descMale + '\'' +
                ", descFemale='" + descFemale + '\'' +
                '}';
    }
}
