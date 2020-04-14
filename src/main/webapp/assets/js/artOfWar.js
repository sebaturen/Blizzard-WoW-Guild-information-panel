String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
};

window.onmousemove = function (e) {
    $(".item-floating-desc").each(function(i, tip) {
        var tooltip = $(tip);
        var left = (e.clientX + tooltip.width() + 20 < document.body.clientWidth)?
                (e.clientX + 20 + "px") : (e.clientX - tooltip.width() + "px");
        var top = (e.clientY + tooltip.height() + 20 < document.body.clientHeight)?
                (e.clientY + 20 + "px") : (document.body.clientHeight + 5 - tooltip.height() + "px");

        tooltip.css("left", left);
        tooltip.css("top", top);

    });
};

$(document).ready(function() {

    // Nav bar
    let navbar = $(".navbar");
    let sticky = navbar.offset().top;
    let copNav = navbar.clone().attr('id', 'stickyNav').hide();
    $("body").append(copNav);
    window.onscroll = function() {
        if (window.pageYOffset > sticky) {
            copNav.show();
        } else {
            copNav.hide();
        }
    };

    // Mouse over....
    $('body')
        // Key affix
        .on('mouseover', '.key_affix_img', function() {
            $("#affix_name").text($(this).data("name"));
            $("#affix_desc").text($(this).data("desc"));
            $(".tooltip-affix").show();
        })
        // Key affix
        .on('mouseleave', '.key_affix_img', function() {
            $(".tooltip-affix").hide();
        })
        // Token price
        .on('mouseover', '.token_price', function() {
            /* Load wow token prices */
            $.get('rest/wow_token/history?max=10', function (data) {
                console.log('token price history ready');
                var dataPoints = [];

                var options =  {
                    animationEnabled: true,
                    theme: "light2",
                    title: {
                        text: "Change history"
                    },
                    axisX: {
                        valueFormatString: "DD / HH:mm",
                    },
                    axisY: {
                        title: "GOLD",
                        titleFontSize: 24,
                        includeZero: false
                    },
                    data: [{
                        type: "spline",
                        yValueFormatString: "$#,###.##",
                        dataPoints: dataPoints
                    }]
                };

                for (var i = 0; i < data.length; i++) {
                    dataPoints.push({
                        x: new Date(data[i].date),
                        y: data[i].price/10000
                    });
                }

                console.log(dataPoints);

                $("#tokenGraph").CanvasJSChart(options);

            });
            $("#affix_name").text("");
            $("#affix_desc").text("");
            $("#wow_token_graph").show();
            $(".tooltip-affix").show();
        })
        // Tooltips
        .on('mouseleave', '.token_price', function() {
            $("#wow_token_graph").hide();
            $(".tooltip-affix").hide();
        });
});

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

        // Tank information
        // Members information
        jQuery.each( keyRun.TANK, function(j, mem) {
            $('.key_characters', prepareKey).append(renderMemberInfo(mem).clone());
        });

        // Members information
        jQuery.each( keyRun.HEALER, function(j, mem) {
            $('.key_characters', prepareKey).append(renderMemberInfo(mem).clone());
        });

        // Members information
        jQuery.each( keyRun.DAMAGE, function(j, mem) {
            $('.key_characters', prepareKey).append(renderMemberInfo(mem).clone());
        });

        // Affixes information
        let prepareAffix = $("#key_char_detail").clone().attr('id', 'affixes').removeClass('pjInfo').removeClass('row');
        let affixContent = '<div class="key_affixes row">';
        jQuery.each( keyRun.affixes, function(k, affix) {
            affixContent += '<div class="col"><img class="key_affix_img" src="'+ affix.media +'" data-name="'+ affix.name +'" data-desc="'+ affix.desc +'" /></div>';
        });
        $(prepareAffix).html(affixContent);
        $('.key_characters', prepareKey).append(prepareAffix);

        $(prepareKey).show();

        // Divide keys
        group = $(group).add(prepareKey.clone());
        i = parseInt(i) + 1;
        if (i%3 === 0) {
            let keyCont = $('<div class="row key_runs"></div>');
            $(keyCont).append($(group).clone());
            $(loadLocation).append($(keyCont).clone());
            group = $();
        }
    });
    if ($(group).length > 0) {
        let keyCont = $('<div class="row key_runs"></div>');
        $(keyCont).append($(group).clone());
        $(loadLocation).append($(keyCont).clone());
    }
    $(loadLocation).show();
}

function renderMemberInfo(mem) {
    let isMain = ((mem.main_guild)? "<i class='main_char artOfWar-icon'>&#xe801;</i>":"");
    let prepareMember = $("#key_char_detail").clone().attr('id', 'character_'+ mem.id);
    $('.key_char_name', prepareMember).addClass('character-'+ mem.class);
    $('.key_char_name', prepareMember).html(isMain + mem.name +'<div class="char-realm">'+ mem.realm +'</div>');
    $('.key_char_rol_img', prepareMember).attr('src', 'assets/img/icons/'+ mem.rol +'.png');
    $('.key_char_spec_img', prepareMember).attr('src', 'assets/img/classes/specs/spec_'+ mem.class +'_'+ mem.spec +'.png');
    $('.key_char_ilvl', prepareMember).text(mem.iLvl);
    if (mem.main_guild) {
        prepareMember = prepareMember.wrap("<a href='members.jsp?id="+ mem.id +"'></a>").parent();
    }
    return prepareMember;
}