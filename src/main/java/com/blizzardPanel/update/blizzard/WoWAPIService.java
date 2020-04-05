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

    @GET("profile/wow/character/{realmSlug}/{characterName}/character-media")
    Call<JsonObject> characterMedia(
            @Path("realmSlug") String realm,
            @Path("characterName") String name,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------ Account Profile
    @GET("profile/user/wow")
    Call<JsonObject> accountProfileSummary(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
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

    //------------------------------------------ WoW Token
    @GET("data/wow/token/index")
    Call<JsonObject> wowTokenIndex(
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    @GET("data/wow/mythic-keystone/season/index")
    Call<JsonObject> mythicKeystoneSeasonIndex(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    @GET("data/wow/mythic-keystone/season/{id}")
    Call<JsonObject> mythicKeystoneSeason(
            @Path("id") String seasonId,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    //------------------------------------------ Journal
    @GET("data/wow/journal-instance/index")
    Call<JsonObject> journalInstanceIndex(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );

    @GET("data/wow/journal-instance/{journalInstanceId}")
    Call<JsonObject> journalInstance(
            @Path("journalInstanceId") String journalInstanceId,
            @Query("namespace") String namespace,
            @Header("Authorization") String token,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    @GET("data/wow/journal-encounter/index")
    Call<JsonObject> journalEncounterIndex(
            @Query("namespace") String namespace,
            @Header("Authorization") String token
    );
}
