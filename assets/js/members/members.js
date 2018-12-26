//Members jquery
var visualMember;
var memberDetailLoad; //use in ajax server request.
visualMember = members;
//------------FILTERS-------------------------//
function loadFilterOptions() {
    guildRanks.forEach(function(val) {
        $('#guildRankSelect').append('<option>'+ val +'</option>');
    });
    jQuery.each( mClass, function(i, val) 
    {
        $('#classSelect').append('<option data-desclass="'+ val +'">'+ textClass[i] +'</option>');
    });
    races.forEach(function(val) {
        $('#racesSelect').append('<option>'+ val +'</option>');
    });
    //ADD LISTENER
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
}
//------------SORT---------------------------//
//Sort by rank
$("#rankColum").click(function() {
    visualMember.sort(function(a, b) {
        var rankA = parseInt(a.gRank_id);
        var rankB = parseInt(b.gRank_id);
        if (rankA > rankB) {
            return 1;
        }
        if (rankA < rankB) {
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
        var levA = parseInt(a.level);
        var levB = parseInt(b.level);
        if (levA < levB) {
            return 1;
        }
        if (levA > levB) {
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
    if(memberDetailLoad !== undefined)
    {
        memberDetailLoad.abort();
    }
    if(lastIdClick != $(this).data('id'))
    {
        lastIdClick = $(this).data('id');
        var memInfo = visualMember[lastIdClick];
        if(memInfo.iLevel > 0) {
            var internalId = $(this).data('internal_id');
            showMemberDetail(this, memInfo.img, internalId);
        }            
    } 
    else
    {
        lastIdClick = -1;
        $('.memDetail').remove();
    }
});

function showMemberDetail(tr, avImg, memeberId)
{
    $('.memDetail').remove(); //clear all other member info if is show~
    $(tr).after("<tr class='memDetail'><td class='memContent' colspan='6'></td></tr>");
    var fullSizeImg = (avImg).replace("-avatar.jpg", "-main.jpg");
    $('.memContent').css('background-image', 'url(' + fullSizeImg + ')');
    $('.memContent').append('<div id="memberDetailLoad" class="row justify-content-md-center"><div class="loader"></div></div>');
    //If member information not exist in this window
    var memDetail = window['member_'+ memeberId];
    if(memDetail === undefined )
    {
        memberDetailLoad = $.getScript('assets/js/members/memberDetail.jsp?id='+ memeberId, function() {
            memDetail = window['member_'+ memeberId];
            prepareRender(memDetail);
        });
    }
    else
    {
        prepareRender(memDetail);        
    }
}

function prepareRender(mInfo)
{    
    $('.memContent').append('<div class="infoMember"></div>');
    //Equipo!!!
    $('.infoMember').append('<div class="itemsMember"></div>');
    $('.itemsMember').append(renderItem(mInfo));
        //Tooltip information
        $(".itemDetail")
            .mouseover(function () 
            {
                $(".tooltip-"+ $(this).data("item")).show();
            })
            .mouseleave(function () 
            {
                $(".tooltip-"+ $(this).data("item")).hide();
            });
    //Status!!!
    $('.infoMember').append('<div class="statsMember"></div>');
    $('.statsMember').append(renderStat(mInfo));
    //Spec!!!    
    $('.infoMember').append('<div class="spec" style="clear:both;"></div>');
    $('.spec').append(renderSpec(mInfo));
        //Tooltip information
        $(".spell_inf")
            .mouseover(function () 
            {
                $(".tooltip-"+ $(this).data("tier")).show();
            })
            .mouseleave(function () 
            {
                $(".tooltip-"+ $(this).data("tier")).hide();
            });
    $('#memberDetailLoad').remove();
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
    };
    var itemsRight = {
        'hands': member.items.hands,
        'waist': member.items.waist,
        'legs': member.items.legs,
        'feet': member.items.feet,
        'finger1': member.items.finger1,
        'finger2': member.items.finger2,
        'trinket1': member.items.trinket1,
        'trinket2': member.items.trinket2
    };
    var itemsArm = {
        'mainHand': member.items.mainHand,
        'offHand': member.items.offHand
    };
    var getItemDesc = function(item)
    {
        var gemIcon = '';
        if(item.gem !== undefined && item.gem !== null)
        {
            gemIcon += '<img src="'+ item.gem.img +'" class="gemIcon"/>';
        }
        return '<p class="quality-'+ item.quality +'">'+ item.name +'</p>'+
                    item.ilevel +' '+ gemIcon;
    };
    var getTooltip = function(item, post)
    {
        //Gem info
        var gemInfo = '';
        if(item.gem !== undefined && item.gem !== null)
        {
            gemInfo += '<p><img src="'+ item.gem.img +'" class="gemIcon"/> '+ item.gem.bonus +'</p>';
        }
        //Azerita Level
        var azeritLevel = '';
        if(item.azerite_level > 0)
        {
            azeritLevel = '<p class="tooltip-yellow"> Azerite Power Level '+ item.azerite_level +'</p>';
        }
        //Azerita power
        var azeritPower = '';
        if(item.azerite_power !== undefined && item.azerite_power !== null)
        {
            var i = 0;
            jQuery.each( item.azerite_power, function(pos, azPw) 
            {
                if(azPw.name !== undefined && azPw.name !== null)
                {                    
                    azeritPower += '<p>- '+ azPw.name +' <img class="azeritaSkillImg" src="'+ azPw.img +'"></p>';
                    azeritPower += '<div class="azeritaPowerDesc tooltip-yellow">'+ azPw.desc +'</div>';
                }
                else
                {
                    azeritPower += '<p class="undefinedAzPower">- Undefined</p>';
                }
                i++;
            });
            if(i > 0) azeritPower = '<div class="tooltip-yellow">Active Azerita Powers:</div><div class="azPower">' + azeritPower +'</div>';
        }
        //Spell
        var spellDesc = '';
        if(item.spell !== undefined && item.spell !== null)
        {
            spellDesc = '<p class="tooltip-yellow itemSpellDetail">'+ item.spell.action +': '+ item.spell.desc +"</p>";
        }        
        //Stats info
        var stats = '';
        if(item.armor > 0) stats += '<li>'+ item.armor +' Armor</li>';
        jQuery.each( item.stats, function(stat, amount) 
        {
            stats += '<li>+'+ amount +' '+ stat +'</li>';
        });        
        return '<div  class="item-floting-desc tooltip-'+ post +'">'+
                    '<div class="itemDesc tooltipDesc">'+ 
                        '<p class="quality-'+ item.quality +'">'+ item.name +'</p>'+
                        '<p class="tooltip-yellow">Item Level '+ item.ilevel +'</p>'+
                        azeritLevel +
                        '<p>'+ post.capitalize() +'</p>'+
                        '<ul>'+ stats +'</ul>'+
                        gemInfo +
                        azeritPower +
                        spellDesc +
                    '</div>'+
                '</div>';        
    };
    var outEquip = '<div class="equip row"><div class="itemsLeft col">';
    //LEFT ITEMS----------------------------------------------------------------
    jQuery.each( itemsLeft, function(i, val) 
    {
        if(val !== undefined && val !== null)
        {   
            outEquip += '<div class="item_title itemDetail '+ i +' row" data-item="'+ i +'">'+
                            '<div class="itemIcon left" style="background-image: url('+ val.img +');"></div>'+
                            '<div class="itemDesc ">'+ getItemDesc(val) +'</div>'+
                        '</div>'+ getTooltip(val, i);
        }
    });
    outEquip += '</div><div class="itemsRight col">';
    //RIGHT ITEMS---------------------------------------------------------------
    jQuery.each( itemsRight, function(i, val) 
    {
        if(val !== undefined && val !== null)
        {
            outEquip += '<div class="itemDetail '+ i +' row justify-content-end" data-item="'+ i +'">'+
                            '<div class="itemDesc left">'+ getItemDesc(val) +'</div>'+
                            '<div class="itemIcon" style="background-image: url('+ val.img +');"></div>'+
                        '</div>'+ getTooltip(val, i);       
        }
    });
    outEquip += '</div></div><div class="equip row itemsArm">';
    //MAIN HAND-----------------------------------------------------------------
    if(itemsArm.mainHand !== undefined && itemsArm.mainHand !== null)
    {
        outEquip += '<div class="itemDetail mainHand col" data-item="mainHand">'+
                        '<div class="itemIcon" style="background-image: url('+ itemsArm.mainHand.img +');"></div>'+
                        '<div class="itemDesc">'+ getItemDesc(itemsArm.mainHand) +'</div>'+
                    '</div>'+ getTooltip(itemsArm.mainHand, "mainHand");   
    }
    outEquip += '<div class="itemDetail offHand col"  data-item="offHand">';
    //OFF HAND------------------------------------------------------------------
    if(itemsArm.offHand !== undefined && itemsArm.offHand !== null)
    {
        outEquip += '<div class="itemIcon" style="background-image: url('+ itemsArm.offHand.img +');"></div>'+
                        '<div class="itemDesc">'+ getItemDesc(itemsArm.offHand) +'</div>'+
                        getTooltip(itemsArm.offHand, "offHand"); 
    }    
    outEquip += '</div></div>';
    return outEquip;
}

function renderStat(member)
{
    var iconMedia = '<div class="Media-image"><span class="Icon Media-icon"><svg class="Icon-svg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 64 64"><use xlink:href="assets/img/icons/stat-icon.svg#PUT_STAT"></use></svg></span></div>';
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
        'versatility': member.stats.versatility 
    };
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

function renderSpec(member)
{
    var spellTier = {
        'tier_0': member.active_spec_spells.spells.sp_0,
        'tier_1': member.active_spec_spells.spells.sp_1,
        'tier_2': member.active_spec_spells.spells.sp_2,
        'tier_3': member.active_spec_spells.spells.sp_3,
        'tier_4': member.active_spec_spells.spells.sp_4,
        'tier_5': member.active_spec_spells.spells.sp_5,
        'tier_6': member.active_spec_spells.spells.sp_6
    };
    var output ='<div class="active_spec_spells row">'+
                    '<div class="col-3 spell_inf" data-tier="spec_des">'+
                        '<img src="assets/img/classes/specs/spec_'+ member.m_info.class_slug +'_'+ member.m_info.spec_slug +'.png" style="width: 40px;"/>'+
                        '&nbsp;'+ member.m_info.spec +
                        '<div  class="item-floting-desc tooltip-spec_des">'+
                            '<div class="itemDesc tooltipDesc">'+ 
                                '<p>'+ member.m_info.spec +'</p>'+
                                '<p class="tooltip-yellow itemSpellDetail">'+  member.active_spec_spells.desc +"</p>"+
                            '</div>'+
                        '</div>'+
                    '</div>'+
                    '<div class="col">';
    jQuery.each( spellTier, function(i, val) 
    {
        if(val.name === 'UNSELECTED')
        {
            output += 
                '<div class="spell_inf" data-tier="'+ i +'">'+
                    '<img class="key_affix_img img_spell" src="assets/img/icons/inv_misc_questionmark.jpg"/>'+
                '</div>';
        }
        else
        {
            output += 
                '<div class="spell_inf" data-tier="'+ i +'">'+
                    '<img class="key_affix_img img_spell" src="'+ val.img +'"/>'+
                    '<div  class="item-floting-desc tooltip-'+ i +'">'+
                        '<div class="itemDesc tooltipDesc">'+ 
                            '<p>'+ val.name +'</p>'+
                            '<p class="tooltip-yellow itemSpellDetail">'+ val.action +': '+ val.desc +"</p>"+
                        '</div>'+
                    '</div>'+
                '</div>';   
        }
    });    
    output += '</div></div>';
    return output;
}

function putMembers(vMem)
{
    $("#charContent").html("");
    jQuery.each( vMem, function(i, val) 
    {
        var outForm = 
                '<tr class="pjInfo pointer" data-id="'+i+'" data-internal_id="'+ val.member_id +'">'+
                    '<td scope="row"><img class="img_profile" src="'+ val.img +'" /></td>'+
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
            if(val.gRank_title == gRank_select_filter)               
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