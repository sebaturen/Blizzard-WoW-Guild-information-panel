<%@include file="includes/globalObject.jsp" %>
<jsp:setProperty name="user" property="*" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    <html lang="es">
    <head>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <% if(request.getParameter("logOut") != null && request.getParameter("logOut").equals("true")) {
            session.invalidate();
            response.sendRedirect("login.jsp");
        } %>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">            
            Welcome to <%= guild_info.getName() %><br>            
            <% if (user == null || !user.checkUser()) {%>
                <form method="post">
                    <div class="form-group">
                        <label for="exampleInputEmail1">Email address</label>
                        <input name="email" type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Enter email"/>
                    </div>
                    <div class="form-group">
                        <label for="exampleInputPassword1">Password</label>
                        <input name="password" type="password" class="form-control" id="exampleInputPassword1" placeholder="Password" />
                    </div>
                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
                <br/><a href="register.jsp"><button type="button" class="btn btn-primary btn-sm">Register</button></a>
           <%} else { %>
                <%= (user.getBattleTag() != null)? user.getBattleTag():user.getEmail() %><br/>
                <% if (!user.getWowInfo()) {
                    
                    String baseUrl = String.format(com.artOfWar.blizzardAPI.APIInfo.API_OAUTH_URL, 
                                                com.artOfWar.blizzardAPI.APIInfo.SERVER_LOCATION,
                                                com.artOfWar.blizzardAPI.APIInfo.API_OAUTH_AUTHORIZE);
                    String redirectUri = baseUrl;
                    redirectUri += "?redirect_uri="java.net.URLEncoder.encode(com.artOfWar.blizzardAPI.APIInfo.MAIN_URL+com.artOfWar.blizzardAPI.APIInfo.BLIZZAR_LINK, "UTF-8");
                    redirectUri += "&scope=wow.profile";
                    redirectUri += "&state=%7B%22region%22%3A%22us%22%7D";
                    redirectUri += "&response_type=code";
                    redirectUri += "&client_id=" + com.artOfWar.blizzardAPI.APIInfo.CLIENT_ID;
                %>
                    <a href="<%= redirectUri %>">
                        <button type="button" class="btn btn-primary">Link blizz account</button>
                    </a>
                    <br>
              <%} else {
                    %><button type="button" class="btn btn-outline-danger">Un link blizz account</button><%
                }
                org.json.simple.JSONArray characters = user.getCharacterList();
                if(characters != null)
                {
                    for(int i = 0; i < characters.size(); i++)
                    {
                        org.json.simple.JSONObject infoChar = (org.json.simple.JSONObject) characters.get(i);
                        out.write( infoChar.get("member_name") +"/"+ infoChar.get("realm") +"<br/>");
                    }
                }              
              %>
                
                <form method="post">
                    <input name="logOut" type="hidden" value="true"/>
                    <button type="submit" class="btn btn-primary">Log out</button>
                </form>
           <% } %> 
       </div>
    </body>
</html>