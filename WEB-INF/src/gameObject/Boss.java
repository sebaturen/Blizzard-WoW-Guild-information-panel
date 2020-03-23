/**
 * File : Boss.java
 * Desc : Boss object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.google.gson.JsonObject;

public class Boss extends GameObject
{
    //Boss List DB
    public static final String BOSS_LIST_TABLE_NAME = "boss_list";
    public static final String BOSS_LIST_TABLE_KEY  = "id";
    public static final String[] BOSS_LIST_TABLE_STRUCTURE = {"id", "name", "slug", "description"};
    
    //Atribute
    private int id;
    private String name;
    private String slug;
    private String description;
    
    public Boss(int id)
    {
        super(BOSS_LIST_TABLE_NAME, BOSS_LIST_TABLE_KEY, BOSS_LIST_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public Boss(String slug)
    {        
        super(BOSS_LIST_TABLE_NAME, BOSS_LIST_TABLE_KEY, BOSS_LIST_TABLE_STRUCTURE);
        loadFromDBUniqued("slug", slug);
    }
        
    public Boss(JsonObject bossInfo)
    {
        super(BOSS_LIST_TABLE_NAME, BOSS_LIST_TABLE_KEY, BOSS_LIST_TABLE_STRUCTURE);
        saveInternalInfoObject(bossInfo);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject bossInfo)
    {
        this.id = bossInfo.get("id").getAsInt();
        this.name = bossInfo.get("name").getAsString();
        this.slug = bossInfo.get("slug").getAsString();
        if(bossInfo.has("description") && !bossInfo.get("description").isJsonNull())
            this.description = bossInfo.get("description").getAsString();
        this.isData = true;	
    }

    @Override
    public boolean saveInDB() 
    {
        //{"id", "name", "slug", "description"};
        switch (saveInDBObj(new String[] {this.id +"", this.name, this.slug, this.description}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getSlug() { return this.slug; }

    @Override
    public String toString() {
        return "Boss{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
