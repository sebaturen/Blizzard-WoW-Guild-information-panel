<%@include file="/includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.Member" %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<jsp:useBean id="members" class="com.blizzardPanel.viewController.Members"/>
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
        'int_id': '<%= member.getId() %>',
    });
<%}%>