package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.StaticInformation;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;

public class StaticInformationAPI extends BlizzardAPI {

    public StaticInformationAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Load faction
     * @param info {"type": FACTION, "name": NAME_LOCALE}
     */
    public void faction(JsonObject info) {
        staticInfo(info);
    }

    /**
     * Load gender
     * @param info {"type": GENDER, "name": NAME_LOCALE}
     */
    public void gender(JsonObject info) {
        staticInfo(info);
    }

    /**
     * Load type
     * @param info {"type": TYPE, "name": NAME_LOCALE}
     */
    public void type(JsonObject info) {
        staticInfo(info);
    }

    /**
     * Load playable role
     * @param info {"type": ROLE, "name": NAME_LOCALE}
     */
    public void role(JsonObject info) {
        staticInfo(info);
    }

    /**
     * Load qualirty
     * @param info {"type": QUALITY, "name": NAME_LOCALE}
     */
    public void quality(JsonObject info) {
        staticInfo(info);
    }

    /**
     * Load slot
     * @param info {"type": SLOT, "name": NAME_LOCALE}
     */
    public void slot(JsonObject info) {
        staticInfo(info);
    }

    /**
     * Load slot
     * @param info {"id": ID, "name": NAME_LOCALE}
     */
    public void power(JsonObject info) {
        info.addProperty("type", "POWER_"+ info.get("id"));
        staticInfo(info);
    }


    private void staticInfo(JsonObject info) {

        try {
            JsonArray type_db = BlizzardUpdate.dbConnect.select(
                    StaticInformation.TABLE_NAME,
                    new String[]{"type"},
                    StaticInformation.TABLE_KEY +"=?",
                    new String[]{info.get("type").getAsString()}
            );

            if (type_db.size() == 0) {
                BlizzardUpdate.dbConnect.insert(
                        StaticInformation.TABLE_NAME,
                        StaticInformation.TABLE_KEY,
                        new String[]{
                                StaticInformation.TABLE_KEY,
                                "name"
                        },
                        new String[]{
                                info.get("type").getAsString(),
                                info.get("name").getAsJsonObject().toString()
                        }
                );
                Logs.infoLog(this.getClass(), "OK - Static info is save - "+ info.get("type").getAsString());
            }
        } catch (SQLException | DataException e ) {
            Logs.fatalLog(this.getClass(), "FAILED - to get/save static information");
        }

    }
}
