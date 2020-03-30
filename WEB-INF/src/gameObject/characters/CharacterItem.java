/**
 * File : ItemMember.java
 * Desc : ItemMember Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject2;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.StaticInformation;

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
    private String stats;
    private int armor;
    private int azerite_level;
    private long media_id;

    // Internal DATA
    private Item item;
    private StaticInformation slot;
    private StaticInformation quality;
    private Media media;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long cItemId) {
            super(TABLE_NAME, CharacterItem.class);
            this.id = cItemId;
        }

        public CharacterItem build() {
            CharacterItem newItem = (CharacterItem) load(TABLE_KEY, id);

            // Load info
            newItem.item = new Item.Builder(newItem.item_id).build();
            newItem.slot = new StaticInformation.Builder(newItem.slot_type).build();
            newItem.quality = new StaticInformation.Builder(newItem.quality_type).build();
            newItem.media = new Media.Builder(newItem.media_id).build();

            return newItem;
        }
    }

    // Constructor
    private CharacterItem() {

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
                "\"stats\": \"STATUS\", " + //(stats == null ? "null" : "\"" + stats + "\"") + ", " +
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