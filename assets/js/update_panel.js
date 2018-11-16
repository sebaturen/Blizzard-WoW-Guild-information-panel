//Send a ajax request to server...
$(document).ready(function() {
    $("#buttonForceUpdate").click(function() {
        $.ajax({
            url: "run_update.jsp",
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
    });
});
