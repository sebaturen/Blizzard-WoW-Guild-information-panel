package com.blizzardPanel.blizzardAPI;

import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RaiderIOService {

    //------------------------------------------ API Detail
    String RAIDER_IO_API_URL        = "https://raider.io/api"; //[guilds/characters], {location}, {server}, {guild name/character name}
    String RAIDER_IO_ACTUAL_SEASON  = "season-bfa-4";

    //------------------------------------------ Raider.IO Info
    @GET("/characters/{location}/{server}/{characterName}")
    Call<JSONObject> character(
            @Path("location") String location,
            @Path("server") String server,
            @Path("characterName") String characterName,
            @Query("season") String season
    );

    @GET("/guilds/{location}/{server}/{guildName}")
    Call<JSONObject> guilds(
            @Path("location") String location,
            @Path("server") String server,
            @Path("guildName") String guildName
    );
}
