/**
 * File : UpdateRunning.java
 * Desc : Run player information every X time
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.DataException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;

public class UpdateRunning implements ServletContextListener 
{
	private Thread t = null;
	private ServletContext context;
	private int count = 0;
	private Update blizzUp;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent)
	{
		System.out.println("Update Running is runing!");
		t =  new Thread(){
			//task
			public void run()
			{
				try 
				{
					blizzUp = new Update();
					while(true)
					{
						blizzUp.updateAllNow();
						//Wait 1h...
						Thread.sleep(1000 * 60 * 60);
					}
				} 
				catch (IOException|ParseException|DataException ex)
				{
					System.out.println("Cant create a Data Update Object! "+ ex);
				}
				catch (InterruptedException ex)
				{
					System.out.println("Thread is break! "+ ex);
				}
			}
		};
		t.start();
		//context = contextEvent.getServletContext();
		// you can set a context variable just like this
		//context.setAttribute("TEST", count);
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent)
	{
		// context is destroyed interrupts the thread
		t.interrupt();
	}
}