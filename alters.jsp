<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("alters.jsp", "UTF-8"));
} else {%>
<%@ page import ="com.blizzardPanel.User" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.Member" %>
<%@ page import ="java.util.List" %>
<jsp:useBean id="alters" class="com.blizzardPanel.viewController.Alters"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Alters</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <% if (alters.getUsers() != null) { 
                for(User u : alters.getUsers()) { %>
                    <img src="../assets/img/icons/Battlenet_icon_flat.svg" style="width: 40px"><%= u.getBattleTag().split("#")[0] %>
                <%  //User character info~
                    List<Member> memberChars = u.getCharacters();
                    if(memberChars.size() > 0)
                    {%>
                        <table class="table table-dark character-tab">
                            <tbody>
                              <%for(Member m : memberChars) { if (m.isGuildMember() ) {%>
                                <tr>
                                    <% //Get img from speck
                                    String className = m.getMemberClass().getSlug();                                    
                                    String specName = m.getActiveSpec().getSpec().getSlug();
                                    String mainClass = ((m.isMain())? "<i class='artOfWar-icon'>&#xe801;</i>":"");
                                    %>
                                    <td class="character-<%= className %> char-name"><%= mainClass %> <%= m.getName() %></td>
                                    <td class="char-class"><img src="assets/img/classes/specs/spec_<%= className %>_<%= specName %>.png" style="width: 22px;"/></td>
                                    <td><%= m.getLevel() %></td>
                                    <td class="char-name"><%= m.getRank().getTitle() %></td>
                                </tr>
                              <%}/*End if is guild member */ } /*end foreach member m*/%>
                            </tbody>
                        </table>
                  <%} //close if members character more 0%>
            <%  } /* End foreach Users */ } /*End if alters is not null */%>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>
<%}%>