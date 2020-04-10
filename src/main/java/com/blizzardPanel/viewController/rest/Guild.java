package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.User;
import com.blizzardPanel.gameObject.characters.*;
import com.blizzardPanel.gameObject.guilds.GuildRank;
import com.blizzardPanel.gameObject.guilds.GuildRoster;
import com.blizzardPanel.viewController.GeneralController;
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

@Path("/guild")
public class Guild {

    @GET
    @Path("/ranks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response guildRanks(@Context HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            JsonArray ranks = new JsonArray();
            for(GuildRank rank : GuildController.getInstance().getGuildRanks()) {
                JsonObject rankDet = new JsonObject();
                rankDet.addProperty("id", rank.getId());
                rankDet.addProperty("lvl", rank.getRank_lvl());
                rankDet.addProperty("title", rank.getTitle());

                ranks.add(rankDet);
            }
            return Response.ok(ranks.toString(), MediaType.APPLICATION_JSON_TYPE).build();
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @GET
    @Path("/member/list")
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
                if (currentUser.getGuild_rank() != -1) {
                    info.addProperty("equip_lvl", charInfo.getEquipped_item_level());
                    info.addProperty("avg_lvl", charInfo.getAverage_item_level());

                    if (charInfo.getBestMythicPlusScore() != null) {
                        info.add("mythicScore", charInfo.getMythicPlusScores());
                    }
                    if (charInfo.getMythicPlusScores() != null) {
                        info.add("bestMythicScore", charInfo.getBestMythicPlusScore());
                    }
                    if (charMember.getHoaLvl() > 0) {
                        info.addProperty("hoa_lvl", charMember.getHoaLvl());
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
    @Path("/member/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response memberDetail(@PathParam("id") long id, @Context HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            CharacterMember cm = new CharacterMember.Builder(id).build();
            if (cm != null) {
                JsonObject memberDetail = new JsonObject();

                // Basic information
                memberDetail.addProperty("name", cm.getName());

                // Information
                JsonObject info = new JsonObject();
                info.addProperty("class", cm.getInfo().getClass_id());
                info.addProperty("level", cm.getInfo().getLevel());
                info.addProperty("img", cm.getMedia().getRender_url());
                info.addProperty("race", cm.getInfo().getRace_id());
                memberDetail.add("info", info);

                // Statistics
                JsonObject stats = new JsonObject();
                memberDetail.add("stats", stats);

                // Active Specialization
                JsonObject activeSpec = new JsonObject();
                memberDetail.add("active_spec", activeSpec);

                // Items
                JsonObject items = new JsonObject();
                memberDetail.add("items", items);

                return Response.ok(memberDetail.toString(), MediaType.APPLICATION_JSON_TYPE).build();
            }
            JsonObject notFound = new JsonObject();
            notFound.addProperty("code", 1);
            notFound.addProperty("msg", "Character Member not found");
            return Response.status(404).type(MediaType.APPLICATION_JSON_TYPE).entity(notFound.toString()).build();
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }
}
