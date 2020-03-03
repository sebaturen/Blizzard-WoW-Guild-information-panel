<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    String evUrlCode = "";
    if( request.getParameter("ev") != null && (Integer.parseInt(request.getParameter("ev"))) > 0) {
        evUrlCode = "?ev="+ request.getParameter("ev");
    }
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("events.jsp"+ evUrlCode, "UTF-8"));
} else {%>
<jsp:useBean id="eventsControl" class="com.blizzardPanel.viewController.EventsController" scope="session"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Events</title>
        <%@include file="includes/header.jsp" %>
        <script src="assets/js/events.js"></script>
    </head>
    <body>
        <div id="drag_char" class="item-floting-desc" style="display: none;"></div>
        <%@include file="includes/menu.jsp" %>
        <div id="currentUserInfo" style="display: none;"
             data-user_id="<%= user.getId() %>"
             data-user_show="<%= (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %>"
             data-user_class="<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"
        ></div>
        <div id="ev_container" class="container fill">
            <% if( request.getParameter("ev") != null && (Integer.parseInt(request.getParameter("ev"))) > 0) { /*event detail: */ %>
                <%@include file="userpanel/event_detail.jsp" %>
            <% } else { /* event list */ %>
                <%@include file="userpanel/event_list.jsp" %>
            <% } /*close event list*/ %>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>
<%}%>
