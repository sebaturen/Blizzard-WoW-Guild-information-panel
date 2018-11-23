<%@include file="/includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.Member" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.ItemMember" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.StatsMember" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.Stat" %>
<%@ page import ="com.blizzardPanel.gameObject.Item" %>
<%@ page import ="com.blizzardPanel.gameObject.Spell" %>
<jsp:useBean id="members" class="com.blizzardPanel.viewController.Members"/>
var member = {
<%if(user != null && user.getGuildRank() != -1) { Member member = members.getMember(Integer.parseInt(request.getParameter("id"))); %>
    'stats': {
        <% StatsMember mStat = member.getStats(); %>
        'health': '<%= String.format("%,d", mStat.getHealth()) %>', 
        'stamina': '<%= String.format("%,d", mStat.getSta()) %>', 
        'crit': '<%= String.format("%.2f", mStat.getCrit())+"%" %>', 
        'haste': '<%= String.format("%.2f", mStat.getHaste())+"%" %>', 
        'mastery': '<%= String.format("%.2f", mStat.getMastery())+"%" %>', 
        'versatility': '<%= String.format("%.2f", mStat.getVersatilityDamageDoneBonus())+"%" %>',
        'powerType':'<%= mStat.getPowerType() %>', 
        'power': '<%= String.format("%,d", mStat.getPower()) %>', 
        'primaryStatType': '<%= mStat.getBestStat()[0] %>', 
        'primaryStat': '<%= String.format("%,d", Integer.parseInt(mStat.getBestStat()[1])) %>' 
    },
    'items': {
        <%  String[] equip = {
            "head", "neck", "shoulder", "back", "chest", "shirt", "tabard", "wrist",
            "hands", "waist", "legs", "feet", "finger1", "finger2", "trinket1", "trinket2",
            "mainHand", "offHand"}; 
            for(String post : equip)
            {
                ItemMember im = member.getItemByPost(post); 
                session.setAttribute("im", im);
                if(im != null) {%>
                    '<%= post %>': {
                        'name': "<%= (im.getItem().getName()).replaceAll("\"", "'") %>",
                        'img': '<%= im.getItem().getIconRenderURL() %>',
                        'ilevel': '${im.ilevel}',
                        'quality': '${im.quality}',
                        'azerite_level': '${im.azeriteLevel}',
                        <% if(im.getGem().isInternalData()) { Item gem = im.getGem(); %>
                        'gem': {
                            'name': "<%= (gem.getName()).replaceAll("\"", "'") %>",
                            'bonus': '<%= gem.getGemBonus() %>',
                            'type': '<%= gem.getGemType() %>',
                            'img': '<%= gem.getIconRenderURL() %>'
                        },<%}%>                                
                        'armor': '<%= im.getArmor() %>',
                        'stats': {
                            <% for(Stat s : im.getStats()) {%> 
                                '<%= s.getEnUs() %>': '<%= s.getAmount() %>', 
                            <%}%>
                        },
                        'azerite_power': {
                            <% int i = 0; for(Spell az : im.getAzeritePower()) {%>
                                '<%= i %>': { <%if(az != null) {%>
                                    'name': "<%= (az.getName()).replaceAll("\"", "'") %>",
                                    'img': '<%= az.getIconRenderURL() %>',
                                    'desc': "<%= (az.getDesc()).replaceAll("\"", "'").replaceAll("\n\n", "<br>").replaceAll("\r\r", "<br>") %>" <%}%>
                                },
                            <%i++;}%>
                        },
                        <% if( im.getItem().getItemSpell().getIntId() > 0 ) { Spell sp = im.getItem().getItemSpell(); %>
                        'spell': {
                            'action': '<%= (sp.isPasive())? "Equip":"Use" %>',
                            'desc': "<%= (sp.getDesc()).replaceAll("\"", "'").replaceAll("\n\n", "<br>").replaceAll("\r\r", "<br>") %>"
                        },
                        <%} %>
                    },
              <%}
            }
        %>
    }   
<%}%>
}