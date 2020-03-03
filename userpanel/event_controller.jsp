<%@include file="../includes/globalObject.jsp" %>
<%@page import="com.google.gson.JsonObject"%>
<%
    request.setCharacterEncoding("UTF-8");
    JsonObject json = new JsonObject();
    if(guildMember)
    {%>
        <%@ page import ="java.util.ArrayList" %>
        <%@ page import ="java.util.List" %>
        <%@ page import ="java.util.Date" %>
        <%@ page import ="java.text.SimpleDateFormat" %>
        <%@ page import = "com.blizzardPanel.events.Event" %>
        <%@ page import = "com.blizzardPanel.gameObject.characters.CharacterMember" %>
        <jsp:useBean id="eventsControl" class="com.blizzardPanel.viewController.EventsController" scope="session"/><%

        //Load event edit
        String eventAction = request.getParameter("event_action");
        json.addProperty("action", eventAction);

        //Controll event:
        int eventId = -1;
        Event editEvent = null;
        boolean canEdit = false;

        if(eventAction.equals("addMember") ||
            eventAction.equals("removeParticiple") ||
            eventAction.equals("enableEvent") ||
            eventAction.equals("disableEvent") ||
            eventAction.equals("removeEvent"))
        {
            eventId = Integer.parseInt(request.getParameter("val[event_id]"));
            editEvent = eventsControl.getEvent(eventId);
        }

        //Try action
        switch(eventAction)
        {
            case "enableEvent":
                if(user.getGuildRank() == 0 || user.getGuildRank() == 1) canEdit = true;
                if(canEdit) 
                {
                    editEvent.setEnable(true);
                    json.addProperty("status", "ok");
                }
                break;
            case "disableEvent":
                if(user.getGuildRank() == 0 || user.getGuildRank() == 1) canEdit = true;
                if(canEdit) 
                {
                    editEvent.setEnable(false);
                    json.addProperty("status", "ok");
                }
                break;
            case "removeEvent":
                if(user.getGuildRank() == 0 || user.getGuildRank() == 1) canEdit = true;
                if(canEdit) 
                {
                    editEvent.setHide();
                    json.addProperty("status", "ok");
                }
                break;
            case "addMember":
                int mainCharID = Integer.parseInt(request.getParameter("val[main_id]"));
                int mainSpecID = Integer.parseInt(request.getParameter("val[main_spec]"));
                if(mainCharID > 0 && mainSpecID > 0)
                {
                    //Main char detail
                    CharacterMember mainChar = new CharacterMember(mainCharID);
                    //set main spec
                    mainChar.setActiveSpec(mainSpecID);
                    if(mainChar.getUserID() == user.getId())
                    {
                        //while alters...
                        List<CharacterMember> altersChar = new ArrayList<>();
                        int j = 0;
                        boolean failAlter = false;
                        while(request.getParameter("val[alters]["+ j +"][id]") != null)
                        {
                            int altCharID = Integer.parseInt(request.getParameter("val[alters]["+ j +"][id]"));
                            int altCharSpec = Integer.parseInt(request.getParameter("val[alters]["+ j +"][spec]"));
                            CharacterMember altChar = new CharacterMember(altCharID);
                            altChar.setActiveSpec(altCharSpec);
                            if(altChar.getUserID() != user.getId())
                            {
                                failAlter = true;
                                break;
                            }
                            else
                            {
                                altersChar.add(altChar);
                            }
                            j++;
                        }
                        if(!failAlter)
                        {
                            // Set participed in event...
                            if(editEvent.addCharactersFormUser(user, mainChar, altersChar))
                            {
                                json.addProperty("status", "ok");
                            }
                            else
                            {
                                json.addProperty("status", "fail");
                                json.addProperty("msg", "Failed to save in DB");
                            }
                        }
                        else
                        {
                            json.addProperty("status", "fail");
                            json.addProperty("msg", "Error, alters character not is your character");
                        }
                    }
                    else
                    {
                        json.addProperty("status", "fail");
                        json.addProperty("msg", "Error, main character not is your character");
                    }
                }
                else
                {
                    json.addProperty("status", "fail");
                    json.addProperty("msg", "Error, main character or main spect character not is correct");
                }
                break;
            case "removeParticiple":
                if (editEvent.removeParticiple(user)) {
                    json.addProperty("status", "ok");
                } else {
                    json.addProperty("status", "failde");
                    json.addProperty("msg", "Error, to save remove participated");
                }
                break;
            case "createEvent":
                //Create event values
                String eventTitle = null;
                String eventDesc = null;
                String eventDate = null;
                int minLv = -1;
                int rankLv = -1;
                
                //search values
                int i = 0;
                while(request.getParameter("val["+i+"][name]") != null)
                {
                    String valName = request.getParameter("val["+i+"][name]");
                    String valValue = request.getParameter("val["+i+"][value]");

                    //general settings
                    switch(valName)
                    {
                        case "event_title": eventTitle = valValue; break;
                        case "event_desc": eventDesc = valValue; break;
                        case "event_date": eventDate = valValue; break;
                        case "event_lvl": minLv = Integer.parseInt(valValue); break;
                        case "guild_level": rankLv = Integer.parseInt(valValue); break;
                    }
                    i++;
                }

                System.out.println("title "+ eventTitle);
                System.out.println("desc "+ eventDesc);
                System.out.println("date "+ eventDate);
                System.out.println("lvl "+ minLv);
                System.out.println("ranj "+ rankLv);
                if(eventTitle == null || eventDesc == null || eventDate == null || minLv == -1 || rankLv == -1)
                {
                    json.addProperty("status", "fail");
                    json.addProperty("msg", "Error, complete all information");
                } else {
                    System.out.println(eventDate);
                    //Try parse date time:
                    try
                    {
                        eventDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eventDate));
                        Date nowDate = new Date();
                        Date evDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eventDate);
                        if(nowDate.compareTo(evDate) < 0)
                        {
                            //Try save in DB
                            if(eventsControl.newEvent(user, eventTitle, eventDesc, eventDate, minLv, rankLv))
                            {
                                json.addProperty("status", "ok");
                            }
                            else
                            {
                                json.addProperty("status", "fail");
                                json.addProperty("msg", "Failed to save in DB");
                            }
                        }
                        else
                        {
                            json.addProperty("status", "fail");
                            json.addProperty("msg", "Error, the date must be later than the current one");
                        }
                    } catch (java.text.ParseException ex) {
                        json.addProperty("status", "fail");
                        json.addProperty("msg", "Error, `date` not correct");
                    }
                }
                break;
            default:
                json.addProperty("status", "fail");
                json.addProperty("msg", "Acction not found");
                break;

        }
    }
    else
    {
        json.addProperty("status", "fail");
        json.addProperty("msg", "Not user login detected");
    }
    out.print(json);
    out.flush();
%>
