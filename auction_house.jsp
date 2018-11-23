<%@include file="includes/globalObject.jsp" %>
<% if (user == null || user.getGuildRank() == -1) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("/login.jsp?rdir="+URLEncoder.encode("/auction_house.jsp", "UTF-8"));
} else {%>
<jsp:useBean id="auctionHouse" class="com.blizzardPanel.viewController.AuctionHouse" scope="request"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Auction House</title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="/assets/css/aution_house.css">
        <script src="/assets/js/auctionHouse/autionHouse.js"></script>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="AH_timeUpdate">Last update [<%= auctionHouse.getLastAHUpdate() %>]</div>
            <div class="alert alert-danger" role="alert">Esta función esta EN PRUEBA!, puede no funcionar como se espera</div>
            <div id="AH_searchSection">                
                <div class="form-group">
                    <div id="itemSearchImag"></div>
                    <label>Item name:</label>
                    <input class="form-control" type="text" value="" id="itemName"/>
                    <div id="itemsSuggested" style="display: none;"></div>
                </div>
            </div>
            <table class="table table-dark items_ah-tab">
                <thead>
                    <tr>
                        <th scope="col" id="nameColum" class='pointer'>Unit Price</th>
                        <th scope="col" id="classColum" class='pointer'>Current Auctions</th>
                        <th scope="col" id="classColum" class='pointer'>Stack Price</th>
                    </tr>
                </thead>
                <tbody id="items_ah_content">
                </tbody>
            </table>
        </div>
    </body>
</html>
<%}%>
    