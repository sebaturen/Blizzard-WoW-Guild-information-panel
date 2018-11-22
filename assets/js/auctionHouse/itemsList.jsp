<%@include file="/includes/globalObject.jsp" %>
<%if(user != null && user.getGuildRank() != -1) {%>
<jsp:useBean id="auctionHouse" class="com.artOfWar.viewController.AuctionHouse" scope="session"/>
<%@ page import ="com.artOfWar.gameObject.Item" %>
<%@ page import ="java.util.List" %>
var items = [
<%  List<Item> items = auctionHouse.getItems(request.getParameter("name")); 
    for(Item item : items) {%>
        {
            'itemID': '<%= item.getId() %>',
            'itemName': "<%= (item.getName()).replaceAll("\"", "'") %>",
            'itemImg': '<%= item.getIconRenderURL() %>'
        },        
    <%}//foreach aucItems%>
];
<%} //login if%>