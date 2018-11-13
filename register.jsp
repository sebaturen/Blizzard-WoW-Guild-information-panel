<%@include file="includes/globalObject.jsp" %>
<jsp:useBean id="register" class="com.artOfWar.viewController.Register" scope="request" />
<jsp:setProperty name="register" property="*" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Register</title>
        <%@include file="includes/header.jsp" %>
        <script src="assets/js/jquery.md5.js"></script>
        <script src="../assets/js/passMd5Encrypt.js"></script>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">                     
            <div class="row justify-content-md-center">
                <div class="col-6">
                    <% if ( register.saveRegister() ) { %>
                        Register is complet, go to login...<br>
                        <a href="/login.jsp"><button type="submit" class="btn btn-primary">Login</button></a>
                    <%} else { %>
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
                            <button type="submit" class="btn btn-primary">Register</button>
                        </form><br>
                        <% if (register.isDuplicateUser()) {%>
                            <div class="alert alert-danger" role="alert">
                                <strong>Account error!</strong> Your account is already registered, if you don't remember your password try to restore~
                            </div>                   
                        <%}%>
                        <% if ( register.isTryRegist()) {%>
                            <div class="alert alert-warning" role="alert">
                                <strong>Information!</strong> Chech the information is correct.
                            </div> 
                        <%}%> 
                    <%}%>
                </div>
            </div>
        </div>
    </body>
</html>