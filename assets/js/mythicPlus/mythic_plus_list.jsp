<%@include file="../../../includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.KeystoneDungeon.KeystoneAffix" %>
<%@ page import ="com.blizzardPanel.gameObject.KeystoneDungeon.KeystoneDungeonRun" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<jsp:useBean id="mPlus" class="com.blizzardPanel.viewController.MythicPlusControl" scope="application"/>
<%!
    public String getMemberDetail(CharacterMember mem)
    {
        //General info
        String className = mem.getMemberClass().getSlug();
        String specName = mem.getActiveSpec().getSpec().getSlug();
        String specRol = mem.getActiveSpec().getSpec().getRole();
        Boolean isMain = mem.isGuildMember();

        //Generate render
        return 
            "'name': \""+ mem.getName() +"\","+
            "'class_name': \""+ className +"\","+
            "'spec_name': \""+ specName +"\","+
            "'rol': '"+ specRol +"',"+
            "'i_level': '"+ String.format("%.0f", mem.getItemLevel()) +"',"+
            "'is_main': '"+ isMain +"'";
    }
%>
var keystone_run = [];
var keystone_affixes = [];

<%  List<KeystoneAffix> kAfixIndex = new ArrayList<>();
    int i = 0;
    for(KeystoneDungeonRun keyRun : mPlus.getLastKeyRun()) { int[] durTime = keyRun.getTimeDuration(); %>
        keystone_run [<%= i++ %>] = {
            'run_id': '<%= keyRun.getId() %>',
            'key_lvl': '<%= keyRun.getKeystoneLevel() %>',
            'complete_date': '<%= keyRun.getCompleteDate() %>',
            'upgrade_key': '<%= keyRun.getUpgradeKey() %>',
            'duration_h': '<%= durTime[0] %>',
            'duration_m': '<%= durTime[1] %>',
            'duration_s': '<%= durTime[2] %>',
            'up_down': '<%= ((keyRun.isCompleteInTime())? "downgrade":"upgrade") %>',
            'map_id': '<%= keyRun.getKeystoneDungeon().getMapId() %>',
            'map_name': "<%= keyRun.getKeystoneDungeon().getName() %>",
            'mem': {
            <%
                out.write("0: { "+ getMemberDetail(keyRun.getTank()) +" }, ");
                out.write("1: { "+ getMemberDetail(keyRun.getHealr()) +" }, ");
                int j = 2;
                for(CharacterMember m : keyRun.getDPS())
                    out.write( (j++) +": { "+ getMemberDetail(m) +" }, ");
            %>
            },
            'affix': [
            <% 
                for(KeystoneAffix kAfix : keyRun.getAffixes())
                {
                    //if afix not is save
                    if(!kAfixIndex.contains(kAfix))
                        kAfixIndex.add(kAfix);
                    out.write(kAfix.getId() +", ");
                }                
            %>
            ]
        };
<%  } //foreach keyRun
    
    for(KeystoneAffix kAfix : kAfixIndex) {%>
        keystone_affixes[<%= kAfix.getId() %>] = {
            'name': "<%= kAfix.getName() %>",
            'icon_url': "<%= kAfix.getIcon() %>",
            'desc': "<%= kAfix.getDescription() %>"
        };
<%  } //foreach key afixes%>