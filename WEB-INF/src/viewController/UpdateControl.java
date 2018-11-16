/**
 * File : UpdateControl.java
 * Desc : Update controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.Logs;
import com.artOfWar.blizzardAPI.Update;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/broadcasting")
public class UpdateControl 
{
    private static boolean isRunnin = false;
    
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        synchronized (clients) 
        {
            // Iterate over the connected sessions
            // and broadcast the received message    
            for (Session client : clients) {
                client.getBasicRemote().sendText(message);
            }      
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        // Add session to the connected sessions set
        clients.add(session);
        if(isRunnin)
        {
            try {
                onMessage("Update is in progress.....", null);
            } catch (IOException ex) {
                System.out.println("Fail to send update is progress messaje "+ ex);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        // Remove session from the connected sessions set
        clients.remove(session);
    }
    
    public void runUpdate()
    {        
        if(!isRunnin)
        {
            Logs.publicLog(this);
            generateUpdate();            
        }
    }
    
    private void generateUpdate()
    {   
        Thread upThread = new Thread() {
            @Override
            public void run()
            {        
                setIsRuning(true);
                try {
                    Update up = new Update();
                    up.updateDynamicAll();
                } catch (IOException | ParseException | DataException ex) {
                    Logs.saveLog("fail update...");
                }
                setIsRuning(false);               
                Logs.publicLog(null);
            }
        };
        upThread.start();
    }  
    
    private void setIsRuning(boolean f) { isRunnin = f; } 
}
