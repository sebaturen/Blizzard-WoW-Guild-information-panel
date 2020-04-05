package com.blizzardPanel.gameObject.guilds.activity;

import com.blizzardPanel.gameObject.StaticInformation;
import com.blizzardPanel.gameObject.journal.encounter.Encounter;
import com.google.gson.JsonObject;

public class GuildActivityEncounter {

    // Attribute
    private Encounter encounter;
    private StaticInformation mode;

    public GuildActivityEncounter(JsonObject detail) {
        long encounterId = detail.getAsJsonObject("encounter").get("id").getAsLong();
        String modeType = detail.getAsJsonObject("mode").get("type").getAsString();

        encounter = new Encounter.Builder(encounterId).build();
        mode = new StaticInformation.Builder(modeType).build();
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public StaticInformation getMode() {
        return mode;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"GuildActivityEncounter\", " +
                "\"encounter\":" + (encounter == null ? "null" : encounter) + ", " +
                "\"mode\":" + (mode == null ? "null" : mode) +
                "}";
    }
}
