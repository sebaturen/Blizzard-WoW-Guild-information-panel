/**
 * File : Boss.java
 * Desc : Boss object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import org.json.simple.JSONObject;

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
        loadFromDB(id +"");
    }
    
    public Boss(String slug)
    {        
        super(BOSS_LIST_TABLE_NAME, BOSS_LIST_TABLE_KEY, BOSS_LIST_TABLE_STRUCTURE);
        loadFromDBUniqued("slug", slug);
    }
        
    public Boss(JSONObject bossInfo)
    {
        super(BOSS_LIST_TABLE_NAME, BOSS_LIST_TABLE_KEY, BOSS_LIST_TABLE_STRUCTURE);
        saveInternalInfoObject(bossInfo);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject bossInfo) 
    {        
        if(bossInfo.get("id").getClass() == java.lang.Long.class) //if info come to blizzAPI or DB
        {			
            this.id = ((Long) bossInfo.get("id")).intValue();
        }
        else
        {
            this.id = (Integer) bossInfo.get("id");         
        }
        this.name = bossInfo.get("name").toString();
        this.slug = bossInfo.get("slug").toString();   
        if(bossInfo.containsKey("description") && bossInfo.get("description") != null) 
            this.description = bossInfo.get("description").toString();
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
    public void setId(String id) { this.id = Integer.parseInt(id); }

    @Override
    public String getId() { return this.id +""; }
    public String getName() { return this.name; }
    public String getSlug() { return this.slug; }
    
    
}
