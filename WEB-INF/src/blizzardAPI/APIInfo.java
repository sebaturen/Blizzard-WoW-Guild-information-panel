/**
 * File : APIInfo.java
 * Desc : Blizzard API and Guild Information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

public interface APIInfo 
{
	//Server information
	public static final String SERVER_LOCATION	= "us";
	public static final String GUILD_NAME 		= "Art of War";
	public static final String GUILD_REALM	 	= "Ragnaros";
	
	//API Information
	public static final String API_OAUTH_TOKEN_URL	= "https://%s.battle.net/oauth/token";
	public static final String API_ROOT_URL			= "https://%s.api.blizzard.com/%s"; //location, api path
	
	//Aplication Info
	public static final String CLIENT_ID		= "";
	public static final String CLIENT_SECRET	= "";
	
	//-------------------------
	//API Acces
	//-------------------------
	//World of Warcraft Community APIs
	public static final String API_GUILD_PROFILE	 = "wow/guild/%s/%s"; //realm, guildName
	public static final String API_CHARACTER_PROFILE = "wow/character/%s/%s"; //realm, characterName
	public static final String API_CHARACTER_RACES	 = "wow/data/character/races";
	//World of Warcraft Game Data APIs
	public static final String API_PLAYABLE_CLASS	 = "data/wow/playable-class/";
}