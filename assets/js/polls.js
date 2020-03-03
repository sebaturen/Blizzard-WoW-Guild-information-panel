//Send a ajax request to server...
var currentAdd = [];
var optSelecStatus = [];
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
    $(document).on("click", ".poll_opt_mask", function(d) {
        clickOption(this);
    });
    $('.poll_opt_mask')
        .mouseover(function() {
            $("#btn_poll_"+ $(this).data("poll_id") +"_opt_"+ $(this).data("poll_op_id")).html('<i class="artOfWar-icon">&#xe802;</i>');
        })
        .mouseleave(function() {
            if($("#btn_poll_"+ $(this).data("poll_id") +"_opt_"+ $(this).data("poll_op_id")).hasClass("btn-outline-success"))
            {
                $("#btn_poll_"+ $(this).data("poll_id") +"_opt_"+ $(this).data("poll_op_id")).html('');
            }
    });
    //Add option button
    $('.addOption').click(function() {
        var isMultiSelect = ($(this).parent(".poll_options").data("is_multi"));
        var pollId = ($(this).parent(".poll_options").data("poll_id"));
        if(isNaN(currentAdd[pollId]))
            currentAdd[pollId] = 1; 
        else 
            currentAdd[pollId]++;
        var userAdd = "<span id='poll_"+ pollId +"_opt_add_"+ currentAdd[pollId] +"_user_"+ userId +"'"+
                      "  class='mem-name character-"+ userClass +" char-name'>&nbsp;";
        userAdd += (userClass == "BATTLE_TAG")? "<img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>":"";
        userAdd += userNameShow +",";
        userAdd += "</span>";
        var optionAdded = " <div id='poll_"+ pollId +"_opt_add_"+ currentAdd[pollId] +"' class='poll_"+ pollId +"_opts' data-opt_add_id='"+ currentAdd[pollId] +"'>"+
                                "<div class='poll_option pointer row'>"+
                                    "<div class='col-2 optSelectControl'>"+
                                        "<div type='button' class='btn_poll_option btn btn-success'><i class='artOfWar-icon'>&#xe802;</i></div>"+
                                        "<button onclick='removeAddOption(this)' type='button' class='btn_poll_option btn btn-outline-danger deleteOption' data-poll_id='"+ pollId +"' data-opt_add_id='poll_"+ pollId +"_opt_add_"+ currentAdd[pollId] +"'><i class='artOfWar-icon'>&#xe803;</i></button>"+
                                    "</div>"+
                                    "<div class='col-9 infoOpt'><input class='form-control infoOptText' type='text' value='' name='option_1' id='poll_"+ pollId +"_opt_add_"+ currentAdd[pollId] +"_input'/></div>"+
                                    "<div class='col-1'><button onclick='addOption(this)' class='btn btn-outline-warning right' data-opt_add_id='poll_"+ pollId +"_opt_add_"+ currentAdd[pollId] +"' data-poll_id='"+ pollId +"'>Save</button></div>"+
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

function clickOption(elem)
{
    //Option selected info
    var pollId = $(elem).data("poll_id");
    var idClicked = $(elem).data("poll_op_id");
    var isClicked = ($(elem).attr("data-is_enable") == 'true');
    var isMultiSelect = ($("#poll_"+ pollId +"_options").data("is_multi"));
    var btnOption = $("#btn_poll_"+ pollId +"_opt_"+ idClicked);

    console.log("isClicked ", isClicked);

    /* Check and uncheck... */

    //Change CSS from clicked element
    if (!isClicked) {
        console.log("Agregando tiket!");
        // Remove all other preview selection if have not have a multiple selection
        if (!isMultiSelect) {
            $("div[id^='poll_"+ pollId +"_opt_']").each(function() {

                var opt = $(this).data('opt_id');

                if ($("#mask_poll_"+ pollId +"_opt_"+ opt).attr("data-is_enable") == 'true') {


                    $("#btn_poll_"+ pollId +"_opt_"+ opt).removeClass("btn-success");
                    $("#btn_poll_"+ pollId +"_opt_"+ opt).addClass("btn-outline-success");
                    $("#btn_poll_"+ pollId +"_opt_"+ opt).html('');
                    $("#mask_poll_"+ pollId +"_opt_"+ opt).attr("data-is_enable","false");
                    removeResult(pollId, opt, "#poll_"+ pollId +"_opt_"+ opt +"_user_"+ userId);
    
                    // Remove user
                    $("#poll_"+ pollId +"_opt_"+ opt +"_user_"+ userId).hide();
                }
            });
        }
        // Set select
        $(btnOption).addClass("btn-success");
        $(btnOption).removeClass("btn-outline-success");
        $(btnOption).html('<i class="artOfWar-icon">&#xe802;</i>');
        $(elem).attr("data-is_enable","true");
        addResult(pollId, idClicked, "#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId);

        //Add a current user clicked.
        var userAdd = "<span id='poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId +"'"+
                        "  class='mem-name character-"+ userClass +" char-name'>&nbsp;";
        userAdd += (userClass == "BATTLE_TAG")? "<img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>":"";
        userAdd += userNameShow +",";
        userAdd += "</span>";

        if ($("#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId).length) {
            $("#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId).show();
        } else {
            $("#users_poll_"+ pollId +"_opt_"+ idClicked).append(userAdd);
        }
    } else {
        // Remove selection
        console.log("Sacando tiket!");
        $(btnOption).removeClass("btn-success");
        $(btnOption).addClass("btn-outline-success");
        $(btnOption).html('');
        $(elem).attr("data-is_enable","false");
        removeResult(pollId, idClicked, "#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId);

        // Remove user
        $("#poll_"+ pollId +"_opt_"+ idClicked +"_user_"+ userId).hide();
    }
}

function removeAddOption(elem)
{
    $("#"+ $(elem).data("opt_add_id")).remove();
    $("#addOption_poll_"+ $(elem).data("poll_id")).attr("disabled", false);
    currentAdd[$(elem).data("poll_id")]--;
}

function removeOption(elem)
{
    if(confirm("You want delete this options?"))
    {
        var pollId = $(elem).data("poll_id");
        var pollOptId = $(elem).data("opt_id_db");
        $(".ajaxLoad").show();
        $.ajax({
            method: "POST",
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
                alert(mData.msg);
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
    var addOptId = $(elem).data("opt_add_id");
    if(optionText.length >= 3)
    {
        var pollId = $(elem).data("poll_id");
        $(".ajaxLoad").show();
        $.ajax({
            method: "POST",
            url: "userpanel/poll_controller.jsp",
            data: { poll_action: "addOption", 
                    poll_id: pollId, 
                    poll_opt_add_text: optionText },
            dataType: "json"
        })
        .done(function(mData) {
            if(mData.status == "ok")
            {
                $("#"+ addOptId).remove();
                //Add a current user clicked.
                var userAdd = "<span id='poll_"+ pollId +"_opt_"+ mData.option_id +"_user_"+ userId +"'"+
                              "  class='mem-name character-"+ userClass +" char-name'>";
                userAdd += (userClass == "BATTLE_TAG")? "<img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>":"";
                userAdd += userNameShow +",";
                userAdd += "</span>";
                
                var optAdd = "<div id='poll_"+ pollId +"_opt_"+ mData.option_id +"' class='poll_opts divder row justify-content-between poll_"+ pollId +"_opts' data-opt_id='"+ mData.option_id +"'>"+
                                "<div class='poll_option pointer col-10'>"+
                                    "<div id='mask_poll_"+ pollId +"_opt_"+ mData.option_id +"' class='poll_opt_mask' data-poll_id='"+ pollId +"' data-poll_op_id='"+ mData.option_id +"' data-is_enable='true'></div>"+
                                    "<div id='btn_poll_"+ pollId +"_opt_"+ mData.option_id +"' class='btn_poll_option btn btn-success'><i class='artOfWar-icon'>&#xe802;</i></div>"+
                                    "&nbsp;<span>"+ optionText +"</span>"+
                                "</div>"+
                                "<div class='col-2'><button onclick='removeOption(this)' type='button' class='btn_poll_option_delete btn btn-outline-danger deleteOption' data-poll_id='"+ pollId +"' data-opt_id='poll_"+ pollId +"_opt_"+ mData.option_id +"' data-opt_id_db='"+ mData.option_id +"'><i class='artOfWar-icon'>&#xe803;</i></button></div>"+
                                "<div id='users_poll_"+ pollId +"_opt_"+ mData.option_id +"' class='userSelect'>"+ userAdd +"</div>"+
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
            alert("Fail to save poll option - Error 001");
            $(elem).attr("enable", false);
            $(inputOption).attr("enable", false);
            $("#"+ $(elem).data("opt_add_id")).children(".poll_option").children(".optSelectControl").children(".deleteOption").attr("disabled", false);
        })
        .always(function() {
            $(".ajaxLoad").hide();            
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
    var buttonSelectResult = $("#poll_"+pollId+"_opt_"+optId).children(".poll_option").children(".btn_poll_option");
    var failAction = function() {
        $(buttonSelectResult).removeClass("btn-success");
        $(buttonSelectResult).addClass("btn-outline-success");
        $(buttonSelectResult).html('');
        $(userShowDiv).remove();        
    };
    $.ajax({
        method: "POST",
        url: "userpanel/poll_controller.jsp",
        data: { poll_action: "addResult", 
                poll_id: pollId, 
                poll_opt_id: optId },
        dataType: "json"
    })
    .done(function(mData) {
        if(mData.status != true)
        {
            alert("Failed to save your choice - Error: 201");
            console.log(mData);
            failAction();
        }
    })
    .fail(function() {
        alert("Failed to save your choice - Error: 202");
        failAction();
    })
    .always(function() {
        $(".ajaxLoad").hide();        
    });
}

function removeResult(pollId, optId, userShowDiv, forceShow = false)
{    
    $(".ajaxLoad").show();
    var buttonSelectResult = $("#poll_"+pollId+"_opt_"+optId).children(".poll_option").children(".btn_poll_option");
    var failAction = function() {
        $(userShowDiv).show();
        $(buttonSelectResult).removeClass("btn-outline-success");
        $(buttonSelectResult).addClass("btn-success");
        $(buttonSelectResult).html('<i class="artOfWar-icon">&#xe802;</i>');          
    };
    $.ajax({
        method: "POST",
        url: "userpanel/poll_controller.jsp",
        data: { poll_action: "removeResult", 
                poll_id: pollId, 
                poll_opt_id: optId },
        dataType: "json"
    })
    .done(function(mData) {
        if(mData.status == true)
        {            
            $(userShowDiv).remove();
        }
        else
        {
            if(!forceShow)
            {
                alert("Fail to save your choise - Error: 301");
                failAction();
            }
            console.log(mData);
        }
    })
    .fail(function() {
        alert("Fail to save your choise - Error: 302");
        if(!forceShow)
        {
            failAction();
        }
    })
    .always(function() {
        $(".ajaxLoad").hide();        
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
            }
        }
    });
}

function removePoll(pollId) {
    $(".ajaxLoad").show();
    $.ajax({
        method: "POST",
        url: "userpanel/poll_controller.jsp",
        data: { poll_action: "removePoll", 
                poll_id: pollId},
        dataType: "json"
    })
    .done(function(mData) {
        if(mData.status == "ok")
        {
            document.location.reload()
        }
        else
        {
            console.log(mData);
            alert("Fail to remove poll - Error 004");
        }
    })
    .fail(function() {
        alert("Fail to remove poll - Error 001");
    })
    .always(function() {
        $(".ajaxLoad").hide();            
    });
}

function enablePoll(pollId) {
    $(".ajaxLoad").show();
    $.ajax({
        method: "POST",
        url: "userpanel/poll_controller.jsp",
        data: { poll_action: "enablePoll", 
                poll_id: pollId},
        dataType: "json"
    })
    .done(function(mData) {
        if(mData.status == "ok")
        {
            document.location.reload()
        }
        else
        {
            console.log(mData);
            alert("Failed to enable poll - Error 004");
        }
    })
    .fail(function() {
        alert("Failed to enable poll - Error 001");
    })
    .always(function() {
        $(".ajaxLoad").hide();            
    });
}

function disablePoll(pollId) {
    $(".ajaxLoad").show();
    $.ajax({
        method: "POST",
        url: "userpanel/poll_controller.jsp",
        data: { poll_action: "disablePoll", 
                poll_id: pollId},
        dataType: "json"
    })
    .done(function(mData) {
        if(mData.status == "ok")
        {
            document.location.reload()
        }
        else
        {
            console.log(mData);
            alert("Failed to disable poll - Error 004");
        }
    })
    .fail(function() {
        alert("Failed to disable poll - Error 001");
    })
    .always(function() {
        $(".ajaxLoad").hide();            
    });
}