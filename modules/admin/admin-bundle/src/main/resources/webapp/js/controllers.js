'use strict';

/* Controllers */

function BundleListCtrl($scope, Bundle, i18nService, $routeParams) {

    var LOADING_STATE = 'LOADING';

    $scope.orderProp = 'name';
    $scope.invert = false;
    $scope.versionOrder = new Array("version.major", "version.minor", "version.micro", "version.qualifier");

    $scope.FILTER_MOTECH_BUNDLES = 'org.motechproject.motech-';

    $scope.setOrder = function(prop) {
        if (prop == $scope.orderProp) {
            $scope.invert = !$scope.invert;
        } else {
            $scope.orderProp = prop;
            $scope.invert = false;
        }
    }

    $scope.getSortClass = function(prop) {
        var sortClass = "sorting-no";
        if (prop == $scope.orderProp) {
            if ($scope.invert) {
                sortClass = "sorting-desc";
            } else {
                sortClass = "sorting-asc";
            }
        }
        return sortClass;
    }

    $scope.bundles = Bundle.query();

    if ($routeParams.bundleId != undefined) {
        $scope.bundle = Bundle.get({ bundleId: $routeParams.bundleId });
    }

    $scope.allBundlesCount = function() {
        var count = 0;
        angular.forEach($scope.bundles, function(bundle) {
            if (bundle.symbolicName.search($scope.FILTER_MOTECH_BUNDLES) == 0) {
                count ++;
            }
        });

        return count;
    }

    $scope.activeBundlesCount = function() {
        var count = 0;
        angular.forEach($scope.bundles, function(bundle) {
            if (bundle.symbolicName.search($scope.FILTER_MOTECH_BUNDLES) == 0) {
                count += bundle.isActive() ? 1 : 0;
            }
        });

        return count;
    }

    $scope.installedBundlesCount = function() {
        var count = 0;
        angular.forEach($scope.bundles, function(bundle) {
            if (bundle.symbolicName.search($scope.FILTER_MOTECH_BUNDLES) == 0) {
                count += bundle.isInstalled() ? 1 : 0;
            }
        });

        return count;
    }

    $scope.resolvedBundlesCount = function() {
        var count = 0;
        angular.forEach($scope.bundles, function(bundle) {
            if (bundle.symbolicName.search($scope.FILTER_MOTECH_BUNDLES) == 0) {
                count += bundle.isResolved() ? 1 : 0;
            }
        });

        return count;
    }

    $scope.stopBundle = function(bundle) {
        bundle.$stop(dummyHandler, angularErrorHandler);
    }

    $scope.startBundle = function(bundle) {
        bundle.state = LOADING_STATE;
        bundle.$start(dummyHandler, function() {
            bundle.state = 'RESOLVED';
            motechAlert('bundles.error.start', 'error');
        });
    }

    $scope.restartBundle = function(bundle) {
        bundle.state = LOADING_STATE;
        bundle.$restart(dummyHandler, function() {
             bundle.state = 'RESOLVED';
             motechAlert('bundles.error.restart', 'error');
        });
    }

    $scope.uninstallBundle = function(bundle) {
        jConfirm(jQuery.i18n.prop('bundles.uninstall.confirm'), jQuery.i18n.prop("confirm"), function(val) {
            if (val) {
                var oldState = bundle.state;
                bundle.state = LOADING_STATE;

                bundle.$uninstall(function() {
                    // remove bundle from list
                    $scope.bundles.remove(bundle);
                },
                function() {
                    motechAlert('bundles.error.uninstall', 'error');
                    bundle.state = oldState;
                });
            }
        });
    }

    $scope.getIconClass = function(bundle) {
        var cssClass = '';
        if (!bundle.isActive()) {
            cssClass = 'dullImage';
        }
        return cssClass;
    }

    $scope.bundleStable = function(bundle) {
        return bundle.state != LOADING_STATE;
    }

    $scope.submitBundle = function() {
        blockUI();
        $('#bundleUploadForm').ajaxSubmit({
            success : function(data) {
                $scope.bundles = Bundle.query();
                unblockUI();
            },
            error : jFormErrorHandler
        });
    }

    Bundle.prototype.isActive = function() {
        return this.state == 'ACTIVE';
    }

    Bundle.prototype.printVersion = function() {
        var separator = '.'
        var ver = this.version.major + separator + this.version.minor + separator + this.version.micro;
        if (this.version.qualifier) {
            ver += (separator + this.version.qualifier);
        }
        return ver;
    }


    Bundle.prototype.isInstalled = function() {
        return this.state == 'INSTALLED';
    }

    Bundle.prototype.isResolved = function() {
        return this.state == 'RESOLVED';
    }
}

