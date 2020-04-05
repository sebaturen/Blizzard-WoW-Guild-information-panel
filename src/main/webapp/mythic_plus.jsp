<%@include file="includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
<head>
    <title>${guild.name} - <fmt:message key="label.mythic_plus" /></title>
    <%@include file="includes/header.jsp" %>
    <link type="text/css" rel="stylesheet" href="assets/css/index.css">
    <script src="assets/js/mythic_plus.js"></script>
</head>
<body>
<%@include file="includes/menu.jsp" %>
<div class="container fill">
    <div class="fails_keys">
        <a href="/mythic_plus_fail.jsp" class="btn btn-outline-warning"><fmt:message key="label.failed_runs" /></a>
    </div>
    <div id="bestRun" style="display: none;"></div>
    <div id="runList" style="display: none;"></div>
    <div id="loading" class="justify-content-md-center"><div class="loader"></div></div>
    <div class="item-floating-desc tooltip-affix">
        <div class="itemDesc tooltipDesc">
            <p id="affix_name"></p>
            <p id="affix_desc" class="tooltip-yellow itemSpellDetail"></p>
        </div>
    </div>
</div>
<%@include file="includes/footer.jsp" %>
</body>
</html>