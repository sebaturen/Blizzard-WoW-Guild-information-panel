package com.blizzardPanel.update.blizzard;

public abstract class BlizzardAPI {

    public static final int maxHourRequest = 36000;
    public static final int maxSecondRequest = 100;

    protected WoWAPIService apiCalls;

    protected BlizzardAPI(WoWAPIService apiCalls) {
        this.apiCalls = apiCalls;
    }
}
