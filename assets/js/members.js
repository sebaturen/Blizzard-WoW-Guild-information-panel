//Members jquery
var visualMember;
$(document).ready(function() {
    visualMember = members;
    putMembers(visualMember);
    //------------FILTERS-------------------------//
    //Filtres show
    $('#membersFilters').click(function() {
        $('#formFilter').toggle('fast');
    });
    //Guild rank filter
    $('#guildRankSelect').change(function () { runFilter(); });
    //Class filter selected
    $('#classSelect').change(function () { runFilter(); });
    //Level filter selected
    $('#levelSelect').change(function() { 
        if($("#levelSelect")[0].selectedIndex !== 0) 
            $( "#levelInput" ).prop( "disabled", false );
        else
            $( "#levelInput" ).prop( "disabled", true );
    });
    //item Level filter selected
    $('#ilevelSelect').change(function() { 
        if($("#ilevelSelect")[0].selectedIndex !== 0)  
            $( "#ilevelInput" ).prop( "disabled", false );
        else
            $( "#ilevelInput" ).prop( "disabled", true );
    });
    //Level filter input
    $('#levelInput').keyup(function () { runFilter(); });
    //Ilevel filter inpute    
    $('#ilevelInput').keyup(function () { runFilter(); });  
    //Name filter inpute    
    $('#nameInput').keyup(function () { runFilter(); });   
    //------------SORT---------------------------//
    //Sort by rank
    $("#rankColum").click(function() {
        visualMember.sort(function(a, b) {
            if (a.gRank > b.gRank) {
                return 1;
            }
            if (a.gRank < b.gRank) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMember.sort();
        putMembers(visualMember);
    });    
    //Sort by name
    $("#nameColum").click(function() {
        visualMember.sort(function(a, b) {
            if (a.name > b.name) {
                return 1;
            }
            if (a.name < b.name) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMember.sort();
        putMembers(visualMember);
    });
    //Sort by Class
    $("#classColum").click(function() {
        visualMember.sort(function(a, b) {
            if (a.class > b.class) {
                return 1;
            }
            if (a.class < b.class) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMember.sort();
        putMembers(visualMember);
    });
    //Sort by Level
    $("#levelColum").click(function() {
        visualMember.sort(function(a, b) {
            if (a.level < b.level) {
                return 1;
            }
            if (a.level > b.level) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMember.sort();
        putMembers(visualMember);
    });
    //Sort by Spec
    $("#specColum").click(function() {
        visualMember.sort(function(a, b) {
            if (a.spec > b.spec) {
                return 1;
            }
            if (a.spec < b.spec) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMember.sort();
        putMembers(visualMember);
    });
    //Sort by iLevel
    $("#iLevelColum").click(function() {
        visualMember.sort(function(a, b) {
            var aIlv = parseFloat(a.iLevel);
            var bIlv = parseFloat(b.iLevel);
            if (aIlv < bIlv) {
                return 1;
            }
            if (aIlv > bIlv) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMember.sort();
        putMembers(visualMember);
    });
    //-----------PJ INFO--------------------------//
    var lastIdClick = -1;
    $('#charContent').on('click', 'tr.pjInfo', function() {
        if(lastIdClick != $(this).data('id'))
        {
            lastIdClick = $(this).data('id');
            var memInfo = visualMember[lastIdClick];
            if(memInfo.iLevel > 0) {
                showMemberDetail(this, memInfo);
            }            
        } 
        else
        {
            lastIdClick = -1;
            $('.memDetail').remove();
        }
    });
});

function showMemberDetail(tr, member)
{
    $('.memDetail').remove(); //clear all other member info if is show~
    $(tr).after("<tr class='memDetail'><td class='memContent' colspan='6'></td></tr>");
    var fullSizeImg = (member.img).replace("-avatar.jpg", "-main.jpg");
    $('.memContent').css('background-image', 'url(' + fullSizeImg + ')');
    $('.memContent').append('<div class="infoMember"></div>');
    //Health info
    $('.infoMember').append('<div class="Media-image">'+
                                '<span class="Icon Icon--health Media-icon"><svg class="Icon-svg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 64 64"><use xlink:href="/assets/img/icons/Icon.svg#health"></use></svg></span>'+
                            '</div>'+
                            '<div class="Media-text">'+
                                '<span>'+ member.health +'</span><div class="font-semp-xSmall-white text-upper">Health</div>'+
                            '</div>');
}

function putMembers(vMem)
{
    $("#charContent").html("");
    jQuery.each( vMem, function(i, val) 
    {
        var outForm = 
                '<tr class="pjInfo" data-id="'+i+'">'+
                    '<td scope="row"><img style="height: 50px;" src="'+ val.img +'" /></td>'+
                    '<td scope="row" class="character-'+ val.class +'">'+ val.name +'</td>'+
                    '<td scope="row"><img src="assets/img/classes/class_'+ val.class +'.png" style="width: 22px;"/></td>'+
                    '<td scope="row">'+ val.level +'</td>'+
                    '<td scope="row"><img src="assets/img/classes/specs/spec_'+ val.class +'_'+ val.spec +'.png" style="width: 22px;"/> <img src="assets/img/icons/'+ val.rol +'.png" style="width: 22px;"/></td>';
        if(val.iLevel > 0) 
        {
            outForm += '<td scope="col">'+ val.iLevel +'</td>';
        }
        outForm += '</tr>';
        $("#charContent").append(outForm);
    });
}

function runFilter()
{
    visualMember = members; //clear previews filters
    var gRank_filter = $('#guildRankSelect')[0].selectedIndex;
    var gRank_select_filter = $("#guildRankSelect option:selected").text();
    var class_filter = $('#classSelect')[0].selectedIndex;
    var class_select_filter = $("#classSelect option:selected").text();
    var levelOrd_filter = $('#levelSelect')[0].selectedIndex;
    var levelInp_filter = $('#levelInput').val();
    var iLevelOrd_filter = $('#ilevelSelect')[0].selectedIndex;
    var iLevelInp_filter = $('#ilevelInput').val();
    var nameInp_filter = $('#nameInput').val();
    //Apli filters
    var preMemberGRank = [];
    if(gRank_filter !== 0) {
        jQuery.each( visualMember, function(i, val) 
        {
            if(val.gRank == gRank_select_filter)               
                preMemberGRank.push(val); 
        });
    } else preMemberGRank = visualMember;
    var preMemberClass = [];
    if(class_filter !== 0) {
        jQuery.each( preMemberGRank, function(i, val) 
        {
            if(val.class == class_select_filter)               
                preMemberClass.push(val); 
        });
    } else preMemberClass = preMemberGRank;
    var preMemberLevel = [];
    if(levelOrd_filter !== 0) {
        jQuery.each( preMemberClass, function(i, val) 
        {
            if(levelOrd_filter == 1) //Greater than
                if(val.level >= levelInp_filter)               
                    preMemberLevel.push(val); 
            if(levelOrd_filter == 2) //Less than
                if(val.level <= levelInp_filter)               
                    preMemberLevel.push(val);                 
        });
    } else preMemberLevel = preMemberClass;
    var preMemberIlevl = [];
    if(iLevelOrd_filter !== 0) {
        jQuery.each( preMemberLevel, function(i, val) 
        {
            var ilvInput = parseFloat(iLevelInp_filter);
            var ilv = parseFloat(val.iLevel);
            if(iLevelOrd_filter == 1) //Greater than
                if(ilv >= ilvInput)               
                    preMemberIlevl.push(val); 
            if(iLevelOrd_filter == 2) //Less than
                if(ilv <= ilvInput)               
                    preMemberIlevl.push(val);                 
        });
    } else preMemberIlevl = preMemberLevel;
    var preMemberName = [];
    if(nameInp_filter.length > 0) {
        jQuery.each( preMemberIlevl, function(i, val) 
        {
            if((val.name).indexOf(nameInp_filter) != -1)          
                    preMemberName.push(val);                 
        });
    } else preMemberName = preMemberIlevl;
    visualMember = preMemberName;
    putMembers(visualMember);
}