/**
 * File : UpdateRunningCrontab.java
 * Desc : Force update a guild information
 * THIS FILE NEED PUT IN CRONTAB EVERY HOUR!
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.DataException;
import com.artOfWar.Logs;

import java.io.IOException;
import org.json.simple.parser.ParseException;

public class UpdateRunningCrontab
{
    public static void main(String[] args)
    {
        try 
        {
            Update blizzUp = new Update();
            int upParam = Update.UPDATE_DYNAMIC;
            if(args.length > 0) upParam = Integer.parseInt(args[0]);

            switch(upParam)
            {
                case Update.UPDATE_DYNAMIC:
                    blizzUp.updateDynamicAll();
                    break;
                case Update.UPDATE_STATIC:
                    blizzUp.updateStaticAll();					
                    break;	
                case Update.UPDATE_AUCTION:
                    blizzUp.updateAH();
            }
        } 
        catch (IOException|ParseException|DataException ex)
        {
            Logs.saveLog("Cant create a Data Update Object! "+ ex);
        }
    }
}