function setSuggestedValue(id, val) {
    document.getElementById(id).value = val;
}

function verifyDbConnection(connectionType) {
    var loader = $('#loader');
    loader.show();

    var warnings = $('#verify-alert');
    var info = $('#verify-info');
    var infoSql = $('#verifySql-info');
    var errors = $('#verify-error');

    errors.html("");
    warnings.html("");

    warnings.hide();
    errors.hide();
    info.hide();
    infoSql.hide();

    $.ajax({
        type: 'POST',
        url: (connectionType == 1) ? 'verifyCouchDb' : 'verifySql',
        timeout: 8000,
        data: $('form.bootstrap-config-form').serialize(),
        success: function(data) {
            if (data.success !== undefined && data.success === true) {
                if (connectionType == 1) {
                    info.show();
                    $(window).scrollTop(info.offset().top);
                } else {
                    infoSql.show();
                    $(window).scrollTop(infoSql.offset().top);
                }
            } else {
                if(data.errors !== undefined) {
                    data.errors.forEach(function(item) {
                        errors.append(item + '<br/>');
                    });

                    errors.show();
                }

                if(data.warnings !== undefined) {
                    data.warnings.forEach(function(item) {
                        warnings.append(item + '<br/>');
                    });

                    warnings.show();
                    $(window).scrollTop(warnings.offset().top);
                }
            }
            loader.hide();
        },
        error: function() {
            if(data.warnings !== undefined) {
                data.warnings.forEach(function(item) {
                    warnings.append(item + '<br/>');
                });

                warnings.show();
                $(window).scrollTop(warnings.offset().top);
            }
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