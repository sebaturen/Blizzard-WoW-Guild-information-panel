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
                <% if (user.getBattleTag() == null) {%>
                    <a href="https://us.battle.net/oauth/authorize?redirect_uri=<%= java.net.URLEncoder.encode(com.artOfWar.blizzardAPI.APIInfo.MAIN_URL+com.artOfWar.blizzardAPI.APIInfo.BLIZZAR_LINK, "UTF-8") %>&scope=wow.profile&state=%7B%22region%22%3A%22us%22%7D&response_type=code&client_id=9a30069bb8254369abffe72ea2d8758c">
                        <button type="button" class="btn btn-primary">Link blizz account</button>
                    </a>
                    <br>
              <%}%>
                <form method="post">
                    <input name="logOut" type="hidden" value="true"/>
                    <button type="submit" class="btn btn-primary">Log out</button>
                </form>
           <% } %> 
       </div>
    </body>
</html>