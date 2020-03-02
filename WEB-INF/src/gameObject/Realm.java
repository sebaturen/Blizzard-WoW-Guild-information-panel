/**
 * File : Realm.java
 * Desc : Realm object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.GeneralConfig;
import com.google.gson.JsonObject;

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
    
    public Realm(JsonObject info)
    {
        super(REALM_TABLE_NAME, REALM_TABLE_KEY, REALM_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();
        this.slug = objInfo.get("slug").getAsString();
        this.locale = objInfo.get("locale").getAsString();
        this.connectedRealm = objInfo.get("connected_realm").getAsInt();

        if (objInfo.get("name").isJsonObject()) { // load from blizzard
            this.name = objInfo.get("name").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).getAsString();
        } else { // load from DB
            this.name = objInfo.get("name").getAsString();
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