<%@include file="../includes/globalObject.jsp" %>
<%@ page import ="java.net.URLEncoder" %>
<%
if(!guildMember)
{  
    response.sendRedirect("../login.jsp?rdir="+URLEncoder.encode("userpanel/settings.jsp", "UTF-8"));
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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Settings panel</title>
        <%@include file="../includes/header.jsp" %>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <p>Update all page information (re-load all from Blizzard)</p>
            <a href='blizzard_update/update_panel.jsp'>
                <button type='submit' class='btn btn-outline-warning btn-sm'>Force the Update</button>
            </a>
            <br><br>
            <p>Setting Guild Ranks</p>
            <a href='guildRank/guild_rank.jsp'>
                <button type='submit' class='btn btn-outline-warning btn-sm'>Guild Rank</button>
            </a>
        </div>
        <%@include file="../includes/footer.jsp" %>
    </body>
</html>
<%}/*if is guild leader or officer */}/*if is guild member*/%>