<%@ page import="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@include file="includes/globalObject.jsp" %>
<c:choose>
    <c:when test="${not empty param.id && user.login && user.is_guild_member}">
        <% CharacterMember mDetail = general_config.getMember(Long.parseLong(request.getParameter("id")));
        if (mDetail != null) {
            request.setAttribute("character", mDetail);
        }%>
        <c:choose>
            <c:when test="${not empty character && character.info.guild_id == guild.id && character.is_valid}">
                <jsp:forward page="member_panel/member_detail.jsp" />
            </c:when>
            <c:otherwise>
                <jsp:forward page="member_panel/list_members.jsp" />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${not empty param.id && !user.login}">
        <c:redirect url="login.jsp?rdir=members.jsp?id=${param.id}" />
    </c:when>
    <c:otherwise>
        <jsp:forward page="member_panel/list_members.jsp" />
    </c:otherwise>
</c:choose>