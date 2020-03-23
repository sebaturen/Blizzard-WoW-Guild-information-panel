package com.blizzardPanel.update.raiderIO;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RaiderIOService {

    //------------------------------------------ API Detail
    String RAIDER_IO_API_URL        = "https://raider.io/api/";
    String RAIDER_IO_ACTUAL_SEASON  = "season-bfa-4";

    //------------------------------------------ Raider.IO Info
    @GET("characters/{location}/{server}/{characterName}")
    Call<JsonObject> character(
            @Path("location") String location,
            @Path("server") String server,
            @Path("characterName") String characterName,
            @Query("season") String season
    );

    @GET("guilds/{location}/{server}/{guildName}")
    Call<JsonObject> guilds(
            @Path("location") String location,
            @Path("server") String server,
            @Path("guildName") String guildName
    );
}
