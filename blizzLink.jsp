<%@include file="includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <% if (user == null || !user.checkUser()) {%>
                How you stai here.... that is a mistery... go to home!...
            <%} else {
                if (user.setAccessCode(request.getParameter("code"))) {
                    response.sendRedirect("login.jsp");
                } else {
                    out.write("ERROR! when try save your blizzard information!");
                }
            }%>
        </div>
    </body>
</html>
            