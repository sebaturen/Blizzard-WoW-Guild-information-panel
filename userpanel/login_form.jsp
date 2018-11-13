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
                <div class="col-6">
                    <div class="form-group">
                            <label for="exampleInputEmail1">Email address</label>
                            <input name="email" type="email" class="form-control" id="inputEmail" aria-describedby="emailHelp" placeholder="Enter email"/>
                        </div>
                    <div class="form-group">
                        <label for="exampleInputPassword1">Password</label>
                        <input name="noCryptPassword" type="password" class="form-control" id="inputPass" placeholder="Password" />
                    </div>
                    <form method="post" id="accesInfo">
                        <input name="email" type="hidden" id="email"/>
                        <input name="password" type="hidden" id="password"/>
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </form><br>
                    <% if (user.getTryLogin()) {%>
                    <div class="alert alert-danger" role="alert">
                        <strong>Account error!</strong> Your login information not is correct, try again!.
                    </div>
                    <%}%>
                    <a href="register.jsp"><button type="button" class="btn btn-primary btn-sm">Register</button></a>
                </div>
            </div>
        </div>
    </body>
</html>
<%%>
    
    
    