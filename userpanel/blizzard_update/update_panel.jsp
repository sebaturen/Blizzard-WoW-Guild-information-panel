<%@include file="../../includes/globalObject.jsp" %>
<%@ page import ="java.net.URLEncoder" %>
<%
if(!guildMember)
{  
    response.sendRedirect("../../login.jsp?rdir="+URLEncoder.encode("userpanel/blizzard_update/update_panel.jsp", "UTF-8"));
}
else
{
    if(user.getGuildRank() != 0 && user.getGuildRank() != 1)
    {//Validate user is Guild Lider or Officer
        out.write("Only Guild Leader or Officers can access from this page.");
    }
    else
    {  
%>
<%@ page import="java.io.*, java.net.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Update panel</title>
        <%@include file="../../includes/header.jsp" %>
        <script src="../../assets/js/update_panel.js"></script>
    </head>
    <body>
        <%@include file="../../includes/menu.jsp" %>
        <div class="container fill">
            <button id="buttonForceUpdate" type="button" class="btn btn-outline-danger">Run!</button>           
            <div class="returnCode">
                <code id="updateCode" class=""></code>
            </div>
        </div>
    </body>
</html>
<%}/*if is guild leader or officer */}/*if is guild member*/%>