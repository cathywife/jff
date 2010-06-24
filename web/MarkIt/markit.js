(function() {


var markit_script = document.getElementById('markit-script');

var JQUERY_URL = markit_script.getAttribute('j');
var JQUERY_UI_URL = markit_script.getAttribute('u');
var CKEDITOR_URL = markit_script.getAttribute('c');

function load_script(url, onload) {
    var s = document.createElement('script');
    s.setAttribute('src', url);
    s.setAttribute('charset', 'UTF-8');
    document.getElementsByTagName('body')[0].appendChild(s);
    s.onload = function() {
        this.parentNode.removeChild(this);
        onload();
    }
}

function main() {
    var $ = jQuery;

    $(markit_script).remove();
    markit_script = null;

    var dialog = $('#markit-dialog');

    if (dialog.length == 0) {
        var dialog_html = '##DIALOG_HTML##';
        dialog = $(dialog_html).appendTo('body').draggable();
        dialog.data("scrollTop", 0);
        dialog.data("scrollLeft", 0);

        dialog.find("#btn_mark").click(function(e) {
            dialog.data("scrollTop", $(window).scrollTop());
            dialog.data("scrollLeft", $(window).scrollLeft());
        });

        dialog.find("#btn_scroll").click(function(e) {
            var top = dialog.data("scrollTop");
            var left = dialog.data("scrollLeft");

            window.scrollTo(left, top);
        });
    } else {
        dialog.toggle();
    }
}

if (typeof(jQuery) == 'undefined') {
    load_script(JQUERY_URL,
        function() {
            jQuery.noConflict();
            load_script(JQUERY_UI_URL, main);
        });
} else if (! (jQuery.ui && jQuery.ui.version)) {
    load_script(JQUERY_UI_URL, main);
} else {
    main();
}


})()
