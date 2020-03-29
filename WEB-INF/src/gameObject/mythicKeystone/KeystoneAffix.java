/**
 * File : KeystoneAffixes.java
 * Desc : Keystone Affixes Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */

package com.blizzardPanel.gameObject.mythicKeystone;

import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonObject;

public class KeystoneAffix extends GameObject
{
    //DBStructure
    public static final String TABLE_NAME = "keystone_affixes";
    public static final String TABLE_KEY = "id";
    public static final String[] KEYSTONE_AFFIXES_TABLE_STRUCTURE = {"id", "name", "description", "icon"};
    
    private int id;
    private String name;
    private String description;
    private String icon;
    
    public KeystoneAffix(int id)
    {
        super(TABLE_NAME, TABLE_KEY, KEYSTONE_AFFIXES_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public KeystoneAffix(JsonObject info)
    {
        super(TABLE_NAME, TABLE_KEY, KEYSTONE_AFFIXES_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();
        this.name = objInfo.get("name").getAsString();
        this.description = objInfo.get("description").getAsString();
        this.icon = objInfo.get("icon").getAsString();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        //{"id", "name", "description", "icon"}; //
        switch (saveInDBObj(new String[] {this.id +"", this.name, this.description, this.icon}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;    
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o == this) return true;
        if(o == null || (this.getClass() != o.getClass())) return false;

        return ((KeystoneAffix) o).getId() == this.id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.id;
        return hash;
    }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getIcon() { return this.icon; }
    
}