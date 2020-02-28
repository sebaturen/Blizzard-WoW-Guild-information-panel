//Send a ajax request to server...
$(document).ready(function() {
    $('#sandbox-container .input-group.date').datepicker({ });
    var totalOption = 1;
    $(".addOption").click(function() {
        var addOption = totalOption + 1;
        $("#option_"+ totalOption).after(
            '<div id="option_'+ addOption +'" class="form-group row">'+
                '<label for="example-text-input" class="col-2 col-form-label">Option '+ addOption +'</label>'+
                '<div class="col-10">'+
                    '<input class="form-control" type="text" value="" name="option_'+ addOption +'" id="example-text-input">'+
                '</div>'+
            '</div>');
        totalOption++;
    });
    $("#limitDate").change(function() {
        $("#divDataPicker").toggle();
    });

    //Create poll form!
    $("#poll_create_form").submit(function(event) {
        event.preventDefault();
        //Save poll
        $(".ajaxLoad").show();
        $("#create_poll_result").hide();
        $.ajax({
            method: "POST",
            url: "poll_controller.jsp",
            data: { poll_action: "createPoll",
                    val: $(this).serializeArray() },
            dataType: "json"
        })
        .done(function(mData) {
            if(mData.status == "ok")
            {
               $("#create_poll_result").html("<div class='alert alert-primary' role='alert'><a href='../polls.jsp' class='alert-link'>New poll</a> was created!</div>");
            }
            else
            {
                console.log(mData);
                $("#create_poll_result").html("<div class='alert alert-danger' role='alert'>"+ mData.msg +"</div>");
            }
        })
        .fail(function() {
            $("#create_poll_result").html("<div class='alert alert-danger' role='alert'>Failed to save a new poll</div>");
        })
        .always(function() {
            $("#create_poll_result").show();
            $(".ajaxLoad").hide();
        });
    });
});
