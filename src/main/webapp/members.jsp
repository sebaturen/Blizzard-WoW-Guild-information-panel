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
    <c:when test="${not empty param && !user.login}">
        <c:url value="members.jsp" var="url">
            <c:forEach var="p" items="${param}">
                <c:param name="${p.key}" value="${p.value}" />
            </c:forEach>
        </c:url>
        <c:url value="login.jsp" var="redirectLogin">
            <c:param name="rdir" value="${url}"/>
        </c:url>
        <c:redirect url="${redirectLogin}" />
    </c:when>
    <c:otherwise>
        <jsp:forward page="member_panel/list_members.jsp" />
    </c:otherwise>
</c:choose>