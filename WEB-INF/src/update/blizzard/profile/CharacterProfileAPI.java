package com.blizzardPanel.update.blizzard.profile;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.characters.CharacterSpec;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.blizzardPanel.update.blizzard.gameData.AchievementAPI;
import com.blizzardPanel.update.blizzard.gameData.PlayableClassAPI;
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
import java.util.Date;
import java.util.List;

public class CharacterProfileAPI extends BlizzardAPI {

    public CharacterProfileAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    private int status(String realmSlug, String characterName) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        characterName = characterName.toLowerCase();

        // Check character status:
        Call<JsonObject> status = apiCalls.characterProfileStatus(
                realmSlug,
                characterName,
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization()
        );


        try {
            Response<JsonObject> respondStatus = status.execute();
            if (respondStatus.isSuccessful()) {
                return respondStatus.body().get("id").getAsInt();
            }
        } catch (IOException e) {
            // Status is fail
        }

        return -1;

    }

    public boolean summary(String realmSlug, String characterName) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired())
            BlizzardUpdate.shared.generateAccessToken();

        characterName = characterName.toLowerCase();

        int charId = status(realmSlug, characterName);

        // Check if have a last modified:
        long lastModified = 0L;
        try {

            JsonArray lastModified_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"last_modified"},
                    "id=?",
                    new String[]{charId + ""}
            );
            if (lastModified_db.size() > 0) {
                lastModified = lastModified_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }
        } catch (SQLException | DataException e) {
            // Fail to get info in DB
        }

        if (charId != -1) { // character EXIST!

            Call<JsonObject> sumCall = apiCalls.characterProfileSummary(
                    realmSlug,
                    characterName,
                    "profile-" + GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            try {
                Response<JsonObject> response = sumCall.execute();

                if (response.isSuccessful()) {

                    JsonObject summary = response.body();
                    //--------------------------------------------------------------------------------------------------
                    //
                    // Save static information
                    //
                    //--------------------------------------------------------------------------------------------------
                    BlizzardUpdate.shared.staticInformationAPI.gender(summary.getAsJsonObject("gender"));
                    BlizzardUpdate.shared.staticInformationAPI.faction(summary.getAsJsonObject("faction"));

                    //--------------------------------------------------------------------------------------------------
                    //
                    // Save minimal information
                    //
                    //--------------------------------------------------------------------------------------------------
                    saveMinimalInfo(summary, lastModified, lastModified != 0);

                    //--------------------------------------------------------------------------------------------------
                    //
                    // Save character-info
                    //
                    //--------------------------------------------------------------------------------------------------
                    saveFullInfo(summary);

                    //--------------------------------------------------------------------------------------------------
                    //
                    // Save character-spec
                    //
                    //--------------------------------------------------------------------------------------------------
                    saveCharacterSpec(summary.get("id").getAsInt(), summary.getAsJsonObject("specializations"));


                    // Items
                    // Specs
                    // Status
                    return true;
                } else {
                    if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                        Logs.infoLog(CharacterProfileAPI.class, "NOT Modified Character Summary " + charId);
                        return true;
                    } else {
                        Logs.infoLog(CharacterProfileAPI.class, "ERROR - Character Summary " + charId + " - " + response.code());
                    }
                }
            } catch(IOException e){
                Logs.infoLog(CharacterProfileAPI.class, "FAIL - Character profile (" + realmSlug + "/" + characterName + ") - " + e);
            }
        }
        return false;
    }

    public void smallInfo(JsonObject character) {

        try {
            // Check if exist in DB
            JsonArray chardet_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"last_modified"},
                    "id = ?",
                    new String[]{character.get("id").getAsString()}
            );

            // Prepare values:
            List<Object> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            columns.add("name");
            values.add(character.get("name").getAsString());

            columns.add("realm_id");
            values.add(character.getAsJsonObject("realm").get("id").getAsString());
            BlizzardUpdate.shared.connectedRealmAPI.load(character.getAsJsonObject("realm"));

            columns.add("is_valid");
            values.add("1"); //false
            columns.add("last_modified");
            values.add(""+ new Date().getTime()); // now!

            if (chardet_db.size() > 0) { // Update
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        columns,
                        values,
                        "id = ?",
                        new String[]{character.get("id").getAsString()}
                );
            } else { // Insert
                columns.add("id");
                values.add(character.get("id").getAsString());
                BlizzardUpdate.dbConnect.insert(
                        CharacterMember.TABLE_NAME,
                        CharacterMember.TABLE_KEY,
                        columns,
                        values
                );
            }
            Logs.infoLog(CharacterProfileAPI.class, "Small character data is added "+ character.get("name").getAsString() +" / "+ character.get("id").getAsString());

        } catch (DataException | SQLException e) {
            Logs.infoLog(GuildAPI.class, "FAIL - to save small character detail "+ e);
        }
    }

    public void syncAll() {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        try {

            JsonArray oldCharProfile = BlizzardUpdate.dbConnect.selectQuery("" +
                    "SELECT " +
                    "   member_name, " +
                    "   realm " +
                    "from gMembers_id_name");

            for (JsonElement character : oldCharProfile) {
                String charName = character.getAsJsonObject().get("member_name").getAsString().toLowerCase();
                String realmSlug = character.getAsJsonObject().get("realm").getAsString().toLowerCase().replace(" ", "-").replace("'","");
                System.out.println("Start data from ("+realmSlug+"/"+charName+")");
                summary(realmSlug, charName);
            }
        } catch (SQLException | DataException e) {
            System.out.println("Fail to get all date");
        }
        System.out.println("finish!");
    }

    public void syncNewId() {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        try {

            JsonArray oldCharProfile = BlizzardUpdate.dbConnect.selectQuery("" +
                    "SELECT " +
                    "   internal_id, "+
                    "   member_name, " +
                    "   realm " +
                    "FROM gMembers_id_name " +
                    "WHERE id is null;");

            for(JsonElement character : oldCharProfile) {
                String charName = character.getAsJsonObject().get("member_name").getAsString().toLowerCase();
                String realmSlug = character.getAsJsonObject().get("realm").getAsString().toLowerCase().replace(" ", "-").replace("'","");
                int internalId = character.getAsJsonObject().get("internal_id").getAsInt();
                System.out.println(charName +" -- "+ realmSlug);

                Call<JsonObject> call = apiCalls.characterProfileSummary(
                        realmSlug,
                        charName,
                        "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                        BlizzardUpdate.shared.accessToken.getAuthorization(),
                        "0"
                );

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject character = response.body();
                        if (response.isSuccessful()) {

                            try {

                                // Add in new DB
                                BlizzardUpdate.dbConnect.insert(
                                        CharacterMember.TABLE_NAME,
                                        CharacterMember.TABLE_KEY,
                                        new String[]{
                                                "id",
                                                "name",
                                                "realm_id",
                                                "is_valid",
                                                "last_modified"
                                        },
                                        new String[]{
                                                character.get("id").getAsString(),
                                                character.get("name").getAsString(),
                                                character.getAsJsonObject("realm").get("id").getAsString(),
                                                "0",
                                                response.headers().getDate("Last-Modified").getTime() +""
                                        }
                                );
                                // Update in old DB
                                BlizzardUpdate.dbConnect.update(
                                        CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                        new String[]{"id"},
                                        new String[]{character.get("id").getAsString()},
                                        "internal_id=?",
                                        new String[]{internalId+""}
                                );
                                System.out.println("OK! ("+ charName +"/"+ realmSlug +") ["+ response.body().get("id").getAsString() +"]");
                            } catch (SQLException | DataException e) {
                                Logs.infoLog(CharacterMember.class, "FAIL - old character insert new or update old "+ e);
                            }
                        } else {

                            if (response.code() == 404) {
                                try {
                                    BlizzardUpdate.dbConnect.update(
                                            CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                            new String[]{"isDelete"},
                                            new String[]{"1"},
                                            "internal_id=?",
                                            new String[]{internalId+""}
                                    );
                                } catch (DataException | SQLException e) {
                                    Logs.infoLog(CharacterProfileAPI.class, "FAIL - old character profile update "+ e);
                                }
                            }
                            Logs.infoLog(CharacterProfileAPI.class, "ERROR - old character profile "+ response.code() +" -- ("+ charName +"/"+ realmSlug +")");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Logs.infoLog(CharacterProfileAPI.class, "FAIL - old character profile "+ throwable);
                    }
                });
            }


        } catch (DataException | SQLException e) {
            Logs.infoLog(CharacterProfileAPI.class, "FAIL - to get old characeter profiles "+ e);
        }

    }

    //=================
    // SAVE INTERNAL INFO
    //=================
    // Get small info, like info obtain for guild roster or mythic plus members
    private void saveMinimalInfo(JsonObject info, long lastModified, boolean isInDb) {

        try {
            if (isInDb) { // Update
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"last_modified"},
                        new String[]{lastModified +""},
                        "id=?",
                        new String[]{info.get("id").getAsString()}
                );
            } else { // Insert
                BlizzardUpdate.dbConnect.insert(
                        CharacterMember.TABLE_NAME,
                        CharacterMember.TABLE_KEY,
                        new String[]{
                                "id",
                                "name",
                                "realm_id",
                                "last_modified",
                                "is_valid"
                        },
                        new String[]{
                                info.get("id").getAsString(),
                                info.get("name").getAsString(),
                                info.getAsJsonObject("realm").get("id").getAsString(),
                                lastModified +"",
                                "0"
                        }
                );
            }

            Logs.infoLog(CharacterProfileAPI.class, "OK - Minimal info from character "+ info.get("id"));

        } catch (SQLException | DataException e) {
            Logs.infoLog(CharacterProfileAPI.class, "FAIL - To save miniaml character info "+ info.get("id"));
        }
    }

    // Get ALL player information, is when call the character summary.
    private void saveFullInfo(JsonObject info) {

        try {
            boolean exist = false;
            JsonArray charInfo_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"id"},
                    "id=?",
                    new String[]{info.get("id").getAsString()}
            );
            if (charInfo_db.size() > 0) {
                exist = true;
            }

            // Prepare value
            List<Object> colums = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            colums.add("character_class");
            values.add(info.getAsJsonObject("character_class").get("id").getAsString());
            BlizzardUpdate.shared.playableClassAPI.classDetail(info.getAsJsonObject("character_class"));

            colums.add("race_id");
            values.add(info.getAsJsonObject("race").get("id").getAsString());
            BlizzardUpdate.shared.playableRaceAPI.raceDetail(info.getAsJsonObject("race"));

            colums.add("gender_type");
            values.add(info.getAsJsonObject("gender").get("type").getAsString());
            BlizzardUpdate.shared.staticInformationAPI.gender(info.getAsJsonObject("gender"));

            colums.add("level");
            values.add(info.get("level").getAsString());
            colums.add("achievement_points");
            values.add(info.get("achievement_points").getAsString());

            colums.add("faction_type");
            values.add(info.getAsJsonObject("faction").get("type").getAsString());
            BlizzardUpdate.shared.staticInformationAPI.faction(info.getAsJsonObject("faction"));

            if (info.has("guild")) {
                colums.add("guild_id");
                values.add(info.getAsJsonObject("guild").get("id").getAsString());
                BlizzardUpdate.shared.guildAPI.info(info.getAsJsonObject("guild"));
            }

            colums.add("last_login");
            values.add(info.get("last_login_timestamp").getAsString());

            colums.add("average_item_level");
            values.add(info.get("average_item_level").getAsString());
            colums.add("equipped_item_level");
            values.add(info.get("equipped_item_level").getAsString());


            if (exist) { // Update
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.CHARACTER_INFO_TABLE_NAME,
                        colums,
                        values,
                        "id=?",
                        new String[]{info.get("id").getAsString()}
                );
            } else { // Insert
                colums.add("id");
                values.add(info.get("id").getAsString());
                BlizzardUpdate.dbConnect.insert(
                        CharacterMember.CHARACTER_INFO_TABLE_NAME,
                        CharacterMember.TABLE_KEY,
                        colums,
                        values
                );
            }

            Logs.infoLog(CharacterProfileAPI.class, "OK - Character info from character "+ info.get("id"));

        } catch (SQLException | DataException e) {
            Logs.infoLog(CharacterProfileAPI.class, "FAIL - To save full character info "+ info.get("id"));
        }
    }

    // Get a reference ("href") and process from there.
    private void saveCharacterSpec(int id, JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.get("href").getAsString();

        long lastUpdate = 0L;
        try {
            JsonArray last_spec_change_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"specializations_last_modified"},
                    "id=?",
                    new String[]{id+""}
            );
            if (last_spec_change_db.size() > 0) {
                lastUpdate = last_spec_change_db.get(0).getAsJsonObject().get("specializations_last_modified").getAsLong();
            }
        } catch (DataException | SQLException e) {
            Logs.infoLog(CharacterProfileAPI.class, "Fail to get old info from character specialization "+ id);
        }

        Call<JsonObject> call = apiCalls.freeUrl(
                urlHref,
                BlizzardUpdate.shared.accessToken.getAuthorization(),
                BlizzardUpdate.parseDateFormat(lastUpdate)
        );

        try {

            Response<JsonObject> response = call.execute();

            if (response.isSuccessful()) {

                JsonArray specs = response.body().getAsJsonArray("specializations");
                JsonObject activeSpec = response.body().getAsJsonObject("active_specialization");

                // Save all specs:



                // SAVE LAST UPDATE FROM SPEC!~
            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(CharacterProfileAPI.class, "NOT Modified Character specialization "+ id);
                } else {
                    Logs.infoLog(CharacterProfileAPI.class, "ERROR - Character specialization "+ id +" - "+ response.code());
                }
            }
        } catch (IOException e) {
            Logs.infoLog(CharacterProfileAPI.class, "FAIL - to get character specialization "+ e);
        }
    }

}
