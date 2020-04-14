$(document).ready(function() {
    // Load a member details
    $.get("rest/guild/alters?locale=" + Cookies.get('locale'), function (data) {
        console.log("members is load complete", data);
        loadAlters(data);
    }).always(function () {
        $("#loading").remove();
    });
});

function loadAlters(data) {
    let out = '';
    jQuery.each(data.users, function(i, user) {
        out += '<div class="user">';
        out += '<img id="user_'+ user.id +'" src="assets/img/icons/Battlenet_icon_flat.svg" style="width: 40px" />'+ user.battle_tag
        out += '<span class="right_small_date">['+ user.last_modified +']</span>'
        out += '<div class="character-tab">';
        jQuery.each(user.characters, function (j, char) {
            out += '<a href="members.jsp?id='+ char.id +'"><div id="character_'+ char.id +'" class="row pjInfo">'+
                        '<div class="col"><p class="character-'+ char.class +' char-name">'+ ((char.isMain)? '<i class="artOfWar-icon">&#xe801;</i> ':'')+ char.name +'</p></div>' +
                        '<div class="col"><img src="assets/img/classes/specs/spec_'+ char.class +'_'+ char.spec +'.png" style="width: 22px;"/></div>' +
                        '<div class="col">'+ char.lvl +'</div>' +
                        '<div class="col">'+ char.title +'</div> ' +
                '</div></a>';
        });
        out += "</div></div>";
    });
    $(".container").append(out);
}