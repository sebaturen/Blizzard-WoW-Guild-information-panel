package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.User;
import com.blizzardPanel.gameObject.characters.*;
import com.blizzardPanel.gameObject.guilds.GuildRoster;
import com.blizzardPanel.viewController.GuildController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/member")
public class Members {

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response memberList(@Context HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("user");

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

                // Only if user is a guild member
                if (currentUser.getGuildRank() != -1) {
                    info.addProperty("equip_lvl", charInfo.getEquipped_item_level());
                    info.addProperty("avg_lvl", charInfo.getAverage_item_level());

                    if (charInfo.getBestMythicPlusScore() != null) {
                        info.add("mythicScore", charInfo.getMythicPlusScores());
                    }
                    if (charInfo.getMythicPlusScores() != null) {
                        info.add("bestMythicScore", charInfo.getBestMythicPlusScore());
                    }

                    int hoaLvl = 0;
                    for (CharacterItem item : charMember.getItem()) {
                        if (item.getAzerite_level() > 0) {
                            hoaLvl = item.getAzerite_level();
                        }
                    }
                    if (hoaLvl > 0) {
                        info.addProperty("hoa_lvl", hoaLvl);
                    }
                }

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
