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
			blizzUp.updateAllNow();
		} 
		catch (IOException|ParseException|DataException ex)
		{
			System.out.println("Cant create a Data Update Object! "+ ex);
		}
	}
}