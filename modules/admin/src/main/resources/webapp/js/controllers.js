(function () {

    'use strict';

    /* Controllers */

    var controllers = angular.module('admin.controllers', []);

    controllers.controller('AdminBundleListCtrl', function($scope, Bundle, i18nService, $routeParams, $http, $timeout, ModalFactory, LoadingModal) {

        var LOADING_STATE = 'LOADING', MODULE_LIST_REFRESH_TIMEOUT = 6000; // milliseconds

        $scope.orderProp = 'name';
        $scope.invert = false;
        $scope.startUpload = true;
        $scope.versionOrder = ["version.major", "version.minor", "version.micro", "version.qualifier"];
        $scope.isFileSelected = false;

        $scope.refreshModuleList = function () {
            $scope.$emit('module.list.refresh');
        };

        $scope.bundlesWithSettings = [];
        $http({method:'GET', url:'../admin/api/settings/bundles/list'}).
            success(function (data) {
                $scope.bundlesWithSettings = data;
            });

        $scope.showSettings = function (bundle) {
            return $.inArray(bundle.symbolicName, $scope.bundlesWithSettings) >= 0 ||
                    (bundle.settingsURL && bundle.settingsURL.length !== 0);
        };

        $scope.showDocs = function (bundle) {
            return bundle.docURL !== undefined && bundle.docURL.length > 0 && bundle.docURL !== "http://grameenfoundation.org/";
        };

        $scope.setOrder = function (prop) {
            if (prop === $scope.orderProp) {
                $scope.invert = !$scope.invert;
            } else {
                $scope.orderProp = prop;
                $scope.invert = false;
            }
        };

        $scope.getSortClass = function (prop) {
            var sortClass = "sorting-no";
            if (prop === $scope.orderProp) {
                if ($scope.invert) {
                    sortClass = "sorting-desc";
                } else {
                    sortClass = "sorting-asc";
                }
            }
            return sortClass;
        };

        $scope.bundles = Bundle.query();

        if ($routeParams.bundleId !== undefined) {
            $scope.bundle = Bundle.get({ bundleId:$routeParams.bundleId });
        }

        $scope.allBundlesCount = function () {
            if ($scope.bundles) {
                return $scope.bundles.length;
            } else {
                return 0;
            }
        };

        $scope.activeBundlesCount = function () {
            var count = 0;
            angular.forEach($scope.bundles, function (bundle) {
                count += bundle.isActive() ? 1 : 0;
            });

            return count;
        };

        $scope.installedBundlesCount = function () {
            var count = 0;
            angular.forEach($scope.bundles, function (bundle) {
                count += bundle.isInstalled() ? 1 : 0;
            });

            return count;
        };

        $scope.resolvedBundlesCount = function () {
            var count = 0;
            angular.forEach($scope.bundles, function (bundle) {
                count += bundle.isResolved() ? 1 : 0;
            });

            return count;
        };

        $scope.moduleSources = {
            'Repository':'Repository',
            'File':'File'
        };

        $scope.moduleSource = $scope.moduleSources.Repository;

        $scope.mavenStr = function(artifactId) {
            return 'org.motechproject:'.concat(artifactId).concat(':').concat($scope.msg('server.version'));
        };

        $scope.modules = {};
        $scope.modules[$scope.mavenStr('alerts')] = 'Alerts';
        $scope.modules[$scope.mavenStr('appointments')] = 'Appointments';
        $scope.modules[$scope.mavenStr('atom-client')] = 'Atom Client';
        $scope.modules[$scope.mavenStr('csd')] = 'Care Services Directory';
        $scope.modules[$scope.mavenStr('cms-lite')] = 'CMS Lite';
        $scope.modules[$scope.mavenStr('commcare')] = 'Commcare';
        $scope.modules[$scope.mavenStr('dhis2')] = 'DHIS2';
        $scope.modules[$scope.mavenStr('event-logging')] = 'Event Logging';
        $scope.modules[$scope.mavenStr('http-agent')] = 'Http Agent';
        $scope.modules[$scope.mavenStr('ihe-interop')] = 'IHE Interop';
        $scope.modules[$scope.mavenStr('ivr')] = 'IVR';
        $scope.modules[$scope.mavenStr('message-campaign')] = 'Message Campaign';
        $scope.modules[$scope.mavenStr('metrics')] = 'Metrics';
        $scope.modules[$scope.mavenStr('mtraining')] = 'mTraining';
        $scope.modules[$scope.mavenStr('motech-tasks')] = 'Tasks';
        $scope.modules[$scope.mavenStr('odk')] = 'Open Data Kit';
        $scope.modules[$scope.mavenStr('openmrs-19')] = 'OpenMRS 1.9';
        $scope.modules[$scope.mavenStr('pill-reminder')] = 'Pill Reminder';
        $scope.modules[$scope.mavenStr('motech-scheduler')] = 'Scheduler';
        $scope.modules[$scope.mavenStr('schedule-tracking')] = 'Schedule Tracking';
        $scope.modules[$scope.mavenStr('sms')] = 'SMS';

        $scope.module = "";

        $scope.stopBundleModal = function (bundle) {
            $('#stopBundleModal').on('show.bs.modal', function() {
                $scope.bundle = bundle;
            }).on('hide.bs.modal', function () {
                $scope.bundle = undefined;
            }).modal('show');
        };

        $scope.stopBundle = function (bundle) {
            bundle.state = LOADING_STATE;
            bundle.$stop($scope.refreshModuleList, function (response) {
                bundle.state = 'RESOLVED';
                ModalFactory.handleWithStackTrace('admin.error', 'admin.bundles.error.stop', response);
            });
        };

        $scope.startBundle = function (bundle) {
            var previousState = bundle.state;

            bundle.state = LOADING_STATE;
            bundle.$start(function () {
                LoadingModal.open();

                $timeout(function () {
                    if (previousState === 'INSTALLED') {
                        $scope.$emit('lang.refresh');
                    }

                    $scope.refreshModuleList();

                    LoadingModal.close();
                }, MODULE_LIST_REFRESH_TIMEOUT);
            }, function (response) {
                bundle.state = 'RESOLVED';
                ModalFactory.handleWithStackTrace('admin.error', 'admin.bundles.error.start', response);
            });
        };

        $scope.restartBundle = function (bundle) {
            bundle.state = LOADING_STATE;
            bundle.$restart(dummyHandler, function () {
                bundle.state = 'RESOLVED';
                $scope.$emit('lang.refresh');
                $scope.refreshModuleList();

                ModalFactory.motechAlert('admin.bundles.error.restart', 'admin.error');
            });
        };

        $scope.uninstallBundleModal = function (bundle) {
            $('#removeBundleModal').on('show.bs.modal', function () {
                $scope.bundle = bundle;
            }).on('hide.bs.modal', function () {
                $scope.bundle = undefined;
            }).modal('show');
        };

        $scope.uninstallBundle = function (bundle, withConfig) {
            LoadingModal.open();
            var oldState = bundle.state;
            bundle.state = LOADING_STATE;
            if (withConfig) {
                bundle.$uninstallWithConfig(function () {
                    // remove bundle from list
                    $scope.bundles.removeObject(bundle);
                    $scope.refreshModuleList();
                    LoadingModal.close();
                }, function () {
                    ModalFactory.motechAlert('admin.bundles.error.uninstall', 'admin.error');
                    bundle.state = oldState;
                    LoadingModal.close();
                });
            } else {
                $scope.bundle.$uninstall(function () {
                    // remove bundle from list
                    $scope.bundles.removeObject(bundle);
                    $scope.refreshModuleList();
                    LoadingModal.close();
                }, function () {
                    ModalFactory.motechAlert('admin.bundles.error.uninstall', 'admin.error');
                    bundle.state = oldState;
                    LoadingModal.close();
                });
            }
        };

        $scope.getIconClass = function (bundle) {
            var cssClass = '';
            if (!bundle.isActive()) {
                cssClass = 'dullImage';
            }
            return cssClass;
        };

        $scope.bundleStable = function (bundle) {
            return bundle.state !== LOADING_STATE;
        };

        $scope.loadSettingPage = function (bundle) {
            var moduleName = 'admin',
                url = '/admin/bundleSettings/' + bundle.bundleId;

            if (bundle.settingsURL !== null && bundle.isActive()) {
                moduleName = bundle.angularModule;
                url = bundle.settingsURL;
            }

            $scope.loadModule(moduleName, url);
        };

        $scope.startOnUpload = function () {
            if ($scope.startUpload !== true) {
                $scope.startUpload = true;
                $('.start-on-upload').find('i').removeClass("fa-square-o").addClass('fa-check-square-o');
            } else {
                $scope.startUpload = false;
                $('.start-on-upload').find('i').removeClass("fa-check-square-o").addClass('fa-square-o');
            }
        };



        $scope.checkFile = function (file) {
            if (file) {
                $scope.isFileSelected = true;
            } else {
                $scope.isFileSelected = false;
            }
        };

        $scope.isNoModuleOrFileSelected = function () {
            if ($scope.moduleSource === 'Repository') {
                return !$scope.module;
            } else if ($scope.moduleSource === 'File') {
                return !$scope.isFileSelected;
            }
        };

        $scope.submitBundle = function () {
            LoadingModal.open();
            $('#bundleUploadForm').ajaxSubmit({
                success: function (data, textStatus, jqXHR) {
                    if (jqXHR.status === 0 && data) {
                        ModalFactory.handleWithStackTrace('admin.error', 'admin.bundles.error.start', data);
                        LoadingModal.close();
                    } else {
                        $scope.bundles = Bundle.query(function () {
                            if ($scope.startUpload) {
                                $timeout(function () {
                                    $scope.$emit('lang.refresh');
                                    $scope.refreshModuleList();
                                    LoadingModal.close();
                                }, MODULE_LIST_REFRESH_TIMEOUT);
                            } else {
                                LoadingModal.close();
                            }
                            $scope.module = "";
                            $('#bundleUploadForm .fileupload').trigger('reset');
                        });
                    }
                },
                error:function (response) {
                    ModalFactory.handleWithStackTrace('admin.error', 'admin.bundles.error.start', response);
                    LoadingModal.close();
                }
            });
        };

        Bundle.prototype.isActive = function () {
            return this.state === 'ACTIVE';
        };

        Bundle.prototype.printVersion = function () {
            if (typeof this.version === "undefined") {
               this.version = 0;
            }
            var separator = '.',
            ver = this.version.major + separator + this.version.minor + separator + this.version.micro;
            if (this.version.qualifier) {
                ver += (separator + this.version.qualifier);
            }
            return ver;
        };


        Bundle.prototype.isInstalled = function () {
            return this.state === 'INSTALLED';
        };

        Bundle.prototype.isResolved = function () {
            return this.state === 'RESOLVED';
        };
    });

    controllers.controller('AdminStatusMsgCtrl', function($scope, $rootScope, $timeout, StatusMessage, i18nService,
            $cookieStore, $filter, ModalFactory, LoadingModal) {
        var UPDATE_INTERVAL = 1000 * 30, searchQuery = '',
        IGNORED_MSGS = 'ignoredMsgs',
        checkLevel = function (messageLevel, filterLevel) {
            var result;
            jQuery.each(filterLevel, function (i, val) {
                if (val === messageLevel.toLowerCase()){
                    result = true;
                } else {
                    result = false;
                }
            return (!result);
            });
        return result;
        },
        checkDateTime = function (mDateTime, fDateTimeFrom, fDateTimeTo) {
            var result, messageDateTime = parseInt(mDateTime, 10), filterDateTimeFrom = parseInt(fDateTimeFrom, 10), filterDateTimeTo = parseInt(fDateTimeTo, 10);
            if (!filterDateTimeFrom) {
                if (!filterDateTimeTo) {
                    result = true;
                } else {
                    if (messageDateTime && filterDateTimeTo > messageDateTime) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
            } else {
                if (messageDateTime && messageDateTime > filterDateTimeFrom) {
                    if (!filterDateTimeTo) {
                        result = true;
                    } else {
                        if (messageDateTime < filterDateTimeTo) {
                            result = true;
                        } else {
                            result = false;
                        }
                    }
                } else {
                    result = false;
                }
            }
        return result;
        },
        searchMatch = function (message, searchQuery) {
            var result;
            if (!searchQuery) {
                if (checkDateTime(message.date, $rootScope.filterDateTimeFrom, $rootScope.filterDateTimeTo)) {
                    if ($rootScope.filterModule === '') {
                        if($rootScope.filterLevel && $rootScope.filterLevel.length === 0) {
                            result = true;
                        } else if (checkLevel(message.level, $rootScope.filterLevel)) {
                            result = true;
                        } else {
                            result = false;
                            }
                    } else if (message.moduleName === $rootScope.filterModule) {
                        if($rootScope.filterLevel && $rootScope.filterLevel.length === 0) {
                            result = true;
                        } else if (checkLevel(message.level, $rootScope.filterLevel)) {
                            result = true;
                        } else {
                            result = false;
                        }
                    }
                } else {
                    result = false;
                }
            } else if (searchQuery && message.text.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1) {
                if (checkDateTime(message.date, $rootScope.filterDateTimeFrom, $rootScope.filterDateTimeTo)) {
                    if ($rootScope.filterModule === '') {
                        if($rootScope.filterLevel && $rootScope.filterLevel.length === 0) {
                            result = true;
                        } else if (checkLevel(message.level, $rootScope.filterLevel)) {
                            result = true;
                        } else {
                            result = false;
                            }
                    } else if (message.moduleName === $rootScope.filterModule) {
                        if($rootScope.filterLevel && $rootScope.filterLevel.length === 0) {
                            result = true;
                        } else if (checkLevel(message.level, $rootScope.filterLevel)) {
                            result = true;
                        } else {
                            result = false;
                        }
                    }
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
            return result;
        },
        messageFilter = function (data) {
            var msgs = jQuery.grep(data, function (message, index) {
                return jQuery.inArray(message.id, $scope.ignoredMessages) === -1; // not in ignored list
            });
            $scope.messages = msgs;
            $rootScope.search();
        },
        update = function () {
            var i;
            StatusMessage.query(function (newMessages) {
                function messagesEqual(arg1, arg2) {
                    if (arg1.length !== arg2.length) {
                        return false;
                    }

                    for (i = arg1.length - 1; i >= 0; i-=1) {
                        if (arg1[i].id !== arg2[i].id) {
                            return false;
                        }
                    }

                    return true;
                }

                if (!messagesEqual(newMessages, $scope.messages)) {
                    messageFilter(newMessages);
                }
            });
        };
        $rootScope.filterLevel = [];
        $rootScope.filterModule = '';
        $rootScope.filterDateTimeFrom = '';
        $rootScope.filterDateTimeTo = '';
        $scope.resetItemsPagination();
        $scope.filteredItems = [];
        $scope.itemsPerPage = 10;


        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        }, {
            show: true,
            button: '#admin-filters'
        });

        $scope.ignoredMessages = $cookieStore.get(IGNORED_MSGS);
        $scope.messages = [];

        StatusMessage.query(function (data) {
            messageFilter(data);
        });

        $scope.getCssClass = function (msg) {
            var cssClass = 'msg';
            if (msg.level === 'ERROR') {
                cssClass += ' badge-important';
            } else if (msg.level === 'OK') {
                cssClass += ' badge-info';
            } else if (msg.level === 'CRITICAL') {
                cssClass += ' badge-important';
            } else if (msg.level === 'WARN') {
                cssClass += ' badge-warning';
            } else if (msg.level === 'DEBUG') {
                cssClass += ' badge-success';
            } else if (msg.level === 'INFO') {
                cssClass += ' badge-info';
            }
            return cssClass;
        };

        $scope.printText = function (text) {
            var result = text;
            if (text.match(/^\{[\w\W]*\}$/)) {
                result = i18nService.getMessage(text.replace(/[\{\}]/g, ""));
            }
            return result;
        };

        $scope.refresh = function () {
            StatusMessage.query(function (data) {
                messageFilter(data);
            });
        };

        StatusMessage.prototype.getDate = function () {
            return new Date(this.date);
        };

        $scope.remove = function (message) {
            $scope.messages.removeObject(message);
            if ($scope.ignoredMessages === undefined) {
                $scope.ignoredMessages = [];
            }
            $scope.ignoredMessages.push(message.id);
            $cookieStore.put(IGNORED_MSGS, $scope.ignoredMessages);
        };

        $rootScope.changeItemsPerPage = function (itemsPerPage) {
            $scope.itemsPerPage = itemsPerPage;
            $scope.setCurrentPage(0);
            $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
        };

        $rootScope.search = function () {
            LoadingModal.close();
            $scope.filteredItems = $filter('filter')($scope.messages, function (item) {
                return item && searchMatch(item, $rootScope.query);
            });
            $scope.setCurrentPage(0);
            $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
        };

        $timeout(update, UPDATE_INTERVAL);
    });

    controllers.controller('AdminSettingsCtrl', function($scope, PlatformSettings, i18nService, $http, ModalFactory, LoadingModal) {
        $scope.comboboxValues = { "security.password.validator" : ["none", "lower_upper", "lower_upper_digit", "lower_upper_digit_special"],
                                  "system.language" : ["en", "pl"] };

        $scope.platformSettings = PlatformSettings.get();

        $scope.label = function (key) {
            return i18nService.getMessage('admin.settings.' + key);
        };

        $scope.saveSettings = function (settings) {
            LoadingModal.open();
            $http.post('../admin/api/settings/platform', settings).
                success( ModalFactory.alertHandler('admin.settings.saved', 'admin.success') ).
                error( ModalFactory.alertHandler('admin.settings.error.location') );
        };

        $scope.saveNewSettings = function () {
            LoadingModal.open();
            $('#noSettingsForm').ajaxSubmit({
                success:ModalFactory.alertHandler('admin.settings.saved', $scope.msg('server.success'), function () {
                    $scope.platformSettings = PlatformSettings.get();
                }),
                error: function (response) {
                    ModalFactory.errorAlert(response);
                }
            });
        };

        $scope.uploadSettings = function () {
            $("#settingsFileForm").ajaxSubmit({
                success:ModalFactory.alertHandler('admin.settings.saved', $scope.msg('server.success'), function () {
                    $scope.platformSettings = PlatformSettings.get();
                }),
                error: function (response) {
                    ModalFactory.errorAlert(response);
                }
            });
        };

        $scope.uploadFileLocation = function () {
            $http({method:'POST', url:'../admin/api/settings/platform/location', params:{location:this.location}}).
                success( ModalFactory.alertHandler('admin.settings.saved', 'admin.success') ).
                error( ModalFactory.alertHandler('admin.settings.error.location') );
        };

        $scope.saveAll = function () {
            LoadingModal.open();
            $http.post('../admin/api/settings/platform/list', $scope.platformSettings.settingsList).
                success( ModalFactory.alertHandler('admin.settings.saved', 'admin.success') ).
                error( ModalFactory.alertHandler('admin.settings.error.location') );
        };

        $scope.exportConfig = function () {
            $http.get('../admin/api/settings/platform/export').
            success(function () {
                window.location.replace("../admin/api/settings/platform/export");
            }).
            error( ModalFactory.alertHandler('admin.settings.error.export', 'admin.error') );
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.addValue = function(key, value) {
            if ($scope.comboboxValues[key].indexOf(value) === -1) {
                $scope.comboboxValues[key].push(value);
            }
        };

        $scope.isTextOrNumber = function(option) {
            return (typeof option.value === "string" || typeof option.value === "number" || option.value === null) && !$scope.isCombobox(option);
        };

        $scope.isBool = function(option) {
            return typeof option.value === "boolean";
        };

        $scope.isCombobox = function(option) {
            return $scope.getComboboxValues(option.key) !== undefined;
        };

        $scope.getComboboxValues = function(key) {
            return $scope.comboboxValues[key];
        };
    });

    controllers.controller('AdminModuleCtrl', function($scope, ModuleSettings, Bundle, i18nService, $routeParams) {
        $scope.module = Bundle.details({ bundleId:$routeParams.bundleId });

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });
    });

    controllers.controller('AdminBundleSettingsCtrl', function($scope, Bundle, ModuleSettings, $routeParams, $http, ModalFactory, LoadingModal) {
        $scope.moduleSettings = ModuleSettings.query({ bundleId:$routeParams.bundleId });

        $http.get('../admin/api/settings/' + $routeParams.bundleId + '/raw').success(function (data) {
            $scope.rawFiles = data;
        });

        $scope.module = Bundle.get({ bundleId:$routeParams.bundleId });

        $scope.saveSettings = function (mSettings, doRestart) {
            var successHandler;
            if (doRestart === true) {
                successHandler = restartBundleHandler;
            } else {
                successHandler = ModalFactory.alertHandler('admin.settings.saved', 'admin.success');
            }

            LoadingModal.open();
            mSettings.$save({bundleId:$scope.module.bundleId}, successHandler, ModalFactory.angularHandler('admin.error', 'admin.settings.error'));
        };

        $scope.uploadRaw = function (filename, doRestart) {
            var successHandler,
            id = '#_raw_' + filename.replace('.', '\\.');

            if (doRestart === true) {
                successHandler = restartBundleHandler;
            } else {
                successHandler = ModalFactory.alertHandler('admin.settings.saved', 'admin.success');
            }

            LoadingModal.open();

            $(id).ajaxSubmit({
                success:successHandler,
                error: function (response) {
                    ModalFactory.errorAlert(response);
                }
            });
        };

        var restartBundleHandler = function () {
            $scope.module.$restart(function () {
                LoadingModal.close();
                ModalFactory.motechAlert('admin.settings.saved', 'admin.success');
            }, ModalFactory.alertHandler('admin.bundles.error.restart', 'admin.error'));
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

    });

    controllers.controller('AdminServerLogCtrl', function($scope, $http, LoadingModal) {
        $scope.refresh = function () {
            LoadingModal.open();
            $http({method:'GET', url:'../admin/api/log'}).
                success(
                function (data) {
                    if (data === 'server.tomcat.error.logFileNotFound') {
                        $('#logContent').html($scope.msg(data));
                    } else {
                        $('#logContent').html(data);
                        LoadingModal.close();
                    }
                }).
                error(LoadingModal.close());
        };

        //removing the sidebar from <body> before route change
        $scope.$on('$routeChangeStart', function(event, next, current) {
            $('div[id^="jquerySideBar"]').remove();
        });

        $scope.refresh();

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

    });

    controllers.controller('AdminServerLogOptionsCtrl', function($scope, LogService, $http, ModalFactory) {
        $scope.availableLevels = ['off', 'trace', 'debug', 'info', 'warn', 'error', 'fatal', 'all'];
        $scope.logs = [{name: "", level: "off"}];

        $scope.config = LogService.get();

        $scope.save = function () {
            jQuery.each($scope.logs, function (index) {
                if($scope.logs[index].name !== '' && $scope.logs[index].name !== undefined
                    && $scope.logs[index].level !== '' && $scope.logs[index].level !== undefined
                    && $scope.logs[index].name.toLowerCase() !== 'root') {
                    $scope.config.loggers.push({
                        logName:$scope.logs[index].name,
                        logLevel:$scope.logs[index].level
                    });
                }
            });
            $scope.logs = [];
            $scope.logs = [{name: "", level: "off"}];
            $scope.config.$save({}, ModalFactory.alertHandler('admin.log.changedLevel', $scope.msg('server.success'),function () {
            }), function () {
                ModalFactory.motechAlert('admin.log.changedLevelError', 'admin.error');
            });
        };

        $scope.add = function () {
            $scope.logs.push({
                name:"",
                level:"off"
            });
        };

        $scope.forAll = function (level) {
            var i;

            for (i = 0; i < $scope.config.loggers.length; i += 1) {
                $scope.config.loggers[i].logLevel = level;
            }

            $scope.config.root.logLevel = level;

            jQuery.each($scope.logs, function (index) {
                $scope.logs[index].level = level;
            });
        };

        $scope.change = function (logger, level) {
            $('#changeForAll .active').removeClass('active');
            logger.logLevel = level;
        };

        $scope.changeNew = function (log, level) {
            $('#changeForAll .active').removeClass('active');
            log.level = level;
        };

        $scope.changeRoot = function (level) {
            $('#changeForAll .active').removeClass('active');
            $scope.config.root.logLevel = level;
        };

        $scope.remove = function (logger) {
            $scope.config.loggers.removeObject(logger);

            if ($scope.config.trash === undefined || $scope.config.trash === null) {
                $scope.config.trash = [];
            }

            $scope.config.trash.push(logger);
        };

        $scope.removeNew = function (log) {
            $scope.logs.removeObject(log);
        };

        $scope.levelsCss = function (level) {
            var cssClass = ' btn-default';

            if (level !== undefined) {
                switch (level.toLowerCase()) {
                    case 'trace':
                        cssClass = 'btn-primary';
                        break;
                    case 'debug':
                        cssClass = 'btn-success';
                        break;
                    case 'info':
                        cssClass = 'btn-info';
                        break;
                    case 'warn':
                        cssClass = 'btn-warning';
                        break;
                    case 'error':
                        cssClass = 'btn-danger';
                        break;
                    case 'fatal':
                        cssClass = 'btn-inverse';
                        break;
                    default:
                        cssClass = ' btn-default';
                        break;
                }
            }

            return cssClass;
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

    });

    controllers.controller('AdminNotificationRuleCtrl', function($scope, NotificationRule, NotificationRuleDto, $location, Bundle, ModalFactory) {
        $scope.notificationRuleDto = new NotificationRuleDto();
        $scope.notificationRuleDto.notificationRules = NotificationRule.query();
        $scope.notificationRuleDto.idsToRemove = [];
        $scope.bundles = Bundle.query();

        $scope.changeRuleActionType = function (notificationRule, actionType) {
            notificationRule.actionType = actionType;
        };

        $scope.changeRuleLevel = function (notificationRule, level) {
            notificationRule.level = level;
        };

        $scope.changeRuleModuleName = function (notificationRule, moduleName) {
            notificationRule.moduleName = moduleName;
        };

        $scope.saveRules = function (notificationRule) {
            notificationRule.$save();
        };

        $scope.removeRule = function (notificationRule) {
            $scope.notificationRuleDto.notificationRules.removeObject(notificationRule);
            if (notificationRule.id) {
                $scope.notificationRuleDto.idsToRemove.push(notificationRule.id);
            }
        };

        $scope.newRule = function () {
            var notificationRule = new NotificationRule();
            notificationRule.actionType = 'EMAIL';
            notificationRule.level = 'CRITICAL';
            notificationRule.moduleName = '';

            $scope.notificationRuleDto.notificationRules.push(notificationRule);
        };

        $scope.save = function () {
            $scope.notificationRuleDto.$save(function () {
                ModalFactory.motechAlert('admin.messages.notifications.saved', 'admin.success');
                $location.path('/admin/messages');
            }, ModalFactory.angularHandler('admin.error', 'admin.messages.notifications.errorSave'));
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

    });

    controllers.controller('AdminTopicStatsCtrl', function($scope, $http) {

            $scope.dataAvailable = true;

            $http.get('../admin/api/topics/').success(function (data) {
                $scope.topics = data;
            }).error(function () {
                $scope.dataAvailable = false;
            });

        });

    controllers.controller('AdminQueueStatsCtrl', function($scope, $http) {

        $scope.dataAvailable = true;

        $http.get('../admin/api/queues/').success(function (data) {
            $scope.queues = data;
        }).error(function () {
            $scope.dataAvailable = false;
        });

    });

    controllers.controller('AdminQueueMessageStatsCtrl', function($scope, $http, $routeParams) {

        var queue = $routeParams.queueName;

        $scope.dataAvailable = true;

        $http.get('../admin/api/queues/browse?queueName=' + queue).success(function (data) {
            $scope.messages = data;
        }).error(function () {
                $scope.dataAvailable = false;
            });

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

    });

    controllers.controller('AdminFilterCtrl', function($scope, $rootScope, $timeout, StatusMessage, i18nService, $cookieStore) {

        var UPDATE_INTERVAL = 1000 * 30,
        IGNORED_MSGS = 'ignoredMsgs',
        messageFilter = function (data) {
            var msgs = jQuery.grep(data, function (message, index) {
                return jQuery.inArray(message.id, $scope.ignoredMessages) === -1; // not in ignored list
            });
            $scope.getModuleName = function(messages) {
                var moduleNames = ["all"];
                jQuery.each(messages, function(i, messages){
                    moduleNames.push(messages.moduleName);
                });
            return  $.grep(moduleNames, function(el, index) {
                return index === $.inArray(el, moduleNames);
            });
            };
            $scope.messages = msgs;
            $scope.modules = $scope.getModuleName($scope.messages);
        },
        update = function () {
            var i;
            StatusMessage.query(function (newMessages) {
                function messagesEqual (arg1, arg2) {
                    if (arg1.length !== arg2.length) {
                        return false;
                    }

                    for (i = arg1.length - 1; i >= 0; i-=1) {
                        if (arg1[i].id !== arg2[i].id) {
                            return false;
                        }
                    }

                    return true;
                }

                if (!messagesEqual(newMessages, $scope.messages)) {
                    messageFilter(newMessages);
                }
            });

        };

        $scope.ignoredMessages = $cookieStore.get(IGNORED_MSGS);
        $scope.messages = [];
        $scope.messagesLevels = ['critical', 'error', 'debug', 'info', 'warn'];
        $scope.filterModule = '';

        StatusMessage.query(function (data) {
            messageFilter(data);
        });

        $scope.search = function() {
            $rootScope.query = $scope.query;
            $rootScope.search();
        };

        $scope.setFilterLevel = function(filterLevel) {
            var result, levelExist = function (filterLevel) {
                jQuery.each($rootScope.filterLevel, function (i, val) {
                if (val === filterLevel) {
                    result = true;
                } else {
                    result = false;
                }
                return (!result);
                });
            return result;
            };
            if ($rootScope.filterLevel && $rootScope.filterLevel.length === 0) {
                $rootScope.filterLevel.push(filterLevel);
            } else {
                if (levelExist(filterLevel)) {
                    $rootScope.filterLevel.splice($rootScope.filterLevel.indexOf(filterLevel), 1);
                } else {
                    $rootScope.filterLevel.push(filterLevel);
                }
            }
            $scope.search();
        };

        $scope.setFilterModule = function(filterModule) {
            if (filterModule.toLowerCase() === 'all') {
                $scope.filterModule = filterModule;
                $rootScope.filterModule = '';
            } else {
                $scope.filterModule = filterModule;
                $rootScope.filterModule = $scope.filterModule;
            }
            $scope.search();
        };

        $scope.setDateTimeFilter = function(messageDateTimeFrom, messageDateTimeTo) {
            if (messageDateTimeFrom !== null && messageDateTimeTo === null) {
                $rootScope.filterDateTimeFrom = moment(messageDateTimeFrom).toDate().getTime();
                $rootScope.$apply();
                messageDateTimeTo = '';
            }
            if (messageDateTimeTo !== null && messageDateTimeFrom === null) {
                $rootScope.filterDateTimeTo = moment(messageDateTimeTo).toDate().getTime();
                $rootScope.$apply();
                messageDateTimeFrom = '';
            }
            $scope.search();
            $scope.$apply();
        };

        $timeout(update, UPDATE_INTERVAL);

    });

    controllers.controller('AdminPaginationMessageCtrl', function($scope, $rootScope) {

        $scope.limitPages = [10, 20, 50];
        $scope.itemsPerPage = $scope.limitPages[0];

        $scope.changeItemsPerPage = function() {
            $rootScope.changeItemsPerPage($scope.itemsPerPage);
        };

    });

}());

