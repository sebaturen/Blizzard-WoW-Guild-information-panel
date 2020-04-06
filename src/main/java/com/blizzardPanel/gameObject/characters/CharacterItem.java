/**
 * File : ItemMember.java
 * Desc : ItemMember Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.StaticInformation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CharacterItem {

    // Item Character DB
    public static final String TABLE_NAME  = "character_items";
    public static final String TABLE_KEY   = "id";

    // Attribute
    private long id;
    private long character_id;
    private String slot_type;
    private long item_id;
    private String quality_type;
    private int level;
    private JsonArray stats;
    private int armor;
    private int azerite_level;
    private long media_id;

    // Internal DATA
    private Item item;
    private StaticInformation slot;
    private StaticInformation quality;
    private Media media;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long cItemId) {
            super(TABLE_NAME, CharacterItem.class);
            this.id = cItemId;
        }

        public CharacterItem build() {
            CharacterItem newItem = (CharacterItem) load(TABLE_KEY, id);

            // Load info
            newItem.item = new Item.Builder(newItem.item_id).build();
            newItem.quality = new StaticInformation.Builder(newItem.quality_type).build();
            newItem.slot = new StaticInformation.Builder(newItem.slot_type).build();

            if (newItem.media_id > 0) {
                newItem.media = new Media.Builder(Media.type.ITEM, newItem.media_id).build();
            }

            return newItem;
        }
    }

    // Constructor
    private CharacterItem() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public int getAzerite_level() {
        return azerite_level;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"CharacterItem\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"slot_type\":" + (slot_type == null ? "null" : "\"" + slot_type + "\"") + ", " +
                "\"item_id\":\"" + item_id + "\"" + ", " +
                "\"quality_type\":" + (quality_type == null ? "null" : "\"" + quality_type + "\"") + ", " +
                "\"level\":\"" + level + "\"" + ", " +
                "\"stats\":" + (stats == null ? "null" : stats) + ", " +
                "\"armor\":\"" + armor + "\"" + ", " +
                "\"azerite_level\":\"" + azerite_level + "\"" + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"item\":" + (item == null ? "null" : item) + ", " +
                "\"slot\":" + (slot == null ? "null" : slot) + ", " +
                "\"quality\":" + (quality == null ? "null" : quality) + ", " +
                "\"media\":" + (media == null ? "null" : media) +
                "}";
    }
}