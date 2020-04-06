/**
 * File : Item.java
 * Desc : Item object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

public class Item {

    //Item DB
    public static final String TABLE_NAME = "items";
    public static final String TABLE_KEY = "id";

    // Attribute
    private long id;
    private JsonObject name;
    private boolean is_stackable;
    private String quality_type;
    private int level;
    private int required_level;
    private long media_id;
    private String inventory_type;
    private boolean is_equippable;

    // Update control
    private long last_modified;

    // Internal DATA
    private StaticInformation quality;
    private Media media;
    private StaticInformation inventory;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long itemId) {
            super(TABLE_NAME, Item.class);
            this.id = itemId;
        }

        public Item build() {
            Item newItem = (Item) load(TABLE_KEY, id);

            // Load internal data
            newItem.quality = new StaticInformation.Builder(newItem.quality_type).build();
            newItem.inventory = new StaticInformation.Builder(newItem.inventory_type).build();
            if (newItem.media_id > 0) {
                newItem.media = new Media.Builder(Media.type.ITEM, newItem.media_id).build();
            }

            return newItem;
        }
    }

    // Constructor
    private Item() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"Item\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\": \"NAME\", " + //(name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"is_stackable\":\"" + is_stackable + "\"" + ", " +
                "\"quality_type\":" + (quality_type == null ? "null" : "\"" + quality_type + "\"") + ", " +
                "\"level\":\"" + level + "\"" + ", " +
                "\"required_level\":\"" + required_level + "\"" + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"inventory_type\":" + (inventory_type == null ? "null" : "\"" + inventory_type + "\"") + ", " +
                "\"is_equippable\":\"" + is_equippable + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"quality\":" + (quality == null ? "null" : quality) + ", " +
                "\"media\":" + (media == null ? "null" : media) + ", " +
                "\"inventory\":" + (inventory == null ? "null" : inventory) +
                "}";
    }
}
