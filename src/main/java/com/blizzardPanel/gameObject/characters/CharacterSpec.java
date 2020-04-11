/**
 * File : Spec.java
 * Desc : Speciality object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Spell;
import com.blizzardPanel.gameObject.characters.playable.PlayableSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterSpec {

    //Specs  DB
    public static final String TABLE_NAME = "character_specs";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long character_id;
    private long specialization_id;
    private boolean enable;
    private long tier_0;
    private long tier_1;
    private long tier_2;
    private long tier_3;
    private long tier_4;
    private long tier_5;
    private long tier_6;

    // Internal DATA
    private PlayableSpec playableSpec;
    private List<Spell> tiers = new ArrayList<>();

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long charSpecId) {
            super(TABLE_NAME, CharacterSpec.class);
            this.id = charSpecId;
        }

        public CharacterSpec build() {
            CharacterSpec newCSpec = (CharacterSpec) load(TABLE_KEY, id);

            // Load internal data:
            newCSpec.playableSpec = new PlayableSpec.Builder(newCSpec.specialization_id).build();
            if (newCSpec.tier_0 > 0) {
                newCSpec.tiers.add(new Spell.Builder(newCSpec.tier_0).build());
            }
            if (newCSpec.tier_1 > 0) {
                newCSpec.tiers.add(new Spell.Builder(newCSpec.tier_1).build());
            }
            if (newCSpec.tier_2 > 0) {
                newCSpec.tiers.add(new Spell.Builder(newCSpec.tier_2).build());
            }
            if (newCSpec.tier_3 > 0) {
                newCSpec.tiers.add(new Spell.Builder(newCSpec.tier_3).build());
            }
            if (newCSpec.tier_4 > 0) {
                newCSpec.tiers.add(new Spell.Builder(newCSpec.tier_4).build());
            }
            if (newCSpec.tier_5 > 0) {
                newCSpec.tiers.add(new Spell.Builder(newCSpec.tier_5).build());
            }
            if (newCSpec.tier_6 > 0) {
                newCSpec.tiers.add(new Spell.Builder(newCSpec.tier_6).build());
            }

            return newCSpec;
        }
    }

    // Constructor
    private CharacterSpec() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public long getSpecialization_id() {
        return specialization_id;
    }

    public PlayableSpec getPlayableSpec() {
        return playableSpec;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<Spell> getTiers() {
        return tiers;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"CharacterSpec\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"specialization_id\":\"" + specialization_id + "\"" + ", " +
                "\"enable\":\"" + enable + "\"" + ", " +
                "\"tier_0\":\"" + tier_0 + "\"" + ", " +
                "\"tier_1\":\"" + tier_1 + "\"" + ", " +
                "\"tier_2\":\"" + tier_2 + "\"" + ", " +
                "\"tier_3\":\"" + tier_3 + "\"" + ", " +
                "\"tier_4\":\"" + tier_4 + "\"" + ", " +
                "\"tier_5\":\"" + tier_5 + "\"" + ", " +
                "\"tier_6\":\"" + tier_6 + "\"" + ", " +
                "\"spec\":" + (playableSpec == null ? "null" : playableSpec) + ", " +
                "\"tiers\":" + (tiers == null ? "null" : Arrays.toString(tiers.toArray())) +
                "}";
    }
}