<%@include file="../includes/globalObject.jsp" %>
<% 
if (!request.getParameter("login_redirect").equals("true") || !user.checkUser()) 
{
    response.sendRedirect("index.jsp");
} 
else //only show content if is redirect from login.jsp and the user is valid
{
%>
<%@ page import ="com.artOfWar.gameObject.Member" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <%@include file="../includes/header.jsp" %>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <%= (user.getBattleTag() != null)? user.getBattleTag():user.getEmail() %><br/>
            <%  //Blizzard account vinculation
                if (!user.getWowInfo()) {                    
                    String baseUrl = String.format(com.artOfWar.blizzardAPI.APIInfo.API_OAUTH_URL, 
                                                com.artOfWar.blizzardAPI.APIInfo.SERVER_LOCATION,
                                                com.artOfWar.blizzardAPI.APIInfo.API_OAUTH_AUTHORIZE);
                    String redirectUri = baseUrl;
                    redirectUri += "?redirect_uri="+ java.net.URLEncoder.encode(com.artOfWar.blizzardAPI.APIInfo.MAIN_URL+com.artOfWar.blizzardAPI.APIInfo.BLIZZAR_LINK, "UTF-8");
                    redirectUri += "&scope=wow.profile";
                    redirectUri += "&state=%7B%22region%22%3A%22us%22%7D";
                    redirectUri += "&response_type=code";
                    redirectUri += "&client_id=" + com.artOfWar.blizzardAPI.APIInfo.CLIENT_ID;
                %>
                    <a href="<%= redirectUri %>">
                        <button type="button" class="btn btn-primary">Link blizz account</button>
                    </a><br><br>
              <%} else {
                    %><button type="button" class="btn btn-outline-danger btn-sm">Un-link blizz account</button><br><br><%
                }
                //User character info~
                if(user.getWowInfo())
                {%>
                    <table class="table table-dark character-tab">
                        <thead>
                            <tr>
                                <th scope="col">Name</th>
                                <th scope="col">Spec</th>
                                <th scope="col">Level</th>
                                <th scope="col">Server</th>
                                <th scope="col"></th> 
                            </tr>
                        </thead>
                        <tbody>
                          <%for(Member m : user.getCharacterList()) { %>
                            <tr>
                                <td class="character-<%= (m.getmemberClass().getEnName()).replaceAll("\\s+","") %>"><%= m.getName() %></td>
                                <% //Get img from speck
                                String className = ((m.getmemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase();
                                String specName = ((m.getActiveSpec().getName()).replaceAll("\\s+","-")).toLowerCase();
                                %>
                                <td><img src="assets/img/classes/specs/spec_<%= className %>_<%= specName %>.png" style="width: 22px;"/></td>
                                <td><%= m.getLevel() %></td>
                                <td><%= m.getRealm() %></td>
                                <td><img src="assets/img/icons/Logo-<%= (m.getFaction() == 0)? "alliance":"horde" %>.png" style="width: 22px;"/></td>
                            </tr>
                          <%}%>
                        </tbody>
                    </table>
              <%}%>
                <form method="post">
                    <input name="logOut" type="hidden" value="true"/>
                    <button type="submit" class="btn btn-primary">Log out</button>
                </form>                
        </div>
    </body>
</html>
<%}%>
    
    
    