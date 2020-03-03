
<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@ page import ="java.text.SimpleDateFormat" %>
<jsp:useBean id="progress" class="com.blizzardPanel.viewController.GuildProgress" scope="application"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Guild Progress</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            10497 -> Name>  Sinestro
            10497 -> Server> Gilneas
            <% 
                CharacterMember cm = new CharacterMember(10497);
                cm.getActiveSpec();
            %>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>