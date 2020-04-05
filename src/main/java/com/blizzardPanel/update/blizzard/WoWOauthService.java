package com.blizzardPanel.update.blizzard;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface WoWOauthService {

    //------------------------------------------ API Detail
    String API_OAUTH_URL            = "https://%s.battle.net/oauth/";
    String API_OAUTH_AUTHORIZE      = "authorize";

    //------------------------------------------ Oauth Token
    @FormUrlEncoded
    @POST("token")
    Call<JsonObject> accessToken(
            @Field("grant_type") String grantType,
            @Header("Authorization") String basicAuth
    );

    @FormUrlEncoded
    @POST("token")
    Call<JsonObject> userToken(
            @Field("redirect_uri") String redirectUri,
            @Field("scope") String scope,
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Header("Authorization") String basicAuth
    );

    @GET("userinfo")
    Call<JsonObject> userInfo(
            @Query("locale") String locale,
            @Header("Authorization") String basicAuth
    );
}
