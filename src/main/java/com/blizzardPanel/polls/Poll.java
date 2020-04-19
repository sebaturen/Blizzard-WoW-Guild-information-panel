package com.blizzardPanel.polls;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.guilds.GuildRank;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Poll {

    // Poll DB
    public static final String TABLE_NAME = "polls";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private int id;
    private int owner_user_id;
    private String question;
    private int min_rank;
    private boolean multi_select;
    private boolean can_add_more_option;
    private long start_date;
    private long end_date;
    private boolean is_enabled;
    private boolean is_hide;

    // Internal DATA
    private User owner;
    private GuildRank minRank;
    private List<PollOption> options;

    public static class Builder extends DBLoadObject {

        private int id;
        public Builder(int pollId) {
            super(TABLE_NAME, Poll.class);
            this.id = pollId;
        }

        public Poll build() {
            Poll newPoll = (Poll) load(TABLE_KEY, id);

            // Load internal data
            newPoll.minRank = new GuildRank.Builder(newPoll.min_rank).build();
            newPoll.owner = new User.Builder(newPoll.owner_user_id).build();
            newPoll.loadOptions();

            return newPoll;
        }
    }

    // Constructor
    private Poll() {

    }

    private void loadOptions() {
        options = new ArrayList<>();
        try {
            JsonArray pollOptions = DBLoadObject.dbConnect.select(
                    PollOption.TABLE_NAME,
                    new String[]{PollOption.TABLE_KET},
                    "poll_id = ?",
                    new String[]{id+""}
            );

            for(JsonElement optDet : pollOptions) {
                options.add(new PollOption.Builder(optDet.getAsJsonObject().get("id").getAsInt()).build());
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a poll options ["+ id +"] - "+ e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getQuestion() {
        return question;
    }

    public GuildRank getMinRank() {
        return minRank;
    }

    public boolean isMulti_select() {
        return multi_select;
    }

    public boolean isCan_add_more_option() {
        return can_add_more_option;
    }

    public long getStart_date() {
        return start_date;
    }

    public long getEnd_date() {
        return end_date;
    }

    public boolean isIs_enabled() {
        return is_enabled;
    }

    public List<PollOption> getOptions() {
        if (options == null) {
            loadOptions();
        }
        return options;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Poll\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"owner_user_id\":\"" + owner_user_id + "\"" + ", " +
                "\"question\":" + (question == null ? "null" : "\"" + question + "\"") + ", " +
                "\"min_rank\":\"" + min_rank + "\"" + ", " +
                "\"multi_select\":\"" + multi_select + "\"" + ", " +
                "\"can_add_more_option\":\"" + can_add_more_option + "\"" + ", " +
                "\"start_date\":\"" + start_date + "\"" + ", " +
                "\"end_date\":\"" + end_date + "\"" + ", " +
                "\"is_enabled\":\"" + is_enabled + "\"" + ", " +
                "\"is_hide\":\"" + is_hide + "\"" + ", " +
                "\"owner\":" + (owner == null ? "null" : owner) + ", " +
                "\"minRank\":" + (minRank == null ? "null" : minRank) + ", " +
                "\"options\":" + (options == null ? "null" : Arrays.toString(options.toArray())) +
                "}";
    }
}
