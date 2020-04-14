/*Load Mythic Plus information!*/
let countFinish = 0;
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
    $.get("rest/mythicPlus/weekAffix?locale="+ Cookies.get('locale'), function(data) {
        console.log("mythic affix is load complete", data);
        weekAffixes(data);
    }).always(function() {
        complete();
    });
});

function complete() {
    countFinish++;
    if (countFinish == 3) {
        $("#loading").remove();
    }
}

function bestRun(keyRuns) {
    renderRuns(keyRuns, "#bestRun");
}

function weekRun(keyRuns) {
    renderRuns(keyRuns, "#runList");
}

function weekAffixes(affiexs) {
    jQuery.each(affiexs.current, function(i, affix) {
        let affixData = '<div class="col"><img class="key_affix_img" src="'+ affix.media +'" data-name="'+ affix.name +'" data-desc="'+ affix.desc +'" />'+ affix.name +'</div>';
        $("#currentAffixes").append(affixData);
    });
    jQuery.each(affiexs.next, function(i, affix) {
        let affixData = '<div class="col"><img class="key_affix_img" src="'+ affix.media +'" data-name="'+ affix.name +'" data-desc="'+ affix.desc +'" />'+ affix.name +'</div>';
        $("#nextAffixes").append(affixData);
    });
}