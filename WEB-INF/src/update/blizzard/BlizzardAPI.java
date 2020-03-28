package com.blizzardPanel.update.blizzard;

public abstract class BlizzardAPI {

    protected WoWAPIService apiCalls;

    protected BlizzardAPI(WoWAPIService apiCalls) {
        this.apiCalls = apiCalls;
    }
}
