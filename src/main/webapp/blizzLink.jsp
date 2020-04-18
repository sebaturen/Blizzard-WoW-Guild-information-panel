<%@include file="includes/globalObject.jsp" %>
<% if (request.getParameter("code") == null) {
    response.sendRedirect("index.jsp");
} else { %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - Blizzard link account</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="loading" class="justify-content-md-center"><div class="loader"></div></div>
            <%
                user.copy(new User.Builder().setCode(request.getParameter("code")).build());
                if (user != null)  {
                    if (session.getAttribute("internal_redirect") == null) {
                        response.sendRedirect("login.jsp");
                    } else {
                        String dirRed = (String) session.getAttribute("internal_redirect");
                        session.removeAttribute("internal_redirect");
                        response.sendRedirect(dirRed);
                    }
                } else {
                    out.write("ERROR! when try save your blizzard information!");
                }
            %>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>
<%}%>