<%@include file="../../../includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.WoWToken" %>
<%@ page import ="java.util.List" %>
var wow_token_history = [];
<% 
    List<WoWToken> wTokenHistory = gameInfo.getWoWTokenHistory();
    for (WoWToken wToken : wTokenHistory) { %> 
        wow_token_history.push({
            'date': <%= wToken.getLastUpdate() %>,
            'gold': <%= wToken.getGold() %>,
        });
    <%}
%>