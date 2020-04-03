package com.blizzardPanel.gameObject.mythicKeystones;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MythicDungeonRun {

    // Dungeon Run DB
    public static final String TABLE_NAME = "keystone_dungeon_run";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long completed_timestamp;
    private long duration;
    private int keystone_level;
    private long keystone_dungeon_id;
    private boolean is_completed_within_time;
    private JsonObject key_affixes;

    // Internal Data
    private MythicDungeon dungeon;
    private List<MythicAffix> affixes;
    private List<MythicDungeonMember> members;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long runId) {
            super(TABLE_NAME, MythicDungeonRun.class);
            this.id = runId;
        }

        public MythicDungeonRun build() {
            MythicDungeonRun newRun = (MythicDungeonRun) load(TABLE_KEY, id);

            // Load internal data:
            newRun.dungeon = new MythicDungeon.Builder(newRun.keystone_dungeon_id).build();
            newRun.loadAffixes();
            newRun.loadMembers();

            return newRun;
        }
    }

    // Constructor
    private MythicDungeonRun() {

    }

    public void loadAffixes() {
        affixes = new ArrayList<>();
        key_affixes.keySet().forEach(id -> {
            affixes.add(new MythicAffix.Builder(key_affixes.get(id).getAsInt()).build());
        });
    }

    public void loadMembers() {
        members = new ArrayList<>();
        try {
            JsonArray members_db = DBLoadObject.dbConnect.select(
                    MythicDungeonMember.TABLE_NAME,
                    new String[]{MythicDungeonMember.TABLE_KEY},
                    "keystone_dungeon_run_id=?",
                    new String[]{id+""}
            );

            for (JsonElement member : members_db) {
                JsonObject memberDetail = member.getAsJsonObject();
                members.add(new MythicDungeonMember.Builder(member.getAsJsonObject().get(MythicDungeonMember.TABLE_KEY).getAsInt()).build());
            }
        } catch (SQLException | DataException e) {
            Logs.errorLog(this.getClass(), "FAILED to get a internal members for run ["+ id +"] - "+ e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    public int getKeystone_level() {
        return keystone_level;
    }

    public long getCompleted_timestamp() {
        return completed_timestamp;
    }

    public boolean isIs_completed_within_time() {
        return is_completed_within_time;
    }

    public MythicDungeon getDungeon() {
        return dungeon;
    }

    public List<MythicDungeonMember> getMembers() {
        if (members == null) {
            loadMembers();
        }
        return members;
    }

    public List<MythicAffix> getAffixes() {
        if (affixes == null) {
            loadAffixes();
        }
        return affixes;
    }

    public long getDuration() {
        return duration;
    }

    public int getUpgradeKey() {
        if (dungeon.getKeystone_upgrades_3() >= duration) return 3;
        if (dungeon.getKeystone_upgrades_2() >= duration) return 2;
        if (dungeon.getKeystone_upgrades_1() >= duration) return 1;
        return -1;
    }

    public int[] getTimeDuration() {
        Date timeStart = new Date(0);
        Date time = new Date(this.duration);
        long diff = time.getTime() - timeStart.getTime();
        int[] times = new int[3];
        times[0] = (int) (diff / (60 * 60 * 1000));
        times[1] = (int) (diff / (60 * 1000) % 60);
        times[2] = (int) (diff / 1000 % 60);
        return times;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"MythicDungeonRun\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"completed_timestamp\":\"" + completed_timestamp + "\"" + ", " +
                "\"duration\":\"" + duration + "\"" + ", " +
                "\"keystone_level\":\"" + keystone_level + "\"" + ", " +
                "\"keystone_dungeon_id\":\"" + keystone_dungeon_id + "\"" + ", " +
                "\"is_completed_within_time\":\"" + is_completed_within_time + "\"" + ", " +
                "\"key_affixes\":" + (key_affixes == null ? "null" : key_affixes) + ", " +
                "\"dungeon\":" + (dungeon == null ? "null" : dungeon) + ", " +
                "\"affixes\":" + (affixes == null ? "null" : Arrays.toString(affixes.toArray())) + ", " +
                "\"members\":" + (members == null ? "null" : Arrays.toString(members.toArray())) +
                "}";
    }
}
