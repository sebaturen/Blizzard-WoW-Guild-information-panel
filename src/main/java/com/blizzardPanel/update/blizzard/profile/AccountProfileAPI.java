package com.blizzardPanel.update.blizzard.profile;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.gameObject.characters.CharacterInfo;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.characters.CharacterSpec;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountProfileAPI extends BlizzardAPI {

    public AccountProfileAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    public void summary(User u) {
        Call<JsonObject> call = apiCalls.accountProfileSummary(
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                "Bearer "+u.getAccess_token()
        );

        if (u.getIs_guild_member()) { // async process
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    summaryResponse(u, call, response);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Logs.fatalLog(this.getClass(), "FAILED - to get a user summary ["+ u.getBattle_tag() +"] - "+ t);
                }
            });
        } else { // sync process
            try {
                Response<JsonObject> resp = call.execute();
                summaryResponse(u, call, resp);
            } catch (IOException e) {
                Logs.errorLog(this.getClass(), "Failed to sync user summary ["+ u.getBattle_tag() +"]");
            }
        }

    }

    private void summaryResponse(User u, Call<JsonObject> call, Response<JsonObject> response) {
        if (response.isSuccessful()) {
            JsonObject detail = response.body();
            if (detail.has("wow_accounts")) {

                try {
                    BlizzardUpdate.dbConnect.update(
                            User.TABLE_NAME,
                            new String[]{"last_alters_update"},
                            new String[]{new Date().getTime()+""},
                            User.TABLE_KEY +"=?",
                            new String[]{u.getId()+""}
                    );
                } catch (SQLException | DataException e) {
                    Logs.fatalLog(this.getClass(), "FAILED to update user alters `is update` ["+ u.getId() +"] "+ e);
                }

                for(JsonElement wowAccount : detail.get("wow_accounts").getAsJsonArray() ) {
                    JsonObject wowAccountDetail = wowAccount.getAsJsonObject();
                    if (wowAccountDetail.has("characters")) {
                        for (JsonElement charDet : wowAccountDetail.get("characters").getAsJsonArray()) {
                            saveCharacter(charDet.getAsJsonObject(), u);
                        }
                    }
                }
            }
        } else {
            Logs.errorLog(this.getClass(), "ERROR to get a user summary ["+ u.getBattle_tag() +"] - "+ response.code() +" // "+ call.request());
        }
    }

    private void saveCharacter(JsonObject charDet, User u) {

        try {

            // Save character information...
            long charId = BlizzardUpdate.shared.characterProfileAPI.save(
                    charDet.getAsJsonObject("realm").get("slug").getAsString(),
                    charDet.get("name").getAsString()
            );
            if (charId != charDet.get("id").getAsLong()) {

                // Check if previously is save
                JsonArray internalId_db = BlizzardUpdate.dbConnect.select(
                        CharacterMember.TABLE_NAME,
                        new String[]{CharacterMember.TABLE_KEY},
                        CharacterMember.TABLE_KEY +"=?",
                        new String[]{charDet.get("id").getAsString()}
                );

                if (internalId_db.size() <= 0) {
                    // Save small information
                    // Prepare values:
                    List<Object> columns = new ArrayList<>();
                    List<Object> values = new ArrayList<>();
                    columns.add("name");
                    values.add(charDet.get("name").getAsString());
                    columns.add("realm_slug");
                    values.add(charDet.getAsJsonObject("realm").get("slug").getAsString());
                    columns.add("is_valid");
                    values.add("0");
                    columns.add("blizzard_id");
                    values.add(charDet.get("id").getAsString());
                    columns.add("id");
                    values.add(charDet.get("id").getAsString());

                    BlizzardUpdate.dbConnect.insert(
                            CharacterMember.TABLE_NAME,
                            CharacterMember.TABLE_KEY,
                            columns,
                            values
                    );
                } else {
                    BlizzardUpdate.dbConnect.update(
                            CharacterMember.TABLE_NAME,
                            new String[]{"is_valid"},
                            new String[]{"0"},
                            CharacterMember.TABLE_KEY+"=?",
                            new String[]{charDet.get("id").getAsString()}
                    );
                }
                charId = charDet.get("id").getAsLong();
            }

            // Save Info
            // Prepare values:
            List<Object> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            columns.add("character_id");
            values.add(charId+"");

            columns.add("class_id");
            values.add(charDet.getAsJsonObject("playable_class").get("id").getAsString());
            BlizzardUpdate.shared.playableClassAPI.classDetail(charDet.getAsJsonObject("playable_class"));

            columns.add("race_id");
            values.add(charDet.getAsJsonObject("playable_race").get("id").getAsString());
            BlizzardUpdate.shared.playableRaceAPI.raceDetail(charDet.getAsJsonObject("playable_race"));

            columns.add("gender_type");
            values.add(charDet.getAsJsonObject("gender").get("type").getAsString());
            BlizzardUpdate.shared.staticInformationAPI.gender(charDet.getAsJsonObject("gender"));

            columns.add("level");
            values.add(charDet.get("level").getAsString());

            columns.add("faction_type");
            values.add(charDet.getAsJsonObject("faction").get("type").getAsString());
            BlizzardUpdate.shared.staticInformationAPI.faction(charDet.getAsJsonObject("faction"));

            JsonArray charInfo_db = BlizzardUpdate.dbConnect.select(
                    CharacterInfo.TABLE_NAME,
                    new String[]{CharacterInfo.TABLE_KEY},
                    "character_id=?",
                    new String[]{charId+""}
            );

            if (charInfo_db.size() > 0) { // Update
                BlizzardUpdate.dbConnect.update(
                        CharacterInfo.TABLE_NAME,
                        columns,
                        values,
                        CharacterInfo.TABLE_KEY +"=?",
                        new String[]{charId+""}
                );
                Logs.infoLog(this.getClass(), "INFO Character is UPDATE ["+ charId +"]");
            } else { // Insert
                columns.add("achievement_points");
                values.add("0");
                BlizzardUpdate.dbConnect.insert(
                        CharacterInfo.TABLE_NAME,
                        CharacterInfo.TABLE_KEY,
                        columns,
                        values
                );
                Logs.infoLog(this.getClass(), "INFO Character is INSERT ["+ charId +"]");
            }

            // Link character-user
            JsonArray usCh_db = BlizzardUpdate.dbConnect.select(
                    User.USER_CHARACTER_TABLE_NAME,
                    new String[]{User.USER_CHARACTER_TABLE_KEY},
                    "user_id=? AND character_id=?",
                    new String[]{u.getId()+"", charId+""}
            );

            if (usCh_db.size() == 0) { // Insert
                BlizzardUpdate.dbConnect.insert(
                        User.USER_CHARACTER_TABLE_NAME,
                        User.USER_CHARACTER_TABLE_KEY,
                        new String[]{
                                "user_id",
                                "character_id"
                        },
                        new String[]{
                                u.getId()+"",
                                charId+""
                        }
                );
                Logs.infoLog(this.getClass(), "Character-User is correct link ["+ charId +"]-["+ u.getBattle_tag() +"]");
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to save/update user character ["+ u.getBattle_tag() +"] - ["+ charDet.get("name") +"] - "+ e);
        }
    }
}
