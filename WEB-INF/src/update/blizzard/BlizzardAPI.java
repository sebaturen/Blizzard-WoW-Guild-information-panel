package com.blizzardPanel.update.blizzard;

public abstract class BlizzardAPI {

    protected WoWAPIService apiCalls;

    public BlizzardAPI(WoWAPIService apiCalls) {
        this.apiCalls = apiCalls;
    }
}
