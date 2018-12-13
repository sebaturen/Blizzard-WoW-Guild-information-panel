/**
 * File : UpdateRunningCrontab.java
 * Desc : Force update a guild information
 * THIS FILE NEED PUT IN CRONTAB EVERY HOUR!
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.blizzardAPI;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;

import java.io.IOException;
import java.sql.SQLException;
import org.json.simple.parser.ParseException;

public class UpdateRunningCrontab
{
    public static void main(String[] args)
    { 
        try 
        {
            Update blizzUp = new Update();
            int upParam = -1;
            String upInternal = "null";
            if(args.length > 0) upParam = Integer.parseInt(args[0]);
            if(args.length > 1) upInternal = args[1];
            
            switch(upParam)
            {
                case Update.UPDATE_DYNAMIC:
                    switch(upInternal)
                    {
                        case "GuildProfile":        Logs.saveLogln("Guild Profile Update..."); blizzUp.getGuildProfile(); break;
                        case "GuildMembers":        Logs.saveLogln("Guild Members Update...");  blizzUp.getGuildMembers(); break;
                        case "CharacterInfo":       Logs.saveLogln("Character info Update...");  blizzUp.getCharacterInfo(); break;
                        case "GuildChallenges":     Logs.saveLogln("Guild Challenges Update...");  blizzUp.getGuildChallenges(); break;
                        case "GuildNews":           Logs.saveLogln("Guild News Update...");  blizzUp.getGuildNews(); break;
                        case "WowToken":            Logs.saveLogln("Wow Token Update..."); blizzUp.getWowToken(); break;
                        case "UsersCharacters":     Logs.saveLogln("User Characters Update...");  blizzUp.getUsersCharacters(); break;
                        case "GuildProgression":    Logs.saveLogln("Guild Progression Update...");  blizzUp.getGuildProgression(); break;
                        default:                    
                            blizzUp.updateDynamicAll();                        
                    }
                    break;
                case Update.UPDATE_STATIC:
                    switch(upInternal)
                    {
                        case "PlayableClass":               Logs.saveLogln("Playable Class Update...");  blizzUp.getPlayableClass(); break;
                        case "PlayableSpec":                Logs.saveLogln("Playable Spec Update...");  blizzUp.getPlayableSpec(); break;
                        case "PlayableRaces":               Logs.saveLogln("Playable Races Update...");  blizzUp.getPlayableRaces(); break;
                        case "GuildAchievementsLists":      Logs.saveLogln("Guild Achievements Update...");  blizzUp.getGuildAchievementsLists(); break;
                        case "CharacterAchievementsLists":  Logs.saveLogln("Character Achievements Update...");  blizzUp.getCharacterAchievementsLists(); break;
                        case "BossInformation":             Logs.saveLogln("Bosses info Update...");  blizzUp.getBossInformation(); break;
                        case "updateSpellInformation":      Logs.saveLogln("Spells info Update...");  blizzUp.updateSpellInformation(); break;
                        case "updateItemInformation":       Logs.saveLogln("Items info Update...");  blizzUp.updateItemInformation(); break;
                        default:                    
                            blizzUp.updateStaticAll();                      
                    }				
                    break;	
                case Update.UPDATE_AUCTION:
                    blizzUp.updateAH();
                    break;
                case Update.UPDATE_CLEAR_AH_HISTORY:
                    blizzUp.moveHistoryAH();
                    break;                    
                default:
                    Logs.saveLogln("Not update parametter detected!");
                    break;
            }
        } catch (IOException|ParseException|DataException|SQLException | ClassNotFoundException | java.text.ParseException ex) {
            Logs.saveLogln("Fail to update information - "+ ex);
        }
    }
}