$(document).ready(function() {
    /*Mose over and leave in affix detail*/
    $('.container')
        .on('mouseover', '.nameDescTooltip', function() {
            $("#affix_name").text($(this).data("name"));
            $("#affix_desc").text($(this).data("desc"));
            $(".tooltip-affix").show();
        })
        .on('mouseleave', '.nameDescTooltip', function() {
            $(".tooltip-affix").hide();
        });

    $.get("rest/mythicPlus/"+$("#info").data("id")+"?locale="+ Cookies.get('locale'), function(data) {
        console.log(data);
        renderRuns(data, "#mythic_week");
    }).always(function () {
        $("#loading").remove();
    });
});