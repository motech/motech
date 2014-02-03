(function () {
    'use strict';

    var serverModule = angular.module('motech-dashboard');

    serverModule.controller('MasterCtrl', function ($scope, $http, i18nService, $cookieStore, $q, BrowserDetect, Menu) {
        var handle = function () {
                if (!$scope.$$phase) {
                    $scope.$digest();
                }

                $scope.ready = true;
            };

        $scope.BrowserDetect = BrowserDetect;

        $scope.ready = false;
        $scope.i18n = {};
        $scope.languages = [];
        $scope.contextPath = '';
        $scope.userLang = {};
        $scope.pagedItems = [];
        $scope.currentPage = 0;
        $scope.config = {};
        $scope.bodyState = true;
        $scope.outerLayout = {};
        $scope.innerLayout = {};
        $scope.user = {
            anonymous: true
        };

        $scope.loginViewData = {};
        $scope.resetViewData = {};
        $scope.startupViewData = {};
        $scope.forgotViewData = {};

        $scope.showDashboardLogo = {
            showDashboard : true,
            changeClass : function () {
                return this.showDashboard ? "minimize icon-white icon-caret-up" : "minimize icon-white icon-caret-down";
            },
            changeTitle : function () {
                return this.showDashboard ? "server.minimizeLogo" : "server.expandLogo";
            },
            backgroudUpDown : function () {
                return this.showDashboard ? "body-down" : "body-up";
            },
            changeHeight : function () {
                return this.showDashboard ? "100" : "40";
            }
        };

        $scope.changeName = function (name) {
            return name.replace('.', "");
        };

        $scope.showActiveMenu = {
            hideSection : function (modulesSection) {
                var nameModulesSection = $scope.changeName(modulesSection);
                if (nameModulesSection === 'servermodules') {
                    return $scope.showServerModules.modulesActiveMenu ? "" : "hidden";
                }
                if (nameModulesSection === 'adminmodule') {
                    return $scope.showAdminModules.adminActiveMenu ? "" : "hidden";
                }
                if (nameModulesSection === 'websecurity') {
                    return $scope.showSecurityModules.securityActiveMenu ? "" : "hidden";
                }
            },
            changeModulesClass : function () {
                return $scope.showServerModules.modulesActiveMenu ? "active" : "";
            },
            changeAdminClass : function () {
                return $scope.showAdminModules.adminActiveMenu ? "active" : "";
            },
            changeSecurityClass : function () {
                return $scope.showSecurityModules.securityActiveMenu ? "active" : "";
            }
        };

        $scope.showServerModules = {
            modulesActiveMenu : true
        };

        $scope.showAdminModules = {
            adminActiveMenu : false
        };

        $scope.showSecurityModules = {
            securityActiveMenu : false
        };

        $scope.setUserLang = function (lang, refresh) {
            var locale = toLocale(lang);
            $http({ method: "POST", url: "lang", params: locale })
                .success(function () {
                    $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                        $scope.i18n = data;
                        $scope.loadI18n($scope.i18n);
                    });

                    $scope.startupViewData.startupSettings.language = lang;
                    moment.lang(lang);
                    motechAlert('server.success.changed.language', 'server.changed.language',function(){
                        if (refresh ) {
                            window.location.reload();
                        }
                    });
                })
                .error(function (response) {
                    handleResponse('server.header.error', 'server.error.setLangError', response);
                });
        };

        $scope.msg = function () {
            return i18nService.getMessage(arguments);
        };

        $scope.getLanguage = function (locale) {
            return {
                key: locale.toString() || "en",
                value: $scope.languages[locale.toString()] || $scope.languages[locale.withoutVariant()] || $scope.languages[locale.language] || "English"
            };
        };

        $scope.minimizeHeader = function () {
            $scope.showDashboardLogo.showDashboard = !$scope.showDashboardLogo.showDashboard;
            $scope.outerLayout.sizePane('north', $scope.showDashboardLogo.changeHeight());
            $cookieStore.put("showDashboardLogo", $scope.showDashboardLogo.showDashboard);
        };

        $scope.storeSelected = function () {
            $cookieStore.put("showServerModules", $scope.showServerModules.modulesActiveMenu);
            $cookieStore.put("showAdminModules", $scope.showAdminModules.adminActiveMenu);
            $cookieStore.put("showSecurityModules", $scope.showSecurityModules.securityActiveMenu);
        };

        $scope.selectModules = function (sectionName) {
            var sectionNameModules = $scope.changeName(sectionName);
            if (sectionNameModules === 'servermodules') {
                $scope.showServerModules.modulesActiveMenu = true;
                $scope.showAdminModules.adminActiveMenu = false;
                $scope.showSecurityModules.securityActiveMenu = false;
                $scope.showActiveMenu.hideSection(sectionNameModules);
                $scope.storeSelected();
            }
            if (sectionNameModules === 'adminmodule') {
                $scope.showAdminModules.adminActiveMenu = true;
                $scope.showServerModules.modulesActiveMenu = false;
                $scope.showSecurityModules.securityActiveMenu = false;
                $scope.showActiveMenu.hideSection(sectionNameModules);
                $scope.storeSelected();
            }
            if (sectionNameModules === 'websecurity') {
                $scope.showSecurityModules.securityActiveMenu = true;
                $scope.showAdminModules.adminActiveMenu = false;
                $scope.showServerModules.modulesActiveMenu = false;
                $scope.showActiveMenu.hideSection(sectionNameModules);
                $scope.storeSelected();
            }
        };

        $scope.loadI18n = function (data) {
            i18nService.init(data);
            handle();
        };

        $scope.doAJAXHttpRequest = function (method, url, callback) {
            var defer = $q.defer();

            $http({ method: method, url: url }).
                success(function (data) {
                    callback(data);
                    defer.resolve(data);
                });

            return defer.promise;
        };

        $scope.resetItemsPagination = function () {
            $scope.pagedItems = [];
            $scope.currentPage = 0;
        };

        $scope.groupToPages = function (filteredItems, itemsPerPage) {
            var i;
            $scope.pagedItems = [];

            for (i = 0; i < filteredItems.length; i += 1) {
                if (i % itemsPerPage === 0) {
                    $scope.pagedItems[Math.floor(i / itemsPerPage)] = [ filteredItems[i] ];
                } else {
                    $scope.pagedItems[Math.floor(i / itemsPerPage)].push(filteredItems[i]);
                }
            }
        };

        $scope.range = function (start, end) {
            var ret = [], i;
            if (!end) {
                end = start;
                start = 0;
            }
            for (i = start; i < end; i += 1) {
                ret.push(i);
            }
            return ret;
        };

        $scope.setCurrentPage = function (currentPage) {
            $scope.currentPage = currentPage;
        };

        $scope.firstPage = function () {
            $scope.currentPage = 0;
        };

        $scope.lastPage = function (lastPage) {
            $scope.currentPage = lastPage;
        };

        $scope.prevPage = function () {
            if ($scope.currentPage > 0) {
                $scope.currentPage -= 1;
            }
        };

        $scope.nextPage = function () {
            if ($scope.currentPage < $scope.pagedItems.length - 1) {
                $scope.currentPage += 1;
            }
        };

        $scope.setPage = function () {
            $scope.currentPage = this.number;
        };

        $scope.hidePages = function (number) {
            return ($scope.currentPage + 4 < number && number > 8) || ($scope.currentPage - 4 > number && number + 9 < $scope.pagedItems.length);
        };

        $scope.printDate = function (milis) {
            var date = "";
            if (milis) {
                date = new Date(milis);
            }
            return date;
        };

        $scope.getCurrentModuleName = function () {
            var queryKey = parseUri(window.location.href).queryKey;
            return (queryKey && queryKey.moduleName) || '';
        };

        $scope.getCurrentAnchor = function () {
            return parseUri(window.location.href).anchor;
        };

        $scope.active = function(url) {
            var address = '?moduleName={0}{1}'.format($scope.getCurrentModuleName(), url);

            if (window.location.href.indexOf(address) !== -1) {
                return "active";
            }
        };

        $scope.safeApply = function (fun) {
            var phase = this.$root.$$phase;

            if (phase === '$apply' || phase === '$digest') {
                if(fun && (typeof(fun) === 'function')) {
                    fun();
                }
            } else {
                this.$apply(fun);
            }
        };

        $scope.BrowserDetect.init();

        if ($cookieStore.get("showDashboardLogo") !== undefined) {
           $scope.showDashboardLogo.showDashboard=$cookieStore.get("showDashboardLogo");
        }

        if ($cookieStore.get("showServerModules") !== undefined) {
           $scope.showServerModules.modulesActiveMenu=$cookieStore.get("showServerModules");
           $scope.showAdminModules.adminActiveMenu=$cookieStore.get("showAdminModules");
           $scope.showSecurityModules.securityActiveMenu=$cookieStore.get("showSecurityModules");
        }

        $q.all([
            $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                $scope.i18n = data;
            }),
            $scope.doAJAXHttpRequest('GET', 'lang/list', function (data) {
                $scope.languages = data;
            }),
            $scope.doAJAXHttpRequest('GET', 'lang', function (data) {
                $scope.user.lang = data;
            })
        ]).then(function () {
            $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
            moment.lang($scope.user.lang);
            $scope.loadI18n($scope.i18n);
        });

        $scope.$on('lang.refresh', function () {
            $scope.ready = false;

            $q.all([
                $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                    $scope.i18n = data;
                }), $scope.doAJAXHttpRequest('GET', 'lang/list', function (data) {
                    $scope.languages = data;
                })
            ]).then(function () {
                $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
                moment.lang($scope.user.lang);
                $scope.loadI18n($scope.i18n);
            });
        });

        $scope.setSuggestedValue = function(ngtarget, ngkey, value) {
            ngtarget[ngkey] = value;
        };

        $scope.getUrlVar = function (key) {
            var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search);
            return result || "";
        };

        $scope.getLoginViewData = function() {
            $scope.doAJAXHttpRequest('GET', 'loginviewdata', function (data) {
                var parameter = $scope.getUrlVar("error");
                $scope.loginViewData = data;

                if (parameter !== '') {
                    $scope.loginViewData.error = parameter;
                }
            });
        };

        $scope.getResetViewData = function() {
            var parametr = window.location.search;

            $scope.doAJAXHttpRequest('GET', '../server/resetviewdata' + parametr, function (data) {
                $scope.resetViewData = data;
            });
        };

        $scope.submitResetPasswordForm = function() {
            blockUI();

            $http({
                method: 'POST',
                url: '../server/reset',
                data: $scope.resetViewData.resetForm
            }).success(function(data) {
                unblockUI();

                if (data.errors === undefined || data.errors.length === 0) {
                    data.errors = null;
                }

                $scope.resetViewData = data;
            })
            .error(function(data) {
                unblockUI();
                motechAlert('server.reset.error');
                $scope.resetViewData.errors = ['server.reset.error'];
            });
        };

        $scope.getStartupViewData = function() {
            $scope.doAJAXHttpRequest('GET', '../server/startupviewdata', function (data) {
                $scope.startupViewData = data;
            });
        };

        $scope.submitStartupConfig = function() {
             $scope.startupViewData.startupSettings.loginMode = $scope.securityMode;
             $http({
                method: "POST",
                url: "../server/startup/",
                data: $scope.startupViewData.startupSettings
             })
             .success(function(data) {
                if (data.length === 0) {
                    window.location.assign("../server/");
                }
                $scope.errors = data;
             });
        };

        $scope.getForgotViewData = function() {
            $scope.doAJAXHttpRequest('GET', 'forgotviewdata', function (data) {
                $scope.forgotViewData = data;
            });
        };

        $scope.sendReset = function () {
             if ($scope.forgotViewData.email !== "") {
                 $http({ method: "POST", url: "../server/forgot", data: $scope.forgotViewData.email})
                     .success(function (response) {
                        $scope.error=response;
                        $scope.forgotViewData.emailGetter=false;
                        $scope.forgotViewData.processed=true;
                     })
                     .error(function (response) {
                        $scope.error = 'security.tokenSendError';
                     });
             }
        };
    });

    serverModule.controller('HomeCtrl', function ($scope, $cookieStore, $q, Menu) {
        $scope.securityMode = false;

        $scope.moduleMenu = {};

        $scope.hasMenu = function(menuName){
            var hasMenuWithGivenName, menuSections ;
            hasMenuWithGivenName = false;
            menuSections  = $scope.moduleMenu.sections;
            angular.forEach(menuSections,function(section){
                if(section.name === menuName){
                    hasMenuWithGivenName = true;
                }
            });
            return hasMenuWithGivenName;
        };


        $scope.isActiveLink = function(link) {
            return link.moduleName === $scope.getCurrentModuleName() &&
                (!link.url || link.url === '#' + $scope.getCurrentAnchor());
        };

        if ($cookieStore.get("showDashboardLogo") !== undefined) {
            $scope.showDashboardLogo.showDashboard = $cookieStore.get("showDashboardLogo");
        }

        $q.all([
            $scope.moduleMenu = Menu.get(function(data) {
                $scope.moduleMenu = data;
            }, angularHandler('error', 'server.error.cantLoadMenu')),

            $scope.doAJAXHttpRequest('POST', 'getUser', function (data) {
                var scope = angular.element("body").scope();

                scope.user.userName = data.userName;
                scope.user.securityLaunch = data.securityLaunch;
                scope.user.anonymous = false;

                if (!$scope.$$phase) {
                    $scope.$apply(scope.user);
                }
            })
        ]).then(function () {
            if ($scope.user.lang) {
                $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
                moment.lang($scope.user.lang);
            }
        });

        $scope.$on('module.list.refresh', function () {
            Menu.get(function(data) {
                $scope.moduleMenu = data;
            }, angularHandler('error', 'server.error.cantLoadMenu'));
        });

    });
}());
