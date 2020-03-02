package com.blizzardPanel.blizzardAPI;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface WoWAPIService {

    //------------------------------------------ API Detail
    String API_ROOT_URL             = "https://%s.api.blizzard.com/"; //location
    int LIMIT_PER_SECOND            = 100;
    int LIMIT_PER_HOUR              = 36000;
    long ACTUAL_SEASON_TIMESTAMP    = 1579586219000L; //Update raider.io season too
    String API_CHARACTER_RENDER_URL = "https://render-%s.worldofwarcraft.com/character/%s"; //{region}, {character.thumbnail}
    String API_ITEM_RENDER_URL      = "https://render-%s.worldofwarcraft.com/icons/%s/%s"; //{region}, {size 56}, {item icon}

    @GET
    Call<JsonObject> freeUrl(
            @Url String url,
            @Header("Authorization") String basicAuth
    );

    //------------------------------------------ WoW Info

    @GET("wow/guild/{realm}/{guildName}")
    Call<JsonObject> guildProfile(
            @Path("realm") String realm,
            @Path("guildName") String guildName,
            @Query("fields") String fields,
            @Header("Authorization") String token
    );

    @GET("wow/guild/{realm}/{guildName}")
    Call<JsonObject> guild(
            @Path("realm") String realm,
            @Path("guildName") String guildName,
            @Query("locale") String locale,
            @Query("fields") String fields,
            @Header("Authorization") String token
    );

    @GET("wow/character/{realm}/{name}")
    Call<JsonObject> character(
            @Path("realm") String realm,
            @Path("name") String name,
            @Query("fields") String fields,
            @Header("Authorization") String token
    );

    @GET("wow/spell/{id}")
    Call<JsonObject> spell(
            @Path("id") int id,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("wow/item/{id}")
    Call<JsonObject> item(
            @Path("id") int id,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("wow/boss/")
    Call<JsonObject> bosses(
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("wow/auction/data/{realm}")
    Call<JsonObject> auction(
            @Path("realm") String realm,
            @Header("Authorization") String token
    );

    //------------------------------------------ WoW Data

    @GET("wow/data/character/races")
    Call<JsonObject> playableRaces(
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("wow/data/guild/achievements")
    Call<JsonObject> guildAchievements(
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("wow/data/character/achievements")
    Call<JsonObject> characterAchievements(
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    //------------------------------------------ WoW Profile

    @GET("profile/wow/character/{realm}/{name}/mythic-keystone-profile")
    Call<JsonObject> characterMythicPlusProfile(
            @Path("realm") String realm,
            @Path("name") String name,
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    //------------------------------------------ Data WoW

    @GET("data/wow/token/index")
    Call<JsonObject> token(
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("data/wow/realm/index")
    Call<JsonObject> realmIndex(
            @Query("region") String region,
            @Query("locale") String locale,
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    @GET("data/wow/connected-realm/index")
    Call<JsonObject> connectedRealmIndex(
            @Query("locale") String locale,
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    @GET("data/wow/connected-realm/{id}")
    Call<JsonObject> connectedRealm(
            @Path("id") int id,
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("data/wow/playable-class/index")
    Call<JsonObject> playableClassIndex(
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("data/wow/playable-specialization/")
    Call<JsonObject> playableSpecialization(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    //-----------------------------------------------------------Community OAuth

    @GET("wow/user/characters")
    Call<JsonObject> userCharacter(
            @Query("access_token") String userAccessToken
    );

}
