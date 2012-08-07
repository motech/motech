'use strict';

/* Controllers */

function BundleListCtrl($scope, Bundle, i18nService, $routeParams) {

    var LOADING_STATE = 'LOADING';

    $scope.bundles = Bundle.query();

    if ($routeParams.bundleId != undefined) {
        $scope.bundle = Bundle.get({ bundleId: $routeParams.bundleId });
    }

    $scope.activeBundlesCount = function() {
        var count = 0;
        angular.forEach($scope.bundles, function(bundle) {
            count += bundle.isActive() ? 1 : 0;
        });

        return count;
    }

    $scope.installedBundlesCount = function() {
        var count = 0;
        angular.forEach($scope.bundles, function(bundle) {
            count += bundle.isInstalled() ? 1 : 0;
        });

        return count;
    }

    $scope.resolvedBundlesCount = function() {
        var count = 0;
        angular.forEach($scope.bundles, function(bundle) {
            count += bundle.isResolved() ? 1 : 0;
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

    $scope.bundlesWithSettings = [];
    $http({method: 'GET', url: 'api/settings/bundles/list'}).
        success(function(data) {
            $scope.bundlesWithSettings = data;
        });


    $scope.mappings = [];
    $http({method: 'GET', url: 'api/mappings'}).
          success(function(data) {
              $scope.mappings = data;
          });

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

}

function SettingsCtrl($scope, PlatformSettings, i18nService, $http) {
    var LOADING_STATE = 1, ERROR_STATE = 2;

    $scope.platformSettings = PlatformSettings.query();

    $scope.label = function(key) {
        return i18nService.getMessage('settings.' + key);
    }

    $scope.getImg = function(settings) {
        switch(settings.state) {
        case LOADING_STATE:
            return 'img/load.gif';
        case ERROR_STATE:
            return 'img/delete.png';
        default:
            return 'img/start.png';
        }
    }

    $scope.save = function(settings) {
        captureTyping(function() {
            settings.state = LOADING_STATE;

            settings.$save(function() {
                // success
                if (this.state != undefined) {
                    delete settings.state;
                }
            },
            function() {
               // failure
               settings.state = ERROR_STATE;
            });
        });
    }

    $scope.saveSettings = function() {
        blockUI();
        $('#platformSettingsForm').ajaxSubmit({
            success : alertHandlerWithCallback('settings.saved', function () {
                $scope.platformSettings = PlatformSettings.query();
            }),
            error : jFormErrorHandler
        });
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
            success(alertHandler('settings.saved')).
            error(alertHandler('settings.error.location'));
    }
}

function ModuleCtrl($scope, ModuleSettings, Bundle, i18nService, $routeParams) {
    $scope.module = Bundle.details({ bundleId: $routeParams.bundleId });
}

function BundleSettingsCtrl($scope, Bundle, ModuleSettings, $routeParams) {
    $scope.moduleSettings = ModuleSettings.query({ bundleId : $routeParams.bundleId }, function(data) {
        $scope.settings = [];
        var i,j;
        for (i = 0;  i < data.length; i++) {
            for (j = 0;  j < data[i].settings.length; j++) {
                $scope.settings.push(data[i].settings[j]);
            }
        }
    });

    $scope.module = Bundle.get({ bundleId: $routeParams.bundleId });

    $scope.saveSettings = function(mSettings) {
        blockUI();
        mSettings.$save({bundleId: $scope.module.bundleId}, alertHandler('settings.saved'), angularErrorHandler);
    }
}