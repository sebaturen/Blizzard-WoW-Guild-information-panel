package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Spell;
import com.blizzardPanel.gameObject.characters.PlayableSpec;
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

public class SpellAPI extends BlizzardAPI {

    public SpellAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Load spell detail
     * @param spell {"key": {"href": URL}, "id": ID}
     */
    public void spellDetail(JsonObject spell) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String spellId = spell.get("id").getAsString();

        try {
            // Check is category previously exist:
            JsonArray spell_db = BlizzardUpdate.dbConnect.select(
                    Spell.TABLE_NAME,
                    new String[] {"last_modified"},
                    Spell.TABLE_KEY +" = ?",
                    new String[] {spellId}
            );
            boolean isInDb = (spell_db.size() > 0);
            long lastModified = 0L;
            if (spell_db.size() > 0) {
                lastModified = spell_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare Call
            Call<JsonObject> call = apiCalls.spell(
                    spellId,
                    "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run call
            Response<JsonObject> resp = call.execute();
            if(resp.isSuccessful()) {
                JsonObject blizz_spell = resp.body();

                // Prepare Values
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(blizz_spell.getAsJsonObject("name").toString());
                columns.add("description");
                values.add(blizz_spell.getAsJsonObject("description").toString());

                columns.add("media_id");
                values.add(blizz_spell.getAsJsonObject("media").get("id").getAsString());
                BlizzardUpdate.shared.mediaAPI.mediaDetail(blizz_spell.getAsJsonObject("media"));

                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if(isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            Spell.TABLE_NAME,
                            columns,
                            values,
                            Spell.TABLE_KEY +"=?",
                            new String[]{spellId+""}
                    );
                } else { // Insert
                    columns.add(Spell.TABLE_KEY);
                    values.add(spellId+"");
                    BlizzardUpdate.dbConnect.insert(
                            Spell.TABLE_NAME,
                            Spell.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(Spell.class, "Spell is update "+ spellId);


            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(Spell.class, "NOT Modified Spell Detail "+ spellId);
                } else {
                    Logs.errorLog(Spell.class, "ERROR - Spell detail "+ spellId +" - "+ resp.code());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(Spell.class, "FAILED - to get Spell detail "+ e);
        }

    }
}
