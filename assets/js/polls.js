//Send a ajax request to server...
var currentAdd = 0;
//Session user
var userId;
var userNameShow;
var userClass;
$(document).ready(function() {  
    //Session user
    userId = $("#currentUserInfo").data("user_id");
    userNameShow = $("#currentUserInfo").data("user_show");
    userClass = $("#currentUserInfo").data("user_class");
    //Options selected
    $('.poll_opt_span, .btn_poll_option')
    .mouseover(function() {
        $(this).parent().children(".btn_poll_option").html('<i class="artOfWar-icon">&#xe802;</i>');
    })
    .mouseleave(function() {
        if($(this).parent().children(".btn_poll_option").hasClass("btn-outline-success"))
        {
            $(this).parent().children(".btn_poll_option").html('');
        }
    })
    .click(function() {
        clickOption($(this).parent());
    });
    //Add option button
    $('.addOption').click(function() {
        currentAdd++;
        var isMultiSelect = ($(this).parent(".poll_options").data("is_multi"));
        var pollId = ($(this).parent(".poll_options").data("poll_id"));
        var userAdd = "<span id='poll_"+ pollId +"_opt_add_"+ currentAdd +"_user_"+ userId +"'"+
                      "  class='mem-name character-"+ userClass +" char-name'>&nbsp;";
        userAdd += (userClass == "BATTLE_TAG")? "<img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>":"";
        userAdd += userNameShow +",";
        userAdd += "</span>";
        var optionAdded = " <div id='poll_"+ pollId +"_opt_add_"+ currentAdd +"' class='poll_"+ pollId +"_opts' data-opt_add_id='"+ currentAdd +"'>"+
                                "<div class='poll_option pointer row'>"+
                                    "<div class='col-2 optSelectControl'>"+
                                        "<button onclick='addOptionButton(this)' type='button' class='btn_poll_option btn btn-success'><i class='artOfWar-icon'>&#xe802;</i></button>"+
                                        "<button onclick='removeAddOption(this)' type='button' class='btn_poll_option btn btn-outline-danger deleteOption' data-poll_id='"+ pollId +"' data-opt_add_id='poll_"+ pollId +"_opt_add_"+ currentAdd +"'><i class='artOfWar-icon'>&#xe803;</i></button>"+
                                    "</div>"+
                                    "<div class='col-9 infoOpt'><input class='form-control infoOptText' type='text' value='' name='option_1' id='poll_"+ pollId +"_opt_add_"+ currentAdd +"_input'/></div>"+
                                    "<div class='col-1'><button onclick='addOption(this)' class='btn btn-outline-warning right' data-opt_add_id='poll_"+ pollId +"_opt_add_"+ currentAdd +"' data-poll_id='"+ pollId +"'>Save</button></div>"+
                                "</div>"+
                                userAdd+
                            "</div>";
        $(this).before(optionAdded);
        //Clear other select if not enable multi select
        if(!isMultiSelect)
        {
            $(this).attr("disabled", true);
            clearSelected(userId, pollId, 0);
        }
    });
    
});

function addOptionButton(elem)
{
    clickOption($(elem).parent(".poll_option"));
}

