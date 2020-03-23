package com.blizzardPanel.update.blizzard.profile;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Realm;
import com.blizzardPanel.gameObject.guild.Guild;
import com.blizzardPanel.update.blizzard.AccessToken;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class GuildAPI {

    private WoWAPIService apiCalls;
    private AccessToken ac;

    public GuildAPI(WoWAPIService apiCalls) {
        this.apiCalls = apiCalls;
    }

    public void update(AccessToken ac) {
        this.ac = ac;
        // Prepare guild information:
        Realm guildRealm = new Realm(GeneralConfig.getStringConfig("GUILD_REALM"));
        String guildName = GeneralConfig.getStringConfig("GUILD_NAME");

        // Check if have a main guild in DB:
        boolean isInDB = false;
        Long db_lasModified = 0L;
        try {
            JsonArray db_profile = BlizzardUpdate.dbConnect.selectQuery(
                    "SELECT last_modified " +
                            "FROM guild_info " +
                            "WHERE " +
                            "`name`='"+ guildName +"' AND " +
                            "`realm_id`="+ guildRealm.getId() +";");
            if (db_profile.size() > 0) {
                isInDB = true;
                db_lasModified = db_profile.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }
        } catch (DataException | SQLException e) {
            Logs.infoLog(GuildAPI.class, "Fail to get MAIN guild "+ e);
        }

        // Start all guild update
        if (!isInDB) {
            firstProfileSync(guildRealm, guildName);
        } else {
            profile(guildRealm, guildName, db_lasModified);
        }

    }

    // Get Main guild info
    private void firstProfileSync(Realm guildRealm, String guildName) {
        String guildSlug = guildName.toLowerCase();
        guildSlug = guildSlug.replace(" ", "-");

        Call<JsonObject> call = apiCalls.guild(
                guildRealm.getSlug(),
                guildSlug,
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                ac.getAuthorization()
        );

        try {
            Response<JsonObject> response = call.execute();
            if (response.isSuccessful()) {
                try {
                    // Actual profile info:
                    JsonObject profile = response.body();
                    // Last modified:
                    Long blizz_lastModified = response.headers().getDate("Last-Modified").getTime();

                    // Insert new Guild
                    BlizzardUpdate.dbConnect.insert(
                            Guild.GUILD_TABLE_NAME,
                            Guild.GUILD_TABLE_KEY,
                            new String[] {
                                    "id",
                                    "name",
                                    "realm_id",
                                    "faction_type",
                                    "achievement_points",
                                    "created_timestamp",
                                    "member_count",
                                    "last_modified"
                            },
                            new String[] {
                                    profile.get("id").getAsString(),
                                    profile.get("name").getAsString(),
                                    profile.get("realm").getAsJsonObject().get("id").getAsString(),
                                    profile.get("faction").getAsJsonObject().get("type").getAsString(),
                                    profile.get("achievement_points").getAsString(),
                                    profile.get("created_timestamp").getAsString(),
                                    profile.get("member_count").getAsString(),
                                    blizz_lastModified +""
                            }
                    );

                } catch (DataException | SQLException e) {
                    Logs.infoLog(GuildAPI.class, "FAIL - firstProfileSync Insert "+ e);
                }
            } else {
                Logs.infoLog(GuildAPI.class, "ERROR - firstProfileSync "+ response.code() +" - ("+ call.request() +")");
            }
        } catch (IOException e) {
            Logs.infoLog(GuildAPI.class, "ERROR - firstProfileSync "+ e);
        }

    }

    // Update guild info
    private void profile(Realm guildRealm, String guildName, Long lastModified) {

        // Parse guild info
        String guildSlug = guildName.toLowerCase();
        guildSlug = guildSlug.replace(" ", "-");

        Call<JsonObject> call = apiCalls.guild(
                guildRealm.getSlug(),
                guildSlug,
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                ac.getAuthorization(),
                BlizzardUpdate.parseDateFormat(lastModified)
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        // Actual profile info:
                        JsonObject profile = response.body();
                        // Last modified:
                        Long blizz_lastModified = response.headers().getDate("Last-Modified").getTime();

                        BlizzardUpdate.dbConnect.update(
                                Guild.GUILD_TABLE_NAME,
                                new String[] {
                                        "faction_type",
                                        "achievement_points",
                                        "created_timestamp",
                                        "member_count",
                                        "last_modified"
                                },
                                new String[] {
                                        profile.get("faction").getAsJsonObject().get("type").getAsString(),
                                        profile.get("achievement_points").getAsString(),
                                        profile.get("created_timestamp").getAsString(),
                                        profile.get("member_count").getAsString(),
                                        blizz_lastModified +""
                                },
                                "id = ?",
                                new String[] {
                                        profile.get("id").getAsString()
                                }
                        );

                    } catch (DataException | SQLException e) {
                        Logs.infoLog(GuildAPI.class, "FAIL - profile insert "+ e);
                    }
                } else {
                    if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                        Logs.infoLog(GuildAPI.class, "NOT Modified Profile - "+ guildName);
                    } else {
                        Logs.infoLog(GuildAPI.class, "ERROR - profile "+ response.code() +" - ("+ call.request() +")");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(GuildAPI.class, "FAIL - profile "+ throwable);
            }
        });
    }

    // Update guild roster
    private void roster(AccessToken ac) {

        Realm guildRealm = new Realm(GeneralConfig.getStringConfig("GUILD_REALM"));
        String guildSlug = GeneralConfig.getStringConfig("GUILD_NAME").toLowerCase();
        guildSlug = guildSlug.replace(" ", "-");

        Call<JsonObject> call = apiCalls.guildRoster(
                guildRealm.getSlug(),
                guildSlug,
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                ac.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    System.out.println("=== GuildRoster");
                    System.out.println(response.body());
                } else {
                    Logs.infoLog(GuildAPI.class, "ERROR - guildRoster "+ response.code() +" - ("+ call.request() +")");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(GuildAPI.class, "FAIL - guildRoster "+ throwable);
            }
        });
    }

    // Update guild roster
    private void achievements(AccessToken ac) {

        Realm guildRealm = new Realm(GeneralConfig.getStringConfig("GUILD_REALM"));
        String guildSlug = GeneralConfig.getStringConfig("GUILD_NAME").toLowerCase();
        guildSlug = guildSlug.replace(" ", "-");

        Call<JsonObject> call = apiCalls.guildAchievements(
                guildRealm.getSlug(),
                guildSlug,
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                ac.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    System.out.println("=== GuildAchievements");
                    System.out.println(response.body());
                } else {
                    Logs.infoLog(GuildAPI.class, "ERROR - guildAchievements "+ response.code() +" - ("+ call.request() +")");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(GuildAPI.class, "FAIL - guildAchievements "+ throwable);
            }
        });
    }

    // Update guild roster
    private void activity(AccessToken ac) {

        Realm guildRealm = new Realm(GeneralConfig.getStringConfig("GUILD_REALM"));
        String guildSlug = GeneralConfig.getStringConfig("GUILD_NAME").toLowerCase();
        guildSlug = guildSlug.replace(" ", "-");

        Call<JsonObject> call = apiCalls.guildAchievements(
                guildRealm.getSlug(),
                guildSlug,
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                ac.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    System.out.println("=== GuildActivity");
                    System.out.println(response.body());
                } else {
                    Logs.infoLog(GuildAPI.class, "ERROR - guildActivity "+ response.code() +" - ("+ call.request() +")");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(GuildAPI.class, "FAIL - guildActivity "+ throwable);
            }
        });
    }

}
