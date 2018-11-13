//Encript the password in Client browser, and send MD5 to server.
$(document).ready(function() {
    $("#accesInfo").submit(function(e) {
        e.preventDefault();
        var md5Pass = $.md5($('#inputPass').val());
        var email = $('#inputEmail').val();
        $('#password').val(md5Pass);
        $('#email').val(email);
        this.submit();
    });
});
