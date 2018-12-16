<%@include file="../../../includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.Character" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterItems" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterStats" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterSpec" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.Stat" %>
<%@ page import ="com.blizzardPanel.gameObject.Item" %>
<%@ page import ="com.blizzardPanel.gameObject.Spell" %>
<jsp:useBean id="members" class="com.blizzardPanel.viewController.Members" scope="session"/>
<%if(guildMember) {
    int memberID = Integer.parseInt(request.getParameter("id"));
    Character member = members.getMember(memberID); %>
var member_<%= memberID %> = {
    'm_info': {
        'name': '<%= member.getName() %>', 
        'class': '<%= member.getMemberClass().getName() %>', 
        'class_slug': '<%= member.getMemberClass().getSlug() %>', 
        'spec': '<%= member.getActiveSpec().getSpec().getName() %>', 
        'spec_slug': '<%= member.getActiveSpec().getSpec().getSlug() %>', 
        'level': <%= member.getLevel() %>, 
        'img': '<%= member.getThumbnailURL() %>',
        'rol': '<%= member.getActiveSpec().getSpec().getRole() %>', 
        'member_id': <%= member.getId() %>, 
        'gRank_id': <%= member.getRank().getId() %>,
        'gRank_title': '<%= member.getRank().getTitle() %>',
        'race': '<%= member.getRace().getName() %>'
    },
    'stats': {
        <% CharacterStats mStat = member.getStats(); %>
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
    'active_spec_spells': {
        <% CharacterSpec acSpec = member.getActiveSpec();
            if (acSpec != null)
            {%>
                'desc': "<%= (acSpec.getSpec().getDescript(member.getGender())).replaceAll("\"", "'").replaceAll("\r\n", "<br>").replaceAll("\r\n", "<br>") %>",
                'spells': {
                  <%int i = 0;
                    for(Spell sp : acSpec.getSpells())
                    {%>
                        'sp_<%= i %>': {
                      <%if(sp != null)
                        {%>
                            'name': "<%= (sp.getName()).replaceAll("\"", "'") %>",
                            'action': '<%= (sp.isPasive())? "Equip":"Use" %>',
                            'desc': "<%= (sp.getDesc()).replaceAll("\"", "'").replaceAll("\n\n", "<br>").replaceAll("\r\r", "<br>") %>",
                            'img': '<%= sp.getIconRenderURL() %>'
                      <%} else {%>
                            'name': 'UNSELECTED',
                            'desc': '',
                            'img': 'error.jpg'
                      <%}%>
                        },
                  <%i++;
                    }%> 
                }
          <%}
        %>
    },
    'items': {
        <%  String[] equip = {
            "head", "neck", "shoulder", "back", "chest", "shirt", "tabard", "wrist",
            "hands", "waist", "legs", "feet", "finger1", "finger2", "trinket1", "trinket2",
            "mainHand", "offHand"}; 
            for(String post : equip)
            {
                CharacterItems im = member.getItemByPost(post);
                if(im != null) {%>
                    '<%= post %>': {
                        'name': "<%= (im.getItem().getName()).replaceAll("\"", "'") %>",
                        'img': '<%= im.getItem().getIconRenderURL() %>',
                        'ilevel': '<%= im.getIlevel() %>',
                        'quality': '<%= im.getQuality() %>',
                        'azerite_level': '<%= im.getAzeriteLevel() %>',
                        'armor': '<%= im.getArmor() %>',
                        <% if(im.getGem() != null) { Item gem = im.getGem(); %>
                        'gem': {
                            'name': "<%= (gem.getName()).replaceAll("\"", "'") %>",
                            'bonus': '<%= gem.getGemBonus() %>',
                            'type': '<%= gem.getGemType() %>',
                            'img': '<%= gem.getIconRenderURL() %>'
                        },<%}%>
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
                        <% if( im.getItem().getItemSpell().getId() > 0 ) { Spell sp = im.getItem().getItemSpell(); %>
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
}
<%} else { out.write("Only from user members..."); }%>
