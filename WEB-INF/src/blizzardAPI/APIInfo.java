/**
 * File : APIInfo.java
 * Desc : Blizzard API and Guild Information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.blizzardAPI;

public interface APIInfo 
{    
    //Aplication Info
    public static final String CLIENT_ID        = "";
    public static final String CLIENT_SECRET    = "";
    
    //-------------------------
    //API Acces
    //-------------------------        
    //-----------------------------------------------------------API Information
    public static final String API_OAUTH_URL            = "https://%s.battle.net/oauth/%s";
    public static final String API_OAUTH_AUTHORIZE      = "authorize";
    public static final String API_OAUTH_TOKEN          = "token";
    public static final String API_OAUTH_USERINFO       = "userinfo";
    public static final String API_ROOT_URL             = "https://%s.api.blizzard.com/%s"; //location, api path
    public static final String API_CHARACTER_RENDER_URL = "https://render-%s.worldofwarcraft.com/character/%s"; //{region}, {character.thumbnail}
    public static final String API_ITEM_RENDER_URL      = "https://render-%s.worldofwarcraft.com/icons/%s/%s"; //{region}, {size 56}, {item icon}
    public static final String RAIDER_IO_API_URL        = "https://raider.io/api/guilds/%s/%s/%s"; //location, server, guild name

    //------------------------------------------World of Warcraft Community APIs
    //Guild Profile API
    public static final String API_GUILD_PROFILE = "wow/guild/%s/%s";        //realm, guildName
    //Character Profile API    
    public static final String API_CHARACTER_PROFILE = "wow/character/%s/%s";    //realm, characterName
    //Data Resources
    public static final String API_CHARACTER_RACES      = "wow/data/character/races";
    public static final String API_GUILD_ACHIEVEMENTS   = "wow/data/guild/achievements";
    public static final String API_CHARACTER_ACHIVEMENTS = "wow/data/character/achievements";
    //Spell API
    public static final String API_SPELL = "wow/spell/%s"; //spell id
    //Boss API
    public static final String API_BOSS_MASTER_LIST = "wow/boss/";
    //Item API
    public static final String API_ITEM = "wow/item/%s"; //{item id}
    //Auction API
    public static final String API_AUCTION = "wow/auction/data/%s"; //{realm}
    
    //------------------------------------------World of Warcraft Game Data APIs
    //Playable Class API
    public static final String API_PLAYABLE_CLASS = "data/wow/playable-class/";
    //WoW Token API
    public static final String API_WOW_TOKEN = "data/wow/token/";
    
    //-----------------------------------------------------------Community OAuth
    //Profile API
    public static final String API_WOW_OAUTH_PROFILE = "wow/user/characters";
}