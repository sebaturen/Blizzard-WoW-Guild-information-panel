<%@include file="includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - <fmt:message key="label.mythic_plus" /></title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">
        <script src="assets/js/mythic_plus.js"></script>
    </head>
    <body> <!-- style="background-color: #01073d;" -->
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div class="affixes row key_title">
                <div class="col weekDivider">
                    <h3><fmt:message key="label.current_affixes" /></h3>
                    <div id="currentAffixes" class="row"></div>
                </div>
                <div class="col">
                    <h3><fmt:message key="label.next_affixes" /></h3>
                    <div id="nextAffixes" class="row"></div>
                </div>
            </div>
            <div id="bestRun" style="display: none;">
                <div class="key_title">
                    <h1><fmt:message key="label.best_runs" /></h1>
                    <h3 clsdd="key_divide_title">(<fmt:message key="label.season" /> 4)</h3>
                </div>
            </div>
            <div id="runList" style="display: none;">
                <div class="key_title">
                    <h1 class="key_divide_title"><fmt:message key="label.runs_of_the_week" /></h1>
                </div>
            </div>
            <div id="loading" class="justify-content-md-center"><div class="loader"></div></div>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>