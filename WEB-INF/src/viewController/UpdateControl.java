/**
 * File : UpdateControl.java
 * Desc : Update controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.Update;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
    private static boolean runUpdate = false;
    private static String[] updArg;

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
                session.getBasicRemote().sendText("Update is in progress.....");
            } catch (IOException ex) {
                Logs.errorLog(UpdateControl.class, "Fail to send update if is progress messaje "+ ex);
            }
        }
        else if (runUpdate)
        {
            Logs.setUpdateControl(this);
            generateUpdate();
        }
    }

    @OnClose
    public void onClose(Session session) {
        // Remove session from the connected sessions set
        clients.remove(session);
    }

    public void runUpdate(String[] args)
    {
        if(!isRunnin)
        {
            runUpdate = true;
            updArg = args;
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
                    up.setUpdate(updArg);
                } catch (IOException | ParseException | DataException ex) {
                    Logs.errorLog(UpdateControl.class, "fail update...");
                }
                setIsRuning(false);
                setRunUpdate(false);
                Logs.setUpdateControl(null);
            }
        };
        upThread.start();
    }


    public void messageForAll(String msg) {
        try {
            onMessage(msg, null);
        } catch (IOException ex) {
            Logs.errorLog(UpdateControl.class, "Fail to send update msg");
        }
    }


    private void setIsRuning(boolean f) { isRunnin = f; }
    private void setRunUpdate(boolean f) { runUpdate = f; }
}
