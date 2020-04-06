package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/wow_token")
public class WoWToken {

    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response wowToken(
            @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (max > 100) {
            max = 100;
        }
        try {
            JsonArray wowTokens_db = DBLoadObject.dbConnect.select(
                    com.blizzardPanel.gameObject.WoWToken.TABLE_NAME,
                    new String[]{"last_updated_timestamp", "price"},
                    "1=1 ORDER BY last_updated_timestamp DESC LIMIT "+ max,
                    new String[]{}
            );

            JsonArray prices = new JsonArray();
            for(JsonElement wowToken : wowTokens_db) {
                JsonObject token = new JsonObject();
                token.addProperty("date", wowToken.getAsJsonObject().get("last_updated_timestamp").getAsLong());
                token.addProperty("price", wowToken.getAsJsonObject().get("price").getAsLong());

                prices.add(token);
            }
            return Response.ok(prices.toString(), MediaType.APPLICATION_JSON_TYPE).build();
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get an wow token prices - "+ e);
            return Response.serverError().build();
        }
    }
}
