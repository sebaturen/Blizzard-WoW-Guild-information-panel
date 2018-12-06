<%@include file="../includes/globalObject.jsp" %>
<% 
if (    request.getParameter("login_redirect") == null || 
        !request.getParameter("login_redirect").equals("true") || 
        !user.checkUser()) 
{
    response.sendRedirect("../login.jsp");
} 
else //only show content if is redirect from login.jsp and the user is valid
{%>
<%@ page import ="com.blizzardPanel.gameObject.characters.Member" %>
<%@ page import ="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - User Panel</title>
        <%@include file="../includes/header.jsp" %>
        <script src="assets/js/user_panel.js"></script>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <img src="../assets/img/icons/Battlenet_icon_flat.svg" style="width: 40px"><%= user.getBattleTag() %>
            <%= (user.getGuildRank() == 0 || user.getGuildRank() == 1)? "<a href='userpanel/settings.jsp' class='right'><button type='submit' class='btn btn-outline-warning btn-sm'>Settings</button></a>":"" %><br/>
            <%  
                if(!user.isCharsReady())
                {
                    out.write("Your characters are loading, come back in a moment (F5?)...");
                    out.write("<br><a href='login.jsp'><button type='button' class='btn btn-info btn-sm'>Reload</button></a>");
                }
                //User character info~
                List<Member> memberChars = user.getCharacters();
                if(memberChars.size() > 0)
                {%>
                    <table class="table table-dark character-tab">
                        <thead>
                            <tr>
                                <th scope="col">Main</th>
                                <th scope="col">Name</th>
                                <th scope="col">Spec</th>
                                <th scope="col">Level</th>
                                <th scope="col">Server</th>
                                <th scope="col"></th>
                            </tr>
                        </thead>
                        <tbody>
                          <%for(Member m : memberChars) { %>
                            <tr>
                                <% //Get img from speck
                                String className = ((m.getMemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase();
                                String specName = ((m.getActiveSpec().getName()).replaceAll("\\s+","-")).toLowerCase();
                                String mainClassCode = (m.isMain())? "&#xe801;":"&#xe800;";
                                %>
                                <td><i class="main_char artOfWar-icon pointer" data-member_id="<%= m.getId() %>"><%= mainClassCode %></i></td>
                                <td class="character-<%= className %>"><%= m.getName() %></td>
                                <td><img src="assets/img/classes/specs/spec_<%= className %>_<%= specName %>.png" style="width: 22px;"/></td>
                                <td><%= m.getLevel() %></td>
                                <td><%= m.getRealm() %></td>
                                <td><img src="assets/img/icons/Logo-<%= (m.getFaction() == 0)? "alliance":"horde" %>.png" style="width: 22px;"/></td>
                            </tr>
                          <%}//end foreach member m%>
                        </tbody>
                    </table>
              <%} //close if members character more 0%>
            <form method="post">
                <input name="logOut" type="hidden" value="true"/>
                <button type="submit" class="btn btn-primary">Log out</button>
            </form>                
        </div>
    </body>
</html>
<%}%>
    
    
    