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