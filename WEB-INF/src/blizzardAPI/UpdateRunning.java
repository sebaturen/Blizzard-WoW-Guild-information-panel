/**
 * File : UpdateRunning.java
 * Desc : Run player information every X time
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class UpdateRunning implements ServletContextListener 
{
	private Thread t = null;
	private ServletContext context;
	private int count = 0;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent)
	{
		System.out.println("im run?!!!");
		t =  new Thread(){
			//task
			public void run()
			{
				DBConnect dbConn = new DBConnect();
				try {
					while(true)
					{
						dbConn.insert("jdbc_test", new String[] {"username", "email"}, new String[] {"Test", t.getId() +"v: "+ count});
						System.out.println("Thread running every second "+ count);
						count++;
						Thread.sleep(1000);
					}
				} catch (Exception ex) {
				}
			}
		};
		//t.start();
		context = contextEvent.getServletContext();
		// you can set a context variable just like this
		context.setAttribute("TEST", count);
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent)
	{
		// context is destroyed interrupts the thread
		t.interrupt();
	}
}