function StatusMsgCtrl($scope, $timeout, StatusMessage, i18nService, $cookieStore) {
    var UPDATE_INTERVAL = 1000 * 30;
    var IGNORED_MSGS = 'ignoredMsgs';

    $scope.ignoredMessages = $cookieStore.get(IGNORED_MSGS);
    $scope.messages = [];

    StatusMessage.query(function(data) {
        messageFilter(data);
    });

    $scope.getCssClass = function(msg) {
        var cssClass = 'msg';
        if (msg.level == 'ERROR') {
            cssClass += ' error';
        } else if (msg.level == 'OK') {
            cssClass += ' ok';
        }
        return cssClass;
    }

    $scope.printText = function(text) {
        var result = text;
        if (text.match(/^\{.*\}$/)) {
            result = i18nService.getMessage(text.replace(/[\{\}]/g, ""));
        }
        return result;
    }

    $scope.refresh = function() {
        StatusMessage.query(function(data) {
            messageFilter(data);
        });
    }

    StatusMessage.prototype.getDate = function() {
        return new Date(this.date);
    }

    var update = function() {
        StatusMessage.query(function (newMessages) {
            if (!messagesEqual(newMessages, $scope.messages)) {
                messageFilter(newMessages);
            }

            $timeout(update, UPDATE_INTERVAL);

            function messagesEqual(arg1, arg2) {
                if(arg1.length !== arg2.length) {
                    return false;
                }

                for(var i = arg1.length; i--;) {
                    if(arg1[i]._id != arg2[i]._id)
                        return false;
                }

                return true;
            }
        });
    }

    $scope.remove = function(message) {
        $scope.messages.remove(message);
        if ($scope.ignoredMessages == undefined) {
            $scope.ignoredMessages = [];
        }
        $scope.ignoredMessages.push(message._id);
        $cookieStore.put(IGNORED_MSGS, $scope.ignoredMessages);
    }

    $timeout(update, UPDATE_INTERVAL);

    var messageFilter = function(data) {
        var msgs = jQuery.grep(data, function(message, index) {
            return jQuery.inArray(message._id, $scope.ignoredMessages) == -1; // not in ignored list
        });
        $scope.messages = msgs;
    }
}

function MasterCtrl($scope, i18nService, $http) {
    i18nService.init(jQuery.i18n.browserLang());

    $scope.bundlesWithSettings = [];
    $http({method: 'GET', url: 'api/settings/bundles/list'}).
        success(function(data) {
            $scope.bundlesWithSettings = data;
        });


    $scope.mappings = [];
    $scope.getMappings = function() {
        $http({method: 'GET', url: 'api/mappings'}).
              success(function(data) {
                  $scope.mappings = data;
              });
    }
    $scope.getMappings();

    $scope.showSettings = function(bundle) {
        return $.inArray(bundle.symbolicName, $scope.bundlesWithSettings) >= 0;
    }

    $scope.msg = function(key) {
        return i18nService.getMessage(key);
    }

    $scope.printDate = function(milis) {
        var date = "";
        if (milis) {
            var date = new Date(milis);
        }
        return date;
    }

    $scope.languages = i18nService.languages;
    $scope.userLang = null;
    $http({method: 'GET', url: 'api/locale/lang/'}).
        success(function(data) {
            i18nService.init(data);
            $scope.userLang = i18nService.getLanguage(toLocale(data));
        });

    $scope.setUserLang = function(lang) {
        var locale = toLocale(lang);

        $http({ method: "POST", url: "api/locale/lang/", params: locale }).success(function() {
            i18nService.init(lang);
            $scope.userLang = i18nService.getLanguage(locale);
        });
    }
}

