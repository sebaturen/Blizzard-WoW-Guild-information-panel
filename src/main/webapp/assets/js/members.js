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

    //------------SORT---------------------------//
    // Sort by rank
    $("#rankColum").on("click", function() {
        console.log("rank sort?");
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
    // Sort by Hoa Level
    $("#hoalvl").on("click", function() {
        visualMembers.sort(function(a, b) {
            let aHoalv = parseFloat(a.info.hoa_lvl);
            let bHoaIlv = parseFloat(b.info.hoa_lvl);
            if (aHoalv < bHoaIlv) {
                return 1;
            }
            if (aHoalv > bHoaIlv) {
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
                aIlv = parseFloat(a.info.mythicScore.all);
            }
            if (typeof b.info.mythicScore != 'undefined') {
                bIlv = parseFloat(b.info.mythicScore.all);
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
        let outForm = (fullLoad)? '<a href="?id='+ val.id +'">':'';
         outForm += '<div class="row pjInfo pointer" data-id="'+ val.id +'">';
         if (typeof val.media.avatar != 'undefined') {
             outForm += '<div class="col"><img class="img_profile" src="'+ val.media.avatar +'" /></div>';
         } else {
             outForm += '<div class="col"><img class="img_profile" src="https://render-us.worldofwarcraft.com/character/tichondrius/00/000000000-avatar.jpg?alt=/shadow/avatar/2-1.jpg" /></div>';
         }
         outForm += '<div class="col character-'+ val.info.class.id +'">'+ val.name +'</div>'+
                    '<div class="col"><img src="assets/img/classes/class_'+ val.info.class.id +'.png" style="width: 22px;"/></div>'+
                    '<div class="col">'+ val.info.lvl +'</div>'+
                    '<div class="col d-none d-md-block"><img src="assets/img/classes/specs/spec_'+ val.info.class.id +'_'+ val.spec.id +'.png" style="width: 22px;"/> <img src="assets/img/icons/'+ val.spec.rol +'.png" style="width: 22px;"/></div>';
        if(typeof val.info.equip_lvl != 'undefined') {

            // Equip LVL
            outForm += '<div class="col d-none d-md-block">'+ val.info.avg_lvl +'<br>';
            if (val.info.equip_lvl != val.info.avg_lvl) {
                outForm += '<div class="bestScoreMythc">'+ val.info.equip_lvl +'</div></div>';
            } else {
                outForm += '</div>';
            }

            // HOA lvl
            outForm += '<div class="col d-none d-md-block">';
            if (typeof val.info.hoa_lvl != 'undefined') {
                outForm += val.info.hoa_lvl;
            }
            outForm += '</div>';

            // Raider IO
            let mScore = '';
            outForm += '<div class="col d-none d-md-block">';
            if (typeof val.info.mythicScore != 'undefined' && val.info.mythicScore.all > 0) {
                mScore = parseInt(val.info.mythicScore.all);
                outForm += parseInt(val.info.mythicScore.all) +'<br>';
            }
            if (typeof val.info.bestMythicScore != 'undefined' && val.info.bestMythicScore.score != val.info.mythicScore.all) {
                if (mScore != parseInt(val.info.bestMythicScore.score)) {
                    outForm += '<div class="bestScoreMythc" style="color: '+ val.info.bestMythicScore.scoreColor +'">'+ parseInt(val.info.bestMythicScore.score) +' - '+ val.info.bestMythicScore.season.name +'</div>';
                }
            }
            outForm += '</div>';
        }
        outForm += '</div>';
        outForm += (fullLoad)? '</a>':'';
        $("#charContent").append(outForm);
    });
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

    // Apply filters
    //-----------GUILD RANK
    let preMemberGRank = [];
    if(rank_filter !== -1) {
        jQuery.each( visualMembers, function(i, val) {
            if(parseInt(val.rank) === rank_filter) {
                preMemberGRank.push(val);
            }
        });
    } else preMemberGRank = visualMembers;
    //-----------CLASS
    let preMemberClass = [];
    if(class_filter !== -1) {
        jQuery.each( preMemberGRank, function(i, val) {
            if(parseInt(val.info.class.id) === class_filter) {
                preMemberClass.push(val);
            }
        });
    } else preMemberClass = preMemberGRank;
    //-----------LEVEL
    let preMemberLevel = [];
    if(levelOrd_filter !== -1) {
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
        jQuery.each( preMemberName, function(i, val)
        {
            if(val.info.race_id == race_filter)
                preMemberRace.push(val);
        });
    } else preMemberRace = preMemberName;

    // Ready to print!
    visualMembers = preMemberRace;
    addMember(visualMembers);

}