package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.gameObject.characters.CharacterInfo;
import com.blizzardPanel.gameObject.characters.CharacterMedia;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.characters.CharacterSpec;
import com.blizzardPanel.gameObject.guilds.GuildRoster;
import com.blizzardPanel.viewController.GuildController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/member")
public class Members {

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response memberList() {

        // Load rosters
        JsonArray members = new JsonArray();
        for(GuildRoster guildRoster : GuildController.getInstance().getGuildRosters()) {
            CharacterMember charMember = guildRoster.getMember();
            JsonObject member = new JsonObject();
            member.addProperty("id", charMember.getId());
            member.addProperty("rank", guildRoster.getGuildRank().getRank_lvl());
            member.addProperty("name", charMember.getName());

            // INFO ---------------------------
            JsonObject info = new JsonObject();
            CharacterInfo charInfo = charMember.getInfo();
            if (charInfo != null) {
                info.addProperty("lvl", charInfo.getLevel());
                info.addProperty("race_id", charInfo.getPlayableRace().getId());

                JsonObject classMember = new JsonObject();
                classMember.addProperty("id", charInfo.getPlayableClass().getId());
                classMember.addProperty("media", charInfo.getPlayableClass().getMedia().getValue());

                info.add("class", classMember);
            }
            member.add("info", info);

            // SPEC ---------------------------
            JsonObject spec = new JsonObject();
            CharacterSpec charSpec = charMember.getActiveSpec();
            if (charSpec != null) {
                spec.addProperty("id", charSpec.getPlayableSpec().getId());
                spec.addProperty("rol", charSpec.getPlayableSpec().getRole().getType());
                spec.addProperty("media", charSpec.getPlayableSpec().getMedia().getValue());
            }
            member.add("spec", spec);

            // MEDIA --------------------------
            JsonObject media = new JsonObject();
            CharacterMedia charMedia = charMember.getMedia();
            if (charMedia != null) {
                media.addProperty("avatar", charMedia.getAvatar_url());
            }
            member.add("media", media);

            members.add(member);
        }

        CacheControl cc = new CacheControl();
        cc.setMaxAge(1);
        return Response.ok(members.toString(), MediaType.APPLICATION_JSON).cacheControl(cc).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response memberDetail(@PathParam("id") long id) {
        JsonObject memberDetail = new JsonObject();
        memberDetail.addProperty("id", id);

        return Response.ok(memberDetail.toString(), MediaType.APPLICATION_JSON_TYPE).build();
    }
}
