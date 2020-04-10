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
                            <td class="character-${cm.info.class_id}">${cm.name}</td>
                            <td>
                                <c:if test="${cm.activeSpec.specialization_id != 1}">
                                    <img src="assets/img/classes/specs/spec_${cm.info.class_id}_${cm.activeSpec.specialization_id}.png" style="width: 22px;"/>
                                </c:if>
                            </td>
                            <td>${cm.info.level}</td>
                            <td>
                                <c:if test="${cm.bestMythicRun != null}">
                                    ${cm.bestMythicRun.keystone_level}
                                </c:if>
                            </td>
                            <td>${cm.realm.getName(locale)}</td>
                            <td><img src="assets/img/icons/${cm.info.faction.type}.png" style="width: 22px;"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <form method="get" class="form-inline">
                <fmt:message key="label.select_language" />
                <select name="locale" class="form-control mx-sm-3 mb-2" id="locale" style="width: auto;">
                    <option value="en_US" <c:if test="${locale == 'en_US'}">selected</c:if> >English (U.S.)</option>
                    <option value="es_MX" <c:if test="${locale == 'es_MX'}">selected</c:if> >Spanish (Mexican)</option>
                    <option value="pt_BR" <c:if test="${locale == 'pt_BR'}">selected</c:if> >Portuguese (Brazilian)</option>
                    <option value="de_DE" <c:if test="${locale == 'de_DE'}">selected</c:if> >German</option>
                    <option value="en_GB" <c:if test="${locale == 'en_GB'}">selected</c:if> >English (U.K.)</option>
                    <option value="es_ES" <c:if test="${locale == 'es_ES'}">selected</c:if> >Spanish (Spain)</option>
                    <option value="fr_FR" <c:if test="${locale == 'fr_FR'}">selected</c:if> >French</option>
                    <option value="it_IT" <c:if test="${locale == 'it_IT'}">selected</c:if> >Italian</option>
                    <option value="ru_RU" <c:if test="${locale == 'ru_RU'}">selected</c:if> >Russian</option>
                    <option value="ko_KR" <c:if test="${locale == 'ko_KR'}">selected</c:if> >Korean</option>
                    <option value="zh_TW" <c:if test="${locale == 'zh_TW'}">selected</c:if> >Chinese (Taiwan)</option>
                    <option value="zh_CN" <c:if test="${locale == 'zh_CN'}">selected</c:if> >Chinese (Simplified)</option>
                </select>
                <button type="submit" class="btn btn-outline-secondary"><fmt:message key="label.save" /></button>
            </form>
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
