<%@include file="includes/globalObject.jsp" %>
<jsp:useBean id="register" class="com.artOfWar.viewController.Register" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="welcome">
                Valida tu hash, ingresa la contraseña
                <form method="post">
                    <input name="password" type="password" class="form-control" id="exampleInputPassword1" placeholder="Password" />
                    <button type="submit" class="btn btn-primary">Enviar</button>
                </form>
                <%= (request.getParameter("password") != null)? register.encodePass(request.getParameter("password")):"" %>
            </div>
        </div>
    </body>
</html>