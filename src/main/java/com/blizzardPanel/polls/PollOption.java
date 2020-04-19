package com.blizzardPanel.polls;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollOption {

    // Poll Option DB
    public static final String TABLE_NAME = "poll_options";
    public static final String TABLE_KET = "id";

    // DB Attribute
    private int id;
    private int poll_id;
    private String option;
    private int owner_id;
    private long create_timestamp;

    // Internal DATA
    private User owner;
    private List<PollOptionResult> results;

    public static class Builder extends DBLoadObject {

        private int id;
        public Builder(int pollOptionId) {
            super(TABLE_NAME, PollOption.class);
            this.id = pollOptionId;
        }

        public PollOption build() {
            PollOption newPollOption = (PollOption) load(TABLE_KET, id);

            // Load internal data:
            newPollOption.owner = new User.Builder(newPollOption.owner_id).build();
            newPollOption.loadResults();

            return newPollOption;
        }
    }

    // Constructor
    private PollOption() {

    }

    private void loadResults() {
        results = new ArrayList<>();
        try {
            JsonArray optResult = DBLoadObject.dbConnect.select(
                    PollOptionResult.TABLE_NAME,
                    new String[]{PollOptionResult.TABLE_KET},
                    "poll_option_id = ?",
                    new String[]{id+""}
            );

            for(JsonElement resDet : optResult) {
                results.add(new PollOptionResult.Builder(resDet.getAsJsonObject().get("id").getAsInt()).build());
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a poll result [opt:"+ id +"/poll:"+ poll_id +"] - "+ e);
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

    public String getOption() {
        return option;
    }

    public User getOwner() {
        return owner;
    }

    public long getCreate_timestamp() {
        return create_timestamp;
    }

    public List<PollOptionResult> getResults() {
        if (results == null) {
            loadResults();
        }
        return results;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"PollOption\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"poll_id\":\"" + poll_id + "\"" + ", " +
                "\"option\":" + (option == null ? "null" : "\"" + option + "\"") + ", " +
                "\"owner_id\":\"" + owner_id + "\"" + ", " +
                "\"create_timestamp\":\"" + create_timestamp + "\"" + ", " +
                "\"owner\":" + (owner == null ? "null" : owner) + ", " +
                "\"results\":" + (results == null ? "null" : Arrays.toString(results.toArray())) +
                "}";
    }

}
