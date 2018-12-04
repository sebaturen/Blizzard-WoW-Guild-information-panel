//Send a ajax request to server...
$(document).ready(function() {
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
        $("#dateLimitSelect").prop("disabled", !$('#limitDate').is(':checked'));
    });
});