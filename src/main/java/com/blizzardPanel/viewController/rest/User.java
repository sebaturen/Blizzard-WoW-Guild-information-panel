package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class User {

    @POST
    @Path("/{character_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setMain(
            @PathParam("character_id") long char_id,
            @Context HttpServletRequest request
    ) {
        com.blizzardPanel.User currentUser = (com.blizzardPanel.User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            if(currentUser.setMainCharacter(char_id)) {
                return Response.ok().build();
            }
            JsonObject resp = new JsonObject();
            resp.addProperty("code", 1);
            resp.addProperty("msg", "Not change");
            return Response.status(304).type(MediaType.APPLICATION_JSON_TYPE).entity(resp.toString()).build();

        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }
}
