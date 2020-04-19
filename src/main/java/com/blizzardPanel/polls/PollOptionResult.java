package com.blizzardPanel.polls;

import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;

public class PollOptionResult {

    // Poll option result
    public static final String TABLE_NAME = "poll_option_result";
    public static final String TABLE_KET = "id";

    // DB Attribute
    private int id;
    private int poll_option_id;
    private int owner_id;
    private long timestamp;

    // Internal DATA
    private User owner;

    public static class Builder extends DBLoadObject {

        private int id;
        public Builder(int pollOptionResultId) {
            super(TABLE_NAME, PollOptionResult.class);
            this.id = pollOptionResultId;
        }

        public PollOptionResult build(){
            PollOptionResult newPollOptionResult = (PollOptionResult) load(TABLE_KET, id);

            // Load internal data:
            newPollOptionResult.owner = new User.Builder(newPollOptionResult.owner_id).build();

            return newPollOptionResult;
        }
    }

    // Constructor
    private PollOptionResult() {

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

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"PollOptionResult\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"poll_option_id\":\"" + poll_option_id + "\"" + ", " +
                "\"owner_id\":\"" + owner_id + "\"" + ", " +
                "\"timestamp\":\"" + timestamp + "\"" + ", " +
                "\"owner\":" + (owner == null ? "null" : owner) +
                "}";
    }
}
