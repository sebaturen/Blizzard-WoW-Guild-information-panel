package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.ServerTime;
import com.blizzardPanel.gameObject.mythicKeystones.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

@Path("/mythicPlus")
public class MythicPlus {

    @GET
    @Path("/best/{season}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response memberList(
            @DefaultValue("en_US") @QueryParam("locale") String locale,
            @PathParam("season") long season
    ) {
        MythicSeason mythicSeason = new MythicSeason.Builder(season).build();

        try {
            String query =
                    "SELECT DISTINCT " +
                    "    ex.id " +
                    "FROM ( " +
                    "    SELECT " +
                    "        kr.id " +
                    "    FROM " +
                    "        keystone_dungeon_run_members km " +
                    "    LEFT JOIN keystone_dungeon_run kr ON km.keystone_dungeon_run_id = kr.id " +
                    "    LEFT JOIN guild_roster gr ON km.character_id = gr.character_id " +
                    "WHERE " +
                    "    gr.character_id IS NOT NULL " +
                    "    AND kr.is_completed_within_time = 1 " +
                    "    AND kr.completed_timestamp >= "+ mythicSeason.getStart_timestamp() +" " +
                    ((mythicSeason.getEnd_timestamp() > 0) ? "AND kr.completed_timestamp <= " + mythicSeason.getEnd_timestamp() : "") +
                    " ORDER BY " +
                    "    kr.keystone_level DESC " +
                    "LIMIT 100 " +
                    ") ex " +
                    "LIMIT 3;";
            return Response.ok(loadKeys(query, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
        } catch (DataException | SQLException e) {
            Logs.errorLog(this.getClass(), "FATAL - error MythicPlus best season ["+ season +"] "+ e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/weekRuns")
    @Produces(MediaType.APPLICATION_JSON)
    public Response weekRuns(@DefaultValue("en_US") @QueryParam("locale") String locale) {
        try {
            String query =
                    "SELECT DISTINCT " +
                    "    ex.id " +
                    "FROM ( " +
                    "    SELECT " +
                    "        kr.id " +
                    "    FROM " +
                    "        keystone_dungeon_run_members km " +
                    "    LEFT JOIN keystone_dungeon_run kr ON km.keystone_dungeon_run_id = kr.id " +
                    "    LEFT JOIN guild_roster gr ON km.character_id = gr.character_id " +
                    "WHERE " +
                    "    gr.character_id IS NOT NULL " +
                    "    AND kr.completed_timestamp >= "+ ServerTime.getLastResetTime() +" " +
                    "ORDER BY " +
                    "    kr.keystone_level DESC " +
                    ") ex;";

            return Response.ok(loadKeys(query, locale).toString(), MediaType.APPLICATION_JSON_TYPE).build();
        } catch (SQLException | DataException e) {
            Logs.errorLog(this.getClass(), "FATAL - error MythicPlus week season "+ e);
            return Response.serverError().build();
        }
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
        runDetail.addProperty("complete_date", new Date(runOb.getCompleted_timestamp()).toString());
        runDetail.addProperty("is_completed_within_time", runOb.isIs_completed_within_time());
        runDetail.addProperty("map_id", runOb.getDungeon().getId());
        runDetail.addProperty("map_name", runOb.getDungeon().getName(locale));

        // Prepare time
        runDetail.addProperty("upgrade_key", runOb.getUpgradeKey());
        int[] duration = runOb.getTimeDuration();
        runDetail.addProperty("duration_h", duration[0]);
        runDetail.addProperty("duration_m", duration[1]);
        runDetail.addProperty("duration_s", duration[2]);

        JsonArray members = new JsonArray();
        for (MythicDungeonMember member : runOb.getMembers()) {
            JsonObject mem = new JsonObject();
            mem.addProperty("name", member.getCharacter().getName());
            mem.addProperty("class", member.getCharacter().getInfo().getPlayableClass().getId());
            mem.addProperty("spec", member.getCharacter().getActiveSpec().getPlayableSpec().getId());
            mem.addProperty("rol", member.getCharacter().getActiveSpec().getPlayableSpec().getRole().getType());
            mem.addProperty("iLvl", member.getCharacter_item_level());
            mem.addProperty("is_guildMember", "??");
            mem.addProperty("realm", member.getCharacter().getRealm().getName(locale));

            members.add(mem);
        }
        runDetail.add("members", members);

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