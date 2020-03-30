package com.blizzardPanel.update.blizzard;

public class BlizzardAPIError {

    private int code;
    private String type;
    private String detail;

    public BlizzardAPIError() {
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"BlizzardAPIError\", " +
                "\"code\":\"" + code + "\"" + ", " +
                "\"type\":" + (type == null ? "null" : "\"" + type + "\"") + ", " +
                "\"detail\":" + (detail == null ? "null" : "\"" + detail + "\"") +
                "}";
    }
}
