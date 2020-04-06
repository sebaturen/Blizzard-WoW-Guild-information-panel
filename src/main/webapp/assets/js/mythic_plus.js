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
    $("#bestRun").html("<div class='key_title'> <h1>Best Runs</h1>\n\
                        <h3 class='key_divide_title'>(Season 4)</h3></div>");
    $("#bestRun").append(renderRuns(keyRuns));
    $("#bestRun").show();
}

function weekRun(keyRuns) {
    $("#runList").html("<div class='key_title'><h1 class='key_divide_title'>Runs of the week</h1></div>");
    $("#runList").append(renderRuns(keyRuns));
    $("#runList").show();
}

function renderRuns(keyRuns) {
    var out = '';
    jQuery.each( keyRuns, function(i, keyRun) {
        i = parseInt(i);
        if(keyRun !== undefined ) {
            if (i === 0) { out += '<div class="row ">'; } else if (i%3 === 0) { out += '</div><div class="row">'; }
            out +=
                `
                    <div id='key_run_`+ keyRun.id +`' class='key_run_group dungeon-challenge col'>
                        <div class='key_run_dun_img dungeon-challenge-img' style='background-image: url("assets/img/dungeon/`+ keyRun.map_id +`.jpg");'>
                            <div class='key_run_lvl'>`+ keyRun.lvl +`</div>
                            <h2 class='dung-title'>`+ keyRun.map_name +`</h2>
                        </div>
                        <p class='group-time key-`+ keyRun.is_completed_within_time +`'>
                            [`+ keyRun.duration_h +`h:`+ keyRun.duration_m +`m:`+ keyRun.duration_s +`s]`+ ((keyRun.upgrade_key > 0)? ` (+`+ keyRun.upgrade_key +`)`:``) +`
                        </p>
                        <p class='key-date'>`+ keyRun.complete_date +`</p>
                        <table class='table table-dark character-tab'>
                            <thead>
                                <tr>
                                    <th scope='col'>Name</th>
                                    <th scope='col'>Role</th>
                                    <th scope='col'>iLevel</th>
                                </tr>
                            </thead>
                            <tbody>
                `;
            jQuery.each( keyRun.members, function(j, mem) {
                var isMain = ((mem.main_guild)? "<i class='main_char artOfWar-icon'>&#xe801;</i>":"");
                out +=
                    `
                        <tr>
                            <td class='character-`+ mem.class +`'>
                                `+ isMain +``+ mem.name +`
                                <div class='char-realm'>`+ mem.realm +`</div>
                            </td>
                            <td>
                                <img src='assets/img/icons/`+ mem.rol +`.png' style='width: 22px;'/>
                                <img src='assets/img/classes/specs/spec_`+ mem.class +`_`+ mem.spec +`.png' style='width: 22px;'/>
                            </td>
                            <td>`+ mem.iLvl +`</td>
                        </tr>
                            
                    `;
            });
            out += `<tr><td colspan='3' class='key_affixes'>`;
            jQuery.each( keyRun.affixes, function(k, affix) {
                out += `<img class='key_affix_img' src='`+ affix.media +`' data-name='`+ affix.name +`' data-desc='`+ affix.desc +`' />`;
            });
            out +=
                `
                    </td>
                    </tr>
                    </tbody>
                    </table>
                    </div> 
                `;
        }
    });
    out += "</div>"; //<!-- close last 'i' div open -->

    return out;
}