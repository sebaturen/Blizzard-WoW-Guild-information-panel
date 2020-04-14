package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.DataException;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.characters.playable.PlayableClass;
import com.blizzardPanel.gameObject.characters.playable.PlayableRace;
import com.blizzardPanel.viewController.GuildController;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/playable")
public class PlayableInfo {

    @GET
    @Path("/class")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pClass(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            try {
                JsonArray pClasses = new JsonArray();
                JsonArray classes_db = DBLoadObject.dbConnect.select(
                        PlayableClass.TABLE_NAME,
                        new String[]{PlayableClass.TABLE_KEY}
                );

                for(JsonElement classDet : classes_db) {
                    JsonObject classDetail = classDet.getAsJsonObject();
                    PlayableClass pClassOv = new PlayableClass.Builder(classDetail.get(PlayableClass.TABLE_KEY).getAsLong()).build();

                    JsonObject pClass = new JsonObject();
                    pClass.addProperty("id", pClassOv.getId());
                    pClass.addProperty("name", pClassOv.getName(locale));

                    pClasses.add(pClass);
                }
                return Response.ok(pClasses.toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } catch (SQLException | DataException e) {
                JsonObject errorOut = new JsonObject();
                errorOut.addProperty("msg", e.getMessage());
                return Response.status(500).type(MediaType.APPLICATION_JSON_TYPE).entity(errorOut.toString()).build();
            }
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @GET
    @Path("/race")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pRace(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            try {
                JsonArray pRaces = new JsonArray();
                JsonArray races_db = DBLoadObject.dbConnect.select(
                        PlayableRace.TABLE_NAME,
                        new String[]{PlayableRace.TABLE_KEY}
                );

                for(JsonElement raceDet : races_db) {
                    JsonObject raceDetail = raceDet.getAsJsonObject();
                    PlayableRace pRaceOv = new PlayableRace.Builder(raceDetail.get(PlayableRace.TABLE_KEY).getAsLong()).build();

                    if (pRaceOv.getFaction_type().equals(GuildController.getInstance().getFaction_type())
                    && pRaceOv.isIs_selectable()) {
                        JsonObject pRace = new JsonObject();
                        pRace.addProperty("id", pRaceOv.getId());
                        pRace.addProperty("name", pRaceOv.getName(locale));

                        pRaces.add(pRace);
                    }
                }
                return Response.ok(pRaces.toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } catch (SQLException | DataException e) {
                JsonObject errorOut = new JsonObject();
                errorOut.addProperty("msg", e.getMessage());
                return Response.status(500).type(MediaType.APPLICATION_JSON_TYPE).entity(errorOut.toString()).build();
            }
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }
}
