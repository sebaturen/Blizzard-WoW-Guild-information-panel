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
import javax.xml.crypto.Data;
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
                info.addProperty("gender", charInfo.getGender_type());
                info.addProperty("faction", charInfo.getFaction().getType());

                // Only if user is a guild member
                if (currentUser != null && currentUser.getGuild_rank() != -1) {
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

        return Response.ok(members.toString(), MediaType.APPLICATION_JSON).build();
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
                                "    gr.character_id, " +
                                "    uc.user_id, " +
                                "    gk.title," +
                                "    CASE when u.main_character_id = c.id then 1 else 0 end as isMain " +
                                "FROM " +
                                "    guild_roster gr " +
                                "    LEFT JOIN `characters` c ON gr.character_id = c.id " +
                                "    LEFT JOIN character_info ci ON gr.character_id = ci.character_id " +
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
                                "    isMain DESC, " +
                                "    ci.`level` DESC;";
                JsonArray characters_db = DBLoadObject.dbConnect.selectQuery(query);

                return Response.ok(alterListGen(characters_db, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
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

    @GET
    @Path("/alter/{character_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterList(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @PathParam("character_id") long characterId,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            String query =
                    "SELECT " +
                    "    uc.character_id, " +
                    "    uc.user_id, " +
                    "    gk.title, " +
                    "    CASE WHEN u.main_character_id = uc.character_id THEN " +
                    "        1 " +
                    "    ELSE " +
                    "        0 " +
                    "    END AS isMain " +
                    "FROM " +
                    "    user_character uc " +
                    "    LEFT JOIN guild_roster gr ON uc.character_id = gr.character_id " +
                    "    LEFT JOIN guild_rank gk ON gk.id = gr.rank_id, " +
                    "    users u " +
                    "WHERE " +
                    "    uc.user_id = ( " +
                    "        SELECT " +
                    "            u.user_id " +
                    "        FROM " +
                    "            user_character u " +
                    "        WHERE " +
                    "            u.character_id = "+ characterId +") " +
                    "    AND gr.guild_id = "+ GuildController.getInstance().getId() +" " +
                    "    AND u.id = uc.user_id " +
                    "    AND gk.guild_id = gr.guild_id;";
            try {
                JsonArray characters_db = DBLoadObject.dbConnect.selectQuery(query);
                return Response.ok(alterListGen(characters_db, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } catch (SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "FAILED to get character altes "+ e);
                JsonObject notAuthorize = new JsonObject();
                notAuthorize.addProperty("code", 1);
                notAuthorize.addProperty("msg", "Failed to get information");
                return Response.status(500).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
            }
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    private JsonObject alterListGen(JsonArray characterList, String locale) {
        JsonObject users = new JsonObject();
        for(JsonElement char_db : characterList) {

            JsonObject charDetail_db = char_db.getAsJsonObject();

            CharacterMember cm = new CharacterMember.Builder(charDetail_db.get("character_id").getAsLong()).build();
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
            characterDetail.addProperty("isMain", (charDetail_db.get("isMain").getAsInt() == 1));

            users.get(charDetail_db.get("user_id").getAsString()).getAsJsonObject().getAsJsonArray("characters").add(characterDetail);

        }

        JsonObject infoResp = new JsonObject();
        infoResp.add("users", users);

        return infoResp;
    }
}
