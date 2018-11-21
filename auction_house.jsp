<%@include file="includes/globalObject.jsp" %>
<% if (user == null || user.getGuildRank() == -1) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("/login.jsp?rdir="+URLEncoder.encode("/auction_house.jsp", "UTF-8"));
} else {%>
<jsp:useBean id="auctionHouse" class="com.artOfWar.viewController.AuctionHouse" scope="request"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Auction House</title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="/assets/css/aution_house.css">
        <script src="/assets/js/autionHouse.js"></script>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="AH_timeUpdate">Last update [<%= auctionHouse.getLastAHUpdate() %>]</div>
            <div class="alert alert-danger" role="alert">Esta función esta EN PRUEBA!, puede no funcionar como se espera</div>
            <div id="AH_searchSection">                
                <div class="form-group">
                    <label>Item name:</label>
                    <input class="form-control" type="text" value="" id="itemName"/>
                </div>
            </div>
            <table class="table table-dark items_ah-tab">
                <thead>
                    <tr>
                        <th scope="col" id="rankColum" class='pointer'>#</th>
                        <th scope="col" id="nameColum" class='pointer'>Item</th>
                        <th scope="col" id="classColum" class='pointer'>Quantity</th>
                        <th scope="col" id="classColum" class='pointer'>Buy</th>
                        <th scope="col" id="levelColum" class='pointer'>Push</th>
                        <th scope="col" id="specColum" class='pointer'>Owner</th>
                        <th scope="col" id="specColum" class='pointer'>Time Left</th>
                    </tr>
                </thead>
                <tbody id="items_ah_content">
                </tbody>
            </table>
        </div>
    </body>
</html>
<%}%>
    