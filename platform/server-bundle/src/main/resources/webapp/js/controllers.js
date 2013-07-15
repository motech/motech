(function () {
    'use strict';

    var serverModule = angular.module('motech-dashboard');

    serverModule.controller('MasterCtrl', function ($scope, $http, i18nService, $cookieStore, BrowserDetect) {
        $scope.ready = false;

        $scope.i18n = {};
        $scope.languages = [];
        $scope.securityMode=false;
        $scope.userLang = null;
        $scope.BrowserDetect = BrowserDetect;
        $scope.pagedItems = [];
        $scope.currentPage = 0;
        $scope.showDashboardLogo = {
            showDashboard : true,
            changeClass : function() {
                if (this.showDashboard) {
                    return "minimize action-minimize-up";
                } else {
                    return "minimize action-minimize-down";
                }
            },
            changeTitle : function() {
                if (this.showDashboard) {
                    return "minimizeLogo";
                } else {
                    return "expandLogo";
                }
            },
            backgroudUpDown : function() {
                if (this.showDashboard) {
                    return "body-down";
                } else {
                    return "body-up";
                }
            }
        };

        if ($cookieStore.get("showDashboardLogo") !== undefined) {
           $scope.showDashboardLogo.showDashboard=$cookieStore.get("showDashboardLogo");
        }

        $scope.BrowserDetect.init();

        $http({method: 'GET', url: 'lang/locate'}).
            success(function(data) {
                $scope.i18n = data;
            });

        $http({method: 'GET', url: 'lang/list'}).
            success(function(data) {
                $scope.languages = data;

                $http({method: 'GET', url: 'lang'}).
                    success(function(data) {
                        $scope.loadI18n(data);
                        $scope.userLang = $scope.getLanguage(toLocale(data));
                    });
            });

        $scope.setUserLang = function(lang) {
            var locale = toLocale(lang);

            $http({ method: "POST", url: "lang", params: locale }).success(function() {
                window.location.reload();
            });
        };

        $scope.msg = function(key, value) {
            return i18nService.getMessage(key, value);
        };

        $scope.setModuleUrl = function(url) {
            $scope.moduleUrl = url;
        };

        $scope.printDate = function(milis) {
            var date = "";
            if (milis) {
                date = new Date(milis);
            }
            return date;
        };

        $scope.getLanguage = function(locale) {
           return {
               key: locale.toString() || "en",
               value: $scope.languages[locale.toString()] || $scope.languages[locale.withoutVariant()] || $scope.languages[locale.language] || "English"
           };
        };

        $scope.active = function(address) {
            if (window.location.href.indexOf(address) !== -1) {
                return "active";
            }
        };

        $scope.minimizeHeader = function() {
            $scope.showDashboardLogo.showDashboard=!$scope.showDashboardLogo.showDashboard;
            $cookieStore.put("showDashboardLogo", $scope.showDashboardLogo.showDashboard);
        };

        function handle() {
            $scope.ready = true;

            if (!$scope.$$phase) {
                $scope.$digest();
            }
        }

        $scope.loadI18n = function(lang) {
            if (!$scope.i18n || $scope.i18n.length <= 0) {
                $scope.ready = true;
            }

            var key, i, handler;

            for (key in $scope.i18n) {
                for (i = 0; i < $scope.i18n[key].length; i+=1) {
                    handler = void 0;
                    // last one
                    if (i === $scope.i18n[key].length-1) {
                        handler = handle();
                    }
                    i18nService.init(lang, key, $scope.i18n[key][i], handler);
                }
            }
        };

        $scope.loginMode = function(mode) {
            $scope.securityMode = mode;
            return $scope.securityMode;
        };

        $scope.resetItemsPagination = function () {
            $scope.pagedItems = [];
            $scope.currentPage = 0;
        };

        $scope.groupToPages = function (filteredItems, itemsPerPage) {
            var i;
            $scope.pagedItems = [];

            for (i = 0; i < filteredItems.length; i+=1) {
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
            for (i = start; i < end; i+=1) {
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
                $scope.currentPage-=1;
            }
        };

        $scope.nextPage = function () {
            if ($scope.currentPage < $scope.pagedItems.length - 1) {
                $scope.currentPage+=1;
            }
        };

        $scope.setPage = function () {
            $scope.currentPage = this.number;
        };

        $scope.hidePages = function (number) {
            return ($scope.currentPage + 4 < number && number > 8) || ($scope.currentPage - 4 > number && number + 9 < $scope.pagedItems.length);
        };
    });
}());
