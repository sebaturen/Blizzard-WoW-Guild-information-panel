<%@include file="includes/globalObject.jsp" %>
<% if (user == null || !user.checkUser()) {
    response.sendRedirect("index.jsp");
} else { %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Blizzard link account</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <% if (user.setAccessCode(request.getParameter("code"))) {
                response.sendRedirect("login.jsp");
            } else {
                out.write("ERROR! when try save your blizzard information!");
            }%>
        </div>
    </body>
</html>
<%}%>
            