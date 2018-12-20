//Send a ajax request to server...
var arg = 0;
$(document).ready(function() {
    $("#buttonForceUpdate").click(function() { runAjaxUpdate(); });
    $("#buttonForceUpdateGuildProfile").click(function() { arg = "GuildProfile"; runAjaxUpdate(); });
    $("#buttonForceUpdateGuildMembers").click(function() { arg = "GuildMembers"; runAjaxUpdate(); });
    $("#buttonForceUpdateCharacterInfo").click(function() { arg = "CharacterInfo"; runAjaxUpdate(); });
    $("#buttonForceUpdateGuildChallenges").click(function() { arg = "GuildChallenges"; runAjaxUpdate(); });
    $("#buttonForceUpdateGuildNews").click(function() { arg = "GuildNews"; runAjaxUpdate(); });
    $("#buttonForceUpdateWowToken").click(function() { arg = "WowToken"; runAjaxUpdate(); });
    $("#buttonForceUpdateUsersCharacters").click(function() { arg = "UsersCharacters"; runAjaxUpdate(); });
    $("#buttonForceUpdateGuildProgression").click(function() { arg = "GuildProgression"; runAjaxUpdate(); });
});

function runAjaxUpdate()
{
    //Disable buttons
    $("#buttonForceUpdate").attr("disabled", true);
    $("#buttonForceUpdateGuildProfile").attr("disabled", true);
    $("#buttonForceUpdateGuildMembers").attr("disabled", true);
    $("#buttonForceUpdateCharacterInfo").attr("disabled", true);
    $("#buttonForceUpdateGuildChallenges").attr("disabled", true);
    $("#buttonForceUpdateGuildNews").attr("disabled", true);
    $("#buttonForceUpdateWowToken").attr("disabled", true);
    $("#buttonForceUpdateUsersCharacters").attr("disabled", true);
    $("#buttonForceUpdateGuildProgression").attr("disabled", true);
    //run ajax
    $.ajax({
        url: "run_update.jsp?arg="+ arg,
        success: function(){
            var webSocket = new WebSocket('ws://artofwar.cl:80/broadcasting');

            webSocket.onerror = function(event) {
                onError(event);
            };

            webSocket.onopen = function(event) {
                onOpen(event);
            };

            webSocket.onmessage = function(event) {
                onMessage(event);
            };

            function onMessage(event) {
                $("#updateCode").append(event.data +"<br>");
            }

            function onOpen(event) {
                $("#updateCode").append(event.data);
            }

            function onError(event) {
                alert(event.data);
            }

             function send(v) {
                webSocket.send(v);
            }
        }
    });
}
