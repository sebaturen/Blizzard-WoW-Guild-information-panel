package com.blizzardPanel.gameObject.journal.encounter;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.StaticInformation;
import com.blizzardPanel.gameObject.journal.instance.Instance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class Encounter {

    // Encounter DB
    public static final String TABLE_NAME = "encounters";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private JsonObject description;
    private JsonArray creatures;
    private long instance_id;
    private String category;
    private JsonArray modes;

    // Update control
    private long last_modified;

    // Internal DATA
    private Instance instance;
    private List<Creature> creatureList;
    private List<StaticInformation> modeList;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long encounterId) {
            super(TABLE_NAME, Encounter.class);
            this.id = encounterId;
        }

        public Encounter build() {
            Encounter newEncounter = (Encounter) load(TABLE_KEY, id);

            // Load internal data
            newEncounter.instance = new Instance.Builder(newEncounter.instance_id).build();

            return newEncounter;
        }
    }

    // Constructor
    private Encounter() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    public Instance getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Encounter\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"description\":" + (description == null ? "null" : description) + ", " +
                "\"creatures\":" + (creatures == null ? "null" : creatures) + ", " +
                "\"instance_id\":\"" + instance_id + "\"" + ", " +
                "\"category\":" + (category == null ? "null" : "\"" + category + "\"") + ", " +
                "\"modes\":" + (modes == null ? "null" : modes) + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"instance\":" + (instance == null ? "null" : instance) + ", " +
                "\"creatureList\":" + (creatureList == null ? "null" : Arrays.toString(creatureList.toArray())) + ", " +
                "\"modeList\":" + (modeList == null ? "null" : Arrays.toString(modeList.toArray())) +
                "}";
    }
}
