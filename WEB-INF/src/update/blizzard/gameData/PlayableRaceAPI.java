package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.characters.PlayableRace;
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

public class PlayableRaceAPI extends BlizzardAPI {

    public PlayableRaceAPI(WoWAPIService apicalls) {
        super(apicalls);
    }

    public void raceDetail(JsonObject detail) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String raceId = detail.get("id").getAsString();

        try {
            // Check is race previously exist
            JsonArray race_db = BlizzardUpdate.dbConnect.select(
                    PlayableRace.TABLE_NAME,
                    new String[]{PlayableRace.TABLE_KEY, "last_modified"},
                    PlayableRace.TABLE_KEY+"=?",
                    new String[]{raceId+""}
            );
            boolean isInDb = (race_db.size() > 0);
            long lastModified = 0L;
            if (race_db.size() > 0) {
                lastModified = race_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.playableRace(
                    raceId,
                    "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_race = resp.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(blizz_race.getAsJsonObject("name").toString());
                columns.add("gender_name_male");
                values.add(blizz_race.getAsJsonObject("gender_name").getAsJsonObject("male").toString());
                columns.add("gender_name_female");
                values.add(blizz_race.getAsJsonObject("gender_name").getAsJsonObject("female").toString());
                columns.add("faction_type");
                values.add(blizz_race.getAsJsonObject("faction").get("type").getAsString());
                BlizzardUpdate.shared.staticInformationAPI.faction(blizz_race.getAsJsonObject("faction"));
                columns.add("is_selectable");
                values.add((blizz_race.get("is_selectable").getAsBoolean())? "1":"0");
                columns.add("is_allied_race");
                values.add((blizz_race.get("is_allied_race").getAsBoolean())? "1":"0");
                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // update
                    BlizzardUpdate.dbConnect.update(
                            PlayableRace.TABLE_NAME,
                            columns,
                            values,
                            PlayableRace.TABLE_KEY+"=?",
                            new String[]{raceId+""}
                    );
                } else { //insert
                    columns.add(PlayableRace.TABLE_KEY);
                    values.add(blizz_race.get("id").getAsString());
                    BlizzardUpdate.dbConnect.insert(
                            PlayableRace.TABLE_NAME,
                            PlayableRace.TABLE_KEY,
                            columns,
                            values
                    );
                }
                Logs.infoLog(PlayableRaceAPI.class, "Playable Race OK "+ raceId);

            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(PlayableRaceAPI.class, "NOT Modified Playable Race "+ raceId);
                } else {
                    Logs.errorLog(PlayableRaceAPI.class, "ERROR - playable race "+ raceId +" - "+ resp.code());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(PlayableRaceAPI.class, "FAILED - to get playable race "+ e);
        }

    }
}
