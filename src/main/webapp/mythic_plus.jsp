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
            <div id="mythic_run_mold" class="key_run_group dungeon-challenge col" style="display: none">
                <div class='key_run_dun_img dungeon-challenge-img' style=''>
                    <div class='key_run_lvl'></div>
                    <h2 class='key_dung_title'></h2>
                </div>
                <p class='key_group_time'></p>
                <p class='key_date'></p>
                <table class='table table-dark character-tab'>
                    <thead>
                    <tr>
                        <th scope='col'><fmt:message key="label.name" /></th>
                        <th scope='col'><fmt:message key="label.role" /></th>
                        <th scope='col'><fmt:message key="label.ilvl" /></th>
                    </tr>
                    </thead>
                    <tbody class="key_characters">
                        <tr id="key_char_detail">
                            <td class="key_char_name"></td>
                            <td>
                                <img class="key_char_rol_img" src='' style='width: 22px;'/>
                                <img class="key_char_spec_img" rc='' style='width: 22px;'/>
                            </td>
                            <td class="key_char_ilvl"></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="bestRun" style="display: none;">
                <div class="key_title">
                    <h1>Best Runs</h1><br>
                    <h3 clsdd="key_divide_title">(Season 4)</h3>
                </div>
            </div>
            <div id="runList" style="display: none;">
                <div class="key_title">
                    <h1 class="key_divide_title">Best Runs</h1>
                </div>

            </div>
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