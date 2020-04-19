let currentUser;
$(document).ready(function() {
    // Get an polls list
    $.get("rest/polls/list", function(data) {
        console.log(data);
        currentUser = data.current_user;
        jQuery.each(data.enabled, function(i, val) {
           renderPoll("#enabled_polls", val);
        });
        jQuery.each(data.disabled, function(i, val) {
            renderPoll("#disabled_polls", val);
        });
    }).always(function() {
        $("#loading").hide();
    });

    // Clicked event
    $(document).on("click", ".enabled_poll_opt", function() {
        let optSelect = $(this);
        let pollSelect = $("#poll_"+ optSelect.data("poll_id"));
        let addOption = !optSelect.hasClass("opt_selected");

        pollSelect.find(".loader").show();

        if (addOption) {
            $.ajax({
                url: "rest/polls/"+ optSelect.data("poll_id") +"/"+ optSelect.data("opt_id"),
                type: 'PUT',
                success: function(r) {
                    if (!pollSelect.data("multi_select")) {
                        pollSelect.find(".enabled_poll_opt").removeClass("opt_selected");
                        pollSelect.find(".char_"+ currentUser.id).removeClass("opt_selected");
                    }
                    optSelect.addClass("opt_selected");
                    optSelect.find(".result").append(resultUserDet(currentUser));
                    // Result counter
                    optSelect.data("opt_total_result", optSelect.data("opt_total_result") + 1);
                    pollSelect.data("total_result", pollSelect.data("total_result") + 1);
                    refreshPollPercent(pollSelect);
                }
            }).always(function() {
                pollSelect.find(".loader").hide();
            });
        } else {
            $.ajax({
                url: "rest/polls/"+ optSelect.data("poll_id") +"/"+ optSelect.data("opt_id"),
                type: 'DELETE',
                success: function(r) {
                    if (pollSelect.data("multi_select")) {
                        optSelect.removeClass("opt_selected");
                        optSelect.find(".char_"+ currentUser.id).remove();
                    } else {
                        pollSelect.find(".enabled_poll_opt").removeClass("opt_selected");
                        pollSelect.find(".char_"+ currentUser.id).remove();
                    }
                    // Result counter
                    optSelect.data("opt_total_result", optSelect.data("opt_total_result") - 1);
                    pollSelect.data("total_result", pollSelect.data("total_result") - 1);
                    refreshPollPercent(pollSelect);
                }
            }).always(function() {
                pollSelect.find(".loader").hide();
            });
        }

        console.log("opt!", optSelect.data());
    });
});

function renderPoll(location, pollInfo) {
    let preparePoll = $("#poll_mold").clone().attr('id', 'poll_'+ pollInfo.id);
    preparePoll.attr("data-can_add_more_option", pollInfo.config.can_add_more_option);
    preparePoll.attr("data-end_date", pollInfo.config.end_date);
    preparePoll.attr("data-is_enabled", pollInfo.config.is_enabled);
    preparePoll.attr("data-min_rank", pollInfo.config.min_rank);
    preparePoll.attr("data-multi_select", pollInfo.config.multi_select);
    preparePoll.attr("data-start_date", pollInfo.config.start_date);
    preparePoll.attr("data-total_result", pollInfo.total_result);

    $('.question', preparePoll).text(pollInfo.question);
    let ownerName = pollInfo.owner.name;
    if (pollInfo.owner.type == "BATTLE_TAG") {
        ownerName = "<span><img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 40px'>"+ ownerName +"</span>";
    } else {
        ownerName = "<span class='character-"+ pollInfo.owner.color +"'>"+ ownerName +"</span>";
    }
    $('.detail', preparePoll).html($('.detail', preparePoll).text().replace("{name}", ownerName));
    $('.options', preparePoll).html("");
    pollInfo.options.sort(function (a,b) {
        let countA = a.results.length;
        let countB = b.results.length;
        if (countA < countB) {
            return 1;
        }
        if (countA > countB) {
            return -1;
        }
        // a must be equal to b
        return 0;
    });
    jQuery.each(pollInfo.options.sort(), function(i, opt) {
        let optPoll = $("#opt_mold").clone().attr('id', 'poll_opt_'+ opt.id);
        if (pollInfo.config.is_enabled) {
            if (pollInfo.config.end_date == 0) {
                optPoll.addClass("enabled_poll_opt");
            } else if (new Date().getTime() <= pollInfo.config.end_date) {
                optPoll.addClass("enabled_poll_opt");
            }
        }
        optPoll.attr("data-poll_id", pollInfo.id);
        optPoll.attr("data-opt_id", opt.id);
        optPoll.attr("data-opt_total_result", opt.results.length);
        if (opt.is_selected) {
            optPoll.addClass('opt_selected');
        }
        $('.opt_text', optPoll).text(opt.option);
        let percent = opt.results.length * 100 / pollInfo.total_result;
        if (!isNaN(percent)) {
            $(".progress_percent", optPoll).text(parseInt(percent)+"%");
        }
        $('.progress-bar', optPoll).css('width', percent+"%");
        $('.progress-bar', optPoll).attr('aria-valuenow', percent);
        $('.result', optPoll).html("");
        jQuery.each(opt.results, function (j, result) {
            $('.result', optPoll).append(resultUserDet(result.owner));
        })
        $('.options', preparePoll).append(optPoll.clone());
    });
    $(preparePoll).show();

    $(location).append(preparePoll.clone());
    $(location).show();
}

function resultUserDet(user) {
    let optOwnerName = user.name;
    if (user.type == "BATTLE_TAG") {
        optOwnerName = "<span class='char_"+ user.id +"'><img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 18px'>"+ optOwnerName +"</span>";
    } else {
        optOwnerName = "<span class='char_"+ user.id +" character_name character-"+ user.color +"'>"+ optOwnerName +"</span>";
    }
    return optOwnerName;
}

function refreshPollPercent(poll) {
    poll.find(".option_det").each(function() {
        let percent = $(this).data("opt_total_result") * 100 / poll.data("total_result");
        if (!isNaN(percent)) {
            console.log($(this).data("opt_total_result"), poll.data("total_result"), percent);
            $(this).find(".progress-bar").css('width', percent+"%");
            $(this).find(".progress-bar").attr('aria-valuenow', percent);
            $(this).find(".progress_percent").text(parseInt(percent)+"%");
        } else {
            $(this).find(".progress-bar").css('width', "0%");
            $(this).find(".progress-bar").attr('aria-valuenow', 0);
            $(this).find(".progress_percent").text("");
        }
    });
}