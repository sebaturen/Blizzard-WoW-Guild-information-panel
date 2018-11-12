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
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">            
            <div class="row justify-content-md-center">
                <div class="col-6">
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
                </div>
            </div>
        </div>
    </body>
</html>
<%%>
    
    
    