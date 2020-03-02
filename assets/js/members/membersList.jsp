<%@include file="../../../includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterItem" %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<%@ page import ="com.google.gson.JsonObject"%>
<jsp:useBean id="members" class="com.blizzardPanel.viewController.Members" scope="session"/>
var members = [];
var guildRanks = [];
var mClass = [];
var textClass = [];
var races = [];
var moreDetail = false;
<%
if(guildMember)
{
    %>moreDetail = true;<%
}
List<Integer> guildRank = new ArrayList<>();
List<String> mClass = new ArrayList<>();
List<String> txtClass = new ArrayList<>();
List<String> races  = new ArrayList<>();
if(members.getMembersList() != null)
{
    for(CharacterMember member : members.getMembersList())
    {
        String className = member.getMemberClass().getSlug();
        String specName = member.getActiveSpec().getSpec().getSlug();
        String iLevel = "0";
        String race = "";
        int mScore = 0;
        int mBestScore = 0;
        String mBestScoreSeason = "";
        int hoalvl = 0;
        // get azerit neck lvl
        CharacterItem neckItem = member.getItemByPost("neck");
        if (neckItem != null) {
            hoalvl = neckItem.getAzeriteLevel();
        }
        if(guildMember)
        {
            iLevel = String.format("%.2f", member.getItemLevel());
            race = member.getRace().getName();        
            if (!guildRank.contains(member.getRank().getId()))
            {
                guildRank.add(member.getRank().getId());
                %>guildRanks.push('<%= member.getRank().getTitle() %>');<%
            }   
            if (!mClass.contains(className)) 
            {
                mClass.add(className);
                txtClass.add(member.getMemberClass().getName());
                %>mClass.push('<%= className %>');
                textClass.push('<%= member.getMemberClass().getName() %>');<%
            }
            if (!races.contains(member.getRace().getName()))
            {
                races.add(member.getRace().getName());
                %>races.push("<%= member.getRace().getName() %>");<%
            }
            mScore = (int) member.getMythicScoreAll();
            mBestScore = (int) member.getBestMythicScore();
            mBestScoreSeason = member.getBestMythicScoreSeason();
        } %>
        members.push({   
            'name': '<%= member.getName() %>', 
            'class': '<%= className %>', 
            'spec': '<%= specName %>', 
            'level': <%= member.getLevel() %>, 
            'img': '<%= member.getThumbnailURL() %>',
            'rol': '<%= member.getActiveSpec().getSpec().getRole() %>', 
            'member_id': <%= member.getId() %>, 
            'gRank_id': <%= member.getRank().getId() %>,
            'gRank_title': '<%= member.getRank().getTitle() %>',
            'iLevel': '<%= iLevel %>',
            'race': "<%= race %>",
            'hoalvl': "<%= hoalvl %>",
            'mythicScore': '<%= mScore %>',
            'bestMythicScore': {
                'score': '<%= mBestScore %>',
                'season': '<%= mBestScoreSeason %>'
            }
        });
    <%} //End foreach member%>
<%} //End if members not null%>
