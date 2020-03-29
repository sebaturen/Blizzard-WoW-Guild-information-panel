package com.blizzardPanel.update.blizzard.profile;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.guild.Guild;
import com.blizzardPanel.gameObject.guild.Activity;
import com.blizzardPanel.gameObject.guild.Rank;
import com.blizzardPanel.gameObject.guild.Roster;
import com.blizzardPanel.gameObject.guild.achievement.GuildAchievement;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.managers.GuildManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
                fullSync = guild_db.get(0).getAsJsonObject().get("full_sync").getAsLong() == 1;
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
                    Logs.infoLog(this.getClass(), "NOT Modified guild detail "+ guildId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - guild detail "+ guildId +" - "+ resp.code() +" // "+ call.request());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get guild detail "+ e);
        }

    }

    public void info(String realm, String name) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();
        String guildSlug = name.replace(" ", "-").toLowerCase();

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
                fullSync = guild_db.get(0).getAsJsonObject().get("full_sync").getAsLong() == 1;
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
                    Logs.infoLog(this.getClass(), "NOT Modified guild detail "+ guildSlug);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - To get guild info "+ resp.code() +" // "+ call.request());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - Get Guild info "+ realm +"/"+ name);
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

            Logs.infoLog(this.getClass(), "Guild info OK "+ detail.get("id").getAsString());

            // Load more info!
            if (fullInfo) {

                String nameSlug = detail.get("name").getAsString().toLowerCase().replace(" ", "-");
                String realmSlug = detail.getAsJsonObject("realm").get("slug").getAsString();
                long guildId = detail.get("id").getAsLong();

                Logs.infoLog(this.getClass(), "START Full guild sync ("+ realmSlug +"/"+ nameSlug +")");

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
            Logs.fatalLog(this.getClass(), "FAILED - to load guild detail "+ e);
        }
    }

    // Update guild roster
    private void roster(String realmSlug, String nameSlug, long guildId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();
        nameSlug = nameSlug.replaceAll(" ", "-").toLowerCase();

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
                Logs.fatalLog(this.getClass(), "FAILED - Try load roster from not exist guild! "+ guildId);
            } else { // Guild exist run all!

                Call<JsonObject> call = apiCalls.guildRoster(
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
                            if (response.body() != null && response.body().has("members")) {

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
                                    Logs.fatalLog(this.getClass(), "FAILED - Save guild update -roster update- "+ e);
                                }

                                // Set rosters is in status change
                                try {
                                    BlizzardUpdate.dbConnect.update(
                                            Roster.TABLE_NAME,
                                            new String[]{"current_status"},
                                            new String[]{"0"},
                                            "guild_id = ?",
                                            new String[]{guildId+""}
                                    );
                                } catch (SQLException | DataException e) {
                                    Logs.fatalLog(this.getClass(), "FAILED - to change status members ("+ guildId +") "+ e);
                                }

                                // foreach rosters
                                int count = response.body().getAsJsonArray("members").size();
                                for (JsonElement characters : response.body().getAsJsonArray("members")) {
                                    Logs.infoLog(this.getClass(), count +" member to go");
                                    count--;
                                    // Prepare Character info
                                    JsonObject member = characters.getAsJsonObject();
                                    JsonObject character = member.getAsJsonObject("character");

                                    // Try save all character information
                                    long characterId = BlizzardUpdate.shared.characterProfileAPI.save(character);

                                    if (characterId != -1) {
                                        try {
                                            // Save guild_roster DB
                                            // Check if exist in DB
                                            JsonArray roster_db = BlizzardUpdate.dbConnect.select(
                                                    Roster.TABLE_NAME,
                                                    new String[]{"add_regist"},
                                                    Roster.TABLE_KEY +" = ?",
                                                    new String[]{characterId +""}
                                            );

                                            if (roster_db.size() > 0) { // Update
                                                BlizzardUpdate.dbConnect.update(
                                                        Roster.TABLE_NAME,
                                                        new String[]{"rank"},
                                                        new String[]{member.get("rank").getAsString()},
                                                        Roster.TABLE_KEY+"=?",
                                                        new String[]{characterId+""}
                                                );
                                                Logs.infoLog(this.getClass(), "OK - Roster ["+ characterId +"] is UPDATE");
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
                                                                characterId+"",
                                                                guildId+"",
                                                                member.get("rank").getAsString(),
                                                                new Date().getTime() +""
                                                        }
                                                );
                                                Logs.infoLog(this.getClass(), "OK - Roster ["+ characterId +"] is INSERT");
                                            }

                                            // Save rank
                                            // Check if exist in DB
                                            JsonArray rank_db = BlizzardUpdate.dbConnect.select(
                                                    Rank.TABLE_NAME,
                                                    new String[]{Rank.TABLE_KEY},
                                                    Guild.TABLE_KEY +"=?",
                                                    new String[]{guildId+""}
                                            );
                                            if (rank_db.size() == 0) { // Insert rank
                                                BlizzardUpdate.dbConnect.insert(
                                                        Rank.TABLE_NAME,
                                                        Rank.TABLE_KEY,
                                                        new String[]{
                                                                "guild_id",
                                                                "rank"
                                                        },
                                                        new String[]{
                                                                guildId +"",
                                                                member.get("rank").getAsString()
                                                        }
                                                );
                                                Logs.infoLog(this.getClass(), "New rank is added");
                                            }
                                        } catch (DataException | SQLException e) {
                                            Logs.fatalLog(this.getClass(), "FAILED to set guild roster "+ character.get("id").getAsString() +" - "+ e);
                                        }

                                    }
                                }

                                // Remove roster is out
                                try {
                                    BlizzardUpdate.dbConnect.delete(
                                            Roster.TABLE_NAME,
                                            "current_status is FALSE",
                                            new String[]{}
                                    );
                                } catch (SQLException | DataException e) {
                                    Logs.fatalLog(this.getClass(), "FAILED - to remove members is out ("+ guildId +") "+ e);
                                }
                            }

                        } else {
                            if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                                Logs.infoLog(this.getClass(), "NOT Modified rosters - "+ guildId);
                            } else {
                                Logs.errorLog(this.getClass(), "ERROR - rosters "+ response.code() +" - ("+ guildId +")" +" // "+ call.request());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Logs.fatalLog(this.getClass(), "FAILED - guildRoster "+ throwable);
                    }
                });

            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - try load info from guild "+ guildId);
        }
    }

    // Update guild roster
    private void achievements(String realmSlug, String nameSlug, long guildId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();
        nameSlug = nameSlug.replaceAll(" ", "-").toLowerCase();

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
                Logs.fatalLog(this.getClass(), "FAILED - Try load achievement from not exist guild! ");
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
                                Logs.fatalLog(this.getClass(), "FAILED - Save guild update -last achievement update- "+ e);
                            }

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
                                        String key = BlizzardUpdate.dbConnect.insert(
                                                GuildAchievement.TABLE_NAME,
                                                GuildAchievement.TABLE_KEY,
                                                columns,
                                                values
                                        );
                                        Logs.infoLog(this.getClass(), "OK - Guild achievement is register ("+ guildId +") -> "+ key);
                                    }
                                } catch (DataException | SQLException e) {
                                    Logs.fatalLog(this.getClass(), "FAILED - Insert new guildAchievements - "+ e);
                                }
                            }

                        } else {
                            if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                                Logs.infoLog(this.getClass(), "NOT Modified guildAchievements - "+ guildId);
                            } else {
                                Logs.errorLog(this.getClass(), "ERROR - profile "+ response.code() +" - ("+ call.request() +")" +" // "+ call.request());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Logs.fatalLog(this.getClass(), "FAILED - guildAchievements "+ throwable);
                    }
                });
            }

        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get guildAchievements list "+ e);
        }

    }

    // Update guild roster
    private void activities(String realmSlug, String nameSlug, long guildId) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();
        nameSlug = nameSlug.replaceAll(" ", "-").toLowerCase();

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

                        // Save process complete! (last update)
                        try {
                            BlizzardUpdate.dbConnect.update(
                                    Guild.TABLE_NAME,
                                    new String[]{"activities_last_modified"},
                                    new String[]{response.headers().getDate("Last-Modified").getTime() +""},
                                    Guild.TABLE_KEY +" = ?",
                                    new String[]{guildId +""}
                            );
                        } catch (DataException | SQLException e) {
                            Logs.fatalLog(this.getClass(), "FAILED to save last modified ("+ guildId +") - "+ e);
                        }

                        if (response.body() != null && response.body().has("activities")) {
                            for(JsonElement activity : response.body().getAsJsonArray("activities")) {
                                JsonObject activityDet = activity.getAsJsonObject();

                                // Prepare values:
                                List<Object> columns = new ArrayList<>();
                                List<Object> values = new ArrayList<>();
                                columns.add("guild_id");
                                values.add(guildId+"");

                                columns.add("type");
                                String type = activityDet.getAsJsonObject("activity").get("type").getAsString();
                                values.add(type);
                                activityDet.remove("activity");

                                columns.add("timestamp");
                                String timeStamp = activityDet.get("timestamp").getAsString();
                                values.add(timeStamp);
                                activityDet.remove("timestamp");

                                columns.add("detail");
                                Set<String> key = activityDet.keySet();
                                String iterNext = key.iterator().next();
                                values.add(activityDet.getAsJsonObject(iterNext).toString());

                                // Check is activity previously exist:
                                try {
                                    JsonArray db_guild_activities = BlizzardUpdate.dbConnect.select(
                                            Activity.TABLE_NAME,
                                            new String[]{"guild_id", "type", "timestamp"},
                                            "guild_id = ? AND type = ? AND timestamp = ?",
                                            new String[]{guildId + "", type, timeStamp}
                                    );
                                    boolean isInDb = (db_guild_activities.size() > 0);

                                    if (!isInDb) { // Insert
                                        String keyDB = BlizzardUpdate.dbConnect.insert(
                                                Activity.TABLE_NAME,
                                                Activity.TABLE_KEY,
                                                columns,
                                                values
                                        );
                                        Logs.infoLog(this.getClass(), "OK - Activity is register ("+ guildId +") --> "+ keyDB);
                                    }
                                } catch (SQLException | DataException e){
                                    Logs.fatalLog(this.getClass(), "FAILED - get/save guild activity "+ e);
                                }
                            }
                        }
                    } else {
                        if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                            Logs.infoLog(this.getClass(), "NOT Modified guild activity - "+ guildId);
                        } else {
                            Logs.errorLog(this.getClass(), "ERROR - guild activity "+ response.code() +" - ("+ guildId +") --> "+ call.request());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    Logs.fatalLog(this.getClass(), "FAILED - guildActivity "+ throwable);
                }
            });
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - try load info from guild "+ guildId);
        }
    }

    public void working() {
        try {
            JsonArray guilds_db = BlizzardUpdate.dbConnect.selectQuery(
                    "SELECT " +
                    "    g.id, " +
                    "    g.`name`, " +
                    "    r.slug " +
                    "FROM " +
                    "    guild_info g, " +
                    "    realms r " +
                    "WHERE " +
                    "    g.realm_id = r.id;"
            );

            for(JsonElement guild : guilds_db) {
                roster(
                        guild.getAsJsonObject().get("slug").getAsString(),
                        guild.getAsJsonObject().get("name").getAsString().replaceAll(" ", "-").toLowerCase(),
                        guild.getAsJsonObject().get("id").getAsLong());
            }
        } catch (SQLException | DataException e) {
            System.out.println("fail to get old guilds");
        }
    }

}
