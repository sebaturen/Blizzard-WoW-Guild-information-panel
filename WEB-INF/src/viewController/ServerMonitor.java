/**
 * File : ServerMonitor.java
 * Desc : Server Monitor controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.Logs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/serverMonitor")
public class ServerMonitor 
{
    private static boolean isRunnin = false;
    private Process htopProcess;
        
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        synchronized (clients) 
        {
            // Iterate over the connected sessions
            // and broadcast the received message  
            System.out.println("Msg is send!");
            for (Session client : clients) {
                client.getBasicRemote().sendText(message);
            }      
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        // Add session to the connected sessions set
        System.out.println("Session is open!");
        clients.add(session);
        /*if(isRunnin)
        {
            try {
                session.getBasicRemote().sendText("TOP is .....");
            } catch (IOException ex) {
                Logs.saveLogln("Fail to send update if is progress messaje "+ ex);
            }
        }*/
    }

    @OnClose
    public void onClose(Session session) {
        // Remove session from the connected sessions set
        clients.remove(session);
        if(clients.isEmpty())
            closeSee();
    }
    
    public void runSee()
    {
        if(!isRunnin)
        {
            System.out.println("Run!");
            isRunnin = true;
            
            Thread upThread = new Thread() {
                @Override
                public void run()
                {        
                    System.out.println("Run 1@");
                    String s;
                    try {
                        String[] cmdline = { "sh", "-c", "echo", "q | htop | aha --black --line-fix" }; 
                        System.out.println("Creando proceso....");
                        htopProcess = Runtime.getRuntime().exec(cmdline);
                        System.out.println("Proces is ejecutado");
                        BufferedReader br = new BufferedReader(new InputStreamReader(htopProcess.getInputStream()));
                        System.out.println("Leyendo contenido");
                        while ((s = br.readLine()) != null)
                        {
                            
                            onMessage(s, null);
                            System.out.println("line: " + s);
                        }
                        htopProcess.waitFor();
                        System.out.println ("exit: " + htopProcess.exitValue());
                        htopProcess.destroy();
                    } catch (Exception e) {
                        System.out.println("Error > "+ e);
                        Logs.saveLogln("Fail to see HTOP server - "+ e);
                    }
                }
            };
            upThread.start();            
        }
    }
    
    public void closeSee()
    {
        if(this.htopProcess != null)
            htopProcess.destroy();
    }
    
}
