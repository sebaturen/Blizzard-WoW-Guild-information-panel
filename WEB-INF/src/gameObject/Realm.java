/**
 * File : Realm.java
 * Desc : Realm object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.GeneralConfig;
import org.json.simple.JSONObject;

public class Realm extends GameObject
{
    //DBStructure
    public static final String REALM_TABLE_NAME = "realms";
    public static final String REALM_TABLE_KEY = "id";
    public static final String[] REALM_TABLE_STRUCTURE = {"id", "connected_realm", "name", "slug", "locale"};
    
    //Atribute
    private int id;
    private int connectedRealm;
    private String name;
    private String slug;
    private String locale;
    
    public Realm(int id)
    {
        super(REALM_TABLE_NAME, REALM_TABLE_KEY, REALM_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public Realm(String name)
    {
        super(REALM_TABLE_NAME, REALM_TABLE_KEY, REALM_TABLE_STRUCTURE);
        loadFromDBUniqued("name", name);        
    }
    
    public Realm(JSONObject info)
    {
        super(REALM_TABLE_NAME, REALM_TABLE_KEY, REALM_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        this.slug = objInfo.get("slug").toString();
        this.locale = objInfo.get("locale").toString();
        this.connectedRealm = (Integer) objInfo.get("connected_realm");
         
        if(objInfo.get("id").getClass() == Long.class)
        {//Info come from blizzard
            this.id = ((Long) objInfo.get("id")).intValue();
            this.name = ((JSONObject) objInfo.get("name")).get(GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")).toString();
        }
        else
        {//load from DB
            this.id = (Integer) objInfo.get("id");
            this.name = objInfo.get("name").toString();
        }
        
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {        
        // {"id", "name", "slug", "locale"};
        switch (saveInDBObj(new String[] {this.id +"", this.connectedRealm+"", this.name, this.slug, this.locale}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }

    //Getters and Setteres
    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return this.id; }
    public int getConnectedRealm() { return this.connectedRealm; }
    public String getName() { return this.name; }
    public String getSlug() { return this.slug; }
    public String getLocale() { return this.locale; }

    
    
}