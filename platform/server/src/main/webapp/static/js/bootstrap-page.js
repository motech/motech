function setSuggestedValue(id, val) {
    document.getElementById(id).value = val;
}

function verifyDbConnection() {
    var loader = $('#loader');
    loader.show();

    var warnings = $('#verify-alert');
    var infoSql = $('#verifySql-info');
    var errors = $('#verify-error');

    errors.html("");
    warnings.html("");

    warnings.hide();
    errors.hide();
    infoSql.hide();

    $.ajax({
        type: 'POST',
        url: 'verifySql',
        timeout: 8000,
        data: $('form.bootstrap-config-form').serialize(),
        success: function(data) {
            if (data.success !== undefined && data.success === true) {
                infoSql.show();
                $(window).scrollTop(infoSql.offset().top);
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

const TIMEOUT = 7000;
var redirectCount = 0;

function redirect() {
    $(location).attr('href', "../module/server/");
}

function endOfTime() {
    $.ajax({
        type: 'GET',
        url: 'isErrorOccurred',
        success: function(data) {
            if (data === true) {
                $(location).attr('href', "../bootstrap/error/startup");
            } else {
                $(location).attr('href', "../bootstrap/");
            }
        }
    });
}

function attemptRedirect() {
    if (redirectCount < 150) {
        $.ajax({
            url: "../module/server/",
            async: false,
            success: function() {
                redirect();
            },
            error: function() {
                redirectCount++;
                // check error
                $.ajax({
                    type: 'GET',
                    url: 'isErrorOccurred',
                    success: function(data) {
                        if (data === true) {
                            // go to error
                            $(location).attr('href', "../bootstrap/error/startup");
                        } else {
                            // attempt another redirection
                            setTimeout(function(){ attemptRedirect() }, TIMEOUT);
                        }
                    },
                    error: function() {
                        // should not happen, but attempt to redirect again
                        setTimeout(function(){ attemptRedirect() }, TIMEOUT);
                    }
                });
            }
        });
    } else {
        endOfTime();
    }
}