/*Load Mythic Plus information!*/
var countFinish = 0;
$(document).ready(function() {
    /*Load a member details*/
    $.get("rest/mythicPlus/best/4?locale="+ Cookies.get('locale'), function(data) {
        console.log("mythic best load complete", data);
        bestRun(data);
    }).always(function() {
        complete();
    });
    $.get("rest/mythicPlus/weekRuns?locale="+ Cookies.get('locale'), function(data) {
        console.log("mythic is load complete", data);
        weekRun(data);
    }).always(function() {
        complete();
    });

    /*Mose over and leave in affix detail*/
    $('#runList, #bestRun')
        .on('mouseover', '.key_affix_img', function() {
            $("#affix_name").text($(this).data("name"));
            $("#affix_desc").text($(this).data("desc"));
            $(".tooltip-affix").show();
        })
        .on('mouseleave', '.key_affix_img', function() {
            $(".tooltip-affix").hide();
        });
});

function complete() {
    countFinish++;
    if (countFinish == 2) {
        $("#loading").remove();
    }
}

function bestRun(keyRuns) {
    renderRuns(keyRuns, "#bestRun");
}

function weekRun(keyRuns) {
    renderRuns(keyRuns, "#runList");
}

function renderRuns(keyRuns, loadLocation) {
    let group = $();
    jQuery.each(keyRuns, function(i, keyRun) {

        // Prepare all run information
        let prepareKey = $("#mythic_run_mold").clone().attr('id', 'mythic_run_'+ i);
        $('.key_run_dun_img', prepareKey).css('background-image', 'url("assets/img/dungeon/'+ keyRun.map_id +'.jpg")');
        $('.key_run_lvl', prepareKey).text(keyRun.lvl);
        $('.key_dung_title', prepareKey).text(keyRun.map_name);
        $('.key_group_time', prepareKey).addClass('key-'+ keyRun.is_completed_within_time);
        $('.key_group_time', prepareKey).text('['+ keyRun.duration_h +'h:'+ keyRun.duration_m +'m:'+ keyRun.duration_s +'s]'+ ((keyRun.upgrade_key > 0)? ' (+'+ keyRun.upgrade_key +')':''));
        $('.key_date', prepareKey).text(keyRun.complete_date);
        $('#key_char_detail', prepareKey).remove();

        // Members information
        jQuery.each( keyRun.members, function(j, mem) {
            let isMain = ((mem.main_guild)? "<i class='main_char artOfWar-icon'>&#xe801;</i>":"");
            let prepareMember = $("#key_char_detail").clone().attr('id', 'character_'+ j);
            $('.key_char_name', prepareMember).addClass('character-'+ mem.class);
            $('.key_char_name', prepareMember).html(isMain + mem.name +'<div class="char-realm">'+ mem.realm +'</div>');
            $('.key_char_rol_img', prepareMember).attr('src', 'assets/img/icons/'+ mem.rol +'.png');
            $('.key_char_spec_img', prepareMember).attr('src', 'assets/img/classes/specs/spec_'+ mem.class +'_'+ mem.spec +'.png');
            $('.key_char_ilvl', prepareMember).text(mem.iLvl);
            $('.key_characters', prepareKey).append(prepareMember.clone());
        });

        // Affixes information
        let prepareAffix = $("#key_char_detail").clone().attr('id', 'affixes');
        let affixContent = '<td colspan="3" class="key_affixes">';
        jQuery.each( keyRun.affixes, function(k, affix) {
            affixContent += '<img class="key_affix_img" src="'+ affix.media +'" data-name="'+ affix.name +'" data-desc="'+ affix.desc +'" />';
        });
        $(prepareAffix).html(affixContent);
        $('.key_characters', prepareKey).append(prepareAffix);
        
        $(prepareKey).show();

        // Divide keys
        group = $(group).add(prepareKey.clone());
        i = parseInt(i) + 1;
        if (i%3 === 0) {
            let keyCont = $('<div class="row"></div>');
            $(keyCont).append($(group).clone());
            $(loadLocation).append($(keyCont).clone());
            group = $();
        }
    });
    if ($(group).length > 0) {
        let keyCont = $('<div class="row"></div>');
        $(keyCont).append($(group).clone());
        $(loadLocation).append($(keyCont).clone());
    }
    $(loadLocation).show();
}