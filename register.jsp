<%@include file="includes/globalObject.jsp" %>
<jsp:useBean id="register" class="com.artOfWar.viewController.Register" scope="request" />
<jsp:setProperty name="register" property="*" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">                     
            <% if ( !user.checkUser() && !register.isData() ) { %>
            <form method="post">
                <div class="form-group">
                    <label for="exampleInputEmail1">Email address</label>
                    <input name="email" type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Enter email"/>
                </div>
                <div class="form-group">
                    <label for="exampleInputPassword1">Password</label>
                    <input name="password" type="password" class="form-control" id="exampleInputPassword1" placeholder="Password" />
                </div>
                <input name="register" type="hidden" value="true"/>
                <button type="submit" class="btn btn-primary">Register</button>
            </form>
            <%} else {
                if( register.saveRegister() ) 
                { %>
                    Register is complet, go to login...<br>
                    <a href="/login.jsp"><button type="submit" class="btn btn-primary">Login</button></a>
              <%} else { %>
                    Em... we have an error, pls try again...
              <%}
            }%>
        </div>
    </body>
</html>