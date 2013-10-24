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
        $scope.config = {};

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
                    $scope.doAJAXHttpRequest('GET', 'lang/locate', function (data) {
                        $scope.i18n = data;
                        $scope.loadI18n($scope.i18n);
                    });

                    $scope.userLang = $scope.getLanguage(locale);
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

        $scope.verifyDbConnection = function() {
            var alerts = $('.alerts-container'), infos, warnings, errors;
            alerts.empty();
            blockUI();

            $http({
                method: 'POST',
                url: '../server/bootstrap/verify',
                timeout: 8000,
                data: $('form.bootstrap-config-form').serialize(),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
            .success(function(data) {
                unblockUI();
                if (data.success !== undefined && data.success === true) {
                    motechAlert('server.bootstrap.verify.success', 'server.bootstrap.verify.info');
                    infos = $('<div class="alert alert-success">' + $scope.msg('server.bootstrap.verify.success') + '</div>');
                    alerts.append(infos);
                } else {
                    if(data.errors !== undefined) {
                        motechAlert('server.bootstrap.verify.error', 'server.bootstrap.verify.info');
                        warnings = $('<div class="alert"></div>');
                        errors = $('<div class="alert alert-error"></div>');
                        warnings.append($scope.msg('server.bootstrap.verify.error'));
                        data.errors.forEach(function(item) {
                            errors.append($scope.msg(item) + '<br>');
                        });
                        alerts.append(warnings);
                        alerts.append(errors);
                    } else {
                        motechAlert('server.bootstrap.verify.warning', 'server.bootstrap.verify.info');
                        warnings = $('<div class="alert">' + $scope.msg('server.bootstrap.verify.warning') + '</div>');
                        alerts.append(warnings);
                    }
                }
            })
            .error(function(data) {
                unblockUI();
                motechAlert('server.bootstrap.verify.server.error', 'server.bootstrap.verify.info');
                alerts.append('<div class="alert">' + $scope.msg('server.bootstrap.verify.error') + '</div>');
            });
        };
    });

    serverModule.controller('HomeCtrl', function ($scope, $cookieStore, $q, Menu) {
        $scope.securityMode = false;

        $scope.moduleMenu = {};

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

                scope.user = data;
                scope.user.anonymous = false;

                if (!$scope.$$phase) {
                    $scope.$apply(scope.user);
                }
            })
        ]).then(function () {
            $scope.userLang = $scope.getLanguage(toLocale($scope.user.lang));
            moment.lang($scope.user.lang);
        });

        $scope.$on('module.list.refresh', function () {
            Menu.get(function(data) {
                $scope.moduleMenu = data;
            }, angularHandler('error', 'server.error.cantLoadMenu'));
        });

    });

}());
