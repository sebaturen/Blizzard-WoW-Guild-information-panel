<%@include file="../../../includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.Member" %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
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
    for(Member member : members.getMembersList())
    {
        String className = ((member.getMemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase(); 
        String specName = ((member.getActiveSpec().getName()).replaceAll("\\s+","-")).toLowerCase(); 
        String iLevel = "0";
        String race = "";
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
                txtClass.add(member.getMemberClass().getEnName());
                %>mClass.push('<%= className %>');
                textClass.push('<%= member.getMemberClass().getEnName() %>');<%
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
            'gRank_id': <%= member.getRank().getId() %>,
            'gRank_title': '<%= member.getRank().getTitle() %>',
            'iLevel': '<%= iLevel %>',
            'race': '<%= race %>',
        });
    <%} //End foreach member%>
<%} //End if members not null%>
