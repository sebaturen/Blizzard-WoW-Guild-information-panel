package com.blizzardPanel.update.blizzard.profile;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.AchievementCategory;
import com.blizzardPanel.gameObject.Realm;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.guild.Guild;
import com.blizzardPanel.gameObject.guild.Roster;
import com.blizzardPanel.gameObject.guild.achievement.GuildAchievement;
import com.blizzardPanel.update.blizzard.AccessToken;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.blizzardPanel.update.blizzard.gameData.AchievementAPI;
import com.blizzardPanel.update.blizzard.gameData.ConnectedRealmAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuildAPI extends BlizzardAPI {

    public GuildAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    public void info(JsonObject detail) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = detail.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");
        String guildId = detail.get("id").getAsString();

        try {

            // Check if guild previously exist
            JsonArray guild_db = BlizzardUpdate.dbConnect.select(
                    Guild.TABLE_NAME,
                    new String[] {"last_modified", "full_sync"},
                    Guild.TABLE_KEY +" = ?",
                    new String[]{guildId}
            );
            boolean isInDb = (guild_db.size() > 0);
            boolean fullSync = false;
            long lastModified = 0L;
            if (guild_db.size() > 0) {
                fullSync = guild_db.get(0).getAsJsonObject().get("full_sync").getAsInt() == 1;
                lastModified = guild_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            Response<JsonObject> resp = call.execute();

            if (resp.isSuccessful()) {
                JsonObject guildInfo = resp.body();

                loadInfo(guildInfo, resp.headers().getDate("Last-Modified").getTime(), isInDb, fullSync);

            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(GuildAPI.class, "NOT Modified guild detail "+ guildId);
                } else {
                    Logs.errorLog(GuildAPI.class, "ERROR - guild detail "+ guildId +" - "+ resp.code());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(GuildAPI.class, "FAILED - to get guild detail "+ e);
        }

    }

    public void info(String realm, String name) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();
        String guildSlug = name.toLowerCase();
        guildSlug = guildSlug.replace(" ", "-");

        try {

            String query = "SELECT" +
                    "   r.slug, " +
                    "   g.last_modified, " +
                    "   g.full_sync "+
                    "FROM " +
                    "   guild_info g, " +
                    "   realms r " +
                    "WHERE " +
                    "   r.`id` = g.`realm_id` " +
                    "   AND g. `name` = '"+ name +"' " +
                    "   AND r. `name` like '%"+ realm +"%';";
            JsonArray guild_db = BlizzardUpdate.dbConnect.selectQuery(query);
            boolean isInDb = (guild_db.size() > 0);
            boolean fullSync = false;
            String realmSlug = "NONE";
            long lastModified = 0L;
            if (guild_db.size() > 0) {
                lastModified = guild_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
                fullSync = guild_db.get(0).getAsJsonObject().get("full_sync").getAsInt() == 1;
                realmSlug = guild_db.get(0).getAsJsonObject().get("slug").getAsString();
            }

            // Load realm if not exist
            if (!isInDb) {
                JsonArray realm_db = BlizzardUpdate.dbConnect.selectQuery(
                        "SELECT " +
                        "   slug " +
                        "FROM " +
                        "   realms " +
                        "WHERE " +
                        "   name like '%"+ realm +"%'"
                );

                if (realm_db.size() > 0) {
                    realmSlug = realm_db.get(0).getAsJsonObject().get("slug").getAsString();
                } else {
                    BlizzardUpdate.shared.connectedRealmAPI.index();
                    realm_db = BlizzardUpdate.dbConnect.selectQuery(
                            "SELECT " +
                            "   slug " +
                            "FROM " +
                            "   realms " +
                            "WHERE " +
                            "   name like '%"+ realm +"%'"
                    );
                    if (realm_db.size() > 0) {
                        realmSlug = realm_db.get(0).getAsJsonObject().get("slug").getAsString();
                    }
                }
            }

            Call<JsonObject> call = apiCalls.guild(
                    realmSlug,
                    guildSlug,
                    "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            Response<JsonObject> resp = call.execute();

            if (resp.isSuccessful()) {
                loadInfo(resp.body(), resp.headers().getDate("Last-Modified").getTime(), isInDb, fullSync);
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(GuildAPI.class, "NOT Modified guild detail "+ guildSlug);
                } else {
                    Logs.errorLog(GuildAPI.class, "ERROR - To get guild info "+ resp.code());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(GuildAPI.class, "FAILED - Get Guild info "+ realm +"/"+ name);
        }
    }

    private void loadInfo(JsonObject detail, long lastModified, boolean isInDb, boolean fullInfo) {

        // Prepare values:
        List<Object> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        columns.add("name");
        values.add(detail.get("name").getAsString());

        columns.add("realm_id");
        values.add(detail.getAsJsonObject("realm").get("id").getAsString());
        BlizzardUpdate.shared.connectedRealmAPI.load(detail.get("realm").getAsJsonObject());

        columns.add("faction_type");
        values.add(detail.getAsJsonObject("faction").get("type").getAsString());
        BlizzardUpdate.shared.staticInformationAPI.faction(detail.getAsJsonObject("faction"));

        columns.add("achievement_points");
        values.add(detail.get("achievement_points").getAsString());

        columns.add("created_timestamp");
        values.add(detail.get("created_timestamp").getAsString());

        columns.add("member_count");
        values.add(detail.get("member_count").getAsString());

        columns.add("last_modified");
        values.add(lastModified+"");

        try {

            if (isInDb) { // Update
                BlizzardUpdate.dbConnect.update(
                        Guild.TABLE_NAME,
                        columns,
                        values,
                        Guild.TABLE_KEY+"=?",
                        new String[]{detail.get("id").getAsString()}
                );
            } else { // Insert
                columns.add(Guild.TABLE_KEY);
                values.add(detail.get("id").getAsString());
                columns.add("full_sync");
                values.add("0");

                BlizzardUpdate.dbConnect.insert(
                        Guild.TABLE_NAME,
                        Guild.TABLE_KEY,
                        columns,
                        values
                );
            }

            Logs.infoLog(GuildAPI.class, "Guild info OK "+ detail.get("id").getAsString());

            // Load more info!
            if (fullInfo) {
                String nameSlug = detail.get("name").getAsString().toLowerCase().replace(" ", "-");
                String realmSlug = detail.getAsJsonObject("realm").get("slug").getAsString();
                int guildId = detail.get("id").getAsInt();

                Logs.infoLog(GuildAPI.class, "START Full guild sync ("+ realmSlug +"/"+ nameSlug +")");

                // Achievements
                achievements(realmSlug, nameSlug, guildId);
                // Rosters
                roster(realmSlug, nameSlug, guildId);
                // Activities
                activities(realmSlug, nameSlug, guildId);
                // Rank
                //rank(realmSlug, nameSlug, guildId);
            }

        } catch (DataException | SQLException e) {
            Logs.fatalLog(GuildAPI.class, "FAILED - to load guild detail "+ e);
        }
    }

    // Update guild roster
    private void roster(String realmSlug, String nameSlug, int guildId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        try {
            // Check is category previously exist:
            JsonArray db_guild_roter = BlizzardUpdate.dbConnect.select(
                    Guild.TABLE_NAME,
                    new String[]{"roster_last_modified"},
                    Guild.TABLE_KEY +" = ?",
                    new String[]{guildId + ""}
            );
            boolean isInDb = (db_guild_roter.size() > 0);
            Long lastModified = 0L;
            if (db_guild_roter.size() > 0) {
                lastModified = db_guild_roter.get(0).getAsJsonObject().get("roster_last_modified").getAsLong();
            }

            if (!isInDb) { // Guild not exist! stop!
                Logs.fatalLog(GuildAPI.class, "FAILED - Try load roster from not exist guild! "+ guildId);
            } else { // Guild exist run all!

                Call<JsonObject> call = apiCalls.guildRoster(
                        realmSlug,
                        nameSlug,
                        "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                        BlizzardUpdate.shared.accessToken.getAuthorization(),
                        "0"
                );

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            for (JsonElement characters : response.body().getAsJsonArray("members")) {
                                // Prepare Character info
                                JsonObject member = characters.getAsJsonObject();
                                JsonObject character = member.getAsJsonObject("character");
                                int rank = member.get("rank").getAsInt();

                                // Check character status:
                                if (!BlizzardUpdate.shared.characterProfileAPI.summary(
                                        character.getAsJsonObject("realm").get("slug").getAsString(),
                                        character.get("name").getAsString()
                                )) {
                                    BlizzardUpdate.shared.characterProfileAPI.smallInfo(character);
                                }

                                try {

                                    // Save guild_roster DB
                                    // Check if exist in DB
                                    JsonArray roster_db = BlizzardUpdate.dbConnect.select(
                                            Roster.TABLE_NAME,
                                            new String[]{"add_regist"},
                                            Roster.TABLE_KEY +" = ?",
                                            new String[]{character.get("id").getAsString()}
                                    );
                                    boolean isInDb = (roster_db.size() > 0);

                                    if (isInDb) { // Update
                                        BlizzardUpdate.dbConnect.update(
                                                Roster.TABLE_NAME,
                                                new String[]{"rank"},
                                                new String[]{member.get("rank").getAsString()},
                                                Roster.TABLE_KEY+"=?",
                                                new String[]{character.get("id").getAsString()}
                                        );
                                    } else { // Insert
                                        BlizzardUpdate.dbConnect.insert(
                                                Roster.TABLE_NAME,
                                                Roster.TABLE_KEY,
                                                new String[]{
                                                        "character_id",
                                                        "guild_id",
                                                        "rank",
                                                        "add_regist"
                                                },
                                                new String[]{
                                                        character.get("id").getAsString(),
                                                        guildId+"",
                                                        member.get("rank").getAsString(),
                                                        new Date().getTime() +""
                                                }
                                        );
                                    }

                                } catch (DataException | SQLException e) {
                                    Logs.fatalLog(GuildAPI.class, "FAILED to set guild roster "+ character.get("id").getAsString() +" - "+ e);
                                }

                            }

                            // Save last roster update
                            try {
                                BlizzardUpdate.dbConnect.update(
                                        Guild.TABLE_NAME,
                                        new String[]{"roster_last_modified"},
                                        new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                                        Guild.TABLE_KEY+" = ?",
                                        new String[]{guildId+""}
                                );
                            } catch (DataException | SQLException e ){
                                Logs.fatalLog(GuildAPI.class, "FAILED - Save guild update -roster update- "+ e);
                            }

                        } else {
                            Logs.errorLog(GuildAPI.class, "ERROR - guildRoster "+ response.code() +" - ("+ call.request() +")");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Logs.fatalLog(GuildAPI.class, "FAILED - guildRoster "+ throwable);
                    }
                });

            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(GuildAPI.class, "FAILED - try load info from guild "+ guildId);
        }
    }

    // Update guild roster
    private void achievements(String realmSlug, String nameSlug, int guildId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        try {
            // Check is category previously exist:
            JsonArray db_guild_achiev = BlizzardUpdate.dbConnect.select(
                    Guild.TABLE_NAME,
                    new String[] {"achievement_last_modified"},
                    Guild.TABLE_KEY +" = ?",
                    new String[] {guildId+""}
            );
            boolean isInDb = (db_guild_achiev.size() > 0);
            Long lastModified = 0L;
            if (db_guild_achiev.size() > 0) {
                lastModified = db_guild_achiev.get(0).getAsJsonObject().get("achievement_last_modified").getAsLong();
            }

            if (!isInDb) { // Guild not exist! stop!
                Logs.fatalLog(GuildAPI.class, "FAILED - Try load achievement from not exist guild! ");
            } else { // Guild exist run all!
                Call<JsonObject> call = apiCalls.guildAchievements(
                        realmSlug,
                        nameSlug,
                        "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                        BlizzardUpdate.shared.accessToken.getAuthorization(),
                        BlizzardUpdate.parseDateFormat(lastModified)
                );

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            System.out.println("=== GuildAchievements");
                            for (JsonElement achievements : response.body().getAsJsonArray("achievements")) {
                                // First save a new achievement
                                BlizzardUpdate.shared.achievementAPI.achievementDetail(achievements.getAsJsonObject().getAsJsonObject("achievement"));
                                // Save a new guild achievement
                                try {
                                    // Prepare values:
                                    List<Object> columns = new ArrayList<>();
                                    List<Object> values = new ArrayList<>();
                                    columns.add("guild_id");
                                    values.add(guildId+"");
                                    columns.add("achievement_id");
                                    values.add(achievements.getAsJsonObject().get("id").getAsString());
                                    if (achievements.getAsJsonObject().has("completed_timestamp")) {
                                        columns.add("time_completed");
                                        values.add(achievements.getAsJsonObject().get("completed_timestamp").getAsString());
                                    }

                                    JsonArray guildAchievemet_db = BlizzardUpdate.dbConnect.select(
                                            GuildAchievement.TABLE_NAME,
                                            new String[]{"achievement_id"},
                                            "achievement_id = ?",
                                            new String[]{achievements.getAsJsonObject().get("id").getAsString()}
                                    );

                                    if (guildAchievemet_db.size() == 0) {
                                        BlizzardUpdate.dbConnect.insert(
                                                GuildAchievement.TABLE_NAME,
                                                GuildAchievement.TABLE_KEY,
                                                columns,
                                                values
                                        );
                                    }
                                } catch (DataException | SQLException e) {
                                    Logs.fatalLog(GuildAPI.class, "FAILED - Insert new guildAchievements - "+ e);
                                }
                            }
                            System.out.println("=== GuildAchievements END");

                            // Save last achievement update
                            try {
                                BlizzardUpdate.dbConnect.update(
                                        Guild.TABLE_NAME,
                                        new String[]{"achievement_last_modified"},
                                        new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                                        Guild.TABLE_KEY+" = ?",
                                        new String[]{guildId+""}
                                );
                            } catch (DataException | SQLException e ){
                                Logs.fatalLog(GuildAPI.class, "FAILED - Save guild update -last achievement update- "+ e);
                            }
                        } else {
                            if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                                Logs.infoLog(GuildAPI.class, "NOT Modified guildAchievements - "+ guildId);
                            } else {
                                Logs.errorLog(GuildAPI.class, "ERROR - profile "+ response.code() +" - ("+ call.request() +")");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Logs.fatalLog(GuildAPI.class, "FAILED - guildAchievements "+ throwable);
                    }
                });
            }

        } catch (DataException | SQLException e) {
            Logs.fatalLog(GuildAPI.class, "FAILED - to get guildAchievements list "+ e);
        }

    }

    // Update guild roster
    private void activities(String realmSlug, String nameSlug, int guildId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        try {
            // Check is category previously exist:
            JsonArray db_guild_activities = BlizzardUpdate.dbConnect.select(
                    Guild.TABLE_NAME,
                    new String[]{"activities_last_modified"},
                    Guild.TABLE_KEY +" = ?",
                    new String[]{guildId + ""}
            );
            Long lastModified = 0L;
            if (db_guild_activities.size() > 0) {
                lastModified = db_guild_activities.get(0).getAsJsonObject().get("activities_last_modified").getAsLong();
            }

            Call<JsonObject> call = apiCalls.guildActivity(
                    realmSlug,
                    nameSlug,
                    "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {

                        System.out.println("=== GuildActivity");
                        for(JsonElement news : response.body().getAsJsonArray("activities")) {
                            System.out.println("Type -- "+ news.getAsJsonObject().get("activity").getAsJsonObject().get("type").getAsString());
                        }

                    } else {
                        Logs.errorLog(GuildAPI.class, "ERROR - guildActivity "+ response.code() +" - ("+ call.request() +")");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    Logs.fatalLog(GuildAPI.class, "FAILED - guildActivity "+ throwable);
                }
            });
        } catch (SQLException | DataException e) {
            Logs.fatalLog(GuildAPI.class, "FAILED - try load info from guild "+ guildId);
        }
    }

}
