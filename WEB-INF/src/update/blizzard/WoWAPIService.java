package com.blizzardPanel.update.blizzard;

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

    @GET
    Call<JsonObject> freeUrl(
            @Url String url,
            @Header("Authorization") String basicAuth,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------
    // NEW API Sys
    //------------------------------------------

    //------------------------------------------
    // Profile
    //------------------------------------------
    //------------------------------------------ Guild API

    @GET("data/wow/guild/{realmSlug}/{nameSlug}")
    Call<JsonObject> guild(
            @Path("realmSlug") String realmSlug,
            @Path("nameSlug") String nameSlug,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    @GET("data/wow/guild/{realmSlug}/{nameSlug}/activity")
    Call<JsonObject> guildActivity(
            @Path("realmSlug") String realmSlug,
            @Path("nameSlug") String nameSlug,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    @GET("data/wow/guild/{realmSlug}/{nameSlug}/achievements")
    Call<JsonObject> guildAchievements(
            @Path("realmSlug") String realmSlug,
            @Path("nameSlug") String nameSlug,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    @GET("data/wow/guild/{realmSlug}/{nameSlug}/roster")
    Call<JsonObject> guildRoster(
            @Path("realmSlug") String realmSlug,
            @Path("nameSlug") String nameSlug,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------ Character Profile API
    @GET("profile/wow/character/{realmSlug}/{characterName}")
    Call<JsonObject> characterProfileSummary(
            @Path("realmSlug") String realm,
            @Path("characterName") String name,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    @GET("profile/wow/character/{realmSlug}/{characterName}/status")
    Call<JsonObject> characterProfileStatus(
            @Path("realmSlug") String realmSlug,
            @Path("characterName") String name,
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    @GET("profile/wow/character/{realmSlug}/{characterName}/mythic-keystone-profile")
    Call<JsonObject> characterMythicKeystoneProfileIndex(
            @Path("realmSlug") String realm,
            @Path("characterName") String name,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------
    // Game Data
    //------------------------------------------
    //------------------------------------------ Achievements
    @GET("data/wow/achievement-category/index")
    Call<JsonObject> achievementCategories(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    //------------------------------------------ Playable Class
    @GET("data/wow/playable-class/{classId}")
    Call<JsonObject> playableClass(
            @Path("classId") String classId,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------ Playable Race
    @GET("data/wow/playable-race/{playableRaceId}")
    Call<JsonObject> playableRace(
            @Path("playableRaceId") String playableRaceId,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------ Playable Specialization
    @GET("data/wow/playable-specialization/{specId}")
    Call<JsonObject> playableSpecialization(
            @Path("specId") String specId,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------ Connected Realm
    @GET("data/wow/connected-realm/index")
    Call<JsonObject> connectedRealmIndex(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    //------------------------------------------ Spell
    @GET("data/wow/spell/{spellId}")
    Call<JsonObject> spell(
            @Path("spellId") String spellId,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------ Item
    @GET("data/wow/item/{itemId}")
    Call<JsonObject> item(
            @Path("itemId") String itemID,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------
    //------------------------------------------
    //------------------------------------------
    //------------------------------------------
    //------------------------------------------

    //------------------------------------------ WoW Info



    @GET("wow/guild/{realm}/{guildName}")
    Call<JsonObject> guildOld(
            @Path("realm") String realm,
            @Path("guildName") String guildName,
            @Query("locale") String locale,
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
    @GET("profile/wow/character/{realmSlug}/{characterName}")
    Call<JsonObject> character(
            @Path("realmSlug") String realm,
            @Path("characterName") String name,
            @Query("namespace") String namespace,
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
