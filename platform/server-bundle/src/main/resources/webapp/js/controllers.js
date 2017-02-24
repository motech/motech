(function () {
    'use strict';

    var serverModule = angular.module('motech-dashboard');

    serverModule.filter('pathToId', function() {

        function pathToIdFilter(input) {
            return input.replace(/[\/]/g,'-');
        }

        return pathToIdFilter;
    }).filter('dottedToId', function() {

        function dottedToIdFilter(input) {
            return input.replace(/\./g,'-');
        }

        return dottedToIdFilter;
    });

    serverModule.controller('MotechMasterCtrl', function ($scope, $rootScope, $ocLazyLoad, $state, $stateParams, $http,
          i18nService, $cookieStore, $q, BrowserDetect, Menu, $location, $timeout, ModalFactory, LoadingModal, $window) {

        var handle = function () {
                if (!$scope.$$phase) {
                    $scope.$digest();
                }

                $scope.ready = true;
            },
            checkForRefresh = function () {
                if (window.location.hash !== "" && $scope.user.anonymous !== true) {
                    var start_pos = window.location.hash.indexOf('/') + 1,
                        end_pos = window.location.hash.indexOf('/', start_pos);
                    if (end_pos < 0) {
                        end_pos = window.location.hash.length;
                    }
                    $scope.loadModule(window.location.hash.substring(start_pos, end_pos), "/" + window.location.hash.substring(start_pos, window.location.hash.length));
                } else {
                    window.location.hash = '';
                }
                $scope.setBootstrapDialogMessages();
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

        $scope.moduleToLoad = undefined;
        $scope.activeLink = undefined;
        $scope.documentationUrl = undefined;
        $scope.activeMenu = "servermodules";
        $scope.selectedTabState = {};
        $scope.selectedTabState.selectedTab = "";

        $scope.loginViewData = {};
        $scope.resetViewData = {};
        $scope.startupViewData = {};
        $scope.forgotViewData = {};

        $scope.showDashboardLogo = {
            showDashboard : true,
            changeClass : function () {
                return this.showDashboard ? "fa fa-caret-up" : "fa fa-caret-down";
            },
            changeTitle : function () {
                return this.showDashboard ? "server.minimizeLogo" : "server.expandLogo";
            },
            backgroundUpDown : function () {
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
                var changedModulesSection = $scope.changeName(modulesSection);
                return $scope.activeMenu === changedModulesSection ? "" : "hidden";
            },
            changeClass : function (modulesSection) {
                var changedModulesSection = $scope.changeName(modulesSection.name);
                if (changedModulesSection === $scope.activeMenu) {
                    if (modulesSection.name === "server.modules" && $scope.activeLink !== undefined && $scope.activeLink.moduleName !== "admin"
                        && $scope.activeLink.moduleName !== "webSecurity" && $scope.activeLink.moduleName !== "rest-docs") {
                        $scope.documentationUrl = $scope.lastModulesActiveMenu;
                    } else {
                        $scope.documentationUrl = modulesSection.moduleDocsUrl;
                    }
                    return "active";
                }
                return "";
            }
        };

        $scope.setDocsUrl = function (url, modulesSection) {
            $scope.documentationUrl = url;
            if (modulesSection) {
                $scope.lastModulesActiveMenu = url;
            }
        };

        $scope.getLangForDisplay = function() {
            return $scope.languages[$scope.userLang.key];
        };

        $scope.getLangCode = function() {
            return $scope.userLang.key;
        };

        $scope.setUserLang = function (lang, refresh) {
            var locale = toLocale(lang), setLangUrl = "userlang";

            // update the current selection
            $scope.userLang = $scope.getLanguage(locale);

            if ($scope.isStartupView()) {
                setLangUrl = "lang/session"; // For startup settings we set the session language only, since there's no user yet
            }

            $http.post(setLangUrl, locale)
                .success(function () {
                    $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                        $scope.i18n = data;
                        $scope.loadI18n($scope.i18n);

                        if ($scope.isStartupView()) {
                            $scope.startupViewData.startupSettings.language = lang;
                        }

                        moment.locale(lang);
                        if (refresh) {
                            $state.reload();
                        }
                        ModalFactory.showAlert('server.success.changed.language', 'server.changed.language');
                    });
                })
                .error(function (response) {
                    ModalFactory.showErrorAlertWithResponse('server.error.setLangError', 'server.error', response);
                });
        };

        $scope.isStartupView = function() {
            return ($scope.startupViewData && $scope.startupViewData.startupSettings);
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

        /*
        * This function provide local messages for Bootstrap3-Dialog plugin.
        * https://nakupanda.github.io/bootstrap3-dialog/
        */
        $scope.setBootstrapDialogMessages = function () {
            BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_DEFAULT] = $scope.msg('server.bootstrapDialog.TYPE_DEFAULT');
            BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_INFO] = $scope.msg('server.bootstrapDialog.TYPE_INFO');
            BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_PRIMARY] = $scope.msg('server.bootstrapDialog.TYPE_PRIMARY');
            BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_SUCCESS] = $scope.msg('server.bootstrapDialog.TYPE_SUCCESS');
            BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_WARNING] = $scope.msg('server.bootstrapDialog.TYPE_WARNING');
            BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_DANGER] = $scope.msg('server.bootstrapDialog.TYPE_DANGER');
            BootstrapDialog.DEFAULT_TEXTS.OK = $scope.msg('server.bootstrapDialog.ok');
            BootstrapDialog.DEFAULT_TEXTS.CANCEL = $scope.msg('server.bootstrapDialog.cancel');
            BootstrapDialog.DEFAULT_TEXTS.CONFIRM = $scope.msg('server.bootstrapDialog.confirm');
        };

        $scope.minimizeHeader = function () {
            $scope.showDashboardLogo.showDashboard = !$scope.showDashboardLogo.showDashboard;
            $scope.outerLayout.sizePane('north', $scope.showDashboardLogo.changeHeight());
            $cookieStore.put("showDashboardLogo", $scope.showDashboardLogo.showDashboard);
        };

        $scope.storeSelected = function () {
            $cookieStore.put("activeMenu", $scope.activeMenu);
        };

        $scope.selectModules = function (sectionName) {
            var sectionNameChanged = $scope.changeName(sectionName);
            $scope.activeMenu = sectionNameChanged;
            $scope.storeSelected();
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

        $scope.loadModule = function (moduleName, url) {
            var convertUrl = function (urlParam) {
                if(urlParam.indexOf('/') === 0) {urlParam = urlParam.replace('/', '');}
                if(urlParam.indexOf('/') > 0) {urlParam = urlParam.replace('/', '.');}
                if(urlParam.indexOf('/') > 0) {urlParam = urlParam.replace(/(\/)\w+((\/)\w*)*/i, '');}
                return urlParam;
            };

            $scope.reloadToLoginPageIfSessionExpired();
            if (url.indexOf('admin/bundleSettings/') > 0) {
                $scope.selectedTabState.selectedTab = 'bundleSettings';
            } else {
                $scope.selectedTabState.selectedTab = url.replace(/(\/)\d+((\/)\w*)*/i, '').toString().substring(url.replace(/(\/)\d+((\/)\w*)*/i, '').toString().lastIndexOf("/")+1);
            }
            $scope.activeLink = {moduleName: moduleName, url: url};

            if (moduleName) {
                LoadingModal.open();

                $http.get('../server/module/critical/' + moduleName).success(function (response) {
                    if (response.data !== undefined && response.data !== '' && response.status !== 408) {
                        ModalFactory.showErrorAlert(null, null, response.status + ": " + response.statusText);
                        LoadingModal.close();
                    }
                });

                if ($scope.moduleToLoad === moduleName || url === '/login') {
                    $location.path(url);
                    if (url.indexOf('admin/bundleSettings/') > 0) {
                        $state.go('admin.bundleSettings', {'bundleId': url.substring(url.lastIndexOf("/")+1)});
                    } else {
                        $state.go(convertUrl(url), $state.params);
                    }
                    LoadingModal.close();
                    innerLayout({}, { show: false });
                } else {
                    $scope.moduleToLoad = moduleName;
                    if (!$ocLazyLoad.isLoaded(moduleName)) {
                        $ocLazyLoad.load(moduleName);
                    }

                    if (url) {
                        if ($ocLazyLoad.isLoaded(moduleName)) {
                            $location.path(url);
                            LoadingModal.close();
                        }
                        $scope.$on('ocLazyLoad.moduleLoaded', function(e, params) {
                            if ($ocLazyLoad.isLoaded(moduleName)) {
                                $location.path(url);
                                LoadingModal.close();
                            }
                        });
                    } else {
                        LoadingModal.close();
                    }
                }
            }
        };

        $scope.$watch(function() {
            return $window.location.href;
        }, function() {
            return $scope.reloadToLoginPageIfSessionExpired();
        });

        $scope.reloadToLoginPageIfSessionExpired = function() {
            var fetchedUser = {};
            if ($scope.activeLink) {
                $scope.checkSession(function(response) {
                    fetchedUser = response;

                    if (typeof fetchedUser.userName === 'undefined') {
                        $window.location.reload();
                    }
                });
            }
        };

        $scope.checkSession = function(callback) {
            $http.get('../server/getUser').success(function (response) {
                callback(response);
            });
        };

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
            innerLayout({}, { show: false });
            resizeLayout();
        });

        $scope.loadI18n = function (data) {
            i18nService.init(data);
            handle();
            checkForRefresh();
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

        /**
        Deprecated. This function exists for backward compatibility
        and should not be used.
        */
        $scope.active = function(url) {
            if (window.location.href.indexOf(url) !== -1) {
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

        if ($cookieStore.get("activeMenu") !== undefined) {
            $scope.activeMenu = $cookieStore.get("activeMenu");
        }

        $q.all([
            $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                $scope.i18n = data;
            }),
            $scope.doAJAXHttpRequest('GET', 'lang/list', function (data) {
                // "TODO: Temporarily hiding the Chinese option, until it's fixed with MOTECH-2484"
                if(data['zh_TW.Big5'] !== undefined) {
                    delete data['zh_TW.Big5'];
                }
                $scope.languages = data;
            }),
            $scope.doAJAXHttpRequest('GET', 'lang', function (data) {
                $scope.user.lang = data;
            })
        ]).then(function () {
            $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
            moment.locale($scope.user.lang);
            $scope.loadI18n($scope.i18n);
        });

        $scope.$on('lang.refresh', function () {
            $scope.ready = false;

            $q.all([
                $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                    $scope.i18n = data;
                }), $scope.doAJAXHttpRequest('GET', 'lang/list', function (data) {
                    // "TODO: Temporarily hiding the Chinese option, until it's fixed with MOTECH-2484"
                    if(data['zh_TW.Big5'] !== undefined) {
                        delete data['zh_TW.Big5'];
                    }
                    $scope.languages = data;
                })
            ]).then(function () {
                $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
                moment.locale($scope.user.lang);
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
                var errorParameter, blockedParameter;
                errorParameter = $scope.getUrlVar("error");
                blockedParameter = $scope.getUrlVar("blocked");
                $scope.loginViewData = data;
                $scope.loginContextPath = $scope.loginViewData.contextPath + 'j_spring_security_check';
                $scope.loginContextPathOpenId = $scope.loginViewData.contextPath + 'j_spring_openid_security_check';

                if (errorParameter !== '') {
                    $scope.loginViewData.error = errorParameter;
                } else if (blockedParameter !== '') {
                    $scope.loginViewData.blocked = blockedParameter;
                }
            });
        };

        //Used when user has forgotten the password
        $scope.getResetViewData = function() {
            var parametr = window.location.search;

            $scope.doAJAXHttpRequest('GET', '../server/forgotresetviewdata' + parametr, function (data) {
                $scope.resetViewData = data;
            });
        };

        //Used when user has forgotten the password
        $scope.submitResetPasswordForm = function() {
            LoadingModal.open();

            $http({
                method: 'POST',
                url: '../server/forgotreset',
                data: $scope.resetViewData.resetForm
            }).success(function(data) {
                LoadingModal.close();

                if (data.errors === undefined || data.errors.length === 0) {
                    data.errors = null;
                }

                $scope.resetViewData = data;
            })
            .error(function(data) {
                LoadingModal.close();
                ModalFactory.showErrorAlert('server.reset.error', 'server.error');
                $scope.resetViewData.errors = ['server.reset.error'];
            });
        };

        //Used when user must change the password
        $scope.initChangePasswordViewData = function() {
            $scope.changePasswordViewData = {
                changePasswordForm: {
                        username: '',
                        oldPassword: '',
                        password: '',
                        passwordConfirmation: ''
                    },
                errors: [],
                changeSucceded: false,
                userBlocked: false
            };
        };

        //Used when user must change the password
        $scope.submitChangePasswordForm = function() {
            LoadingModal.open();

            $http({
                method: 'POST',
                url: '../server/changepassword',
                data: $scope.changePasswordViewData.changePasswordForm
            }).success(function(data) {
                LoadingModal.close();

                if (data.userBlocked) {
                    window.location = "./login?blocked=true";
                    return;
                }

                if (data.errors === undefined || data.errors.length === 0) {
                    data.errors = null;
                }

                $scope.changePasswordViewData.errors = data.errors;
                $scope.changePasswordViewData.changeSucceded = data.changeSucceded;
            }).error(function(data) {
                LoadingModal.close();
                ModalFactory.showErrorAlert('server.reset.error', 'server.error');
                $scope.resetViewData.errors = ['server.reset.error'];
            });
        };

        $scope.getStartupViewData = function() {
            $scope.doAJAXHttpRequest('GET', '../server/startupviewdata', function (data) {
                // "TODO: Temporarily hiding the Chinese option, until it's fixed with MOTECH-2484"
                if(data.languages['zh_TW.Big5'] !== undefined) {
                    delete data.languages['zh_TW.Big5'];
                }
                if(data.pageLang === 'zh_TW' || data.startupSettings.language === 'zh') {
                    data.pageLang = 'en';
                    data.startupSettings.language = 'en';
                }
                $scope.startupViewData = data;
            });
        };

        $scope.submitStartupConfig = function() {
             LoadingModal.open();
             $scope.startupViewData.startupSettings.loginMode = $scope.securityMode;
             $http({
                method: "POST",
                url: "../server/startup/",
                data: $scope.startupViewData.startupSettings
             })
             .success(function(data) {
                if (data.length === 0) {
                    window.location.assign("../server/");
                } else {
                    LoadingModal.close();
                }
                $scope.errors = data;
             })
             .error(function(data) {
                LoadingModal.close();
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

    serverModule.controller('MotechHomeCtrl', function ($scope, $state, $ocLazyLoad, $cookieStore, $q, Menu, $rootScope, $http, ModalFactory, LoadingModal) {
        $scope.securityMode = false;
        $scope.moduleMenu = {};
        $state.go('homepage');
        $scope.openInNewTab = function (url) {
            var win = window.open(url, '_blank');
            win.focus();
        };

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

        $(document).on('keydown',function(e){
            if(e.keyCode === 8) {
                var $target = e.target || e.srcElement, preventKeyPress = false;
                switch($target.tagName) {
                case 'BODY':
                    preventKeyPress = true;
                    break;
                case 'INPUT':
                    preventKeyPress = $target.readOnly || $target.disabled || ($target.attributes.type && $.inArray($target.attributes.type.value.toLowerCase(), ["radio", "checkbox", "submit", "button"]) >= 0);
                    break;
                case 'TEXTAREA':
                    preventKeyPress = $target.readOnly || $target.disabled;
                    break;
                case 'DIV':
                    preventKeyPress = $target.readOnly || $target.disabled;
                    break;
                default:
                    preventKeyPress = false;
                    break;
                }

                if (preventKeyPress) {
                    e.preventDefault();
                }
            }
        });

        $scope.isActiveLink = function(link) {
            return $scope.activeLink && $scope.activeLink.moduleName === link.moduleName && $scope.activeLink.url === link.url;
        };

        if ($cookieStore.get("showDashboardLogo") !== undefined) {
            $scope.showDashboardLogo.showDashboard = $cookieStore.get("showDashboardLogo");
        }

        $q.all([
            $scope.moduleMenu = Menu.get(function(data) {
                $scope.moduleMenu = data;
            }, function(response) {
                    ModalFactory.showErrorAlertWithResponse('server.error.cantLoadMenu', 'server.error', response);
                }
            ),

            $scope.doAJAXHttpRequest('GET', 'getUser', function (data) {
                var scope = angular.element("body").scope();

                scope.user.userName = data.userName;
                scope.user.securityLaunch = data.securityLaunch;
                scope.user.anonymous = false;

                if (!$scope.$$phase) {
                    $scope.$apply(scope.user);
                }

                // set in the rootScope for other modules
                $rootScope.username = data.userName;
            })
        ]).then(function () {
            if ($scope.user.lang) {
                $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
                moment.locale($scope.user.lang);
            }
        });

        $scope.$on('module.list.refresh', function () {
            Menu.get(function(data) {
                $scope.moduleMenu = data;
                $state.reload();
            }, function(response) {
                    LoadingModal.close();
                    ModalFactory.showErrorAlertWithResponse('server.error.cantLoadMenu', 'server.error', response);
                }
            );
        });

        jgridDefaultSettings();
    });

    serverModule.controller('MotechHomepageCtrl', function ($scope, $http, LoadingModal) {

        $scope.links = [];

        $scope.getUser = function() {
            var link = {};
            LoadingModal.open();
            $http.get('../server/module/menu')
            .success(function(data) {
                angular.forEach(data.sections, function(views) {
                    angular.forEach(views.links, function(viewsLink) {
                        link.name = viewsLink.name;
                        link.url = viewsLink.url;
                        link.modules = viewsLink.moduleName;
                        $scope.links.push(link);
                        link = {
                            name: "",
                            url: "",
                            modules: ""
                        };
                    });
                });
                LoadingModal.close();
            });
        };

        $scope.getIndexLink = function (links, searchName, propertyName) {
            var i, link, linksLength = links.length;

            for(i = 0; i < linksLength; i+=1) {
                link = links[i];
                if (link.hasOwnProperty(propertyName) && link[propertyName] === searchName) {
                    return i;
                }
            }
            return -1;
        };

        $scope.isModule = function (moduleName) {
            if ($scope.getIndexLink($scope.links, moduleName, "modules") === -1) {
                return false;
            }
            return true;
        };

        $scope.isLink = function (linkName) {
            if ($scope.getIndexLink($scope.links, linkName, "name") === -1) {
                return false;
            }
            return true;
        };

        $scope.goToPage = function (linkName, tabPath) {
            var linkIndex = $scope.getIndexLink($scope.links, linkName, "name");
            if (tabPath && linkIndex >= 0) {
                $scope.loadModule($scope.links[linkIndex].modules, tabPath);
            } else {
                $scope.loadModule($scope.links[linkIndex].modules, $scope.links[linkIndex].url);
            }
        };

        $scope.getUser();
        innerLayout({});

    });
}());
