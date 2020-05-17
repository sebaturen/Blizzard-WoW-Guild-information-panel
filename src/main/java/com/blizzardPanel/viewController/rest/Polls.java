package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.polls.Poll;
import com.blizzardPanel.polls.PollOption;
import com.blizzardPanel.polls.PollOptionResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Date;

@Path("/polls")
public class Polls {

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pollLists(@Context HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getGuild_rank() != -1) {
            JsonObject pollsDetail = new JsonObject();

            try {
                // Enabled polls
                JsonArray pollsEnabled = DBLoadObject.dbConnect.selectQuery(
                        "SELECT " +
                                " id " +
                                "FROM " +
                                "    polls " +
                                "WHERE " +
                                "    is_enabled = TRUE " +
                                "    AND is_hide = FALSE " +
                                "    AND IF(end_date IS NOT NULL, IF(UNIX_TIMESTAMP(now()) * 1000 < end_date, 1, 0), 1) = 1"
                );

                JsonArray enabledPolls = new JsonArray();
                for(JsonElement pollE : pollsEnabled) {
                    Poll p = new Poll.Builder(pollE.getAsJsonObject().get("id").getAsInt()).build();
                    if (currentUser.getGuild_rank() != -1 && currentUser.getGuild_rank() <= p.getMinRank().getRank_lvl()) {
                        enabledPolls.add(pollToJson(currentUser, p));
                    }
                }
                pollsDetail.add("enabled", enabledPolls);
            } catch (DataException | SQLException e) {
                Logs.fatalLog(this.getClass(), "FAILED to get enabled poll list "+ e);
            }

            try {
                // Disabled polls
                JsonArray pollsDisabled = DBLoadObject.dbConnect.selectQuery(
                        "SELECT " +
                                " id " +
                                "FROM " +
                                "    polls " +
                                "WHERE " +
                                "    IF (is_enabled = TRUE, IF(UNIX_TIMESTAMP(now()) * 1000 > end_date, 1, 0), 1) = 1 " +
                                "    AND is_hide = FALSE"
                );

                JsonArray disabledPolls = new JsonArray();
                for(JsonElement pollD : pollsDisabled) {
                    Poll p = new Poll.Builder(pollD.getAsJsonObject().get("id").getAsInt()).build();
                    if (currentUser.getGuild_rank() != -1 && currentUser.getGuild_rank() <= p.getMinRank().getRank_lvl()) {
                        disabledPolls.add(pollToJson(currentUser, p));
                    }
                }
                pollsDetail.add("disabled", disabledPolls);
            } catch (DataException | SQLException e) {
                Logs.fatalLog(this.getClass(), "FAILED to get disabled poll list "+ e);
            }

            pollsDetail.add("current_user", ownerDetail(currentUser));

            return Response.ok(pollsDetail.toString(), MediaType.APPLICATION_JSON_TYPE).build();
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @PUT
    @Path("/{poll_id}/{opt_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPollOptionResult(
            @PathParam("poll_id") int pollId,
            @PathParam("opt_id") int optId,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getGuild_rank() != -1) {
            Poll p = new Poll.Builder(pollId).build();
            if (currentUser.getGuild_rank() <= p.getMinRank().getRank_lvl()) {
                for (PollOption opt : p.getOptions()) {
                    if (opt.getId() == optId) {
                        boolean preResult = false;
                        for (PollOptionResult result : opt.getResults()) {
                            if (currentUser.equals(result.getOwner())) {
                                preResult = true;
                            }
                        }
                        if (!preResult) {
                            try {
                                DBLoadObject.dbConnect.insert(
                                        PollOptionResult.TABLE_NAME,
                                        PollOptionResult.TABLE_KET,
                                        new String[]{"poll_option_id", "owner_id", "timestamp"},
                                        new String[]{optId+"", currentUser.getId()+"", new Date().getTime()+""}
                                );
                                return Response.ok().build();
                            } catch (SQLException | DataException e) {
                                Logs.fatalLog(this.getClass(), "FAILED to delete option [p:"+ pollId +"/o:"+ optId +"] "+ e);
                                return Response.serverError().build();
                            }
                        }
                    }
                }
            }
            JsonObject notAuthorize = new JsonObject();
            notAuthorize.addProperty("code", 1);
            notAuthorize.addProperty("msg", "ㄱ.ㄱ");
            return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @DELETE
    @Path("/{poll_id}/{opt_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removePollOptionResult(
            @PathParam("poll_id") int pollId,
            @PathParam("opt_id") int optId,
            @Context HttpServletRequest request
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getGuild_rank() != -1) {
            Poll p = new Poll.Builder(pollId).build();
            if (currentUser.getGuild_rank() <= p.getMinRank().getRank_lvl()) {
                for (PollOption opt : p.getOptions()) {
                    if (opt.getId() == optId) {
                        for (PollOptionResult result : opt.getResults()) {
                            if (currentUser.equals(result.getOwner())) {
                                try {
                                    DBLoadObject.dbConnect.delete(
                                            PollOptionResult.TABLE_NAME,
                                            "poll_option_id = ? AND owner_id = ?",
                                            new String[]{optId+"", currentUser.getId()+""}
                                    );
                                    return Response.ok().build();
                                } catch (SQLException | DataException e) {
                                    Logs.fatalLog(this.getClass(), "FAILED to delete option [p:"+ pollId +"/o:"+ optId +"] "+ e);
                                    return Response.serverError().build();
                                }
                            }
                        }
                    }
                }
            }
            JsonObject notAuthorize = new JsonObject();
            notAuthorize.addProperty("code", 1);
            notAuthorize.addProperty("msg", "ㄱ.ㄱ");
            return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @DELETE
    @Path("/{poll_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removePoll(
            @PathParam("poll_id") int pollId,
            @Context HttpServletRequest request
    ){
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getGuild_rank() != -1) {
            if (currentUser.getGuild_rank() == 0 || currentUser.getGuild_rank() == 1) {
                Poll p = new Poll.Builder(pollId).build();
                if (p != null && p.getId() > 0) {
                    try {
                        DBLoadObject.dbConnect.update(
                                Poll.TABLE_NAME,
                                new String[]{"is_hide"},
                                new String[]{"1"},
                                Poll.TABLE_KEY +" = ?",
                                new String[]{pollId+""}
                        );
                        return Response.ok().build();
                    } catch (SQLException | DataException e) {
                        Logs.fatalLog(this.getClass(), "FAILED to delete poll [p:" + pollId + "] " + e);
                        return Response.serverError().build();
                    }
                }
            }
            JsonObject notAuthorize = new JsonObject();
            notAuthorize.addProperty("code", 1);
            notAuthorize.addProperty("msg", "ㄱ.ㄱ");
            return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    @PUT
    @Path("/{poll_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editPoll(
            @PathParam("poll_id") int pollId,
            @Context HttpServletRequest request,
            String data
    ) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getGuild_rank() != -1) {
            if (currentUser.getGuild_rank() == 0 || currentUser.getGuild_rank() == 1) {
                Poll p = new Poll.Builder(pollId).build();
                if (p != null && p.getId() > 0) {
                    try {
                        String action = data.split("action=")[1];
                        switch (action) {
                            case "disabled":
                                DBLoadObject.dbConnect.update(
                                        Poll.TABLE_NAME,
                                        new String[]{"is_enabled"},
                                        new String[]{"0"},
                                        Poll.TABLE_KEY + " = ?",
                                        new String[]{pollId + ""}
                                );
                                return Response.ok().build();
                            case "enabled":
                                DBLoadObject.dbConnect.update(
                                        Poll.TABLE_NAME,
                                        new String[]{"is_enabled"},
                                        new String[]{"1"},
                                        Poll.TABLE_KEY + " = ?",
                                        new String[]{pollId + ""}
                                );
                                return Response.ok().build();
                        }
                    } catch (SQLException | DataException | ArrayIndexOutOfBoundsException e) {
                        Logs.fatalLog(this.getClass(), "FAILED to update poll [p:" + pollId + "] " + e);
                        return Response.serverError().build();
                    }
                }
            }
            JsonObject notAuthorize = new JsonObject();
            notAuthorize.addProperty("code", 1);
            notAuthorize.addProperty("msg", "ㄱ.ㄱ");
            return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
        }
        JsonObject notAuthorize = new JsonObject();
        notAuthorize.addProperty("code", 0);
        notAuthorize.addProperty("msg", "user not login/not guilder member. Action is not authorize");
        return Response.status(403).type(MediaType.APPLICATION_JSON_TYPE).entity(notAuthorize.toString()).build();
    }

    private JsonObject pollToJson(User currentUser, Poll p) {
        JsonObject pDet = new JsonObject();

        pDet.addProperty("id", p.getId());
        String ownerName;
        pDet.add("owner", ownerDetail(p.getOwner()));
        pDet.addProperty("question", p.getQuestion());

        JsonObject config = new JsonObject();
        config.addProperty("min_rank", p.getMinRank().getRank_lvl());
        config.addProperty("multi_select", p.isMulti_select());
        config.addProperty("can_add_more_option", p.isCan_add_more_option());
        config.addProperty("start_date", p.getStart_date());
        config.addProperty("end_date", p.getEnd_date());
        config.addProperty("is_enabled", p.isIs_enabled());
        pDet.add("config", config);

        JsonArray options = new JsonArray();
        int totalResult = 0;
        for(PollOption opt : p.getOptions()) {
            JsonObject optDet = new JsonObject();
            optDet.addProperty("id", opt.getId());
            optDet.addProperty("option", opt.getOption());
            optDet.add("owner", ownerDetail(opt.getOwner()));
            optDet.addProperty("create_timestamp", opt.getCreate_timestamp());
            optDet.addProperty("is_selected", false);

            // Add results
            JsonArray restOpt = new JsonArray();
            for(PollOptionResult rest : opt.getResults()) {
                JsonObject restDet = new JsonObject();
                restDet.addProperty("id", rest.getId());
                restDet.add("owner", ownerDetail(rest.getOwner()));
                restDet.addProperty("timestamp", rest.getTimestamp());

                restOpt.add(restDet);

                // Is select for current user
                if (currentUser.equals(rest.getOwner())) {
                    optDet.addProperty("is_selected", true);
                }
                totalResult++;
            }
            optDet.add("results", restOpt);

            options.add(optDet);
        }
        pDet.addProperty("total_result", totalResult);
        pDet.add("options", options);

        return pDet;
    }

    private JsonObject ownerDetail(User u) {
        String ownerName;
        String type;
        long ownerColor = 0L;
        if (u.getMainCharacter() == null) {
            ownerName = u.getBattle_tag().split("#")[0];
            type = "BATTLE_TAG";
        } else {
            ownerName = u.getMainCharacter().getName();
            ownerColor = u.getMainCharacter().getInfo().getClass_id();
            type = "CHARACTER";
        }

        JsonObject ownDetail = new JsonObject();
        ownDetail.addProperty("id", u.getId());
        ownDetail.addProperty("guild_rank", u.getGuild_rank());
        ownDetail.addProperty("name", ownerName);
        ownDetail.addProperty("color", ownerColor);
        ownDetail.addProperty("type", type);

        return ownDetail;
    }
}
