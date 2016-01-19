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
};

var TIMEOUT = 5000;

var SERVER_BUNDLE = 'org.motechproject.motech-platform-server-bundle';
var MDS = 'org.motechproject.motech-platform-dataservices';
var WEB_SECURITY = 'org.motechproject.motech-platform-web-security';
var OSGI_WEB_UTIL = 'org.motechproject.motech-platform-osgi-web-util';
var CONFIG_CORE = 'org.motechproject.motech-platform-config-core';
var COMMONS_SQL = 'org.motechproject.motech-platform-commons-sql';
var EVENT = 'org.motechproject.motech-platform-event';
var EMAIL = 'org.motechproject.motech-platform-email';
var SERVER_CONFIG = 'org.motechproject.motech-platform-server-config';

var TRACKED_BUNDLES = [ OSGI_WEB_UTIL, CONFIG_CORE, COMMONS_SQL, EVENT, EMAIL, SERVER_BUNDLE, MDS, WEB_SECURITY, SERVER_CONFIG];

var timer;

function startLoading() {
    retrieveStatus();
    timer = setInterval(function(){retrieveStatus()}, TIMEOUT);
}

var statusRetrievalCount = 0;

function redirect() {
    $(location).attr('href', "../../");
}

function parseSymbolicName(symbolicName) {
    return symbolicName.replace(/\./g, '\\.');
}

function createError(symbolicName, bundleError, contextError) {
    var errorId = "#error-" + parseSymbolicName(symbolicName);

    if ($(errorId).length == 0) {
        $("#bundleErrors").append('<div class="text-danger" id="error-' + symbolicName + '"><p><b>' + symbolicName + '</b></p><pre hidden="true" class="bundleError"></pre><pre hidden="true" class="contextError"></pre></div>');
    }

    if (bundleError) {
        $(errorId).children('.bundleError').text(bundleError);
        $(errorId).children('.bundleError').show();
    }

    if (contextError) {
        $(errorId).children('.contextError').text(contextError);
        $(errorId).children('.contextError').show();
    }

    $("#bundleErrors").show();
}

function setStatus(symbolicName, status) {
    // we have dots in ids
    var divId = '#loading-' + parseSymbolicName(symbolicName),
        element = $(divId);

    if (element.length) {
        if (status === 'LOADING') {
            element.find('.fa-check-circle').hide();
            element.find('.fa-times-circle').hide();

            // add notch and start spinning
            element.find('.fa-circle-o').removeClass('.fa-circle-o').addClass('fa-circle-o-notch fa-spin');
        } else if (status === 'OK') {
            element.find('.fa-circle-o').hide();
            element.find('.fa-circle-o-notch').hide();
            element.find('.fa-times-circle').hide();

            element.find('.fa-check-circle').show();
        } else {
            // failed
            element.find('.fa-circle-o').hide();
            element.find('.fa-circle-o-notch').hide();
            element.find('.fa-check-circle').hide();

            element.find('.fa-times-circle').show();
        }
    }
}

function setStartupPercentage(percentage) {
    $('#startupProgressPercentage').text(percentage + '%').css({width: percentage + '%'}).attr('aria-valuenow', percentage);
    $('.startup').css({width: 600});
}

function containsServerBundle(bundles) {
    return $.inArray(SERVER_BUNDLE, bundles) != -1
}

function markFailure(startedBundles) {
    $('.progress').hide();
    $('#loadingTitle').hide();
    $('#loadingFailureTitle').show();

    // mark remaining as failed
    $(TRACKED_BUNDLES).each(function(index, symbolicName) {
        if ($.inArray(symbolicName, startedBundles) == -1) {
            setStatus(symbolicName, 'FAILED');
        }
    });
}

function stopLoading() {
    clearInterval(timer);
}

function retrieveStatus() {
    if (statusRetrievalCount++ < 150) {
        $.ajax({
            url: "status",
            success: function(platformStatus) {
                var startedBundles = platformStatus.startedBundles,
                    osgiStartedBundles = platformStatus.osgiStartedBundles,
                    bundleErrorsByBundle = platformStatus.bundleErrorsByBundle,
                    contextErrorsByBundle = platformStatus.contextErrorsByBundle;

                setStartupPercentage(platformStatus.startupProgressPercentage);

                // OSGi started bundles, without their context loaded
                $(osgiStartedBundles).each(function(index, symbolicName) {
                    if ($.inArray(symbolicName, startedBundles) == -1) {
                        setStatus(symbolicName, 'LOADING');
                    }
                });

                // fully started bundles (modules)
                $(startedBundles).each(function(index, symbolicName) {
                    setStatus(symbolicName, 'OK');
                });

                // names of bundles with either bundle or context errors
                var bundlesWithError = Object.keys(contextErrorsByBundle).concat(Object.keys(bundleErrorsByBundle));

                // mark errors
                $.each(bundlesWithError, function(index, symbolicName) {
                    createError(symbolicName, bundleErrorsByBundle[symbolicName], contextErrorsByBundle[symbolicName]);
                    setStatus(symbolicName, 'FAILED');
                });

                // if server bundle, web-security or MDS itself failed, then we are done. Stop polling for errors.
                if (platformStatus.inFatalError) {
                    markFailure(startedBundles);
                    stopLoading();
                }

                // check for success at the end
                if (containsServerBundle(startedBundles)) {
                    // we are done when server-bundle is ready
                    setTimeout(function(){
                        redirect();
                    }, 2000);
                }
           },
           error: function(data) {
                // error while retrieving status from server
                if($.type(data) === "string") {
                    $("#retrievalError").text(data);
                }
                $("#retrievalError").show();
           }
       });
    } else {
        // timed out 150 * 5s
        stopLoading();
    }
}