<% if (!guildMember) {%>
    <%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("events.jsp", "UTF-8"));
} else { %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<%@ page import ="java.util.Date" %>
<%@ page import ="com.blizzardPanel.events.Event" %>
<%= (user.getGuildRank() == 0 || user.getGuildRank() == 1)? "<a href='userpanel/event_create.jsp' class='right'><button type='submit' class='btn btn-outline-warning btn-sm'>Create Event</button></a><br><br>":"" %>
<% 
    List<Event> events = eventsControl.getActiveEvents();
    List<Event> disableEvent = eventsControl.getDisableEvents(); 
    List<Event> expireEvent = eventsControl.getExpireEvents();
    if(events.size() > 0) { %>
        <h1 class='key_title key_divide_title'>Events</h1>
        <table class="table table-dark events-tab">
            <thead>
                <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Description</th>
                    <th scope="col">Organizer</th>
                    <th scope="col">Total attendees</th>
                    <th scope="col">Date</th>
                    <% if (user.getGuildRank() == 0 || user.getGuildRank() == 1) { %>
                        <th scope="col">Action</th>
                    <% } %>
                </tr>
            </thead>
            <tbody>
                <% for(Event e : events) { 
                    if ( 
                        e.getMinRank() != null && 
                        (user.getGuildRank() <= (e.getMinRank().getId()))
                    ) { 
                        User owner = e.getOwner(); %>
                    <tr>
                        <td><a href="?ev=<%= e.getId() %>"><%= e.getTitle() %></a></td>
                        <td><%= e.getDesc() %></td>
                        <td><span class="character-<%= (owner.getMainCharacter() != null)? owner.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (owner.getMainCharacter() != null)? owner.getMainCharacter().getName():user.getBattleTag().split("#")[0] %></span></td>
                        <td><%= e.totalAssist() %></td>
                        <td><%= e.getStringDate() %></td>
                        <% if (user.getGuildRank() == 0 || user.getGuildRank() == 1) { %>
                            <td><button type="button" onclick='disableEvent(<%= e.getId() %>)' class="disable_event btn btn-outline-primary" data-event_id="<%= e.getId() %>">Disable</button></td>
                        <% } %>
                    </tr>
                    <% } /* end if rank */ %>
                <% } /* end foreach events */%>
            </tbody>
        </table>
    <% } /*end iff events size > 0*/
    if(disableEvent.size() > 0) { %>
        <h1 class='key_title key_divide_title'>Disable Events</h1>
        <table class="table table-dark events-tab">
            <thead>
                <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Description</th>
                    <th scope="col">Organizer</th>
                    <th scope="col">Total attendees</th>
                    <th scope="col">Date</th>
                    <% if (user.getGuildRank() == 0 || user.getGuildRank() == 1) { %>
                        <th scope="col">Action</th>
                    <% } %>
                </tr>
            </thead>
            <tbody>
                <% for(Event e : disableEvent) { 
                    if ( 
                        e.getMinRank() != null && 
                        (user.getGuildRank() <= (e.getMinRank().getId()))
                    ) { %>
                    <tr>
                        <td><a href="?evHistory=<%= e.getId() %>"><%= e.getTitle() %></a></td>
                        <td><%= e.getDesc() %></td>
                        <td><span class="character-<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %></span></td>
                        <td><%= e.totalAssist() %></td>
                        <td><%= e.getStringDate() %></td>
                        <% if (user.getGuildRank() == 0 || user.getGuildRank() == 1) { 
                            Date today = new Date(); %>
                            <td>
                                <% if (today.before(e.getDate())) { %><button type="button" onclick='enableEvent(<%= e.getId() %>)' class="enable_event btn btn-outline-warning">Enable</button><% } %>
                                <button type="button" onclick='removeEvent(<%= e.getId() %>)' class="remove_event btn btn-outline-danger" data-event_id="<%= e.getId() %>">Remove</button>
                            </td>
                        <% } %>
                    </tr>
                    <% } /* end if rank */ %>
                <% } /* end foreach events */%>
            </tbody>
        </table>
    <% } /*end iff disable event size > 0*/
    if(expireEvent.size() > 0) { %>
        <h1 class='key_title key_divide_title'>Expire Events</h1>
        <table class="table table-dark events-tab">
            <thead>
                <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Description</th>
                    <th scope="col">Organizer</th>
                    <th scope="col">Total attendees</th>
                    <th scope="col">Date</th>
                    <% if (user.getGuildRank() == 0 || user.getGuildRank() == 1) { %>
                        <th scope="col">Action</th>
                    <% } %>
                </tr>
            </thead>
            <tbody>
                <% for(Event e : expireEvent) { 
                    if ( 
                        e.getMinRank() != null && 
                        (user.getGuildRank() <= (e.getMinRank().getId()))
                    ) { %>
                    <tr>
                        <td><a href="?evHistory=<%= e.getId() %>"><%= e.getTitle() %></a></td>
                        <td><%= e.getDesc() %></td>
                        <td><span class="character-<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %></span></td>
                        <td><%= e.totalAssist() %></td>
                        <td><%= e.getStringDate() %></td>
                        <% if (user.getGuildRank() == 0 || user.getGuildRank() == 1) { 
                            Date today = new Date(); %>
                            <td>
                                <% if (today.before(e.getDate())) { %><button type="button" onclick='enableEvent(<%= e.getId() %>)' class="enable_event btn btn-outline-warning">Enable</button><% } %>
                                <button type="button" onclick='removeEvent(<%= e.getId() %>)' class="remove_event btn btn-outline-danger" data-event_id="<%= e.getId() %>">Remove</button>
                            </td>
                        <% } %>
                    </tr>
                    <% } /* end if rank */ %>
                <% } /* end foreach events */%>
            </tbody>
        </table>
    <% } /*end iff disable event size > 0*/ %>
<%} /* first if */%>