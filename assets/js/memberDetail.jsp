<%@include file="/includes/globalObject.jsp" %>
<%@ page import ="com.artOfWar.gameObject.characters.Member" %>
<%@ page import ="com.artOfWar.gameObject.characters.ItemMember" %>
<%@ page import ="com.artOfWar.gameObject.characters.StatsMember" %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<jsp:useBean id="members" class="com.artOfWar.viewController.Members"/>
var members = [];
var guildRanks = [];
var mClass = [];
var textClass = [];
var races = [];
var moreDetail = false;
<%
if(user != null && user.getGuildRank() != -1)
{
    %>moreDetail = true;<%
}
List<Integer> guildRank = new ArrayList<>();
List<String> mClass = new ArrayList<>();
List<String> txtClass = new ArrayList<>();
List<String> races  = new ArrayList<>();
for(Member member : members.getMembersList())
{ 
    String className = ((member.getmemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase(); 
    String specName = ((member.getActiveSpec().getName()).replaceAll("\\s+","-")).toLowerCase(); 
    String iLevel = "0";
    String race = "";
    if(user != null && user.getGuildRank() != -1)
    {
        iLevel = String.format("%.2f", member.getItemLevel());
        race = member.getRace().getName();        
        if (!guildRank.contains(member.getRank()))
        {
            guildRank.add(member.getRank());
            %>guildRanks.push('<%= member.getRank() %>');<%
        }
        if (!mClass.contains(className)) 
        {
            mClass.add(className);
            txtClass.add(member.getmemberClass().getEnName());
            %>mClass.push('<%= className %>');
            textClass.push('<%= member.getmemberClass().getEnName() %>');<%
        }
        if (!races.contains(member.getRace().getName()))
        {
            races.add(member.getRace().getName());
            %>races.push('<%= member.getRace().getName() %>');<%
        }
    } %>
    members.push({   
        'name': '<%= member.getName() %>', 
        'class': '<%= className %>', 
        'spec': '<%= specName %>', 
        'level': <%= member.getLevel() %>, 
        'img': '<%= member.getThumbnailURL() %>',
        'rol': '<%= member.getActiveSpec().getRole() %>', 
        'member_id': <%= member.getId() %>, 
        'gRank': <%= member.getRank() %>,
        'iLevel': '<%= iLevel %>',
        'race': '<%= race %>',
        <% if (user != null && user.getGuildRank() != -1) { %>
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
                        if(im != null) {%>
                            '<%= post %>': {
                                'name': "<%= (im.getItem().getName()).replaceAll("\"", "'") %>",
                                'img': '<%= im.getItem().getIconRenderURL() %>.jpg',
                                'ilevel': '<%= im.getIlevel() %>',
                                'quality': '<%= im.getQuality() %>'
                            },
                      <%}
                    }
                %>
            }
      <%}%>
    });
<%}%>