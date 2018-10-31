/**
 * File : UpdateRunningCrontab.java
 * Desc : Force update a guild information
 * THIS FILE NEED PUT IN CRONTAB EVERY HOUR!
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.DataException;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;

public class UpdateRunningCrontab
{
	public static void main(String[] args)
	{
		try 
		{
			Update blizzUp = new Update();
			int upParam = Update.DYNAMIC_UPDATE;
			if(args.length > 0) upParam = Update.STATIC_UPDATE;
			
			switch(upParam)
			{
				case Update.DYNAMIC_UPDATE:
					blizzUp.updateDynamicAll();
					break;
				case Update.STATIC_UPDATE:
					blizzUp.updateStaticAll();					
					break;					
			}
		} 
		catch (IOException|ParseException|DataException ex)
		{
			System.out.println("Cant create a Data Update Object! "+ ex);
		}
	}
}