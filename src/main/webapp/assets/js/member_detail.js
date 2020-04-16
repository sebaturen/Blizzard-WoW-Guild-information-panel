$(document).ready(function() {
    // Load mythic runs
    $.get("rest/mythicPlus/"+$("#info").data("id")+"?locale="+ Cookies.get('locale'), function(data) {
        console.log(data);
        renderRuns(data, "#mythic_week");
    }).always(function () {
        $("#loading").remove();
    });

    // Load alters
    let urlParams = new URLSearchParams(window.location.search);
    $.get("rest/guild/alter/"+ urlParams.get('id') +"?locale=" + Cookies.get('locale'), function (data) {
        console.log("alters is load complete", data);
        renderAlters("#alters", data);
    }).always(function () {
        $("#altLoading").remove();
    });
});