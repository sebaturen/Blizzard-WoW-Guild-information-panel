//Send a ajax request to server...
$(document).ready(function() {
    
    $('input[name="event_date"]').daterangepicker({
        timePicker: true,
        singleDatePicker: true,
        locale: {
            format: 'Y-MM-D HH:mm:ss'
          }
    }, function(start, end, label) {
        console.log("A new date selection was made: " + start.format('YYYY-MM-DD'));
    });

    //Create poll form!
    $("#event_create_form").submit(function(event) {
        event.preventDefault();
        //Save event date
        $(".ajaxLoad").show();
        $("#create_event_result").hide();
        $.ajax({
            method: "POST",
            url: "event_controller.jsp",
            data: { event_action: "createEvent",
                    val: $(this).serializeArray() },
            dataType: "json"
        })
        .done(function(mData) {
            if(mData.status == "ok")
            {
               $("#create_event_result").html("<div class='alert alert-primary' role='alert'><a href='../events.jsp' class='alert-link'>New event</a> was created!</div>");
            }
            else
            {
                console.log(mData);
                $("#create_event_result").html("<div class='alert alert-danger' role='alert'>"+ mData.msg +"</div>");
            }
        })
        .fail(function() {
            $("#create_event_result").html("<div class='alert alert-danger' role='alert'>Failed to save a new event</div>");
        })
        .always(function() {
            $("#create_event_result").show();
            $(".ajaxLoad").hide();
        });
    });
});
