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
    <img src="../assets/img/icons/Battlenet_icon_flat.svg" style="width: 40px"><%= user.getBattle_tag() %>
    <%= (user.getGuildRank() == 0 || user.getGuildRank() == 1)? "<a href='userpanel/settings.jsp' class='right'><button type='submit' class='btn btn-outline-warning btn-sm'>Settings</button></a>":"" %><br/>
    <%
        //User character info~
        List<CharacterMember> memberChars = user.getCharacters();
        if (memberChars.size() == 0) {
            %>Your characteres is loading... please refresh the page in a seconds (F5) <br><br><%
        } else { %>
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
        <% for(CharacterMember m : memberChars) {%>
        <tr>
            <% //Get img from speck
                long classId = m.getInfo().getPlayableClass().getId();
                long specId = m.getActiveSpec().getSpecialization_id();
                String mainClassCode = "&#xe800;";
                if (user.getMainCharacter() != null && m.getId() == user.getMainCharacter().getId()) {
                    mainClassCode = "&#xe801;";
                }

                String mDunName = "";
                String mDunLvl = "";
                if (m.getBestMythicRun() != null) {
                    mDunName = m.getBestMythicRun().getDungeon().getName("en_US");
                    mDunLvl = "+"+ m.getBestMythicRun().getKeystone_level();
                }
            %>
            <td><i class="main_char artOfWar-icon pointer" data-member_id="<%= m.getId() %>"><%= mainClassCode %></i></td>
            <td class="character-<%= classId %>"><%= m.getName() %></td>
            <td><%
                if (specId != 1) {
                    %><img src="assets/img/classes/specs/spec_<%= classId %>_<%= specId %>.png" style="width: 22px;"/></td><%
                }
            %>
            <td><%= m.getInfo().getLevel() %></td>
            <td data-dun_name="<%= mDunName %>"><%= mDunLvl %></td>
            <td><%= m.getRealm().getName(locale) %></td>
            <td><img src="assets/img/icons/<%= m.getInfo().getFaction().getType() %>.png" style="width: 22px;"/></td>
        </tr>
        <%} /* end foreach member m */ %>
        </tbody>
    </table>
    <% } /*close if members CharacterMember more 0 */ %>
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
            <img src="../assets/img/icons/Discord-Logo-Color.svg" style="width: 35px">
            <div class="returnCode">
                <code id="discordCode" class="">
                    <% if(user.getDiscord_user_id() > 0) {
                            out.write("!regist "+ user.getAccess_token());
                        } else { %>
                            <fmt:message key="label.account_ready" />
                    <% } %>
                </code>
            </div>
        </div>
    </form>
</div>
<%@include file="../includes/footer.jsp" %>
</body>
</html>
<%}%>
