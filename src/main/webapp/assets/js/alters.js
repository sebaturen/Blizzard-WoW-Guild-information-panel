$(document).ready(function() {
    // Load a member details
    $.get("rest/guild/alters?locale=" + Cookies.get('locale'), function (data) {
        console.log("members is load complete", data);
        renderAlters(".container", data);
    }).always(function () {
        $("#loading").remove();
    });
});