package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.ServerTime;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.characters.playable.PlayableSpec;
import com.blizzardPanel.gameObject.mythicKeystones.*;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.raiderIO.RaiderIOUpdate;
import com.blizzardPanel.viewController.GuildController;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Path("/mythicPlus/")
public class MythicPlus {

    @GET
    @Path("best/{season}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response memberList(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @PathParam("season") long season
    ) {
        MythicSeason mythicSeason = new MythicSeason.Builder(season).build();

        try {
            String query =
                    "SELECT DISTINCT " +
                    "    kr.id " +
                    "FROM " +
                    "    keystone_dungeon_run_members km " +
                    "    LEFT JOIN keystone_dungeon_run kr ON km.keystone_dungeon_run_id = kr.id " +
                    "    LEFT JOIN keystone_dungeon kd ON kr.keystone_dungeon_id = kd.id " +
                    "    LEFT JOIN guild_roster gr ON km.character_id = gr.character_id " +
                    "WHERE " +
                    "    gr.character_id IS NOT NULL " +
                    "    AND gr.guild_id = "+ GuildController.getInstance().getId() +" "+
                    "    AND kr.is_completed_within_time = 1 " +
                    "    AND kr.completed_timestamp >= "+ mythicSeason.getStart_timestamp() +" " +
                    ((mythicSeason.getEnd_timestamp() > 0) ? "AND kr.completed_timestamp <= " + mythicSeason.getEnd_timestamp() : "") +
                    "ORDER BY " +
                    "    kr.keystone_level DESC, " +
                    "    CASE WHEN kd.keystone_upgrades_3 >= kr.duration THEN " +
                    "        3 " +
                    "    ELSE " +
                    "        CASE WHEN kd.keystone_upgrades_2 >= kr.duration THEN " +
                    "            2 " +
                    "        ELSE " +
                    "            CASE WHEN kd.keystone_upgrades_1 >= kr.duration THEN " +
                    "                1 " +
                    "            ELSE " +
                    "                -1 " +
                    "            END " +
                    "        END " +
                    "    END DESC, " +
                    "    kr.completed_timestamp DESC " +
                    "LIMIT 3;";

            return Response.ok(loadKeys(query, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FATAL - error MythicPlus best season ["+ season +"] "+ e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("weekRuns")
    @Produces(MediaType.APPLICATION_JSON)
    public Response weekRuns(@DefaultValue("en_US") @QueryParam("locale") String locale) {
        try {
            String query =
                "SELECT DISTINCT " +
                "    kr.id " +
                "FROM " +
                "    keystone_dungeon_run_members km " +
                "    LEFT JOIN keystone_dungeon_run kr ON km.keystone_dungeon_run_id = kr.id " +
                "    LEFT JOIN guild_roster gr ON km.character_id = gr.character_id " +
                "WHERE " +
                "    gr.character_id IS NOT NULL " +
                "    AND gr.guild_id = "+ GuildController.getInstance().getId() +" "+
                "    AND kr.completed_timestamp >= "+ ServerTime.getLastResetTime() +" " +
                "ORDER BY " +
                "    kr.completed_timestamp DESC";

            return Response.ok(loadKeys(query, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FATAL - error MythicPlus week season "+ e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("{characterId}")
    public Response characterWeek(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @PathParam("characterId") long characterId,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {

            try {
                String query =
                        "SELECT " +
                        "    kr.id " +
                        "FROM " +
                        "    keystone_dungeon_run kr, " +
                        "    keystone_dungeon_run_members km " +
                        "WHERE " +
                        "    km.keystone_dungeon_run_id = kr.id " +
                        "    AND km.character_id = "+ characterId +" " +
                        "    AND kr.completed_timestamp >= "+ ServerTime.getLastResetTime() +" " +
                        "ORDER BY " +
                        "    kr.keystone_level DESC, " +
                        "    kr.completed_timestamp DESC";
                return Response.ok(loadKeys(query, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } catch (SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "FATAL - error MythicPlus week season "+ e);
                return Response.serverError().build();
            }
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();

    }

    @GET
    @Path("topFailed")
    public Response topFailedRuns(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            try {
                String query =
                        "SELECT DISTINCT  " +
                        "    kr.id " +
                        "FROM " +
                        "    keystone_dungeon_run_members km " +
                        "    LEFT JOIN keystone_dungeon_run kr ON km.keystone_dungeon_run_id = kr.id " +
                        "    LEFT JOIN guild_roster gr ON km.character_id = gr.character_id " +
                        "WHERE " +
                        "    gr.character_id IS NOT NULL " +
                        "    AND gr.guild_id = "+ GuildController.getInstance().getId() +" " +
                        "    AND is_completed_within_time = FALSE " +
                        "    AND completed_timestamp >= 1606172400000" +
                        "ORDER BY " +
                        "    kr.duration DESC " +
                        "LIMIT 3";
                return Response.ok(loadKeys(query, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } catch (SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "FATAL - error MythicPlus failed top season "+ e);
                return Response.serverError().build();
            }
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @GET
    @Path("weekRunsFailed")
    public Response weekRunsFailed(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getIs_guild_member()) {
            try {
                String query =
                        "SELECT DISTINCT " +
                        "    kr.id " +
                        "FROM " +
                        "    keystone_dungeon_run_members km " +
                        "    LEFT JOIN keystone_dungeon_run kr ON km.keystone_dungeon_run_id = kr.id " +
                        "    LEFT JOIN guild_roster gr ON km.character_id = gr.character_id " +
                        "WHERE " +
                        "    gr.character_id IS NOT NULL " +
                        "    AND gr.guild_id = "+ GuildController.getInstance().getId() +" " +
                        "    AND kr.completed_timestamp >= "+ ServerTime.getLastResetTime() +" " +
                        "    AND kr.is_completed_within_time = FALSE " +
                        "ORDER BY " +
                        "    kr.completed_timestamp DESC;";

                return Response.ok(loadKeys(query, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } catch (SQLException | DataException e) {
                Logs.fatalLog(this.getClass(), "FATAL - error MythicPlus failed week season "+ e);
                return Response.serverError().build();
            }
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @GET
    @Path("weekAffix")
    public Response weekAffix(@DefaultValue("en_US") @QueryParam("locale") String locale) {
        // Get current affix:
        try {
            String query =
                    "SELECT " +
                    "    * " +
                    "FROM " +
                    "    keystone_affixes_weeks " +
                    "WHERE " +
                    "    `week` = ( " +
                    "        SELECT " +
                    "            affix1 " +
                    "        FROM " +
                    "            keystone_affixes_weeks " +
                    "        WHERE " +
                    "            `week` = 0)";

            JsonObject currentAffixesDB = DBLoadObject.dbConnect.selectQuery(query).get(0).getAsJsonObject();
            int nextAffix = currentAffixesDB.get("week").getAsInt()+1;
            if (nextAffix > 12) {
                nextAffix = 1;
            }
            JsonObject nextAffixesDB = DBLoadObject.dbConnect.select(
                    "keystone_affixes_weeks",
                    new String[] { "affix1", "affix2", "affix3", "affix4" },
                    "week = ?",
                    new String[] { ""+ nextAffix }
            ).get(0).getAsJsonObject();

            // Prepare output
            JsonArray currentAffixes = new JsonArray();
            currentAffixes.add(affixDetail(currentAffixesDB.get("affix1").getAsInt(), locale));
            currentAffixes.add(affixDetail(currentAffixesDB.get("affix2").getAsInt(), locale));
            currentAffixes.add(affixDetail(currentAffixesDB.get("affix3").getAsInt(), locale));
            currentAffixes.add(affixDetail(currentAffixesDB.get("affix4").getAsInt(), locale));

            JsonArray nextAffixes = new JsonArray();
            nextAffixes.add(affixDetail(nextAffixesDB.get("affix1").getAsInt(), locale));
            nextAffixes.add(affixDetail(nextAffixesDB.get("affix2").getAsInt(), locale));
            nextAffixes.add(affixDetail(nextAffixesDB.get("affix3").getAsInt(), locale));
            nextAffixes.add(affixDetail(nextAffixesDB.get("affix4").getAsInt(), locale));

            JsonObject weekAffixNexAffix = new JsonObject();
            weekAffixNexAffix.add("current", currentAffixes);
            weekAffixNexAffix.add("next", nextAffixes);

            return Response.ok(weekAffixNexAffix.toString(), MediaType.APPLICATION_JSON_TYPE).build();

        } catch (DataException | SQLException e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }

    private JsonObject affixDetail(int afId, String locale) {
        MythicAffix af = new MythicAffix.Builder(afId).build();
        if (af == null) {
            // try load info from blizzard again... if no t exist, principal in first time running new affixes
            BlizzardUpdate.shared.mythicKeystoneDungeonAPI.affixesDetail(afId);
            af = new MythicAffix.Builder(afId).build();
        }
        JsonObject affixDetail = new JsonObject();
        if (af != null) {
            affixDetail.addProperty("id", af.getId());
            affixDetail.addProperty("name", af.getName(locale));
            affixDetail.addProperty("desc", af.getDescription(locale));
            affixDetail.addProperty("media", af.getMedia().getValue());
        }
        return affixDetail;
    }

    private JsonArray loadKeys(String query, String locale) throws SQLException, DataException {

        JsonArray keyDb = DBLoadObject.dbConnect.selectQuery(query);

        JsonArray runs = new JsonArray();
        for (JsonElement run : keyDb) {
            MythicDungeonRun runOb = new MythicDungeonRun.Builder(run.getAsJsonObject().get("id").getAsLong()).build();
            runs.add(parseRunDetail(runOb, locale));
        }

        return runs;

    }

    private JsonObject parseRunDetail(MythicDungeonRun runOb, String locale) {

        JsonObject runDetail = new JsonObject();
        runDetail.addProperty("id", runOb.getId());
        runDetail.addProperty("lvl", runOb.getKeystone_level());
        runDetail.addProperty("complete_date", new SimpleDateFormat(GeneralConfig.getDateFormat(locale)).format(new Date(runOb.getCompleted_timestamp())));
        runDetail.addProperty("is_completed_within_time", runOb.isIs_completed_within_time());
        runDetail.addProperty("map_id", runOb.getDungeon().getId());
        runDetail.addProperty("map_name", runOb.getDungeon().getName(locale));

        // Prepare time
        runDetail.addProperty("upgrade_key", runOb.getUpgradeKey());
        int[] duration = runOb.getTimeDuration();
        runDetail.addProperty("duration_h", duration[0]);
        runDetail.addProperty("duration_m", duration[1]);
        runDetail.addProperty("duration_s", duration[2]);

        JsonArray healer = new JsonArray();
        JsonArray tank = new JsonArray();
        JsonArray dps = new JsonArray();
        for (MythicDungeonMember member : runOb.getMembers()) {
            JsonObject mem = new JsonObject();
            mem.addProperty("id", member.getCharacter().getId());
            mem.addProperty("name", member.getCharacter().getName());
            mem.addProperty("iLvl", member.getCharacter_item_level());
            mem.addProperty("realm", member.getCharacter().getRealm().getName(locale));

            boolean mainGuildStatus = false;
            if (member.getCharacter().getInfo() != null) {
                mainGuildStatus = (member.getCharacter().getInfo().getGuild_id() == GuildController.getInstance().getId());
            }
            mem.addProperty("main_guild", mainGuildStatus);

            PlayableSpec spec;
            try {
                spec = member.getCharacter().getActiveSpec().getPlayableSpec();
            } catch (NullPointerException e) {
                spec = new PlayableSpec.Builder(member.getCharacter_spec_id()).build();
            }
            mem.addProperty("class", spec.getPlayableClass().getId());
            mem.addProperty("spec", spec.getId());
            mem.addProperty("rol", spec.getRole().getType());

            switch (spec.getRole().getType()) {
                case "TANK":
                    tank.add(mem);
                    break;
                case "HEALER":
                    healer.add(mem);
                    break;
                case "DAMAGE":
                    dps.add(mem);
                    break;
            }
        }
        runDetail.add("TANK", tank);
        runDetail.add("HEALER", healer);
        runDetail.add("DAMAGE", dps);

        JsonArray affixes = new JsonArray();
        for (MythicAffix affix : runOb.getAffixes()) {
            JsonObject aff = new JsonObject();
            aff.addProperty("id", affix.getId());
            aff.addProperty("name", affix.getName(locale));
            aff.addProperty("desc", affix.getDescription(locale));
            aff.addProperty("media", affix.getMedia().getValue());

            affixes.add(aff);
        }
        runDetail.add("affixes", affixes);

        return runDetail;
    }

}