package com.blizzardPanel.update.blizzard.profile;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.characters.*;
import com.blizzardPanel.gameObject.mythicKeystones.MythicDungeonMember;
import com.blizzardPanel.gameObject.mythicKeystones.MythicDungeonRun;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterProfileAPI extends BlizzardAPI {

    public CharacterProfileAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    public void update(long characterId) {
        try {
            JsonArray character_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"name", "realm_slug", "last_modified"},
                    CharacterMember.TABLE_KEY +"=? and is_valid = 1",
                    new String[]{characterId+""}
            );

            for (JsonElement charDet : character_db) {
                JsonObject charDbDet = charDet.getAsJsonObject();

                summary(
                        charDbDet.get("realm_slug").getAsString()+"",
                        charDbDet.get("name").getAsString()+"",
                        characterId,
                        charDbDet.get("last_modified").getAsLong());
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a character in DB ["+ characterId +"] - "+ e);
        }
    }

    public long status(String realmSlug, String name) {
        long characterId = -1;

        Call<JsonObject> call = apiCalls.characterProfileStatus(
                realmSlug,
                name.toLowerCase(),
                "profile-" + GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization()
        );

        try {
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject inf = resp.body();
                if (inf.has("is_valid") && inf.get("is_valid").getAsBoolean()) {
                    characterId = inf.get("id").getAsLong();
                }
            } else {
                Logs.infoLog(this.getClass(), "Character (r:"+ realmSlug +"/u:"+ name +") NOT EXIST or is delete ["+ resp.code() +"]");
            }
        } catch (IOException e) {
            Logs.errorLog(this.getClass(), "ERROR to get a character info (r:"+ realmSlug +"/u:"+ name +") "+ e);
        }
        return characterId;
    }

    /**
     * Get from Blizzard the character info and save
     * @param realmSlug
     * @param name
     * @return internal ID (IF IS -1, the character can't save!)
     */
    public long save(String realmSlug, String name) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        // Get a internal ID if exist
        boolean isInDb = false;
        long characterId = status(realmSlug, name);
        long lastModified = 0L;

        if (characterId != -1) { // character exist in blizzard and can get a ID

            try {
                // Check if character previously exist in DB
                JsonArray characterId_db = BlizzardUpdate.dbConnect.select(
                        CharacterMember.TABLE_NAME,
                        new String[]{CharacterMember.TABLE_KEY, "last_modified"},
                        "id = ?",
                        new String[]{characterId+""}
                );
                isInDb = (characterId_db.size() > 0);
                if (isInDb) {
                    lastModified = characterId_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
                }
            } catch (SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "FAILED to get realm or old information ("+ realmSlug +"/"+ name +") ["+ characterId +"] "+ e);
            }

            // Get an ID
            Call<JsonObject> status = apiCalls.characterProfileStatus(
                    realmSlug,
                    name.toLowerCase(),
                    "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization()
            );

            try {
                Response<JsonObject> resp = status.execute();

                if (resp.isSuccessful()) {
                    JsonObject respDetail = resp.body();
                    long blizzId = respDetail.get("id").getAsLong();

                    if (isInDb) { // Update
                        BlizzardUpdate.dbConnect.update(
                                CharacterMember.TABLE_NAME,
                                new String[]{
                                        "blizzard_id",
                                        "is_valid",
                                },
                                new String[]{
                                        blizzId+"",
                                        "1"
                                },
                                "id=?",
                                new String[]{characterId+""}
                        );
                        Logs.infoLog(this.getClass(), "Character (r:"+ realmSlug +"/c:"+ name +") is small info update");
                    } else { // Insert
                        BlizzardUpdate.dbConnect.insert(
                                CharacterMember.TABLE_NAME,
                                CharacterMember.TABLE_KEY,
                                new String[]{
                                        "id",
                                        "blizzard_id",
                                        "name",
                                        "realm_slug",
                                        "is_valid"
                                },
                                new String[]{
                                        blizzId+"",
                                        blizzId+"",
                                        name,
                                        realmSlug,
                                        "1"
                                }
                        );
                        Logs.infoLog(this.getClass(), "Character (r:"+ realmSlug +"/c:"+ name +") is small info insert");
                    }

                    //------------------------------------------------------------------------------------------------------
                    // GET all information:
                    summary(realmSlug, name, characterId, lastModified);

                } else {
                    if (isInDb) { // Update IS_VALID = false
                        BlizzardUpdate.dbConnect.update(
                                CharacterMember.TABLE_NAME,
                                new String[]{"is_valid"},
                                new String[]{"0"},
                                "id=?",
                                new String[]{characterId+""}
                        );
                        Logs.infoLog(this.getClass(), "Character (r:"+ realmSlug +"/c:"+ name +") is NOT valid - "+ resp.code());
                    } else {
                        Logs.errorLog(this.getClass(), "Character (r:"+ realmSlug +"/c:"+ name +") NOT exist or is inaccessible ["+ resp.code() +"]");
                    }
                }
            } catch (IOException | SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "FAILED - to get a character information ("+ realmSlug +"/"+ name +") "+ e);
            }
        }

        return characterId;
    }

    /**
     * Try get information from blizzard and save all we have
     * @param minDetail {"name": NAME, "realm": {"slug": realmSlug, ...}, ... }
     * @return internal ID (IF IS -1, the character can't save!)
     */
    public long save(JsonObject minDetail) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String name = minDetail.get("name").getAsString();
        String realmSlug = minDetail.getAsJsonObject("realm").get("slug").getAsString();

        // Get a internal ID if exist
        try {
            // Check if character previously exist exist
            JsonArray characterId_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{CharacterMember.TABLE_KEY, "last_modified"},
                    "id = ?",
                    new String[]{minDetail.get("id").getAsString()}
            );
            if (characterId_db.size() == 0) { // Insert
                BlizzardUpdate.dbConnect.insert(
                        CharacterMember.TABLE_NAME,
                        CharacterMember.TABLE_KEY,
                        new String[]{
                                "id",
                                "name",
                                "realm_slug",
                                "is_valid",
                                "blizzard_id"
                        },
                        new String[]{
                                minDetail.get("id").getAsString(),
                                name,
                                realmSlug,
                                "0",
                                minDetail.get("id").getAsString()
                        }
                );
            }

            long saveId = save(realmSlug, name);

            // If this character is save but have an other ID, this char is changed:
            // - Delete
            // - Change faction
            // - Change Sex
            // - etc
            if (saveId != minDetail.get("id").getAsLong() && characterId_db.size() > 0) {
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"is_valid"},
                        new String[]{"0"},
                        "id=?",
                        new String[]{minDetail.get("id").getAsString()}
                );
            }

            return saveId;
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get character information ("+ realmSlug +"/"+ name +") "+ e);
        }

        return -1;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Prepare all character information to get:
     *  - info
     *  - specs
     *  - items
     *  - status
     *  if is in guild full sync:
     *  - mythic plus
     *  - achievements
     * @param realmSlug character realm slug
     * @param characterName character full name
     * @param characterId internal character id
     * @param lastModified last_modification from this character
     */
    private void summary(String realmSlug, String characterName, long characterId, long lastModified) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        // Prepare call detail
        Call<JsonObject> call = apiCalls.characterProfileSummary(
                realmSlug,
                characterName.toLowerCase(),
                "profile-" + GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization(),
                BlizzardUpdate.parseDateFormat(lastModified)
        );

        try {
            Response<JsonObject> response = call.execute();

            if (response.isSuccessful()) {

                // Update last change
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"last_modified"},
                        new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                        "id=?",
                        new String[]{characterId+""}
                );

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
                // Save character-info
                //
                //--------------------------------------------------------------------------------------------------
                saveFullInfo(summary, characterId);

                //--------------------------------------------------------------------------------------------------
                //
                // Save character-spec
                //
                //--------------------------------------------------------------------------------------------------
                saveCharacterSpec(summary.getAsJsonObject("specializations"), characterId);

                //--------------------------------------------------------------------------------------------------
                //
                // Save character-items
                //
                //--------------------------------------------------------------------------------------------------
                saveCharacterEquipmentItems(summary.getAsJsonObject("equipment"), characterId);

                //--------------------------------------------------------------------------------------------------
                //
                // Save character-Status
                //
                //--------------------------------------------------------------------------------------------------
                saveCharacterStatistics(summary.getAsJsonObject("statistics"), characterId);

                //--------------------------------------------------------------------------------------------------
                //
                // Save character-Media
                //
                //--------------------------------------------------------------------------------------------------
                saveCharacterMedia(realmSlug, characterName, characterId);

                //--------------------------------------------------------------------------------------------------
                //
                // Save info if a guild member we wont full sync
                //
                //--------------------------------------------------------------------------------------------------
                try {
                    JsonArray guild_roster = BlizzardUpdate.dbConnect.selectQuery(
                            "SELECT " +
                                    "    gr.* " +
                                    "FROM " +
                                    "    guild_roster gr, " +
                                    "    guild_info g " +
                                    "WHERE " +
                                    "    g.id = gr.guild_id " +
                                    "    AND g.full_sync is TRUE " +
                                    "    AND character_id = "+ characterId +";"
                    );
                    if (guild_roster.size() > 0) {
                        // Save character-MythicPlus
                        saveCharacterMythicPlus(summary.getAsJsonObject("mythic_keystone_profile"), characterId);
                        // Save character-achievements
                        //...?
                    }
                } catch (SQLException | DataException e) {
                    Logs.fatalLog(this.getClass(), "FAILED - to get mythicPlus or achievement for guild/not member ["+ characterId +"] - "+ e);
                }
                Logs.infoLog(this.getClass(), "OK - Character (r:"+ realmSlug +"/c:"+ characterName +") - ["+ characterId +"] save process complete");
            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Character Summary ["+ characterId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Character Summary ["+ characterId +"] - "+ response.code() +" // "+ call.request());
                }
            }
        } catch(IOException | DataException | SQLException e){
            Logs.fatalLog(this.getClass(), "FAILED - Character summary (" + realmSlug + "/" + characterName + ") - " + e);
        }
    }

    /**
     * Save a full character information (character_info)
     * @param info {Json information}
     * @param characterId internal ID
     */
    private void saveFullInfo(JsonObject info, long characterId) {

        // Prepare value
        List<Object> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        columns.add("character_class_id");
        values.add(info.getAsJsonObject("character_class").get("id").getAsString());
        BlizzardUpdate.shared.playableClassAPI.classDetail(info.getAsJsonObject("character_class"));

        columns.add("race_id");
        values.add(info.getAsJsonObject("race").get("id").getAsString());
        BlizzardUpdate.shared.playableRaceAPI.raceDetail(info.getAsJsonObject("race"));

        columns.add("gender_type");
        values.add(info.getAsJsonObject("gender").get("type").getAsString());
        BlizzardUpdate.shared.staticInformationAPI.gender(info.getAsJsonObject("gender"));

        columns.add("level");
        values.add(info.get("level").getAsString());
        columns.add("achievement_points");
        values.add(info.get("achievement_points").getAsString());

        columns.add("faction_type");
        values.add(info.getAsJsonObject("faction").get("type").getAsString());
        BlizzardUpdate.shared.staticInformationAPI.faction(info.getAsJsonObject("faction"));

        if (info.has("guild")) {
            columns.add("guild_id");
            values.add(info.getAsJsonObject("guild").get("id").getAsString());
            BlizzardUpdate.shared.guildAPI.info(info.getAsJsonObject("guild"));
        }

        columns.add("last_login");
        values.add(info.get("last_login_timestamp").getAsString());

        columns.add("average_item_level");
        values.add(info.get("average_item_level").getAsString());
        columns.add("equipped_item_level");
        values.add(info.get("equipped_item_level").getAsString());

        try {
            JsonArray isInDb = BlizzardUpdate.dbConnect.select(
                    CharacterInfo.TABLE_NAME,
                    new String[]{CharacterInfo.TABLE_KEY},
                    CharacterInfo.TABLE_KEY +"=?",
                    new String[]{characterId+""}
            );

            if (isInDb.size() > 0) { // Update
                BlizzardUpdate.dbConnect.update(
                        CharacterInfo.TABLE_NAME,
                        columns,
                        values,
                        CharacterInfo.TABLE_KEY +"=?",
                        new String[]{characterId+""}
                );
                Logs.infoLog(this.getClass(), "OK - Character Info UPDATE ["+ characterId +"]");
            } else { // Insert
                columns.add(CharacterInfo.TABLE_KEY);
                values.add(characterId+"");
                BlizzardUpdate.dbConnect.insert(
                        CharacterInfo.TABLE_NAME,
                        CharacterInfo.TABLE_KEY,
                        columns,
                        values
                );
                Logs.infoLog(this.getClass(), "OK - Character info INSERT ["+ characterId +"]");
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to UPDATE/INSERT Character info ["+ characterId +"]");
        }
    }

    /**
     * Save specialization for character (character_spec)
     * @param reference {"href": URL, ...}
     * @param characterId internal ID
     */
    private void saveCharacterSpec(JsonObject reference, long characterId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.get("href").getAsString();

        try {

            JsonArray last_spec_change_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"specializations_last_modified"},
                    CharacterMember.TABLE_KEY +"=?",
                    new String[]{characterId+""}
            );
            long lastUpdate = 0L;
            if (last_spec_change_db.size() > 0) {
                lastUpdate = last_spec_change_db.get(0).getAsJsonObject().get("specializations_last_modified").getAsLong();
            }

            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastUpdate)
            );

            Response<JsonObject> response = call.execute();

            if (response.isSuccessful()) {
                JsonObject bodyContent = response.body();
                if (bodyContent != null && bodyContent.has("specializations")) {
                    JsonArray blizzSpec = bodyContent.getAsJsonArray("specializations");
                    long activeSpecId = bodyContent.getAsJsonObject("active_specialization").get("id").getAsLong();

                    // SAVE LAST UPDATE FROM CHARACTER-SPEC!~
                    BlizzardUpdate.dbConnect.update(
                            CharacterMember.TABLE_NAME,
                            new String[]{"specializations_last_modified"},
                            new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                            CharacterMember.TABLE_KEY +"=?",
                            new String[]{characterId+""}
                    );

                    // Remove current specs...
                    BlizzardUpdate.dbConnect.delete(
                            CharacterSpec.TABLE_NAME,
                            "character_id=?",
                            new String[]{characterId+""}
                    );

                    // Save all specs:
                    for (JsonElement spec : blizzSpec) {
                        JsonObject specDetail = spec.getAsJsonObject().getAsJsonObject("specialization");
                        BlizzardUpdate.shared.playableSpecializationAPI.specializationDetail(specDetail);

                        JsonArray talents = spec.getAsJsonObject().getAsJsonArray("talents");
                        JsonArray pvpTalents = spec.getAsJsonObject().getAsJsonArray("pvp_talent_slots");

                        // Prepare Values
                        List<Object> columns = new ArrayList<>();
                        List<Object> values = new ArrayList<>();

                        columns.add("character_id");
                        values.add(characterId+"");

                        columns.add("specialization_id");
                        values.add(specDetail.get("id").getAsString());

                        columns.add("enable");
                        values.add(specDetail.get("id").getAsLong() == activeSpecId? "1":"0");

                        if (talents != null) {
                            int i = 0;
                            for(JsonElement talent : talents) {
                                JsonObject talentDetail = talent.getAsJsonObject();

                                columns.add("tier_"+ i);
                                values.add(talentDetail.getAsJsonObject("spell_tooltip").getAsJsonObject("spell").get("id").getAsString());
                                BlizzardUpdate.shared.spellAPI.spellDetail(talentDetail.getAsJsonObject("spell_tooltip").getAsJsonObject("spell"));
                                i++;

                            }
                        }

                        // Check is character spec previously exist:
                        JsonArray spec_db = BlizzardUpdate.dbConnect.select(
                                CharacterSpec.TABLE_NAME,
                                new String[] {CharacterSpec.TABLE_KEY},
                                "character_id = ? AND specialization_id = ?",
                                new String[] {characterId+"", specDetail.get("id").getAsString()}
                        );
                        boolean isInDb = (spec_db.size() > 0);

                        if (isInDb) { // Update
                            String specInternalId = spec_db.get(0).getAsJsonObject().get(CharacterSpec.TABLE_KEY).getAsString();
                            BlizzardUpdate.dbConnect.update(
                                    CharacterSpec.TABLE_NAME,
                                    columns,
                                    values,
                                    CharacterSpec.TABLE_KEY+"=?",
                                    new String[]{specInternalId}
                            );
                            Logs.infoLog(this.getClass(), "OK Spec for character ["+ characterId +"] is UPDATE ");
                        } else { // Insert
                            BlizzardUpdate.dbConnect.insert(
                                    CharacterSpec.TABLE_NAME,
                                    CharacterSpec.TABLE_KEY,
                                    columns,
                                    values
                            );
                            Logs.infoLog(this.getClass(), "OK Spec for character ["+ characterId +"] is INSERT ");
                        }
                    }
                } else {
                    Logs.errorLog(this.getClass(), "Error to get spec for character ["+ characterId +"] is empty!");
                }
            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Character specialization ["+ characterId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Character specialization ["+ characterId +"] - "+ response.code() +" // "+ call.request());
                }
            }
        } catch (IOException | SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get character specialization ["+ characterId +"] "+ e);
        }
    }

    /**
     * Save equipment for character (character_items)
     * @param reference {"href": URL, ...}
     * @param characterId internal ID
     */
    private void saveCharacterEquipmentItems(JsonObject reference, long characterId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.get("href").getAsString();
        try {

            JsonArray last_equipment_change_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"equipment_last_modified"},
                    CharacterMember.TABLE_KEY +"=?",
                    new String[]{characterId+""}
            );
            long lastUpdate = 0L;
            if (last_equipment_change_db.size() > 0) {
                lastUpdate = last_equipment_change_db.get(0).getAsJsonObject().get("equipment_last_modified").getAsLong();
            }

            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastUpdate)
            );

            Response<JsonObject> response = call.execute();
            if (response.isSuccessful()) {
                JsonObject bodyContent = response.body();
                if (bodyContent != null && bodyContent.has("equipped_items")) {
                    JsonArray equipments = bodyContent.getAsJsonArray("equipped_items");

                    // SAVE LAST UPDATE FROM CHARACTER-SPEC!~
                    BlizzardUpdate.dbConnect.update(
                            CharacterMember.TABLE_NAME,
                            new String[]{"equipment_last_modified"},
                            new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                            CharacterMember.TABLE_KEY +"=?",
                            new String[]{characterId+""}
                    );

                    // Save all items:
                    if (equipments != null) {
                        for (JsonElement equipItem : equipments) {
                            JsonObject equipItemDetail = equipItem.getAsJsonObject();


                            // Prepare Values
                            List<Object> columns = new ArrayList<>();
                            List<Object> values = new ArrayList<>();
                            columns.add("character_id");
                            values.add(characterId+"");

                            columns.add("slot_type");
                            values.add(equipItemDetail.getAsJsonObject("slot").get("type").getAsString());
                            BlizzardUpdate.shared.staticInformationAPI.slot(equipItemDetail.getAsJsonObject("slot"));

                            columns.add("item_id");
                            values.add(equipItemDetail.getAsJsonObject("item").get("id").getAsString());
                            BlizzardUpdate.shared.itemAPI.itemDetail(equipItemDetail.getAsJsonObject("item"));

                            columns.add("quality_type");
                            values.add(equipItemDetail.getAsJsonObject("quality").get("type").getAsString());
                            BlizzardUpdate.shared.staticInformationAPI.quality(equipItemDetail.getAsJsonObject("quality"));

                            if (equipItemDetail.has("level")) {
                                columns.add("level");
                                values.add(equipItemDetail.getAsJsonObject("level").get("value").getAsString());
                            }

                            if (equipItemDetail.has("stats") && equipItemDetail.get("stats").toString().length() > 2) {
                                columns.add("stats");
                                values.add(equipItemDetail.getAsJsonArray("stats").toString());
                            }

                            if (equipItemDetail.has("armor")) {
                                columns.add("armor");
                                values.add(equipItemDetail.getAsJsonObject("armor").get("value").getAsString());
                            }

                            if (equipItemDetail.has("azerite_details")) {
                                if (equipItemDetail.getAsJsonObject("azerite_details").has("level")) {
                                    columns.add("azerite_level");
                                    values.add(equipItemDetail.getAsJsonObject("azerite_details").getAsJsonObject("level").get("value").getAsString());
                                }
                            }

                            columns.add("media_id");
                            values.add(equipItemDetail.getAsJsonObject("media").get("id").getAsString());
                            BlizzardUpdate.shared.mediaAPI.mediaDetail(Media.type.ITEM, equipItemDetail.getAsJsonObject("media"));

                            // Check is equipment item previously exist:
                            JsonArray equipItem_db = BlizzardUpdate.dbConnect.select(
                                    CharacterItem.TABLE_NAME,
                                    new String[] {CharacterItem.TABLE_KEY},
                                    "character_id = ? AND slot_type = ?",
                                    new String[] {characterId+"", equipItemDetail.getAsJsonObject("slot").get("type").getAsString()}
                            );
                            boolean isInDb = (equipItem_db.size() > 0);

                            if (isInDb) { // Update
                                String equipInternalId = equipItem_db.get(0).getAsJsonObject().get(CharacterItem.TABLE_KEY).getAsString();
                                BlizzardUpdate.dbConnect.update(
                                        CharacterItem.TABLE_NAME,
                                        columns,
                                        values,
                                        CharacterItem.TABLE_KEY+"=?",
                                        new String[]{equipInternalId}
                                );
                                Logs.infoLog(this.getClass(), "OK Equip item ["+ characterId +"] is UPDATE");
                            } else { // Insert
                                BlizzardUpdate.dbConnect.insert(
                                        CharacterItem.TABLE_NAME,
                                        CharacterItem.TABLE_KEY,
                                        columns,
                                        values
                                );
                                Logs.infoLog(this.getClass(), "OK Equip item ["+ characterId +"] is INSERT");
                            }
                        }
                    } else {
                        Logs.errorLog(this.getClass(), "Error to get equipment item for character ["+ characterId +"] is empty!");
                    }
                } else {
                    Logs.errorLog(this.getClass(), "Error to get items for character ["+ characterId +"] is empty!");
                }
            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Character equipment ["+ characterId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Character equipment ["+ characterId +"] - "+ response.code() +" // "+ call.request());
                }
            }
        } catch (IOException | SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get character equipment ["+ characterId +"] "+ e);
        }
    }

    /**
     * Save a character staticstics (character_stats)
     * @param reference {"href": URL, ...}
     * @param characterId internal ID
     */
    private void saveCharacterStatistics(JsonObject reference, long characterId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.get("href").getAsString();
        try {

            JsonArray last_statistics_change_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"statistics_last_modified"},
                    CharacterMember.TABLE_KEY +"=?",
                    new String[]{characterId+""}
            );
            long lastUpdate = 0L;
            if (last_statistics_change_db.size() > 0) {
                lastUpdate = last_statistics_change_db.get(0).getAsJsonObject().get("statistics_last_modified").getAsLong();
            }

            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastUpdate)
            );

            Response<JsonObject> response = call.execute();
            if (response.isSuccessful()) {
                JsonObject statistics = response.body();

                // SAVE LAST UPDATE FROM CHARACTER-SPEC!~
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"statistics_last_modified"},
                        new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                        CharacterMember.TABLE_KEY +"=?",
                        new String[]{characterId+""}
                );

                // Prepare Values
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("health");
                values.add(statistics.get("health").getAsString());

                columns.add("power");
                values.add(statistics.get("power").getAsString());

                columns.add("power_type");
                values.add("POWER_"+ statistics.getAsJsonObject("power_type").get("id").getAsString());
                BlizzardUpdate.shared.staticInformationAPI.power(statistics.getAsJsonObject("power_type"));

                columns.add("speed");
                values.add(statistics.getAsJsonObject("speed").toString());

                columns.add("strength");
                values.add(statistics.getAsJsonObject("strength").toString());

                columns.add("agility");
                values.add(statistics.getAsJsonObject("agility").toString());

                columns.add("intellect");
                values.add(statistics.getAsJsonObject("intellect").toString());

                columns.add("stamina");
                values.add(statistics.getAsJsonObject("stamina").toString());

                columns.add("melee");
                JsonObject melee = new JsonObject();
                melee.add("cirt", statistics.getAsJsonObject("melee_crit"));
                melee.add("haste", statistics.getAsJsonObject("melee_haste"));
                values.add(melee.toString());

                columns.add("mastery");
                values.add(statistics.getAsJsonObject("mastery").toString());

                columns.add("bonus_armor");
                values.add(statistics.get("bonus_armor").getAsString());

                columns.add("lifesteal");
                values.add(statistics.getAsJsonObject("lifesteal").toString());

                columns.add("versatility");
                JsonObject versa = new JsonObject();
                versa.addProperty("base", statistics.get("versatility").getAsString());
                versa.addProperty("damage_done_bonus", statistics.get("versatility_damage_done_bonus").getAsString());
                versa.addProperty("healing_done_bonus", statistics.get("versatility_healing_done_bonus").getAsString());
                versa.addProperty("damage_taken_bonus", statistics.get("versatility_damage_taken_bonus").getAsString());
                values.add(versa);

                columns.add("avoidance");
                values.add(statistics.getAsJsonObject("avoidance").toString());

                columns.add("attack_power");
                values.add(statistics.get("attack_power").getAsString());

                columns.add("hand");
                JsonObject hand = new JsonObject();
                JsonObject mainHand = new JsonObject();
                mainHand.addProperty("damage_min", statistics.get("main_hand_damage_min").getAsString());
                mainHand.addProperty("damage_max", statistics.get("main_hand_damage_max").getAsString());
                mainHand.addProperty("speed", statistics.get("main_hand_speed").getAsString());
                mainHand.addProperty("dps", statistics.get("main_hand_dps").getAsString());
                JsonObject offHand = new JsonObject();
                mainHand.addProperty("damage_min", statistics.get("off_hand_damage_min").getAsString());
                mainHand.addProperty("damage_max", statistics.get("off_hand_damage_max").getAsString());
                mainHand.addProperty("speed", statistics.get("off_hand_speed").getAsString());
                mainHand.addProperty("dps", statistics.get("off_hand_dps").getAsString());
                hand.add("main", mainHand);
                hand.add("off", offHand);
                values.add(hand.toString());

                columns.add("spell");
                JsonObject spell = new JsonObject();
                spell.addProperty("power", statistics.get("spell_power").getAsString());
                spell.addProperty("penetration", statistics.get("spell_penetration").getAsString());
                spell.add("haste", statistics.getAsJsonObject("spell_haste"));
                spell.add("crit", statistics.get("spell_crit").getAsJsonObject());
                values.add(spell.toString());

                columns.add("mana");
                JsonObject mana = new JsonObject();
                mana.addProperty("regen", statistics.get("mana_regen").getAsString());
                mana.addProperty("regen_combat", statistics.get("mana_regen_combat").getAsString());
                values.add(mana);

                columns.add("armor");
                values.add(statistics.getAsJsonObject("armor").toString());

                columns.add("dodge");
                values.add(statistics.getAsJsonObject("dodge").toString());

                columns.add("parry");
                values.add(statistics.getAsJsonObject("parry").toString());

                columns.add("block");
                values.add(statistics.getAsJsonObject("block").toString());

                columns.add("ranged");
                JsonObject range = new JsonObject();
                range.add("crit", statistics.getAsJsonObject("ranged_crit"));
                range.add("haste", statistics.getAsJsonObject("ranged_haste"));
                values.add(range.toString());

                if (statistics.has("corruption")) {
                    columns.add("corruption");
                    values.add(statistics.getAsJsonObject("corruption").toString());
                }

                // Check is statistics previously exist:
                JsonArray statistics_db = BlizzardUpdate.dbConnect.select(
                        CharacterStats.TABLE_NAME,
                        new String[] {CharacterStats.TABLE_KEY},
                        "character_id = ?",
                        new String[] {characterId+""}
                );
                boolean isInDb = (statistics_db.size() > 0);

                if (isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            CharacterStats.TABLE_NAME,
                            columns,
                            values,
                            CharacterStats.TABLE_KEY +"=?",
                            new String[]{characterId+""}
                    );
                    Logs.infoLog(this.getClass(), "OK Character Statistics  ["+ characterId +"] UPDATE");
                } else { // Insert
                    columns.add("character_id");
                    values.add(characterId+"");
                    BlizzardUpdate.dbConnect.insert(
                            CharacterStats.TABLE_NAME,
                            CharacterStats.TABLE_KEY,
                            columns,
                            values
                    );
                    Logs.infoLog(this.getClass(), "OK Character Statistics  ["+ characterId +"] INSERT");
                }
            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Character statistics ["+ characterId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Character statistics ["+ characterId +"] - "+ response.code() +" // "+ call.request());
                }
            }
        } catch (IOException | SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get character statistics ["+ characterId +"] "+ e);
        }

    }

    /**
     * Save all character mythicPlus runs information (keystone_dungeon_run / keystone_dungeon_run_members)
     * @param reference {"href": URL, ...}
     * @param characterId internal ID
     */
    private void saveCharacterMythicPlus(JsonObject reference, long characterId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.get("href").getAsString();
        try {
            JsonArray last_mythicPlus_change_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"mythic_plus_last_modified"},
                    CharacterMember.TABLE_KEY +"=?",
                    new String[]{characterId+""}
            );
            long lastUpdate = 0L;
            if (last_mythicPlus_change_db.size() > 0) {
                lastUpdate = last_mythicPlus_change_db.get(0).getAsJsonObject().get("mythic_plus_last_modified").getAsLong();
            }

            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastUpdate)
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject currentPeriod = response.body().getAsJsonObject("current_period");
                        JsonArray seasons = response.body().getAsJsonArray("seasons");

                        // SAVE LAST UPDATE FROM CHARACTER-mythic plus!~
                        try {
                            BlizzardUpdate.dbConnect.update(
                                    CharacterMember.TABLE_NAME,
                                    new String[]{"mythic_plus_last_modified"},
                                    new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                                    CharacterMember.TABLE_KEY +"=?",
                                    new String[]{characterId+""}
                            );

                            //-------------
                            // Current Period
                            if (currentPeriod.has("best_runs")) {
                                saveCharacterBestMythicPlus(currentPeriod.getAsJsonArray("best_runs"), characterId);
                            }

                            //------------
                            // Other seasons:
                            if (seasons != null) {
                                for(JsonElement season : seasons) {
                                    saveCharacterSeasonMythicPlus(season.getAsJsonObject(), characterId);
                                }
                            }
                        } catch (DataException | SQLException e) {
                            Logs.fatalLog(this.getClass(), "FAILED - to save mythic plus last modified ["+ characterId +"]");
                        }

                    } else {
                        if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                            Logs.infoLog(this.getClass(), "NOT Modified Character Mythic Plus ["+ characterId +"]");
                        } else {
                            Logs.errorLog(this.getClass(), "ERROR - Mythic Plus ["+ characterId +"] - "+ response.code() +" // "+ call.request());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    Logs.fatalLog(this.getClass(), "FAILED - to get mythic plus ["+ characterId +"] "+ throwable);
                }
            });

        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get mythic plus ["+ characterId +"] "+ e);
        }
    }

    /**
     * Save best run from character member detail information
     * @param bestRuns {"completed_timestamp": true/false, "duration": xxxx, "keystone_level" ...}
     */
    private void saveCharacterBestMythicPlus(JsonArray bestRuns, long characterId) {
        for(JsonElement runs : bestRuns) {
            JsonObject runsDetail = runs.getAsJsonObject();

            // Prepare Values
            List<Object> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            columns.add("completed_timestamp");
            values.add(runsDetail.get("completed_timestamp").getAsString());

            columns.add("duration");
            values.add(runsDetail.get("duration").getAsString());

            columns.add("keystone_level");
            values.add(runsDetail.get("keystone_level").getAsString());

            columns.add("keystone_dungeon_id");
            values.add(runsDetail.getAsJsonObject("dungeon").get("id").getAsString());
            BlizzardUpdate.shared.mythicKeystoneDungeonAPI.dungDetail(runsDetail.getAsJsonObject("dungeon"));

            columns.add("is_completed_within_time");
            values.add(runsDetail.get("is_completed_within_time").getAsBoolean()? "1":"0");

            columns.add("key_affixes");
            JsonObject affixes = new JsonObject();
            int i = 0;
            for (JsonElement affix : runsDetail.getAsJsonArray("keystone_affixes")) {
                affixes.addProperty(i+"", affix.getAsJsonObject().get("id").getAsString());
                BlizzardUpdate.shared.mythicKeystoneDungeonAPI.affixesDetail(affix.getAsJsonObject());;
                i++;
            }
            values.add(affixes.toString());

            // Save KeyDungeonRun
            try {
                // Check is character spec previously exist:
                JsonArray keyDunRun_db = BlizzardUpdate.dbConnect.select(
                        MythicDungeonRun.TABLE_NAME,
                        new String[] {MythicDungeonRun.TABLE_KEY},
                        "completed_timestamp = ? AND duration = ? AND keystone_level = ? AND keystone_dungeon_id = ? AND is_completed_within_time = ?",
                        new String[] {
                                runsDetail.get("completed_timestamp").getAsString(),
                                runsDetail.get("duration").getAsString(),
                                runsDetail.get("keystone_level").getAsString(),
                                runsDetail.getAsJsonObject("dungeon").get("id").getAsString(),
                                runsDetail.get("is_completed_within_time").getAsBoolean()? "1":"0"
                        }
                );

                if (keyDunRun_db.size() == 0) { // Insert
                    String keyDunRunId = BlizzardUpdate.dbConnect.insert(
                            MythicDungeonRun.TABLE_NAME,
                            MythicDungeonRun.TABLE_KEY,
                            columns,
                            values
                    );

                    // Insert members
                    // Check if exist
                    for (JsonElement member : runsDetail.getAsJsonArray("members")) {
                        JsonObject memberDetail = member.getAsJsonObject();

                        // Save member
                        long memberId = save(memberDetail.getAsJsonObject("character"));
                        if (memberId != -1) { // member is successful save in DB

                            // Prepare Values
                            List<Object> columnsMembers = new ArrayList<>();
                            List<Object> valuesMembers = new ArrayList<>();
                            columnsMembers.add("keystone_dungeon_run_id");
                            valuesMembers.add(keyDunRunId);

                            columnsMembers.add("character_id");
                            valuesMembers.add(memberId);

                            columnsMembers.add("character_spec_id");
                            valuesMembers.add(memberDetail.getAsJsonObject("specialization").get("id").getAsString());

                            columnsMembers.add("character_item_level");
                            valuesMembers.add(memberDetail.get("equipped_item_level").getAsString());

                            // Add member
                            try {
                                BlizzardUpdate.dbConnect.insert(
                                        MythicDungeonMember.TABLE_NAME,
                                        MythicDungeonMember.TABLE_KEY,
                                        columnsMembers,
                                        valuesMembers
                                );
                                Logs.infoLog(this.getClass(), "OK - Member is INSERT for keyDungeon ("+ keyDunRunId +") - ["+ memberId +"]");
                            } catch (DataException | SQLException e) {
                                Logs.fatalLog(this.getClass(), "FAILED - to save mythic plus run member ["+ characterId +"] ["+ memberId +"] --> "+ e);
                            }
                        }

                    }

                    Logs.infoLog(this.getClass(), "OK - KeyDungeon is added ["+ characterId +"] - "+ keyDunRunId);
                }
            } catch (SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "FAILED - to save mythic plus runs ["+ characterId +"] - "+ e);
            }

        }

    }

    /**
     * Save all season character have an information
     * @param reference {"key": {"href": URL}, "id": seasonID, ....}
     * @param characterId internal ID
     */
    private void saveCharacterSeasonMythicPlus(JsonObject reference, long characterId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        String seasonId = reference.get("id").getAsString();

        try {
            // Check is category previously exist:
            JsonArray seasonMP_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"mythic_plus_seasons_last_modified"},
                    CharacterMember.TABLE_KEY +" = ?",
                    new String[]{characterId+""}
            );
            long lastModified = 0L;
            JsonObject lastSeasonModified = new JsonObject();
            if (seasonMP_db.size() > 0) {
                JsonObject db_modif = seasonMP_db.get(0).getAsJsonObject();
                lastSeasonModified = db_modif.getAsJsonObject("mythic_plus_seasons_last_modified");
                if (lastSeasonModified != null && !lastSeasonModified.isJsonNull() && lastSeasonModified.has(seasonId)) {
                    lastModified = lastSeasonModified.get(seasonId).getAsLong();
                } else {
                    lastSeasonModified = new JsonObject();
                }
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
                JsonObject bestRuns = resp.body();

                // Save last modified season
                // Update last modified
                String idPeriod = bestRuns.getAsJsonObject("season").get("id").getAsString();
                if (lastSeasonModified != null && lastSeasonModified.has(idPeriod)) {
                    lastSeasonModified.remove(idPeriod);
                }
                lastSeasonModified.addProperty(idPeriod, resp.headers().getDate("Last-Modified").getTime());
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"mythic_plus_seasons_last_modified"},
                        new String[]{lastSeasonModified.toString()},
                        CharacterMember.TABLE_KEY +"=?",
                        new String[]{characterId +""}
                );

                if (bestRuns.has("best_runs")) {
                    saveCharacterBestMythicPlus(bestRuns.getAsJsonArray("best_runs"), characterId);
                    Logs.infoLog(this.getClass(), "OK - Run MythicPlusSeason Update ["+ characterId +"]");
                }

            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Season Mythic Plus ["+ characterId +"]");
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - season Mythic Plus ["+ characterId +"] - "+ resp.code() +" // "+ call.request());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get season Mythic Plus ["+ characterId +"] "+ e);
        }

    }

    /**
     * Save all image relative to this character
     * @param realmSlug Slug realm
     * @param characterName Name of character
     * @param characterId internal character ID
     */
    private void saveCharacterMedia(String realmSlug, String characterName, long characterId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();
        characterName = characterName.toLowerCase();

        try {

            JsonArray last_media_change_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"media_last_modified"},
                    CharacterMember.TABLE_KEY +"=?",
                    new String[]{characterId+""}
            );
            long lastUpdate = 0L;
            if (last_media_change_db.size() > 0) {
                lastUpdate = last_media_change_db.get(0).getAsJsonObject().get("media_last_modified").getAsLong();
            }

            Call<JsonObject> call = apiCalls.characterMedia(
                    realmSlug,
                    characterName,
                    "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastUpdate)
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JsonObject charMedia = response.body();

                            // SAVE LAST UPDATE FROM CHARACTER-MEDIA!~
                            BlizzardUpdate.dbConnect.update(
                                    CharacterMember.TABLE_NAME,
                                    new String[]{"media_last_modified"},
                                    new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                                    CharacterMember.TABLE_KEY +"=?",
                                    new String[]{characterId+""}
                            );

                            // Prepare value
                            List<Object> columns = new ArrayList<>();
                            List<Object> values = new ArrayList<>();
                            columns.add("avatar_url");
                            values.add(charMedia.get("avatar_url").getAsString());
                            columns.add("bust_url");
                            values.add(charMedia.get("bust_url").getAsString());
                            columns.add("render_url");
                            values.add(charMedia.get("render_url").getAsString());

                            JsonArray media_db = BlizzardUpdate.dbConnect.select(
                                    CharacterMedia.TABLE_NAME,
                                    new String[] {CharacterMedia.TABLE_KEY},
                                    "character_id = ?",
                                    new String[] {characterId+""}
                            );
                            boolean isInDb = (media_db.size() > 0);

                            if (isInDb) { // Update
                                BlizzardUpdate.dbConnect.update(
                                        CharacterMedia.TABLE_NAME,
                                        columns,
                                        values,
                                        CharacterMedia.TABLE_KEY+"=?",
                                        new String[]{characterId+""}
                                );
                                Logs.infoLog(this.getClass(), "OK - Character media UPDATE ["+ characterId +"]");
                            } else { // Insert
                                columns.add("character_id");
                                values.add(characterId+"");
                                BlizzardUpdate.dbConnect.insert(
                                        CharacterMedia.TABLE_NAME,
                                        CharacterMedia.TABLE_KEY,
                                        columns,
                                        values
                                );
                                Logs.infoLog(this.getClass(), "OK - Character media INSERT ["+ characterId +"]");
                            }
                        } catch (SQLException | DataException e) {
                            Logs.fatalLog(this.getClass(), "FAILED - to save character media ["+ characterId +"] "+ e);
                        }
                    } else {
                        if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                            Logs.infoLog(this.getClass(), "NOT Modified Character media ["+ characterId +"]");
                        } else {
                            Logs.errorLog(this.getClass(), "ERROR - Character media ["+ characterId +"] - "+ response.code() +" // "+ call.request());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Logs.fatalLog(this.getClass(), "FAILED - to get character media ["+ characterId +"] "+ t);
                }
            });
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get character media ["+ characterId +"] "+ e);
        }

    }
}
