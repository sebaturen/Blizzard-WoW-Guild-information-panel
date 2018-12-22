String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}; 

window.onmousemove = function (e) {
    var x = e.clientX +20,
        y = e.clientY +20;
    $(".item-floting-desc").css("top", y+"px");
    $(".item-floting-desc").css("left", x+"px");
};