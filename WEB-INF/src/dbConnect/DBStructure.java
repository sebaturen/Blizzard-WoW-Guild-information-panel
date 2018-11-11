/**
 * File : DBStructure.java
 * Desc : All Game object database structure
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.dbConnect;

public interface DBStructure 
{
    //Update interval
    public static final String UPDATE_INTERVAL_TABLE_NAME = "update_timeline";
    public static final String UPDATE_INTERVAL_TABLE_KEY = "id";
    public static final String[] UPDATE_INTERVAL_TABLE_STRUCTURE = {"id", "type", "update_time"};
    
    //Guild
    public static final String GUILD_TABLE_NAME = "guild_info";
    public static final String GUILD_TABLE_KEY = "id";
    public static final String[] GUILD_TABLE_STRUCTURE = {"id", "name", "realm","lastModified", "battlegroup", 
                                                        "level", "side", "achievementPoints"};
    
    //Members - id - name    
    public static final String GMEMBER_ID_NAME_TABLE_NAME = "gMembers_id_name";
    public static final String GMEMBER_ID_NAME_TABLE_KEY = "internal_id";
    public static final String[] GMEMBER_ID_NAME_TABLE_STRUCTURE = {"internal_id", "member_name", "realm", 
                                                                    "rank", "in_guild", "user_id"};
    
    //Character information
    public static final String CHARACTER_INFO_TABLE_NAME = "character_info";
    public static final String CHARACTER_INFO_TABLE_KEY = "internal_id";
    public static final String[] CHARACTER_INFO_TABLE_STRUCTURE = {"internal_id", "battlegroup", "class",
                                                                    "race", "gender", "level", "achievementPoints",
                                                                    "thumbnail", "calcClass", "faction", "totalHonorableKills",
                                                                    "guild_name", "lastModified"};
    
    //Spells
    public static final String SPELLS_TABLE_NAME = "spells";
    public static final String SPELLS_TABLE_KEY = "id";
    public static final String[] SPELLS_TABLE_STRUCTURE = {"id", "name", "icon", "description",
                                                           "castTime", "cooldown", "range"};
            
    //Specs  
    public static final String SPECS_TABLE_NAME = "specs";
    public static final String SPECS_TABLE_KEY = "id";
    public static final String[] SPECS_TABLE_STRUCTURE = {"id", "member_id", "name", "role", "enable",
                                                            "tier_0", "tier_1", "tier_2",
                                                            "tier_3", "tier_4", "tier_5",
                                                            "tier_6"};
    
    //Challenges

    public static final String CHALLENGES_TABLE_NAME = "challenges";
    public static final String CHALLENGES_TABLE_KEY = "id";
    public static final String[] CHALLENGES_TABLE_STRUCTURE = {"id", "map_name",
                                                        "bronze_hours", "bronze_minutes", "bronze_seconds", "bronze_milliseconds",
                                                        "silver_hours", "silver_minutes", "silver_seconds", "silver_milliseconds",
                                                        "gold_hours", "gold_minutes", "gold_seconds", "gold_milliseconds"};
    
    //Challenge Groups
    public static final String CHALLENGE_GROUPS_TABLE_NAME = "challenge_groups";
    public static final String CHALLENGE_GROUPS_TABLE_KEY = "group_id";
    public static final String[] CHALLENGE_GROUPS_TABLE_STRUCTURE = {"group_id", "challenge_id", "time_date",
                                                                     "time_hours", "time_minutes", "time_seconds",
                                                                     "time_milliseconds", "is_positive"};
    
    //Challenge group Members
    public static final String CHALLENGE_GROUP_MEMBERS_TABLE_NAME = "challenge_group_members";
    public static final String CHALLENGE_GROUP_MEMBERS_TABLE_KEY = "member_in_group_id";
    public static final String[] CHALLENGE_GROUP_MEMBERS_TABLE_STRUCTURE = {"member_in_group_id", "internal_member_id",
                                                                            "group_id", "spec_id"};
    
    //Races 
    public static final String RACES_TABLE_NAME = "races";
    public static final String RACES_TABLE_KEY = "id";
    public static final String[] RACES_TABLE_STRUCTURE = {"id", "mask", "side", "name"};
                
    //Playable Class
    public static final String PLAYABLE_CLASS_TABLE_NAME = "playable_class";
    public static final String PLAYABLE_CLASS_TABLE_KEY = "id";
    public static final String[] PLAYABLE_CLASS_TABLE_STRUCTURE = {"id", "en_US"};
               
    //Guild Achivements lists
    public static final String GUILD_ACHIVEMENTS_LISTS_TABLE_NAME = "guild_achievements_list";
    public static final String GUILD_ACHIVEMENTS_LISTS_TABLE_KEY = "id";
    public static final String[] GUILD_ACHIVEMENTS_LISTS_TABLE_STRUCTURE = {"id", "title", "description",
                                                                            "icon", "points", "classification"};
    
    //Player Achivements category
    public static final String PLAYER_ACHIVEMENTS_CATEGORY_TABLE_NAME = "player_achivement_category";
    public static final String PLAYER_ACHIVEMENTS_CATEGORY_TABLE_KEY = "id";
    public static final String[] PLAYER_ACHIVEMENTS_CATEGORY_TABLE_STRUCTURE = {"id", "name", "father_id"};
    
    //Player Achivements list
    public static final String PLAYER_ACHIVEMENTS_LIST_TABLE_NAME = "player_achivement_list";
    public static final String PLAYER_ACHIVEMENTS_LIST_TABLE_KEY = "id";
    public static final String[] PLAYER_ACHIVEMENTS_LIST_TABLE_STRUCTURE = {"id", "category_id", "title", 
                                                                            "points", "description", "icon"};
    
    //Wow Token
    public static final String WOW_TOKEN_TABLE_NAME = "wow_token";
    public static final String WOW_TOKEN_TABLE_KEY = "last_updated_timestamp";
    public static final String[] WOW_TOKEN_TABLE_STRUCTURE = {"last_updated_timestamp", "price"};
    
    //User
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_TABLE_KEY = "id";
    public static final String[] USER_TABLE_STRUCTURE = {"id","email", "password", "battle_tag", "access_token", "guild_rank", "wowinfo"};
    
    //In many time, we need insert out to id, because the id is auto update.
    public static String[] outKey(String[] array)
    {
        String[] outArray = new String[array.length-1];
        for(int i = 1; i < array.length; i++)
        {
            outArray[i-1] = array[i];
        }
        return outArray;
    }
               
}