function SettingsCtrl($scope, PlatformSettings, i18nService, $http) {

    $scope.platformSettings = PlatformSettings.query();

    $scope.label = function(key) {
        return i18nService.getMessage('settings.' + key);
    }

    $scope.saveSettings = function(settings) {
        blockUI();
        settings.$save(alertHandlerWithCallback('settings.saved', function () {
            $scope.platformSettings = PlatformSettings.query();
        }), jFormErrorHandler);
    }

    $scope.saveNewSettings = function() {
        blockUI();
        $('#noSettingsForm').ajaxSubmit({
            success : alertHandlerWithCallback('settings.saved', function () {
                $scope.platformSettings = PlatformSettings.query();
            }),
            error : jFormErrorHandler
        });
    }

    $scope.uploadSettings = function() {
        $("#settingsFileForm").ajaxSubmit({
            success : alertHandlerWithCallback('settings.saved', function () {
                $scope.platformSettings = PlatformSettings.query();
            }),
            error : jFormErrorHandler
        });
    }

    $scope.uploadFileLocation = function() {
        $http({method: 'POST', url: 'api/settings/platform/location', params: {location: this.location}}).
            success(alertHandler('settings.saved', 'success')).
            error(alertHandler('settings.error.location'));
    }

    $scope.saveAll = function() {
        blockUI();
        $http.post('api/settings/platform/list', $scope.platformSettings).
            success(alertHandler('settings.saved', 'success')).
            error(alertHandler('settings.error.location'));
    }
}

function ModuleCtrl($scope, ModuleSettings, Bundle, i18nService, $routeParams) {
    $scope.module = Bundle.details({ bundleId: $routeParams.bundleId });
}

function BundleSettingsCtrl($scope, Bundle, ModuleSettings, $routeParams, $http) {
    $scope.moduleSettings = ModuleSettings.query({ bundleId : $routeParams.bundleId });

    $http.get('api/settings/' + $routeParams.bundleId + '/raw').success(function(data) {
        $scope.rawFiles = data;
    })

    $scope.module = Bundle.get({ bundleId: $routeParams.bundleId });

    $scope.saveSettings = function(mSettings, doRestart) {
        var successHandler;
        if (doRestart == true) {
            successHandler = restartBundleHandler;
        } else {
            successHandler = alertHandler('settings.saved', 'success')
        }

        blockUI();
        mSettings.$save({bundleId: $scope.module.bundleId}, successHandler, angularErrorHandler);
    }

    $scope.uploadRaw = function(filename, doRestart) {
        var successHandler;
        if (doRestart == true) {
            successHandler = restartBundleHandler;
        } else {
            successHandler = alertHandler('settings.saved', 'success')
        }

        blockUI();
        var id = '#_raw_' + filename.replace('.', '\\.');
        $(id).ajaxSubmit({
          success : successHandler,
          error : jFormErrorHandler
        });
    }

    var restartBundleHandler = function() {
        $scope.module.$restart(function() {
            unblockUI();
            motechAlert('settings.saved', 'success');
        }, alertHandler('bundles.error.restart', 'error'));
    }
}

function OperationsCtrl($scope, $http) {
    $http({method: 'GET', url: 'api/mappings/graphite'}).
        success(function(data) {
            $scope.graphiteUrl = data;
            // prefix with http://
            if ($scope.graphiteUrl && $scope.graphiteUrl.lastIndexOf("http://") !== 0) {
                $scope.graphiteUrl = "http://" + $scope.graphiteUrl;
            }
        });
}
