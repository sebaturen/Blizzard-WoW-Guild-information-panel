<%@include file="/includes/globalObject.jsp" %>
<%if(user != null && user.getGuildRank() != -1) {%>
<jsp:useBean id="auctionHouse" class="com.artOfWar.viewController.AuctionHouse" scope="request"/>
<%@ page import ="com.artOfWar.gameObject.AuctionItem" %>
<%@ page import ="java.util.List" %>
var items = {
<%  List<AuctionItem> aucItems = auctionHouse.getAucItem(request.getParameter("name")); 
    for(AuctionItem auc : aucItems) {%>
        '<%= auc.getId() %>': {
            'itemName': '<%= auc.getItem().getName() %>',
            'itemImg': '<%= auc.getItem().getIconRenderURL() %>',
            'quantity': '<%= auc.getQuantity() %>',
            'buyGold': '<%= auc.getBuyoutDividePrice()[0] %>',
            'buySilver': '<%= auc.getBuyoutDividePrice()[1] %>',
            'buyCopper': '<%= auc.getBuyoutDividePrice()[2] %>',
            'pushGold': '<%= auc.getBidDividePrice()[0] %>',
            'pushSilver': '<%= auc.getBidDividePrice()[1] %>',
            'pushCopper': '<%= auc.getBidDividePrice()[2] %>',
            'owner': '<%= auc.getOwner() %>',
            'timeLef': '<%= auc.getTimeLeft() %>'
        },
    <%}//foreach aucItems%>
 };
<%} //login if%>