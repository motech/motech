(function () {
    'use strict';

    var serverModule = angular.module('motech-dashboard');

    serverModule.controller('MasterCtrl', function ($scope, $http, i18nService, $cookieStore, $q, BrowserDetect) {
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

        $scope.showDashboardLogo = {
            showDashboard : true,
            changeClass : function () {
                return this.showDashboard ? "minimize action-minimize-up" : "minimize action-minimize-down";
            },
            changeTitle : function () {
                return this.showDashboard ? "server.minimizeLogo" : "server.expandLogo";
            },
            backgroudUpDown : function () {
                return this.showDashboard ? "body-down" : "body-up";
            }
        };

        $scope.setUserLang = function (lang, refresh) {
            var locale = toLocale(lang);

            $http({ method: "POST", url: "lang", params: locale })
                .success(function () {
                    $scope.loadI18n(lang);
                    $scope.userLang = $scope.getLanguage(locale);
                    moment.lang(lang);

                    if (refresh) {
                        window.location.reload();
                    }
                })
                .error(function (response) {
                    handleResponse('server.header.error', 'server.error.setLangError', response);
                });
        };

        $scope.msg = function (key, value) {
            return i18nService.getMessage(key, value);
        };

        $scope.getLanguage = function (locale) {
            return {
                key: locale.toString() || "en",
                value: $scope.languages[locale.toString()] || $scope.languages[locale.withoutVariant()] || $scope.languages[locale.language] || "English"
            };
        };

        $scope.minimizeHeader = function () {
            $scope.showDashboardLogo.showDashboard = !$scope.showDashboardLogo.showDashboard;
            $cookieStore.put("showDashboardLogo", $scope.showDashboardLogo.showDashboard);
        };

        $scope.loadI18n = function (lang) {
            var key, handler, i;

            if (!$scope.i18n || $scope.i18n.length <= 0) {
                handle();
            }

            for (key in $scope.i18n) {
                for (i = 0; i < $scope.i18n[key].length; i += 1) {
                    handler = undefined;

                    // last one
                    if (i === $scope.i18n[key].length - 1) {
                        handler = handle;
                    }

                    i18nService.init(lang, key, $scope.i18n[key][i], handler);
                }
            }
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

        $scope.active = function(url) {
            var address = '?moduleName={0}{1}'.format($scope.getCurrentModuleName(), url);

            if (window.location.href.indexOf(address) !== -1) {
                return "active";
            }
        };

        $scope.BrowserDetect.init();

        if ($cookieStore.get("showDashboardLogo") !== undefined) {
           $scope.showDashboardLogo.showDashboard=$cookieStore.get("showDashboardLogo");
        }

        $q.all([
            $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                $scope.i18n = data;
            }),
            $scope.doAJAXHttpRequest('GET', 'lang/list', function (data) {
                $scope.languages = data;
            }),
            $scope.doAJAXHttpRequest('GET', 'lang', function (data) {
                $scope.user = {
                    lang : data,
                    anonymous: true
                };
            })
        ]).then(function () {
            $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
            moment.lang($scope.user.lang);
            $scope.loadI18n($scope.user.lang);
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
                $scope.loadI18n($scope.user.lang);
            });
        });
    });

    serverModule.controller('HomeCtrl', function ($scope, $cookieStore, $q) {
        $scope.securityMode = false;
        $scope.modulesWithSubMenu = [];
        $scope.modulesWithoutSubMenu = [];

        if ($cookieStore.get("showDashboardLogo") !== undefined) {
            $scope.showDashboardLogo.showDashboard = $cookieStore.get("showDashboardLogo");
        }

        $q.all([
            $scope.doAJAXHttpRequest('POST', 'getModulesWithoutSubMenu', function (data) {
                $scope.modulesWithoutSubMenu = data;
            }),
            $scope.doAJAXHttpRequest('POST', 'getModulesWithSubMenu', function (data) {
                $scope.modulesWithSubMenu = data;
            }),
            $scope.doAJAXHttpRequest('POST', 'getUser', function (data) {
                var scope = angular.element("body").scope();

                scope.user = data;
                scope.user.anonymous = false;

                if (!$scope.$$phase) {
                    $scope.$apply(scope.user);
                }
            })
        ]).then(function () {
            $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
            moment.lang($scope.user.lang);
            $scope.loadI18n($scope.user.lang);
        });

        $scope.$on('module.list.refresh', function () {
            $scope.doAJAXHttpRequest('POST', 'getModulesWithoutSubMenu', function (data) {
                $scope.modulesWithoutSubMenu = data;
            });

            $scope.doAJAXHttpRequest('POST', 'getModulesWithSubMenu', function (data) {
                $scope.modulesWithSubMenu = data;
            });
        });

    });

}());
