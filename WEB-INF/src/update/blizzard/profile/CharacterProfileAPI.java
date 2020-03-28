package com.blizzardPanel.update.blizzard.profile;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Realm;
import com.blizzardPanel.gameObject.characters.*;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeonRun;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.blizzardPanel.update.blizzard.gameData.AchievementAPI;
import com.blizzardPanel.update.blizzard.gameData.PlayableClassAPI;
import com.blizzardPanel.viewController.MythicPlusControl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
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
        boolean isInDb = false;
        try {

            JsonArray lastModified_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"last_modified"},
                    CharacterMember.TABLE_KEY +"=?",
                    new String[]{charId + ""}
            );
            if (lastModified_db.size() > 0) {
                isInDb = true;
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
                    saveMinimalInfo(summary, lastModified, isInDb);

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

                    //--------------------------------------------------------------------------------------------------
                    //
                    // Save character-items
                    //
                    //--------------------------------------------------------------------------------------------------
                    saveCharacterEquipmentItems(summary.get("id").getAsInt(), summary.getAsJsonObject("equipment"));

                    //--------------------------------------------------------------------------------------------------
                    //
                    // Save character-Status
                    //
                    //--------------------------------------------------------------------------------------------------
                    saveCharacterStatistics(summary.get("id").getAsInt(), summary.getAsJsonObject("statistics"));

                    return true;
                } else {
                    if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                        Logs.infoLog(CharacterProfileAPI.class, "NOT Modified Character Summary " + charId);
                        return true;
                    } else {
                        Logs.errorLog(CharacterProfileAPI.class, "ERROR - Character Summary " + charId + " - " + response.code());
                    }
                }
            } catch(IOException e){
                Logs.fatalLog(CharacterProfileAPI.class, "FAILED - Character profile (" + realmSlug + "/" + characterName + ") - " + e);
            }
        }
        return false;
    }

    // Use to save information if character NOT EXIST
    public void smallInfo(JsonObject character) {

        try {
            // Check if exist in DB
            JsonArray chardet_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"last_modified"},
                    CharacterMember.TABLE_KEY+" = ?",
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
            values.add("0"); //false
            columns.add("last_modified");
            values.add(""+ new Date().getTime()); // now!

            if (chardet_db.size() > 0) { // Update
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        columns,
                        values,
                        CharacterMember.TABLE_KEY+" = ?",
                        new String[]{character.get("id").getAsString()}
                );
            } else { // Insert
                columns.add(CharacterMember.TABLE_KEY);
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
            Logs.fatalLog(CharacterProfileAPI.class, "FAILED - to save small character detail "+ e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // SAVE INTERNAL INFO
    //
    //------------------------------------------------------------------------------------------------------------------
    // save small if character EXIST
    private void saveMinimalInfo(JsonObject info, long lastModified, boolean isInDb) {

        try {
            if (isInDb) { // Update
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"last_modified", "is_valid"},
                        new String[]{lastModified +"", "1"},
                        CharacterMember.TABLE_KEY+"=?",
                        new String[]{info.get("id").getAsString()}
                );
            } else { // Insert
                BlizzardUpdate.dbConnect.insert(
                        CharacterMember.TABLE_NAME,
                        CharacterMember.TABLE_KEY,
                        new String[]{
                                CharacterMember.TABLE_KEY,
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
                                "1"
                        }
                );
            }

            Logs.infoLog(CharacterProfileAPI.class, "OK - Minimal info from character "+ info.get("id"));

        } catch (SQLException | DataException e) {
            Logs.fatalLog(CharacterProfileAPI.class, "FAILED - To save minimal character info "+ info.get("id") +" "+ e);
        }
    }

    // Get ALL player information, is when call the character summary.
    private void saveFullInfo(JsonObject info) {

        try {
            boolean exist = false;
            JsonArray charInfo_db = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"id"},
                    CharacterMember.TABLE_KEY+"=?",
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
                        CharacterMember.INFO_TABLE_NAME,
                        colums,
                        values,
                        CharacterMember.INFO_TABLE_KEY +"=?",
                        new String[]{info.get("id").getAsString()}
                );
            } else { // Insert
                colums.add(CharacterMember.INFO_TABLE_KEY);
                values.add(info.get("id").getAsString());
                BlizzardUpdate.dbConnect.insert(
                        CharacterMember.INFO_TABLE_NAME,
                        CharacterMember.INFO_TABLE_KEY,
                        colums,
                        values
                );
            }

            Logs.infoLog(CharacterProfileAPI.class, "OK - Character info from character "+ info.get("id"));

        } catch (SQLException | DataException e) {
            Logs.fatalLog(CharacterProfileAPI.class, "FAILED - To save full character info "+ info.get("id") +" - "+ e);
        }
    }

    // Get a reference ("href") and process from there.
    private void saveCharacterSpec(int characterId, JsonObject reference) {
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

                JsonArray specs = response.body().getAsJsonArray("specializations");
                int activeSpecId = response.body().getAsJsonObject("active_specialization").get("id").getAsInt();

                // Save all specs:
                for (JsonElement spec : specs) {
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
                    values.add(specDetail.get("id").getAsInt() == activeSpecId? "1":"0");

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
                    } else { // Insert
                        BlizzardUpdate.dbConnect.insert(
                                CharacterSpec.TABLE_NAME,
                                CharacterSpec.TABLE_KEY,
                                columns,
                                values
                        );
                    }

                    Logs.infoLog(CharacterProfileAPI.class, "OK Spec for character "+ characterId +" is added ");

                }

                // SAVE LAST UPDATE FROM CHARACTER-SPEC!~
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"specializations_last_modified"},
                        new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                        CharacterMember.TABLE_KEY +"=?",
                        new String[]{characterId+""}
                );

            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(CharacterProfileAPI.class, "NOT Modified Character specialization "+ characterId);
                } else {
                    Logs.errorLog(CharacterProfileAPI.class, "ERROR - Character specialization "+ characterId +" - "+ response.code());
                }
            }
        } catch (IOException | SQLException | DataException e) {
            Logs.fatalLog(CharacterProfileAPI.class, "FAILED - to get character specialization "+ e);
        }
    }

    // Get a equipment character info ("href")
    private void saveCharacterEquipmentItems(int characterId, JsonObject reference) {
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

                JsonArray equipments = response.body().getAsJsonArray("equipped_items");

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

                        columns.add("level");
                        values.add(equipItemDetail.getAsJsonObject("level").get("value").getAsString());

                        if (equipItemDetail.has("stats")) {
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
                        BlizzardUpdate.shared.mediaAPI.mediaDetail(equipItemDetail.getAsJsonObject("media"));

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
                        } else { // Insert
                            BlizzardUpdate.dbConnect.insert(
                                    CharacterItem.TABLE_NAME,
                                    CharacterItem.TABLE_KEY,
                                    columns,
                                    values
                            );
                        }

                        Logs.infoLog(CharacterProfileAPI.class, "OK Equip item is update "+ characterId);

                    }
                }

                // SAVE LAST UPDATE FROM CHARACTER-SPEC!~
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"equipment_last_modified"},
                        new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                        CharacterMember.TABLE_KEY +"=?",
                        new String[]{characterId+""}
                );

            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(CharacterProfileAPI.class, "NOT Modified Character equipment "+ characterId);
                } else {
                    Logs.errorLog(CharacterProfileAPI.class, "ERROR - Character equipment "+ characterId +" - "+ response.code());
                }
            }
        } catch (IOException | SQLException | DataException e) {
            Logs.fatalLog(CharacterProfileAPI.class, "FAILED - to get character equipment "+ e);
        }

    }

    // Get a statistics
    private void saveCharacterStatistics(int characterId, JsonObject reference) {
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
                //System.out.println(statistics);

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
                } else { // Insert
                    columns.add("character_id");
                    values.add(characterId+"");
                    BlizzardUpdate.dbConnect.insert(
                            CharacterStats.TABLE_NAME,
                            CharacterStats.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(CharacterProfileAPI.class, "OK Statistics is update "+ characterId);

                // SAVE LAST UPDATE FROM CHARACTER-SPEC!~
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"statistics_last_modified"},
                        new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                        CharacterMember.TABLE_KEY +"=?",
                        new String[]{characterId+""}
                );


            } else {
                if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(CharacterProfileAPI.class, "NOT Modified Character statistics "+ characterId);
                } else {
                    Logs.errorLog(CharacterProfileAPI.class, "ERROR - Character statistics "+ characterId +" - "+ response.code());
                }
            }
        } catch (IOException | SQLException | DataException e) {
            Logs.fatalLog(CharacterProfileAPI.class, "FAILED - to get character statistics "+ e);
        }

    }
}
