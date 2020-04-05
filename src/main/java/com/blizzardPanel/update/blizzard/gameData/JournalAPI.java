package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Expansion;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.journal.Encounter;
import com.blizzardPanel.gameObject.journal.Instance;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JournalAPI extends BlizzardAPI {

    public JournalAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    private enum additionalDataType {
        MAP,
        AREA,
        LOCATION
    }

    public void update() {
        instanceIndex();
        encountersIndex();
    }

    /**
     * Load all encounters for encounters index
     */
    private void encountersIndex() {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        Call<JsonObject> call = apiCalls.journalEncounterIndex(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray encounters = response.body().getAsJsonArray("encounters");
                    for(JsonElement encounter : encounters) {
                        encounter(encounter.getAsJsonObject());
                    }
                    Logs.infoLog(this.getClass(), "Encounter Index process is completed");

                } else {
                    Logs.errorLog(this.getClass(), "ERROR to update encounters index "+ response.code() +" // "+ call.request());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logs.fatalLog(this.getClass(), "FAILED to update encounters index "+ t);
            }
        });
    }

    /**
     * Load all instance for instance index...
     */
    private void instanceIndex() {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        Call<JsonObject> call = apiCalls.journalInstanceIndex(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().has("instances")) {
                    JsonArray instances = response.body().getAsJsonArray("instances");
                    for(JsonElement instance : instances) {
                        instance(instance.getAsJsonObject());
                    }
                    Logs.infoLog(this.getClass(), "Instance Index process is completed");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR to get a instance index "+ response.code() +" // "+ call.request());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logs.fatalLog(this.getClass(), "FAILED to get a instance index "+ t);
            }
        });
    }

    /**
     * Load instance detail
     * @param reference {"key": {"href": URL}, "id": ID, "name": {NAME}}
     */
    private void instance(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");

        String instanceId = reference.get("id").getAsString();

        try {
            // Check if exist in DB
            JsonArray instance_db = BlizzardUpdate.dbConnect.select(
                    Instance.TABLE_NAME,
                    new String[]{Instance.TABLE_KEY, "last_modified"},
                    Instance.TABLE_KEY +"=?",
                    new String[]{instanceId}
            );
            boolean isInDb = (instance_db.size() > 0);
            long lastModified = 0L;
            if (instance_db.size() > 0) {
                lastModified = instance_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare Call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run Call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject instanceDetail = resp.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(instanceDetail.getAsJsonObject("name").toString());

                if (instanceDetail.has("map")) {
                    columns.add("map_id");
                    values.add(instanceDetail.getAsJsonObject("map").get("id").getAsString());
                    additionalData(additionalDataType.MAP, instanceDetail.getAsJsonObject("map"));
                }

                if (instanceDetail.has("area")) {
                    columns.add("area_id");
                    values.add(instanceDetail.getAsJsonObject("area").get("id").getAsString());
                    additionalData(additionalDataType.AREA, instanceDetail.getAsJsonObject("area"));
                }

                if (instanceDetail.has("description")) {
                    columns.add("description");
                    values.add(instanceDetail.getAsJsonObject("description").toString());
                }

                columns.add("expansion_id");
                values.add(instanceDetail.getAsJsonObject("expansion").get("id").getAsString());
                expansion(instanceDetail.getAsJsonObject("expansion"));

                if (instanceDetail.has("location")) {
                    columns.add("location_id");
                    values.add(instanceDetail.getAsJsonObject("location").get("id").getAsString());
                    additionalData(additionalDataType.LOCATION, instanceDetail.getAsJsonObject("location"));
                }

                columns.add("modes");
                JsonArray modes = new JsonArray();
                for (JsonElement mode : instanceDetail.getAsJsonArray("modes")) {
                    JsonObject modeDetail = mode.getAsJsonObject();

                    JsonObject mod = new JsonObject();
                    mod.addProperty("type", modeDetail.getAsJsonObject("mode").get("type").getAsString());
                    BlizzardUpdate.shared.staticInformationAPI.mode(modeDetail.getAsJsonObject("mode"));
                    mod.addProperty("players", modeDetail.get("players").getAsInt());
                    mod.addProperty("is_tracked", modeDetail.get("is_tracked").getAsBoolean());

                    modes.add(mod);
                }
                values.add(modes.toString());

                columns.add("media_id");
                values.add(instanceDetail.getAsJsonObject("media").get("id").getAsString());
                BlizzardUpdate.shared.mediaAPI.mediaDetail(Media.type.INSTANCE, instanceDetail.getAsJsonObject("media"));

                columns.add("minimum_level");
                values.add(instanceDetail.get("minimum_level").getAsString());

                columns.add("category_type");
                values.add(instanceDetail.getAsJsonObject("category").get("type").getAsString());

                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            Instance.TABLE_NAME,
                            columns,
                            values,
                            Instance.TABLE_KEY +"=?",
                            new String[]{instanceId}
                    );
                    Logs.infoLog(this.getClass(), "Instance is UPDATE ["+ instanceId +"]");
                } else { // Insert
                    columns.add(Instance.TABLE_KEY);
                    values.add(instanceId);
                    BlizzardUpdate.dbConnect.insert(
                            Instance.TABLE_NAME,
                            Instance.TABLE_KEY,
                            columns,
                            values
                    );
                    Logs.infoLog(this.getClass(), "Instance is INSERT ["+ instanceId +"]");
                }
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified instance ["+ instanceId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - instance ["+ instanceId +"] "+ resp.code() +" // "+ call.request());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get/update instance ["+ instanceId +"] "+ e);
        }
    }

    /**
     * Save map detail
     * @param detail {"name": {NAME JSON}, "id": ID}
     */
    private void additionalData(additionalDataType type, JsonObject detail) {

        String tableName = "";
        String tableKey = "";
        switch (type) {
            case MAP:
                tableName = Instance.MAP_TABLE_NAME;
                tableKey = Instance.MAP_TABLE_KEY;
                break;
            case AREA:
                tableName = Instance.AREA_TABLE_NAME;
                tableKey = Instance.AREA_TABLE_KEY;
                break;
            case LOCATION:
                tableName = Instance.LOCATION_TABLE_NAME;
                tableKey = Instance.LOCATION_TABLE_KEY;
                break;
        }

        try {
            JsonArray map_db = BlizzardUpdate.dbConnect.select(
                    tableName,
                    new String[]{tableKey},
                    tableKey +"=?",
                    new String[]{detail.get("id").getAsString()}
            );

            if (map_db.size() == 0) { // Insert
                BlizzardUpdate.dbConnect.insert(
                        tableName,
                        tableKey,
                        new String[]{tableKey, "name"},
                        new String[]{
                                detail.get("id").getAsString(),
                                detail.getAsJsonObject("name").toString()
                        }
                );
                Logs.infoLog(this.getClass(), "New "+ type +" is create ["+ detail.get("id") +"]");
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to update/insert new "+ type +" ["+ detail.get("id") +"] "+ e);
        }
    }

    /**
     * Load expansion information
     * @param reference {"key": {"href": URL}, "id": ID}
     */
    private void expansion(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");

        String expansionId = reference.get("id").getAsString();

        try {

            // Check is category previously exist:
            JsonArray expansion_db = BlizzardUpdate.dbConnect.select(
                    Expansion.TABLE_NAME,
                    new String[]{"last_modified"},
                    Expansion.TABLE_KEY +" = ?",
                    new String[]{expansionId}
            );
            boolean isInDb = (expansion_db.size() > 0);
            long lastModified = 0L;
            if (expansion_db.size() > 0) {
                lastModified = expansion_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );


            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject exp_blizz = resp.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(exp_blizz.getAsJsonObject("name").toString());
                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");


                if (isInDb) { // UPDATE
                    BlizzardUpdate.dbConnect.update(
                            Expansion.TABLE_NAME,
                            columns,
                            values,
                            Expansion.TABLE_KEY+" = ?",
                            new String[]{expansionId}
                    );
                    Logs.infoLog(this.getClass(), "Expansion IS UPDATE ["+ expansionId +"]");
                } else { // INSERT
                    columns.add(Expansion.TABLE_KEY);
                    values.add(expansionId);
                    BlizzardUpdate.dbConnect.insert(
                            Expansion.TABLE_NAME,
                            Expansion.TABLE_KEY,
                            columns,
                            values
                    );
                    Logs.infoLog(this.getClass(), "Expansion IS INSERT ["+ expansionId +"]");
                }
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified expansion ["+ expansionId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - expansion ["+ expansionId +"] - "+ resp.code() +" // "+ call.request());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get/update expansion "+ e);
        }

    }

    /**
     * Load encounter detail
     * @param reference {"key": {"href": URL}, "id": ID, "name": {NAME}}
     */
    private void encounter(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");

        String encounterId = reference.get("id").getAsString();

        try {
            // Check if exist in DB
            JsonArray encounter_db = BlizzardUpdate.dbConnect.select(
                    Encounter.TABLE_NAME,
                    new String[]{Encounter.TABLE_KEY, "last_modified"},
                    Encounter.TABLE_KEY +"=?",
                    new String[]{encounterId}
            );
            boolean isInDb = (encounter_db.size() > 0);
            long lastModified = 0L;
            if (encounter_db.size() > 0) {
                lastModified = encounter_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare Call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run Call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject encounterDetail = resp.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(encounterDetail.getAsJsonObject("name").toString());

                if (encounterDetail.has("description")) {
                    columns.add("description");
                    values.add(encounterDetail.getAsJsonObject("description").toString());
                }

                if (encounterDetail.has("creatures")) {
                    columns.add("creatures");
                    JsonArray creatures = new JsonArray();
                    for(JsonElement creature : encounterDetail.getAsJsonArray("creatures")) {
                        JsonObject creatureDetail = creature.getAsJsonObject();

                        JsonObject creat = new JsonObject();
                        creat.addProperty("id", creatureDetail.get("id").getAsInt());
                        creature(creatureDetail);

                        creatures.add(creat);
                    }
                    values.add(creatures);
                }

                if (encounterDetail.getAsJsonObject("instance").get("id").getAsLong() > 0) {
                    columns.add("instance_id");
                    values.add(encounterDetail.getAsJsonObject("instance").get("id").getAsString());
                    instance(encounterDetail.getAsJsonObject("instance"));
                }

                if (encounterDetail.has("category")
                        && !encounterDetail.getAsJsonObject("category").isJsonNull()
                        && encounterDetail.getAsJsonObject("category").has("type")
                ) {
                    columns.add("category");
                    values.add(encounterDetail.getAsJsonObject("category").get("type"));
                }

                if (encounterDetail.has("modes")) {
                    columns.add("modes");
                    JsonArray modes = new JsonArray();
                    for (JsonElement mode : encounterDetail.getAsJsonArray("modes")) {
                        JsonObject modeDetail = mode.getAsJsonObject();

                        JsonObject mod = new JsonObject();
                        mod.addProperty("type", modeDetail.get("type").getAsString());
                        BlizzardUpdate.shared.staticInformationAPI.mode(modeDetail);

                        modes.add(mod);
                    }
                    values.add(modes.toString());
                }

                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            Encounter.TABLE_NAME,
                            columns,
                            values,
                            Encounter.TABLE_KEY +"=?",
                            new String[]{encounterId}
                    );
                    Logs.infoLog(this.getClass(), "Encounter is UPDATE ["+ encounterId +"]");
                } else { // Insert
                    columns.add(Encounter.TABLE_KEY);
                    values.add(encounterId);
                    BlizzardUpdate.dbConnect.insert(
                            Encounter.TABLE_NAME,
                            Encounter.TABLE_KEY,
                            columns,
                            values
                    );
                    Logs.infoLog(this.getClass(), "Encounter is INSERT ["+ encounterId +"]");
                }
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified encounter ["+ encounterId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - encounter ["+ encounterId +"] "+ resp.code() +" // "+ call.request());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get/update encounter ["+ encounterId +"] "+ e);
        }
    }

    /**
     * Save creature detail
     * @param detail {"id": ID, "name": {NAME}, "creature_display": {REFERENCE}}
     */
    private void creature(JsonObject detail) {
        try {
            JsonArray creature_db = BlizzardUpdate.dbConnect.select(
                    Encounter.CREATURE_TABLE_NAME,
                    new String[]{Encounter.TABLE_KEY},
                    Encounter.TABLE_KEY +"=?",
                    new String[]{detail.get("id").getAsString()}
            );

            if (creature_db.size() == 0) { // Insert
                // Media
                BlizzardUpdate.shared.mediaAPI.mediaDetail(Media.type.CREATURE, detail.getAsJsonObject("creature_display"));

                // Add data
                BlizzardUpdate.dbConnect.insert(
                        Encounter.CREATURE_TABLE_NAME,
                        Encounter.CREATURE_TABLE_KEY,
                        new String[]{Encounter.CREATURE_TABLE_KEY, "name", "media_id"},
                        new String[]{
                                detail.get("id").getAsString(),
                                detail.getAsJsonObject("name").toString(),
                                detail.getAsJsonObject("creature_display").get("id").getAsString()
                        }
                );
                Logs.infoLog(this.getClass(), "New creature is create ["+ detail.get("id") +"]");
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to update/insert new creature ["+ detail.get("id") +"] "+ e);
        }
    }
}
