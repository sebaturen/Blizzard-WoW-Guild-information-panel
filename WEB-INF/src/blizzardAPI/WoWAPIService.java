package com.blizzardPanel.blizzardAPI;

import okhttp3.RequestBody;
import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface WoWAPIService {

    //------------------------------------------ API Detail
    String API_OAUTH_URL            = "https://%s.battle.net";
    String API_ROOT_URL             = "https://%s.api.blizzard.com"; //location
    int API_SECOND_LIMIT_ERROR      = 429;
    long ACTUAL_SEASON_TIMESTAMP    = 1579586219000L; //Update raider.io season too
    String API_CHARACTER_RENDER_URL = "https://render-%s.worldofwarcraft.com/character/%s"; //{region}, {character.thumbnail}
    String API_ITEM_RENDER_URL      = "https://render-%s.worldofwarcraft.com/icons/%s/%s"; //{region}, {size 56}, {item icon}

    @GET
    Call<JSONObject> freeUrl(
            @Url String url,
            @Header("Authorization") String basicAuth
    );

    //------------------------------------------ Oauth Token
    @POST("/oauth/token")
    Call<JSONObject> accessToken(
            @Body RequestBody params,
            @Header("Authorization") String basicAuth
    );

    //------------------------------------------ WoW Info

    @GET("/wow/guild/{realm}/{guildName}")
    Call<JSONObject> guildProfile(
            @Path("realm") String realm,
            @Path("guildName") String guildName,
            @Header("Authorization") String token
    );

    @GET("/wow/guild/{realm}/{guildName}")
    Call<JSONObject> guild(
            @Path("realm") String realm,
            @Path("guildName") String guildName,
            @Query("locale") String locale,
            @Query("fields") String fields,
            @Header("Authorization") String token
    );

    @GET("/wow/character/{realm}/{name}")
    Call<JSONObject> character(
            @Path("realm") String realm,
            @Path("name") String name,
            @Header("Authorization") String token
    );

    @GET("/wow/spell/{id}")
    Call<JSONObject> spell(
            @Path("id") int id,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/wow/item/{id}")
    Call<JSONObject> item(
            @Path("id") int id,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/wow/boss")
    Call<JSONObject> bosses(
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/wow/auction/data/{realm}")
    Call<JSONObject> auction(
            @Path("realm") String realm,
            @Header("Authorization") String token
    );

    //------------------------------------------ WoW Data

    @GET("/wow/data/character/races")
    Call<JSONObject> playableRaces(
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/wow/data/guild/achievements")
    Call<JSONObject> guildAchievements(
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/wow/data/character/achievements")
    Call<JSONObject> characterAchievements(
            @Query("locale") String locale,
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

    //------------------------------------------ Data WoW

    @GET("/data/wow/token/index")
    Call<JSONObject> token(
            @Query("namespace") String namespace,
            @Query("locale") String locale,
            @Header("Authorization") String token
    );

    @GET("/data/wow/realm/index")
    Call<JSONObject> realmIndex(
            @Query("region") String region,
            @Query("locale") String locale,
            @Query("namespace") String namespace,
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

    @GET("/data/wow/playable-specialization/")
    Call<JSONObject> playableSpecialization(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    //-----------------------------------------------------------Community OAuth

    @GET("/wow/user/characters")
    Call<JSONObject> userCharacter(
            @Query("access_token") String userAccessToken,
            @Header("Authorization") String token
    );

}
