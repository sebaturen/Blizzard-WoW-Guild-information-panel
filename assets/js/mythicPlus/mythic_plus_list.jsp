<%@include file="../../../includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.mythicKeystone.KeystoneAffix" %>
<%@ page import ="com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeonRun" %>
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
            "'is_main': '"+ isMain +"',"+
            "'realm': \""+ mem.getRealm() +"\"";
    }
%>
var keystone_best_run = [];
var keystone_run = [];
var keystone_affixes = [];

<%  List<KeystoneAffix> kAfixIndex = new ArrayList<>();
    int i = 0;
    for(KeystoneDungeonRun keyRun : mPlus.getWeekKeyRun()) { int[] durTime = keyRun.getTimeDuration(); %>
        keystone_run [<%= i++ %>] = {
            'run_id': '<%= keyRun.getId() %>',
            'key_lvl': '<%= keyRun.getKeystoneLevel() %>',
            'complete_date': '<%= keyRun.getCompleteDate() %>',
            'upgrade_key': '<%= keyRun.getUpgradeKey() %>',
            'duration_h': '<%= durTime[0] %>',
            'duration_m': '<%= durTime[1] %>',
            'duration_s': '<%= durTime[2] %>',
            'up_down': '<%= ((keyRun.isCompleteInTime())? "upgrade":"downgrade") %>',
            'map_id': '<%= keyRun.getKeystoneDungeon().getMapId() %>',
            'map_name': "<%= keyRun.getKeystoneDungeon().getName() %>",
            'mem': {
            <%
                if(keyRun.getMembers().size() > 0)
                {
                    int j = 0;
                    if(keyRun.getTank() != null)
                        out.write( (j++) +": { "+ getMemberDetail(keyRun.getTank()) +" }, ");
                    if(keyRun.getHealr() != null)
                        out.write( (j++) +": { "+ getMemberDetail(keyRun.getHealr()) +" }, ");
                    for(CharacterMember m : keyRun.getDPS())
                        out.write( (j++) +": { "+ getMemberDetail(m) +" }, ");                    
                }
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

    i = 0;
    for(KeystoneDungeonRun keyRun : mPlus.getLastBestKeyRun()) { int[] durTime = keyRun.getTimeDuration(); %>
        keystone_best_run [<%= i++ %>] = {
            'run_id': '<%= keyRun.getId() %>',
            'key_lvl': '<%= keyRun.getKeystoneLevel() %>',
            'complete_date': '<%= keyRun.getCompleteDate() %>',
            'upgrade_key': '<%= keyRun.getUpgradeKey() %>',
            'duration_h': '<%= durTime[0] %>',
            'duration_m': '<%= durTime[1] %>',
            'duration_s': '<%= durTime[2] %>',
            'up_down': '<%= ((keyRun.isCompleteInTime())? "upgrade":"downgrade") %>',
            'map_id': '<%= keyRun.getKeystoneDungeon().getMapId() %>',
            'map_name': "<%= keyRun.getKeystoneDungeon().getName() %>",
            'mem': {
            <%                
                if(keyRun.getMembers().size() > 0)
                {
                    int j = 0;
                    if(keyRun.getTank() != null)
                        out.write( (j++) +": { "+ getMemberDetail(keyRun.getTank()) +" }, ");
                    if(keyRun.getHealr() != null)
                        out.write( (j++) +": { "+ getMemberDetail(keyRun.getHealr()) +" }, ");
                    for(CharacterMember m : keyRun.getDPS())
                        out.write( (j++) +": { "+ getMemberDetail(m) +" }, ");    
                }
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