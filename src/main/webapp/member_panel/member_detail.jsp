<%@include file="../includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - ${character.name}</title>
        <%@include file="../includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/members.css">
        <script src="assets/js/member_detail.js"></script>
    </head>
    <body class="member_bg_content" style="background-image: url('${character.media.render_url}')">
        <%@include file="../includes/menu.jsp" %>
        <div class="container">
            <div id="info" class="name_title" data-id="${character.id}">
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
        </div>
        <div class="contentDivide">
            <div class="container">
                <div class="separator statistics">
                    <c:set var="iconMedia" value="<div class='Media-image'><span class='Icon Media-icon'><svg class='Icon-svg' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' viewBox='0 0 64 64'><use xlink:href='assets/img/icons/stat-icon.svg#PUT_STAT'></use></svg></span></div>" />
                    <c:set var="charStats" value="${character.stats}" />
                    <div class="interSeparator stat-primary row">
                        <!-- Health -->
                        <c:set var="hIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'health')}"/>
                        <div class="col Icon--health statDetail">
                            ${hIconMedia}
                            <div class="Media-text">
                                <span>${charStats.health}</span><div class="bold"><fmt:message key="label.health" /></div>
                            </div>
                        </div>
                        <!-- Power -->
                        <c:set var="pIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', charStats.powerType.getName('en_US').toLowerCase())}"/>
                        <div class="col Icon--${charStats.powerType.getName('en_US').toLowerCase()} statDetail">
                            ${pIconMedia}
                            <div class="Media-text">
                                <span>${charStats.power}</span><div class="bold">${charStats.powerType.getName(locale)}</div>
                            </div>
                        </div>
                        <!-- Intellect / Agility -->
                        <c:choose>
                            <c:when test="${
                    charStats.intellect.get('base').getAsInt() !=
                    charStats.intellect.get('effective').getAsInt()}">
                                <c:set var="iaIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'intellect')}"/>
                                <div class="col Icon--intellect statDetail">
                                        ${iaIconMedia}
                                    <div class="Media-text">
                                        <span>${charStats.intellect.get('effective')}</span><div class="bold"><fmt:message key="label.intellect" /></div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:set var="iaIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'agility')}"/>
                                <div class="col Icon--agility statDetail">
                                        ${iaIconMedia}
                                    <div class="Media-text">
                                        <span>${charStats.agility.get('effective')}</span><div class="bold"><fmt:message key="label.agility" /></div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <!-- Stamina -->
                        <c:set var="sIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'stamina')}"/>
                        <div class="col Icon--stamina statDetail">
                            ${sIconMedia}
                            <div class="Media-text">
                                <span>${charStats.stamina.get('effective')}</span><div class="bold"><fmt:message key="label.stamina" /></div>
                            </div>
                        </div>
                    </div>
                    <div class="interSeparator stat-secundary row">
                        <!-- Critic -->
                        <c:set var="critIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'critical-strike')}"/>
                        <div class="col Icon--critical-strike statDetail">
                            ${critIconMedia}
                            <div class="Media-text">
                            <span>
                                <fmt:formatNumber type="number" maxFractionDigits="1" value ="${charStats.melee.getAsJsonObject('cirt').get('value').getAsFloat()}"/>%
                            </span>
                                <div class="bold"><fmt:message key="label.critical_Strike" /></div>
                            </div>
                        </div>
                        <!-- Celerity -->
                        <c:set var="hasteIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'haste')}"/>
                        <div class="col Icon--haste statDetail">
                            ${hasteIconMedia}
                            <div class="Media-text">
                            <span>
                                <fmt:formatNumber type="number" maxFractionDigits="1" value ="${charStats.spell.getAsJsonObject('haste').get('value').getAsFloat()}"/>%
                            </span>
                                <div class="bold"><fmt:message key="label.haste" /></div>
                            </div>
                        </div>
                        <!-- Mastery -->
                        <c:set var="masteryIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'mastery')}"/>
                        <div class="col Icon--mastery statDetail">
                            ${masteryIconMedia}
                            <div class="Media-text">
                            <span>
                                <fmt:formatNumber type="number" maxFractionDigits="1" value ="${charStats.mastery.get('value').getAsFloat()}"/>%
                            </span>
                                <div class="bold"><fmt:message key="label.mastery" /></div>
                            </div>
                        </div>
                        <!-- Versa -->
                        <c:set var="versaIconMedia" value="${fn:replace(iconMedia, 'PUT_STAT', 'versatility')}"/>
                        <div class="col Icon--versatility statDetail">
                            ${versaIconMedia}
                            <div class="Media-text">
                            <span>
                                <fmt:formatNumber type="number" maxFractionDigits="1" value ="${charStats.versatility.get('damage_done_bonus').getAsFloat()}"/>%
                            </span>
                                <div class="bold"><fmt:message key="label.versatility" /></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="bg_solid">
            <div class="container">
                <div class="separator specialization">
                    <c:forEach var="spec" items="${character.specs}">
                        <div class="interSeparator row">
                            <div
                                    class="col-3 nameDescTooltip"
                                    data-spec="${spec.playableSpec.id}"
                                    data-name="${spec.playableSpec.getName(locale)}"
                                    data-desc="${spec.playableSpec.getDesc(locale, character.info.gender_type)}"
                            >
                                <img class="key_affix_img img_spell" src="${spec.playableSpec.media.value}" style="width: 40px;"/>
                                    ${spec.playableSpec.getName(locale)}
                            </div>
                            <div class="col row">
                                <c:forEach var="tier" items="${spec.tiers}">
                                    <div
                                            class="nameDescTooltip spell_inf col"
                                            data-tier="${tier.id}"
                                            data-name="${tier.getName(locale)}"
                                            data-desc="${tier.getDescription(locale)}"
                                    >
                                        <img class="key_affix_img img_spell" src="${tier.media.value}"/>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="container">
            <div id="mythic_week" class="separator">
                <div id="loading" class="justify-content-md-center"><div class="loader"></div></div>
                <div id="mythic_run_mold" class="key_run_group dungeon-challenge col" style="display: none">
                    <div class='key_run_dun_img dungeon-challenge-img' style=''>
                        <div class='key_run_lvl'></div>
                        <h2 class='key_dung_title'></h2>
                    </div>
                    <p class='key_group_time'></p>
                    <p class='key_date'></p>
                    <table class='table table-dark character-tab'>
                        <thead>
                        <tr>
                            <th scope='col'><fmt:message key="label.name" /></th>
                            <th scope='col'><fmt:message key="label.role" /></th>
                            <th scope='col'><fmt:message key="label.ilvl" /></th>
                        </tr>
                        </thead>
                        <tbody class="key_characters">
                        <tr id="key_char_detail">
                            <td class="key_char_name"></td>
                            <td>
                                <img class="key_char_rol_img" src='' style='width: 22px;'/>
                                <img class="key_char_spec_img" rc='' style='width: 22px;'/>
                            </td>
                            <td class="key_char_ilvl"></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="item-floating-desc tooltip-affix">
            <div class="itemDesc tooltipDesc">
                <p id="affix_name"></p>
                <p id="affix_desc" class="tooltip-yellow itemSpellDetail"></p>
            </div>
        </div>
        <%@include file="../includes/footer.jsp" %>
    </body>
</html>

