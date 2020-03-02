/**
 * File : KeystoneDungeon.java
 * Desc : KeystoneDungeon Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */

package com.blizzardPanel.gameObject.mythicKeystone;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.gameObject.GameObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class KeystoneDungeon extends GameObject
{
    //DBStructure
    public static final String KEYSTONE_DUNGEON_TABLE_NAME = "keystone_dungeon";
    public static final String KEYSTONE_DUNGEON_TABLE_KEY = "id";
    public static final String[] KEYSTONE_DUNGEON_TABLE_STRUCTURE = {"id", "map_id", "name", "slug", 
                        "keystone_upgrades_1", "keystone_upgrades_2", "keystone_upgrades_3"};

    //Atributes
    private int id;
    private int mapId;
    private String name;
    private String slug;
    private long keystoneUpgrades1;
    private long keystoneUpgrades2;
    private long keystoneUpgrades3;

    public KeystoneDungeon(int id)
    {
        super(KEYSTONE_DUNGEON_TABLE_NAME, KEYSTONE_DUNGEON_TABLE_KEY, KEYSTONE_DUNGEON_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    public KeystoneDungeon(JsonObject info)
    {
        super(KEYSTONE_DUNGEON_TABLE_NAME, KEYSTONE_DUNGEON_TABLE_KEY, KEYSTONE_DUNGEON_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();

        if (objInfo.has("map") && !objInfo.get("map").isJsonNull()) { //from blizzard
            this.mapId = objInfo.get("map").getAsJsonObject().get("id").getAsInt();
            this.name = objInfo.get("name").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).getAsString();
            this.slug = objInfo.get("zone").getAsJsonObject().get("slug").getAsString();
            JsonArray keystoneUpdate = objInfo.get("keystone_upgrades").getAsJsonArray();
            this.keystoneUpgrades1 = keystoneUpdate.get(0).getAsJsonObject().get("qualifying_duration").getAsLong();
            this.keystoneUpgrades2 = keystoneUpdate.get(1).getAsJsonObject().get("qualifying_duration").getAsLong();
            this.keystoneUpgrades3 = keystoneUpdate.get(2).getAsJsonObject().get("qualifying_duration").getAsLong();
        } else { //from DB
            this.mapId = objInfo.get("map_id").getAsInt();
            this.name = objInfo.get("name").getAsString();
            this.slug = objInfo.get("slug").getAsString();
            this.keystoneUpgrades1 = objInfo.get("keystone_upgrades_1").getAsLong();
            this.keystoneUpgrades2 = objInfo.get("keystone_upgrades_2").getAsLong();
            this.keystoneUpgrades3 = objInfo.get("keystone_upgrades_3").getAsLong();
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB()
    {
        //if(this.isInternalData) return true; //if preview save...
        /* {"id", "name", "icon", "description",
         * "castTime", "cooldown", "range"};
         */
        switch (saveInDBObj(new String[] {this.id +"", this.mapId+"", this.name, this.slug,
                                        this.keystoneUpgrades1+"", this.keystoneUpgrades2+"", this.keystoneUpgrades3+""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }

    //Getters and Setters
    @Override
    public int getId() { return this.id; }
    public int getMapId() { return this.mapId; }
    public String getName() { return this.name; }
    public String getSlug() { return this.slug; }
    public long getKeystoneUpgrades1() { return this.keystoneUpgrades1; }
    public long getKeystoneUpgrades2() { return this.keystoneUpgrades2; }
    public long getKeystoneUpgrades3() { return this.keystoneUpgrades3; }

    @Override
    public void setId(int id) { this.id = id; }
}
