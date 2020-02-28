String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}; 

window.onmousemove = function (e) {
    $(".item-floting-desc").each(function(i, tip) {
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
    /*Mose over and leave in affix detail*/
    $('#navbarSupportedContent')
    .on('mouseover', '#token_price', function() {
        /*Load wow token prices */
        $.getScript('assets/js/wowToken/wow_token_list.jsp', function() {
            console.log('token price history ready');

            var dataPoints = [];

            var options =  {
                animationEnabled: true,
                theme: "light2",
                title: {
                    text: "Change history"
                },
                axisX: {
                    valueFormatString: "DD HH:mm",
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
            
            for (var i = 0; i < wow_token_history.length; i++) {
                dataPoints.push({
                    x: new Date(wow_token_history[i].date),
                    y: wow_token_history[i].gold
                });
            }

            console.log(dataPoints);

            $("#tokenGraph").CanvasJSChart(options);



            //weekRun(keystone_run);
            //bestRun(keystone_best_run);
            $("#afixLoad").hide();
        });
        $(".tooltip-wow_token").show();
    })
    .on('mouseleave', '#token_price', function() {
        $(".tooltip-wow_token").hide();
    });
});