function clickOption(elem)
{
    var isMultiSelect = ($(elem).parent().parent(".poll_options").data("is_multi"));
    var pollId = ($(elem).parent().parent(".poll_options").data("poll_id"));
    var idClicked = $(elem).parent().data("opt_id");
    if($(elem).children(".btn_poll_option").hasClass("btn-outline-success"))
    {
        if(!isMultiSelect && currentAdd > 0)
        {
            alert("You can't select more options in this poll, remove add option if you want change selection");
        }
        else
        {
            //Change CSS from clicked element
            $(elem).children(".btn_poll_option").removeClass("btn-outline-success");
            $(elem).children(".btn_poll_option").addClass("btn-success");
            $(elem).children(".btn_poll_option").html('<i class="artOfWar-icon">&#xe802;</i>');
            //If multi selecction is disabled, clear old selections
            if(!isMultiSelect) clearSelected(userId, pollId, idClicked);
            //Add a current user clicked.
            var userAdd = "<span id='poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId +"'"+
                          "  class='mem-name character-"+ userClass +" char-name'>&nbsp;";
            userAdd += (userClass == "BATTLE_TAG")? "<img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>":"";
            userAdd += userNameShow +",";
            userAdd += "</span>";
            $(elem).parent().children(".userSelect").append(userAdd);
            addResult(pollId, idClicked, "#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId);
        }
    }
    else
    {
        var selectDiv = $(elem).children(".btn_poll_option");
        $(selectDiv).removeClass("btn-success");
        $(selectDiv).addClass("btn-outline-success");
        $(selectDiv).html('');
        //Remove current user selected
        $("#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId).hide();
        removeResult(pollId, idClicked, selectDiv, "#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId);

    }
}

function removeAddOption(elem)
{
    $("#"+ $(elem).data("opt_add_id")).remove();
    $("#addOption_poll_"+ $(elem).data("poll_id")).attr("disabled", false);
    currentAdd--;
}

function removeOption(elem)
{
    if(confirm("You want delete this options?"))
    {
        var pollId = $(elem).data("poll_id");
        var pollOptId = $(elem).data("opt_id_db");
        $(".ajaxLoad").show();
        $.ajax({
            method: "GET",
            url: "userpanel/poll_controller.jsp",
            data: { poll_action: "removeOption", 
                    poll_id: pollId,
                    poll_opt_id: pollOptId },
            dataType: "json"
        })
        .done(function(mData) {
            $(".ajaxLoad").hide();
            if(mData.status == true)
            {
                $("#"+ $(elem).data("opt_id")).remove();
            }
            else
            {
                console.log(mData);
                alert("Fail to remove option - Error 102");
                $(elem).attr("disabled", false);
            }
        })
        .fail(function() {
            $(".ajaxLoad").hide();
            alert("Fail to remove option - Error 101");
            $(elem).attr("disabled", false);
        });
        $(elem).attr("disabled", true);
    }
}

function addOption(elem)
{
    var inputOption = $("#"+ $(elem).data("opt_add_id")).children(".poll_option").children(".infoOpt").children(".infoOptText");
    var optionText = $(inputOption).val();
    if(optionText.length >= 3)
    {
        var pollId = $(elem).data("poll_id");
        $(".ajaxLoad").show();
        $.ajax({
            method: "GET",
            url: "userpanel/poll_controller.jsp",
            data: { poll_action: "addOption", 
                    poll_id: pollId, 
                    poll_opt_add_text: optionText },
            dataType: "json"
        })
        .done(function(mData) {
            $(".ajaxLoad").hide();
            if(mData.status == "ok")
            {
                $("#poll_"+ pollId +"_opt_add_"+ currentAdd).remove();
                //Add a current user clicked.
                var userAdd = "<span id='poll_"+ pollId +"_opt_"+ mData.option_id +"_user_"+ userId +"'"+
                              "  class='mem-name character-"+ userClass +" char-name'>";
                userAdd += (userClass == "BATTLE_TAG")? "<img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>":"";
                userAdd += userNameShow +",";
                userAdd += "</span>";
                
                var optAdd = "<div id='poll_"+ pollId +"_opt_"+ mData.option_id +"' class='divder row justify-content-between poll_"+ pollId +"_opts' data-opt_id='"+ mData.option_id +"'>"+
                                "<div class='poll_option pointer col-10'>"+
                                    "<button type='button' class='btn_poll_option btn btn-success'><i class='artOfWar-icon'>&#xe802;</i></button>"+
                                    "&nbsp;<span>"+ optionText +"</span>"+
                                "</div>"+
                                "<div class='col-2'><button onclick='removeOption(this)' type='button' class='btn_poll_option_delete btn btn-outline-danger deleteOption' data-poll_id='"+ pollId +"' data-opt_id='poll_"+ pollId +"_opt_"+ mData.option_id +"' data-opt_id_db='"+ mData.option_id +"'><i class='artOfWar-icon'>&#xe803;</i></button></div>"+
                                "<div class='userSelect'>"+ userAdd +"</div>"+
                            " </div>";
                $("#addOption_poll_"+ pollId).before(optAdd);
            }
            else
            {
                console.log(mData);
                alert("Fail to save poll option - Error 002");
                $(elem).attr("enable", false);
                $(inputOption).attr("enable", false);
                $("#"+ $(elem).data("opt_add_id")).children(".poll_option").children(".optSelectControl").children(".deleteOption").attr("disabled", false);                
            }
        })
        .fail(function() {
            $(".ajaxLoad").hide();
            alert("Fail to save poll option - Error 001");
            $(elem).attr("enable", false);
            $(inputOption).attr("enable", false);
            $("#"+ $(elem).data("opt_add_id")).children(".poll_option").children(".optSelectControl").children(".deleteOption").attr("disabled", false);
        });
        $(elem).attr("disabled", true);
        $(inputOption).attr("disabled", true);
        $("#"+ $(elem).data("opt_add_id")).children(".poll_option").children(".optSelectControl").children(".deleteOption").attr("disabled", true);
    }
    else
    {
        alert("Input min 3 characters.");
    }
}

function addResult(pollId, optId, userShowDiv)
{
    $(".ajaxLoad").show();
    $.ajax({
        method: "GET",
        url: "userpanel/poll_controller.jsp",
        data: { poll_action: "addResult", 
                poll_id: pollId, 
                poll_opt_id: optId },
        dataType: "json"
    })
    .done(function(mData) {
        $(".ajaxLoad").hide();
        if(mData.status != true)
        {
            alert("Fail to save your choise - Error: 201");
            console.log(mData);
            $(userShowDiv).remove();
        }
    })
    .fail(function() {
        $(".ajaxLoad").hide();
        alert("Fail to save your choise - Error: 202");
        $(userShowDiv).remove();
    });
}

function removeResult(pollId, optId, selecDiv, userShowDiv, forceShow = false)
{    
    $(".ajaxLoad").show();
    $.ajax({
        method: "GET",
        url: "userpanel/poll_controller.jsp",
        data: { poll_action: "removeResult", 
                poll_id: pollId, 
                poll_opt_id: optId },
        dataType: "json"
    })
    .done(function(mData) {
        $(".ajaxLoad").hide();
        if(mData.status == true)
        {            
            $(userShowDiv).remove();
        }
        else
        {
            if(!forceShow)
            {
                alert("Fail to save your choise - Error: 301");
                console.log(mData);
                $(userShowDiv).show();
                $(selecDiv).removeClass("btn-outline-success");
                $(selecDiv).addClass("btn-success");
                $(selecDiv).html('<i class="artOfWar-icon">&#xe802;</i>');                
            }
        }
    })
    .fail(function() {
        $(".ajaxLoad").hide();
        alert("Fail to save your choise - Error: 302");
        if(!forceShow)
        {
            $(userShowDiv).show();
            $(selecDiv).removeClass("btn-outline-success");
            $(selecDiv).addClass("btn-success");
            $(selecDiv).html('<i class="artOfWar-icon">&#xe802;</i>');            
        }
    });
}

function clearSelected(userId, pollId, idOmited)
{
    $(".poll_"+ pollId +"_opts").each(function() 
    {
        var currentOpt = parseInt($(this).data("opt_id"));
        if(currentOpt != idOmited)
        {
            if(currentOpt >= 0)
            {
                var selecDiv = $(this).children(".poll_option").children(".btn_poll_option");
                $(selecDiv).removeClass("btn-success");
                $(selecDiv).addClass("btn-outline-success");
                $(selecDiv).html('');
                $("#poll_"+ pollId +"_opt_"+ currentOpt +"_user_"+ userId).hide();            
                removeResult(pollId, currentOpt, selecDiv, "#poll_"+ pollId +"_opt_"+ currentOpt +"_user_"+ userId, true);                
            }
        }
    });
}