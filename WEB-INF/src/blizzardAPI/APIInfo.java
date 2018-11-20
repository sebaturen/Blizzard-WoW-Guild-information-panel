/**
 * File : APIInfo.java
 * Desc : Blizzard API and Guild Information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

public interface APIInfo 
{
    //Server information
    public static final String SERVER_LOCATION  = "us";
    public static final String GUILD_NAME       = "Art of War";
    public static final String GUILD_REALM      = "Ragnaros";
    //Web main URL
    public static final String MAIN_URL         = "http://artofwar.cl/";
    public static final String BLIZZAR_LINK     = "blizzLink.jsp";

    //API Information
    public static final String API_OAUTH_URL        = "https://%s.battle.net/oauth/%s";
    public static final String API_OAUTH_AUTHORIZE  = "authorize";
    public static final String API_OAUTH_TOKEN      = "token";
    public static final String API_OAUTH_USERINFO   = "userinfo";
    public static final String API_ROOT_URL         = "https://%s.api.blizzard.com/%s"; //location, api path
    public static final String API_CHARACTER_RENDER_URL  = "http://render-%s.worldofwarcraft.com/character/%s"; //{region}, {character.thumbnail}
    public static final String API_ITEM_RENDER_URL       = "http://render-%s.worldofwarcraft.com/icons/%s/%s"; //{region}, {size 56}, {item icon}
    public static final String RAIDER_IO_API_URL         = "https://raider.io/api/guilds/%s/%s/%s"; //location, server, guild name

    //Aplication Info
    public static final String CLIENT_ID        = "";
    public static final String CLIENT_SECRET    = "";

    //-------------------------
    //API Acces
    //-------------------------
    //World of Warcraft Community APIs
    public static final String API_GUILD_PROFILE        = "wow/guild/%s/%s";        //realm, guildName
    public static final String API_CHARACTER_PROFILE    = "wow/character/%s/%s";    //realm, characterName
    public static final String API_CHARACTER_RACES      = "wow/data/character/races";
    public static final String API_GUILD_ACHIVEMENTS    = "wow/data/guild/achievements";
    public static final String API_SPELL                = "wow/spell/%s"; //spell id
    public static final String API_BOSS_MASTER_LIST     = "wow/boss/";
    public static final String API_ITEM                 = "wow/item/%s"; //{item id}
    public static final String API_AUCTION              = "wow/auction/data/%s"; //{realm}
    //World of Warcraft Game Data APIs
    public static final String API_PLAYABLE_CLASS       = "data/wow/playable-class/";
    public static final String API_WOW_TOKEN            = "data/wow/token/";
    //Community OAuth
    public static final String API_WOW_OAUTH_PROFILE    = "wow/user/characters";
}