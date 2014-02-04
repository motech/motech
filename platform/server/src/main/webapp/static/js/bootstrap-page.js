function setSuggestedValue(id, val) {
    document.getElementById(id).value = val;
}

function verifyDbConnection() {
    var loader = $('#loader');
    loader.show();

    var warnings = $('#verify-alert');
    var info = $('#verify-info');
    var errors = $('#verify-error');

    errors.html("");

    warnings.hide();
    errors.hide();
    info.hide();

    $.ajax({
        type: 'POST',
        url: 'verify',
        timeout: 8000,
        data: $('form.bootstrap-config-form').serialize(),
        success: function(data) {
            if (data.success !== undefined && data.success === true) {
                info.show();
                $(window).scrollTop(info.offset().top);
            } else {
                if(data.errors !== undefined) {
                    data.errors.forEach(function(item) {
                        errors.append(item + '<br/>');
                    });

                    errors.show();
                }
                warnings.show();
                $(window).scrollTop(warnings.offset().top);
            }
            loader.hide();
        },
        error: function() {
            warnings.show();
            $(window).scrollTop(warnings.offset().top);
            loader.hide();
        }
    });
}

const TIMEOUT = 5000;
var redirectCount = 0;

function redirect() {
    $(location).attr('href', "../module/server/");
}

function attemptRedirect() {
    if (redirectCount < 5) {
        $.ajax({
            url: "../module/server/",
            success: function() {
                redirect();
            },
            error: function() {
                redirectCount++;
                setInterval(function(){attemptRedirect()}, TIMEOUT);
            }
        });
    } else {
        redirect();
    }
}