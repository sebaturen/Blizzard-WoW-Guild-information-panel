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

    public void faction(JsonObject info) {
        staticInfo(info);
    }

    public void gender(JsonObject info) {
        staticInfo(info);
    }

    public void type(JsonObject info) {
        staticInfo(info);
    }

    private void staticInfo(JsonObject info) {

        try {
            JsonArray type_db = BlizzardUpdate.dbConnect.select(
                    StaticInformation.TABLE_NAME,
                    new String[]{"type"},
                    "type=?",
                    new String[]{info.get("type").getAsString()}
            );

            if (type_db.size() == 0) {
                BlizzardUpdate.dbConnect.insert(
                        StaticInformation.TABLE_NAME,
                        StaticInformation.TABLE_KEY,
                        new String[]{
                                "type",
                                "name"
                        },
                        new String[]{
                                info.get("type").getAsString(),
                                info.get("name").getAsJsonObject().toString()
                        }
                );
            }
        } catch (SQLException | DataException e ) {
            Logs.infoLog(StaticInformationAPI.class, "FAIL - to get/save static information");
        }

    }
}
