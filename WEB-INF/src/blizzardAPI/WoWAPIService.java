package com.blizzardPanel.blizzardAPI;

import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface WoWAPIService {

    //------------------------------------------ WoW Info

    @GET("/wow/guild/{realm}/{guildName}")
    Call<JSONObject> guildProfile(
            @Path("realm") String realm,
            @Path("guildName") String guildName,
            @Header("Authorization") String token
    );

    @GET("/wow/guild/{realm}/{guildName}")
    Call<JSONObject> guild(
            @Query("locale") String locale,
            @Query("fields") String fields,
            @Path("realm") String realm,
            @Path("guildName") String guildName,
            @Header("Authorization") String token
    );

    @GET("/wow/character/{realm}/{name}")
    Call<JSONObject> character(
            @Path("realm") String realm,
            @Path("name") String name,
            @Header("Authorization") String token
    );

    //------------------------------------------ WoW Profile

    @GET("/profile/wow/character/{realm}/{name}/mythic-keystone-profile")
    Call<JSONObject> characterMythicPlusProfile(
            @Path("realm") String realm,
            @Path("name") String name,
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    //------------------------------------------ WoW Data

    @GET("/data/wow/token/index")
    Call<JSONObject> token(
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/data/wow/connected-realm/index")
    Call<JSONObject> connectedRealmIndex(
            @Query("locale") String locale,
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    @GET("/data/wow/connected-realm/{id}")
    Call<JSONObject> connectedRealm(
            @Path("id") int id,
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/data/wow/playable-class/index")
    Call<JSONObject> playableClassIndex(
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );


}
