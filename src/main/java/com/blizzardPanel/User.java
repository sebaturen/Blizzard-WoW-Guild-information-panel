package com.blizzardPanel;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.guilds.GuildRank;
import com.blizzardPanel.gameObject.guilds.GuildRoster;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.viewController.GuildController;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {

    // User DB
    public static final String TABLE_NAME = "users";
    public static final String TABLE_KEY = "id";
    public static final String TABLE_NAME_VPN = "user_vpn";
    public static final String TABLE_KEY_VPN = "id";
    // Character user DB
    public static final String USER_CHARACTER_TABLE_NAME = "user_character";
    public static final String USER_CHARACTER_TABLE_KEY = "id";

    // DB Attribute
    private int id;
    private String battle_tag;
    private String access_token;
    private long discord_user_id;
    private long main_character_id;
    private String vpn_ip;
    private long last_login;
    private long last_alters_update;
    private Boolean is_guild_member = false;
    private int guild_rank = -1;

    // Internal DATA
    private CharacterMember mainCharacter;
    private List<CharacterMember> characters;
    private List<CharacterMember> invalidCharacters;

    public static class Builder extends DBLoadObject {

        private int id;
        private String code;
        private String battleTag;
        private String ipAddrs;

        public Builder(int userId) {
            super(TABLE_NAME, User.class);
            this.id = userId;
        }
        public Builder(String battleTag) {
            super(TABLE_NAME, User.class);
            this.battleTag = battleTag;
        }
        public Builder(String userIP, boolean falseValue) {
            super(TABLE_NAME, User.class);
            this.ipAddrs = userIP;
        }
        public Builder setCode(String code) {
            this.code = code;
            return this;
        }
        public Builder() {
            super(TABLE_NAME, User.class);
        }

        public User build() {
            User newUser = null;

            if (id > 0) {
                newUser = (User) load(TABLE_KEY, id);
            }
            if (battleTag != null) {
                newUser = (User) load("battle_tag", battleTag);
            }
            if (ipAddrs != null) {
                newUser = (User) load("vpn_ip", ipAddrs);
            }
            if (code != null) {
                String accessToken = BlizzardUpdate.shared.getUserAccessToken(this.code);
                if (accessToken != null) {
                    String battleTag = BlizzardUpdate.shared.getBattleTag(accessToken);
                    if (battleTag != null) {
                        // Check if user previews exist
                        try {
                            JsonArray userExist = BlizzardUpdate.dbConnect.select(
                                    User.TABLE_NAME,
                                    new String[]{"battle_tag"},
                                    "battle_tag =?",
                                    new String[]{battleTag}
                            );
                            if (userExist.size() > 0) {
                                newUser = new User.Builder(battleTag).build();
                            }
                        } catch (SQLException | DataException e) {
                            Logs.fatalLog(this.getClass(), "FAILED to load a user from battletag ["+ battleTag +"] - "+ e);
                        }
                        // Save a new user
                        if (newUser == null) {
                            newUser = new User();
                            newUser.battle_tag = battleTag;
                            newUser.access_token = accessToken;
                            newUser.id = BlizzardUpdate.shared.saveUser(newUser);
                        }
                        newUser.access_token = accessToken;
                        newUser.updateCharacters();
                    }
                }
            }

            // Load info
            if (newUser != null) {
                newUser.loadMainCharacter();
            }

            return newUser;
        }
    }

    // Constructor
    public User() {

    }

    private void loadMainCharacter() {
        if (main_character_id > 0) {
            mainCharacter = new CharacterMember.Builder(main_character_id).build();
        }
    }

    private void loadCharacters() {
        characters = new ArrayList<>();
        invalidCharacters = new ArrayList<>();
        try {
            JsonArray chars_db = DBLoadObject.dbConnect.selectQuery(
                    "SELECT " +
                    "    c.id " +
                    "FROM " +
                    "    user_character uc, " +
                    "    users u, " +
                    "    `characters` c " +
                    "    LEFT JOIN character_info ci ON c.id = ci.character_id " +
                    "WHERE " +
                    "    uc.character_id = c.id " +
                    "    AND uc.user_id = "+ id +" " +
                    "    AND u.id = uc.user_id " +
                    "ORDER BY " +
                    "    CASE WHEN c.id = u.main_character_id then 0 else 1 end, " +
                    "    c.is_valid = TRUE DESC, " +
                    "    ci. `level` DESC"
            );
            int guildRank = -1;
            boolean isGuildMember = false;
            for(JsonElement charDB : chars_db) {
                // Add Characters for a user
                CharacterMember newC = new CharacterMember.Builder(charDB.getAsJsonObject().get("id").getAsLong()).build();
                if (newC.isIs_valid()) {
                    characters.add(newC);
                } else {
                    invalidCharacters.add(newC);
                }

                // Check if is a guild member
                if (newC.getInfo().getGuild_id() == GuildController.getInstance().getId()
                && !isGuildMember) {
                    isGuildMember = true;
                }

                // Set a Guild rank
                JsonArray roster_db = DBLoadObject.dbConnect.select(
                        GuildRoster.TABLE_NAME,
                        new String[]{GuildRoster.TABLE_KEY},
                        "character_id = ?",
                        new String[]{newC.getId()+""}
                );
                if (roster_db.size() > 0) {
                    GuildRoster roster = new GuildRoster.Builder(newC.getId()).build();
                    if (roster != null && (guildRank == -1 || guildRank > roster.getGuildRank().getRank_lvl())) {
                        guildRank = roster.getGuildRank().getRank_lvl();
                    }
                }
            }
            if (guildRank != guild_rank || isGuildMember != is_guild_member) {
                guild_rank = guildRank;
                is_guild_member = isGuildMember;
                BlizzardUpdate.shared.saveUser(this);
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to load characters for a user ["+ battle_tag +"] "+ e);
        }
    }

    private void updateCharacters() {
        BlizzardUpdate.shared.accountProfileAPI.summary(this);
        if (!is_guild_member) {
            loadCharacters();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getBattle_tag() {
        return battle_tag;
    }

    public String getAccess_token() {
        return access_token;
    }

    public boolean isLogin() {
        return (id > 0);
    }

    public int getId() {
        return id;
    }

    public int getGuild_rank() {
        return guild_rank;
    }

    public Boolean getIs_guild_member() {
        return is_guild_member;
    }

    public List<CharacterMember> getCharacters() {
        loadCharacters();
        return characters;
    }

    public CharacterMember getMainCharacter() {
        if (mainCharacter == null) {
            loadMainCharacter();
        }
        return mainCharacter;
    }

    public long getDiscord_user_id() {
        return discord_user_id;
    }

    public void copy(User u) {
        id = u.id;
        battle_tag = u.battle_tag;
        access_token = u.access_token;
        discord_user_id = u.discord_user_id;
        guild_rank = u.guild_rank;
        main_character_id = u.main_character_id;
        last_login = u.last_login;
        last_alters_update = u.last_alters_update;
        mainCharacter = u.mainCharacter;
        characters = u.characters;
        is_guild_member = u.is_guild_member;
        vpn_ip = u.vpn_ip;
    }

    public long getLast_alters_update() {
        return last_alters_update;
    }

    public long getLast_login() {
        return last_login;
    }

    public boolean setMainCharacter(long charId) {
        for (CharacterMember cm : characters) {
            if (cm.getId() == charId) {
                main_character_id = charId;
                mainCharacter = cm;
                BlizzardUpdate.shared.saveUser(this);
                return true;
            }
        }
        return false;
    }

    public String getVpn_ip() {
        return vpn_ip;
    }

    public long getMain_character_id() {
        return main_character_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()) {
            if (id == ((User) obj).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"User\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"battle_tag\":" + (battle_tag == null ? "null" : "\"" + battle_tag + "\"") + ", " +
                "\"access_token\":" + (access_token == null ? "null" : "\"" + access_token + "\"") + ", " +
                "\"discord_user_id\":\"" + discord_user_id + "\"" + ", " +
                "\"main_character_id\":\"" + main_character_id + "\"" + ", " +
                "\"vpn_ip\":" + (vpn_ip == null ? "null" : "\"" + vpn_ip + "\"") + ", " +
                "\"last_login\":\"" + last_login + "\"" + ", " +
                "\"last_alters_update\":\"" + last_alters_update + "\"" + ", " +
                "\"is_guild_member\":" + (is_guild_member == null ? "null" : "\"" + is_guild_member + "\"") + ", " +
                "\"guild_rank\":\"" + guild_rank + "\"" + ", " +
                "\"mainCharacter\":" + (mainCharacter == null ? "null" : mainCharacter) + ", " +
                "\"characters\":" + (characters == null ? "null" : Arrays.toString(characters.toArray())) + ", " +
                "\"invalidCharacters\":" + (invalidCharacters == null ? "null" : Arrays.toString(invalidCharacters.toArray())) +
                "}";
    }
}
