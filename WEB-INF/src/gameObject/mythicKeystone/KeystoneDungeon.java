/**
 * File : KeystoneDungeon.java
 * Desc : KeystoneDungeon Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */

package com.blizzardPanel.gameObject.mythicKeystone;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public KeystoneDungeon(JSONObject info)
    {
        super(KEYSTONE_DUNGEON_TABLE_NAME, KEYSTONE_DUNGEON_TABLE_KEY, KEYSTONE_DUNGEON_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    protected void saveInternalInfoObject(JSONObject objInfo)
    {
        if(objInfo.get("id").getClass() == java.lang.Long.class)
        {//info come from blizz
            this.id = ((Long) objInfo.get("id")).intValue();
            this.mapId = ((Long) ((JSONObject) objInfo.get("map")).get("id")).intValue();
            this.name = ((JSONObject) objInfo.get("name")).get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).toString();
            this.slug = ((JSONObject) objInfo.get("zone")).get("slug").toString();
            JSONArray kestonUpgrade = (JSONArray) objInfo.get("keystone_upgrades");
            this.keystoneUpgrades1 = (long) ( (JSONObject) kestonUpgrade.get(0) ).get("qualifying_duration");
            this.keystoneUpgrades2 = (long) ( (JSONObject) kestonUpgrade.get(1) ).get("qualifying_duration");
            this.keystoneUpgrades3 = (long) ( (JSONObject) kestonUpgrade.get(2) ).get("qualifying_duration");
        }
        else
        {
            this.id = (Integer) objInfo.get("id");
            this.mapId = (Integer) objInfo.get("map_id");
            this.name = objInfo.get("name").toString();
            this.slug = objInfo.get("slug").toString();
            this.keystoneUpgrades1 = (long) objInfo.get("keystone_upgrades_1");
            this.keystoneUpgrades2 = (long) objInfo.get("keystone_upgrades_2");
            this.keystoneUpgrades3 = (long) objInfo.get("keystone_upgrades_3");
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
