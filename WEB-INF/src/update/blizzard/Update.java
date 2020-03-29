package com.blizzardPanel.update.blizzard;

public class Update {

    // Update interval DB
    public static final String UPDATE_INTERVAL_TABLE_NAME = "update_timeline";
    public static final String UPDATE_INTERVAL_TABLE_KEY = "id";
    public static final String[] UPDATE_INTERVAL_TABLE_STRUCTURE = {"id", "type", "update_time"};

    // Constant
    public static final int UPDATE_TYPE_DYNAMIC = 0;
    public static final int UPDATE_TYPE_STATIC = 1;
    public static final int UPDATE_TYPE_AUCTION = 2;
    public static final int UPDATE_TYPE_CLEAR_AH_HISTORY = 3;
    public static final int UPDATE_TYPE_GUILD_NEWS = 4;
    public static final int UPDATE_TYPE_AUCTION_CHECK = 5;
}
