//Send a ajax request to server...
$(document).ready(function() {
    $("#serverMonitor").click(function() {
        $("#serverMonitor").attr("disabled", true);
        $.ajax({
            url: "run_server_monitor.jsp",
            success: function(){
                var webSocket = new WebSocket('ws://artofwar.cl:80/serverMonitor');

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
                    $("#monitorOutput").append(event.data +"<br>");
                }

                function onOpen(event) {
                    $("#monitorOutput").append(event.data);
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
