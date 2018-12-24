<%@include file="includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Mythic Plus</title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">
        <script src="assets/js/mythicPlus/mythic_plus.js"></script>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="afixLoad" class="loader"></div>
            <div id="runList"></div>
            <div class="item-floting-desc tooltip-affix">
                <div class="itemDesc tooltipDesc">
                    <p id="afix_name"></p>
                    <p id="afix_desc" class="tooltip-yellow itemSpellDetail"></p>
                </div>
            </div>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>