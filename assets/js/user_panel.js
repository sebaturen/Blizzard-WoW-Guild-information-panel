//Send a ajax request to server...
$(document).ready(function() 
{
    $(".main_char").click(function() 
    { 
        var actualMain = getActualMain();
        var idChange = $(this).data("member_id");
        $.ajax({
            method: "GET",
            url: "userpanel/run_user_main.jsp?id="+ idChange,
            dataType: "json"
        })
        .done(function(mData) {
            if(mData.status != "ok")
            {
                alert("Failed to save Main Character - "+ mData.error);
                setMain(actualMain);
            }
        })
        .fail(function() {
            alert("Failed to save Main character - Error 003");
            setMain(actualMain);
        });
        setMain(idChange);
    });
});

function getActualMain()
{
    var id;
    $(".main_char").each(function() 
    {
        if($(this).html() == "")
        id = $(this).data("member_id");
    });
    return id;
}

function setMain(id)
{
    $(".main_char").each(function() 
    {
        $(this).html("");
        if($(this).data("member_id") == id) 
            $(this).html("&#xe801;");
        else
            $(this).html("&#xe800;");
    });
}