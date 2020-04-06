<%@include file="../includes/globalObject.jsp" %>
<%
    if (    request.getParameter("login_redirect") == null ||
            !request.getParameter("login_redirect").equals("true") ||
            !user.isLogin()
    ) {
        response.sendRedirect("../login.jsp");
    }
    else //only show content if is redirect from login.jsp and the user is valid
    {%>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@ page import ="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - User Panel</title>
        <%@include file="../includes/header.jsp" %>
        <script src="assets/js/user_panel.js"></script>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <img src="assets/img/icons/Battlenet_icon_flat.svg" style="width: 40px"><%= user.getBattle_tag() %>
            <c:if test="${user.guildRank == 0 || user.guildRank == 1}">
                <a href='userpanel/settings.jsp' class='right'><button type='submit' class='btn btn-outline-warning btn-sm'><fmt:message key="label.settings" /></button></a>
            </c:if>
            <br/>
            <c:if test="${user.characters.size() == 0}">
                <fmt:message key="label.character_loading" /><br><br>
            </c:if>
            <c:if test="${user.characters.size() != 0}">
                <table class="table table-dark character-tab">
                    <thead>
                    <tr>
                        <th scope="col"><fmt:message key="label.main" /></th>
                        <th scope="col"><fmt:message key="label.name" /></th>
                        <th scope="col"><fmt:message key="label.spec" /></th>
                        <th scope="col"><fmt:message key="label.level" /></th>
                        <th scope="col"><fmt:message key="label.best_run" /></th>
                        <th scope="col"><fmt:message key="label.server" /></th>
                        <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${user.characters}" var="cm">
                        <tr>
                            <td>
                                <i class="main_char artOfWar-icon pointer" data-member_id="${cm.id}">
                                    <c:if test="${user.mainCharacter != null && user.mainCharacter.id == cm.id}">
                                        &#xe801;
                                    </c:if>
                                    <c:if test="${user.mainCharacter == null || user.mainCharacter.id != cm.id}">
                                        &#xe800;
                                    </c:if>
                                </i>
                            </td>
                            <td class="character-${cm.info.character_class_id}">${cm.name}</td>
                            <td>
                                <c:if test="${cm.activeSpec.specialization_id != 1}">
                                    <img src="assets/img/classes/specs/spec_${cm.info.character_class_id}_${cm.activeSpec.specialization_id}.png" style="width: 22px;"/>
                                </c:if>
                            </td>
                            <td>${cm.info.level}</td>
                            <td>
                                <c:if test="${cm.bestMythicRun != null}">
                                    ${cm.bestMythicRun.keystone_level}
                                </c:if>
                            </td>
                            <td>${cm.realm.getName(cookie['locale'].getValue())}</td>
                            <td><img src="assets/img/icons/${cm.info.faction.type}.png" style="width: 22px;"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <fmt:message key="label.select_language" />
            <select class="form-control" id="locale" style="width: auto;">
                <option value="en_US" <%= (locale.equals("en_US"))? "selected":"" %>>English (U.S.)</option>
                <option value="es_MX" <%= (locale.equals("es_MX"))? "selected":"" %>>Spanish (Mexican)</option>
                <option value="pt_BR" <%= (locale.equals("pt_BR"))? "selected":"" %>>Portuguese (Brazilian)</option>
                <option value="de_DE" <%= (locale.equals("de_DE"))? "selected":"" %>>German</option>
                <option value="en_GB" <%= (locale.equals("en_GB"))? "selected":"" %>>English (U.K.)</option>
                <option value="es_ES" <%= (locale.equals("es_ES"))? "selected":"" %>>Spanish (Spain)</option>
                <option value="fr_FR" <%= (locale.equals("fr_FR"))? "selected":"" %>>French</option>
                <option value="it_IT" <%= (locale.equals("it_IT"))? "selected":"" %>>Italian</option>
                <option value="ru_RU" <%= (locale.equals("ru_RU"))? "selected":"" %>>Russian</option>
                <option value="ko_KR" <%= (locale.equals("ko_KR"))? "selected":"" %>>Korean</option>
                <option value="zh_TW" <%= (locale.equals("zh_TW"))? "selected":"" %>>Chinese (Taiwan)</option>
                <option value="zh_CN" <%= (locale.equals("zh_CN"))? "selected":"" %>>Chinese (Simplified)</option>
            </select><br>
            <form method="post">
                <input name="logOut" type="hidden" value="true"/>
                <button type="submit" class="btn btn-primary"><fmt:message key="label.log_out" /></button>
                <div class="discCode">
                    <img src="assets/img/icons/Discord-Logo-Color.svg" style="width: 35px">
                    <div class="returnCode">
                        <code id="discordCode" class="">
                            <c:if test="${user.discord_user_id > 0}">
                                !regist ${user.access_token}
                            </c:if>
                            <c:if test="${user.discord_user_id == 0}">
                                <fmt:message key="label.account_ready" />
                            </c:if>
                        </code>
                    </div>
                </div>
            </form>
        </div>
        <%@include file="../includes/footer.jsp" %>
    </body>
</html>
<%}%>
