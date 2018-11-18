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
    //Races filter selected
    $('#racesSelect').change(function () { runFilter(); });
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
        //Equipo!!!
        $('.infoMember').append('<div class="itemsMember"></div>');
        $('.itemsMember').append(renderItem(member));
        //Status!!!
        $('.infoMember').append('<div class="statsMember"></div>');
        $('.statsMember').append(renderStat(member));
                    
}

function renderItem(member)
{
    var itemsLeft = {
        'head': member.items.head,
        'neck': member.items.neck,
        'shoulder': member.items.shoulder,
        'back': member.items.back,
        'chest': member.items.chest,
        'shirt': member.items.shirt,
        'tabard': member.items.tabard,
        'wrist': member.items.wrist
    }
    var itemsRight = {
        'hands': member.items.hands,
        'waist': member.items.waist,
        'legs': member.items.legs,
        'feet': member.items.feet,
        'finger1': member.items.finger1,
        'finger2': member.items.finger2,
        'trinket1': member.items.trinket1,
        'trinket2': member.items.trinket2
    }
    var itemsArm = {
        'mainHand': member.items.mainHand,
        'offHand': member.items.offHand
    }
    var outEquip = '<div class="equip row"><div class="itemsLeft col">';
    jQuery.each( itemsLeft, function(i, val) 
    {
        if(val !== undefined && val !== null)
        {
            outEquip += '<div class="itemDetail '+ i +' row">'+
                            '<div class="itemIcon left" style="background-image: url('+ val.img +');"></div>'+
                            '<div class="itemDesc ">'+ 
                                '<p class="quality-'+ val.quality +'">'+val.name +'</p>'+
                                val.ilevel+
                            '</div>'+
                        '</div>';            
        }
    });
    outEquip += '</div><div class="itemsRight col">';
    jQuery.each( itemsRight, function(i, val) 
    {
        if(val !== undefined && val !== null)
        {
            outEquip += '<div class="itemDetail '+ i +' row justify-content-end">'+
                            '<div class="itemDesc left">'+ 
                                '<p class="quality-'+ val.quality +'">'+val.name +'</p>'+
                                val.ilevel+
                            '</div>'+
                            '<div class="itemIcon" style="background-image: url('+ val.img +');"></div>'+
                        '</div>';             
        }
    });
    outEquip += '</div></div><div class="equip row itemsArm">';
    if(itemsArm.mainHand !== undefined && itemsArm.mainHand !== null)
    {
        outEquip += '<div class="itemDetail mainHand col">'+
                        '<div class="itemIcon" style="background-image: url('+ itemsArm.mainHand.img +');"></div>'+
                        '<div class="itemDesc">'+ 
                            '<p class="quality-'+ itemsArm.mainHand.quality +'">'+itemsArm.mainHand.name +'</p>'+
                            itemsArm.mainHand.ilevel+
                        '</div>'+
                    '</div>';
    }
    outEquip += '<div class="itemDetail offHand col">';
    if(itemsArm.offHand !== undefined && itemsArm.offHand !== null)
    {
        outEquip += '<div class="itemIcon" style="background-image: url('+ itemsArm.offHand.img +');"></div>'+
                        '<div class="itemDesc">'+ 
                            '<p class="quality-'+ itemsArm.offHand.quality +'">'+itemsArm.offHand.name +'</p>'+
                            itemsArm.offHand.ilevel+
                        '</div>';
    }    
    outEquip += '</div></div>';
    return outEquip;
}

function renderStat(member)
{
    var iconMedia = '<div class="Media-image"><span class="Icon Media-icon"><svg class="Icon-svg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 64 64"><use xlink:href="/assets/img/icons/Icon.svg#PUT_STAT"></use></svg></span></div>';
    var statsMember = {
        'health': member.stats.health,
        'powerType': member.stats.powerType,
        'power': member.stats.power,
        'primaryStat': member.stats.primaryStatType,
        'primaryStatVal': member.stats.primaryStat,
        'stamina': member.stats.stamina,
        'critical-strike': member.stats.crit,
        'haste': member.stats.haste, 
        'mastery': member.stats.mastery, 
        'versatility': member.stats.versatility };
    var outStatus = '';
    jQuery.each( statsMember, function(i, val) 
    {
        if(i === 'powerType')
        {
            i = statsMember.powerType;
            val = statsMember.power;
        }
        if(i === 'primaryStat')
        {
            i = statsMember.primaryStat;
            val = statsMember.primaryStatVal;
        }
        if(i !== 'power' && i !== 'primaryStatVal')
        {
            outStatus += '<div class="Icon--'+ i +' statDetail">'+ iconMedia.replace(/PUT_STAT/g, i) +
                            '<div class="Media-text">'+
                                '<span>'+ val +'</span><div class="bold">'+ i.capitalize() +'</div>'+
                            '</div>'+
                        '</div>';            
        }
    });
    return outStatus;
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
    var class_select_filter = $("#classSelect option:selected").data("desclass");
    var levelOrd_filter = $('#levelSelect')[0].selectedIndex;
    var levelInp_filter = $('#levelInput').val();
    var iLevelOrd_filter = $('#ilevelSelect')[0].selectedIndex;
    var iLevelInp_filter = $('#ilevelInput').val();
    var nameInp_filter = $('#nameInput').val();
    var race_filter = $('#racesSelect')[0].selectedIndex;
    var race_select_filter = $("#racesSelect option:selected").text();
    //Apli filters
    //-----------GUILD RANK
    var preMemberGRank = [];
    if(gRank_filter !== 0) {
        jQuery.each( visualMember, function(i, val) 
        {
            if(val.gRank == gRank_select_filter)               
                preMemberGRank.push(val); 
        });
    } else preMemberGRank = visualMember;
    //-----------CLASS
    var preMemberClass = [];
    if(class_filter !== 0) {
        jQuery.each( preMemberGRank, function(i, val) 
        {
            if(val.class == class_select_filter)               
                preMemberClass.push(val); 
        });
    } else preMemberClass = preMemberGRank;
    //-----------LEVEL
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
    //-----------ITEM LEVEL
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
    //-----------NAME
    var preMemberName = [];
    if(nameInp_filter.length > 0) {
        jQuery.each( preMemberIlevl, function(i, val) 
        {
            var memName = val.name.toLowerCase();
            var inputName = nameInp_filter.toLowerCase();
            if((memName).indexOf(inputName) != -1)          
                preMemberName.push(val);                   
        });
    } else preMemberName = preMemberIlevl;
    //-----------RACES
    var preMemberRace = [];
    if(race_filter !== 0) {
        jQuery.each( preMemberName, function(i, val) 
        {
            if(val.race == race_select_filter)          
                preMemberRace.push(val);                
        });
    } else preMemberRace = preMemberName;
    
    visualMember = preMemberRace;
    putMembers(visualMember);
}