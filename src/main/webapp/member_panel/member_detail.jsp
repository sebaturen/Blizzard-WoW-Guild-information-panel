<%@include file="../includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - ${character.name}</title>
        <%@include file="../includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/members.css">
        <script src="assets/js/members.js"></script>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="member_bg_content" style="background-image: url('${character.media.render_url}')">
            <div class="container">
                <div class="name_title">
                    <c:set var="title" value="${character.info.getTitle(locale)}"/>
                    <c:choose>
                        <c:when test="${not empty title}">
                            <c:set var="charName" value="<h1 class='warcraft_font' style='display: inline;'>${character.name}</h1>" />
                            <c:set var="name" value="${fn:replace(title, '{name}', charName)}"/>
                            ${name}
                        </c:when>
                        <c:otherwise>
                            <h1 class="warcraft_font">${character.name}</h1>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="member_items row">
                    <div class="itemsLeft col items_left">
                        <c:set var="leftEquip" value="${['HEAD','NECK','SHOULDER','BACK','CHEST','SHIRT','TABARD','WRIST']}" scope="application" />
                        <c:forEach items="${leftEquip}" var="post">
                            <c:set var="charItem" value="${character.getItem(post)}" />
                            <div class="itemDetail row" data-item="${charItem.item.id}">
                                <div class="itemIcon left <c:if test="${charItem.item.media.id == 0}">equip_unknown_${post}</c:if>" style="background-image: url('${charItem.item.media.value}');"></div>
                                <div class="itemDesc quality-${charItem.quality_type}">${charItem.item.getName(locale)}</div>
                                <div class="itemIlvl"><c:if test="${charItem.level > 0}">${charItem.level}</c:if></div>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="itemsRight col items_right">
                        <c:set var="rightEquip" value="${['HANDS','WAIST','LEGS','FEET','FINGER_1','FINGER_2','TRINKET_1','TRINKET_2']}" scope="application" />
                        <c:forEach items="${rightEquip}" var="post">
                            <c:set var="charItem" value="${character.getItem(post)}" />
                            <div class="itemDetail row justify-content-end" data-item="${charItem.item.id}">
                                <div class="itemDesc left quality-${charItem.quality_type}">${charItem.item.getName(locale)}</div>
                                <div class="itemIlvl"><c:if test="${charItem.level > 0}">${charItem.level}</c:if></div>
                                <div class="itemIcon <c:if test="${charItem.item.media.id == 0}">equip_unknown_${post}</c:if>" style="background-image: url('${charItem.item.media.value}');"></div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                <div class="hand_items row">
                    <div class="col items_left">
                        <c:set var="charItem" value="${character.getItem('MAIN_HAND')}" />
                        <div class="itemDetail row justify-content-end" data-item="${charItem.item.id}">
                            <div class="itemDesc left quality-${charItem.quality_type}">${charItem.item.getName(locale)}</div>
                            <div class="itemIlvl"><c:if test="${charItem.level > 0}">${charItem.level}</c:if></div>
                            <div class="itemIcon <c:if test="${charItem.item.media.id == 0}">equip_unknown_MAIN_HAND</c:if>" style="background-image: url('${charItem.item.media.value}');"></div>
                        </div>
                    </div>
                    <div class="col items_right">
                        <c:set var="charItem" value="${character.getItem('OFF_HAND')}" />
                        <div class="itemDetail row" data-item="${charItem.item.id}">
                            <div class="itemIcon left <c:if test="${charItem.item.media.id == 0}">equip_unknown_OFF_HAND</c:if>" style="background-image: url('${charItem.item.media.value}');"></div>
                            <div class="itemDesc quality-${charItem.quality_type}">${charItem.item.getName(locale)}</div>
                            <div class="itemIlvl"><c:if test="${charItem.level > 0}">${charItem.level}</c:if></div>
                        </div>
                    </div>
                </div>
                <div class="status row">
                    <c:set var="charStats" value="${character.stats}" />
                </div>
            </div>
        </div>
        <%@include file="../includes/footer.jsp" %>
    </body>
</html>

