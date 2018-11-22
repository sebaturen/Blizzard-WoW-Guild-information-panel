<%@include file="/includes/globalObject.jsp" %>
<%if(user != null && user.getGuildRank() != -1) {%>
<jsp:useBean id="auctionHouse" class="com.artOfWar.viewController.AuctionHouse" scope="session"/>
<%@ page import ="com.artOfWar.gameObject.AuctionItem" %>
<%@ page import ="java.util.List" %>
var auctions = [
<%  List<AuctionItem> aucItems = auctionHouse.getAucItem(Integer.parseInt(request.getParameter("id")));
    int quantity = 0;
    int stacks = 0;
    long stackPrice = 0;
    long uniquePrice = 0;
    for(AuctionItem auc : aucItems) 
    {
        //First round, look not have a buyout price.
        if(auc.getBuyout() == 0)
        {
            stacks++;
            quantity += auc.getQuantity();            
        }
        else
        {
            //print last elements...
            if(auc.getUniqueBuyoutPrice() != uniquePrice)
            {%>
                {
                    'quantity': '<%= quantity %>',
                    'stacks': '<%= stacks %>',
                    'uniqueGold': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(uniquePrice)[0] %>',
                    'uniqueSilver': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(uniquePrice)[1] %>',
                    'uniqueCopper': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(uniquePrice)[2] %>',
                    'stackGold': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(stackPrice)[0] %>',
                    'stackSilver': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(stackPrice)[1] %>',
                    'stackCopper': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(stackPrice)[2] %>'
                },<%
                quantity = 0;
                stacks = 0;
                stackPrice = 0;
                uniquePrice = 0;
            }
            stacks++;
            quantity += auc.getQuantity();
            stackPrice = auc.getBuyout();
            uniquePrice = auc.getUniqueBuyoutPrice();            
        }
    }//foreach aucItems%>
    {
        'quantity': '<%= quantity %>',
        'stacks': '<%= stacks %>',
        'uniqueGold': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(uniquePrice)[0] %>',
        'uniqueSilver': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(uniquePrice)[1] %>',
        'uniqueCopper': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(uniquePrice)[2] %>',
        'stackGold': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(stackPrice)[0] %>',
        'stackSilver': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(stackPrice)[1] %>',
        'stackCopper': '<%= com.artOfWar.gameObject.AuctionItem.dividePrice(stackPrice)[2] %>'
    }
];
<%} //login if%>