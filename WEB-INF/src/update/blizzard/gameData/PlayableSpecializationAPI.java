package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.characters.Static.PlayableSpec;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayableSpecializationAPI extends BlizzardAPI {

    public PlayableSpecializationAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Load spec detail
     * @param reference {"key": {"href": URL}, "id": ID}
     */
    public void specializationDetail(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String specId = reference.get("id").getAsString();

        try {
            // Check if category previously exist:
            JsonArray spec_db = BlizzardUpdate.dbConnect.select(
                    PlayableSpec.TABLE_NAME,
                    new String[] {"last_modified"},
                    PlayableSpec.TABLE_KEY +" = ?",
                    new String[] {specId}
            );
            boolean isInDb = (spec_db.size() > 0);
            long lastModified = 0L;
            if (spec_db.size() > 0) {
                lastModified = spec_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.playableSpecialization(
                    specId,
                    "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_spec = resp.body();

                // Prepare Values
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();

                columns.add("playable_class_id");
                values.add(blizz_spec.getAsJsonObject("playable_class").get("id").getAsString());
                BlizzardUpdate.shared.playableClassAPI.classDetail(blizz_spec.getAsJsonObject("playable_class"));

                columns.add("name");
                values.add(blizz_spec.getAsJsonObject("name").toString());

                columns.add("role_type");
                values.add(blizz_spec.getAsJsonObject("role").get("type").getAsString());
                BlizzardUpdate.shared.staticInformationAPI.role(blizz_spec.getAsJsonObject("role"));

                columns.add("desc_male");
                values.add(blizz_spec.getAsJsonObject("gender_description").getAsJsonObject("male").toString());
                columns.add("desc_female");
                values.add(blizz_spec.getAsJsonObject("gender_description").getAsJsonObject("female").toString());

                columns.add("media_id");
                values.add(blizz_spec.getAsJsonObject("media").get("id").getAsString());
                BlizzardUpdate.shared.mediaAPI.mediaDetail(blizz_spec.getAsJsonObject("media"));

                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            PlayableSpec.TABLE_NAME,
                            columns,
                            values,
                            PlayableSpec.TABLE_KEY +"=?",
                            new String[]{blizz_spec.get("id").getAsString()}
                    );
                } else { // Insert
                    columns.add(PlayableSpec.TABLE_KEY);
                    values.add(specId+"");
                    BlizzardUpdate.dbConnect.insert(
                            PlayableSpec.TABLE_NAME,
                            PlayableSpec.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(this.getClass(), "OK - Specialisation is update "+ specId);


            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Specialization Detail "+ specId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - specialization detail "+ specId +" - "+ resp.code() +" // "+ call.request());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get specialization detail "+ e);
        }
    }
}
