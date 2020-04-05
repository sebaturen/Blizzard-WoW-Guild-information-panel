package com.blizzardPanel.update.blizzard;

public enum UpdateType {
    GUILD("GUILD"),
    GUILD_ACTIVITIES("GUILD_ACTIVITIES"),
    WOW_TOKEN("WOW_TOKEN"),
    AUCTION("AUCTION"),
    CLEAR_AH_HISTORY("CLEAR_AH_HISTORY"),
    PLAYABLE_CLASS("PLAYABLE_CLASS"),
    MYTHIC_KEYSTONE_SEASON("MYTHIC_KEYSTONE_SEASON"),
    FULL_SYNC_ROSTERS("FULL_SYNC_ROSTERS"),
    SPELL("SPELL"),
    @Deprecated
    ALL_DYNAMIC("ALL_DYNAMIC"),
    @Deprecated
    ALL_STATIC("ALL_STATIC"),
    @Deprecated
    UPDATE_TYPE_AUCTION_CHECK("UPDATE_TYPE_AUCTION_CHECK");

    private final String val;
    private UpdateType(String val) {
        this.val = val;
    }

    public String toString() {
        return this.val;
    }
}
