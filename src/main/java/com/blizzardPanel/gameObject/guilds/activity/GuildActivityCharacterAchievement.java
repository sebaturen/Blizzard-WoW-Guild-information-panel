package com.blizzardPanel.gameObject.guilds.activity;

import com.blizzardPanel.gameObject.achievements.Achievement;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.google.gson.JsonObject;

public class GuildActivityCharacterAchievement {

    // Attribute
    private CharacterMember characterMember;
    private Achievement achievement;

    public GuildActivityCharacterAchievement(JsonObject detail) {
        long characterId = detail.getAsJsonObject("character").get("id").getAsLong();
        long achievementId = detail.getAsJsonObject("achievement").get("id").getAsLong();

        characterMember = new CharacterMember.Builder(characterId).build();
        achievement = new Achievement.Builder(achievementId).build();
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public CharacterMember getCharacterMember() {
        return characterMember;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"GuildActivityCharacterAchievement\", " +
                "\"characterMember\":" + (characterMember == null ? "null" : characterMember) + ", " +
                "\"achievement\":" + (achievement == null ? "null" : achievement) +
                "}";
    }
}
