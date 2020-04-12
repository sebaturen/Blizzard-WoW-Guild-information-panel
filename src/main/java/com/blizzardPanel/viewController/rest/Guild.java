package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.characters.*;
import com.blizzardPanel.gameObject.guilds.GuildRank;
import com.blizzardPanel.gameObject.guilds.GuildRoster;
import com.blizzardPanel.viewController.GeneralController;
import com.blizzardPanel.viewController.GuildController;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

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

    @GET
    @Path("/alters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response altersList(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            try {
                String query =
                        "SELECT " +
                                "    c.id, " +
                                "    uc.user_id, " +
                                "    gk.title " +
                                "FROM " +
                                "    guild_roster gr " +
                                "    LEFT JOIN `characters` c ON gr.character_id = c.id " +
                                "    left join character_info ci on gr.character_id = ci.character_id " +
                                "    LEFT JOIN user_character uc ON gr.character_id = uc.character_id, " +
                                "    guild_rank gk, " +
                                "    users u " +
                                "WHERE " +
                                "    gr.guild_id = 61031120 " +
                                "    AND gk.id = gr.rank_id " +
                                "    AND uc.id IS NOT NULL " +
                                "    AND u.id = uc.user_id " +
                                "ORDER BY " +
                                "    gr.rank_id ASC, " +
                                "    u.guild_rank ASC, " +
                                "    ci.`level` DESC;";
                JsonArray charactes_db = DBLoadObject.dbConnect.selectQuery(query);

                JsonObject users = new JsonObject();
                for(JsonElement char_db : charactes_db) {

                    JsonObject charDetail_db = char_db.getAsJsonObject();

                    CharacterMember cm = new CharacterMember.Builder(charDetail_db.get("id").getAsLong()).build();
                    if (!users.has(charDetail_db.get("user_id").getAsString())) {
                        User u = new User.Builder(charDetail_db.get("user_id").getAsInt()).build();
                        JsonObject userDetail = new JsonObject();
                        userDetail.addProperty("id", u.getId());
                        userDetail.addProperty("battle_tag", u.getBattle_tag().split("#")[0]);
                        userDetail.addProperty("last_modified", new SimpleDateFormat(GeneralConfig.getDateFormat(locale)).format(new Date(u.getLast_alters_update())));
                        JsonArray characters = new JsonArray();
                        userDetail.add("characters", characters);
                        users.add(charDetail_db.get("user_id").getAsString(), userDetail);
                    }

                    JsonObject characterDetail = new JsonObject();
                    characterDetail.addProperty("id", cm.getId());
                    characterDetail.addProperty("name", cm.getName());
                    characterDetail.addProperty("lvl", cm.getInfo().getLevel());
                    characterDetail.addProperty("class", cm.getInfo().getClass_id());
                    characterDetail.addProperty("spec", cm.getActiveSpec().getSpecialization_id());
                    characterDetail.addProperty("title", charDetail_db.get("title").getAsString());

                    users.get(charDetail_db.get("user_id").getAsString()).getAsJsonObject().getAsJsonArray("characters").add(characterDetail);

                }

                JsonObject infoResp = new JsonObject();
                infoResp.add("users", users);
                return Response.ok(infoResp.toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } catch (SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "Failed to get alters "+ e);
                return Response.serverError().build();
            }
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }
}
