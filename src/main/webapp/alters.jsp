<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("alters.jsp", "UTF-8"));
}%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - <fmt:message key="label.alters" /></title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">
        <script src="assets/js/alters.js"></script>
    </head>
    <body style="background-color: #030317;">
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="loading" class="justify-content-md-center"><div class="loader"></div></div>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>