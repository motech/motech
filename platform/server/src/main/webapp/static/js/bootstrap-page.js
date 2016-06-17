function setSuggestedValue(id, val) {
    document.getElementById(id).value = val;
}

function setSuggestedValueByName(name, value) {
	$("input[name='"+name+"']").val(value);
}

function saveBootstrapData(){
    var bootstrapString = JSON.stringify($('form[name="bcform"]').serializeArray());
    sessionStorage.setItem("bootstrapString",bootstrapString);
};

(function retrieveBootstrapData(){
		$(document).ready(function(){
	    		if(sessionStorage.getItem("bootstrapString") != null){
				var bootstrapObj = JSON.parse(sessionStorage.getItem("bootstrapString"));
				for (var key in bootstrapObj){
				       setSuggestedValueByName(bootstrapObj[key]["name"], bootstrapObj[key]["value"]);
				}
				sessionStorage.removeItem("bootstrapString");
			 }
		});

})();


function verifyConnection(url) {
    var loader = $('#loader');
    loader.show();

    var sqlWarnings = $('#verifySql-alert');
    var amqWarnings = $('#verifyAmq-alert');
    var infoSql = $('#verifySql-info');
    var infoAmq = $('#verifyAmq-info');
    var sqlErrors = $('#verifySql-error');
    var amqErrors = $('#verifyAmq-error');
    var bootstrapErrors = $('#bootstrapErrors');

    sqlErrors.html("");
    amqErrors.html("");
    sqlWarnings.html("");
    amqWarnings.html("");
    bootstrapErrors.html("");

    sqlWarnings.hide();
    amqWarnings.hide();
    sqlErrors.hide();
    amqErrors.hide();
    infoSql.hide();
    infoAmq.hide();
    bootstrapErrors.hide();

    $.ajax({
        type: 'POST',
        url: url,
        timeout: 8000,
        data: $('form.bootstrap-config-form').serialize(),
        success: function(data) {
            if (data.success !== undefined && data.success === true) {
                if(url == VERIFY_SQL_URL) {

                                                     infoSql.show();
                                                     $(window).scrollTop(infoSql.offset().top);
                                                }
                                                if (url == VERIFY_AMQ_URL) {
                                                    infoAmq.show();
                                                    $(window).scrollTop(infoAmq.offset().top);
                                                }
            } else {
                if(data.errors !== undefined && data.sqlConfigError === true) {
                    data.errors.forEach(function(item) {
                        sqlErrors.append(item + '<br/>');
                    });

                    sqlErrors.show();
                }
                if(data.errors !== undefined && data.amqConfigError === true) {
                    data.errors.forEach(function(item) {
                        amqErrors.append(item + '<br/>');
                    });

                    amqErrors.show();
                }

                if(data.warnings !== undefined && data.sqlConfigError === true) {
                    data.warnings.forEach(function(item) {
                        sqlWarnings.append(item + '<br/>');
                    });

                    sqlWarnings.show();
                    $(window).scrollTop(sqlWarnings.offset().top);
                }
                if(data.warnings !== undefined && data.amqConfigError === true) {
                    data.warnings.forEach(function(item) {
                        amqWarnings.append(item + '<br/>');
                    });

                    amqWarnings.show();
                    $(window).scrollTop(amqWarnings.offset().top);
                }
            }
            loader.hide();
        },
        error: function() {
            if(data.warnings !== undefined && data.sqlConfigError === true) {
                data.warnings.forEach(function(item) {
                    sqlWarnings.append(item + '<br/>');
                });

                sqlWarnings.show();
                $(window).scrollTop(sqlWarnings.offset().top);
            }
            if(data.warnings !== undefined && data.amqConfigError === true) {
                                data.warnings.forEach(function(item) {
                                    amqWarnings.append(item + '<br/>');
                                });

                                amqWarnings.show();
                                $(window).scrollTop(amqWarnings.offset().top);
            }
            loader.hide();
        }

    });
};

var TIMEOUT = 5000;
const VERIFY_AMQ_URL = 'verifyAmq';
const VERIFY_SQL_URL = 'verifySql';
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

$.ajaxSetup ({
    // Disable caching of AJAX responses. fix for IE9
    cache: false
});

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
            element.find('.circle-loader').hide();

            // change for spinning gif
            element.find('.circle-loader-spinner').show();
        } else if (status === 'OK') {
            element.find('.circle-loader').hide();
            element.find('.circle-loader-spinner').hide();
            element.find('.fa-times-circle').hide();

            element.find('.fa-check-circle').show();
        } else {
            // failed
            element.find('.circle-loader').hide();
            element.find('.circle-loader-spinner').hide();
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
