<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("events.jsp", "UTF-8"));
} else { %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<%@ page import ="com.blizzardPanel.User" %>
<%@ page import ="com.blizzardPanel.events.Event" %>
<%@ page import ="com.blizzardPanel.events.EventAsist" %>
<%@ page import ="com.blizzardPanel.events.EventAsistCharacter" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterSpec" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.PlayableSpec" %>
<% 
Event ev = new Event(Integer.parseInt(request.getParameter("evHistory"))); 
EventAsist evAsist = ev.getAssistDetail(user); 
if (ev.isHide()) {
    response.sendRedirect("events.jsp");
} else if (ev.isExpire() || !ev.isEnable()) { %>
    <div class="loader ajaxLoad" style="display: none;"></div>
    <div id="event_add_result" style="display: none;"></div>
    <span class="ev_owner character-<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %></span>
    <div id="eventDetail" data-id="<%= ev.getId() %>" class="key_title key_divide_title"><h1><%= ev.getTitle() %></h1></div>
    <span class="right_small_date"><%= ev.getDate() %></span>
    <p><%= ev.getDesc() %></p>
    <!-- OTHER PLAYERS... -->
    <table class="table table-dark character-tab">
        <thead>
            <tr>
                <th scope="col">Member</th>
                <th scope="col">Main Select</th>
                <th scope="col">Alters select</th>
            </tr>
        </thead>
        <tbody>
            <% for(EventAsist assistens : ev.getEventAssist()) { 
                User asistUser = assistens.getUser(); 
                CharacterMember mainChar = assistens.getMainEventAssistCharacter().getCharMember();
                String className = mainChar.getMemberClass().getSlug(); %>
                <tr>
                    <td><span class="character-<%= (asistUser.getMainCharacter() != null)? asistUser.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (asistUser.getMainCharacter() != null)? asistUser.getMainCharacter().getName():asistUser.getBattleTag().split("#")[0] %></span></td>
                    <td>
                        <img src="assets/img/classes/specs/spec_<%= className %>_<%= mainChar.getActiveSpec().getSpec().getSlug() %>.png" style="width: 22px;" >
                        <span class="character-<%= className %>"><%= mainChar.getName() %></span>
                    </td>
                    <td>
                        <% for(EventAsistCharacter alters : assistens.getAlterEventAssistCharacter() ) { 
                            CharacterMember alt = alters.getCharMember();
                            String altClassName = alt.getMemberClass().getSlug(); %>
                                <img src="assets/img/classes/specs/spec_<%= altClassName %>_<%= alt.getActiveSpec().getSpec().getSlug() %>.png" style="width: 22px;" >
                                <span class="character-<%= altClassName %>"><%= alt.getName() %></span>
                                <br>
                        <% } %>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
<% /* is enable/disable event */ } else {
    response.sendRedirect("events.jsp");
} }/* first if */%>