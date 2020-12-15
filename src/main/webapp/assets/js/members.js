/*Load members information!*/
let members;
let visualMembers;
let filterLoad = 0;
$(document).ready(function() {
    // Load a member details
    $.get("rest/guild/member/list?locale="+ Cookies.get('locale'), function(data) {
        console.log("members is load complete", data);
        members = data;
        visualMembers = members;
        addMember(visualMembers);
        loadQueryFilters();
    });
    // Prepare filters
    if ($("#fullLoad").length != 0) {
        $.get("rest/guild/ranks", function(data) {
            console.log("ranks", data);
            jQuery.each(data, function(i, val) {
                $("#guildRankSelect").append('<option value="'+ val.lvl +'">'+ val.title +'</option>');
            });
            filterCompleted();
        });
        $.get("rest/playable/class?locale="+ Cookies.get('locale'), function (data) {
            console.log("classes", data);
            jQuery.each(data, function(i, val) {
                $("#classSelect").append('<option value="'+ val.id +'">'+ val.name +'</option>');
            });
            filterCompleted();
        });
        $.get("rest/playable/race?locale="+ Cookies.get('locale'), function (data) {
            console.log("races", data);
            jQuery.each(data, function(i, val) {
                $("#racesSelect").append('<option value="'+ val.id +'">'+ val.name +'</option>');
            });
            filterCompleted();
        })
    }

    // IO Detail
    $(document)
        .on('mouseover', '.charIO', function() {
            let ioDet = $(this).children(".charIODetail").data();
            if (typeof ioDet != 'undefined') {
                $("#affix_name").text("Raider.IO");
                $("#affix_desc").html(
                    `
                        <div class="tankIODet">
                            <img class="" src="assets/img/icons/TANK.png" style="width: 22px;"/>
                            <span class="" style="color: `+ ioDet.io_tank_color +`">`+ parseInt(ioDet.io_tank_score) +`</span>
                        </div>
                        <div class="healrIODet">
                            <img class="" src="assets/img/icons/HEALER.png" style="width: 22px;"/>
                            <span class="" style="color: `+ ioDet.io_healer_color +`">`+ parseInt(ioDet.io_healer_score) +`</span>
                        </div>
                        <div class="dpsIODet">
                            <img class="" src="assets/img/icons/DAMAGE.png" style="width: 22px;"/>
                            <span class="" style="color: `+ ioDet.io_dps_color +`">`+ parseInt(ioDet.io_dps_score) +`</span>
                        </div>
                    `
                );
                $(".tooltip-affix").show();
            }
        })
        .on('mouseleave', '.charIO', function() {
            $(".tooltip-affix").hide();
        })

    //------------SORT---------------------------//
    // Sort by rank
    $("#rankColum").on("click", function() {
        visualMembers.sort(function(a, b) {
            var rankA = parseInt(a.rank);
            var rankB = parseInt(b.rank);
            if (rankA > rankB) {
                return 1;
            }
            if (rankA < rankB) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMembers.sort();
        addMember(visualMembers);
    });
    // Sort by name
    $("#nameColum").on("click", function() {
        visualMembers.sort(function(a, b) {
            if (a.name > b.name) {
                return 1;
            }
            if (a.name < b.name) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMembers.sort();
        addMember(visualMembers);
    });
    // Sort by Class
    $("#classColum").on("click", function() {
        visualMembers.sort(function(a, b) {
            if (a.info.class.id > b.info.class.id) {
                return 1;
            }
            if (a.info.class.id < b.info.class.id) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMembers.sort();
        addMember(visualMembers);
    });
    // Sort by Level
    $("#levelColum").on("click", function() {
        visualMembers.sort(function(a, b) {
            let levA = parseInt(a.info.lvl);
            let levB = parseInt(b.info.lvl);
            if (levA < levB) {
                return 1;
            }
            if (levA > levB) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMembers.sort();
        addMember(visualMembers);
    });
    // Sort by Spec
    $("#specColum").on("click", function() {
        visualMembers.sort(function(a, b) {
            if (a.spec.id > b.spec.id) {
                return 1;
            }
            if (a.spec.id < b.spec.id) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMembers.sort();
        addMember(visualMembers);
    });
    // Sort by iLevel
    $("#iLevelColum").on("click", function() {
        visualMembers.sort(function(a, b) {
            let aIlv = parseFloat(a.info.avg_lvl);
            let bIlv = parseFloat(b.info.avg_lvl);
            if (aIlv < bIlv) {
                return 1;
            }
            if (aIlv > bIlv) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMembers.sort();
        addMember(visualMembers);
    });
    // Sort by Raider.IO
    $("#ioScore").on("click", function() {
        visualMembers.sort(function(a, b) {
            let aIlv = 0;
            let bIlv = 0;
            if (typeof a.info.mythicScore != 'undefined') {
                aIlv = parseFloat(a.info.mythicScore.all.score);
            }
            if (typeof b.info.mythicScore != 'undefined') {
                bIlv = parseFloat(b.info.mythicScore.all.score);
            }
            if (aIlv < bIlv) {
                return 1;
            }
            if (aIlv > bIlv) {
                return -1;
            }
            // a must be equal to b
            return 0;
        });
        visualMembers.sort();
        addMember(visualMembers);
    });

    //------------FILTERS---------------------------//
    $("#membersFilters").on("click", function() {
        $("#formFilter").toggle();
    });
    // Filter name, level, ilvl
    $("#nameInput, #levelInput, #ilevelInput").on("keyup", function() {
        applyFilter();
    });
    // Filter Level
    $("#guildRankSelect, #classSelect, #racesSelect").on("change", function() {
        applyFilter();
    });
    // Filter enable
    $("#levelSelect, #ilevelSelect").on("change", function() {
        $(this).closest(".form-group").find(".vInput").prop("disabled", this.selectedIndex === 0);
    });
});

function filterCompleted() {
    filterLoad++;
    if (filterLoad == 3) {
        $("#membersFilters").show();
    }
}

function addMember(members){
    $("#charContent").html("");
    let fullLoad = ($("#fullLoad").length != 0);
    jQuery.each(members, function(i, val) {
         let outForm = '<a href="?id='+ val.id +'"><div class="row pjInfo pointer" data-id="'+ val.id +'">';
         if (typeof val.media.avatar != 'undefined') {
             outForm += '<div class="col"><img class="img_profile" src="'+ val.media.avatar +'" /></div>';
         } else {
             let defaultAvatar = "";
             if (val.info.faction == "ALLIANCE") {
                 defaultAvatar += "1-";
             } else {
                 defaultAvatar += "2-";
             }
             if (val.info.gender == "MALE") {
                 defaultAvatar += "0.jpg";
             } else {
                 defaultAvatar += "1.jpg";
             }
             outForm += '<div class="col"><img class="img_profile" src="https://render-us.worldofwarcraft.com/character/tichondrius/00/000000000-avatar.jpg?alt=/shadow/avatar/'+ defaultAvatar +'" /></div>';
         }
         outForm += '<div class="col character-'+ val.info.class.id +'">'+ val.name +'</div>'+
                    '<div class="col"><img src="assets/img/classes/class_'+ val.info.class.id +'.png" style="width: 22px;"/></div>'+
                    '<div class="col">'+ val.info.lvl +'</div>'+
                    '<div class="col d-none d-md-block">' +
                        ((val.spec.id > 0)? '<img src="assets/img/classes/specs/spec_'+ val.info.class.id +'_'+ val.spec.id +'.png" style="width: 22px;"/> <img src="assets/img/icons/'+ val.spec.rol +'.png" style="width: 22px;"/>':'')+
                    '</div>';
        if(typeof val.info.equip_lvl != 'undefined') {

            // Equip LVL
            outForm += '<div class="col d-none d-md-block">'+ val.info.avg_lvl +'<br>';
            if (val.info.equip_lvl != val.info.avg_lvl) {
                outForm += '<div class="bestScoreMythc">'+ val.info.equip_lvl +'</div></div>';
            } else {
                outForm += '</div>';
            }

            // Raider IO
            let mScore = '';
            outForm += '<div class="col d-none d-md-block charIO">';
            // Current mythic plus score
            if (typeof val.info.mythicScore != 'undefined') {
                // All (best spec)
                if (val.info.mythicScore.all.score > 0) {
                    mScore = parseInt(val.info.mythicScore.all.score);
                    outForm += '<span style="color: '+ val.info.mythicScore.all.scoreColor +'">'+ mScore +'</span><br>';
                    // for specialization
                    outForm += `<div 
                                class="charIODetail" 
                                data-io_tank_score="`+ val.info.mythicScore.tank.score +`"
                                data-io_tank_color="`+ val.info.mythicScore.tank.scoreColor +`"
                                data-io_healer_score="`+ val.info.mythicScore.healer.score +`"
                                data-io_healer_color="`+ val.info.mythicScore.healer.scoreColor +`"
                                data-io_dps_score="`+ val.info.mythicScore.dps.score +`"
                                data-io_dps_color="`+ val.info.mythicScore.dps.scoreColor +`"
                                style="display: none;"
                            ></div>`;
                }
            }
            // Best season mythic plus
            if (typeof val.info.bestMythicScore != 'undefined' && val.info.bestMythicScore != null) {
                if (typeof val.info.mythicScore != 'undefined' && parseInt(val.info.bestMythicScore.score) > parseInt(val.info.mythicScore.all.score)
                || typeof val.info.mythicScore == 'undefined') {
                    outForm += '<div class="bestScoreMythc" style="color: '+ val.info.bestMythicScore.scoreColor +'">'+ parseInt(val.info.bestMythicScore.score) +' - '+ val.info.bestMythicScore.season.name +'</div>';
                }
            }
            outForm += '</div>';
        }
        outForm += '</div></a>';
        $("#charContent").append(outForm);
    });
}

function loadQueryFilters() {
    let rank = getURLParameter('rank');
    let charClass = getURLParameter('class');
    let lvlType = getURLParameter('lvl_type');
    let lvl = getURLParameter('lvl');
    let ilvlType = getURLParameter('ilvl_type');
    let ilvl = getURLParameter('ilvl');
    let name = getURLParameter('name');
    let race = getURLParameter('race');

    if (rank != null) {
        $('#guildRankSelect').val(rank);
    }
    if (charClass != null) {
        $('#classSelect').val(charClass)
    }
    if (lvlType != null) {
        $('#levelSelect').val(lvlType)
    }
    if (lvl != null) {
        $('#levelInput').val(lvl)
    }
    if (ilvlType != null) {
        $('#ilevelSelect').val(ilvlType)
    }
    if (ilvl != null) {
        $('#ilevelInput').val(ilvl)
    }
    if (name != null) {
        $('#nameInput').val(name)
    }
    if (race != null) {
        $('#racesSelect').val(race)
    }
    applyFilter();
}

function applyFilter() {
    visualMembers = members; //clear previews filters
    let rank_filter = parseInt($('#guildRankSelect').val());
    let class_filter = parseInt($('#classSelect').val());
    let levelOrd_filter = parseInt($('#levelSelect').val());
    let levelInp_filter = parseInt($('#levelInput').val());
    let iLevelOrd_filter = parseInt($('#ilevelSelect').val());
    let iLevelInp_filter = parseInt($('#ilevelInput').val());
    let nameInp_filter = $('#nameInput').val();
    let race_filter = parseInt($('#racesSelect').val());

    let params = {};
    // Apply filters
    //-----------GUILD RANK
    let preMemberGRank = [];
    if(rank_filter !== -1) {
        params["rank"] = rank_filter;
        jQuery.each( visualMembers, function(i, val) {
            if(parseInt(val.rank) === rank_filter) {
                preMemberGRank.push(val);
            }
        });
    } else preMemberGRank = visualMembers;
    //-----------CLASS
    let preMemberClass = [];
    if(class_filter !== -1) {
        params["class"] = class_filter;
        jQuery.each( preMemberGRank, function(i, val) {
            if(parseInt(val.info.class.id) === class_filter) {
                preMemberClass.push(val);
            }
        });
    } else preMemberClass = preMemberGRank;
    //-----------LEVEL
    let preMemberLevel = [];
    if(levelOrd_filter !== -1) {
        params["lvl_type"] = levelOrd_filter;
        params["lvl"] = levelInp_filter;
        jQuery.each( preMemberClass, function(i, val)
        {
            if(levelOrd_filter === 1) //Greater than
                if(val.info.lvl >= levelInp_filter)
                    preMemberLevel.push(val);
            if(levelOrd_filter === 2) //Less than
                if(val.info.lvl <= levelInp_filter)
                    preMemberLevel.push(val);
        });
    } else preMemberLevel = preMemberClass;
    //-----------ITEM LEVEL
    let preMemberIlevl = [];
    if(iLevelOrd_filter !== -1) {
        params["ilvl_type"] = iLevelOrd_filter;
        params["ilvl"] = iLevelInp_filter;
        jQuery.each( preMemberLevel, function(i, val)
        {
            if(iLevelOrd_filter === 1) //Greater than
                if(val.info.avg_lvl >= iLevelInp_filter)
                    preMemberIlevl.push(val);
            if(iLevelOrd_filter === 2) //Less than
                if(val.info.avg_lvl <= iLevelInp_filter)
                    preMemberIlevl.push(val);
        });
    } else preMemberIlevl = preMemberLevel;
    //-----------NAME
    let preMemberName = [];
    if(nameInp_filter.length > 0) {
        params["name"] = nameInp_filter;
        jQuery.each( preMemberIlevl, function(i, val)
        {
            let memName = val.name.toLowerCase();
            let inputName = nameInp_filter.toLowerCase();
            if((memName).indexOf(inputName) !== -1)
                preMemberName.push(val);
        });
    } else preMemberName = preMemberIlevl;
    //-----------RACES
    let preMemberRace = [];
    if(race_filter !== -1) {
        params["race"] = race_filter;
        jQuery.each( preMemberName, function(i, val)
        {
            if(val.info.race_id == race_filter)
                preMemberRace.push(val);
        });
    } else preMemberRace = preMemberName;

    // Ready to print!
    let refresh = window.location.protocol + "//" + window.location.host + window.location.pathname + '?'+ $.param(params);
    window.history.pushState({ path: refresh }, '', refresh);
    visualMembers = preMemberRace;
    addMember(visualMembers);

}