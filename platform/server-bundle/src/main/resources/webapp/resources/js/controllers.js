'use strict';

function MasterCtrl($scope, $http, i18nService, $cookieStore) {
    $scope.ready = false;

    $scope.i18n = {};
    $scope.languages = [];
    $scope.userLang = null;
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
    }

    if ($cookieStore.get("showDashboardLogo") != undefined) {
       $scope.showDashboardLogo.showDashboard=$cookieStore.get("showDashboardLogo");
    }

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
    }

    $scope.msg = function(key) {
        return i18nService.getMessage(key);
    };

    $scope.setModuleUrl = function(url) {
        $scope.moduleUrl = url;
    }

    $scope.printDate = function(milis) {
        var date = "";
        if (milis) {
            var date = new Date(milis);
        }
        return date;
    }

    $scope.getLanguage = function(locale) {
       return {
           key: locale.toString() || "en",
           value: $scope.languages[locale.toString()] || $scope.languages[locale.withoutVariant()] || $scope.languages[locale.language] || "English"
       }
    }

    $scope.active = function(address) {
        if (window.location.href.indexOf(address) != -1) {
            return "active";
        }
    }

    $scope.minimizeHeader = function() {
        $scope.showDashboardLogo.showDashboard=!$scope.showDashboardLogo.showDashboard;
        $cookieStore.put("showDashboardLogo", $scope.showDashboardLogo.showDashboard);
    }

    $scope.loadI18n = function(lang) {
        if (!$scope.i18n || $scope.i18n.length <= 0) {
            $scope.ready = true;
        }

        var key, i;

        for (key in $scope.i18n) {
            for (i = 0; i < $scope.i18n[key].length; i++) {
                var handler = undefined;
                // last one
                if (i == $scope.i18n[key].length-1) {
                    handler = function() {
                        $scope.ready = true;
                        $scope.$digest();
                    }
                }
                i18nService.init(lang, key, $scope.i18n[key][i], handler);
            }
        }
    }
}
