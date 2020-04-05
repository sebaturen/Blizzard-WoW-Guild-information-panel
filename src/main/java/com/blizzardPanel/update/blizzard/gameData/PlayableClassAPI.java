package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.characters.playable.PlayableClass;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayableClassAPI extends BlizzardAPI {

    public PlayableClassAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Update all playable class is save in DB
     */
    public void update() {
        try {
            JsonArray class_db = BlizzardUpdate.dbConnect.select(
                    PlayableClass.TABLE_NAME,
                    new String[]{PlayableClass.TABLE_KEY}
            );

            for (JsonElement classD : class_db) {
                classDetail(classD.getAsJsonObject());
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get a class DB detail "+ e);
        }
    }

    /**
     * Load class detail
     * @param reference {"id": ID, href not is required}
     */
    public void classDetail(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String classId = reference.get("id").getAsString();

        try {

            // Check is category previously exist:
            JsonArray class_db = BlizzardUpdate.dbConnect.select(
                    PlayableClass.TABLE_NAME,
                    new String[]{"last_modified"},
                    PlayableClass.TABLE_KEY +" = ?",
                    new String[]{classId}
            );
            boolean isInDb = (class_db.size() > 0);
            Long lastModified = 0L;
            if (class_db.size() > 0) {
                lastModified = class_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.playableClass(
                    classId,
                    "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            Response<JsonObject> response = call.execute();
            if (response.isSuccessful()) {

                JsonObject playableClass = response.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(playableClass.getAsJsonObject("name").toString());
                columns.add("gender_name_male");
                values.add(playableClass.getAsJsonObject("gender_name").getAsJsonObject("male").toString());
                columns.add("gender_name_female");
                values.add(playableClass.getAsJsonObject("gender_name").getAsJsonObject("female").toString());
                columns.add("media_id");
                values.add(playableClass.getAsJsonObject("media").get("id").getAsString());
                BlizzardUpdate.shared.mediaAPI.mediaDetail(Media.type.P_CLASS, playableClass.getAsJsonObject("media"));
                columns.add("last_modified");
                values.add(response.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // update
                    BlizzardUpdate.dbConnect.update(
                            PlayableClass.TABLE_NAME,
                            columns,
                            values,
                            PlayableClass.TABLE_KEY+"=?",
                            new String[]{classId+""}
                    );
                } else { // Insert
                    columns.add(PlayableClass.TABLE_KEY);
                    values.add(classId+"");
                    BlizzardUpdate.dbConnect.insert(
                            PlayableClass.TABLE_NAME,
                            PlayableClass.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(this.getClass(), "Playable Class OK "+ classId);
            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Playable Class "+ classId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Playable Class "+ classId +" - "+ response.code() +" // "+ call.request());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get Playable Class info "+ e);
        }

    }

}
