<%@include file="../includes/globalObject.jsp" %>
<jsp:setProperty name="user" property="*" />
<% 
if (! request.getParameter("login_redirect").equals("true")) 
{
    response.sendRedirect("index.jsp");
} 
else if ( user.checkUser() ) //login is complate and successful
{
    response.sendRedirect("login.jsp");    
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Login</title>
        <%@include file="../includes/header.jsp" %>
        <script src="../assets/js/jquery.md5.js"></script>
        <script src="../assets/js/passMd5Encrypt.js"></script>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <div class="row justify-content-md-center">
                    <% // "redirectUri" is generate in menu.jsp %>
                    <a href="<%= redirectUri %>"> 
                        <button type="button" class="btn btn-primary">Blizzard Login</button>
                    </a><br><br>
            </div>
        </div>
    </body>
</html>
<%%>
    
    
    