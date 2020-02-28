package com.blizzardPanel.blizzardAPI;

import okhttp3.RequestBody;
import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface WoWOauthService {

    @POST("/oauth/token")
    Call<JSONObject> token(
            @Body RequestBody params,
            @Header("Authorization") String basicAuth
    );

}
