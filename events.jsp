<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("events.jsp", "UTF-8"));
} else {%>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<%@ page import = "com.blizzardPanel.events.Event" %>
<jsp:useBean id="eventsControl" class="com.blizzardPanel.viewController.EventsController" scope="session"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Events</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div id="currentUserInfo" style="display: none;"
             data-user_id="<%= user.getId() %>"
             data-user_show="<%= (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %>"
             data-user_class="<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"
        ></div>
        <div class="container fill">
            <%  List<Event> events = eventsControl.getEvents();
                if(events.size() > 0) {
                    for(Event e : events) {%>
                    hola 1
                <%} /*end foreach events*/ } /*end iff events size > 0*/ %>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>
<%}%>
