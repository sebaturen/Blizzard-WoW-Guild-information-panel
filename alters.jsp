<%@include file="includes/globalObject.jsp" %>
<% if (user == null || user.getGuildRank() == -1) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("/login.jsp?rdir="+URLEncoder.encode("/alter.jsp", "UTF-8"));
} else {%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Alters</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            En construccion...
        </div>
    </body>
</html>
<%}%>
    
    
    
    