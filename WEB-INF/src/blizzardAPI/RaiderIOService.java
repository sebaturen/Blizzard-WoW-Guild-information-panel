package com.blizzardPanel.blizzardAPI;

import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RaiderIOService {

    @GET("/characters/{location}/{server}/{characterName}")
    Call<JSONObject> character(
            @Path("location") String location,
            @Path("server") String server,
            @Path("characterName") String characterName
    );

    @GET("/guilds/{location}/{server}/{guildName}")
    Call<JSONObject> guilds(
            @Path("location") String location,
            @Path("server") String server,
            @Path("guildName") String guildName
    );
}
