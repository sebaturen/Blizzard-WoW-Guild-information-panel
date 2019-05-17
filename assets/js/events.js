$(document).ready(function() {

    //User character drag...
    var charSelectId = 0;
    $(".user_char").mousedown(function()
    {
        charSelectId = $(this).data("id");
        var nameChar = $(this).data("name");
        var classChar = $(this).data("class");
        var lvlChar = $(this).data("lvl");
        var sepcsChar = [];
        var specCharHtml = "<div class='specs'>";
        var i = 0;
        while($(this).data("spec-"+ i +"-id") != null)
        {
            sepcsChar[i] = {id: $(this).data("spec-"+ i +"-id"), name: ($(this).data("spec-"+ i +"-slug")) };
            specCharHtml += '<img src="assets/img/classes/specs/spec_'+ classChar +'_'+ sepcsChar[i].name +'.png" style="width: 40px;">&nbsp';
            i++;
        }
        specCharHtml += "</div>";
        var htmlPj = "<div>" +
                        "<div class='character-"+ classChar +"'>"+ nameChar +"</div>"+
                        lvlChar +
                        specCharHtml+
                    "</div>";
        $(".item-floting-desc").html(htmlPj);
        $(".item-floting-desc").show();
    });

    //Mouse release
    $("#ev_container").mouseup(function() {
        $(".item-floting-desc").html();
        $(".item-floting-desc").hide();
    });

    //Mose drop in main zone
    $("#main_zone").mouseup(function()
    {
        var nameChar = $("#char_info_"+ charSelectId).data("name");
        var classChar = $("#char_info_"+ charSelectId).data("class");
        var lvlChar = $("#char_info_"+ charSelectId).data("lvl");
        $("#main_name").html(nameChar);
        $("#main_lvl").html(lvlChar);
        //Specs:
        var sepcsChar = [];
        var specCharHtml = "<div class='specs'>";
        var i = 0;
        while($("#char_info_"+ charSelectId).data("spec-"+ i +"-id") != null)
        {
            sepcsChar[i] = {id: $("#char_info_"+ charSelectId).data("spec-"+ i +"-id"), name: ($("#char_info_"+ charSelectId).data("spec-"+ i +"-slug")) };
            specCharHtml += '<img class="black_white" src="assets/img/classes/specs/spec_'+ classChar +'_'+ sepcsChar[i].name +'.png" style="width: 40px;">&nbsp';
            i++;
        }
        specCharHtml += "</div>";
        $("#main_specs").html(specCharHtml);

        console.log("main zone drop "+ charSelectId);
    });

    //Mose drop in alter zone
    $("#alters_zone").mouseup(function()
    {
        console.log("alter zone drop");
    });
});
